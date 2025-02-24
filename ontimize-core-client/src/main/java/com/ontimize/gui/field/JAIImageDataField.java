package com.ontimize.gui.field;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.media.jai.PlanarImage;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.imaging.JAIUtils;
import com.ontimize.gui.imaging.JImageDisplayJAI;
import com.ontimize.jee.common.util.remote.BytesBlock;

/**
 * This class implements a Java advanced imaging.
 * <p>
 *
 * @see @see
 *      <a href= "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/index.html"
 *      ><code>Java[tm] Advanced Imaging API Documentation</code></a>
 * @author Imatia Innovation
 */
public class JAIImageDataField extends DataField {

	private static final Logger logger = LoggerFactory.getLogger(JAIImageDataField.class);

	/**
	 * The reference to output format. By default, <code>jpeg<code>.
	 */
	protected String outputFormat = "jpeg";

	/**
	 * The reference to encode parameters. By default, null.
	 */
	protected com.sun.media.jai.codec.ImageEncodeParam encodeParams = null;

	/**
	 * A reference to image data block. By default, null.
	 */
	protected BytesBlock value = null;

	/**
	 * A reference for a rendered image. By default, null.
	 */
	protected RenderedImage image = null;

	/**
	 * A reference for a zoom dialog. By default, null.
	 */
	protected JDialog dZoom = null;

	/**
	 * A reference for a scroll.
	 */
	protected JScrollPane scroll = null;

