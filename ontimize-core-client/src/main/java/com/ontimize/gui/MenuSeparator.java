package com.ontimize.gui;

import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.UIDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;

/**
 * This class implements a separator between menu elements.
 * <p>
 *
 * @author Imatia Innovation
 */
public class MenuSeparator extends JSeparator implements FormComponent, Freeable, IdentifiedElement {

	private static final Logger logger = LoggerFactory.getLogger(MenuSeparator.class);

	/**
	 * The reference for attribute. By default, null.
	 */
	protected Object attribute = null;

	private MenuPermission visiblePermission = null;

	private MenuPermission enabledPermission = null;

	/**
	 * The class constructor. Calls to super() and initializes parameters.
	 * <p>
	 * @param parameters
	 */
	public MenuSeparator(final Map parameters) {
		super();
		this.init(parameters);
		this.initPermissions();
	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		return null;
	}

	/**
	 * Inits parameters.
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
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The attribute to manage the separator.</td>
	 *        </tr>
	 *        <Table>
	 */

	@Override
	public void init(final Map parameters) {
		this.attribute = parameters.get("attr");
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void free() {
		this.removeAll();
		if (com.ontimize.gui.ApplicationManager.DEBUG) {
			MenuSeparator.logger.debug(" Free");
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
				this.enabledPermission = new MenuPermission("enabled", (String) this.attribute, true);
			}
			try {
				manager.checkPermission(this.enabledPermission);
				this.restricted = false;
			} catch (final Exception e) {
				this.restricted = true;
				super.setEnabled(false);

				if (ApplicationManager.DEBUG_SECURITY) {
					MenuSeparator.logger.debug(null, e);
				}
			}
			if (this.visiblePermission == null) {
				this.visiblePermission = new MenuPermission("visible", (String) this.attribute, true);
			}
			try {
				manager.checkPermission(this.visiblePermission);
			} catch (final Exception e) {
				super.setVisible(false);
				if (ApplicationManager.DEBUG_SECURITY) {
					MenuSeparator.logger.debug(null, e);
				}
			}
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

	/**
	 * Returns the name of the L&F class that renders this component.
	 * @return the string "PopupMenuSeparatorUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	@Override
	public String getUIClassID() {
		return "PopupMenuSeparatorUI";

	}

}
