package com.ontimize.gui.container;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
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
import com.ontimize.gui.CustomRowGridBagLayout;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.border.ColorTitleBorder;
import com.ontimize.util.swing.layout.AbsoluteLayout;

/**
 * This class creates a panel and places the components in form of a <code>row</code>.
 * <p>
 *
 * @author Imatia Innovation
 */
public class Row extends JPanel
implements FormComponent, Internationalization, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Row.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String ROW = "Row";

	protected TexturePaint texturePaint;

	/**
	 * The reference to the title key. By default, null.
	 */
	protected String titleKey = null;

	/**
	 * The reference to the title border.
	 */
	protected TitledBorder titledBorder;

	protected boolean titleToUpperCase;

	/**
	 * The condition of vertical expansion. By default, false.
	 */
	protected boolean verticalExpand = false;

	protected Double verticalWeight;

	/**
	 * The border style.
	 */
	protected int borderStyle = -1; // EtchedBorder.RAISED;

	/**
	 * The key for lowered style.
	 */
	public static final String LOWERED = "lowered";

	/**
	 * The key for bevel lowered style.
	 */
	public static final String BEVEL_LOWERED = "bevellowered";

	/**
	 * The key for bevel raised style.
	 */
	public static final String BEVEL_RAISED = "bevelraised";

	/** Color property. */
	public static final String COLOR = "color";

	/** Visible property **/
	public static final String VISIBLE = "visible";

	/**
	 * The lowered value.
	 */
	public static final int B_LOWERED = 0;

	/**
	 * The bevel raised value.
	 */
	public static final int B_RAISED = 1;

	/**
	 * The bevel lowered value.
	 */
	public static final int B_BEVEL_LOWERED = 2;

	/**
	 * The bevel raised value.
	 */
	public static final int B_BEVEL_RAISED = 3;

	/** The empty value. */
	public static final int B_COLOR = 4;

	/**
	 * The border position.
	 */
	protected int borderPosition = TitledBorder.DEFAULT_POSITION;

	/**
	 * The key for title position
	 */
	public static final String ABOVE_TOP = "abovetop";

	/**
	 * The key for title position
	 */
	public static final String TOP = "top";

	/**
	 * The key for title position
	 */
	public static final String BELOW_TOP = "belowtop";

	/**
	 * The key for title position
	 */
	public static final String ABOVE_BOTTOM = "abovebottom";

	/**
	 * The key for title position
	 */
	public static final String BOTTOM = "bottom";

	/**
	 * The key for title position
	 */
	public static final String BELOW_BOTTOM = "belowbottom";

	/**
	 * The horizontal gap when using Flow Layout.
	 */
	public static int defaultFLayoutHGap = 1;

	/**
	 * The vertical gap when using Flow Layout.
	 */
	public static int defaultFLayoutVGap = 2;

	/**
	 * The attribute reference. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The preferred height. By default, -1.
	 */
	protected int preferredHeight = -1;

	/**
	 * The preferred width. By default, -1.
	 */
	protected int preferredWidth = -1;

	/**
	 * Reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * Reference to visible form permissions. By default, null.
	 */
	protected FormPermission visiblePermissions = null;

	/**
	 * Reference to enabled form permissions. By default, null.
	 */
	protected FormPermission enabledPermissions = null;

	/**
	 * The background paint reference. By default, null.
	 */
	protected Paint backgroundPaint = null;

	/**
	 * An instance of a rectangle.
	 */
	protected Rectangle r = new Rectangle();

	/**
	 * The reference for a background image. By default, null.
	 */
	protected java.awt.Image backgroundImage = null;

	protected float opacity = 1.0f;

	/** Visibility condition **/
	protected boolean visibility;

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * Class constructor. Calls to <code>super()</code> and inits parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public Row(final Map parameters) {
		super();
		this.init(parameters);
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			if (this.verticalWeight != null) {
				return new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1,
						this.verticalWeight.doubleValue(), GridBagConstraints.NORTH, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0);
			} else if (!this.verticalExpand) {
				return new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
			} else {
				return new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
			}
		} else {
			return null;
		}
	}

	/**
	 * Inits parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 *
	 *
	 *        <p>
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
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
	 *        <td>The attribute to manage the field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>layout</td>
	 *        <td><i>flow or {@link CustomRowGridBagLayout} instance</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The field layout.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>lowered/raised/bevellowered/bevelraised</td>
	 *        <td>raised</td>
	 *        <td>no</td>
	 *        <td>The border definition.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>titleposition</td>
	 *        <td><i>abovetop/top/belowtop/abovebottom/bottom/belowbottom</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The title-position of the titled border..</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>expand</td>
	 *        <td><i>yes/no or numerical value like weight in GridBagConstraints</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The expansion.</td>
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
	 *        <td>bgpaint</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Paint value to use in this container. See
	 *        {@link ColorConstants#paintNameToPaint(String)}</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>height</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The height in pixels for component. Used in empty rows.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>width</td>
	 *        <td><i></td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The width in pixels for component. Used in empty rows.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>bgimage</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The path for a background image.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The opaque condition for row</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>margin</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The margin value for component.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>textureimage</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to source icon to extract a image and create a <code>TexturePaint</code></td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>align</td>
	 *        <td><i>right/left</td>
	 *        <td>center</td>
	 *        <td>no</td>
	 *        <td>The horizontal alignment. This parameters is only used if the layout value is
	 *        'flow'.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>visible</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>If the row is visible</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		this.setName(Row.ROW);
		this.setAttrParameter(parameters);
		this.setLayoutParameter(parameters);
		this.setBorderParameter(parameters);
		this.setPositionTitleParameter(parameters);
		final Object title = this.setTitleParameter(parameters);
		this.setUppercaseParameter(parameters);
		this.setTitleFontParameter(parameters);
		this.setTitleColorParameter(parameters);
		this.setOpacityParameter(parameters);
		this.setExpandParameter(parameters);
		this.setBackgroundColor(parameters);
		this.setBackgroundPaint(parameters);
		this.setHeightParameter(parameters);
		this.setWidthParameter(parameters);
		this.setBackgroundImage(parameters);
		this.setOpaqueParameter(parameters);
		this.setMarginParameter(parameters);
		this.setBorder(ParseUtils.getBorder((String) parameters.get("border"), this.getBorder()));
		this.setTextureImageParameter(parameters);
		this.setTitleBackgroundColorParameter(parameters, title);
		final Object oVisible = parameters.get(Row.VISIBLE);
		this.visibility = ParseUtils.getBoolean((String) oVisible, true);

	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @param title
	 */
	protected void setTitleBackgroundColorParameter(final Map parameters, final Object title) {
		final Object color = parameters.get("titlebgcolor");
		if ((color != null) && (title != null) && (this.borderStyle == Row.B_COLOR)) {
			try {
				final Color currentColor = ColorConstants.parseColor((String) color);
				if (currentColor != null) {
					this.setBackground(currentColor);
					this.setOpaque(true);
				}
			} catch (final Exception e) {
				Row.logger.error(null, e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTextureImageParameter(final Map parameters) {
		final Image im = ParseUtils.getImage((String) parameters.get("textureimage"), null);
		if (im != null) {
			final BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			bi.getGraphics().drawImage(im, 0, 0, null);
			this.texturePaint = new TexturePaint(bi, new Rectangle(bi.getWidth(null), bi.getHeight(null)));
		} else {
			this.texturePaint = null;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setMarginParameter(final Map parameters) {
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
					Row.logger.error(null, e);
				} else {
					Row.logger.trace(null, e);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setOpaqueParameter(final Map parameters) {
		final Object opaque = parameters.get("opaque");
		if (opaque != null) {
			this.setOpaque(ParseUtils.getBoolean((String) opaque, false));
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBackgroundImage(final Map parameters) {
		final Object bgimage = parameters.get("bgimage");
		if (bgimage != null) {
			final String bgi = bgimage.toString();
			try {
				final URL url = this.getClass().getClassLoader().getResource(bgi);
				if (url == null) {
					Row.logger.debug(this.getClass().toString() + ": Image not found -> " + bgi);
				} else {
					final java.awt.Image im = Toolkit.getDefaultToolkit().getImage(url);
					final MediaTracker mt = new MediaTracker(this);
					mt.addImage(im, 0);
					mt.waitForID(0);
					this.setBackgroundImage(im);
				}
			} catch (final Exception e) {
				Row.logger.error(null, e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setWidthParameter(final Map parameters) {
		final Object width = parameters.get("width");
		if (width != null) {
			try {
				this.preferredWidth = Integer.parseInt(width.toString());
			} catch (final Exception e) {
				Row.logger.error("Error parameter 'width': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHeightParameter(final Map parameters) {
		final Object height = parameters.get("height");
		if (height != null) {
			try {
				this.preferredHeight = Integer.parseInt(height.toString());
			} catch (final Exception e) {
				Row.logger.error("Error parameter 'height': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBackgroundPaint(final Map parameters) {
		final Object bgpaint = parameters.get("bgpaint");
		if (bgpaint != null) {
			final String bgp = bgpaint.toString();
			// The value must be a color name defined in ColorConstants class.
			try {
				this.setBackgroundPaint(ColorConstants.paintNameToPaint(bgp));
			} catch (final Exception e) {
				Row.logger.error(null, e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBackgroundColor(final Map parameters) {
		final Object bgcolor = parameters.get("bgcolor");
		if (bgcolor != null) {
			final String bg = bgcolor.toString();
			if (bg.indexOf(";") > 0) {
				try {
					this.setBackground(ColorConstants.colorRGBToColor(bg));
				} catch (final Exception e) {
					Row.logger.trace(null, e);
				}
			} else {
				try {
					this.setBackground(ColorConstants.parseColor(bg));
				} catch (final Exception e) {
					Row.logger.error("Error in parameter 'color':" + e.getMessage(), e);
				}

			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setExpandParameter(final Map parameters) {
		// Parameter expand
		final Object expand = parameters.get("expand");
		if (expand != null) {
			try {
				this.verticalWeight = new Double((String) parameters.get("expand"));
				parameters.remove("expand");
			} catch (final Exception e) {
				Row.logger.trace(null, e);
				this.verticalWeight = null;
				this.verticalExpand = ParseUtils.getBoolean((String) expand, this.verticalExpand);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setOpacityParameter(final Map parameters) {
		final Object opacity = parameters.get("opacity");
		if (opacity != null) {
			this.opacity = ParseUtils.getFloat(opacity.toString(), 1.0f);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTitleColorParameter(final Map parameters) {
		final Object titleColor = parameters.get("titlecolor");
		if ((this.titledBorder != null) && (titleColor != null)) {
			final Color color = ParseUtils.getColor(titleColor.toString(), null);
			if (color != null) {
				this.titledBorder.setTitleColor(color);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTitleFontParameter(final Map parameters) {
		final Object titleFont = parameters.get("titlefont");
		if ((this.titledBorder != null) && (titleFont != null)) {
			final Font font = ParseUtils.getFont(titleFont.toString(), null);
			if (font != null) {
				this.titledBorder.setTitleFont(font);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setUppercaseParameter(final Map parameters) {
		final Object toUpperCase = parameters.get("touppercase");
		if ((toUpperCase != null) && (this.titledBorder != null) && (this.titleKey != null)) {
			this.titleToUpperCase = ParseUtils.getBoolean((String) toUpperCase, false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @return
	 */
	protected Object setTitleParameter(final Map parameters) {
		// Parameter title
		final Object title = parameters.get("title");
		if (title != null) {
			this.titleKey = title.toString();
			switch (this.borderStyle) {
			case B_LOWERED:
				this.titledBorder = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			case B_RAISED:
				this.titledBorder = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			case B_BEVEL_LOWERED:
				this.titledBorder = new TitledBorder(new BevelBorder(BevelBorder.LOWERED), title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			case B_BEVEL_RAISED:
				this.titledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), title.toString());
				this.setBorder(this.titledBorder);
				this.titledBorder.setTitlePosition(this.borderPosition);
				break;
			case B_COLOR:
				this.titledBorder = new ColorTitleBorder(title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			default:
				this.titledBorder = new TitledBorder(title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				// this.setBorder(new TitledBorder(new
						// EtchedBorder(EtchedBorder.RAISED), title.toString()));
				break;
			}
		}
		return title;
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setPositionTitleParameter(final Map parameters) {
		// Position title
		final Object position = parameters.get("titleposition");
		if (position != null) {
			if (position.toString().equalsIgnoreCase(Row.TOP)) {
				this.borderPosition = TitledBorder.TOP;
			} else if (position.toString().equalsIgnoreCase(Row.ABOVE_TOP)) {
				this.borderPosition = TitledBorder.ABOVE_TOP;
			} else if (position.toString().equalsIgnoreCase(Row.BELOW_TOP)) {
				this.borderPosition = TitledBorder.BELOW_TOP;
			} else if (position.toString().equalsIgnoreCase(Row.BOTTOM)) {
				this.borderPosition = TitledBorder.BOTTOM;
			} else if (position.toString().equalsIgnoreCase(Row.ABOVE_BOTTOM)) {
				this.borderPosition = TitledBorder.ABOVE_BOTTOM;
			} else if (position.toString().equalsIgnoreCase(Row.BELOW_BOTTOM)) {
				this.borderPosition = TitledBorder.BELOW_BOTTOM;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBorderParameter(final Map parameters) {
		// Parameter border
		final Object border = parameters.get("border");
		if (border != null) {
			if (border.toString().equalsIgnoreCase(Row.LOWERED)) {
				this.borderStyle = Row.B_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Row.BEVEL_LOWERED)) {
				this.borderStyle = Row.B_BEVEL_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Row.BEVEL_RAISED)) {
				this.borderStyle = Row.B_BEVEL_RAISED;
			} else if (border.toString().equalsIgnoreCase(Row.COLOR)) {
				this.borderStyle = Column.B_COLOR;
			} else {
				this.borderStyle = -1;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLayoutParameter(final Map parameters) {
		final Object layout = parameters.get("layout");
		if (layout == null) {
			this.setLayout(new CustomRowGridBagLayout());
		} else {
			if (layout.equals("flow")) {
				final Object oAlign = parameters.get("align");
				int align = FlowLayout.CENTER;
				if (oAlign == null) {
					align = FlowLayout.CENTER;
				} else {
					if (oAlign.equals("right")) {
						align = FlowLayout.RIGHT;
					} else {
						if (oAlign.equals("left")) {
							align = FlowLayout.LEFT;
						} else {
							align = FlowLayout.CENTER;
						}
					}
				}
				// If it is a flowLayout check the align
				final FlowLayout l = new FlowLayout(align);
				l.setHgap(Row.defaultFLayoutHGap);
				l.setVgap(Row.defaultFLayoutVGap);
				this.setLayout(l);
			} else if ("absolute".equals(layout)) {
				this.setLayout(new AbsoluteLayout());
			} else {
				this.setLayout(new CustomRowGridBagLayout());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAttrParameter(final Map parameters) {
		final Object attr = parameters.get("attr");
		if (attr == null) {
			if (ApplicationManager.DEBUG) {
				Row.logger.debug(this.getClass().toString() + ": INFO: Parameter 'attr' not found");
			}
		} else {
			this.attribute = attr;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		if (this.preferredHeight != -1) {
			/* if(this.getComponentCount()==0) */d.height = this.preferredHeight;
		}
		if (this.preferredWidth != -1) {
			/* if(this.getComponentCount()==0) */d.width = this.preferredWidth;
		}

		if ((d.height == 0) && (d.width == 0) && this.isVisible()) {
			try {
				d.height = this.getFontMetrics(this.getFont()).getHeight();
			} catch (final Exception ex) {
				Row.logger.trace(null, ex);
				d.height = 8;
			}
			d.width = 1;
		}
		return d;
	}

	@Override
	public Dimension getMinimumSize() {
		final Dimension d = super.getMinimumSize();
		if (this.preferredHeight != -1) {
			/* if(this.getComponentCount()==0) */d.height = this.preferredHeight;
		}
		if (this.preferredWidth != -1) {
			/* if(this.getComponentCount()==0) */d.width = this.preferredWidth;
		}
		return d;
	}

	@Override
	public Dimension getMaximumSize() {
		final Dimension d = super.getMaximumSize();
		if (this.preferredHeight != -1) {
			/* if(this.getComponentCount()==0) */d.height = this.preferredHeight;
		}
		if (this.preferredWidth != -1) {
			/* if(this.getComponentCount()==0) */d.width = this.preferredWidth;
		}
		return d;
	}

	@Override
	public List getTextsToTranslate() {
		if (this.titleKey != null) {
			final List v = new Vector();
			v.add(this.titleKey);
			return v;
		}
		return null;
	}

	@Override
	public String getName() {
		return Row.ROW;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			if ((this.titleKey != null) && (this.titledBorder != null)) {
				if (resources != null) {
					String value = resources.getString(this.titleKey);
					if (this.titleToUpperCase && (value != null)) {
						value = value.toUpperCase();
					}
					this.titledBorder.setTitle(value);
				} else {
					this.titledBorder.setTitle(this.titleKey);
				}
				this.repaint();
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				Row.logger.debug(null, e);
			} else {
				Row.logger.trace(null, e);
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

	@Override
	public void setVisible(final boolean visible) {

		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (!permission) {
				this.visibility = false;
				return;
			}

			if (!this.visibility) {
				super.setVisible(false);
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
	 * Checks visible permission for row.
	 * <p>
	 * @return the visible condition
	 */
	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermissions == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.visiblePermissions = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.visiblePermissions != null) {
					manager.checkPermission(this.visiblePermissions);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (ApplicationManager.DEBUG_SECURITY) {
					Row.logger.debug(null, e);
				} else {
					Row.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Sets background paint and repaints.
	 * <p>
	 * @param p the paint to set
	 */
	public void setBackgroundPaint(final Paint p) {
		this.backgroundPaint = p;
		this.repaint();
	}

	/**
	 * Checks the enabled permission for row.
	 * <p>
	 * @return the condition of permissions
	 */
	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermissions == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.enabledPermissions = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.enabledPermissions != null) {
					manager.checkPermission(this.enabledPermissions);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (ApplicationManager.DEBUG_SECURITY) {
					Row.logger.debug(null, e);
				} else {
					Row.logger.trace(null, e);
				}

				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		if (this.texturePaint != null) {
			final Graphics2D g2 = (Graphics2D) g;
			final Rectangle2D r = new Rectangle2D.Float(0, 0, this.getSize().width, this.getSize().height);
			// Now fill the round rectangle.
			g2.setPaint(this.texturePaint);
			g2.fill(r);
		} else if (this.backgroundPaint != null) {
			// Insets insets = this.getInsets();
			((Graphics2D) g).setPaint(this.backgroundPaint);
			/*
			 * r.width = this.getWidth()-insets.right-insets.left; r.height =
			 * this.getHeight()-insets.top-insets.bottom;
			 */
			this.r.width = this.getWidth();
			this.r.height = this.getHeight();
			((Graphics2D) g).fill(this.r);
		} else if (this.backgroundImage != null) {
			g.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
		} else {
			super.paintComponent(g);
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
	 * Sets background image and repaints.
	 * <p>
	 * @param im the background image
	 */
	public void setBackgroundImage(final java.awt.Image im) {
		this.backgroundImage = im;
		this.repaint();
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	public float getOpacity() {
		return this.opacity;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
