package com.ontimize.gui.container;

import java.awt.Color;
import java.awt.Dimension;
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
import com.ontimize.gui.CustomColumnGridBagLayout;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.border.ColorTitleBorder;

/**
 * An extended JPanel with new functionalities to put the information in vertical layout grouped in
 * a column.
 * <p>
 *
 * @author Imatia Innovation
 */

public class Column extends JPanel implements FormComponent, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Column.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String COLUMN = "Column";

	public static final String VALIGN = "valign";

	public static final String EXPANDLAST = "expandlast";

	protected TexturePaint texturePaint;

	/** The column title. By default is "null". */
	protected String title = null;

	/**
	 * Indicates a special title with arbitrary border with the addition of a String title in a
	 * specified position and justification.
	 */
	protected TitledBorder titledBorder;

	protected boolean titleToUpperCase;

	/** To specify horizontal expand. By default, "true". */
	protected boolean horizontalExpand = true;

	protected Double horizontalWeight;

	/**
	 * Indicates whether the component placed in the bottom of column will be expanded and take up all
	 * remaining column space .
	 */
	protected boolean lastExpand = true;

	/** The attribute object. */
	protected Object attribute = null;

	/** The alignment. By default, TOP value. */
	protected int alignment = GridBagConstraints.NORTH;

	/** The vertical alignment. By default, TOP value. */
	protected int alignmentV = GridBagConstraints.NORTH;

	/** A reference to parent form. */
	protected Form parentForm = null;

	/** To set the visible permissions. */
	protected FormPermission visiblePermission = null;

	/** To set the active permissions. */
	protected FormPermission activedPermission = null;

	/** The border style. */
	private int borderStyle = -1;

	/** Lowered property. */
	public static final String LOWERED = "lowered";

	/** Raised property */
	public static final String RAISED = "raised";

	/** Bevel lowered property. */
	public static final String BEVEL_LOWERED = "bevellowered";

	/** Bevel raised property. */
	public static final String BEVEL_RAISED = "bevelraised";

	/** Color property. */
	public static final String COLOR = "color";

	/** The bevel lowered value. */
	public static final int B_LOWERED = 0;

	/** The bevel raised value. */
	public static final int B_RAISED = 1;

	/** The bevel lowered value. */
	public static final int B_BEVEL_LOWERED = 2;

	/** The bevel raised value. */
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

	/** Visible property **/
	public static final String VISIBLE = "visible";

	/** The preferred column width. By default, -1. */
	protected int preferredWidht = -1;

	/** The preferred column high. By default, -1. */
	protected int preferredHigh = -1;

	/** A reference to background paint. By default, null. */
	protected Paint backgroundPaint = null;

	/** A new Rectangle. */
	protected Rectangle r = new Rectangle();

	/** A reference to specify a background image in the column. */
	protected java.awt.Image backgroundImage = null;

	/** Visibility condition **/
	protected boolean visibility;

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * The class constructor. Calls to <code>super()</code> and initializes parameters.
	 * <p>
	 * @param parameters The <code>Hashtable</code> with parameters
	 */
	public Column(final Map parameters) {
		super();
		this.init(parameters);
	}

	@Override
	public String getName() {
		return Column.COLUMN;
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			if (this.horizontalWeight != null) {
				return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, this.horizontalWeight.doubleValue(),
						1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0);
			} else if (this.horizontalExpand) {
				return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
			} else {
				return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.CENTER,
						GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);
			}
		} else {
			return null;
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
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>expandlast</td>
	 *        <td><i>yes/no</td>
	 *        <td><code></code></td>
	 *        <td>no</td>
	 *        <td>Indicates whether the component placed in the bottom of column will be expanded and
	 *        take up all remaining column space.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>title</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The column title.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>lowered/raised/bevellowered/bevelraised</td>
	 *        <td>raised</td>
	 *        <td>no</td>
	 *        <td>The border definition.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>titleposition</td>
	 *        <td><i>abovetop/top/belowtop/abovebottom/bottom/belowbottom</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The title-position of the titled border..</td>
	 *        </tr>
	 *        <tr>
	 *        <td>expand</td>
	 *        <td><i>yes/no or numerical value like weight in GridBagConstraints</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates the horizontal expansion.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>valign</td>
	 *        <td><i>top/center/bottom</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the vertical alignment.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>height</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The preferred column height in pixels. Useful for specifying empty columns.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>width</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The preferred column width in pixels. Useful for specifying empty columns.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgimage</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The path to background image.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The background color. A possible color for {@link ColorConstants} or a RGB value like:
	 *        '150;230;23'</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgpaint</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Paint value to use in this container. See
	 *        {@link ColorConstants#paintNameToPaint(String)}</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>yes</td>
	 *        <td>Indicates whether column is opaque.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>margin</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the column margin.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>textureimage</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to source icon to extract a image and create a <code>TexturePaint</code></td>
	 *        </tr>
	 *        <tr>
	 *        <td>touppercase</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>To put text of title border into upper case.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>titlebgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The color of the border ColorTitleBorder. A possible color for {@link ColorConstants}
	 *        or a RGB value like: '150;230;23'</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>visible</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>If the column is visible</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		final Object expandlast = parameters.get(Column.EXPANDLAST);
		this.lastExpand = DataField.parseBoolean((String) expandlast, true);
		this.setVerticalAlign(parameters);
		final Object attr = parameters.get("attr");
		this.attribute = attr;
		this.setBorderParameter(parameters);
		this.setPositionParameter(parameters);
		final Object otitle = this.setTitle(parameters);
		this.setUppercaseParameter(parameters);
		this.setTitleFont(parameters);
		this.setTitleColor(parameters);
		this.setExpand(parameters);
		this.setHeight(parameters);
		this.setWidth(parameters);
		this.setBackgroundColor(parameters);
		this.setBackgroundPaint(parameters);
		this.setBackgroundImage(parameters);
		this.setOpaqueParameter(parameters);
		this.setMarginParameter(parameters);
		this.setBorder(ParseUtils.getBorder((String) parameters.get("border"), this.getBorder()));
		this.setTextureImage(parameters);
		this.setTitleColor(parameters, otitle);
		final Object oVisible = parameters.get(Row.VISIBLE);
		this.visibility = ParseUtils.getBoolean((String) oVisible, true);
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @param title
	 */
	protected void setTitleColor(final Map parameters, final Object title) {
		final Object color = parameters.get("titlebgcolor");
		if ((color != null) && (title != null) && (this.borderStyle == Column.B_COLOR)) {
			try {
				final Color currentColor = ColorConstants.parseColor((String) color);
				if (currentColor != null) {
					this.setBackground(currentColor);
					this.setOpaque(true);
				}
			} catch (final Exception e) {
				Column.logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTextureImage(final Map parameters) {
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
					Column.logger.error(null, e);
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
			if (opaque.toString().equalsIgnoreCase("no")) {
				this.setOpaque(false);
			} else if (opaque.toString().equalsIgnoreCase("yes")) {
				this.setOpaque(true);
			}
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
			// Only defined colors in ColorConstants
			try {
				final URL url = ImageManager.getIconURL(bgi);
				if (url == null) {
					Column.logger.debug(this.getClass().toString() + ": Image not found -> " + bgi);
				} else {
					final java.awt.Image im = Toolkit.getDefaultToolkit().getImage(url);
					final MediaTracker mt = new MediaTracker(this);
					mt.addImage(im, 0);
					mt.waitForID(0);
					this.setBackgroundImage(im);
				}
			} catch (final Exception e) {
				Column.logger.error(e.getMessage(), e);
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
			// Only defined colors in ColorConstants
			try {
				this.setBackgroundPaint(ColorConstants.paintNameToPaint(bgp));
			} catch (final Exception e) {
				Column.logger.error(e.getMessage(), e);
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
					Column.logger
					.error(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage(), e);
				}
			} else {
				try {
					this.setBackground(ColorConstants.parseColor(bg));
				} catch (final Exception e) {
					Column.logger
					.error(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setWidth(final Map parameters) {
		final Object width = parameters.get("width");
		if (width != null) {
			try {
				this.preferredWidht = Integer.parseInt(width.toString());
			} catch (final Exception e) {
				Column.logger.error(this.getClass().toString() + ": Error in parameter 'width': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHeight(final Map parameters) {
		final Object height = parameters.get("height");
		if (height != null) {
			try {
				this.preferredHigh = Integer.parseInt(height.toString());
			} catch (final Exception e) {
				Column.logger.error(this.getClass().toString() + ": Error in parameter 'height': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setExpand(final Map parameters) {
		// Expand parameter
		final Object expand = parameters.get("expand");
		if (expand != null) {
			try {
				this.horizontalWeight = new Double((String) parameters.get("expand"));
			} catch (final Exception e) {
				Column.logger.trace(null, e);
				this.horizontalWeight = null;
				this.horizontalExpand = ParseUtils.getBoolean((String) expand, true);
			}
		} else {
			this.horizontalExpand = true;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTitleColor(final Map parameters) {
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
	protected void setTitleFont(final Map parameters) {
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
		if ((toUpperCase != null) && (this.titledBorder != null) && (this.title != null)) {
			this.titleToUpperCase = ParseUtils.getBoolean((String) toUpperCase, false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @return
	 */
	protected Object setTitle(final Map parameters) {
		// Title parameter
		final Object title = parameters.get("title");
		if (title == null) {
			// There is not border with title.
		} else {
			this.title = title.toString();
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
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			case B_COLOR:
				// this.titledBorder = new TitledBorder(new EmptyBorder(new
						// Insets(2, 2, 2, 2)),title.toString());
				this.titledBorder = new ColorTitleBorder(title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			default:
				this.titledBorder = new TitledBorder(title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			}
		}
		return title;
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setPositionParameter(final Map parameters) {
		// Position title
		final Object position = parameters.get("titleposition");
		if (position != null) {
			if (position.toString().equalsIgnoreCase(Column.TOP)) {
				this.borderPosition = TitledBorder.TOP;
			} else if (position.toString().equalsIgnoreCase(Column.ABOVE_TOP)) {
				this.borderPosition = TitledBorder.ABOVE_TOP;
			} else if (position.toString().equalsIgnoreCase(Column.BELOW_TOP)) {
				this.borderPosition = TitledBorder.BELOW_TOP;
			} else if (position.toString().equalsIgnoreCase(Column.BOTTOM)) {
				this.borderPosition = TitledBorder.BOTTOM;
			} else if (position.toString().equalsIgnoreCase(Column.ABOVE_BOTTOM)) {
				this.borderPosition = TitledBorder.ABOVE_BOTTOM;
			} else if (position.toString().equalsIgnoreCase(Column.BELOW_BOTTOM)) {
				this.borderPosition = TitledBorder.BELOW_BOTTOM;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBorderParameter(final Map parameters) {
		// Border parameter
		final Object border = parameters.get("border");
		if (border != null) {
			if (border.toString().equalsIgnoreCase(Column.LOWERED)) {
				this.borderStyle = Column.B_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Column.BEVEL_LOWERED)) {
				this.borderStyle = Column.B_BEVEL_LOWERED;
			} else if (border.toString().equalsIgnoreCase(Column.BEVEL_RAISED)) {
				this.borderStyle = Column.B_BEVEL_RAISED;
			} else if (border.toString().equalsIgnoreCase(Column.COLOR)) {
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
	protected void setVerticalAlign(final Map parameters) {
		final Object valign = parameters.get(Column.VALIGN);
		if (valign == null) {
		} else {
			if (valign.equals("center")) {
				this.alignmentV = GridBagConstraints.CENTER;
			} else {
				if (valign.equals("bottom")) {
					this.alignmentV = GridBagConstraints.SOUTH;
				} else {
					this.alignmentV = GridBagConstraints.NORTH;
				}
			}
		}
		if (this.alignmentV != GridBagConstraints.NORTH) {
			this.setLayout(new CustomColumnGridBagLayout(this, this.alignmentV, this.lastExpand));
		} else {
			this.setLayout(new CustomColumnGridBagLayout(this, this.lastExpand));
		}
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		if (this.preferredHigh != -1) {
			/* if(this.getComponentCount()==0) */d.height = this.preferredHigh;
		}
		if (this.preferredWidht != -1) {
			/* if(this.getComponentCount()==0) */d.width = this.preferredWidht;
		}

		if ((d.height == 0) && (d.width == 0) && this.isVisible()) {
			try {
				d.height = this.getFontMetrics(this.getFont()).getHeight();
			} catch (final Exception ex) {
				Column.logger.trace(null, ex);
				d.height = 8;
			}
			d.width = 1;
		}

		return d;
	}

	@Override
	public Dimension getMinimumSize() {
		final Dimension d = super.getMinimumSize();
		if (this.preferredHigh != -1) {
			d.height = this.preferredHigh;
		}
		if (this.preferredWidht != -1) {
			d.width = this.preferredWidht;
		}
		return d;
	}

	@Override
	public Dimension getMaximumSize() {
		final Dimension d = super.getMaximumSize();
		if (this.preferredHigh != -1) {
			d.height = this.preferredHigh;
		}
		if (this.preferredWidht != -1) {
			d.width = this.preferredWidht;
		}
		return d;
	}

	@Override
	public List getTextsToTranslate() {
		if (this.title != null) {
			final List v = new Vector();
			v.add(this.title);
			return v;
		}
		return null;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			if ((this.title != null) && (this.titledBorder != null)) {
				if (resources != null) {
					String value = resources.getString(this.title);
					if (this.titleToUpperCase && (value != null)) {
						value = value.toUpperCase();
					}
					this.titledBorder.setTitle(value);
				} else {
					this.titledBorder.setTitle(this.title);
				}
				this.repaint();
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Column.logger.debug(this.getClass().toString() + " : " + e.getMessage(), e);
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

	/**
	 * Inits permissions.
	 *
	 * @see #checkEnabledPermission()
	 * @see #setEnabled(boolean)
	 * @see #checkVisiblePermission()
	 * @see #checkEnabledPermission()
	 */
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
	 * Checks the visibility permissions.
	 * <p>
	 * @return The visibility condition
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
					Column.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Column.logger.debug(this.getClass().toString() + ": " + e.getMessage());
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks the enable permissions.
	 * <p>
	 * @return The enable condition
	 */
	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.activedPermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.activedPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Checks to show
				if (this.activedPermission != null) {
					manager.checkPermission(this.activedPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Column.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Column.logger.debug(this.getClass().toString() + ": " + e.getMessage());
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/** The restricted condition. By default, "false". */
	protected boolean restricted = false;

	/**
	 * Checks the restricted condition
	 * <p>
	 * @return the restricted condition
	 */
	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Sets the backgroundpaint parameter and repaints the column.
	 * <p>
	 * @param p The background paint configuration
	 */
	public void setBackgroundPaint(final Paint p) {
		this.backgroundPaint = p;
		this.repaint();
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
	 * Sets a background image in column.
	 * <p>
	 *
	 * @see Image
	 * @param im The awt image to set in background image
	 */
	public void setBackgroundImage(final java.awt.Image im) {
		this.backgroundImage = im;
		this.repaint();
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
