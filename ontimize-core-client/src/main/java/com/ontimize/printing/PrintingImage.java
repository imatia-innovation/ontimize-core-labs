package com.ontimize.printing;

import java.awt.Color;
import java.awt.Graphics;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.util.remote.BytesBlock;

public class PrintingImage extends RectangleElement {

	private static final Logger logger = LoggerFactory.getLogger(PrintingImage.class);

	ImageIcon im = null;

	java.awt.Image img = null;

	boolean specifiedImg = false;

	public PrintingImage(final Map parameters) {
		super(parameters);
		final Object img = parameters.get("img");
		if (img != null) {
			final URL urlImg = this.getClass().getClassLoader().getResource(img.toString());
			if (urlImg == null) {
				PrintingImage.logger.debug("Image not encountered: " + img.toString());
			} else {
				this.specifiedImg = true;
				this.im = new ImageIcon(urlImg);
				if ((this.width != -1) && (this.high != -1)) {
					this.img = this.im.getImage()
							.getScaledInstance(AbstractPrintingElement.millimeterToPagePixels(this.width),
									AbstractPrintingElement.millimeterToPagePixels(this.high), java.awt.Image.SCALE_FAST);
				} else {
					this.img = this.im.getImage();
				}
			}
		} else {
			PrintingImage.logger.debug("Parameter 'img' not found");
		}
	}

	@Override
	public void setContent(final Object value) {
		PrintingImage.logger.debug("ImagenP: setValue");
		if ((value != null) && (value instanceof BytesBlock)) {
			final byte[] bytesImagen = ((BytesBlock) value).getBytes();
			// Update
			if (bytesImagen == null) {
				return;
			} else {
				// Creates the image
				try {
					this.im = new ImageIcon(bytesImagen);
					if ((this.width != -1) && (this.high != -1)) {
						this.img = this.im.getImage()
								.getScaledInstance(AbstractPrintingElement.millimeterToPagePixels(this.width),
										AbstractPrintingElement.millimeterToPagePixels(this.high),
										java.awt.Image.SCALE_DEFAULT);
					} else {
						this.img = this.im.getImage();
					}
				} catch (final Exception e) {
					PrintingImage.logger.error("Error creating image.", e);
				}
			}
		} else if ((value != null) && (value instanceof ImageIcon)) {
			// Creates the image
			try {
				this.im = (ImageIcon) value;
				if ((this.width != -1) && (this.high != -1)) {
					this.img = this.im.getImage()
							.getScaledInstance(AbstractPrintingElement.millimeterToPagePixels(this.width),
									AbstractPrintingElement.millimeterToPagePixels(this.high),
									java.awt.Image.SCALE_DEFAULT);
				} else {
					this.img = this.im.getImage();
				}
			} catch (final Exception e) {
				PrintingImage.logger.error("Error creating imagen.", e);
			}
		}
	}

	@Override
	public void paint(final Graphics g, final double scale) {
		final Color c = g.getColor();
		g.setColor(this.color);
		g.translate((int) (AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
				(int) (AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale));
		if (this.img != null) {
			g.drawImage(this.img, 0, 0, (int) (scale * this.img.getWidth(null)),
					(int) (scale * this.img.getHeight(null)), null);
		}
		g.translate((int) (-AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
				(int) (-AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale));
		g.setColor(c);
	}

}
