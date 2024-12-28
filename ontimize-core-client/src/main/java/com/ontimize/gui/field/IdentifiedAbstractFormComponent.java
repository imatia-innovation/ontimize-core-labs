package com.ontimize.gui.field;

import java.awt.Component;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;

/**
 * Basic implementation for {@link AccessForm} and {@link IdentifiedElement} interfaces.
 * <p>
 *
 * @author Imatia Innovation
 */
public abstract class IdentifiedAbstractFormComponent extends AbstractFormComponent
implements IdentifiedElement, AccessForm {

	private static final Logger logger = LoggerFactory.getLogger(IdentifiedAbstractFormComponent.class);

	/**
	 * Reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * Reference to visible permission. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * Reference to enabled permission. By default, null.
	 */
	protected FormPermission enabledPermission = null;

	/**
	 * The reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * Gets the parent form.
	 * <p>
	 * @return the reference to parent form
	 */
	public Form getParentForm() {
		return this.parentForm;
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
	 *        <td>...</td>
	 *        <td>...</td>
	 *        <td>...</td>
	 *        <td>...</td>
	 *        <td>...</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) throws Exception {

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

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Checks the visible permission.
	 * <p>
	 * @return the visible permission condition
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
					IdentifiedAbstractFormComponent.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					IdentifiedAbstractFormComponent.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks enabled permission.
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
					IdentifiedAbstractFormComponent.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					IdentifiedAbstractFormComponent.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void free() {
		this.attribute = null;
		this.visiblePermission = null;
		this.enabledPermission = null;
		this.parentForm = null;
	}

}
