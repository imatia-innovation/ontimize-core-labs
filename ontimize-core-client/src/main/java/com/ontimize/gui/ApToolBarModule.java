package com.ontimize.gui;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a button bar for application.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ApToolBarModule extends JComponent implements FormComponent, IdentifiedElement, SecureElement, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(ApToolBarModule.class);

	/**
	 * The reference to attribute. By default, null.
	 */
	protected String attribute = null;

	protected String moduleName;

	protected MenuPermission visiblePermission = null;

	/**
	 * The class constructor. Inits parameters and permissions.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public ApToolBarModule(final Map parameters) {
		this.init(parameters);
		this.initPermissions();
	}

	@Override
	public Dimension getMinimumSize() {
		return this.getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, 0);
	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		return null;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {

	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector(0);
		return v;
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Initializes parameters and selects the bundle equals to class {@link ResourceBundle}, adding a
	 * suffix to basic file name.
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
	 *        <td>The attribute for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>name</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The module name</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {

		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr.toString();
		} else {
			this.attribute = "aptoolbarmodule";
		}

		this.moduleName = ParseUtils.getString((String) parameters.get("name"), null);
		this.setOpaque(false);
	}

	@Override
	public void setVisible(final boolean vis) {
		if (ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS) {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				if (this.visiblePermission == null) {
					this.visiblePermission = new MenuPermission("visible", this.attribute, true);
				}
				try {
					// Check to show
					if (vis) {
						manager.checkPermission(this.visiblePermission);
					}
					super.setVisible(vis);
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG_SECURITY) {
						ApToolBarModule.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
					}
				}
			} else {
				super.setVisible(vis);
			}
		} else {
			super.setVisible(vis);
		}
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		if (ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS) {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (this.visiblePermission == null) {
				this.visiblePermission = new MenuPermission("visible", this.attribute, true);
			}
			try {
				manager.checkPermission(this.visiblePermission);
			} catch (final Exception e) {
				super.setVisible(false);
				if (ApplicationManager.DEBUG_SECURITY) {
					ApToolBarModule.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
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

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
