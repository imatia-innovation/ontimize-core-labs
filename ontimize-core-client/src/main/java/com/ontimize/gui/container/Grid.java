package com.ontimize.gui.container;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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
 * This class implements a basic grid contained in a panel.
 * <p>
 *
 * @author Imatia Innovation
 */
public class Grid extends JPanel implements FormComponent, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Grid.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String GRID = "Grid";

	/**
	 * The lowered key.
	 */
	public static final String LOWERED = "lowered";

	/**
	 * The bevel-lowered key.
	 */
	public static final String BEVEL_LOWERED = "bevellowered";

	/**
	 * The bevel-raised key.
	 * @deprecated Use {@link #BEVEL_RAISED}
	 */
	@Deprecated
	public static final String BEVEL_REAISED = "bevelraised";

	/**
	 * The bevel-raised key.
	 */
	public static final String BEVEL_RAISED = "bevelraised";

	/**
	 * The lowered value. By default, 0.
	 */
	public static final int B_LOWERED = 0;

	/**
	 * The raised value. By default, 0.
	 */
	public static final int B_RAISED = 1;

	/**
	 * The bevel-lowered value. By default, 2.
	 */
	public static final int B_BEVEL_LOWERED = 2;

	/**
	 * The bevel-raised value. By default, 2.
	 */
	public static final int B_BEBEL_RAISED = 3;

	/**
	 * The reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The reference to visible permission. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * The reference to enabled permission. By default, null.
	 */
	protected FormPermission enabledPermission = null;

	/**
	 * The reference to title. By default, null.
	 */
	protected String title = null;

	protected int borderStyle = EtchedBorder.RAISED;

	public static boolean defaultOpaque = true;

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * The class constructor. Calls to <code>super()</code> and inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public Grid(final Map parameters) {
		super();
		this.init(parameters);
	}

	@Override
	public String getName() {
		return Grid.GRID;
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
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The attribute.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>margin</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The grid margin.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>rows</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The number of rows.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>columns</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The number of rows.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>hgap</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The horizontal gap.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>vgap</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The vertical gap.</td> </tr
	 *
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>lowered/raised/bevellowered/bevelraised</td>
	 *        <td>raised</td>
	 *        <td>no</td>
	 *        <td>The border definition. Border is set only when parameter 'title' is present (in other
	 *        case border will be raised).</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>title</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The title. If title does not exist, border will be RAISED.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		final Object attr = parameters.get("attr");
		this.attribute = attr;

		final Object margin = parameters.get("margin");
		if (margin != null) {
			try {
				final Insets m = ApplicationManager.parseInsets((String) margin);
				final Border b = this.getBorder();
				if (b != null) {
					this.setBorder(new CompoundBorder(b, new EmptyBorder(m)));
				} else {
					this.setBorder(new EmptyBorder(m));
				}
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					Grid.logger.error(null, e);
				}
			}
		}

		final Object rows = parameters.get("rows");
		if (rows == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " parameter 'rows' not found");
		}
		final int r = Integer.parseInt(rows.toString());

		final Object columns = parameters.get("columns");
		if (columns == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " parameter 'columns' not found");
		}
		final int c = Integer.parseInt(columns.toString());

		this.setLayout(new GridLayout(r, c));

		final Object hgap = parameters.get("hgap");
		if (hgap != null) {
			((GridLayout) this.getLayout()).setHgap(Integer.parseInt(hgap.toString()));
		}

		final Object vgap = parameters.get("vgap");
		if (vgap != null) {
			((GridLayout) this.getLayout()).setVgap(Integer.parseInt(vgap.toString()));
		}

		// Parameter border
		final Object border = parameters.get("border");
		if (border != null) {
			if (border.toString().equalsIgnoreCase(Grid.LOWERED)) {
				this.borderStyle = Grid.B_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Grid.BEVEL_LOWERED)) {
				this.borderStyle = Grid.B_BEVEL_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Grid.BEVEL_RAISED)) {
				this.borderStyle = Grid.B_BEBEL_RAISED;
			} else {
				this.borderStyle = -1;
			}
		}

		// Parameter title
		final Object title = parameters.get("title");
		if (title == null) {
			// There is not title border
		} else {
			this.title = title.toString();
			switch (this.borderStyle) {
			case B_LOWERED:
				this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title.toString()));
				break;
			case B_RAISED:
				this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), title.toString()));
				break;
			case B_BEVEL_LOWERED:
				this.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED), title.toString()));
				break;
			case B_BEBEL_RAISED:
				this.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), title.toString()));
				break;
			default:
				this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), title.toString()));
				break;
			}
		}

		final Object bgcolor = parameters.get("bgcolor");
		if (bgcolor != null) {
			final String bg = bgcolor.toString();
			if (bg.indexOf(";") > 0) {
				try {
					this.setBackground(ColorConstants.colorRGBToColor(bg));
				} catch (final Exception e) {
					Grid.logger.error(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage(),
							e);
				}
			} else {
				try {
					this.setBackground(ColorConstants.parseColor(bg));
				} catch (final Exception e) {
					Grid.logger.error(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage(),
							e);
				}
			}
		}

		this.setOpaque(ParseUtils.getBoolean((String) parameters.get("opaque"), Grid.defaultOpaque));
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			if (this.title != null) {
				final TitledBorder border = (TitledBorder) this.getBorder();
				if (resources != null) {
					border.setTitle(resources.getString(this.title));
				} else {
					border.setTitle(this.title);
				}
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Grid.logger.debug(this.getClass().toString() + " : " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	protected static class InnerGridPanel extends JPanel {

		protected Component innerComponent = null;

		public InnerGridPanel(final Component c) {
			super(new GridBagLayout());
			this.innerComponent = c;
			this.setOpaque(false);
			if (c instanceof FormComponent) {
				this.add(c, ((FormComponent) c).getConstraints(this.getLayout()));
			} else {
				this.add(c);
			}
		}

		public Component getInnerComponent() {
			return this.innerComponent;
		}

		@Override
		public void remove(final Component component) {
			if (this.getParent() instanceof Grid) {
				this.getParent().remove(this);
			}
			super.remove(component);
		}

	}

	@Override
	public void add(final Component c, final Object constraints) {
		final JPanel auxPanel = new InnerGridPanel(c);
		super.add(auxPanel, constraints);
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (!permission) {
				return;
			}
		}
		super.setVisible(visible);
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
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Checks to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Grid.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Grid.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks the enabled permission.
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
					Grid.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Grid.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
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
