package com.ontimize.gui.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;

/**
 * An extension of {@link MatteBorder} border that uses parameters like insets, title, color,
 * upicon, downicon, recttitlecolor, fontcolor, fontshadowcolor and font to create a configurable
 * border.
 *
 * @author Imatia Innovation
 */
public class CurveMattedDeployableBorder extends MatteBorder implements IDeployBorder {

	public static final String INSETS = "insets";

	public static int LEFT_TO_ICON_MARGIN = 11;

	public static int UP_TO_ICON_MARGIN = 11;

	public static int ICON_TO_TEXT_MARGIN = 14;

	public static int TEXT_TO_END_MARGIN = 57;

	public static int CURVE_WIDTH = 34;

	public static String ARROW_UP_ICON_PATH = ImageManager.ARROW_UP;

	public static String ARROW_DOWN_ICON_PATH = ImageManager.ARROW_DOWN;

	public static String ARROW_LEFT_ICON_PATH = ImageManager.ARROW_LEFT;

	public static String ARROW_RIGHT_ICON_PATH = ImageManager.ARROW_RIGHT;

	protected ImageIcon downIcon;

	protected ImageIcon upIcon;

	protected ImageIcon rightIcon;

	protected ImageIcon leftIcon;

	protected Rectangle imageBound;

	protected boolean highlight;

	protected String title;

	protected Color rectTitleColor;

	public static Color defaultRectTitleColor = new Color(0xb6b6b6);

	protected Color highlightColor;

	public static Color defaultHighLightColor = new Color(0x2a3c48);

	public static Color defaultSolidBandColor = new Color(0xeef4f6);

	protected Color solidBandColor = CurveMattedDeployableBorder.defaultSolidBandColor;

	public static Color defaultGradientBandInitColor = new Color(0xdae8ec);

	protected Color gradientBandInitColor = CurveMattedDeployableBorder.defaultGradientBandInitColor;

	public static Color defaultGradientBandEndColor = new Color(0xe3ecf0);

	protected Color gradientBandEndColor = CurveMattedDeployableBorder.defaultGradientBandEndColor;

	protected Color fontColor;

	public static Color defaultFontColor = new Color(0xe1e1e1);

	protected Color fontShadowColor;

	public static Color defaultFontShadowColor = new Color(0x22303a);

	protected Font font;

	public static Font defaultFont = Font.decode("Arial-BOLD-12");

	public int orientation = CollapsiblePanel.VERTICAL_ORIENTATION;

	public static int defaultVerticalBorderPosition = SwingConstants.TOP;

	public static int defaultHorizontalBorderPosition = SwingConstants.LEFT;

	public int borderPosition;

	protected Insets margin = null;

	public CurveMattedDeployableBorder(final Map parameters) throws Exception {
		super(parameters.containsKey(CurveMattedDeployableBorder.INSETS) ? ApplicationManager
				.parseInsets((String) parameters.get(CurveMattedDeployableBorder.INSETS)) : new Insets(22, 0, 0, 0),
				parameters.containsKey("color") ? ColorConstants.parseColor((String) parameters.get("color"))
						: new Color(0xdbdbdb));
		this.init(parameters);
	}

