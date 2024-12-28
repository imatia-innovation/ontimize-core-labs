package com.ontimize.gui.container;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.CustomColumnGridBagLayout;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ParseUtils;

public class CollapsiblePanel extends JPanel
implements FormComponent, IdentifiedElement, AccessForm, HasPreferenceComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(CollapsiblePanel.class);

	/**
	 * The key for orientation
	 */
	public static final String ORIENTATION = "orientation";

	public static final String REVERSE_ICON = "reverseicon";

	public static final String HORIZONTAL_ORIENTATION_VALUE = "horizontal";

	public static final String VERTICAL_ORIENTATION_VALUE = "vertical";

	public static final int HORIZONTAL_ORIENTATION = 0;

	public static final int VERTICAL_ORIENTATION = 1;

	public static String borderClass;

	protected int orientation = CollapsiblePanel.VERTICAL_ORIENTATION;

	protected JPanel innerComponent;

	protected JViewport viewPort = null;

	protected boolean deployedState = false;

	protected boolean expandHorizontal = true;

	protected boolean expandVertical = false;

	protected boolean expandLast = true;

	protected int verticalAlignment = GridBagConstraints.NORTH;

	protected Object attribute;

	protected int borderStyle = EtchedBorder.RAISED;

	protected String title;

	protected int deployTime = 200;

	protected boolean animated = true;

	protected ResourceBundle bundle = null;

	protected Timer timer = null;

	public int customHeight = -1;

	public int customWidth = -1;

	public int minHeight = -1;

	public int minWidth = -1;

	protected String tiptext = "";

	protected boolean doFirstShow;

	protected boolean firstTime;

	protected String baseTooltip;

	protected boolean reverseIcons = false;

	protected boolean initiatedPreferences = false;

	protected Form parentForm;

	public CollapsiblePanel(final Map parameters) {
		super();

		this.innerComponent = new JPanel();
		this.innerComponent.setOpaque(false);
		this.viewPort = new JViewport();
		this.viewPort.setView(this.innerComponent);
		super.setLayout(new BorderLayout());
		super.addImpl(this.viewPort, BorderLayout.CENTER, -1);

		if (this.deployTime < 0) {
			this.deployTime = 0;
		}

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (CollapsiblePanel.this.getBorder() instanceof IDeployBorder) {
						final IDeployBorder border = (IDeployBorder) CollapsiblePanel.this.getBorder();
						if (border.getImageBound() == null) {
							return;
						}
						if (border.getImageBound().contains(e.getPoint())) {
							CollapsiblePanel.this.doActionDeploy(CollapsiblePanel.this.animated);
						}
					}
				}
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				if (CollapsiblePanel.this.getBorder() instanceof IDeployBorder) {
					final IDeployBorder border = (IDeployBorder) CollapsiblePanel.this.getBorder();
					if (border.getImageBound() == null) {
						return;
					}
					if (border.getImageBound().contains(e.getPoint())) {
						border.setHighlight(true);
					}
					CollapsiblePanel.this.repaint();
					super.mouseEntered(e);
				}
			}

			@Override
			public void mouseExited(final MouseEvent mouseevent) {
				if (CollapsiblePanel.this.getBorder() instanceof IDeployBorder) {
					final IDeployBorder border = (IDeployBorder) CollapsiblePanel.this.getBorder();
					border.setHighlight(false);
					CollapsiblePanel.this.repaint();
					super.mouseExited(mouseevent);
				}
			}

		});

		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(final MouseEvent e) {
				if (CollapsiblePanel.this.getBorder() instanceof IDeployBorder) {
					final IDeployBorder border = (IDeployBorder) CollapsiblePanel.this.getBorder();
					if (border.getImageBound() == null) {
						return;
					}
					if (border.getImageBound().contains(e.getPoint())) {
						border.setHighlight(true);
					} else {
						border.setHighlight(false);
					}
					CollapsiblePanel.this.repaint();
					if (CollapsiblePanel.this.getParent().getBounds().contains(e.getPoint())
							&& (CollapsiblePanel.this.tiptext.length() > 0)) {
						CollapsiblePanel.this.setToolTipText(CollapsiblePanel.this.tiptext);
					} else {
						CollapsiblePanel.this.setToolTipText(null);
					}
				}
			}
		});

		this.init(parameters);

		final ActionListener target = new ActionListener() {

			protected boolean start = false;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final long currentTime = System.currentTimeMillis();
				long totalTime = currentTime - CollapsiblePanel.this.cycleStart;
				if ((totalTime > CollapsiblePanel.this.deployTime) && !this.start) {
					CollapsiblePanel.this.cycleStart = currentTime;
					totalTime = 0;
					this.start = true;
				}

				float fraction = (float) totalTime / CollapsiblePanel.this.deployTime;
				fraction = Math.min(1.0f, fraction);
				if (CollapsiblePanel.this.isVerticalOrientation()) {
					CollapsiblePanel.this.calculatedCustomHeight(fraction, true);
				} else {
					CollapsiblePanel.this.calculatedCustomWidth(fraction, true);
				}
				if (Float.compare(fraction, 1) == 0) {
					CollapsiblePanel.this.timer.stop();
					this.start = false;
				}
			}
		};

		if (this.animated) {
			this.timer = new Timer(35, target);
			this.timer.setInitialDelay(0);
		}
	}

	protected long cycleStart = 0;

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		if (this.firstTime) {
			if (!this.doFirstShow) {
				this.deployedState = true;
				this.doActionDeploy(false);
				this.doFirstShow = true;
			} else {
				this.doActionDeploy(false);
			}
			this.firstTime = false;
		}
	}

	protected void calculatedCustomHeight(final float fraction, final boolean animated) {
		if (animated) {
			if (this.deployedState) {
				this.customHeight = (int) (this.viewPort.getView().getPreferredSize().height * fraction)
						+ this.getInsets().top + this.getInsets().bottom;
			} else {
				this.customHeight = (int) (this.viewPort.getView().getPreferredSize().height * (1 - fraction))
						+ this.getInsets().top;
				this.minHeight = this.customHeight;
			}
		} else {
			if (this.deployedState) {
				this.customHeight = this.viewPort.getView().getPreferredSize().height + this.getInsets().top
						+ this.getInsets().bottom;
			} else {
				this.customHeight = this.getInsets().top;
				this.minHeight = this.customHeight;
			}
		}

		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.customHeight);
		this.validate();
	}

	protected void calculatedCustomWidth(final float fraction, final boolean animated) {
		if (animated) {
			if (this.deployedState) {
				this.customWidth = (int) (this.viewPort.getView().getPreferredSize().width * fraction)
						+ this.getInsets().left + this.getInsets().right;
			} else {
				this.customWidth = (int) (this.viewPort.getView().getPreferredSize().width * (1 - fraction))
						+ this.getInsets().left;
				this.minWidth = this.customWidth + 10;
			}
		} else {
			if (this.deployedState) {
				this.customWidth = this.viewPort.getView().getPreferredSize().width + this.getInsets().left
						+ this.getInsets().right;
			} else {
				this.customWidth = this.getInsets().left
						+ (this.orientation == CollapsiblePanel.VERTICAL_ORIENTATION ? 10 : 0);
				this.minWidth = this.customWidth;
			}
		}
		this.customHeight = this.viewPort.getView().getPreferredSize().height + this.getInsets().top
				+ this.getInsets().bottom;
		this.setBounds(this.getX(), this.getY(), this.customWidth, this.customHeight);
		this.validate();
	}

	@Override
	public void validate() {
		// super.validate();
		Container parent = SwingUtilities.getAncestorOfClass(CollapsiblePanel.class, this);

		if (parent == null) {
			parent = this.getParent();
		} else {
			parent = parent.getParent();
		}

		if (parent != null) {
			final LayoutManager manager = parent.getLayout();

			if (!this.isVerticalOrientation()) {
				if (manager instanceof GridBagLayout) {
					final GridBagConstraints currentConstraints = ((GridBagLayout) manager).getConstraints(this);

					if (this.deployedState && this.expandHorizontal) {
						currentConstraints.weightx = 1;
					} else {
						currentConstraints.weightx = 0;
					}
					((GridBagLayout) manager).setConstraints(this, currentConstraints);
				}
			} else {
				if (manager instanceof GridBagLayout) {
					final GridBagConstraints currentConstraints = ((GridBagLayout) manager).getConstraints(this);

					if (this.deployedState && this.expandVertical) {
						currentConstraints.weighty = 1;
					} else {
						currentConstraints.weighty = 0;
					}
					((GridBagLayout) manager).setConstraints(this, currentConstraints);
				}
			}
			if (parent instanceof JComponent) {
				((JComponent) parent).revalidate();
			} else {
				parent.invalidate();
			}
			parent.doLayout();
			parent.repaint();
		}
	}

	public void doActionDeploy(final boolean animate) {
		if (animate) {
			if (this.timer != null) {
				this.timer.stop();
				this.setDeploy(!this.isDeploy());
				this.timer.start();
			}
		} else {
			this.setDeploy(!this.isDeploy());
			if (this.isVerticalOrientation()) {
				this.calculatedCustomHeight(0, false);
			} else {
				this.calculatedCustomWidth(0, false);
			}
		}
		this.saveVisiblePreference();

		if (!this.firstTime) {
			this.firePropertyChange("deploy", !this.isDeploy(), this.isDeploy());
		}
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
	 *        <td>orientation</td>
	 *        <td><i>horizontal</i> / <i>vertical</i></td>
	 *        <td>vertical</td>
	 *        <td>no</td>
	 *        <td>The orientation for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The attribute.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>title</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The title for container.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>anim</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Sets the animation enabled/disabled</td>
	 *        </tr>
	 *        <tr>
	 *        <td>border</td>
	 *        <td>raised;lowered;bevellowered;bevelraised</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Border for container</td>
	 *        </tr>
	 *        <tr>
	 *        <td>borderclass</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Border class used to create the border for container. See
	 *        {@link #createCustomBorder(String, Hashtable)}</td>
	 *        </tr>
	 *        <tr>
	 *        <td>icon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to icon showed in container</td>
	 *        </tr>
	 *        <tr>
	 *        <td>time</td>
	 *        <td></td>
	 *        <td>200</td>
	 *        <td>no</td>
	 *        <td>Sets deploying/undeploying duration in milliseconds</td>
	 *        </tr>
	 *        <tr>
	 *        <td>startshowed</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Sets the deploying initial status</td>
	 *        </tr>
	 *        <tr>
	 *        <td>reverseicon</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Changes the order when the icons are shown</td>
	 *        </tr>
	 *        <tr>
	 *        <td>valign</td>
	 *        <td>center/bottom/top</td>
	 *        <td>top</td>
	 *        <td>no</td>
	 *        <td>The vertical alignment for component</td>
	 *        </tr>
	 *        <tr>
	 *        <td>tooltip</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Specifies the tip for container.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>expandlast</td>
	 *        <td>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates whether the component placed in the bottom of this panel will be expanded
	 *        and take up all remaining column space.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>expand</td>
	 *        <td>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the expansion for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>expandvertical</td>
	 *        <td>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the expansion for component vertically.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The background color. A possible color for {@link ColorConstants} or a RGB value like:
	 *        '150;230;23'.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The opacity condition</td>
	 *        </tr>
	 *        <tr>
	 *        <td>margin</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Fixes insets for component. Its structure must be 4 values for insets separated by
	 *        ';'(top;left;bottom;right).</td>
	 *        </tr>
	 *        </table>
	 */

	@Override
	public void init(final Map parameters) {
		this.firstTime = true;
		final Object expandlast = parameters.get("expandlast");
		this.expandLast = DataField.parseBoolean((String) expandlast, true);
		this.doFirstShow = ParseUtils.getBoolean((String) parameters.get("startshowed"), true);

		final Object valin = parameters.get("valign");
		if (valin != null) {
			if (valin.equals("center")) {
				this.verticalAlignment = GridBagConstraints.CENTER;
			} else {
				if (valin.equals("bottom")) {
					this.verticalAlignment = GridBagConstraints.SOUTH;
				} else {
					this.verticalAlignment = GridBagConstraints.NORTH;
				}
			}
		}

		if (this.verticalAlignment != GridBagConstraints.NORTH) {
			this.innerComponent.setLayout(new CustomColumnGridBagLayout(this, this.verticalAlignment, this.expandLast));
		} else {
			this.innerComponent.setLayout(new CustomColumnGridBagLayout(this, this.expandLast));
		}

		final Object attr = parameters.get("attr");
		this.attribute = attr;

		final Object orient = parameters.get(CollapsiblePanel.ORIENTATION);
		if (orient != null) {
			if (CollapsiblePanel.HORIZONTAL_ORIENTATION_VALUE.equals(orient)) {
				this.orientation = CollapsiblePanel.HORIZONTAL_ORIENTATION;
			}
		}

		// Border parameter
		final Object border = parameters.get("border");
		if (border != null) {
			if (border.toString().equalsIgnoreCase(Column.LOWERED)) {
				this.borderStyle = Column.B_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Column.BEVEL_LOWERED)) {
				this.borderStyle = Column.B_BEVEL_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Column.BEVEL_RAISED)) {
				this.borderStyle = Column.B_BEVEL_RAISED;
			} else {
				this.borderStyle = Column.B_RAISED;
			}
		}

		this.baseTooltip = (String) parameters.get("tooltip");
		if (this.baseTooltip != null) {
			this.tiptext = this.baseTooltip;
		}

		final Object reverse = parameters.get(CollapsiblePanel.REVERSE_ICON);
		if (reverse != null) {
			this.reverseIcons = ApplicationManager.parseStringValue(reverse.toString());
		}

		// Title parameter
		this.title = (String) parameters.get("title");

		final Object oBorderclass = parameters.get("borderclass");
		if ((oBorderclass != null) && !"".equals(oBorderclass)) {
			this.createCustomBorder((String) oBorderclass, parameters);
		} else if ((CollapsiblePanel.borderClass != null) && !"".equals(CollapsiblePanel.borderClass)) {
			this.createCustomBorder(CollapsiblePanel.borderClass, parameters);
		} else if (this.title != null) {
			this.createDeployBorder(this.title);
		} else {
			this.createDeployBorder("");
		}

		// Parameter expand
		final Object expand = parameters.get("expand");
		if ((expand != null) && expand.toString().equalsIgnoreCase("no")) {
			this.expandHorizontal = false;
		}

		this.expandVertical = ParseUtils.getBoolean((String) parameters.get("expandvertical"), false);

		final Object bgcolor = parameters.get("bgcolor");
		if (bgcolor != null) {
			final String bg = bgcolor.toString();
			if (bg.indexOf(";") > 0) {
				try {
					this.setBackground(ColorConstants.colorRGBToColor(bg));
				} catch (final Exception e) {
					CollapsiblePanel.logger.error("Error in parameter 'bgcolor':", e);
				}
			} else {
				try {
					this.setBackground(ColorConstants.parseColor(bg));
				} catch (final Exception e) {
					CollapsiblePanel.logger.error("Error in parameter 'bgcolor':", e);
				}
			}
		}

		final Object time = parameters.get("time");
		if (time != null) {
			try {
				this.deployTime = Integer.parseInt(time.toString());
			} catch (final Exception e) {
				CollapsiblePanel.logger.error("Error in parameter 'time':", e);
			}
		}

		final Object anim = parameters.get("anim");
		if (anim == null) {
			this.animated = false;
		} else {
			if (anim.toString().equalsIgnoreCase("no")) {
				this.animated = false;
			} else {
				this.animated = true;
			}
		}

		final boolean opaque = ParseUtils.getBoolean((String) parameters.get("opaque"), false);
		this.setOpaque(opaque);

		final Object margin = parameters.get("margin");
		if (margin != null) {
			try {
				final Insets m = ApplicationManager.parseInsets((String) margin);
				final Border b = this.getBorder();
				if ((b != null) && (b instanceof IDeployBorder)) {
					((IDeployBorder) b).setMargin(m);
				}
			} catch (final Exception e) {
				CollapsiblePanel.logger.debug(null, e);
			}
		}

		// deployedState = !doFirstShow;
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.bundle = resources;
		if (this.baseTooltip != null) {
			try {
				this.tiptext = ApplicationManager.getTranslation(this.baseTooltip, resources);
			} catch (final Exception e) {
				CollapsiblePanel.logger.debug("Translation not found for key {}", this.baseTooltip, e);
			}
		}
		if ((this.getBorder() != null) && (this.getBorder() instanceof IDeployBorder)) {
			((IDeployBorder) this.getBorder()).setTitle(ApplicationManager.getTranslation(this.title, resources));
		}
	}

	public boolean isDeploy() {
		return this.deployedState;
	}

	@Override
	public void setBorder(final Border border) {
		if ((border != null) && (border instanceof IDeployBorder)) {
			super.setBorder(border);
		} else if ((border != null) && (border instanceof TitledBorder)) {
			final TitledBorder current = (TitledBorder) border;
			super.setBorder(new DeployBorder(current.getBorder(), current.getTitle()));
		} else {
			super.setBorder(border);
		}
	}

	public void setDeploy(final boolean deploy) {
		this.deployedState = deploy;
	}

	public static boolean computeIntersection(final Rectangle dest, final int rx, final int ry, final int rw, final int rh) {
		final int x1 = Math.max(rx, dest.x);
		final int x2 = Math.min(rx + rw, dest.x + dest.width);
		final int y1 = Math.max(ry, dest.y);
		final int y2 = Math.min(ry + rh, dest.y + dest.height);
		dest.x = x1;
		dest.y = y1;
		dest.width = x2 - x1;
		dest.height = y2 - y1;

		if ((dest.width <= 0) || (dest.height <= 0)) {
			return false;
		}
		return true;
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			if (this.expandHorizontal) {
				return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
			} else {
				return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);
			}
		} else {
			return null;
		}
	}

	protected void createDeployBorder(final String title) {
		this.title = title;
		switch (this.borderStyle) {
		case Column.B_LOWERED:
			this.setBorder(new DeployBorder(new EtchedBorder(EtchedBorder.LOWERED), this.title, this.orientation,
					this.reverseIcons));
			break;
		case Column.B_RAISED:
			this.setBorder(new DeployBorder(new EtchedBorder(EtchedBorder.RAISED), this.title, this.orientation,
					this.reverseIcons));
			break;
		case Column.B_BEVEL_LOWERED:
			this.setBorder(new DeployBorder(new BevelBorder(BevelBorder.LOWERED), this.title, this.orientation,
					this.reverseIcons));
			break;
		case Column.B_BEVEL_RAISED:
			this.setBorder(new DeployBorder(new BevelBorder(BevelBorder.RAISED), this.title, this.orientation,
					this.reverseIcons));
			break;
		default:
			this.setBorder(new DeployBorder(new EtchedBorder(EtchedBorder.RAISED), this.title, this.orientation,
					this.reverseIcons));
			break;
		}
	}

	/**
	 * Creates by reflection a specific border.
	 * @param borderClassName The border class to do <code>Class.forName</code> about it
	 * @param parameters Parameters specified in .xml definition that are necessary for border
	 */
	protected void createCustomBorder(final String borderClassName, final Map parameters) {
		Object borderInstance = null;
		try {
			final Class renderClass = Class.forName(borderClassName);
			Constructor constructor = null;
			try {
				constructor = renderClass.getConstructor(new Class[] { Hashtable.class });
				borderInstance = constructor.newInstance(new Object[] { parameters });
			} catch (final Exception e) {
				CollapsiblePanel.logger.debug(null, e);
				try {
					constructor = renderClass.getConstructor(new Class[] {});
					borderInstance = constructor.newInstance(new Object[] {});
				} catch (final Exception e1) {
					CollapsiblePanel.logger
					.error("ERROR. Unable to create custom border. Using a default border instead of custom.", e1);
					this.setBorder(new DeployBorder(new EtchedBorder(EtchedBorder.RAISED), this.title, this.orientation,
							this.reverseIcons));
				}
			}
		} catch (final Exception e2) {
			CollapsiblePanel.logger
			.error("ERROR. Unable to create custom border. Using a default border instead of custom. ", e2);
			this.setBorder(new DeployBorder(new EtchedBorder(EtchedBorder.RAISED), this.title, this.orientation,
					this.reverseIcons));
		}
		if ((borderInstance != null) && (borderInstance instanceof Border)) {
			this.setBorder((Border) borderInstance);
		}
	}

	public void setTitle(final String title) {
		if (this.getBorder() instanceof IDeployBorder) {
			((IDeployBorder) this.getBorder()).setTitle(title);
		} else {
			this.createDeployBorder(title);
		}
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void initPermissions() {

	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public void setLayout(final LayoutManager mgr) {
		if (this.innerComponent != null) {
			this.innerComponent.setLayout(mgr);
		}
	}

	@Override
	protected void addImpl(final Component comp, final Object constraints, final int index) {
		if (this.innerComponent != null) {
			this.innerComponent.add(comp, constraints, index);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension dim = this.viewPort.getView().getPreferredSize();
		if (this.isVerticalOrientation()) {
			if (this.customHeight != -1) {
				dim.height = this.customHeight;
			} else if (this.deployedState) {
				dim.height = 0;
			}
		} else {
			if (this.customWidth != -1) {
				dim.width = this.customWidth;
				dim.height = this.customHeight;
			} else if (this.deployedState) {
				dim.width = 0;
			}
		}
		return dim;
	}

	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}

	protected boolean isVerticalOrientation() {
		if (this.orientation == CollapsiblePanel.VERTICAL_ORIENTATION) {
			return true;
		}
		return false;
	}

	@Override
	public void setPreferredSize(final Dimension preferredSize) {
		((JComponent) this.viewPort.getView()).setPreferredSize(preferredSize);
	}

	@Override
	public LayoutManager getLayout() {
		if (this.innerComponent != null) {
			return this.innerComponent.getLayout();
		}
		return null;
	}

	@Override
	public void setOpaque(final boolean isOpaque) {
		super.setOpaque(isOpaque);
		if (this.innerComponent != null) {
			this.innerComponent.setOpaque(isOpaque);
		}
		if (this.viewPort != null) {
			this.viewPort.setOpaque(isOpaque);
		}
	}

	/**
	 * Saves the visible preferences.
	 */
	protected void saveVisiblePreference() {
		if (this.initiatedPreferences) {
			final String p = this.getVisiblePreferenceKey();
			if (this.parentForm != null) {
				if (this.parentForm.getFormManager() != null) {
					final Application ap = this.parentForm.getFormManager().getApplication();
					try {
						if (ap.getPreferences() != null) {
							ap.getPreferences().setPreference(this.getUser(), p, this.isDeploy() + "");
							// ap.getPreferences().savePreferences();
						}
					} catch (final Exception e) {
						CollapsiblePanel.logger.debug("Unable to save visible preferences:", e);
					}
				}
			}
		}
	}

	/**
	 * Gets the preferred split position.
	 * <p>
	 * @return the split position
	 */
	protected String getVisiblePreferenceKey() {
		final Form f = this.parentForm;
		Object at = this.attribute;
		if (at == null) {
			at = "";
		}
		return f != null ? BasicApplicationPreferences.COLLAPSIBLE_PANEL_VISIBLE + "_" + f
				.getArchiveName() + "_" + at : BasicApplicationPreferences.COLLAPSIBLE_PANEL_VISIBLE + "_" + at;
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

	@Override
	public void initPreferences(final ApplicationPreferences ap, final String user) {
		this.initiatedPreferences = true;
		if (ap == null) {
			return;
		}
		final String pref = ap.getPreference(user, this.getVisiblePreferenceKey());
		if (pref != null) {
			try {
				final boolean deploy = Boolean.valueOf(pref).booleanValue();
				this.doFirstShow = deploy;
			} catch (final Exception ex) {
				CollapsiblePanel.logger.debug("Unable to initialize preferences", ex);
			}
		}
	}

	/**
	 * @return true if the first time the component must be expanded and false if it must be shown
	 *         collapsed
	 */
	public boolean isFirstShow() {
		return this.doFirstShow;
	}

	/**
	 * Configure how the component must appear the first time (expanded or collapsed)
	 * @param doFirstShow
	 */
	public void setFirstShow(final boolean doFirstShow) {
		this.doFirstShow = doFirstShow;
	}

	/**
	 * @return true if the panel has not be painted yet
	 */
	public boolean isFirstTime() {
		return this.firstTime;
	}

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
			return null;
		}
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
