package com.ontimize.gui.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Map;

import javax.swing.ImageIcon;
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
public class MattedDeployableBorder extends MatteBorder implements IDeployBorder {

	public static final String INSETS = "insets";

	public static int LEFT_TO_ICON_MARGIN = 5;

	public static int UP_TO_ICON_MARGIN = 5;

	public static int ICON_TO_TEXT_MARGIN = 5;

	protected ImageIcon downIcon;

	protected ImageIcon upIcon;

	public ImageIcon rightIcon = null;

	public ImageIcon leftIcon = null;

	protected Rectangle imageBound;

	protected boolean highlight;

	protected String title;

	protected Color rectTitleColor;

	protected Color highlightColor;

	protected Color fontColor;

	protected Color fontShadowColor;

	protected Font font;

	public int orientation = CollapsiblePanel.VERTICAL_ORIENTATION;

	// TODO use this parameter to paint the border
	protected Insets margin = null;

	public MattedDeployableBorder(final Map parameters) throws Exception {
		super(parameters.containsKey(MattedDeployableBorder.INSETS)
				? ApplicationManager.parseInsets((String) parameters.get(MattedDeployableBorder.INSETS))
						: new Insets(21, 0, 0,
								0),
						parameters.containsKey("color") ? ColorConstants.parseColor((String) parameters.get("color"))
								: new Color(0xdbdbdb));
		this.init(parameters);
	}

