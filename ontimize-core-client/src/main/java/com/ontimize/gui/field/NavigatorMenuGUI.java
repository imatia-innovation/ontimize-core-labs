package com.ontimize.gui.field;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.DefaultActionMenuListener;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.swing.selectablelist.SelectableItem;
import com.ontimize.util.swing.selectablelist.SelectableItemListCellRenderer;
import com.ontimize.util.swing.selectablelist.SelectableItemMouseListener;

public class NavigatorMenuGUI extends NavigationMenu
implements IdentifiedElement, AccessForm, FormComponent, HasPreferenceComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(NavigatorMenuGUI.class);

	public static final String SRC = "src";

	protected Object attribute = null;

	protected FormPermission visiblePermission = null;

	protected FormPermission enabledPermision = null;

	protected Form parentForm = null;

	protected boolean restricted = false;

	public static final String NAVIGATOR_MENU = "navigator_menu";

	protected JPopupMenu popupmenu;

	protected ResourceBundle bundle;

	public static final String RESTORE_DEFAULTS = "navigatormenu.restore_defaults";

	public static final String VISIBLE_ITEMS = "navigatormenu.visible_items";

	protected SetupDialog dialog = null;

	protected ApplicationPreferences aPreferences = null;

	protected String userPrefs = null;

	public static BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 5f,
			new float[] { 0.5f, 5.0f }, 2.5f);

	/**
	 * Method that configures the component.
	 * @param h Map with the parameters.
	 * @throws Exception
	 */
	public NavigatorMenuGUI(final Map h) throws Exception {
		this.init(h);
		this.installMouseHandler();
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * This method gets the <code>Hashtable</code> and initializes the component
	 * <p>
	 * @param arguments the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Indicates the component attribute.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>src</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Resource path to locate the configuration XML.</td>
	 *        </tr>
	 *        </table>
	 */
	@Override
	public void init(final Map arguments) throws Exception {
		if (arguments.containsKey(DataField.ATTR)) {
			this.attribute = arguments.get("attr");
		} else {
			throw new IllegalArgumentException(DataField.ATTR + " parameter is mandatory");
		}

		if (arguments.containsKey(NavigatorMenuGUI.SRC)) {
			final URL urlXML = this.getClass().getClassLoader().getResource(arguments.get(NavigatorMenuGUI.SRC).toString());
			if (urlXML != null) {
				this.parse(urlXML);
			} else {
				throw new IllegalArgumentException(NavigatorMenuGUI.SRC + " parameter is mandatory");
			}
		}
	}

	protected void installMouseHandler() {
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					NavigatorMenuGUI.this.showPopupMenu(e);
				}
			}
		});
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			final Component[] cs = new Component[1];
			cs[0] = this;
			ClientSecurityManager.registerSecuredElement(this, cs);

		}
		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			this.setVisible(false);
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			this.setEnabled(false);
		}

	}

	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					NavigatorMenuGUI.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					NavigatorMenuGUI.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermision == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.enabledPermision = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.enabledPermision != null) {
					manager.checkPermission(this.enabledPermision);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					NavigatorMenuGUI.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					NavigatorMenuGUI.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	/**
	 * This method analyzes a "MenuGroup" node of the XML Document configuration to obtain all the
	 * parameter to build a new MenuGroupGUI into the NavigationMenu.
	 * @param node The "MenuGroup" node of the XML Document configuration.
	 * @return a <code>MenuGroup</code> object.
	 * @throws Exception
	 */
	@Override
	protected MenuGroup createMenuGroup(final Node node) throws Exception {
		final Map parameters = new Hashtable();
		String[] opts = null;
		ImageIcon[] icons = null;
		Object width = null;

		final NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			final Node currentNode = attributes.item(i);

			if (currentNode == null) {
				throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE);
			} else {
				if (NavigationMenu.OPTIONS.equalsIgnoreCase(currentNode.getNodeName())) {
					final String options = currentNode.getNodeValue();
					final ArrayList li = new ArrayList();
					final StringTokenizer tokens = new StringTokenizer(options, ";");
					while (tokens.hasMoreTokens()) {
						li.add(tokens.nextToken());
					}
					opts = (String[]) li.toArray(new String[li.size()]);
					parameters.put(NavigationMenu.OPTIONS, opts);
				} else if (NavigationMenu.ICONS.equalsIgnoreCase(currentNode.getNodeName())) {
					final String sIcons = currentNode.getNodeValue();
					final ArrayList li = new ArrayList();
					final StringTokenizer tokens = new StringTokenizer(sIcons, ";");
					while (tokens.hasMoreTokens()) {
						if (this.imageBasePath != null) {
							final String str = this.imageBasePath + tokens.nextToken();
							li.add(str);
						} else {
							li.add(tokens.nextToken());
						}
					}
					icons = new ImageIcon[li.size()];
					for (int m = 0; m < li.size(); m++) {
						try {
							icons[m] = ImageManager.getIcon((String) li.get(m));
						} catch (final Exception e) {
							NavigatorMenuGUI.logger.error("Resource not found " + li.get(m), e);
						}
					}
					parameters.put(NavigationMenu.ICONS, icons);
				} else if (NavigationMenu.BGHEADER.equalsIgnoreCase(currentNode.getNodeName())) {
					Color bgHeader = Color.green;
					try {
						bgHeader = NavigationMenu.parseColor(currentNode.getNodeValue());
						parameters.put(NavigationMenu.BGHEADER, bgHeader);
					} catch (final Exception e) {
						NavigatorMenuGUI.logger.error(null, e);
					}
				} else if (NavigationMenu.BGBODY.equalsIgnoreCase(currentNode.getNodeName())) {
					Color bgBody = Color.white;
					try {
						bgBody = NavigationMenu.parseColor(currentNode.getNodeValue());
						parameters.put(NavigationMenu.BGBODY, bgBody);
					} catch (final Exception e) {
						NavigatorMenuGUI.logger.error(null, e);
					}
				} else if (NavigationMenu.FOREGROUND.equalsIgnoreCase(currentNode.getNodeName())) {
					Color fg = Color.black;
					try {
						fg = NavigationMenu.parseColor(currentNode.getNodeValue());
						parameters.put(NavigationMenu.FOREGROUND, fg);
					} catch (final Exception e) {
						NavigatorMenuGUI.logger.error(null, e);
					}
				} else if (NavigationMenu.FOREGROUNDHEADER.equalsIgnoreCase(currentNode.getNodeName())) {
					Color fgHeader = Color.white;
					try {
						fgHeader = NavigationMenu.parseColor(currentNode.getNodeValue());
						parameters.put(NavigationMenu.FOREGROUNDHEADER, fgHeader);
					} catch (final Exception e) {
						NavigatorMenuGUI.logger.error(null, e);
					}
				} else if (NavigationMenu.BORDERCOLOR.equalsIgnoreCase(currentNode.getNodeName())) {
					Color border = Color.white;
					try {
						border = NavigationMenu.parseColor(currentNode.getNodeValue());
						parameters.put(NavigationMenu.BORDERCOLOR, border);
					} catch (final Exception e) {
						NavigatorMenuGUI.logger.error(null, e);
					}
				} else if (NavigationMenu.WIDTH.equalsIgnoreCase(currentNode.getNodeName())) {
					width = currentNode.getNodeValue();
					parameters.put(NavigationMenu.WIDTH, currentNode.getNodeValue());
				} else {
					parameters.put(currentNode.getNodeName(), currentNode.getNodeValue());
				}

			}

		}
		if (opts == null) {
			throw new IllegalArgumentException(NavigationMenu.OPTIONS + " parameter is mandatory!!!!");
		} else if (width == null) {
			throw new IllegalArgumentException(NavigationMenu.WIDTH + " parameter is mandatory!!!!");
		} else if ((opts != null) && (icons != null) && (opts.length != icons.length)) {
			throw new IllegalArgumentException(
					NavigationMenu.OPTIONS + " and " + NavigationMenu.ICONS + " parameters must have the same length");
		} else {
			return this.createMenuGroupInstance(parameters);
		}

	}

	/**
	 * This method instantiates a new MenuGroupGUI object with the specified parameters.
	 * @return a <code>MenuGroupGUI</code> object.
	 */
	protected MenuGroupGUI createMenuGroupInstance(final Map parameters) {

		Class cMenuGroupGUI = null;
		Object ob = null;
		try {
			if (parameters.containsKey(NavigationMenu.MENUGROUPCLASS)) {
				cMenuGroupGUI = Class.forName(parameters.get(NavigationMenu.MENUGROUPCLASS).toString());
			} else {
				cMenuGroupGUI = Class
						.forName("com.ontimize.gui.field.NavigatorMenuGUI" + "$" + NavigationMenu.MENUGROUP + "GUI");
			}

			final Constructor constructors = cMenuGroupGUI.getConstructor(new Class[] { Hashtable.class });
			ob = constructors.newInstance(new Object[] { parameters });
		} catch (final Exception e) {
			NavigatorMenuGUI.logger.error(null, e);
		}
		return (MenuGroupGUI) ob;
	}

	/**
	 * * Constructs a new MenuGroupGUI with the specified parameters.
	 * @param header String with the text to display into the Header of the MenuGroup.
	 * @param opts String Array with the identifier of each MenuItem of the MenuGroup.
	 * @param icons ImageIcon Array with the icons to each MenuItem of the MenuGroup.
	 * @param x The coordinate x of the MenuGroup into the NavigationMenu.
	 * @param y The coordinate y of the MenuGroup into the NavigationMenu.
	 * @param width The width of the MenuGroup.
	 * @param height The absolute height of the MenuGroup. The header height is included into this
	 *        height.
	 * @param bgHeader The color of the MenuGroup Header.
	 * @param bgBody The color of the background MenuGroup.
	 * @param fg The color of the MenuGroup font.
	 * @param border The color of the border of the MenuGroup. Use
	 *        {@link #createMenuGroupInstance(Map parameters)}
	 * @deprecated
	 */
	@Deprecated
	@Override
	protected MenuGroup createMenuGroupInstance(final String header, final String[] opts, final ImageIcon[] icons, final int x, final int y,
			final int width, final int height, final Color bgHeader, final Color bgBody, final Color fg,
			final Color border) throws Exception {
		return new MenuGroupGUI(header, opts, icons, x, y, width, height, bgHeader, bgBody, fg, border);
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		if ((this.menuList != null) && (this.menuList.size() > 0)) {
			int xMax = 0;
			int yMax = 0;
			for (int i = 0; i < this.menuList.size(); i++) {
				final MenuGroupGUI current = (MenuGroupGUI) this.menuList.get(i);
				if (xMax < (current.getX() + current.getWidth())) {
					xMax = current.getX() + current.getWidth();
				}
				if (yMax < (current.getY() + current.getHeight())) {
					yMax = current.getY() + current.getHeight();
				}
			}
			if (d.width < xMax) {
				d.width = xMax;
			}

			if (d.height < yMax) {
				d.height = yMax;
			}
		}
		return d;
	}

	@Override
	public void initPreferences(final ApplicationPreferences ap, final String user) {
		if (ap == null) {
			return;
		}
		this.userPrefs = user;
		this.aPreferences = ap;
		final String preferenceValue = this.aPreferences.getPreference(user, this.getNavigationMenuPreferenceKey());
		this.setNavigationMenuPreferenceValue(preferenceValue);
	}

	/**
	 * This method returns a unequivocal key name to assign to this MenuNavigator that will be used into
	 * the Preferences.
	 * @return a <code>String</code> with the name.
	 */
	protected String getNavigationMenuPreferenceKey() {
		final Form f = this.parentForm;
		return f != null ? f.getArchiveName() + "_" + NavigatorMenuGUI.NAVIGATOR_MENU : NavigatorMenuGUI.NAVIGATOR_MENU;
	}

	/**
	 * This method save the current preference values.
	 */
	protected void saveNavigationMenuPreference(final boolean bSaveRemote) {
		if (this.aPreferences == null) {
			return;
		}
		this.aPreferences.setPreference(this.userPrefs, this.getNavigationMenuPreferenceKey(),
				this.getNavigationMenuPreferenceValue());
		if (bSaveRemote) {
			this.aPreferences.savePreferences();
		}
	}

	protected void saveNavigationMenuPreference() {
		this.saveNavigationMenuPreference(false);
	}

	/**
	 * This method obtains the current values of MenuGroups and builds a String with the preference
	 * values of these MenuGroups.
	 *
	 * <blockquote> The preference values saved are:
	 * <p>
	 * - position: The coordinates x and y of each MenuGroup.
	 * </p>
	 * <p>
	 * - items: The current state of visibility of each MenuItem of each MenuGroup.
	 * </p>
	 * * </blockquote>
	 * @return a <code>String</code> with the preference values.
	 */
	protected String getNavigationMenuPreferenceValue() {

		if ((this.menuList == null) || (this.menuList.size() == 0)) {
			return null;
		}
		final StringBuilder buffer = new StringBuilder();

		// Position
		final StringBuilder position = new StringBuilder();
		for (int i = 0; i < this.menuList.size(); i++) {
			final MenuGroupGUI group = (MenuGroupGUI) this.menuList.get(i);
			position.append(group.getHeader()).append("=");
			position.append(group.getX()).append(":");
			position.append(group.getY()).append(";");
		}

		buffer.append("~position~");
		buffer.append(position.toString());
		buffer.append("|");

		// Menu Items.
		final String str = this.getPreferencesStructure(this.menuList);
		buffer.append(str);
		return buffer.toString();
	}

	/**
	 * This method establishes the position (x,y) of the specified MenuGroup
	 * @param name The name of the MenuGroup.
	 * @param x The coordinate x of the MenuGroup.
	 * @param y The coordinate y of the MenuGroup.
	 */
	protected void setMenuGroupValue(final String name, final int x, final int y) {
		if ((this.menuList == null) || (this.menuList.size() == 0)) {
			return;
		}
		for (int i = 0; i < this.menuList.size(); i++) {
			final MenuGroupGUI menu = (MenuGroupGUI) this.menuList.get(i);
			if (name.equals(menu.getHeader())) {
				menu.setXY(x, y);
				return;
			}
		}
	}

	/**
	 * This method establishes the visibility of the MenuItems of the specified MenuGroup.
	 * @param name The name of the MenuGroup.
	 * @param manager The identifier of the MenuItem of the MenuGroup
	 * @param visibility Boolean with the visibility value.
	 */
	protected void setMenuGroupValue(final String name, final String manager, final boolean visibility) {
		if ((this.menuList == null) || (this.menuList.size() == 0)) {
			return;
		}
		for (int i = 0; i < this.menuList.size(); i++) {
			final MenuGroupGUI menu = (MenuGroupGUI) this.menuList.get(i);
			if (name.equals(menu.getHeader())) {
				menu.setVisibleItems(manager, visibility);
				return;
			}
		}
	}

	/**
	 * This method recovers the preference values from the specified String and sets them to the
	 * MenuGroup.
	 * @param value Preference values String.
	 */
	protected void setNavigationMenuPreferenceValue(final String value) {
		if ((value == null) || (value.length() == 0)) {
			return;
		}
		final StringTokenizer token = new StringTokenizer(value, "|");
		while (token.hasMoreTokens()) {
			String current = token.nextToken();
			final String param = current.substring(1, 6);
			if (param.equals("items")) {
				if (current.indexOf("~items~") >= -1) {
					current = current.replaceFirst("~items~", "");
					final StringTokenizer tokenI = new StringTokenizer(current, ";");
					while (tokenI.hasMoreElements()) {
						final String element = tokenI.nextToken();
						final int indexequal = element.indexOf("=");
						if (indexequal >= 0) {
							final String header = element.substring(0, indexequal);
							final String values = element.substring(indexequal + 1);
							final StringTokenizer tokenItem = new StringTokenizer(values, ":");
							while (tokenItem.hasMoreElements()) {
								final String elem = tokenItem.nextToken();
								final int indexparenthesis = elem.indexOf("(");
								final String manager = elem.substring(0, indexparenthesis);
								boolean visibility = true;
								final String visibleValue = elem.substring(indexparenthesis + 1, elem.length() - 1);
								if ("false".equals(visibleValue)) {
									visibility = false;
								}
								this.setMenuGroupValue(header, manager, visibility);
							}
							this.resizeMenuGroup(header);
						}
					}
				}
			}
			if (param.equals("posit")) {
				if (current.indexOf("~position~") >= -1) {
					current = current.replaceFirst("~position~", "");
					final StringTokenizer tokenI = new StringTokenizer(current, ";");
					while (tokenI.hasMoreElements()) {
						final String element = tokenI.nextToken();
						final int indexequal = element.indexOf("=");
						if (indexequal >= 0) {
							final String header = element.substring(0, indexequal);
							final String values = element.substring(indexequal + 1);
							final int x = Integer.parseInt(values.substring(0, values.indexOf(":")));
							final int y = Integer.parseInt(values.substring(values.indexOf(":") + 1));
							this.setMenuGroupValue(header, x, y);
						}
					}
				}
			}

		}
	}

	/**
	 * This method obtains the current visibility values of the MenuItems of the MenuGroups and builds a
	 * String to be used into the preference values.
	 * @param menulist ArrayList with the whole MenuGroups of the NavigationMenu.
	 * @return a <code>String</code>
	 */
	public String getPreferencesStructure(final ArrayList menulist) {

		final StringBuilder buffer = new StringBuilder("~items~");
		final String equals = "=";
		final String semicolon = ";";

		final int numGroupElem = menulist.size();
		final StringBuilder groupElement = new StringBuilder();
		for (int i = 0; i < numGroupElem; i++) {
			final MenuGroupGUI menugroupGUI = (MenuGroupGUI) menulist.get(i);
			final String name = menugroupGUI.getHeader();
			final String items = this.getPreferenceItems(menugroupGUI.getMenuItem());
			groupElement.append(name).append(equals).append(items).append(semicolon);
		}
		buffer.append(groupElement.toString());
		return buffer.toString();
	}

	/**
	 * This is an auxiliary method to check the visibility value of the MenuItem and builds a String
	 * with them to be used into the preference values.
	 * @param items An Array of MenuItem objects of a MenuGroup.
	 * @return a <code>String</code>.
	 */
	public String getPreferenceItems(final MenuItem[] items) {
		final StringBuilder buffer = new StringBuilder();

		boolean moreElem = false;
		for (int i = 0; i < items.length; i++) {
			if (moreElem) {
				buffer.append(":");
			} else {
				moreElem = true;
			}
			final String manager = items[i].getManager();
			String value = null;
			if (items[i].isVisible()) {
				value = "true";
			} else {
				value = "false";
			}
			buffer.append(manager).append("(").append(value).append(")");
		}
		return buffer.toString();
	}

	/**
	 * This method resizes the specified MenuGroup.
	 * @param header String with the header name of the MenuGroup.
	 */
	protected void resizeMenuGroup(final String header) {
		for (int i = 0; i < this.menuList.size(); i++) {
			final MenuGroupGUI menu = (MenuGroupGUI) this.menuList.get(i);
			if (header.equals(menu.getHeader())) {
				menu.reBound();
			}
		}
	}

	/**
	 * This method restore the default values of the MenuGroups of the NavigationMenu.
	 */
	protected void setDefaultValues() {
		if ((this.menuList == null) || (this.menuList.size() == 0)) {
			return;
		}
		for (int i = 0; i < this.menuList.size(); i++) {
			final MenuGroupGUI menu = (MenuGroupGUI) this.menuList.get(i);
			menu.setDefaultValue();
		}
		this.saveNavigationMenuPreference();
	}

	@Override
	public void setResourceBundle(final ResourceBundle bundle) {
		super.setResourceBundle(bundle);
		this.bundle = bundle;
	}

	/**
	 * This method shows a PopupMenu when a right mouse button click is done.
	 * @param e Mouse event.
	 */
	protected void showPopupMenu(final MouseEvent e) {
		if (this.popupmenu == null) {
			this.popupmenu = new JPopupMenu();
			final JMenuItem defaultValueMenu = new JMenuItem(
					ApplicationManager.getTranslation(NavigatorMenuGUI.RESTORE_DEFAULTS, this.bundle));
			defaultValueMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					NavigatorMenuGUI.this.setDefaultValues();
				}
			});

			final JMenuItem visibleItemsMenu = new JMenuItem(
					ApplicationManager.getTranslation(NavigatorMenuGUI.VISIBLE_ITEMS, this.bundle));
			visibleItemsMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					NavigatorMenuGUI.this.configureMenuGroupItems();
				}
			});

			this.popupmenu.add(defaultValueMenu);
			this.popupmenu.add(visibleItemsMenu);
		}

		if (this.popupmenu != null) {
			this.popupmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * This method creates the visibility configuration JDialog to set which MenuItems are going to be
	 * displayed in the MenuGroups. If it exists, the method only opens it. In addition, the method
	 * recovers information of the current visibility configuration to repaint all the MenuGroups.
	 */
	private void configureMenuGroupItems() {

		if (this.dialog == null) {
			ApplicationManager.getApplication().getFrame();

			final Window w = SwingUtilities.getWindowAncestor(this.parentForm);
			if (w instanceof Frame) {
				this.dialog = new SetupDialog((Frame) w, true);
			} else if (w instanceof Dialog) {
				this.dialog = new SetupDialog((Dialog) w, true);
			}
			this.dialog.getContentPane().setLayout(new GridLayout(1, 0));
			this.dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.dialog.setInputData(this.menuList);
			this.dialog.setResourceBundle(this.bundle);
			this.dialog.jInit();
			final Form f = this.parentForm;
			final String s = f != null ? f.getArchiveName() + "_" + NavigatorMenuGUI.NAVIGATOR_MENU + "_JDIALOG"
					: NavigatorMenuGUI.NAVIGATOR_MENU + "JDIALOG";
			this.dialog.setSizePositionPreference(s);
			this.dialog.pack();
			ApplicationManager.center(this.dialog);
		}

		this.dialog.setResourceBundle(this.bundle);
		this.dialog.setVisible(true);

		if (this.dialog.operation == SetupDialog.ACCEPT) {
			final Map data = this.dialog.getOutputData();
			this.updateVisibleItemsValues(data);
			for (int i = 0; i < this.menuList.size(); i++) {
				final MenuGroupGUI menu = (MenuGroupGUI) this.menuList.get(i);
				menu.reBound();
			}
			this.saveNavigationMenuPreference(true);
			this.repaint();
		}
	}

	/**
	 * This method updates the visibility values of each MenuItem of each MenuGroup with the information
	 * contained into the Map received. The information is returned by the visibility
	 * configuration JDialog.
	 * @param data Map with the MenuItem visibility information. Pair values like:
	 *
	 *        <Table BORDER=1 CELLPADDING=2 CELLSPACING=1 RULES=ROWS * * FRAME=BOX>
	 *        <tr>
	 *        <td><b>key</td>
	 *        <td><b>values</td>
	 *        </tr>
	 *        <tr>
	 *        <td>"MenuGroupName"</td>
	 *        <td>MenuItemInformation Object</td>
	 *        </tr>
	 *        </table>
	 *
	 */
	public void updateVisibleItemsValues(final Map data) {

		final Iterator it = data.keySet().iterator();
		while (it.hasNext()) {
			final String nameGroup = (String) it.next();
			for (int j = 0; j < this.menuList.size(); j++) {
				final String currentHeader = ((MenuGroupGUI) this.menuList.get(j)).getHeader();
				if (nameGroup.equals(currentHeader)) {
					final MenuGroup currentMenu = (MenuGroup) this.menuList.get(j);
					final MenuItem[] currentMenuItems = currentMenu.getMenuItem();

					final List items = (List) data.get(nameGroup);
					final int numItems = items.size();
					for (int k = 0; k < numItems; k++) {
						final MenuItemInformation menuItemInfo = (MenuItemInformation) items.get(k);
						final String currentItemName = menuItemInfo.getItemName();
						for (int m = 0; m < currentMenuItems.length; m++) {
							final String mIname = currentMenuItems[m].getManager();
							if (currentItemName.equals(mIname)) {
								boolean visibility = true;
								if (!menuItemInfo.isSelected()) {
									visibility = false;
								}
								currentMenuItems[m].setVisible(visibility);
								break;
							}
						}
					}
					break;
				}
			}

		}

	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Public static classes
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This class integrate Ontimize characteristics into the MenuGroup.
	 *
	 * @author Imatia Innovation
	 */
	public static class MenuGroupGUI extends MenuGroup {

		/**
		 * Default value to the x coordinate of the MenuGroupGUI
		 */
		protected int defaultX;

		/**
		 * Default value to the y coordinate of the MenuGroupGUI
		 */
		protected int defaultY;

		/**
		 * Value of displacements into x-axis.
		 */
		int offsetX = 0;

		/**
		 * Value of displacements into x-axis.
		 */
		int offsetY = 0;

		/**
		 * Boolean that indicate if the MenuGroup are going to be dragged.
		 */
		boolean drag = false;

		boolean executing = false;

		/**
		 * Constructs a new MenuGroupGUI with the parameters specified into the hashtable.
		 * @param parameters Map with the whole parameters.
		 */
		public MenuGroupGUI(final Map parameters) {
			super(parameters);

			if ((this.borderString != null) && this.hasBorder) {
				this.border = this.getBorder(this.borderString);
				this.setBorder(this.border);
			}

			int x = 0;
			int y = 0;
			if (parameters.containsKey(NavigationMenu.X)) {
				x = Integer.parseInt((String) parameters.get(NavigationMenu.X));
			}
			if (parameters.containsKey(NavigationMenu.Y)) {
				y = Integer.parseInt((String) parameters.get(NavigationMenu.Y));
			}

			// String[] options = null;
			// if(parameters.containsKey(NavigationMenu.OPTIONS)){
			// options = (String[])parameters.get(NavigationMenu.OPTIONS);
			// }

			this.defaultX = x;
			this.defaultY = y;

			ToolTipManager.sharedInstance().registerComponent(this);
		}

		/**
		 * Constructs a new MenuGroupGUI with the specified parameters.
		 * @param header String with the text to display into the Header of the MenuGroup.
		 * @param options String Array with the identifier of each MenuItem of the MenuGroup.
		 * @param icons ImageIcon Array with the icons to each MenuItem of the MenuGroup.
		 * @param x The coordinate x of the MenuGroup into the NavigationMenu.
		 * @param y The coordinate y of the MenuGroup into the NavigationMenu.
		 * @param w The width of the MenuGroup.
		 * @param h The absolute height of the MenuGroup. The header height is included into this height.
		 * @param bgH The color of the MenuGroup Header.
		 * @param bgB The color of the background MenuGroup.
		 * @param fg The color of the MenuGroup font.
		 * @param borderColor The color of the border of the MenuGroup.
		 *
		 *        Use {@link #MenuGroupGUI(Map parameters)}
		 * @deprecated
		 */
		@Deprecated
		public MenuGroupGUI(final String header, final String[] options, final ImageIcon[] icons, final int x, final int y, final int w, final int h, final Color bgH,
				final Color bgB, final Color fg, final Color borderColor) {
			super(header, options, icons, x, y, w, h, bgH, bgB, fg, borderColor);
			this.defaultX = x;
			this.defaultY = y;

			ToolTipManager.sharedInstance().registerComponent(this);
		}

		/**
		 * @param parameters the <code>Hashtable</code> with the whole parameters to configure the MenuItem.
		 *        <p>
		 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
		 *        <tr>
		 *        <td><b>attribute</td>
		 *        <td><b>values</td>
		 *        <td><b>default</td>
		 *        <td><b>required</td>
		 *        <td><b>meaning</td>
		 *        </tr>
		 *        <tr>
		 *        <td>menuitemclass</td>
		 *        <td></td>
		 *        <td><i>"com.ontimize.gui.field.NavigatorMenuGUI$MenuItemGUI"</td>
		 *        <td>no</td>
		 *        <td>The class of the MenuItem</td>
		 *        </tr>
		 *        </table>
		 */
		@Override
		protected void createMenuItems(final Map parameters) {
			if (!parameters.containsKey(NavigationMenu.MENUITEMCLASS)) {
				parameters.put(NavigationMenu.MENUITEMCLASS, "com.ontimize.gui.field.NavigatorMenuGUI$MenuItemGUI");
			}
			String[] options = null;
			if (parameters.containsKey(NavigationMenu.OPTIONS)) {
				options = (String[]) parameters.get(NavigationMenu.OPTIONS);
			}
			super.createMenuItems(parameters);
			for (int i = 0; i < options.length; i++) {
				((MenuItemGUI) this.menuItem[i]).setVisiblePermission(this.checkVisiblePermission(this.menuItem[i]));
				((MenuItemGUI) this.menuItem[i]).setEnabledPermission(this.checkEnabledPermission(this.menuItem[i]));
			}
		}

		/**
		 * This method returns a Border object from a String with the border style.
		 * @param border String with the border style.
		 * @return a <code>Border</code> object.
		 */
		protected Border getBorder(final String border) {
			try {
				final Border bmBorder = BorderManager.getBorder(border);
				return bmBorder;

			} catch (final Exception e) {
				NavigatorMenuGUI.logger.debug(this.getClass().getName() + ". Error getBorder: " + e.getMessage(), e);
				return null;
			}
		}

		public void setDefaultValue() {
			this.setXY(this.defaultX, this.defaultY);
		}

		/**
		 * This method establishes the visibility of the given MenuItem identifier.
		 * @param manager The identifier of the MenuItem.
		 * @param visibility Boolean with the visibility value of the MenuItem.
		 */
		public void setVisibleItems(final String manager, final boolean visibility) {
			for (int j = 0; j < this.menuItem.length; j++) {
				final String currentManager = this.menuItem[j].getManager();
				if (manager.equals(currentManager)) {
					this.menuItem[j].setVisible(visibility);
					return;
				}
			}
		}

		@Override
		public String getToolTipText(final MouseEvent event) {
			final int index = this.getOptionIndex(event.getX(), event.getY());
			if (index == -2) {
				if (this.dragEnabled) {
					return ApplicationManager.getTranslation("navigatormenu.drag_message");
				}
			}
			return null;
		}

		@Override
		protected void installMouseHandler() {
			this.addMouseMotionListener(new MouseMotionAdapter() {

				@Override
				public void mouseDragged(final MouseEvent e) {
					if (MenuGroupGUI.this.executing) {
						return;
					}
					if (MenuGroupGUI.this.drag) {
						final int tempX = e.getX() - MenuGroupGUI.this.offsetX;
						final int tempY = e.getY() - MenuGroupGUI.this.offsetY;
						MenuGroupGUI.this.move(tempX, tempY);
						final MenuGroupGUI groupMove = (MenuGroupGUI) e.getComponent();

						final java.awt.Rectangle rect = new java.awt.Rectangle(groupMove.getBounds().width,
								groupMove.getHeaderHeight());
						// scrollRectToVisible(groupMove.getBounds());
						MenuGroupGUI.this.scrollRectToVisible(rect);
					}
				}

				@Override
				public void mouseMoved(final MouseEvent e) {
					if (MenuGroupGUI.this.executing) {
						return;
					}
					if (MenuGroupGUI.this.drag) {
						return;
					}
					final int index = MenuGroupGUI.this.getOptionIndex(e.getX(), e.getY());
					if (index == -1) {
						MenuGroupGUI.this.selectedOption = null;
						e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						return;
					}
					if (index == -2) {
						MenuGroupGUI.this.selectedOption = null;
						return;
					}
					if (MenuGroupGUI.this.menuItem[index].isVisible()
							&& MenuGroupGUI.this.menuItem[index].isEnabled()) {
						MenuGroupGUI.this.selectedOption = MenuGroupGUI.this.menuItem[index].getManager();
						if (MenuGroupGUI.this.selectedOption != null) {
							e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						} else {
							e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					} else {
						MenuGroupGUI.this.selectedOption = null;
						e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}

					MenuGroupGUI.this.repaint();
				}
			});

			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(final MouseEvent e) {
					if (MenuGroupGUI.this.executing) {
						return;
					}
					if (SwingUtilities.isLeftMouseButton(e)) {
						final int index = MenuGroupGUI.this.getOptionIndex(e.getX(), e.getY());
						if (((index == -2) && MenuGroupGUI.this.dragEnabled)
								|| (MenuGroupGUI.this.dragEnabled && MenuGroupGUI.this.dragAllMenuEnabled)) {
							e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
							MenuGroupGUI.this.offsetX = e.getX();
							MenuGroupGUI.this.offsetY = e.getY();
							MenuGroupGUI.this.drag = true;
						}
					}
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
					if (MenuGroupGUI.this.executing) {
						return;
					}
					if (SwingUtilities.isLeftMouseButton(e)) {
						if (MenuGroupGUI.this.drag) {
							((NavigatorMenuGUI) e.getComponent().getParent()).saveNavigationMenuPreference();
						}
						MenuGroupGUI.this.drag = false;
						e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}

				@Override
				public void mouseClicked(final MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						if (MenuGroupGUI.this.drag) {
							return;
						}
						final int index = MenuGroupGUI.this.getOptionIndex(e.getX(), e.getY());
						if ((index == -1) || (index == -2)) {
							MenuGroupGUI.this.selectedOption = null;
							return;
						}
						if (MenuGroupGUI.this.menuItem[index].isVisible()
								&& MenuGroupGUI.this.menuItem[index].isEnabled()) {
							final Cursor oldCursor = e.getComponent().getCursor();
							try {
								MenuGroupGUI.this.executing = true;
								e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								MenuGroupGUI.this.menuItem[index].actionPerformed(
										new ActionEvent(MenuGroupGUI.this, 0, MenuGroupGUI.this.selectedOption));
							} finally {
								MenuGroupGUI.this.executing = false;
								e.getComponent().setCursor(oldCursor);
							}
						}
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					MenuGroupGUI.this.selectedOption = null;
					MenuGroupGUI.this.repaint();
				}
			});
		}

		@Override
		protected void paintOptions(final Graphics g) {
			g.setFont(new Font("Verdana", Font.PLAIN, 10));
			final Insets insets = this.getInsets();
			int d = this.getItemHeight();
			int yoffset = this.headerheight + insets.top; // + insets.bottom +
			// optionsOffset;

			boolean first = true;
			for (int i = 0; i < this.menuItem.length; i++) {
				if (this.menuItem[i].isVisible()) {

					boolean selected = false;
					if (this.menuItem[i].getManager().equals(this.selectedOption)) {
						selected = true;
					}

					boolean enabled = true;
					if (!this.menuItem[i].isEnabled()) {
						enabled = false;
					}

					this.menuItemRenderer.setResourceBundle(this.bundle);
					g.translate(insets.left, yoffset);

					final Component c = this.menuItemRenderer.getMenuItemRendererComponent(this, this.menuItem[i], selected,
							enabled);
					if (i == (this.menuItem.length - 1)) {
						d = d + 1;
					}
					c.setSize(this.getWidth() - insets.left - insets.right, d - 1);
					c.paint(g);

					if (this.separator) {
						if (!first) {
							final Graphics2D g2d = (Graphics2D) g;
							final Stroke old = g2d.getStroke();
							final Color oldColor = g2d.getColor();
							try {
								final Color newColor = ColorConstants.parseColor("#999999");
								g2d.setColor(newColor);
							} catch (final Exception e) {
								NavigatorMenuGUI.logger.error(null, e);
							}
							g2d.setStroke(NavigatorMenuGUI.stroke);
							g2d.drawLine(0, -1, this.getWidth() - insets.left - insets.right, -1);
							g2d.setStroke(old);
							g2d.setColor(oldColor);
						}
					}

					g.translate(-insets.left, -yoffset);
					yoffset = yoffset + d;

					if (first) {
						first = false;
					}

				}

			}

		}

		/**
		 * This method checks the visibility of the specified MenuItem according to Ontimize Permissions. If
		 * it returns true it means that the MenuItem could be visible.
		 * @param menuItem The MenuItem to be checked.
		 * @return a <code>boolean</code>.
		 */
		protected boolean checkVisiblePermission(final MenuItem menuItem) {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				final MenuPermission visiblePermission = new MenuPermission("visible", menuItem.getManager(), true);
				try {
					manager.checkPermission(visiblePermission);
					return true;
				} catch (final Exception e) {
					NavigatorMenuGUI.logger.trace(null, e);
					return false;
				}
			}
			return true;
		}

		/**
		 * This method checks the availibility of the specified MenuItem according to Ontimize Permissions.
		 * If it returns true it means that the MenuItem could be enabled.
		 * @param menuItem The MenuItem to be checked.
		 * @return a <code>boolean</code>.
		 */
		protected boolean checkEnabledPermission(final MenuItem menuItem) {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				final MenuPermission enabledPermission = new MenuPermission("enabled", menuItem.getManager(), true);
				try {
					manager.checkPermission(enabledPermission);
					return true;
				} catch (final Exception e) {
					NavigatorMenuGUI.logger.trace(null, e);
					return false;
				}
			}
			return true;
		}

	}

	/**
	 * This class integrate Ontimize characteristics into the MenuItem.
	 *
	 * @author Imatia Innovation
	 *
	 */
	public static class MenuItemGUI extends MenuItem {

		/**
		 * Boolean that specifies if this MenuItemGUI is visible in accordance with the Ontimize
		 * Permissions.
		 */
		protected boolean visiblePermission = true;

		/**
		 * Boolean that specifies if this MenuItemGUI is enabled in accordance with the Ontimize
		 * Permissions.
		 */
		protected boolean enabledPermission = true;

		/**
		 * Constructs a new MenuItemGUI specifying the identifier and the icon of the MenuItmeGUI.
		 * @param manager String with the identifier of the MenuItemGUI. That is, the manager associated to
		 *        this MenuItem.
		 * @param icon ImageIcon with the icon of the MenuItemGUI.
		 */
		public MenuItemGUI(final String manager, final ImageIcon icon) {
			super(manager, icon);
		}

		/**
		 * This method returns if the MenuItemGUI is visible according to the Ontimize Permissions. If it is
		 * true the MenuItemGUI is visible.
		 * @return a <code>boolean</code>.
		 */
		public boolean isVisiblePermission() {
			return this.visiblePermission;
		}

		/**
		 * This method sets if the MenuItemGUI is visible according to the Ontimize Permissions.
		 * @param visiblePermission boolean.
		 */
		public void setVisiblePermission(final boolean visiblePermission) {
			this.visiblePermission = visiblePermission;
		}

		/**
		 * This method returns if the MenuItemGUI is enabled according to the Ontimize Permissions. If it is
		 * true the MenuItemGUI is enabled.
		 * @return a <code>boolean</code>.
		 */
		public boolean isEnabledPermission() {
			return this.enabledPermission;
		}

		/**
		 * This method sets if the MenuItemGUI is enabled according to the Ontimize Permissions.
		 * @param enabledPermission boolean.
		 */
		public void setEnabledPermission(final boolean enabledPermission) {
			this.enabledPermission = enabledPermission;
		}

		@Override
		public boolean isVisible() {
			if (!this.visiblePermission) {
				return false;
			}
			return super.isVisible();
		}

		@Override
		public boolean isEnabled() {
			if (!this.enabledPermission) {
				return false;
			}
			return super.isEnabled();
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ApplicationManager.getApplication().getMenuListener() instanceof DefaultActionMenuListener) {
				((DefaultActionMenuListener) ApplicationManager.getApplication().getMenuListener()).actionPerformed(e);
			}
		}

	}

	/**
	 * This class introduces a group of SelectableItem objects into a SelectableItem.
	 *
	 * @author Imatia Innovation.
	 *
	 */
	public static class GroupSelectableItem extends SelectableItem {

		/**
		 * ArrayList of SelectableItems.
		 */
		protected ArrayList itemSelectableItem = null;

		/**
		 * Constructs a new GroupSelectableItem
		 * @param text String with the text of the SelectableItem.
		 */
		public GroupSelectableItem(final String text) {
			super(text);
		}

		/**
		 * This method returns the ArrayList of SelectableItems associated to this GroupSelectableItem.
		 * @return a <code>ArrayList</code> of SelectableItems.
		 */
		public ArrayList getItemSelectableItem() {
			return this.itemSelectableItem;
		}

		/**
		 * This method sets the ArrayList of SelectableItems of this GroupSelectableItem.
		 * @param itemSelectableItem ArrayList of SelectableItems.
		 */
		public void setItemSelectableItem(final ArrayList itemSelectableItem) {
			this.itemSelectableItem = itemSelectableItem;
		}

		/**
		 * This method adds a SelectableItem to the ArrayList of SelectablItems.
		 * @param item The SelectbleItem to be added.
		 */
		public void addItemSelectableItem(final SelectableItem item) {
			if (this.itemSelectableItem == null) {
				this.itemSelectableItem = new ArrayList();
			}
			this.itemSelectableItem.add(item);
		}

		public boolean isEmpty() {
			if (this.itemSelectableItem == null) {
				return true;
			}
			return false;
		}

	}

	/**
	 * This class configures the JDialog in which the visibility of the MenuItems of the MenuGroups are
	 * configured.
	 *
	 * @author Imatia Innovation.
	 *
	 */
	public static class SetupDialog extends EJDialog implements Internationalization {

		/**
		 * Text of the title of the JDialog.
		 */
		public static final String MENU_TITLE_TEXT_KEY = "navigatormenu.title_dialog";

		/**
		 * Text of the accept button.
		 */
		public static final String ACCEPT_BUTTON_TEXT = "OptionPane.okButtonText";

		/**
		 * Text of the cancel button.
		 */
		public static final String CANCEL_BUTTON_TEXT = "OptionPane.cancelButtonText";

		/**
		 * Code of the operation of push the accept button.
		 */
		public static final int ACCEPT = 0;

		/**
		 * Code of the operation of push the cancel button.
		 */
		public static final int CANCEL = -1;

		/**
		 * ResourceBundle to be applied to the JDialog.
		 */
		protected ResourceBundle bundle = null;

		/**
		 * Contains the result of the operation that was done over the JDialog.
		 */
		public int operation = -1;

		/**
		 * ArrayList that contains all the MenuGroups of the NavigationMenu.
		 */
		protected ArrayList menulist = null;

		/**
		 * Map that contains the whole information about a MenuGroup.
		 */
		protected Map menuGroupInfo = null;

		/**
		 * Cancel JButton.
		 */
		protected JButton bcancel = null;

		/**
		 * Accept JButton
		 */
		protected JButton baccept = null;

		/**
		 * Constructs a new JDialog with the specified parameters.
		 * @param owner Frame
		 * @param modal Boolean to configure modal mode.
		 */
		public SetupDialog(final Frame owner, final boolean modal) {
			super(owner, SetupDialog.MENU_TITLE_TEXT_KEY, modal);
		}

		/**
		 * Constructs a new JDialog with the specified parameters.
		 * @param owner Dialog
		 * @param modal Boolean to configure modal mode.
		 */
		public SetupDialog(final Dialog owner, final boolean modal) {
			super(owner, SetupDialog.MENU_TITLE_TEXT_KEY, modal);
		}

		boolean allDeselected = false;

		/**
		 * This method initializes the JDialog of visibility configuration.
		 */
		public void jInit() {
			// setUndecorated(true);
			final ArrayList menuGroupGUI = this.menulist;

			final JPanel jMyPanel = new JPanel();
			jMyPanel.setLayout(new GridBagLayout());
			jMyPanel.setOpaque(true);
			jMyPanel.setBackground(new Color(215, 221, 223));

			final DefaultListModel listModel = new DefaultListModel();

			for (int i = 0; i < menuGroupGUI.size(); i++) {
				final MenuGroupGUI menuGroupSelected = (MenuGroupGUI) menuGroupGUI.get(i);
				final String groupName = menuGroupSelected.getHeader();
				final GroupSelectableItem gsItem = new GroupSelectableItem(groupName);

				boolean allSelected = true;
				final MenuItem[] items = menuGroupSelected.getMenuItem();
				if ((items != null) && (items.length > 0)) {
					for (int j = 0; j < items.length; j++) {
						final String view = items[j].getManager();
						final SelectableItem selItem = new SelectableItem(view);
						final MenuItemGUI mIGUI = (MenuItemGUI) items[j];
						if (mIGUI.isVisiblePermission()) {
							if (items[j].isVisible()) {
								selItem.setSelected(true);
							} else {
								allSelected = false;
							}
							gsItem.addItemSelectableItem(selItem);
						}
					}

					if (allSelected) {
						gsItem.setSelected(true);
					}
					if (!gsItem.isEmpty()) {
						listModel.addElement(gsItem);
					}
				}
			}

			final JList jlistGroup = new JList(listModel);
			final SelectableItemListCellRenderer selItemRenderer = new SelectableItemListCellRenderer(this.bundle);
			selItemRenderer.setSelectedBackground(new Color(99, 200, 219));
			selItemRenderer.setSelectedForeground(new Color(255, 255, 255));
			selItemRenderer.setNotSelectedBackground(new Color(240, 240, 240));
			selItemRenderer.setNotSelectedForeground(new Color(67, 67, 67));
			selItemRenderer.setFont(new Font("Verdana", Font.PLAIN, 10));
			jlistGroup.setCellRenderer(selItemRenderer);
			jlistGroup.setOpaque(true);
			jlistGroup.setBackground(new Color(240, 240, 240));

			final JScrollPane jScrollPaneGroup = new JScrollPane(jlistGroup);
			jScrollPaneGroup.setBorder(null);
			final JList jlistItem = new JList(new DefaultListModel());
			jlistItem.setCellRenderer(selItemRenderer);
			jlistItem.setOpaque(true);
			jlistItem.setBackground(new Color(240, 240, 240));

			final JScrollPane jScrollPaneItem = new JScrollPane(jlistItem);
			jScrollPaneItem.setBorder(null);

			final JPanel jPanelCheckSelection = new JPanel();
			jPanelCheckSelection.setLayout(new BorderLayout());
			final ButtonGroup checkBoxGroup = new ButtonGroup();
			final JCheckBox jcheckAll = new JCheckBox();
			jcheckAll.setText("Select all");
			jcheckAll.setFont(new Font("Verdana", Font.PLAIN, 9));
			jcheckAll.setForeground(new Color(240, 240, 240));
			jcheckAll.setOpaque(true);
			jcheckAll.setBackground(new Color(153, 153, 153));
			final JCheckBox jcheckNone = new JCheckBox();
			jcheckNone.setText("Deselect all");
			jcheckNone.setFont(new Font("Verdana", Font.PLAIN, 9));
			jcheckNone.setForeground(new Color(240, 240, 240));
			jcheckNone.setOpaque(true);
			jcheckNone.setBackground(new Color(153, 153, 153));
			final JCheckBox jcheck = new JCheckBox();
			jcheck.setVisible(false);
			jcheck.setSelected(true);

			checkBoxGroup.add(jcheckAll);
			checkBoxGroup.add(jcheckNone);
			checkBoxGroup.add(jcheck);
			// jPanelCheckSelection.add(checkBoxGroup,BorderLayout.CENTER);
			jPanelCheckSelection.add(jcheckAll, BorderLayout.NORTH);
			jPanelCheckSelection.add(jcheckNone, BorderLayout.SOUTH);

			final InnerLabel jLblMenu = new InnerLabel();
			jLblMenu.setOpaque(true);
			jLblMenu.setText("menus");
			jLblMenu.setFont(new Font("Verdana", Font.BOLD, 14));
			jLblMenu.setForeground(new Color(255, 255, 255));
			jLblMenu.setBackground(new Color(67, 67, 67));
			jLblMenu.setHorizontalAlignment(SwingConstants.CENTER);
			jLblMenu.setPreferredSize(27);

			final InnerLabel jLblItem = new InnerLabel();
			jLblItem.setOpaque(true);
			jLblItem.setText("items");
			jLblItem.setFont(new Font("Verdana", Font.BOLD, 14));
			jLblItem.setForeground(new Color(255, 255, 255));
			jLblItem.setBackground(new Color(67, 67, 67));
			jLblItem.setHorizontalAlignment(SwingConstants.CENTER);
			jLblItem.setPreferredSize(27);

			final InnerLabel jLblBackGround = new InnerLabel();
			jLblBackGround.setOpaque(true);
			jLblBackGround.setBackground(new Color(222, 222, 222));
			jLblBackGround.setPreferredSize(41);

			final JPanel jPanelGroup = new JPanel();
			jPanelGroup.setLayout(new BorderLayout());
			jPanelGroup.add(jLblMenu, BorderLayout.NORTH);

			final JPanel jPanelGroupLists = new JPanel();
			jPanelGroupLists.setLayout(new BorderLayout());
			jPanelGroupLists.add(jPanelCheckSelection, BorderLayout.NORTH);
			jPanelGroupLists.add(jScrollPaneGroup, BorderLayout.CENTER);

			final Border border = BorderFactory.createMatteBorder(2, 1, 2, 2, new Color(109, 115, 115));

			jPanelGroupLists.setBorder(border);

			jPanelGroup.add(jPanelGroupLists, BorderLayout.CENTER);

			final JPanel jPanelItem = new JPanel();
			jPanelItem.setLayout(new BorderLayout());
			jPanelItem.add(jLblItem, BorderLayout.NORTH);

			final JPanel jPanelItemLists = new JPanel();
			jPanelItemLists.setLayout(new BorderLayout());
			jPanelItemLists.add(jLblBackGround, BorderLayout.NORTH);
			jPanelItemLists.add(jScrollPaneItem, BorderLayout.CENTER);
			jPanelItemLists.setBorder(border);

			jPanelItem.add(jPanelItemLists, BorderLayout.CENTER);

			final JSplitPane jsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jPanelGroup, jPanelItem);
			jsplitPane.setBorder(null);
			jsplitPane.setDividerSize(7);
			jMyPanel.add(jsplitPane, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.EAST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));

			this.baccept = new JButton(SetupDialog.ACCEPT_BUTTON_TEXT, ImageManager.getIcon(ImageManager.OK)) {

				BasicButtonUI buttonUI = new BasicButtonUI();

				@Override
				public void setUI(final ButtonUI ui) {
					super.setUI(new BasicButtonUI());
				}
			};

			this.baccept.setFont(new Font("Verdana", Font.PLAIN, 10));
			this.baccept.setOpaque(true);
			this.baccept.setBackground(new Color(74, 112, 116));
			this.baccept.setForeground(new Color(222, 222, 222));
			// baccept.setForeground(new Color(67,67,67));
			this.baccept.setBorder(null);
			this.baccept.setFocusPainted(false);

			jMyPanel.add(this.baccept, new GridBagConstraints(0, 1, 1, 1, 0.5, 0, GridBagConstraints.EAST, 0,
					new Insets(2, 0, 2, 3), 20, 10));

			this.bcancel = new JButton(SetupDialog.CANCEL_BUTTON_TEXT, ImageManager.getIcon(ImageManager.CANCEL)) {

				@Override
				public void setUI(final ButtonUI ui) {
					super.setUI(new BasicButtonUI());
				}
			};

			this.bcancel.setFont(new Font("Verdana", Font.PLAIN, 10));
			this.bcancel.setOpaque(true);
			this.bcancel.setBackground(new Color(74, 112, 116));
			this.bcancel.setForeground(new Color(222, 222, 222));
			// bcancel.setForeground(new Color(67,67,67));
			this.bcancel.setBorder(null);
			this.bcancel.setFocusPainted(false);
			jMyPanel.add(this.bcancel, new GridBagConstraints(1, 1, 1, 1, 0.5, 0, GridBagConstraints.WEST, 0,
					new Insets(2, 3, 2, 0), 20, 10));

			// //////////////////////////Listeners///////////////////////////////////////////////////
			this.baccept.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {

					Map data = new Hashtable();
					data = SetupDialog.this.getInformationJlists(jlistGroup, jlistItem);
					SetupDialog.this.setOutputData(data);
					SetupDialog.this.operation = SetupDialog.ACCEPT;
					SetupDialog.this.setVisible(false);
				}

			});

			this.bcancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					SetupDialog.this.operation = SetupDialog.CANCEL;
					SetupDialog.this.setVisible(false);
				}

			});

			jcheckAll.setFocusPainted(false);
			jcheckAll.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (jcheckAll.isSelected()) {
						for (int i = 0; i < jlistGroup.getModel().getSize(); i++) {
							final GroupSelectableItem group = (GroupSelectableItem) jlistGroup.getModel().getElementAt(i);
							group.setSelected(true);
							((DefaultListModel) jlistGroup.getModel()).setElementAt(group, i);
						}
						jPanelGroupLists.repaint();
					}
				}

			});

			jcheckNone.setFocusPainted(false);
			jcheckNone.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (jcheckNone.isSelected()) {
						for (int i = 0; i < jlistGroup.getModel().getSize(); i++) {
							final GroupSelectableItem group = (GroupSelectableItem) jlistGroup.getModel().getElementAt(i);
							group.setSelected(false);
							((DefaultListModel) jlistGroup.getModel()).setElementAt(group, i);
						}
						jPanelGroupLists.repaint();
					}
					SetupDialog.this.allDeselected = true;
				}

			});

			jlistGroup.getModel().addListDataListener(new ListDataListener() {

				@Override
				public void contentsChanged(final ListDataEvent e) {

					final int index = e.getIndex0();
					final GroupSelectableItem gsItem = (GroupSelectableItem) jlistGroup.getModel().getElementAt(index);
					final ArrayList selectableItem = gsItem.getItemSelectableItem();
					if (gsItem.isSelected()) {
						for (int j = 0; j < selectableItem.size(); j++) {
							final SelectableItem selItem = (SelectableItem) selectableItem.get(j);
							selItem.setSelected(true);
						}
					} else {
						for (int j = 0; j < selectableItem.size(); j++) {
							final SelectableItem selItem = (SelectableItem) selectableItem.get(j);
							selItem.setSelected(false);
						}
					}

					jsplitPane.repaint();
				}

				@Override
				public void intervalAdded(final ListDataEvent e) {
				}

				@Override
				public void intervalRemoved(final ListDataEvent e) {
				}

			});

			jlistGroup.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					final int index = jlistGroup.getSelectedIndex();
					final GroupSelectableItem menuSelected = (GroupSelectableItem) jlistGroup.getModel().getElementAt(index);
					final String menuGroupNameSelected = menuSelected.getText();
					final ArrayList selectableItem = menuSelected.getItemSelectableItem();

					for (int i = 0; i < menuGroupGUI.size(); i++) {
						final MenuGroupGUI menuGroupSelected = (MenuGroupGUI) menuGroupGUI.get(i);
						if (menuGroupSelected.getHeader().equals(menuGroupNameSelected)) {

							final DefaultListModel dataItemModel = (DefaultListModel) jlistItem.getModel();
							dataItemModel.clear();
							for (int j = 0; j < selectableItem.size(); j++) {
								final SelectableItem selItem = (SelectableItem) selectableItem.get(j);
								dataItemModel.addElement(selItem);
							}
							break;
						}
					}

					boolean anySelected = false;

					for (int j = 0; j < jlistGroup.getModel().getSize(); j++) {
						final GroupSelectableItem menu = (GroupSelectableItem) jlistGroup.getModel().getElementAt(j);

						if (menu.isSelected()) {
							anySelected = true;
						}
					}

					if (anySelected) {
						jcheck.setSelected(true);
					}
					if (!anySelected && SetupDialog.this.allDeselected) {
						jcheck.setSelected(true);
						SetupDialog.this.allDeselected = false;
					}
				}

			});

			jlistGroup.addMouseListener(new SelectableItemMouseListener());

			jlistItem.addMouseListener(new SelectableItemMouseListener());

			jlistItem.getModel().addListDataListener(new ListDataListener() {

				@Override
				public void contentsChanged(final ListDataEvent e) {
					boolean allmarked = true;

					for (int i = 0; i < jlistItem.getModel().getSize(); i++) {
						final SelectableItem selItem = (SelectableItem) jlistItem.getModel().getElementAt(i);
						if (!selItem.isSelected()) {
							allmarked = false;
						}
					}
					final int index = jlistGroup.getSelectedIndex();
					final GroupSelectableItem groupItem = (GroupSelectableItem) jlistGroup.getModel().getElementAt(index);
					if (allmarked && !groupItem.isSelected()) {
						groupItem.setSelected(true);
					}
					if (!allmarked && groupItem.isSelected()) {
						groupItem.setSelected(false);
					}

					jsplitPane.repaint();
				}

				@Override
				public void intervalAdded(final ListDataEvent e) {
				}

				@Override
				public void intervalRemoved(final ListDataEvent e) {
				}

			});

			jlistItem.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
				}

			});

			this.getContentPane().add(jMyPanel);
		}

		/**
		 * This method returns the necessary data to configure the JDialog.
		 * @return a <code>ArrayList</code> whit the input data necessary.
		 */
		public ArrayList getInputData() {
			return this.menulist;
		}

		/**
		 * This method establishes the input data necessary to configure the JDialog. That is a list with
		 * the whole MenuGroups of the NavigationMenu.
		 * @param menulist ArrayList whit the input data necessary.
		 */
		public void setInputData(final ArrayList menulist) {
			this.menulist = menulist;
		}

		/**
		 * This method returns a data structure with the visibility configuration of the MenuItems of the
		 * MenuGroups.
		 * @return a <code>Hashtable</code> whit the data structure.
		 */
		public Map getOutputData() {
			return this.menuGroupInfo;
		}

		/**
		 * This method established the data structure with the visibility configuration of the MenuItems of
		 * the MenuGroups.
		 * @param data Hashatble with the data structure.
		 */
		public void setOutputData(final Map data) {
			this.menuGroupInfo = data;
		}

		/**
		 * This method obtains the information of the Jlists with the visibility configuration of the
		 * MenuItems and creates a Map containing all this information.
		 * @param jlistGroup Jlist that contains all the MenuGroups of the NavigationMenu.
		 * @param jlistItem JList that contains all MenuItems of each MenuGroup.
		 * @return a <code>Hashtable</code> with all information.
		 */
		public Map getInformationJlists(final JList jlistGroup, final JList jlistItem) {
			final Map data = new Hashtable();

			final int numMenuGroups = jlistGroup.getModel().getSize();
			for (int i = 0; i < numMenuGroups; i++) {
				final GroupSelectableItem menuGroup = (GroupSelectableItem) jlistGroup.getModel().getElementAt(i);
				final String menuGName = menuGroup.getText();

				final List infoItems = new Vector();
				final ArrayList items = menuGroup.getItemSelectableItem();
				for (int j = 0; j < items.size(); j++) {
					final SelectableItem selItem = (SelectableItem) items.get(j);
					final String name = selItem.getText();
					boolean selected = true;
					if (!selItem.isSelected()) {
						selected = false;
					}
					final MenuItemInformation menuIInfo = new MenuItemInformation(name, selected);
					infoItems.add(menuIInfo);
				}
				if ((menuGName != null) && !infoItems.isEmpty()) {
					data.put(menuGName, infoItems);
				}
			}

			return data;
		}

		/**
		 * Returns list with all <code>MenuGroup</code> in component.
		 * @return menugroup list
		 * @since 5.2068EN-0.7
		 */
		public ArrayList getMenulist() {
			return this.menulist;
		}

		/**
		 * Set list with all <code>MenuGroup</code> in component.
		 * @param menulist List with <code>MenuGroup</code>
		 * @since 5.2068EN-0.7
		 */
		public void setMenulist(final ArrayList menulist) {
			this.menulist = menulist;
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public void setComponentLocale(final Locale arg0) {
		}

		@Override
		public void setResourceBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
			this.setTitle(ApplicationManager.getTranslation(SetupDialog.MENU_TITLE_TEXT_KEY, this.bundle));
			if (this.baccept != null) {
				this.baccept.setText(ApplicationManager.getTranslation(SetupDialog.ACCEPT_BUTTON_TEXT, this.bundle));
			}
			if (this.bcancel != null) {
				this.bcancel.setText(ApplicationManager.getTranslation(SetupDialog.CANCEL_BUTTON_TEXT, this.bundle));
			}

		}

	}

	public static class InnerLabel extends JLabel {

		public void setPreferredSize(final int height) {
			final Dimension preferredSize = new Dimension();
			preferredSize.width = 50;
			preferredSize.height = height;
			super.setPreferredSize(preferredSize);
		}

	}

	/**
	 * This class is necessary to the visibility configuration of the MenuItems. It contains the current
	 * state of selection of each MenuItem into the JList of the configuration JDialog.
	 *
	 * @author Imatia Innovation.
	 *
	 */
	public static class MenuItemInformation {

		/**
		 * Boolean that indicates if a MenuItem is selected.
		 */
		protected boolean selected;

		/**
		 * String with the itemName.
		 */
		protected String itemName;

		/**
		 * Constructs a new MenuItemInformation with the specified parameters.
		 * @param itemName The name of the MenuItem.
		 * @param selected Boolean indicating if the MenuItem is selected.
		 */
		public MenuItemInformation(final String itemName, final boolean selected) {
			this.itemName = itemName;
			this.selected = selected;
		}

		/**
		 * This method returns if the MenuItem is selected.
		 * @return a <code>boolean</code> with the selection value.
		 */
		public boolean isSelected() {
			return this.selected;
		}

		/**
		 * This method establishes the visibility of the MenuItem.
		 * @param selected
		 */
		public void setSelected(final boolean selected) {
			this.selected = selected;
		}

		/**
		 * This method returns the MenuItem name.
		 * @return a <code>String</code> with the MenuItem name.
		 */
		public String getItemName() {
			return this.itemName;
		}

		/**
		 * This method establishes the MenuItem name.
		 * @param itemName The name of the MenuItem.
		 */
		public void setItemName(final String itemName) {
			this.itemName = itemName;
		}

	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
