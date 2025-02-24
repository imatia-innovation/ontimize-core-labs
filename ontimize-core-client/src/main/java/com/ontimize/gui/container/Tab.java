package com.ontimize.gui.container;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.CustomColumnGridBagLayout;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;

/**
 * This class implements a tab into a <code>JPanel</code> container.
 * <p>
 *
 * @author Imatia Innovation
 */
public class Tab extends JPanel implements FormComponent, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Tab.class);

	/**
	 * The insets for default margin. By default, null.
	 */
	public static Insets DEFAULT_TAB_MARGIN = null;

	/**
	 * Condition about querying when it is visible the default value. By default, true.
	 */
	public static boolean QUERY_IF_VISIBLE_DEFAULT_VALUE = true;

	/**
	 * Condition about scroll use. By default, false.
	 */
	public static boolean USE_SCROLL = false;

	/**
	 * The tab title. By default, "Tab"
	 */
	protected String title = new String("Tab");

	/**
	 * The title key reference. By default, null.
	 */
	protected String titleKey = null;

	/**
	 * An instance of a scroll pane.
	 */
	protected JScrollPane scroll = new JScrollPane();

	/**
	 * An instance of a panel.
	 */
	protected JPanel containPanel = new JPanel();

	/**
	 * An instance of layout.
	 */
	protected CustomColumnGridBagLayout layout = new CustomColumnGridBagLayout(this.containPanel);

	/**
	 * The reference for an icon. By default, null.
	 */
	protected ImageIcon icon = null;

	/**
	 * The attribute reference. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The visible permission reference. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * The enabled permission reference. By default, null.
	 */
	protected FormPermission enabledPermission = null;

	/**
	 * A reference for the parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The reference to the tip key. By default, null.
	 */
	protected String tipKey = null;

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * The querying when it is visible the default value condition assignation.
	 */
	protected boolean queryIfVisible = Tab.QUERY_IF_VISIBLE_DEFAULT_VALUE;

	/**
	 * The use scroll condition.
	 */
	protected boolean useScroll = Tab.USE_SCROLL;

	/**
	 * The tab field attribute vector. By defautl, null.
	 */
	protected List tabFieldAttributesList = null;

	/**
	 * The List for not required tab field attribute list. By default, null.
	 */
	protected List noRequiredTabFieldAttributesList = null;

	/**
	 * Init parameters and sets scroll and margins.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public Tab(final Map parameters) {
		this.init(parameters);
		if (Tab.DEFAULT_TAB_MARGIN != null) {
			this.setBorder(new EmptyBorder(Tab.DEFAULT_TAB_MARGIN));
		}
		this.containPanel.setLayout(this.layout);
		if (this.useScroll) {
			super.add(this.scroll);
			this.scroll.getViewport().add(this.containPanel);
		} else {
			super.add(this.containPanel);
		}
		this.containPanel.setOpaque(false);
		this.scroll.setOpaque(false);
		this.scroll.getViewport().setOpaque(false);
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (this.title != null) {
			return this.title;
		} else if (this.attribute != null) {
			return this.attribute.toString();
		}
		return this.title;
	}

	@Override
	public LayoutManager getLayout() {
		return this.containPanel.getLayout();
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>attribute</td>
	 * <td><b>values</td>
	 * <td><b>default</td>
	 * <td><b>required</td>
	 * <td><b>meaning</td>
	 * </tr>
	 * <tr>
	 * <td>title</td>
	 * <td></td>
	 * <td></td>
	 * <td>no (only required when attr is not specified)</td>
	 * <td>The title for component. It must be unique in the form.</td>
	 * </tr>
	 * <tr>
	 * <td>icon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The icon panel.</td>
	 * </tr>
	 * <tr>
	 * <td>attr</td>
	 * <td></td>
	 * <td></td>
	 * <td>no (only required when title is not specified)</td>
	 * <td>Indicates the component attribute.</td>
	 * </tr>
	 * <tr>
	 * <td>queryifvisible</td>
	 * <td>yes/no</td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Specifies when data tab are queried. Only when is visible or in normal query mode.</td>
	 * </tr>
	 * <tr>
	 * <td>tip</td>
	 * <td></td>
	 * <td>title</td>
	 * <td>no</td>
	 * <td>Specifies the tip for tab.</td>
	 * </tr>
	 * <tr>
	 * <td>opaque</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Specifies if the component must be opaque or not.</td>
	 * </tr>
	 * <tr>
	 * <td>bgcolor</td>
	 * <td>A color</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Specifies the component background color.</td>
	 * </tr>
	 * </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		this.setLayout(new BorderLayout());
		final Object attr = parameters.get("attr");
		if (attr == null) {
			if (ApplicationManager.DEBUG) {
				Tab.logger.debug(this.getClass().toString() + "attr" + " parameter hasn't been found");
			}
		} else {
			this.attribute = attr;
			this.title = attr.toString();
			this.titleKey = attr.toString();
		}

		final Object tit = parameters.get("title");
		if (tit != null) {
			this.title = tit.toString();
			this.titleKey = tit.toString();
		}

		final Object icon = parameters.get("icon");
		if (icon != null) {
			final ImageIcon imageIcon = ImageManager.getIcon(icon.toString());
			if (imageIcon != null) {
				this.icon = imageIcon;
			} else {
				Tab.logger.debug(this.getClass().toString() + " : " + icon + " icon hasn't been found");
			}
		}

		final Object queryifvisible = parameters.get("queryifvisible");
		if (queryifvisible == null) {
		} else {
			if (queryifvisible.toString().equalsIgnoreCase("yes")) {
				this.queryIfVisible = true;
			} else {
				this.queryIfVisible = false;
			}
		}

		final Object usescroll = parameters.get("usescroll");
		if (usescroll == null) {
		} else {
			if (usescroll.toString().equalsIgnoreCase("yes")) {
				this.useScroll = true;
			} else {
				this.useScroll = false;
			}
		}

		final Object tip = parameters.get("tip");
		if (tip != null) {
			this.tipKey = tip.toString();
		} else {
			this.tipKey = this.titleKey;
		}

		final Object oOpaque = parameters.get("opaque");
		// if (oOpaque != null &&
		// !ApplicationManager.parseStringValue(parameters.get("opaque").toString()))
		// {
		// this.setOpaque(false);
		// }

		if (oOpaque != null) {
			this.setOpaque(ApplicationManager.parseStringValue(oOpaque.toString()));
		} else {
			// By default
			this.setOpaque(false);
		}

		final Object bgcolor = parameters.get(DataField.BGCOLOR);
		if (bgcolor != null) {
			final String bg = bgcolor.toString();
			if (bg.indexOf(";") > 0) {
				try {
					this.setBackground(ColorConstants.colorRGBToColor(bg));
				} catch (final Exception e) {
					Tab.logger.error(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage(),
							e);
				}
			} else {
				try {
					this.setBackground(ColorConstants.parseColor(bg));
				} catch (final Exception e) {
					Tab.logger.error(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage(),
							e);
				}
			}
		}

	}

	/**
	 * Returns the <code>queryIfVisible</code> condition.
	 * <p>
	 * @return the <code>queryIfVisible</code> condition
	 */
	public boolean isQueryIfVisible() {
		return this.queryIfVisible;
	}

	/**
	 * Sets the query visibility condition.
	 * <p>
	 * @param query the query condition
	 */
	public void setQueryIfVisible(final boolean query) {
		this.queryIfVisible = query;
	}

	/**
	 * Gets the title key.
	 * <p>
	 * @return the title key
	 */
	public String getTitleKey() {
		return this.titleKey;
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.titleKey);
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			final JTabbedPane parentPanel = (JTabbedPane) this.getParent();
			final int index = parentPanel.indexOfTab(this.title);
			if (resources != null) {
				this.title = resources.getString(this.titleKey);
				parentPanel.setTitleAt(index, this.title);
			}
			final String tip = ApplicationManager.getTranslation(this.tipKey, resources);
			parentPanel.setToolTipTextAt(index, tip);

		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tab.logger.debug(this.getClass().toString() + " : " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	@Override
	public void add(final Component c, final Object constraints, final int index) {
		this.containPanel.add(c, constraints, index);
	}

	@Override
	public void add(final Component c, final Object constraints) {
		this.containPanel.add(c, constraints);
	}

	@Override
	public Component add(final Component c) {
		return this.containPanel.add(c);
	}

	@Override
	public Component add(final Component c, final int index) {
		return this.containPanel.add(c, index);
	}

	/**
	 * Gets the icon.
	 * <p>
	 * @return the icon reference
	 */
	public Icon getIcon() {
		return this.icon;
	}

	/**
	 * Gets the tip key.
	 * <p>
	 * @return the tip
	 */
	public String getTip() {
		return this.tipKey;
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void setVisible(final boolean vis) {
		if (vis) {
			final boolean permission = this.checkVisiblePermission();
			if (!permission) {
				return;
			}
		}
		super.setVisible(vis);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		super.setEnabled(enabled);
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			final Container parent = this.getParent();
			if (parent instanceof TabPanel) {
				((TabPanel) parent).hideTabs(this.getConstraints(null).toString());
			} else if (parent instanceof JTabbedPane) {
				for (int i = 0; i < ((JTabbedPane) parent).getTabCount(); i++) {
					if (((JTabbedPane) parent).getComponentAt(i) == this) {
						((JTabbedPane) parent).setEnabledAt(i, false);
						break;
					}
				}
			} else {
				this.setVisible(false);
			}
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			final Container parent = this.getParent();
			if (parent instanceof TabPanel) {
				((TabPanel) parent).setTabEnabled(this.getConstraints(null).toString(), false);
			} else if (parent instanceof JTabbedPane) {
				for (int i = 0; i < ((JTabbedPane) parent).getTabCount(); i++) {
					if (((JTabbedPane) parent).getComponentAt(i) == this) {
						((JTabbedPane) parent).setEnabledAt(i, false);
						break;
					}
				}
			} else {
				this.setEnabled(false);
			}
		}
	}

	/**
	 * Checks the visible permissions.
	 * <p>
	 * @return the visibility condition
	 */
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
					Tab.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Tab.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Gets the tab field attributes.
	 * <p>
	 * @return the List with tab field attributes
	 */
	public List getTabFieldAttributes() {
		if (this.tabFieldAttributesList == null) {
			this.tabFieldAttributesList = new Vector();
			this.getFieldAttributes(this, this.tabFieldAttributesList);
		}
		return this.tabFieldAttributesList;
	}

	/**
	 * Gets not required tab field attributes.
	 * <p>
	 * @return the List with not required tab fields attributes
	 */
	public List getNotRequiredTabFieldAttributes() {
		if (this.noRequiredTabFieldAttributesList == null) {
			this.noRequiredTabFieldAttributesList = new Vector();
			this.getNotRequiredFieldAttributes(this, this.noRequiredTabFieldAttributesList);
		}
		return this.noRequiredTabFieldAttributesList;
	}

	/**
	 * Gets the field attributes for a container.
	 * <p>
	 * @param c the container where component is present
	 * @param v the List to insert the field attributes
	 */
	protected void getFieldAttributes(final Container c, final List v) {
		for (int i = 0; i < c.getComponentCount(); i++) {
			final Component cA = c.getComponent(i);
			if (cA instanceof DataComponent) {
				v.add(((DataComponent) cA).getAttribute());
			}
			if (cA instanceof Container) {
				this.getFieldAttributes((Container) cA, v);
			}
		}
	}

	/**
	 * Gets the not required field attributes.
	 * <p>
	 * @param c the container
	 * @param v the List to get the not required field attributes
	 */
	protected void getNotRequiredFieldAttributes(final Container c, final List v) {
		for (int i = 0; i < c.getComponentCount(); i++) {
			final Component cA = c.getComponent(i);
			if ((cA instanceof DataComponent) && !((DataComponent) cA).isRequired()) {
				v.add(((DataComponent) cA).getAttribute());
			}
			if (cA instanceof TabPanel) {
				final TabPanel parent = (TabPanel) cA;
				final int number = parent.getTabCount();
				for (int j = 0; j < number; j++) {
					final Tab current = (Tab) parent.getComponentAt(j);
					final int currentindex = parent.getSelectedIndex();
					if (j == currentindex) {
						this.getNotRequiredFieldAttributes(current, v);
					} else if (!current.isQueryIfVisible()) {
						this.getNotRequiredFieldAttributes(current, v);
					}
				}

				continue;
			}
			if (cA instanceof Container) {
				this.getNotRequiredFieldAttributes((Container) cA, v);
			}
		}
	}

	/**
	 * Checks the enabled permissions.
	 * <p>
	 * @return the enabled permission condition
	 */
	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.enabledPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Tab.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Tab.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
