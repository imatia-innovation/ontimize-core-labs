package com.ontimize.gui;

import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
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
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a deployed list to use in the menu bar. It uses swing components.
 *
 * @version 1.0 01/05/2001
 */

public class Menu extends JMenu implements FormComponent, IdentifiedElement, Freeable, SecureElement, StatusComponent,
HasPreferenceComponent, IDynamicItem {

	private static final Logger logger = LoggerFactory.getLogger(Menu.class);

	public static final String MENU_ACCELERATOR = "menu_accelerator";

	protected String statusText = null;

	protected String attribute = null;

	protected String shortcut = "";

	private MenuPermission visiblePermission = null;

	private MenuPermission enabledPermission = null;

	protected boolean dynamic = false;

	public Menu(final Map parameters) {
		this.init(parameters);
		final Insets margin = this.getMargin();
		final int leftMargin = margin.left;
		final int newMargin = Math.max(0, leftMargin - 5);
		this.setMargin(new Insets(margin.top, newMargin, margin.bottom, margin.right));
		this.initPermissions();
		ApplicationManager.registerStatusComponent(this);
		this.dynamic = ParseUtils.getBoolean((String) parameters.get("dynamic"), false);
	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		return null;
	}

	/**
	 * This method initializes the menu parameters
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
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
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Attribute to identify the component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>mnemonic</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>string specifying the mnemonic value</td>
	 *        </tr>
	 *        <tr>
	 *        <td>shortcut</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Key stroke to open the menu</td>
	 *        </tr>
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

		final Object status = parameters.get("status");
		if (status == null) {
			this.statusText = this.attribute;
		} else {
			this.statusText = status.toString();
		}

		// Icon parameter
		final Object oIcon = parameters.get("icon");
		if (oIcon != null) {
			this.setHorizontalAlignment(SwingConstants.LEFT);
			final String sIconFile = oIcon.toString();
			final ImageIcon icon = ImageManager.getIcon(sIconFile);
			if (icon != null) {
				this.setIcon(icon);
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
				this.shortcut = ks.toString();
			} catch (final Exception e) {
				Menu.logger.trace(null, e);
			}
		}
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.attribute);
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			if (resources != null) {
				this.setText(resources.getString(this.attribute));
			} else {
				this.setText(this.attribute);
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Menu.logger.debug(null, e);
			} else {
				Menu.logger.trace(null, e);
			}
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void free() {
		final EventListener[] listeners = this.getListeners(Action.class);
		for (int i = 0; i < listeners.length; i++) {
			this.removeActionListener((ActionListener) listeners[i]);
		}
		final MenuElement[] menuChildren = this.getSubElements();
		// Free the menu components
		for (int i = 0; i < menuChildren.length; i++) {
			if (menuChildren[i] instanceof Freeable) {
				try {
					((Freeable) menuChildren[i]).free();
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						Menu.logger.debug("Exception in free() method " + menuChildren[i].getClass().toString(), e);
					} else {
						Menu.logger.trace("Exception in free() method " + menuChildren[i].getClass().toString(), e);
					}
				}
			}
		}
		// Now removes the children
		this.removeAll();
		if (ApplicationManager.DEBUG) {
			Menu.logger.debug(this.getClass().toString() + " Free");
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
					Menu.logger.debug(null, e);
				} else {
					Menu.logger.trace(null, e);
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
					Menu.logger.debug(null, e);
				} else {
					Menu.logger.trace(null, e);
				}
			}
		} else {
			super.setEnabled(enabled);
		}
	}

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
					Menu.logger.debug(null, e);
				} else {
					Menu.logger.trace(null, e);
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
					Menu.logger.debug(null, e);
				} else {
					Menu.logger.trace(null, e);
				}
			}
		}
	}

	@Override
	public String getStatusText() {
		return this.statusText;
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void initPreferences(final ApplicationPreferences aPrefs, final String user) {
		// KeyStroke
		if (aPrefs != null) {
			final String pref = aPrefs.getPreference(user, this.getAcceleratorPreferenceKey());
			if (pref != null) {
				final String prefs[] = pref.split(" ");
				final KeyStroke ks = KeyStroke.getKeyStroke(Integer.parseInt(prefs[1]), Integer.parseInt(prefs[0]));
				// KeyStroke ks = KeyStroke.getKeyStroke(pref);
				if (ks != null) {
					super.setAccelerator(ks);
				}
			}
		}
	}

	protected String getAcceleratorPreferenceKey() {
		return Menu.MENU_ACCELERATOR + "_" + this.attribute;
	}

	@Override
	public boolean isDynamic() {
		return this.dynamic;
	}

	public void setDynamic(final boolean dynamic) {
		this.dynamic = dynamic;
	}

}
