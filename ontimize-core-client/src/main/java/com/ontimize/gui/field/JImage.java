package com.ontimize.gui.field;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.cache.ImageRepository;
import com.ontimize.gui.Freeable;

/**
 * This class implements a background panel image. The image format should be supported by
 * <code>MediaTracker</code> and ransparencies are also allowed. The preferred size for component is
 * fixed by pixels of image.
 * <p>
 *
 * @author Imatia Innovation
 */
public class JImage extends JPanel implements ImageObserver, FormComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(JImage.class);

	public static boolean DEBUG = false;

	public static final String TEXT_LOADING = "Loading....";

	public static final String TEXT_ERROR = "Error loading image";

	public static final String TEXT_ABORTED = "Image load aborted";

	public static final String TEXT_IMAGE_NOT_FOUND = "Image not found";

	protected int status = ImageObserver.SOMEBITS;

	protected java.awt.Image image = null;

	protected URL urlImage = null;

	protected int width = -1;

	protected int high = -1;

	protected boolean lockWidth = false;

	protected boolean lockHigh = false;

	protected boolean rotate = false;

	public JImage(final Map parameters) {
		this.setOpaque(false);
		this.init(parameters);
	}

	@Override
	public void init(final Map parameters) {
		final Object oWidth = parameters.get("width");
		if (oWidth != null) {
			this.lockWidth = true;
			try {
				this.width = Integer.parseInt(oWidth.toString());
			} catch (final Exception e) {
				JImage.logger.error(this.getClass().toString() + " Error in parameter width.", e);
			}
		}
		final Object oHeight = parameters.get("height");
		if (oHeight != null) {
			this.lockHigh = true;
			try {
				this.high = Integer.parseInt(oHeight.toString());
			} catch (final Exception e) {
				JImage.logger.error(this.getClass().toString() + " Error in parameter height.", e);
			}
		}

		final Object rotate = parameters.get("rotate");
		if (rotate != null) {
			final String rot = rotate.toString();
			if (rot.equalsIgnoreCase("true") || rot.equalsIgnoreCase("yes")) {
				this.rotate = true;
			} else {
				this.rotate = false;
			}
		}

		final Object imag = parameters.get("img");
		if (imag == null) {
			JImage.logger.debug(this.getClass().toString() + " parameter 'img' not found");
		} else {
			this.urlImage = this.getClass().getClassLoader().getResource(imag.toString());
			if (this.urlImage == null) {
				JImage.logger.debug(this.getClass().toString() + " " + imag.toString() + " not found.");
			} else {
				this.image = ImageRepository.getImage(this.urlImage, this);
			}
		}

	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		if (layout instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
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
	public Dimension getPreferredSize() {
		if (this.image != null) {
			if ((this.width != -1) && (this.high != -1)) {
				if (!this.rotate) {
					return new Dimension(this.width, this.high);
				} else {
					return new Dimension(this.high, this.width);
				}
			} else if ((this.width != -1) && (this.high == -1)) {
				if (!this.rotate) {
					return new Dimension(this.image.getWidth(this), this.high);
				} else {
					return new Dimension(this.high, this.image.getWidth(this));
				}
			} else if ((this.width == -1) && (this.high != -1)) {
				if (!this.rotate) {
					return new Dimension(this.width, this.image.getHeight(this));
				} else {
					return new Dimension(this.image.getHeight(this), this.width);
				}
			} else {
				if (this.rotate) {
					return new Dimension(this.image.getHeight(this), this.image.getWidth(this));
				} else {
					return new Dimension(this.image.getWidth(this), this.image.getHeight(this));
				}
			}
		} else {
			return new Dimension(0, 0);
		}
	}

	@Override
	public boolean imageUpdate(final java.awt.Image im, final int info, final int x, final int y, final int width, final int height) {
		this.status = info;
		this.revalidate();
		this.repaint();
		return true;
		/*
		 * switch(status) { case ImageObserver.ALLBITS: img = im; this.revalidate(); repaint(); return
		 * false; case ImageObserver.FRAMEBITS: img = im; this.revalidate(); repaint(); return true;
		 * default: return true; }
		 */
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2 = (Graphics2D) g;
		if (this.urlImage == null) {
			g2.drawString(JImage.TEXT_IMAGE_NOT_FOUND, this.getWidth() / 2, this.getHeight() / 2);
		} else {
			// Scale factor
			final double factorX = (double) this.getWidth() / (double) this.getPreferredSize().width;
			final double factorY = (double) this.getHeight() / (double) this.getPreferredSize().height;
			double factorXY = 1.0;
			if ((factorX > 1) && (factorY > 1)) {
				factorXY = Math.min(factorX, factorY);
			} else {
				factorXY = 1.0;
			}
			// Paint the image
			switch (this.status) {
			case ImageObserver.ALLBITS:
				if (this.rotate) {
					g2.rotate(Math.PI / 2, (int) (factorXY * this.getPreferredSize().width) / 2,
							(int) (factorXY * this.getPreferredSize().height) / 2);
					g2.translate(
							((int) (factorXY * this.getPreferredSize().width) / 2)
							- ((int) (factorXY * this.getPreferredSize().height) / 2),
							-(((int) (factorXY * this.getPreferredSize().width) / 2)
									- ((int) (factorXY * this.getPreferredSize().height) / 2)));
					g2.drawImage(this.image, 0, 0, (int) (factorXY * this.getPreferredSize().height),
							(int) (factorXY * this.getPreferredSize().width), this);
				} else {
					g2.drawImage(this.image, 0, 0, (int) (factorXY * this.getPreferredSize().width),
							(int) (factorXY * this.getPreferredSize().height), this);
				}
				// g.drawImage(img,0,0,(int)(factorXY*this.getPreferredSize().width),
				// (int)(factorXY*this.getPreferredSize().height),this);
				break;
			default:
				if (this.rotate) {
					g2.rotate(Math.PI / 2, (int) (factorXY * this.getPreferredSize().width) / 2,
							(int) (factorXY * this.getPreferredSize().height) / 2);
					g2.translate(
							((int) (factorXY * this.getPreferredSize().width) / 2)
							- ((int) (factorXY * this.getPreferredSize().height) / 2),
							-(((int) (factorXY * this.getPreferredSize().width) / 2)
									- ((int) (factorXY * this.getPreferredSize().height) / 2)));
					g2.drawImage(ImageRepository.getImage(this.urlImage), 0, 0,
							(int) (factorXY * this.getPreferredSize().height),
							(int) (factorXY * this.getPreferredSize().width), this);
				} else {
					g2.drawImage(ImageRepository.getImage(this.urlImage), 0, 0,
							(int) (factorXY * this.getPreferredSize().width),
							(int) (factorXY * this.getPreferredSize().height), this);
				}
				// g.drawImage(RepositorioImagenes.getImage(this.urlImg),0,0,(int)(
						// factorXY*this.getPreferredSize().width),(int)(factorXY*this.
				// getPreferredSize().height),this);
				// g.drawString(this.TEXT_LOADING,this.getWidth()/2,this.getHeight()/
				// 2);
				break;
			}
		}
	}

	public void setImage(final URL url) {
		this.image = ImageRepository.getImage(url, this);
		this.revalidate();
		this.repaint();
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
