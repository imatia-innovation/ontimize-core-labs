package com.ontimize.printing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintingLabel extends RectangleElement {

	private static final Logger logger = LoggerFactory.getLogger(PrintingLabel.class);

	protected JLabel label = new JLabel();

	protected String text = null;

	public PrintingLabel(final Map parameters) {
		super(parameters);

		this.label.setOpaque(false);
		this.label.setBorder(null);

		if (AbstractPrintingElement.defaultFont != null) {
			this.label.setFont(AbstractPrintingElement.defaultFont);
		}

		if (this.color != null) {
			this.label.setForeground(this.color);
		}
		try {
			if (this.italics && this.bold) {
				this.label.setFont(this.label.getFont().deriveFont(Font.ITALIC | Font.BOLD));
			} else if (this.italics) {
				this.label.setFont(this.label.getFont().deriveFont(Font.ITALIC));
			} else if (this.bold) {
				this.label.setFont(this.label.getFont().deriveFont(Font.BOLD));
			}
		} catch (final Exception e) {
			PrintingLabel.logger.error(e.getMessage(), e);
		}
		final Object c = parameters.get("text");
		if (c != null) {
			this.text = c.toString();
			super.setContent(c);
			this.label.setText(c.toString());
		}
		this.label.setHorizontalAlignment(this.aligment);
	}

	public JLabel getLabel() {
		return this.label;
	}

	@Override
	public void setContent(final Object c) {
		if (this.text == null) {
			if (c != null) {
				this.label.setText(c.toString());
			} else {
				this.label.setText("");
			}
		} else {
			PrintingLabel.logger.debug("Error setting the content of the label");
		}
	}

	@Override
	public synchronized void paint(final Graphics g, final double scale) {
		if (this.color == null) {
			return;
		}
		final Color c = g.getColor();
		g.setColor(this.color);
		this.label.setFont(this.label.getFont().deriveFont((float) this.fontSize));
		final int iPreviousTextSize = this.label.getFontMetrics(this.label.getFont()).stringWidth(this.getLabel().getText());
		final int iAimSize = (int) (scale * iPreviousTextSize);
		try {
			if (Double.compare(scale, 1) == 0) {
			} else if (scale < 1.0) {
				this.label.setFont(this.label.getFont().deriveFont((float) (scale * this.fontSize)));
				int iCurrentTextSize = this.label.getFontMetrics(this.label.getFont())
						.stringWidth(this.getLabel().getText());
				int iCurrentFontSize = (int) (scale * this.fontSize);
				while ((iCurrentTextSize > iAimSize) && (iCurrentFontSize > 5)) {
					iCurrentFontSize--;
					this.label.setFont(this.label.getFont().deriveFont((float) iCurrentFontSize));
					iCurrentTextSize = this.label.getFontMetrics(this.label.getFont())
							.stringWidth(this.getLabel().getText());
				}
			} else {
				this.label.setFont(this.label.getFont().deriveFont((float) (scale * this.fontSize)));
				int iCurrentTextSize = this.label.getFontMetrics(this.label.getFont())
						.stringWidth(this.getLabel().getText());
				int iCurrentFontSize = (int) (scale * this.fontSize);
				while ((iCurrentTextSize < iAimSize) && (iCurrentFontSize < 60)) {
					iCurrentFontSize++;
					this.label.setFont(this.label.getFont().deriveFont((float) iCurrentFontSize));
					iCurrentTextSize = this.label.getFontMetrics(this.label.getFont())
							.stringWidth(this.getLabel().getText());
				}
			}
		} catch (final Exception e) {
			PrintingLabel.logger.error(e.getMessage(), e);
		}
		if ((this.width != -1) && (this.high != -1)) {
			this.label.setSize((int) (AbstractPrintingElement.millimeterToPagePixels(this.width) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.high) * scale));
		} else {
			this.label.setSize(this.label.getPreferredSize());
		}

		try {
			if (this.italics && this.bold) {
				this.label.setFont(this.label.getFont().deriveFont(Font.ITALIC | Font.BOLD));
				if (AbstractPrintingElement.DEBUG) {
					PrintingLabel.logger.debug("Setting italic and bold: " + this.toString());
				}
			} else if (this.italics) {
				this.label.setFont(this.label.getFont().deriveFont(Font.ITALIC));
				if (AbstractPrintingElement.DEBUG) {
					PrintingLabel.logger.debug("Setting italic : " + this.toString());
				}
			} else if (this.bold) {
				this.label.setFont(this.label.getFont().deriveFont(Font.BOLD));
				if (AbstractPrintingElement.DEBUG) {
					PrintingLabel.logger.debug("Setting bold: " + this.toString());
				}
			}
		} catch (final Exception e) {
			PrintingLabel.logger.error(null, e);
		}
		g.translate((int) (AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
				(int) (AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale));
		this.label.paint(g);
		g.translate((int) (-AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
				(int) (-AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale));
		g.setColor(c);
	}

}
