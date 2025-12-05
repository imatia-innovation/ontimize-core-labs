package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.ImagePreview;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.FileUtils;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.transfer.ImageTransferHandler;
import com.ontimize.util.templates.ITemplateField;

/**
 * The main class to visualize a .gif, .jpeg or .png images.
 *
 * @see BytesBlock
 * @author Imatia Innovation
 */

public class ImageDataField extends DataField implements DataComponent, OpenDialog, Freeable, ITemplateField {

	private static final Logger logger = LoggerFactory.getLogger(ImageDataField.class);

	/** The top value. */
	protected static final int TOP = 0;

	/** The right value. */
	protected static final int RIGHT = 1;

	/** The max size value. */
	public static final int MAX_SIZE = 2048;

	/** The location buttons. By default, top. */
	protected int locationButtons = ImageDataField.TOP;

	/** The reference to a cursor. By default, null. */
	protected static Cursor cursorZoom = null;

	/** The tip message showed over image. */
	protected static String DOUBLE_CLICK_TO_VIEW_TIP = "datafield.double_click_view_image";

	/** The tip message shows over open image button. */
	protected static String OPEN_IMAGE_BUTTON_TIP = "datafield.select_image";

	/** The tip message over delete data field button. */
	protected static String DELETE_DATA_FIELD_BUTTON_TIP = "datafield.reset_field";

	/** The tip message over paste from clipboard button. */
	protected static String CLIPBOARD_PASTE_BUTTON_TIP = "datafield.paste_from_clipboard";

	/** The clipboard error message. */
	protected static String CLIPBOARD_ERROR_MESSAGE = "datafield.no_image_in_clipboard";