	/**
	 * This class implements an extended image display JAI to manage cursors and focus listeners.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class EJImageDisplayJAI extends JImageDisplayJAI {

		/**
		 * The class constructor. Calls to super, sets cursor and adds mouse listener
		 */
		public EJImageDisplayJAI() {
			super();
			this.setCursor(ApplicationManager.getZoomCursor());
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(final MouseEvent e) {
					if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
						// Zoom in
						if (EJImageDisplayJAI.this.zoom >= 1) {
							try {
								EJImageDisplayJAI.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								EJImageDisplayJAI.this.setZoom(EJImageDisplayJAI.this.zoom + 1);
							} catch (final Exception ex) {
								JAIImageDataField.logger.trace(null, ex);
							} finally {
								EJImageDisplayJAI.this.setCursor(Cursor.getDefaultCursor());
							}
						} else {
							try {
								EJImageDisplayJAI.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								EJImageDisplayJAI.this.setZoom(EJImageDisplayJAI.this.zoom * 2);
							} catch (final Exception ex) {
								JAIImageDataField.logger.trace(null, ex);
							} finally {
								EJImageDisplayJAI.this.setCursor(Cursor.getDefaultCursor());
							}
						}
					} else {
						if (EJImageDisplayJAI.this.zoom > 1) {
							try {
								EJImageDisplayJAI.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								EJImageDisplayJAI.this.setZoom(EJImageDisplayJAI.this.zoom - 1);
							} catch (final Exception ex) {
								JAIImageDataField.logger.trace(null, ex);
							} finally {
								EJImageDisplayJAI.this.setCursor(Cursor.getDefaultCursor());
							}
						} else {
							try {
								EJImageDisplayJAI.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								EJImageDisplayJAI.this.setZoom(EJImageDisplayJAI.this.zoom / 2);
							} catch (final Exception ex) {
								JAIImageDataField.logger.trace(null, ex);
							} finally {
								EJImageDisplayJAI.this.setCursor(Cursor.getDefaultCursor());
							}
						}
					}
				}
			});
		}

	}

	/**
	 * The reference to display zoom. By default, null.
	 */
	protected EJImageDisplayJAI displayZoom = null;

	/**
	 * The class constructor. Inits parameters and manages the mouse click events for zoom window.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public JAIImageDataField(final Map parameters) {
		this.dataField = new JImageDisplayJAI();
		this.init(parameters);
		this.dataField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if ((e.getClickCount() == 2) && (!JAIImageDataField.this.isEmpty())) {
					if (JAIImageDataField.this.dZoom == null) {
						final Window w = SwingUtilities.getWindowAncestor(JAIImageDataField.this);
						if (w instanceof Frame) {
							JAIImageDataField.this.dZoom = new JDialog((Frame) w, "Zoom", true);
						} else {
							JAIImageDataField.this.dZoom = new JDialog((Dialog) w, "Zoom", true);
						}
						JAIImageDataField.this.displayZoom = new EJImageDisplayJAI();
						JAIImageDataField.this.dZoom.getContentPane()
						.add(new JScrollPane(JAIImageDataField.this.displayZoom));
					}
					JAIImageDataField.this.displayZoom
					.set(((JImageDisplayJAI) JAIImageDataField.this.dataField).getSource());
					JAIImageDataField.this.dZoom.pack();
					ApplicationManager.center(JAIImageDataField.this.dZoom);
					JAIImageDataField.this.dZoom.setVisible(true);
				}
			}
		});
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. The next parameter are added:
	 *
	 *        <p>
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
	 *
	 *        <tr>
	 *        <td>format</td>
	 *        <td></td>
	 *        <td>jpeg</td>
	 *        <td>no</td>
	 *        <td>The output format.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>adjustsize</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Adjust the size.</td>
	 *        </tr>
	 *
	 *        </Table>
	 */

	@Override
	public void init(final Map parameters) {
		super.init(parameters);

		final Object format = parameters.get("format");
		if (format != null) {
			this.outputFormat = format.toString();
		} else {
			this.encodeParams = new com.sun.media.jai.codec.JPEGEncodeParam();
			((com.sun.media.jai.codec.JPEGEncodeParam) this.encodeParams).setQuality(0.90f);
		}

		final Object adjustsize = parameters.get("adjustsize");
		if (adjustsize != null) {
			try {
				((JImageDisplayJAI) this.dataField).setAdjustToSize(Integer.parseInt(adjustsize.toString()));
			} catch (final Exception e) {
				JAIImageDataField.logger.error("Error in parameter 'adjustsize'", e);
			}
		}
		this.remove(this.dataField);
		this.scroll = new JScrollPane(this.dataField);
		this.add(this.scroll,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, this.weightDataFieldH, 0.0,
						GridBagConstraints.EAST, this.redimensJTextField,
						new Insets(2, 2, 2, 2), 0, 0));
	}

	@Override
	public Object getValue() {
		// Return the bytes
		if ((this.value != null) && ApplicationManager.DEBUG) {
			JAIImageDataField.logger.debug(this.getClass().toString() + " : " + this.getAttribute()
			+ " -> Image size in bytes: " + this.value.getBytes().length);
		}
		return this.value;
	}

	@Override
	public boolean isEmpty() {
		if (this.value == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void deleteData() {
		this.valueSave = this.value;
		this.value = null;
		((JImageDisplayJAI) this.dataField).set(null);
		this.fireValueChanged(this.value, this.valueSave, ValueEvent.PROGRAMMATIC_CHANGE);
		this.scroll.invalidate();
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.LONGVARBINARY;
	}

	/**
	 * Gets the rendered image.
	 * <p>
	 *
	 * @see JImageDisplayJAI#getSource()
	 * @return the rendered image
	 */
	public RenderedImage getImage() {
		return ((JImageDisplayJAI) this.dataField).getSource();
	}

	/**
	 * Sets the image. Calls to:
	 * <ul>
	 * <li>{@link JAIUtils#saveImage(RenderedImage, String, OutputStream, com.sun.media.jai.codec.ImageEncodeParam)}
	 * <li>{@link JImageDisplayJAI#set(RenderedImage)}
	 * </ul>
	 *
	 * <p>
	 * @param im the rendered image to set
	 * @throws Exception when an exception occurs
	 */
	public void setImagen(final RenderedImage im) throws Exception {
		if (im == null) {
			this.deleteData();
			return;
		}
		final long t = System.currentTimeMillis();
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		InputStream in = null;
		try {
			this.valueSave = this.value;
			JAIUtils.saveImage(im, this.outputFormat, bOut, this.encodeParams);
			final long t2 = System.currentTimeMillis();
			JAIImageDataField.logger.debug(this.getClass().toString() + " Tiempo saveImage " + (t2 - t));
			this.value = new BytesBlock(bOut.toByteArray());
			if (ApplicationManager.DEBUG) {
				JAIImageDataField.logger.debug(this.getClass().toString() + " : " + this.getAttribute()
				+ " -> setValue : Image size in bytes: " + this.value.getBytes().length);
			}
			in = new com.sun.media.jai.codec.ByteArraySeekableStream(this.value.getBytes());
			((JImageDisplayJAI) this.dataField)
			.set(JAIUtils.loadImage((com.sun.media.jai.codec.ByteArraySeekableStream) in));
			if (im instanceof PlanarImage) {
				((PlanarImage) im).dispose();
			}
			final long t3 = System.currentTimeMillis();
			JAIImageDataField.logger.debug(this.getClass().toString() + " Time set " + (t3 - t2));
			this.fireValueChanged(this.value, this.valueSave, ValueEvent.PROGRAMMATIC_CHANGE);
			this.scroll.invalidate();
		} finally {
			bOut.close();
			in.close();
		}
	}

	@Override
	public void setValue(final Object value) {
		if (ApplicationManager.DEBUG) {
			JAIImageDataField.logger
			.debug(this.getClass().toString() + " " + this.getAttribute() + " Set Value with : " + value);
		}
		if (value == null) {
			this.deleteData();
			return;
		}
		if (value instanceof BytesBlock) {
			// Decodify the image
			InputStream in = null;
			try {
				this.valueSave = this.value;
				in = new com.sun.media.jai.codec.ByteArraySeekableStream(((BytesBlock) value).getBytes());
				((JImageDisplayJAI) this.dataField)
				.set(JAIUtils.loadImage((com.sun.media.jai.codec.ByteArraySeekableStream) in));
				this.value = (BytesBlock) value;
				if (ApplicationManager.DEBUG) {
					JAIImageDataField.logger
					.debug(this.getClass().toString() + " : " + this.getAttribute()
					+ " -> Set Value : Image size (bytes): " + this.value.getBytes().length);
				}
				in.close();
				this.fireValueChanged(value, this.valueSave, ValueEvent.PROGRAMMATIC_CHANGE);
				this.scroll.invalidate();
			} catch (final Exception e) {
				JAIImageDataField.logger.error(null, e);
				this.deleteData();
				return;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (final Exception e) {
					JAIImageDataField.logger.trace(null, e);
				}
			}
		} else if (value instanceof RenderedImage) {
			try {
				this.valueSave = this.value;
				this.setImagen((RenderedImage) value);
			} catch (final Exception e) {
				JAIImageDataField.logger.error(null, e);
				this.deleteData();
				return;
			}
		}
	}

	/**
	 * Sets the encode parameters.
	 * <p>
	 * @param params the encoding parameters
	 */
	public void setEncodeParams(final com.sun.media.jai.codec.ImageEncodeParam params) {
		this.encodeParams = params;
	}

	/**
	 * Sets encode format.
	 * <p>
	 * @param format the encoding format
	 */
	public void setEncodeFormat(final String format) {
		this.outputFormat = format;
	}

	/**
	 * Sets the encode format.
	 * <p>
	 * @param format the format
	 * @param params the image encode param
	 */
	public void setEncodeFormat(final String format, final com.sun.media.jai.codec.ImageEncodeParam params) {
		this.outputFormat = format;
		this.encodeParams = params;
	}

}