	protected void init(final Map parameters) throws Exception {
		this.title = ParseUtils.getString((String) parameters.get("title"), "");

		final String orient = (String) parameters.get(CollapsiblePanel.ORIENTATION);
		if (orient != null) {
			if (CollapsiblePanel.HORIZONTAL_ORIENTATION_VALUE.equals(orient)) {
				this.orientation = CollapsiblePanel.HORIZONTAL_ORIENTATION;
			}
		}

		final String sPosition = (String) parameters.get("borderposition");
		if (sPosition != null) {
			if ("top".equals(sPosition)) {
				this.borderPosition = SwingConstants.TOP;
			} else if ("bottom".equals(sPosition)) {
				this.borderPosition = SwingConstants.BOTTOM;
			} else if ("right".equals(sPosition)) {
				this.borderPosition = SwingConstants.RIGHT;
			} else if ("left".equals(sPosition)) {
				this.borderPosition = SwingConstants.LEFT;
			} else {
				if (this.orientation == CollapsiblePanel.VERTICAL_ORIENTATION) {
					this.borderPosition = CurveMattedDeployableBorder.defaultVerticalBorderPosition;
				} else {
					this.borderPosition = CurveMattedDeployableBorder.defaultHorizontalBorderPosition;
				}
			}
		} else {
			if (this.orientation == CollapsiblePanel.VERTICAL_ORIENTATION) {
				this.borderPosition = CurveMattedDeployableBorder.defaultVerticalBorderPosition;
			} else {
				this.borderPosition = CurveMattedDeployableBorder.defaultHorizontalBorderPosition;
			}
		}

		if ((this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION)
				&& !parameters.containsKey(CurveMattedDeployableBorder.INSETS)) {
			// if (this.borderPosition == SwingConstants.LEFT) {
			this.top = 0;
			this.left = 21;
			this.bottom = 0;
			this.right = 0;
			// }
			// else if( this.borderPosition == SwingConstants.RIGHT){
			// this.top = 0;
			// this.left = 0;
			// this.bottom = 0;
			// this.right = 21;
			// }
		}

		if (this.orientation == CollapsiblePanel.VERTICAL_ORIENTATION) {
			final String sUpIcon = (String) parameters.get("upicon");
			if (sUpIcon != null) {
				this.upIcon = ImageManager.getIcon(sUpIcon);
			} else {
				this.upIcon = ImageManager.getIcon(CurveMattedDeployableBorder.ARROW_UP_ICON_PATH);
			}
			final String sDownIcon = (String) parameters.get("downicon");
			if (sDownIcon != null) {
				this.downIcon = ImageManager.getIcon(sDownIcon);
			} else {
				this.downIcon = ImageManager.getIcon(CurveMattedDeployableBorder.ARROW_DOWN_ICON_PATH);
			}
			if ((this.upIcon != null) && (this.downIcon != null)) {
				this.imageBound = new Rectangle(CurveMattedDeployableBorder.LEFT_TO_ICON_MARGIN,
						(this.top - this.upIcon.getIconHeight()) / 2, this.upIcon.getIconWidth(),
						this.upIcon.getIconHeight());
			} else {
				throw new Exception("upicon and downicon are mandatory");
			}
		} else {
			final String sLeftIcon = (String) parameters.get("lefticon");
			if (sLeftIcon != null) {
				this.leftIcon = ImageManager.getIcon(sLeftIcon);
			} else {
				this.leftIcon = ImageManager.getIcon(CurveMattedDeployableBorder.ARROW_LEFT_ICON_PATH);
			}
			final String sRightIcon = (String) parameters.get("righticon");
			if (sRightIcon != null) {
				this.rightIcon = ImageManager.getIcon(sRightIcon);
			} else {
				this.rightIcon = ImageManager.getIcon(CurveMattedDeployableBorder.ARROW_RIGHT_ICON_PATH);
			}
			this.imageBound = new Rectangle((this.left / 2) - (this.leftIcon.getIconWidth() / 2),
					CurveMattedDeployableBorder.UP_TO_ICON_MARGIN, this.leftIcon.getIconWidth(),
					this.leftIcon.getIconHeight());
		}

		final String recTitleColor = (String) parameters.get("recttitlecolor");
		if (recTitleColor != null) {
			this.rectTitleColor = ColorConstants.parseColor(recTitleColor);
		} else {
			this.rectTitleColor = CurveMattedDeployableBorder.defaultRectTitleColor;
		}

		final String sHighlightColor = (String) parameters.get("highlightcolor");
		this.highlightColor = ParseUtils.getColor(sHighlightColor, this.color);

		final String sFontColor = (String) parameters.get("fontcolor");
		if (sFontColor != null) {
			this.fontColor = ColorConstants.parseColor(sFontColor);
		} else {
			this.fontColor = CurveMattedDeployableBorder.defaultFontColor;
		}

		final String sFontColorShadow = (String) parameters.get("fontshadowcolor");
		if (sFontColorShadow != null) {
			this.fontShadowColor = ColorConstants.parseColor(sFontColorShadow);
		} else {
			this.fontShadowColor = CurveMattedDeployableBorder.defaultFontShadowColor;
		}

		final String sFont = (String) parameters.get("font");
		if (sFont != null) {
			this.font = Font.decode(sFont);
		} else {
			this.font = CurveMattedDeployableBorder.defaultFont;
		}

	}

	@Override
	public Rectangle getImageBound() {
		return this.imageBound;
	}

	@Override
	public void setHighlight(final boolean highlight) {
		this.highlight = highlight;
	}

	@Override
	public void setTitle(final String title) {
		this.title = title == null ? "" : title;
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
		super.paintBorder(c, g, x, y, width, height);
		g.setFont(this.font);
		if (this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION) {
			this.paintHorizontalOrientation(c, g, x, y, width, height);
		} else {
			this.paintVerticalOrientation(c, g, x, y, width, height);
		}

	}