	/**
	 * The main class to create a zoom window in a new dialog.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class ZoomWindow extends JDialog {

		protected JPanel panelInfo = new JPanel(new BorderLayout());

		protected JScrollPane panelScroll = new JScrollPane();

		protected ImageIcon zoomImage = new ImageIcon(ImageDataField.this.bytesImage);

		protected JLabel labelImage = new JLabel(this.zoomImage);

		protected float zoom = 1;

		protected JLabel sizeLabel = new JLabel();

		protected JButton saveButton = new DataField.FieldButton();

		protected JButton copyButton = new DataField.FieldButton();

		protected JButton pasteButton = new DataField.FieldButton();

		/**
		 * The Jdialog constructor. Constructor has implemented a mouse listener in order to make larger
		 * image when left mouse button is pressed and make it smaller in case of pressing the right button.
		 * <p>
		 * @param frame the frame
		 */
		public ZoomWindow(final Frame frame) {
			super(frame, true);
			if (ImageDataField.cursorZoom != null) {
				this.labelImage.setCursor(ImageDataField.cursorZoom);
			}
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(this.panelInfo, BorderLayout.NORTH);
			final FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
			fl.setHgap(0);
			fl.setVgap(1);
			final JPanel panelSize = new JPanel(fl);
			panelSize.add(this.saveButton);
			panelSize.add(this.copyButton);
			panelSize.add(this.sizeLabel);
			this.saveButton.setIcon(ApplicationManager.getDefaultSaveIcon());
			this.saveButton.setMargin(new Insets(0, 0, 0, 0));
			this.saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					FileUtils.saveJPEGImage(ImageDataField.this.parentFrame,
							((ImageIcon) ZoomWindow.this.labelImage.getIcon()).getImage(),
							ImageDataField.this.resources);
				}
			});

			this.copyButton.setIcon(ImageManager.getIcon(ImageManager.COPY));
			this.copyButton.setToolTipText(
					ApplicationManager.getTranslation("datafield.copy_to_clipboard", ImageDataField.this.resources));
			this.copyButton.setMargin(new Insets(0, 0, 0, 0));
			this.copyButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						ApplicationManager
						.copyToClipboard(((ImageIcon) ZoomWindow.this.labelImage.getIcon()).getImage());
					} catch (final Exception ex) {
						ImageDataField.logger.error(null, ex);
					}

				}
			});

			this.panelInfo.add(panelSize, BorderLayout.NORTH);
			this.getContentPane().add(this.panelScroll);
			this.panelScroll.getViewport().add(this.labelImage);
			// Info
			this.sizeLabel.setText(Integer.toString(this.zoomImage.getIconWidth()) + "x"
					+ Integer.toString(this.zoomImage.getIconHeight()));

			this.labelImage.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent event) {
					try {
						if (event.getClickCount() == 1) {
							if (event.getModifiers() == InputEvent.BUTTON1_MASK) {
								if (ZoomWindow.this.zoom >= 1) {
									final Cursor cursor = ZoomWindow.this.getCursor();
									ZoomWindow.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									ZoomWindow.this.zoom++;
									if (((ZoomWindow.this.zoom * ZoomWindow.this.zoomImage
											.getIconWidth()) > ImageDataField.MAX_SIZE)
											|| ((ZoomWindow.this.zoom * ZoomWindow.this.zoomImage
													.getIconHeight()) > ImageDataField.MAX_SIZE)) {
										ZoomWindow.this.zoom--;
										ZoomWindow.this.setCursor(cursor);
										return;
									}
									final SoftReference sf = new SoftReference(
											new ImageIcon(ZoomWindow.this.zoomImage.getImage()
													.getScaledInstance(
															(int) ZoomWindow.this.zoom
															* ZoomWindow.this.zoomImage.getIconWidth(),
															-1, java.awt.Image.SCALE_FAST)));
									if (sf != null) {
										ZoomWindow.this.labelImage.setIcon((ImageIcon) sf.get());
									}
									ZoomWindow.this.labelImage
									.setToolTipText(Integer.toString((int) ZoomWindow.this.zoom) + ":1");
									ZoomWindow.this.doZoom();
									ZoomWindow.this.setCursor(cursor);
									return;
								}
								if ((ZoomWindow.this.zoom < 1) && (ZoomWindow.this.zoom >= (1 / 8))) {
									final Cursor cursor = ZoomWindow.this.getCursor();
									ZoomWindow.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									ZoomWindow.this.zoom = ZoomWindow.this.zoom * 2;
									final SoftReference sf = new SoftReference(new ImageIcon(ZoomWindow.this.zoomImage
											.getImage()
											.getScaledInstance(
													(int) (ZoomWindow.this.zoom * ZoomWindow.this.zoomImage.getIconWidth()),
													-1, java.awt.Image.SCALE_FAST)));
									if (sf != null) {
										ZoomWindow.this.labelImage.setIcon((ImageIcon) sf.get());
									}
									ZoomWindow.this.labelImage
									.setToolTipText("1:" + Integer.toString((int) (1 / ZoomWindow.this.zoom)));
									ZoomWindow.this.doZoom();
									ZoomWindow.this.setCursor(cursor);
									return;
								}
							} else {
								if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
									if (ZoomWindow.this.zoom > 1) {
										final Cursor cursor = ZoomWindow.this.getCursor();
										ZoomWindow.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
										ZoomWindow.this.zoom--;
										final SoftReference sf = new SoftReference(
												new ImageIcon(ZoomWindow.this.zoomImage.getImage()
														.getScaledInstance(
																(int) ZoomWindow.this.zoom
																* ZoomWindow.this.zoomImage.getIconWidth(),
																-1, java.awt.Image.SCALE_FAST)));
										if (sf != null) {
											ZoomWindow.this.labelImage.setIcon((ImageIcon) sf.get());
										}
										ZoomWindow.this.labelImage
										.setToolTipText(Integer.toString((int) ZoomWindow.this.zoom) + ":1");
										ZoomWindow.this.doZoom();
										ZoomWindow.this.setCursor(cursor);
										return;
									}
									if ((ZoomWindow.this.zoom <= 1) && (ZoomWindow.this.zoom > (1 / 8))) {
										final Cursor cursor = ZoomWindow.this.getCursor();
										ZoomWindow.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
										ZoomWindow.this.zoom = ZoomWindow.this.zoom / 2;
										final SoftReference sf = new SoftReference(
												new ImageIcon(ZoomWindow.this.zoomImage.getImage()
														.getScaledInstance(
																(int) (ZoomWindow.this.zoom
																		* ZoomWindow.this.zoomImage.getIconWidth()),
																-1, java.awt.Image.SCALE_FAST)));
										if (sf != null) {
											ZoomWindow.this.labelImage.setIcon((ImageIcon) sf.get());
										}
										ZoomWindow.this.labelImage
										.setToolTipText("1:" + Integer.toString((int) (1 / ZoomWindow.this.zoom)));
										ZoomWindow.this.doZoom();
										ZoomWindow.this.setCursor(cursor);
										return;
									}
								}
							}
						}
					} catch (final Exception e) {
						ImageDataField.logger.error(null, e);
					}
				}
			});
			// Size
			this.pack();
			// if(this.getWidth()>400 ||this.getHeight()>400) {
			// this.setSize(400,400);
			// }
			// Center
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		}

		/**
		 * Shows the window.
		 */
		public void showWindow() {
			this.setTitle(ImageDataField.this.getLabelComponentText());
			this.setVisible(true);
		}

		/**
		 * Changes the window dimension.
		 */
		public void doZoom() {
			this.pack();
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			if ((this.getSize().width <= d.width) && (this.getSize().height <= d.height)) {
				this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
			} else {
				this.setSize(d.width, d.height);
				this.setLocation(0, 0);
			}
		}

	}

	/**
	 * Class to filter files by extension. For example, "jpeg" or "gif".
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {

		List extensions = new Vector();

		String description = "";

		/**
		 * Class constructor. Supports a list of accepted extensions.
		 * <p>
		 * @param acceptedExtensions accepted extension list
		 * @param description the description
		 */
		public ExtensionFileFilter(final List acceptedExtensions, final String description) {
			this.extensions = acceptedExtensions;
			this.description = description;
		}

		/**
		 * Class constructor. By default, null.
		 */
		public ExtensionFileFilter() {
		}

		/**
		 * Adds an extension.
		 * <p>
		 * @param extension the extension
		 */
		public void addExtension(final String extension) {
			this.extensions.add(extension);
		}

		/**
		 * Gets the description.
		 * <p>
		 * @return the description
		 */
		@Override
		public String getDescription() {
			return this.description;
		}

		/**
		 * Sets description.
		 * <p>
		 * @param description the string with description
		 */
		public void setDescription(final String description) {
			this.description = description;
		}

		/**
		 * Condition to accept the file.
		 * <p>
		 * @param file the file
		 * @return the condition
		 */
		@Override
		public boolean accept(final File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				final int dotIndex = file.getPath().lastIndexOf(".");
				final String extension = file.getPath().substring(dotIndex + 1);
				if (this.extensions.contains(extension)) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

	/**
	 * The parent frame. By default, null.
	 */
	protected Frame parentFrame = null;

	/**
	 * The reference to image icon. By default, null.
	 */
	protected ImageIcon image = null;

	/**
	 * An instance of a load button.
	 */
	protected JButton loadButton = new FieldButton();

	/**
	 * An instance of a delete button.
	 */
	protected JButton deleteBt = new FieldButton();

	/**
	 * An instance of a paste button.
	 */
	protected JButton pasteBt = new FieldButton();

	/**
	 * The last path for file. By default, null.
	 */
	protected File lastPath = null;

	/**
	 * The reference for the bytes of image.
	 */
	protected byte[] bytesImage = null;

	/**
	 * The image high. By default, 0.
	 */
	protected int iHigh = 0;

	/**
	 * The image width. By default, 0.
	 */
	protected int imageWidth = 0;

	protected boolean keepAspectRatio = false;

	protected boolean allowZoom = true;

	public static final String RETURN_BYTES = "returnbytes";

	/**
	 * A reference for returning bytes instead of BytesBlock. By default, false.
	 */
	protected boolean returnBytes = false;

	/**
	 * A reference for an empty image icon. By default, null.
	 */
	protected ImageIcon emptyImage = null;

	/**
	 * A reference for a delete button listener. By default, null.
	 */
	protected ActionListener deleteBtListener = null;

	/**
	 * A reference for a load button listener. By default, null.
	 */
	protected ActionListener loadBtListener = null;

	/**
	 * The zoom listener enabled condition. By default, true.
	 */
	protected boolean zoomListenerEnabled = true;

	/**
	 * The class constructor. Sets border, adds listeners and inits parameters.
	 * <p>
	 * @param param the <code>Hashtable</code> parameters
	 */
	public ImageDataField(final Map param) {
		// In this component, the data field is the image
		this.dataField = new JLabel(this.image);
		this.dataField.setBorder(ParseUtils.getBorder((String) param.get("border"),
				BorderManager.getBorder(BorderManager.DEFAULT_IMAGE_BORDER_KEY)));
		this.dataField.setMinimumSize(new Dimension(10, 10));
		this.dataField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent evento) {
				// With double click, it show a window to see zoom
				if (ImageDataField.this.zoomListenerEnabled && (evento.getClickCount() == 2)
						&& (ImageDataField.this.enabled) && (!ImageDataField.this.isEmpty())) {
					final ZoomWindow zoomWindow = new ZoomWindow(ImageDataField.this.parentFrame);
					// Open the window
					zoomWindow.showWindow();
				}
			}
		});

		this.setTransferHandler(new ImageDataFieldTransferHandler());

		final Map hParameters = ObjectTools.clone(param);
		if (!hParameters.containsKey(DataField.TIP)) {
			hParameters.put(DataField.TIP, ImageDataField.DOUBLE_CLICK_TO_VIEW_TIP);
		}
		this.init(hParameters);
		final JPanel jpButtonsPanel = new JPanel(new GridBagLayout());
		jpButtonsPanel.setOpaque(false);
		if (this.locationButtons == ImageDataField.TOP) {
			jpButtonsPanel.add(this.loadButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jpButtonsPanel.add(this.deleteBt,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jpButtonsPanel.add(this.pasteBt,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

			final GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.dataField);
			constraints.gridx = 0;
			constraints.gridy = GridBagConstraints.RELATIVE;
			constraints.gridwidth = 2;
			// ((GridBagLayout)this.getLayout()).setConstraints(this.campoDatos,constraints);
			this.remove(this.dataField);
			this.add(jpButtonsPanel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.dataField, constraints);
		} else {
			jpButtonsPanel.add(this.loadButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jpButtonsPanel.add(this.deleteBt,
					new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0.01, GridBagConstraints.NORTH,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jpButtonsPanel.add(this.pasteBt,
					new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0.01, GridBagConstraints.NORTH,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(jpButtonsPanel,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0.01, GridBagConstraints.NORTHWEST,
							GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

		}

		final ImageIcon exploreIcon = ParseUtils.getImageIcon((String) param.get("loadicon"),
				ImageManager.getIcon(ImageManager.EXPLORE));
		if (exploreIcon != null) {
			this.loadButton.setIcon(exploreIcon);
		} else {
			this.loadButton.setText("..");
		}
		this.loadButton.setMargin(new Insets(0, 0, 0, 0));

		final ImageIcon deleteIcon = ParseUtils.getImageIcon((String) param.get("deleteicon"),
				ImageManager.getIcon(ImageManager.DELETE));
		if (deleteIcon != null) {
			this.deleteBt.setIcon(deleteIcon);
		} else {
			this.deleteBt.setText("..");
		}
		this.deleteBt.setMargin(new Insets(0, 0, 0, 0));

		this.deleteBtListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				ImageDataField.this.deleteDataByUser();
			}
		};
		this.deleteBt.addActionListener(this.deleteBtListener);
		this.loadBtListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				// Open a dialog to select the images
				JFileChooser chooser = null;
				if (ImageDataField.this.lastPath != null) {
					chooser = new JFileChooser(ImageDataField.this.lastPath);
				} else {
					chooser = new JFileChooser();
				}
				final ExtensionFileFilter filter = new ExtensionFileFilter();
				filter.addExtension("jpg");
				filter.addExtension("gif");
				filter.addExtension("png");
				filter.addExtension("JPG");
				filter.addExtension("GIF");
				filter.addExtension("PNG");
				filter.setDescription("Images");
				chooser.setAccessory(new ImagePreview(chooser));
				chooser.setFileFilter(filter);
				final int selection = chooser.showOpenDialog(ImageDataField.this.parentFrame);
				if (selection == JFileChooser.APPROVE_OPTION) {
					// Update the image
					final File selectedFile = chooser.getSelectedFile();
					ImageDataField.this.lastPath = selectedFile;
					if (!selectedFile.isDirectory()) {
						FileInputStream fileInputStream = null;
						try {
							fileInputStream = new FileInputStream(selectedFile);
							final Object oPreviousValue = ImageDataField.this.getValue();
							ImageDataField.this.bytesImage = new byte[(int) selectedFile.length()];
							int read = 0;
							while (read < ImageDataField.this.bytesImage.length) {
								read += fileInputStream.read(ImageDataField.this.bytesImage, read,
										ImageDataField.this.bytesImage.length - read);
								if (read == -1) {
									break;
								}
							}
							ImageDataField.this.update();
							ImageDataField.this.fireValueChanged(ImageDataField.this.getValue(), oPreviousValue,
									ValueEvent.USER_CHANGE);
							fileInputStream.close();
						} catch (final Exception e) {
							ImageDataField.logger.error(null, e);
							MessageDialog.showMessage(ImageDataField.this.parentFrame, "Error loading image",
									JOptionPane.ERROR_MESSAGE, null);
						} finally {
							if (fileInputStream != null) {
								try {
									fileInputStream.close();
								} catch (final Exception e) {
									ImageDataField.logger.trace(null, e);
								}
							}
						}
					}
				}
			}
		};
		this.loadButton.addActionListener(this.loadBtListener);
		ImageDataField.cursorZoom = ApplicationManager.getZoomCursor();

		final ImageIcon pasteIcon = ParseUtils.getImageIcon((String) param.get("pasteicon"),
				ImageManager.getIcon(ImageManager.PASTE));
		if (exploreIcon != null) {
			this.pasteBt.setIcon(pasteIcon);
		} else {
			this.pasteBt.setText("..");
		}

		this.pasteBt.setToolTipText(ApplicationManager.getTranslation(ImageDataField.CLIPBOARD_PASTE_BUTTON_TIP,
				ImageDataField.this.resources));

		this.pasteBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final java.awt.Image image = ApplicationManager.pasteImageFromClipboard();
					if (image != null) {
						final Object oPreviousValue = ImageDataField.this.getValue();
						final ByteArrayOutputStream bas = new ByteArrayOutputStream();
						final ImageIcon icon = new ImageIcon(image);
						// String [] out=ImageIO.getWriterFormatNames();
						// out.toString();
						ImageIO.write((RenderedImage) icon.getImage(), "png", bas);
						ImageDataField.this.bytesImage = bas.toByteArray();
						ImageDataField.this.update();
						ImageDataField.this.fireValueChanged(ImageDataField.this.getValue(), oPreviousValue,
								ValueEvent.USER_CHANGE);
					} else {
						ImageDataField.this.parentForm.message(ImageDataField.CLIPBOARD_ERROR_MESSAGE,
								Form.ERROR_MESSAGE);
					}
				} catch (final Exception ex) {
					ImageDataField.logger.error(null, ex);
				}

			}
		});

		this.setButtonTips();
	}

	protected void importImage(final Image image) {
		try {
			if (image != null) {
				final Object oPreviousValue = ImageDataField.this.getValue();
				final ByteArrayOutputStream bas = new ByteArrayOutputStream();
				final ImageIcon icon = new ImageIcon(image);
				ImageIO.write((RenderedImage) icon.getImage(), "png", bas);
				ImageDataField.this.bytesImage = bas.toByteArray();
				ImageDataField.this.update();
				ImageDataField.this.fireValueChanged(ImageDataField.this.getValue(), oPreviousValue,
						ValueEvent.USER_CHANGE);
			} else {
				ImageDataField.logger.debug("Image is null");
			}
		} catch (final Exception ex) {
			ImageDataField.logger.error("Error importing image", ex);
		}
	}

	/**
	 * Sets tips for all buttons.
	 */
	protected void setButtonTips() {
		if (this.deleteBt != null) {
			this.deleteBt.setToolTipText(
					ApplicationManager.getTranslation(ImageDataField.DELETE_DATA_FIELD_BUTTON_TIP, this.resources));
		}
		if (this.loadButton != null) {
			this.loadButton.setToolTipText(
					ApplicationManager.getTranslation(ImageDataField.OPEN_IMAGE_BUTTON_TIP, this.resources));
		}
		if (this.pasteBt != null) {
			this.pasteBt.setToolTipText(
					ApplicationManager.getTranslation(ImageDataField.CLIPBOARD_PASTE_BUTTON_TIP, this.resources));
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		this.setButtonTips();
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. Adds the next parameters:
	 *
	 *        <p>
	 *
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
	 *        <tr>
	 *        <td>height</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The preferred field height in pixels. Useful for specifying empty columns.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>width</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The preferred column width in pixels. Useful for specifying empty columns.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>buttons</td>
	 *        <td><i>top/right/no</td>
	 *        <td>top</td>
	 *        <td>no</td>
	 *        <td>The position of buttons. With 'no' buttons will not be showed.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>emptyimage</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The relative path from classpath to load the empty image.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>keepaspectratio</td>
	 *        <td>true/false</td>
	 *        <td>false</td>
	 *        <td>no</td>
	 *        <td>The image keeps its aspect ratio</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>returnbytes</td>
	 *        <td>true/false</td>
	 *        <td>false</td>
	 *        <td>no</td>
	 *        <td>Returns the value as {@link byte[]} instead of {@link BytesBlock}</td>
	 *        </tr>
	 *
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		final Object width = parameters.get("width");
		if (width != null) {
			try {
				this.imageWidth = Integer.parseInt(width.toString());
				if (this.imageWidth < 0) {
					this.imageWidth = 0;
					ImageDataField.logger.warn("Error in parameter width. It must be >0");
				}
			} catch (final Exception e) {
				ImageDataField.logger.error("Error in parameter width : ", e);
			}
		}
		final Object height = parameters.get("height");
		if (height != null) {
			try {
				this.iHigh = Integer.parseInt(height.toString());
				if (this.iHigh < 0) {
					this.iHigh = 0;
					ImageDataField.logger.warn("Error in parameter height. It must be >0");
				}
			} catch (final Exception e) {
				ImageDataField.logger.error("Error in parameter height:", e);
			}
		}
		final Object buttons = parameters.get("buttons");
		if (buttons != null) {
			if ("no".equals(buttons)) {
				this.setVisibleButtons(false);
			} else if ("right".equals(buttons)) {
				this.locationButtons = ImageDataField.RIGHT;
			}
		}
		final Object emptyimage = parameters.get("emptyimage");
		if (emptyimage != null) {
			final ImageIcon iiTempImage = ImageManager.getIcon(emptyimage.toString());
			if (iiTempImage == null) {
				ImageDataField.logger.debug("Not found {} ", emptyimage.toString());
			} else {
				if (this.iHigh != 0) {
					if (this.imageWidth != 0) {
						this.emptyImage = new ImageIcon(iiTempImage.getImage()
								.getScaledInstance(this.imageWidth, this.iHigh, java.awt.Image.SCALE_FAST));
					} else {
						this.emptyImage = new ImageIcon(
								iiTempImage.getImage().getScaledInstance(-1, this.iHigh, java.awt.Image.SCALE_FAST));
					}
				} else {
					if (this.imageWidth != 0) {
						this.emptyImage = new ImageIcon(iiTempImage.getImage()
								.getScaledInstance(this.imageWidth, -1, java.awt.Image.SCALE_FAST));
					} else {
						this.emptyImage = new ImageIcon(iiTempImage.getImage());
					}
				}
				((JLabel) this.dataField).setIcon(this.emptyImage);
				iiTempImage.getImage().flush();
			}
		}

		final boolean borderbuttons = ParseUtils.getBoolean((String) parameters.get("borderbuttons"), true);
		final boolean opaquebuttons = ParseUtils.getBoolean((String) parameters.get("opaquebuttons"), true);
		final boolean highlightButtons = ParseUtils.getBoolean((String) parameters.get("highlightbuttons"), false);
		MouseListener listenerHighlightButtons = null;
		if (highlightButtons) {
			listenerHighlightButtons = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(true);
					((AbstractButton) e.getSource()).setContentAreaFilled(true);
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(false);
					((AbstractButton) e.getSource()).setContentAreaFilled(false);
				}
			};
		}

		this.changeButton(this.loadButton, borderbuttons, opaquebuttons, listenerHighlightButtons);
		this.changeButton(this.deleteBt, borderbuttons, opaquebuttons, listenerHighlightButtons);
		this.changeButton(this.pasteBt, borderbuttons, opaquebuttons, listenerHighlightButtons);

		this.keepAspectRatio = ParseUtils.getBoolean((String) parameters.get("keepaspectratio"), this.keepAspectRatio);
		this.allowZoom = ParseUtils.getBoolean((String) parameters.get("allowzoom"), this.allowZoom);

		this.returnBytes = ParseUtils.getBoolean((String) parameters.get(ImageDataField.RETURN_BYTES),
				this.returnBytes);

	}

	public boolean isReturnBytes() {
		return this.returnBytes;
	}

	public void setReturnBytes(final boolean returnBytes) {
		this.returnBytes = returnBytes;
	}

	/**
	 * @return a <code>BytesBlock</code> instance
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}

		if (!this.isReturnBytes()) {
			// Without compression because they are JPEG and GIF images
			final BytesBlock bytesBlock = new BytesBlock(this.bytesImage, BytesBlock.NO_COMPRESSION);
			return bytesBlock;
		} else {
			return this.bytesImage;
		}
	}

	@Override
	public void setValue(final Object value) {
		if ((value != null) && (value instanceof BytesBlock)) {
			// Cache:
			final Object oCurrentValue = this.getValue();
			if (!this.isEmpty()) {
				if (oCurrentValue.equals(value)) {
					this.valueSave = this.getValue();
					return;
				}
			}
			this.bytesImage = null;
			this.bytesImage = ((BytesBlock) value).getBytes();
			// Update the label
			this.update();
			this.valueSave = this.getValue();
			this.fireValueChanged(this.valueSave, oCurrentValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} else if ((value != null) && (value instanceof byte[])) {
			// Cache:
			final Object oCurrentValue = this.getValue();
			if (!this.isEmpty()) {
				if (oCurrentValue.equals(value)) {
					this.valueSave = this.getValue();
					return;
				}
			}
			this.bytesImage = null;
			this.bytesImage = (byte[]) value;
			// Update the label
			this.update();
			this.valueSave = this.getValue();
			this.fireValueChanged(this.valueSave, oCurrentValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} else {
			this.deleteData();
		}

	}

	protected void update() {
		if (this.bytesImage == null) {
			((JLabel) this.dataField).setIcon(null);
		} else {
			// Show the image, sometimes an it is necessary to scale the image
			ImageIcon tempImage = null;
			try {
				tempImage = new ImageIcon(this.bytesImage);
			} catch (final Exception e) {
				ImageDataField.logger.error("Error creating image.", e);
			}

			if (this.keepAspectRatio && (this.iHigh != 0) && (this.imageWidth != 0)) {
				// Scale the image keeping the aspect ratio
				try {
					this.image = new ImageIcon(
							ImageDataField.getImage(this.bytesImage, this.imageWidth, this.iHigh, this.allowZoom));
				} catch (final IOException e) {
					ImageDataField.logger.error(null, e);
				}
			} else if (!this.allowZoom && (tempImage != null)
					&& ((tempImage.getIconWidth() < this.imageWidth) || (this.imageWidth == 0))
					&& ((this.iHigh == 0) || (tempImage.getIconHeight() < this.iHigh))) {
				try {
					this.image = new ImageIcon(ImageDataField.getImage(this.bytesImage,
							this.imageWidth != 0 ? this.imageWidth : tempImage.getIconWidth(),
									this.iHigh != 0 ? this.iHigh : tempImage.getIconHeight(), this.allowZoom));
				} catch (final IOException e) {
					ImageDataField.logger.error(null, e);
				}
			} else if (this.iHigh != 0) {
				if (this.imageWidth != 0) {
					this.image = new ImageIcon(tempImage.getImage()
							.getScaledInstance(this.imageWidth, this.iHigh, java.awt.Image.SCALE_FAST));
				} else {
					this.image = new ImageIcon(
							tempImage.getImage().getScaledInstance(-1, this.iHigh, java.awt.Image.SCALE_FAST));
				}
			} else {
				if (this.imageWidth != 0) {
					this.image = new ImageIcon(
							tempImage.getImage().getScaledInstance(this.imageWidth, -1, java.awt.Image.SCALE_FAST));
				} else {
					this.image = new ImageIcon(tempImage.getImage());
				}
			}
			tempImage.getImage().flush();
			tempImage = null;
			((JLabel) this.dataField).setIcon(this.image);
		}
	}

	@Override
	public boolean isEmpty() {
		if ((this.image == null) || ((this.image.getIconHeight() <= 0) && (this.image.getIconWidth() <= 0))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns always true.
	 */
	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public void setParentFrame(final Frame frame) {
		this.parentFrame = frame;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (this.loadButton != null) {
			this.loadButton.setEnabled(enabled);
		}
		if (this.deleteBt != null) {
			this.deleteBt.setEnabled(enabled);
		}
		if (this.pasteBt != null) {
			this.pasteBt.setEnabled(enabled);
		}

		super.setEnabled(enabled);
		this.dataField.setEnabled(enabled);
	}

	private void deleteDataByUser() {
		final Object oOldValue = this.getValue();
		this.bytesImage = null;
		this.image = null;
		((JLabel) this.dataField).setIcon(this.emptyImage);
		this.fireValueChanged(null, oOldValue, ValueEvent.USER_CHANGE);
	}

	@Override
	public void deleteData() {
		final Object oOldValue = this.getValue();
		this.bytesImage = null;
		this.image = null;

		((JLabel) this.dataField).setIcon(this.emptyImage);
		this.valueSave = this.getValue();
		this.fireValueChanged(this.valueSave, oOldValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	@Override
	protected void finalize() throws Throwable {
		// Free
		this.image.getImage().flush();
		this.image = null;
		this.bytesImage = null;
		ImageDataField.logger.debug("Finalize method is invoked");
		super.finalize();
	}

	@Override
	public void free() {
		super.free();
		this.parentFrame = null;
		ImageDataField.logger.debug("Free method is invoked");
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.LONGVARBINARY;
	}

	/**
	 * Gets the image for icon.
	 * <p>
	 * @return the image
	 */
	public ImageIcon getImage() {
		return this.image;
	}

	/**
	 * Gets the empty image.
	 * <p>
	 * @return the empty image
	 */
	public ImageIcon getEmptyImage() {
		return this.emptyImage;
	}

	/**
	 * Sets a zoom listener enabled.
	 * <p>
	 * @param enabled the enabled condition
	 */
	public void setZoomListenerEnabled(final boolean enabled) {
		this.zoomListenerEnabled = enabled;
	}

	/**
	 * Uninstalls the delete button listener.
	 */
	public void uninstallDeleteButtonListener() {
		this.deleteBt.removeActionListener(this.deleteBtListener);
	}

	/**
	 * Uninstalls the load button listener.
	 */
	public void uninstallLoadButtonListener() {
		this.loadButton.removeActionListener(this.loadBtListener);
	}

	/**
	 * Gets the delete button listener.
	 * <p>
	 * @return the delete button
	 */
	public JButton getDeleteButton() {
		return this.deleteBt;
	}

	/**
	 * Gets the load button listener.
	 * <p>
	 * @return the load button
	 */
	public JButton getLoadButton() {
		return this.loadButton;
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * Sets visible buttons according to the condition.
	 * <p>
	 * @param visible the visibility condition
	 */
	public void setVisibleButtons(final boolean visible) {

		if (this.deleteBt != null) {
			this.deleteBt.setVisible(visible);
		}
		if (this.loadButton != null) {
			this.loadButton.setVisible(visible);
		}
		if (this.pasteBt != null) {
			this.pasteBt.setVisible(visible);
		}
	}

	public static Image getImage(final byte[] imageData, int width, int height, final boolean allowZoom) throws IOException {
		final Image im = new ImageIcon(imageData).getImage();

		if ((width <= 0) && (height > 0)) {
			width = (height * im.getWidth(null)) / im.getHeight(null);
		} else if ((height <= 0) && (width > 0)) {
			height = (width * im.getHeight(null)) / im.getWidth(null);
		}

		final double widthRatio = Math.abs(im.getWidth(null) / (float) width);
		final double heightRatio = Math.abs(im.getHeight(null) / (double) height);

		Image imScaled = null;

		int x = 0;
		int y = 0;

		if (!allowZoom && (widthRatio <= 1) && (heightRatio <= 1)) {
			imScaled = im;
		} else if ((width > im.getWidth(null)) && (height > im.getHeight(null))) {
			if (widthRatio > heightRatio) {
				imScaled = new ImageIcon(im.getScaledInstance(width, -1, Image.SCALE_SMOOTH)).getImage();
			} else {
				imScaled = new ImageIcon(im.getScaledInstance(-1, height, Image.SCALE_SMOOTH)).getImage();
			}
		} else {// OK
			if ((width < im.getWidth(null)) && (height < im.getHeight(null))) {
				if (widthRatio > heightRatio) {
					imScaled = new ImageIcon(im.getScaledInstance(width, -1, Image.SCALE_SMOOTH)).getImage();
				} else {
					imScaled = new ImageIcon(im.getScaledInstance(-1, height, Image.SCALE_SMOOTH)).getImage();
				}
			} else {
				if (widthRatio > heightRatio) {
					imScaled = new ImageIcon(im.getScaledInstance(width, -1, Image.SCALE_SMOOTH)).getImage();
				} else {
					imScaled = new ImageIcon(im.getScaledInstance(-1, height, Image.SCALE_SMOOTH)).getImage();
				}
			}
		}
		x = Math.abs((imScaled.getWidth(null) - width) / 2);
		y = Math.abs((imScaled.getHeight(null) - height) / 2);

		final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = bi.createGraphics();
		g.drawImage(imScaled, x, y, null);
		return bi;
	}

	public static void debugImage(final Image im, final String title) {
		final JDialog d = new JDialog((Frame) null, title, true);
		d.setContentPane(new JPanel() {

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(im.getWidth(null), im.getHeight(null));
			}

			@Override
			protected void paintComponent(final Graphics g) {
				super.paintComponent(g);
				g.drawImage(im, 0, 0, null);
			}
		});
		d.pack();
		d.setVisible(true);
	}

	@Override
	public int getTemplateDataType() {
		return ITemplateField.DATA_TYPE_IMAGE;
	}

	@Override
	public Object getTemplateDataValue() {
		return this.getValue();
	}

	public static class ImageDataFieldTransferHandler extends ImageTransferHandler {

		@Override
		public boolean importData(final TransferSupport support) {
			if (!(support.getComponent() instanceof ImageDataField)) {
				return false;
			}

			if (!this.canImport(support)) {
				return false;
			}

			// There are three types of DataFlavor to check:
			// 1. A java.awt.Image object (DataFlavor.imageFlavor)
			// 2. A List<File> object (DataFlavor.javaFileListFlavor)
			// 3. Binary data with an image/* MIME type.

			if (support.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				try {
					final Image image = (Image) support.getTransferable().getTransferData(DataFlavor.imageFlavor);
					final ImageDataField dataField = (ImageDataField) support.getComponent();
					dataField.importImage(image);
					return true;
				} catch (final UnsupportedFlavorException e) {
					ImageDataField.logger.error(null, e);
				} catch (final IOException ex) {
					ImageDataField.logger.error(null, ex);
				}
			}

			if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				try {
					final Iterable<?> list = (Iterable<?>) support.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					final Iterator<?> files = list.iterator();
					if (files.hasNext()) {
						final File file = (File) files.next();
						final Image image = ImageIO.read(file);
						final ImageDataField dataField = (ImageDataField) support.getComponent();
						dataField.importImage(image);
						return true;
					}
				} catch (final UnsupportedFlavorException e) {
					ImageDataField.logger.error(null, e);
				} catch (final IOException ex) {
					ImageDataField.logger.error(null, ex);
				}
			}

			for (final DataFlavor flavor : support.getDataFlavors()) {
				if (this.isReadableByImageIO(flavor)) {
					try {
						Image image;

						final Object data = support.getTransferable().getTransferData(flavor);
						if (data instanceof URL) {
							image = ImageIO.read((URL) data);
						} else if (data instanceof File) {
							image = ImageIO.read((File) data);
						} else {
							image = ImageIO.read((InputStream) data);
						}
						final ImageDataField dataField = (ImageDataField) support.getComponent();
						dataField.importImage(image);
						return true;
					} catch (final UnsupportedFlavorException e) {
						ImageDataField.logger.error(null, e);
					} catch (final IOException ex) {
						ImageDataField.logger.error(null, ex);
					}
				}
			}
			return false;
		}

	}

}
