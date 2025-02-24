package com.ontimize.gui.container;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a scrollable separator. For a good performance, only two components are
 * allowed.<br>
 * If orientation is vertical(horizontal), first component will be placed on top(on the left).
 * <p>
 *
 * @author Imatia Innovation
 */
public class SplitPane extends JSplitPane
implements FormComponent, IdentifiedElement, AccessForm, HasPreferenceComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(SplitPane.class);

	public static int defaultDividerSizeValue = 10;

	protected boolean initiatedPreferences = false;

	/**
	 * The orientation. By default, {@link JSplitPane#VERTICAL_SPLIT}
	 */
	protected int orientation = JSplitPane.VERTICAL_SPLIT;

	/**
	 * Indicates whether a component has just added. By default, false.
	 */
	protected boolean addedComponent = false;

	/**
	 * The attribute reference. By default, null
	 */
	protected Object attribute = null;

	/**
	 * The parent form reference. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The class constructor. Calls to JSplitPane <code>constructor</code>, inits parameters and sets
	 * orientation.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public SplitPane(final Map parameters) {
		super(JSplitPane.VERTICAL_SPLIT);
		this.init(parameters);
		if (this.orientation != JSplitPane.VERTICAL_SPLIT) {
			this.setOrientation(this.orientation);
		}
		// this.removeAll();
		this.setOneTouchExpandable(true);
		this.setFocusable(false);
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * Inits parameters:<br>
	 * <ul>
	 * <li><i>orientation:</i> Optional. The component orientation: 'h' horizontal and 'v' vertical.
	 * </ul>
	 * @param parameters
	 */
	/**
	 * Inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *
	 *        <p>
	 *
	 *
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
	 *        <td>orientation</td>
	 *        <td><i>h</i> or <i>horizontal</i> / <i>vertical</i> or <i>v</i></td>
	 *        <td>vertical</td>
	 *        <td>no</td>
	 *        <td>The orientation for component.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The attribute.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>dividersize</td>
	 *        <td>Integer</td>
	 *        <td>10</td>
	 *        <td>no</td>
	 *        <td>The size of the split panel divider</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>bgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The background color. A possible color for {@link ColorConstants} or a RGB value like:
	 *        '150;230;23'</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The opaque condition for split pane</td>
	 *        </tr>
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		final Object orientation = parameters.get("orientation");
		if ((orientation != null) && (orientation.equals("horizontal") || orientation.equals("h"))) {
			this.orientation = JSplitPane.HORIZONTAL_SPLIT;
		}

		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr;
		}
		final Object dividerSize = parameters.get("dividersize");
		if (dividerSize != null) {
			try {
				final Integer integerSizeValue = new Integer(dividerSize.toString());
				this.setDividerSize(integerSizeValue.intValue());
			} catch (final Exception e) {
				SplitPane.logger.error("Error 'dividersize' parameter: " + dividerSize.toString(), e);
			}
		} else {
			this.setDividerSize(SplitPane.defaultDividerSizeValue);
		}

		this.setBackground(ParseUtils.getColor((String) parameters.get("bgcolor"), this.getBackground()));
		this.setOpaque(ParseUtils.getBoolean((String) parameters.get("opaque"), false));
	}

	/**
	 * Adds the component in function of constraints.
	 * <p>
	 * @param c the component
	 * @param cons the constraints for component
	 */
	@Override
	public void add(final Component c, Object cons) {
		if (cons == null) {
			if (this.orientation == JSplitPane.VERTICAL_SPLIT) {
				if (!this.addedComponent) {
					cons = JSplitPane.TOP;
					if (ApplicationManager.DEBUG) {
						SplitPane.logger.debug("SplitPane: Added to the TOP");
					}
				} else {
					cons = JSplitPane.BOTTOM;
					if (ApplicationManager.DEBUG) {
						SplitPane.logger.debug("SplitPane: Added to the BOTTOM");
					}
				}
			} else {
				if (!this.addedComponent) {
					cons = JSplitPane.LEFT;
					if (ApplicationManager.DEBUG) {
						SplitPane.logger.debug("SplitPane: Added to the LEFT");
					}
				} else {
					cons = JSplitPane.RIGHT;
					if (ApplicationManager.DEBUG) {
						SplitPane.logger.debug("SplitPane: Added to the RIGHT");
					}
				}
			}
		}
		super.add(c, cons);
		if (this.leftComponent == c) {
			this.addedComponent = true;
		}
		if (!this.initiatedPreferences) {
			this.setDividerLocation(0.5d);
		}
	}

	@Override
	public void remove(final Component component) {
		if (component == this.leftComponent) {
			this.addedComponent = false;
		}
		super.remove(component);
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		// this.setDividerSize(10);
	}

	/**
	 * Inits permissions. Empty.
	 */
	@Override
	public void initPermissions() {
	}

	@Override
	public void setDividerLocation(final int l) {
		super.setDividerLocation(l);
		this.saveLocationPreference();
	}

	@Override
	public void setDividerLocation(final double proportionalLocation) {
		super.setDividerLocation(proportionalLocation);
	}

	/**
	 * Saves the location preferences.
	 */
	protected void saveLocationPreference() {
		if (this.initiatedPreferences) {
			final String preferenceKey = this.getPositionPreferenceKey();
			if (this.parentForm != null) {
				if (this.parentForm.getFormManager() != null) {
					final Application ap = this.parentForm.getFormManager().getApplication();
					try {
						if (ap.getPreferences() != null) {
							ap.getPreferences()
							.setPreference(this.getUser(), preferenceKey, this.getDividerLocation() + "");
							// if (!isContinuousLayout()){
							// ap.getPreferences().savePreferences();
							// }
						}
					} catch (final Exception e) {
						SplitPane.logger.trace(null, e);
					}
				}
			}
		}
	}

	/**
	 * The user reference. By default null.
	 */
	protected String userPrefs = null;

	/**
	 * Gets user calling to {@link Application#getReferenceLocator()} and
	 * {@link ReferenceLocator#getUser()}.
	 *
	 * <p>
	 * @return the user
	 */
	protected String getUser() {
		final Application ap = this.parentForm.getFormManager().getApplication();
		final EntityReferenceLocator locator = ap.getReferenceLocator();
		if (locator instanceof ClientReferenceLocator) {
			return ((ClientReferenceLocator) locator).getUser();
		} else {
			return this.userPrefs;
		}
	}

	/**
	 * The restricted condition.
	 * <p>
	 * @return boolean the condition of restricted value. By default, false.
	 */
	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Gets the preferred split position.
	 * <p>
	 * @return the split position
	 */
	protected String getPositionPreferenceKey() {
		final Form f = this.parentForm;
		Object at = this.attribute;
		if (at == null) {
			at = "";
		}
		return f != null ? BasicApplicationPreferences.SPLIT_POSITION + "_" + f.getArchiveName() + "_" + at
				: BasicApplicationPreferences.SPLIT_POSITION + "_" + at;
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

	/**
	 * Inits preferences for user.
	 * <p>
	 * @param ap the application preferences
	 * @param user the user
	 */
	@Override
	public void initPreferences(final ApplicationPreferences ap, final String user) {
		this.initiatedPreferences = true;
		if (ap == null) {
			return;
		}
		final String pref = ap.getPreference(user, this.getPositionPreferenceKey());
		if (pref != null) {
			try {
				this.setDividerLocation(Integer.parseInt(pref));
			} catch (final Exception ex) {
				SplitPane.logger.trace(null, ex);
			}
		}
	}

	@Override
	public void free() {
		this.parentForm = null;
	}

}
