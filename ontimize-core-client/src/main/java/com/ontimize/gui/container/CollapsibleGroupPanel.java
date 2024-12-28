package com.ontimize.gui.container;

import java.awt.Color;
import java.awt.Component;
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

import javax.swing.BorderFactory;
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
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.JCollapsibleGroupPanel;

/**
 * This class implements a component that lets the user storing different elements inside it.
 * Between these elements it can be stores <code>CollapsiblGroup</code>
 * <p>
 *
 * @author Imatia Innovation
 */
public class CollapsibleGroupPanel extends JCollapsibleGroupPanel
implements FormComponent, ReferenceComponent, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(CollapsibleGroupPanel.class);

	/**
	 * Attribute to define background color of the Collapsible Group.
	 */
	public static String BGCOLOR = "bgcolor";

	/**
	 * Attribute to define background paint of the Collapsible Group.
	 */
	public static String BGPAINT = "bgpaint";

	/**
	 * Attribute to define background image of the Collapsible Group.
	 */
	public static String BGIMAGE = "bgimage";

	/**
	 * Attribute to define background texture image of the Collapsible Group.
	 */
	public static String TEXTUREIMAGE = "textureimage";

	/**
	 * A reference to background paint. By default, null.
	 */
	protected Paint backgroundPaint = null;

	/**
	 * A reference to specify a background image in the column.
	 */
	protected java.awt.Image backgroundImage = null;

	/**
	 * A reference to specify a texture paint.
	 */
	protected TexturePaint texturePaint;

	/**
	 * The reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The title for the Collapsible Group Panel border. By default is "null".
	 */
	protected String title = null;

	/**
	 * Indicates a special title with arbitrary border with the addition of a String title in a
	 * specified position and justification.
	 */
	protected TitledBorder titledBorder;

	/**
	 * The border position.
	 */
	protected int borderPosition = TitledBorder.DEFAULT_POSITION;

	/**
	 * The reference to parent form for component. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The reference to locator. By default, null.
	 */
	protected EntityReferenceLocator locator = null;

	/**
	 * The reference for visible permission. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	/** The border style. */
	private int borderStyle = -1;

	protected ResourceBundle resources = null;

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * The class constructor. Inits parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public CollapsibleGroupPanel(final Map parameters) {
		super(parameters);
		this.init(parameters);
	}

	/**
	 * Inits the parameters.
	 * <p>
	 * @param parameters the Map with parameters
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
	 *        <td>The attribute for Collapsible Group Panel.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Specifies if the component must be opaque or not.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>anim</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Sets the animation enabled/disabled</td>
	 *        </tr>
	 *        <tr>
	 *        <td>startshowed</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Specifies whether the Collapsible Group Panel starts collpased or not.If true all
	 *        CollapsibleGroups contained on it are forced to be collpased, if not each Collpasible
	 *        Group takes its own 'startshowed' parameter.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>onlyonedeployed</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Specifies if just one Collapsible Group can be opened or it can be opened more than
	 *        one. By default it is allowed to deploy more than one Collpasible Group.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>time</td>
	 *        <td></td>
	 *        <td>200</td>
	 *        <td>no</td>
	 *        <td>Sets deploying/undeploying duration in milliseconds</td>
	 *        </tr>
	 *        <tr>
	 *        <tr>
	 *        <td>margin</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the column margin.</td>
	 *        </tr>
	 *        <td>title</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The Collapsible Group Panel title.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>none/lowered/raised/bevellowered/bevelraised</td>
	 *        <td>none</td>
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
	 *        <td>bgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The background color. A possible color for {@link ColorConstants} or a RGB value like:
	 *        '150;230;23'</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgimage</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The path to background image.</td>
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
	 *        <td>textureimage</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to source icon to extract a image and create a <code>TexturePaint</code></td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		this.setAttributte(parameters);
		this.setOpaque(parameters);
		this.setDeployedQty(parameters);
		this.setDeployTime(parameters);
		this.setBorderParameter(parameters);
		this.setTitlePosition(parameters);
		this.setTitle(parameters);
		this.setMargin(parameters);
		this.setBackgroundColor(parameters);
		this.setBackgroundImage(parameters);
		this.setBackgroundPaint(parameters);
		this.setTexturePaint(parameters);
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTexturePaint(final Map parameters) {
		final Image im = ParseUtils.getImage((String) parameters.get(CollapsibleGroupPanel.TEXTUREIMAGE), null);
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
	protected void setBackgroundPaint(final Map parameters) {
		final Object bgpaint = parameters.get(CollapsibleGroupPanel.BGPAINT);
		if (bgpaint != null) {
			final String bgp = bgpaint.toString();
			// Only defined colors in ColorConstants
			try {
				this.setBackgroundPaint(ColorConstants.paintNameToPaint(bgp));
			} catch (final Exception e) {
				CollapsibleGroupPanel.logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBackgroundImage(final Map parameters) {
		final Object obgimage = parameters.get(CollapsibleGroupPanel.BGIMAGE);
		if (obgimage != null) {
			final String imgbg = obgimage.toString();
			final Image bgimg = this.getImage(imgbg);
			if (bgimg != null) {
				this.setBackgroundImage(bgimg);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBackgroundColor(final Map parameters) {
		final Object obgcolor = parameters.get(CollapsibleGroupPanel.BGCOLOR);
		if (obgcolor != null) {
			final String bg = obgcolor.toString();
			final Color bgcolor = this.getColor(bg, CollapsibleGroupPanel.BGCOLOR);
			if (bgcolor != null) {
				this.setBackground(bgcolor);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setMargin(final Map parameters) {
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
					CollapsibleGroupPanel.logger.error(null, e);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTitle(final Map parameters) {
		final Object title = parameters.get("title");
		if (title == null) {
			// There is not border with title.
		} else {
			this.title = title.toString();
			switch (this.borderStyle) {
			case Column.B_LOWERED:
				this.titledBorder = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			case Column.B_RAISED:
				this.titledBorder = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			case Column.B_BEVEL_LOWERED:
				this.titledBorder = new TitledBorder(new BevelBorder(BevelBorder.LOWERED), title.toString());
				this.titledBorder.setTitlePosition(this.borderPosition);
				this.setBorder(this.titledBorder);
				break;
			case Column.B_BEVEL_RAISED:
				this.titledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), title.toString());
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
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTitlePosition(final Map parameters) {
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
		final Object border = parameters.get("border");
		if (border != null) {
			if (border.toString().equalsIgnoreCase(DataField.NONE)) {
				this.borderStyle = -1;
				this.setBorder(null);
			} else if (border.toString().equalsIgnoreCase(Column.LOWERED)) {
				this.borderStyle = Column.B_LOWERED;
				this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			} else if (border.toString().equalsIgnoreCase(Column.RAISED)) {
				this.borderStyle = Column.B_RAISED;
				this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.toString().equalsIgnoreCase(Column.BEVEL_LOWERED)) {
				this.borderStyle = Column.B_BEVEL_LOWERED;
				this.setBorder(BorderFactory.createLoweredBevelBorder());
			} else if (border.toString().equalsIgnoreCase(Column.BEVEL_RAISED)) {
				this.borderStyle = Column.B_BEVEL_RAISED;
				this.setBorder(BorderFactory.createRaisedBevelBorder());
			} else {
				this.borderStyle = -1;
				final Border b = ParseUtils.getBorder(border.toString(), (Border) null);
				if (b != null) {
					this.setBorder(b);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setDeployTime(final Map parameters) {
		final Object time = parameters.get("time");
		if (time != null) {
			try {
				this.deployTime = Integer.parseInt(time.toString());
			} catch (final Exception e) {
				CollapsibleGroupPanel.logger
				.error(this.getClass().toString() + ": Error in parameter 'time': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setDeployedQty(final Map parameters) {
		final Object oOnlyOneDeployed = parameters.get("onlyonedeployed");
		if ((oOnlyOneDeployed != null)
				&& ApplicationManager.parseStringValue(parameters.get("onlyonedeployed").toString())) {
			this.onlyonedeployed = true;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setOpaque(final Map parameters) {
		final Object oOpaque = parameters.get("opaque");
		if ((oOpaque != null) && !ApplicationManager.parseStringValue(parameters.get("opaque").toString())) {
			this.setOpaque(false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAttributte(final Map parameters) {
		final Object attr = parameters.get("attr");
		if (attr == null) {
			if (CollapsibleGroupPanel.logger.isDebugEnabled()) {
				CollapsibleGroupPanel.logger.debug(this.getClass().toString() + " : Parameter 'attr' not found");
			}
		} else {
			this.attribute = attr;
		}
	}

	/**
	 * This method returns a Color from the specified String with the color.
	 *
	 * String with the value of the color.
	 * @param strColor
	 * @param parameterName Parameter name for which is the specified color. In case of bad parsing, an
	 *        error is printed with the parameter name.
	 * @return a <code>Color</code>.
	 */
	protected Color getColor(final String strColor, final String parameterName) {
		if (strColor.indexOf(";") > 0) {
			try {
				return ColorConstants.colorRGBToColor(strColor);
			} catch (final Exception e) {
				CollapsibleGroupPanel.logger.error(
						this.getClass().toString() + ": Error in parameter '" + parameterName + "': " + e.getMessage(),
						e);
			}
		} else {
			try {
				return ColorConstants.parseColor(strColor);
			} catch (final Exception e) {
				CollapsibleGroupPanel.logger.error(
						this.getClass().toString() + ": Error in parameter '" + parameterName + "': " + e.getMessage(),
						e);
			}
		}
		return null;
	}

	/**
	 * This method returns an Image from the specified path of the image.
	 * @param imageURL The path of the image.
	 * @return an <code>Image</code>.
	 */
	protected Image getImage(final String imageURL) {
		try {
			final URL url = this.getClass().getClassLoader().getResource(imageURL);
			if (url == null) {
				CollapsibleGroupPanel.logger.debug(this.getClass().toString() + ": Image not found -> " + imageURL);
			} else {
				final java.awt.Image im = Toolkit.getDefaultToolkit().getImage(url);
				final MediaTracker mt = new MediaTracker(this);
				mt.addImage(im, 0);
				mt.waitForID(0);
				return im;
			}
		} catch (final Exception e) {
			CollapsibleGroupPanel.logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * This method creates the filler panel that is placed under all components of the Collpasible Group
	 * Panel.
	 */
	@Override
	protected JPanel createFillerComponent() {
		final JPanel filler = new JPanel() {

			@Override
			protected void paintComponent(final Graphics g) {
				if (CollapsibleGroupPanel.this.texturePaint != null) {
					final Graphics2D g2 = (Graphics2D) g;
					final Rectangle2D r = new Rectangle2D.Float(0, 0, this.getSize().width, this.getSize().height);
					// Now fill the round rectangle.
					g2.setPaint(CollapsibleGroupPanel.this.texturePaint);
					g2.fill(r);

				} else if (CollapsibleGroupPanel.this.backgroundPaint != null) {
					((Graphics2D) g).setPaint(CollapsibleGroupPanel.this.backgroundPaint);
					final Rectangle r = new Rectangle();
					r.width = this.getWidth();
					r.height = this.getHeight();
					((Graphics2D) g).fill(r);
				} else if (CollapsibleGroupPanel.this.backgroundImage != null) {
					g.drawImage(CollapsibleGroupPanel.this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(),
							this);
				} else {
					super.paintComponent(g);
				}
			}
		};
		filler.setOpaque(true);
		return filler;
	}

	/**
	 * Sets the background paint parameter and repaints the Collapsible Group Panel.
	 * <p>
	 * @param p The background paint configuration
	 */
	public void setBackgroundPaint(final Paint p) {
		this.backgroundPaint = p;
		this.repaint();
	}

	/**
	 * Sets a background image in Collapsible Group Panel.
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
	public Object getAttribute() {
		return this.attribute;
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
	public void setResourceBundle(final ResourceBundle resource) {
		this.resources = resource;
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
	public void add(final Component c, final Object constraints) {
		if (c instanceof JCollapsibleGroup) {
			this.addGroupPanel((JCollapsibleGroup) c);
		} else if (c instanceof FormComponent) {
			super.add(c, constraints);
		}
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
	}

	/**
	 * Checks the visible permission.
	 * <p>
	 * @return the condition about visibility permission
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
					CollapsibleGroupPanel.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					CollapsibleGroupPanel.logger.debug(this.getClass().toString() + ": " + e.getMessage());
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator b) {
		this.locator = b;
	}

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