	protected void paintHorizontalOrientation(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
		this.drawBaseTitleBackground(g, x, y, this.getMeasureForPosition(this.borderPosition), height);
		int markWidth = g.getFontMetrics().stringWidth(this.title);
		markWidth += CurveMattedDeployableBorder.LEFT_TO_ICON_MARGIN + this.rightIcon
				.getIconWidth() + CurveMattedDeployableBorder.ICON_TO_TEXT_MARGIN
				+ CurveMattedDeployableBorder.TEXT_TO_END_MARGIN;
		this.drawTitleBackground(g, x, y, this.getMeasureForPosition(this.borderPosition), markWidth);
		if ((c != null) && (c instanceof CollapsiblePanel)) {
			final CollapsiblePanel panel = (CollapsiblePanel) c;
			if (this.highlight) {
				if (panel.isDeploy()) {
					final ImageIcon brighterIcon = ImageManager.brighter(this.leftIcon);
					brighterIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				} else {
					final ImageIcon brighterIcon = ImageManager.brighter(this.rightIcon);
					brighterIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				}
			} else {
				if (panel.isDeploy()) {
					this.leftIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				} else {
					this.rightIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				}
			}
		}
		this.drawTitle(g, x, y, this.getMeasureForPosition(this.borderPosition), markWidth);
	}

	protected int getMeasureForPosition(final int position) {
		if (this.borderPosition == SwingConstants.TOP) {
			return this.top;
		} else if (this.borderPosition == SwingConstants.BOTTOM) {
			// TODO change when the border was painted into bottom position...
			return this.top;
		} else if (this.borderPosition == SwingConstants.LEFT) {
			return this.left;
		} else if (this.borderPosition == SwingConstants.RIGHT) {
			// TODO Change when border was painted into right position...
			return this.left;
		}
		return this.top;
	}

	protected void paintVerticalOrientation(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
		this.drawBaseTitleBackground(g, x, y, width, this.getMeasureForPosition(this.borderPosition));
		int markWidth = g.getFontMetrics().stringWidth(this.title);
		markWidth += CurveMattedDeployableBorder.LEFT_TO_ICON_MARGIN + this.upIcon
				.getIconWidth() + CurveMattedDeployableBorder.ICON_TO_TEXT_MARGIN
				+ CurveMattedDeployableBorder.TEXT_TO_END_MARGIN;
		this.drawTitleBackground(g, x, y, markWidth, this.top);
		if ((c != null) && (c instanceof CollapsiblePanel)) {
			final CollapsiblePanel panel = (CollapsiblePanel) c;
			if (this.highlight) {
				if (panel.isDeploy()) {
					final ImageIcon brighterIcon = ImageManager.brighter(this.upIcon);
					brighterIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				} else {
					final ImageIcon brighterIcon = ImageManager.brighter(this.downIcon);
					brighterIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				}
			} else {
				if (panel.isDeploy()) {
					this.upIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				} else {
					this.downIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
				}
			}

		}
		this.drawTitle(g, x, y, width, this.top);
	}

	protected void drawBaseTitleBackground(final Graphics g, final int x, final int y, final int width, final int height) {
		final Color oldColor = g.getColor();
		if (this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION) {
			final Graphics2D g2 = (Graphics2D) g;

			if (this.borderPosition == SwingConstants.LEFT) {
				// painting fixed color...
				g2.setColor(this.solidBandColor);
				g2.fillRect(x, y, width / 2, height);
				// painting grandient color...
				final GradientPaint gp = new GradientPaint(x + (width / 2), y, this.gradientBandInitColor, x + width, y,
						this.gradientBandEndColor, false);
				g2.setPaint(gp);
				g2.fillRect(x + (width / 2), y, (width / 2) + 1, height);
			} else if (this.borderPosition == SwingConstants.RIGHT) {
				// painting fixed color...
				g2.setColor(this.solidBandColor);
				g2.fillRect(x + (width / 2), y, (width / 2) + 1, height);
				// painting grandient color...
				final GradientPaint gp = new GradientPaint(x, y, this.gradientBandInitColor, x + (width / 2), y,
						this.gradientBandEndColor, false);
				g2.setPaint(gp);
				g2.fillRect(x, y, width / 2, height);
			}
		} else {
			final Graphics2D g2 = (Graphics2D) g;
			g2.setColor(this.solidBandColor);
			g2.fillRect(x, y, width, height / 2);

			final GradientPaint gp = new GradientPaint(x, y + (height / 2), this.gradientBandInitColor, x, y + height,
					this.gradientBandEndColor, false);
			g2.setPaint(gp);
			g2.fillRect(x, y + (height / 2), width, height / 2);
		}
		g.setColor(oldColor);
	}

