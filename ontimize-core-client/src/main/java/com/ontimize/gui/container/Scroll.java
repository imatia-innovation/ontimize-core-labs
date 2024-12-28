package com.ontimize.gui.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * This class provides a scrollable view of a lightweight component.
 * <p>
 *
 * @author Imatia Innovation
 */

public class Scroll extends JScrollPane implements FormComponent, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Scroll.class);

	/**
	 * A reference for a column. By default, null.
	 */
	protected Column contain = null;

	/**
	 * A reference to the parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * A reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	protected int preferredWidth;

	protected int preferredHeight;

	/**
	 * The class constructor. Calls to <code>super</code> and inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 */
	public Scroll(final Map parameters) {
		super();
		this.init(parameters);
		parameters.remove("attr");
		this.contain = new Column(parameters);
		this.getViewport().add(this.contain, null);
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

	@Override
	public Dimension getPreferredSize() {
		if ((this.preferredHeight > 0) && (this.preferredWidth > 0)) {
			return new Dimension(this.preferredWidth, this.preferredHeight);
		}
		return super.getPreferredSize();
	}

	/**
	 * Inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
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
	 *        <td>preferredsize</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The preferred size for the Scroll component width format width;height</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether the scroll component is opaque.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover,
	 *        it is also allowed a border defined in #BorderManager</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The border for scroll.</td>
	 *        </tr>
	 *        </Table>
	 */

	@Override
	public void init(final Map parameters) {
		this.attribute = parameters.get("attr");
		final Object oSize = parameters.get("preferredsize");
		if (oSize != null) {
			final Dimension parseSize = ApplicationManager.parseSize(oSize.toString());
			this.setPreferredSize(parseSize);
		}

		if (ApplicationManager.useOntimizePlaf) {
			this.setOpaque(false);
			this.getViewport().setOpaque(false);
		}

		final Object oOpaque = parameters.get("opaque");
		if ((oOpaque != null) && !ApplicationManager.parseStringValue(parameters.get("opaque").toString())) {
			this.setOpaque(false);
			this.getViewport().setOpaque(false);
		}
		this.setBorder(ParseUtils.getBorder((String) parameters.get("border"), this.getBorder()));

		this.preferredWidth = ParseUtils.getInteger((String) parameters.get("preferredwidth"), -1);
		this.preferredHeight = ParseUtils.getInteger((String) parameters.get("preferredheight"), -1);
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void add(final Component c, final Object constraints) {
		if ((this.contain != null) && (c instanceof FormComponent)) {
			this.contain.add(c, constraints);
		} else {
			super.add(c, constraints);
		}
	}

	@Override
	public Component add(final Component c) {
		if ((this.contain != null) && (c instanceof FormComponent)) {
			return this.contain.add(c);
		} else {
			return super.add(c);
		}
	}

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * The visible permission. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * The enabled permission. By default, null.
	 */
	protected FormPermission enabledPermission = null;

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

	/**
	 * Checks the visible permission.
	 * <p>
	 * @return the visible permission condition
	 */
	protected boolean checkVisiblePermission() {
		if (this.attribute == null) {
			return true;
		}
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if (this.parentForm != null) {
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
					Scroll.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Scroll.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				this.setEnabled(true);
			} else {
				this.setEnabled(false);
			}
		} else {
			super.setEnabled(enabled);
		}
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (permission) {
				super.setVisible(visible);
			}
		} else {
			super.setVisible(visible);
		}
	}

	/**
	 * Checks the enabled permission.
	 * <p>
	 * @return the enabled permission condition
	 */
	protected boolean checkEnabledPermission() {
		if (this.attribute == null) {
			return true;
		}
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if (this.parentForm != null) {
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
					Scroll.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Scroll.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

	public Column getContain() {
		return this.contain;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
