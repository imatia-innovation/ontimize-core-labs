package com.ontimize.gui;

import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * The main class to create an item of a menu, where attribute parameter is the actionCommand.
 * <p>
 *
 * @author Imatia Innovation
 */

public class MenuItem extends JMenuItem implements FormComponent, IdentifiedElement, SecureElement, StatusComponent,
HasPreferenceComponent, IDynamicItem, IParameterItem, Transferable, DragGestureListener, DragSourceListener {

	private static final Logger logger = LoggerFactory.getLogger(MenuItem.class);

	/**
	 * The key of a menu accelerator.
	 */
	public static final String MENU_ACCELERATOR = "menu_accelerator";

	/**
	 * The status text reference. By default, null.
	 */
	protected String statusText = null;

	protected String iconPath = null;

	/**
	 * The menu item attribute. By default, null.
	 */
	protected String attribute = null;

	protected ResourceBundle bundle;

	/**
	 * The menu text to show. Useful for items which have the same attr. By default, null.
	 *
	 * @since 5.2074EN
	 */
	protected String text = null;

	/**
	 * The reference to shortcut. By default,"".
	 */
	protected String shortcut = "";

	/**
	 * The visible permission reference. By default, null.
	 */
	private MenuPermission visiblePermission = null;

	/**
	 * The enabled permission reference. By default, null.
	 */
	private MenuPermission enabledPermission = null;

	/**
	 * The form name to show
	 */
	protected String formName = null;

	/**
	 * True when the form is opened in a dialog
	 */
	protected boolean dialog = false;

	// /**
	// * True when the dialog is modal
	// */
	// protected boolean modal = true;

	/**
	 * The name of the FormManager
	 */
	protected String formManagerName = null;

	protected boolean dynamic = false;

	protected DragSource source;

	protected boolean dragEnabled = false;

	/**
	 * The class constructor. It initializes parameters, sets margin and register status component.
	 * <p>
	 * @param parameters The Map with parameters
	 */
	public MenuItem(final Map parameters) {
		this.init(parameters);
		final Insets iMargins = this.getMargin();
		final int iLeftMargin = iMargins.left;
		final int iNewMargin = Math.max(0, iLeftMargin - 5);
		this.setMargin(new Insets(iMargins.top, iNewMargin, iMargins.bottom, iMargins.right));
		this.initPermissions();
		ApplicationManager.registerStatusComponent(this);
		this.dynamic = ParseUtils.getBoolean((String) parameters.get("dynamic"), false);

		this.source = new DragSource();
		this.source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		return null;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>status</td>
	 *        <td></td>
	 *        <td>attr</td>
	 *        <td>no</td>
	 *        <td>The text showed in status bar application. MainApplication translates this text
	 *        according to the bundle.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>icon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Specifies the file name of button icon. Complete or relative path from Ontimize class
	 *        location, for example, if an icon is stored in com/ontimize/gui/images/item.gif, it would
	 *        be correct to specify the path like: 'item.gif'. If not stored in any baseImage bundle,
	 *        write the complete route</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>shortcut</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The reference to shortcut (for example: 'ctrl + C')</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>mnemonic</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Mnemonic letter. NOTE: This parameter has not been Internationalized yet</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>form</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The form name to be opened</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>showdialog</td>
	 *        <td>yes/no</td>
	 *        <td>If the 'form' parameter is established the default values is 'yes'.Otherwise the
	 *        default value is 'no'</td>
	 *        <td>no</td>
	 *        <td>Established whether the form is opened in a dialog</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>formmanager</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The name of FormManager to show. If this parameter isn't established the value is
	 *        taking from 'attr' parameter</td>
	 *        </tr>
	 *
	 *
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		// Attribute
		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr.toString();
			this.setText(this.attribute);
			this.setActionCommand(this.attribute);
		}

		this.text = ParseUtils.getString((String) parameters.get("text"), null);

		final Object status = parameters.get("status");
		if (status == null) {
			this.statusText = this.attribute;
		} else {
			this.statusText = status.toString();
		}

		// Icon parameter
		final Object icon = parameters.get("icon");
		if (icon != null) {
			this.setHorizontalAlignment(SwingConstants.LEFT);
			iconPath = icon.toString();
			final ImageIcon iconCurrent = ImageManager.getIcon(iconPath);
			if (iconCurrent != null) {
				this.setIcon(iconCurrent);
			}
		}
		final Object mnemonic = parameters.get("mnemonic");
		if ((mnemonic != null) && !mnemonic.equals("")) {
			this.setMnemonic(mnemonic.toString().charAt(0));
		}
		final Object shortcut = parameters.get("shortcut");
		if ((shortcut != null) && !shortcut.equals("")) {
			try {
				final KeyStroke ks = KeyStroke.getKeyStroke(shortcut.toString());
				super.setAccelerator(ks);
				this.shortcut = ApplicationMenuBar.acceleratorMessageFromKeystroke(ks);
			} catch (final Exception e) {
				MenuItem.logger.trace(null, e);
			}
		}

		boolean defaultShowDialog = false;
		final Object formName = parameters.get("form");

		if ((formName != null) && (formName instanceof String)) {
			this.setFormName((String) formName);
			defaultShowDialog = true;
		}

		this.formManagerName = ParseUtils.getString((String) parameters.get("formmanager"), (String) attr);
		this.dialog = ParseUtils.getBoolean((String) parameters.get("showdialog"), defaultShowDialog);
		// modal = ParseUtils.getBoolean((String)parameters.get("modal"), true);
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Adds the attribute parameter to the List to translate.
	 * <p>
	 * @return the List with attribute parameter.
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.attribute);
		if (this.text != null) {
			v.add(this.text);
		}
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			this.bundle = resources;
			if (resources != null) {
				if (this.text != null) {
					this.setText(resources.getString(this.text));
				} else {
					this.setText(resources.getString(this.attribute));
				}
			} else {
				if (this.text != null) {
					this.setText(this.text);
				} else {
					this.setText(this.attribute);
				}
			}
		} catch (final Exception e) {
			MenuItem.logger.trace(null, e);
		}
	}

	/**
	 * Sets the component locale.
	 * <p>
	 * @param l the locale to set
	 */
	@Override
	public void setComponentLocale(final Locale l) {
	}

	/**
	 * Removes all listener menu items.
	 */
	public void free() {
		final EventListener[] listeners = this.getListeners(Action.class);
		for (int i = 0; i < listeners.length; i++) {
			this.removeActionListener((ActionListener) listeners[i]);
		}
		final MenuElement[] children = this.getSubElements();
		// All children are freed
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Freeable) {
				try {
					((Freeable) children[i]).free();
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						MenuItem.logger.debug("Exception in free() method " + children[i].getClass().toString(), e);
					} else {
						MenuItem.logger.trace("Exception in free() method " + children[i].getClass().toString(), e);
					}
				}
			}
		}
		// Removes the children
		this.removeAll();
		if (ApplicationManager.DEBUG) {
			MenuItem.logger.debug(this.getClass().toString() + " Free");
		}

	}

	@Override
	public void setVisible(final boolean vis) {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				this.visiblePermission = new MenuPermission("visible", this.attribute, true);
			}
			try {
				// Checks to show
				if (vis) {
					manager.checkPermission(this.visiblePermission);
				}
				super.setVisible(vis);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG_SECURITY) {
					MenuItem.logger.debug(null, e);
				} else {
					MenuItem.logger.trace(null, e);
				}
			}
		} else {
			super.setVisible(vis);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				this.enabledPermission = new MenuPermission("enabled", this.attribute, true);
			}
			try {
				// checks to enable
				if ((enabled) && (manager != null)) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				super.setEnabled(enabled);
			} catch (final Exception e) {
				this.restricted = true;
				if (ApplicationManager.DEBUG_SECURITY) {
					MenuItem.logger.debug(null, e);
				} else {
					MenuItem.logger.trace(null, e);
				}
			}
		} else {
			super.setEnabled(enabled);
		}
	}

	/**
	 * Initializes the permissions.
	 */
	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				this.enabledPermission = new MenuPermission("enabled", this.attribute, true);
			}
			try {
				manager.checkPermission(this.enabledPermission);
				this.restricted = false;
			} catch (final Exception e) {
				this.restricted = true;
				super.setEnabled(false);

				if (ApplicationManager.DEBUG_SECURITY) {
					MenuItem.logger.debug(null, e);
				} else {
					MenuItem.logger.trace(null, e);
				}
			}
			if (this.visiblePermission == null) {
				this.visiblePermission = new MenuPermission("visible", this.attribute, true);
			}
			try {
				manager.checkPermission(this.visiblePermission);
			} catch (final Exception e) {
				super.setVisible(false);
				if (ApplicationManager.DEBUG_SECURITY) {
					MenuItem.logger.debug(null, e);
				} else {
					MenuItem.logger.trace(null, e);
				}
			}
		}
	}

	/**
	 * Gets the <code>StatusText</code> variable.
	 * <p>
	 * @return the status text
	 */
	@Override
	public String getStatusText() {
		return this.statusText;
	}

	/**
	 * The condition of restricted. By default, false.
	 */
	protected boolean restricted = false;

	/**
	 * Gets the restricted condition.
	 * <p>
	 * @return the {@link #restricted} variable value.
	 */
	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void setAccelerator(final KeyStroke ks) {
		super.setAccelerator(ks);
		// preferences
		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		if (prefs != null) {
			String user = null;
			if (ApplicationManager.getApplication().getReferenceLocator() instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser();
			}
			prefs.setPreference(user, this.getAcceleratorPreferencesKey(), ApplicationMenuBar.acceleratorToString(ks));
		}
	}

	/**
	 * Initializes the application preferences.
	 * <p>
	 * @param aPrefs the application preferences
	 * @param user the user reference
	 */
	@Override
	public void initPreferences(final ApplicationPreferences aPrefs, final String user) {
		// KeyStroke
		if (aPrefs != null) {
			final String pref = aPrefs.getPreference(user, this.getAcceleratorPreferencesKey());
			if (pref != null) {
				KeyStroke ks = null;
				final String prefs[] = pref.split(" ");
				try {
					ks = KeyStroke.getKeyStroke(Integer.parseInt(prefs[1]), Integer.parseInt(prefs[0]));
				} catch (final Exception e) {
					MenuItem.logger.trace(null, e);
				}
				if (ks != null) {
					super.setAccelerator(ks);
				}
			}
		}
	}

	/**
	 * Gets the accelerator preference key.
	 * <p>
	 * @return the union of <code>menu_accelerator</code> key with attribute.
	 */
	protected String getAcceleratorPreferencesKey() {
		return MenuItem.MENU_ACCELERATOR + "_" + this.attribute;
	}

	@Override
	public String getFormName() {
		return this.formName;
	}

	public void setFormName(final String formName) {
		this.formName = formName;
	}

	@Override
	public boolean isDialog() {
		return this.dialog;
	}

	public void setDialog(final boolean dialog) {
		this.dialog = dialog;
	}

	// public boolean isModal() {
	// return modal;
	// }

	// public void setModal(boolean modal) {
	// this.modal = modal;
	// }

	@Override
	public String getFormManagerName() {
		return this.formManagerName;
	}

	@Override
	public boolean isDynamic() {
		return this.dynamic;
	}

	public void setDynamic(final boolean dynamic) {
		this.dynamic = dynamic;
	}

	public static DataFlavor APPTOOLBARBUTTON_FLAVOR;

	static {
		try {
			APPTOOLBARBUTTON_FLAVOR = new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ApToolBarButton.class.getName() + "\"");
		} catch (final ClassNotFoundException e) {
			logger.error(null, e);
		}
	}

	protected ApToolBarButton appToolBarButtonInstance;

	protected ApToolBarButton createAppToolBarButton() {
		if (appToolBarButtonInstance == null) {
			final Map params = new Hashtable();
			params.put("attr", this.attribute);
			params.put("action", this.getActionCommand());

			if (iconPath != null) {
				params.put("icon", this.iconPath);
			} else {
				params.put("icon", ImageManager.IMAGE_24);
			}

			params.put("formmanager", this.formManagerName);
			params.put("showdialog", "" + this.dialog);

			appToolBarButtonInstance = new ApToolBarButton(params);
			appToolBarButtonInstance.setResourceBundle(this.bundle);
			final ToolBarListener toolBarListener = ApplicationManager.getApplication().getToolBarListener();

			if (toolBarListener instanceof ActionListener) {
				appToolBarButtonInstance.addActionListener((ActionListener) toolBarListener);
			}
		}
		return appToolBarButtonInstance;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { APPTOOLBARBUTTON_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return true;
	}

	@Override
	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (APPTOOLBARBUTTON_FLAVOR.equals(flavor)) {
			return createAppToolBarButton();
		}
		return this;
	}

	@Override
	public void dragEnter(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dragExit(final DragSourceEvent dse) {
	}

	@Override
	public void dragDropEnd(final DragSourceDropEvent dsde) {
		this.setEnabled(this.dragEnabled);
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent dge) {
		this.dragEnabled = this.isEnabled();
		this.setEnabled(false);
		this.source.startDrag(dge, DragSource.DefaultCopyDrop, this, this);
	}

}