	protected void drawTitleBackground(final Graphics g, final int x, final int y, final int width, final int height) {
		final Color oldColor = g.getColor();
		g.setColor(this.rectTitleColor);
		if (this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION) {
			g.fillRect(x, y, width, height - CurveMattedDeployableBorder.CURVE_WIDTH);
			this.drawTitleBackgroundCurve(g, x, (y + height) - CurveMattedDeployableBorder.CURVE_WIDTH, width,
					CurveMattedDeployableBorder.CURVE_WIDTH);
		} else {
			g.fillRect(x, y, width - CurveMattedDeployableBorder.CURVE_WIDTH, height);
			this.drawTitleBackgroundCurve(g, (x + width) - CurveMattedDeployableBorder.CURVE_WIDTH, y,
					CurveMattedDeployableBorder.CURVE_WIDTH, height);
		}
		g.setColor(oldColor);
	}

	protected void drawTitleBackgroundCurve(final Graphics g, final int x, final int y, final int width, final int height) {

		final Graphics2D g2 = (Graphics2D) g;
		final Color oldColor = g.getColor();
		final RenderingHints rH_old = g2.getRenderingHints();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		float x1 = x;
		float y1 = y;
		float x2 = x + width;
		float y2 = y + height;
		// Control points...
		float ctrlx1 = 0.0f;
		float ctrly1 = 0.0f;
		float ctrlx2 = 0.0f;
		float ctrly2 = 0.0f;

		float x3 = 0.0f;
		float y3 = 0.0f;

		final float ctrlPointDistance = 10.0f;

		if (this.orientation == CollapsiblePanel.VERTICAL_ORIENTATION) {
			if ((this.borderPosition == SwingConstants.TOP) || (this.borderPosition == SwingConstants.BOTTOM)) {
				ctrlx1 = x + ctrlPointDistance;
				ctrly1 = y;
				ctrlx2 = (x + width) - ctrlPointDistance;
				ctrly2 = y + height;
				x3 = x2 - width;
				y3 = y2;
			}
		} else {
			if (this.borderPosition == SwingConstants.LEFT) {
				ctrlx1 = x1;
				ctrly1 = y1 + ctrlPointDistance;
				ctrlx2 = x2;
				ctrly2 = y2 - ctrlPointDistance;
				x3 = x2;
				y3 = y2 - height;
			} else if (this.borderPosition == SwingConstants.RIGHT) {
				x2 = x;
				y1 = y;
				x1 = x + width;
				y2 = y + height;
				ctrlx1 = x1;
				ctrly1 = y1 + ctrlPointDistance;
				ctrlx2 = x2;
				ctrly2 = y2 - ctrlPointDistance;
				x3 = x2;
				y3 = y2 - height;
			}
		}
		g2.setColor(this.rectTitleColor);

		final GeneralPath polygon = new GeneralPath(Path2D.WIND_EVEN_ODD, 3);
		polygon.moveTo(x1, y1);
		polygon.curveTo(ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
		polygon.lineTo(x3, y3);
		polygon.closePath();
		g2.fill(polygon);

		g2.setRenderingHints(rH_old);
		g.setColor(oldColor);
	}

	protected void drawTitle(final Graphics g, final int x, final int y, final int width, final int height) {
		final RenderingHints rH_old = ((Graphics2D) g).getRenderingHints();
		final Color oldColor = g.getColor();

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(this.fontShadowColor);

		if (this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION) {
			final int textLocx = (width / 2) - g.getFontMetrics().getDescent();
			final int textLocy = CurveMattedDeployableBorder.UP_TO_ICON_MARGIN + this.leftIcon.getIconHeight()
			+ CurveMattedDeployableBorder.ICON_TO_TEXT_MARGIN;
			((Graphics2D) g).translate(textLocx, textLocy);
			((Graphics2D) g).rotate((90 * Math.PI) / 180);
			g.setColor(this.fontShadowColor);
			g.drawString(this.title, 0, 1);
			g.setColor(this.fontColor);
			g.drawString(this.title, 0, 0);
			((Graphics2D) g).rotate((-90 * Math.PI) / 180);
			((Graphics2D) g).translate(-textLocx, -textLocy);
		} else {
			final int textLocx = CurveMattedDeployableBorder.LEFT_TO_ICON_MARGIN + this.upIcon.getIconWidth()
			+ CurveMattedDeployableBorder.ICON_TO_TEXT_MARGIN;
			final int textLocy = ((height - g.getFontMetrics().getHeight()) / 2) + g.getFontMetrics().getAscent();
			g.setColor(this.fontShadowColor);
			g.drawString(this.title, textLocx, textLocy + 1);
			g.setColor(this.fontColor);
			g.drawString(this.title, textLocx, textLocy);
		}

		((Graphics2D) g).setRenderingHints(rH_old);
		g.setColor(oldColor);
	}

	@Override
	public void setMargin(final Insets insets) {
		this.margin = insets;
	}

}