	protected void init(final Map parameters) throws Exception {
		this.title = (String) parameters.get("title");

		final String orient = (String) parameters.get(CollapsiblePanel.ORIENTATION);
		if (orient != null) {
			if (CollapsiblePanel.HORIZONTAL_ORIENTATION_VALUE.equals(orient)) {
				this.orientation = CollapsiblePanel.HORIZONTAL_ORIENTATION;
			}
		}

		if ((this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION)
				&& !parameters.containsKey(MattedDeployableBorder.INSETS)) {
			this.top = 0;
			this.left = 21;
			this.bottom = 0;
			this.right = 0;
		}

		if (this.orientation == CollapsiblePanel.VERTICAL_ORIENTATION) {
			final String sUpIcon = (String) parameters.get("upicon");
			if (sUpIcon != null) {
				this.upIcon = ImageManager.getIcon(sUpIcon);
			} else {
				this.upIcon = ImageManager.getIcon(ImageManager.ARROW_UP);
			}
			final String sDownIcon = (String) parameters.get("downicon");
			if (sDownIcon != null) {
				this.downIcon = ImageManager.getIcon(sDownIcon);
			} else {
				this.downIcon = ImageManager.getIcon(ImageManager.ARROW_DOWN);
			}
			if ((this.upIcon != null) && (this.downIcon != null)) {
				this.imageBound = new Rectangle(MattedDeployableBorder.LEFT_TO_ICON_MARGIN,
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
				this.leftIcon = ImageManager.getIcon(ImageManager.ARROW_LEFT);
			}
			final String sRightIcon = (String) parameters.get("righticon");
			if (sRightIcon != null) {
				this.rightIcon = ImageManager.getIcon(sRightIcon);
			} else {
				this.rightIcon = ImageManager.getIcon(ImageManager.ARROW_RIGHT);
			}
			this.imageBound = new Rectangle((this.left / 2) - (this.leftIcon.getIconWidth() / 2),
					MattedDeployableBorder.UP_TO_ICON_MARGIN, this.leftIcon.getIconWidth(),
					this.leftIcon.getIconHeight());
		}

		final String recTitleColor = (String) parameters.get("recttitlecolor");
		if (recTitleColor != null) {
			this.rectTitleColor = ColorConstants.parseColor(recTitleColor);
		} else {
			this.rectTitleColor = new Color(0xb6b6b6);
		}

		final String sHighlightColor = (String) parameters.get("highlightcolor");
		this.highlightColor = ParseUtils.getColor(sHighlightColor, this.color);

		final String sFontColor = (String) parameters.get("fontcolor");
		if (sFontColor != null) {
			this.fontColor = ColorConstants.parseColor(sFontColor);
		} else {
			this.fontColor = new Color(0x333333);
		}

		final String sFontColorShadow = (String) parameters.get("fontshadowcolor");
		if (sFontColorShadow != null) {
			this.fontShadowColor = ColorConstants.parseColor(sFontColorShadow);
		} else {
			this.fontShadowColor = new Color(0xdbdbdb);
		}

		final String sFont = (String) parameters.get("font");
		if (sFont != null) {
			this.font = Font.decode(sFont);
		} else {
			this.font = Font.decode("Arial-BOLD-12");
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
		this.title = title;
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
		int markWidth = g.getFontMetrics().stringWidth(this.title);
		markWidth += MattedDeployableBorder.LEFT_TO_ICON_MARGIN + this.rightIcon.getIconWidth()
		+ MattedDeployableBorder.ICON_TO_TEXT_MARGIN + 8;
		this.drawTitleBackground(g, x, y, this.left, markWidth);
		if ((c != null) && (c instanceof CollapsiblePanel)) {
			final CollapsiblePanel panel = (CollapsiblePanel) c;
			if (this.highlight) {
				g.setColor(this.highlightColor);
				g.fillOval(this.imageBound.x - 1, this.imageBound.y - 1, this.imageBound.width + 1,
						this.imageBound.height + 1);
			}
			if (panel.isDeploy()) {
				this.leftIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
			} else {
				this.rightIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
			}
		}
		this.drawTitle(g, x, y, this.left, markWidth);
	}

	protected void paintVerticalOrientation(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
		int markWidth = g.getFontMetrics().stringWidth(this.title);
		markWidth += MattedDeployableBorder.LEFT_TO_ICON_MARGIN + this.upIcon.getIconWidth()
		+ MattedDeployableBorder.ICON_TO_TEXT_MARGIN + 8;
		this.drawTitleBackground(g, x, y, markWidth, this.top);
		if ((c != null) && (c instanceof CollapsiblePanel)) {
			final CollapsiblePanel panel = (CollapsiblePanel) c;
			if (this.highlight) {
				g.setColor(this.highlightColor);
				g.fillOval(this.imageBound.x - 1, this.imageBound.y - 1, this.imageBound.width + 1,
						this.imageBound.height + 1);
			}
			if (panel.isDeploy()) {
				this.upIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
			} else {
				this.downIcon.paintIcon(c, g, this.imageBound.x, this.imageBound.y);
			}
		}
		this.drawTitle(g, x, y, width, this.top);
	}

	protected void drawTitleBackground(final Graphics g, final int x, final int y, final int width, final int height) {
		if (this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION) {
			g.setColor(this.rectTitleColor);
			g.fillRect(x, y, width, height);
			g.fillOval(x, (y + height) - (width / 2), width, width);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(this.fontShadowColor);
		} else {
			g.setColor(this.rectTitleColor);
			final int w = height;
			g.fillRect(x, y, width - (w / 2), height);
			g.fillOval((x + width) - w, y - 1, w, w + 1);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(this.fontShadowColor);
		}
	}

	protected void drawTitle(final Graphics g, final int x, final int y, final int width, final int height) {
		if (this.orientation == CollapsiblePanel.HORIZONTAL_ORIENTATION) {
			final int textLocx = (width / 2) - g.getFontMetrics().getDescent();
			final int textLocy = MattedDeployableBorder.UP_TO_ICON_MARGIN + this.leftIcon.getIconHeight()
			+ MattedDeployableBorder.ICON_TO_TEXT_MARGIN;
			((Graphics2D) g).translate(textLocx, textLocy);
			((Graphics2D) g).rotate((90 * Math.PI) / 180);
			g.setColor(this.fontShadowColor);
			g.drawString(this.title, 0, 1);
			g.setColor(this.fontColor);
			g.drawString(this.title, 0, 0);
			((Graphics2D) g).rotate((-90 * Math.PI) / 180);
			((Graphics2D) g).translate(-textLocx, -textLocy);
		} else {
			final int textLocx = MattedDeployableBorder.LEFT_TO_ICON_MARGIN + this.upIcon.getIconWidth()
			+ MattedDeployableBorder.ICON_TO_TEXT_MARGIN;
			final int textLocy = ((height - g.getFontMetrics().getHeight()) / 2) + g.getFontMetrics().getAscent();
			g.setColor(this.fontShadowColor);
			g.drawString(this.title, textLocx, textLocy + 1);
			g.setColor(this.fontColor);
			g.drawString(this.title, textLocx, textLocy);
		}
	}

	@Override
	public void setMargin(final Insets insets) {
		this.margin = insets;
	}

}
