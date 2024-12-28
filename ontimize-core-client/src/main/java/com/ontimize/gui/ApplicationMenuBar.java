package com.ontimize.gui;

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.xml.XMLApplicationBuilder;
import com.ontimize.gui.container.RadioItemGroup;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.i18n.LocaleListener;
import com.ontimize.gui.i18n.MenuLocale;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.gui.preferences.ShortcutDialogConfiguration;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.xml.XMLClientProvider;

/**
 * Basic implementation of the application menu bar.
 *
 * @version 1.0
 */
public class ApplicationMenuBar extends JMenuBar
implements FormComponent, Freeable, HasHelpIdComponent, HasPreferenceComponent {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationMenuBar.class);

	protected Map menuItemList = new Hashtable();

	private MenuElement element = null;

	protected boolean dynamicloaded = false;

	private boolean listBuild = false;

	private List buttonGroup = new Vector();

	// TODO check the use of an empty image for the menu items
	private static ImageIcon emptyIcon_16 = null;

	protected ResourceBundle resources;

	private ShortcutDialogConfiguration menuShortcutConfiguration = null;

	public ApplicationMenuBar(final Map parameters) {
		this.init(parameters);
		if (ApplicationMenuBar.emptyIcon_16 == null) {
			ApplicationMenuBar.emptyIcon_16 = ImageManager.getIcon(ImageManager.EMPTY_16);
		}
	}

	@Override
	public void init(final Map parameters) {
		this.installHelpId();
	}

	/**
	 * Return always null
	 */
	@Override
	public Object getConstraints(final LayoutManager layout) {
		return null;
	}

	/**
	 * Method used to store a reference to the RadioItemGroup elements
	 * @param buttonsGroup
	 */
	public void add(final RadioItemGroup buttonsGroup) {
		this.buttonGroup.add(buttonsGroup);
	}

	/**
	 * Get a reference to the component with the specified attribute. The element can be a Menu or a
	 * Menu Item
	 * @param attribute
	 * @return Element reference or null if there are not elements with the specified attribute
	 */
	public JMenuItem getMenuItem(final String attribute) {
		if (!this.listBuild) {
			this.buildList(this);
			this.listBuild = true;
		}
		if (this.menuItemList.containsKey(attribute)) {
			return (JMenuItem) this.menuItemList.get(attribute);
		} else {
			return null;
		}
	}

	/**
	 * Enables or disables the element with the specified attribute. If there are no components with
	 * this attribute nothing is done
	 * @param attribute
	 * @param enabled
	 */
	public void setItemMenuEnabled(final String attribute, final boolean enabled) {
		final JMenuItem item = this.getMenuItem(attribute);
		if (item != null) {
			item.setEnabled(enabled);
		}
	}

	public void clearItemList() {
		this.menuItemList.clear();
		this.listBuild = false;
	}

	/**
	 * Create a List with all the menu elements
	 * @return A List with all the menu elements
	 */
	public List getAllItems() {
		if (!this.listBuild) {
			this.buildList(this);
			this.listBuild = true;
		}
		final List items = new Vector();
		final Enumeration e = Collections.enumeration(this.menuItemList.keySet());
		while (e.hasMoreElements()) {
			items.add(this.menuItemList.get(e.nextElement()));
		}
		return items;
	}

	/**
	 * Creates the list with the menu elements including the identificator
	 * @param element
	 */
	private void buildList(final MenuElement element) {
		if (element instanceof IdentifiedElement) {
			if (this.menuItemList.containsKey(((IdentifiedElement) element).getAttribute())) {
				ApplicationMenuBar.logger
				.debug("ApplicationMenuBar: '" + ((IdentifiedElement) element).getAttribute()
						+ "' attribute has multiple instances. The xml file must be checked.");
			}
			this.menuItemList.put(((IdentifiedElement) element).getAttribute(), element);
		}
		// Now children
		final MenuElement[] childElements = element.getSubElements();
		// If it is a menu and some of the children has an icon use an empty
		// icon to align all items
		if (element instanceof JPopupMenu) {
			boolean bSomeIcon = false;
			for (int i = 0; i < childElements.length; i++) {
				final MenuElement childElement = childElements[i];
				if (childElement instanceof AbstractButton) {
					if (((AbstractButton) childElement).getIcon() != null) {
						bSomeIcon = true;
						break;
					}
				}
			}
			if (bSomeIcon && !ApplicationManager.jvmVersionHigherThan_1_6_0()) {
				for (int i = 0; i < childElements.length; i++) {
					final MenuElement childElement = childElements[i];
					if (childElement instanceof AbstractButton) {
						if (((AbstractButton) childElement).getIcon() == null) {
							((AbstractButton) childElement).setIcon(ApplicationMenuBar.emptyIcon_16);
						}
					}
				}
			}
		}
		for (int i = 0; i < childElements.length; i++) {
			this.buildList(childElements[i]);
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		this.getChildsTexts(this, v);
		return v;
	}

	private void getChildsTexts(final MenuElement menuElement, final List v) {
		final MenuElement[] childElements = menuElement.getSubElements();
		for (int i = 0; i < childElements.length; i++) {
			if (childElements[i] instanceof Internationalization) {
				v.addAll(((Internationalization) childElements[i]).getTextsToTranslate());
			}
			this.getChildsTexts(childElements[i], v);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resources = resources;
		this.setChildsResourceBundle(this, resources);
	}

	private void setChildsResourceBundle(final MenuElement menuElement, final ResourceBundle resoruces) {
		final MenuElement[] childElements = menuElement.getSubElements();
		for (int i = 0; i < childElements.length; i++) {
			if (childElements[i] instanceof Internationalization) {
				((Internationalization) childElements[i]).setResourceBundle(resoruces);
			}
			this.setChildsResourceBundle(childElements[i], resoruces);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void free() {
		this.menuItemList.clear();
		this.menuItemList = null;
		this.buttonGroup.clear();
		this.buttonGroup = null;
		this.element = null;
		final MenuElement[] children = this.getSubElements();
		// Free the menu components
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Freeable) {
				try {
					((Freeable) children[i]).free();
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						ApplicationMenuBar.logger.debug(this.getClass().toString() + ": " + "Exception in free() "
								+ children[i].getClass().toString() + " : " + e.getMessage(), e);
					}
				}
			}
		}
		// Remove the children
		this.removeAll();
		if (com.ontimize.gui.ApplicationManager.DEBUG) {
			ApplicationMenuBar.logger.debug(this.getClass() + " Liberado");
		}
	}

	/**
	 * Gets an List with the reference to the RadioItemGroup elements
	 * @return
	 */
	public List getButtonGroup() {
		return this.buttonGroup;
	}

	@Override
	public String getHelpIdString() {
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);
		return className + "HelpId";
	}

	@Override
	public void installHelpId() {
		try {
			final String helpId = this.getHelpIdString();
			HelpUtilities.setHelpIdString(this, helpId);
		} catch (final Exception e) {
			ApplicationMenuBar.logger.error(e.getMessage(), e);
			return;
		}
	}

	protected void createMenuShortcutsConfigurationDialog() {
		if (this.menuShortcutConfiguration == null) {
			this.menuShortcutConfiguration = new ShortcutDialogConfiguration(
					ApplicationManager.getApplication().getFrame(), this);
			this.menuShortcutConfiguration.pack();
			ApplicationManager.center(this.menuShortcutConfiguration);
		}
	}

	public void showMenuShortcutsConfigurationDialog() {
		// Window
		this.createMenuShortcutsConfigurationDialog();
		this.menuShortcutConfiguration.setResourceBundle(this.resources);
		this.menuShortcutConfiguration.setVisible(true);
	}

	public void addConfigurableKeyStrokeGroup(final String groupName, final List keyBindings) {
		this.createMenuShortcutsConfigurationDialog();
		this.menuShortcutConfiguration.addConfigurableKeyStrokeGroup(groupName, keyBindings);
	}

	/**
	 * This method convert a keystroke in a String with this structure: 'modifiers keycode'. <br>
	 * For example:<br>
	 *
	 * <br>
	 * For <b>keystroke</b> correspondent to: <b>'Ctrl + Alt + a'</b> , method will return:<br>
	 * '<b>650 65</b>'. '650' is the code for modifiers 'Ctrl + Alt' and '65' is the code for 'a'. <br>
	 * Shorcuts will be stored in preferences in this way.
	 *
	 * Note: Method that makes the opposite is
	 * @param accelerator keystroke to convert
	 * @return A <code>String</code> with numeric values of keystroke.
	 */
	public static String acceleratorToString(final KeyStroke accelerator) {
		if (accelerator == null) {
			return null;
		}
		String acceleratorText = "";
		if (accelerator != null) {
			final int modifiers = accelerator.getModifiers();
			if (modifiers > 0) {
				acceleratorText += modifiers;
				acceleratorText += " ";
			}
			final int keyCode = accelerator.getKeyCode();
			if (keyCode != 0) {
				acceleratorText += keyCode;
			}
		}
		return acceleratorText;
	}

	/**
	 * This method convert a keystroke in a String with this structure: i.e. <b>'Ctrl + Alt + a'</b>.
	 * Modifiers (Ctrl + Alt ) are also internazionalized for avoiding problems with jvm
	 * locale-dependent method: <code>getKeyStroke</code>.
	 * @param accelerator keystroke to convert
	 * @return A <code>String</code> with text for keystroke or directly the codes for keystroke when it
	 *         does not exist in bundle.
	 */
	public static String acceleratorMessageFromKeystroke(final KeyStroke accelerator) {
		String ksmessage = new String("");

		// Modifiers are translated because KeyEvent.getKeyModifiersText is
		// dependent of jvm location.
		final int modifiers = accelerator.getModifiers();
		if (modifiers > 0) {
			ksmessage += ApplicationManager.getTranslation(KeyEvent.getKeyModifiersText(modifiers));
			ksmessage += " ";
		}
		final int keyCode = accelerator.getKeyCode();
		if ((keyCode != 0) && (KeyEvent.VK_CONTROL != keyCode) && (KeyEvent.VK_ALT != keyCode)
				&& (KeyEvent.VK_SHIFT != keyCode)) {
			ksmessage += KeyEvent.getKeyText(keyCode);
		}
		return ksmessage;
	}

	@Override
	public void initPreferences(final ApplicationPreferences prefs, final String user) {
		this.initChildPreferences(this, prefs, user);
	}

	private void initChildPreferences(final MenuElement elementoMenu, final ApplicationPreferences prefs, final String user) {
		final MenuElement[] childElements = elementoMenu.getSubElements();
		for (int i = 0; i < childElements.length; i++) {
			if (childElements[i] instanceof HasPreferenceComponent) {
				((HasPreferenceComponent) childElements[i]).initPreferences(prefs, user);
			}
			this.initChildPreferences(childElements[i], prefs, user);
		}
	}

	public void loadDynamicItems() {
		if (!this.dynamicloaded) {
			try {
				final EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
				if (locator instanceof XMLClientProvider) {
					final XMLClientProvider clientProvider = (XMLClientProvider) locator;
					final String xmlMenu = clientProvider.getXMLMenu(locator.getSessionId());
					if (xmlMenu != null) {
						XMLApplicationBuilder.getXMLApplicationBuilder().getMenuBuilder().appendMenu(this, xmlMenu);

						this.clearItemList();
						final List allItems = this.getAllItems();
						for (int i = 0; i < allItems.size(); i++) {
							final Object item = allItems.get(i);
							if ((item instanceof IDynamicItem) && ((IDynamicItem) item).isDynamic()) {
								if (item instanceof MenuLocale) {
									((MenuLocale) item).addLocaleListener(
											(LocaleListener) ApplicationManager.getApplication().getMenuListener());
								} else {
									((JMenuItem) item).addActionListener(
											(ActionListener) ApplicationManager.getApplication().getMenuListener());
								}

								if (item instanceof Internationalization) {
									((Internationalization) item).setResourceBundle(this.resources);
								}
							}
						}
					}
				}
				this.dynamicloaded = true;
			} catch (final Exception e) {
				ApplicationMenuBar.logger.error(null, e);
			}
			this.revalidate();
			this.repaint();
		}
	}

	public void removeDynamicItems() {
		if (this.dynamicloaded) {
			try {
				// deleteDynamicItems
				final List allItems = this.getAllItems();
				for (int i = 0; i < allItems.size(); i++) {
					final JComponent currenComponent = (JComponent) allItems.get(i);
					if ((currenComponent instanceof IDynamicItem) && ((IDynamicItem) currenComponent).isDynamic()) {
						final Container parent = currenComponent.getParent();
						parent.remove(currenComponent);

						if (currenComponent instanceof MenuLocale) {
							((MenuLocale) currenComponent).removeLocaleListener(
									(LocaleListener) ApplicationManager.getApplication().getMenuListener());
						} else {
							((JMenuItem) currenComponent).removeActionListener(
									(ActionListener) ApplicationManager.getApplication().getMenuListener());
						}
					}
				}

				this.clearItemList();
				this.revalidate();
				this.repaint();
				this.dynamicloaded = false;
			} catch (final Exception e) {
				ApplicationMenuBar.logger.error(null, e);
			}
		}
	}

}
