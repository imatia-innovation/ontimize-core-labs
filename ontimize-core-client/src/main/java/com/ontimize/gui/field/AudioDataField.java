package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.ImagePreview;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.ParseUtils;

public class AudioDataField extends DataField implements OpenDialog {

	private static final Logger logger = LoggerFactory.getLogger(AudioDataField.class);

	public static boolean DEBUG_AUDIO = false;

	protected byte[] bytesSound = null;

	protected byte[] currentSound = null;

	protected Frame parentFrame = null;

	private ImageIcon icon = null;

	private ImageIcon emptyIco = null;

	protected JButton loadButton = new JButton();

	protected JButton deleteButton = new JButton();

	protected JFileChooser chooser = null;

	protected File lastPath = null;

	protected AudioPlayer audioPlayer = new AudioPlayer();

	protected AudioPlayerThread playerThread = null;

	protected NumberFormat format = NumberFormat.getInstance();

	protected MouseAdapter fieldListener = new MouseAdapter() {

		@Override
		public void mouseClicked(final MouseEvent e) {
			if ((e.getClickCount() == 2) && !AudioDataField.this.isEmpty()) {
				AudioDataField.this.showAudioPlayer();
			}
		}
	};

	protected class AudioPlayerThread extends Thread {

		private boolean stop = false;

		private int offset = 0;

		private final int frameSize = AudioDataField.this.audioPlayer.audioFormat.getFrameSize();

		private long microsecondPosition = 0;

		public AudioPlayerThread() {
		}

		public long getMicrosecondPosition() {
			return this.microsecondPosition;
		}

		public void setMicrosecondPosition(final long microsecondPosition) {
			this.microsecondPosition = microsecondPosition;
		}

		@Override
		public void run() {
			try {
				AudioDataField.this.audioPlayer.line.setMicrosecondPosition(this.microsecondPosition);
				AudioDataField.this.audioPlayer.line.start();
				AudioDataField.this.audioPlayer.stopButton.setEnabled(true);
				AudioDataField.this.audioPlayer.pauseButton.setEnabled(true);
				AudioDataField.this.audioPlayer.playButton.setEnabled(false);
				while (!this.stop) {
					try {
						Thread.sleep(10);
					} catch (final Exception e) {
						AudioDataField.logger.trace(null, e);
					}

					AudioDataField.this.audioPlayer.slider
					.setValue((int) (AudioDataField.this.audioPlayer.line.getMicrosecondPosition() / 1000));

					if (AudioDataField.DEBUG_AUDIO) {
						AudioDataField.logger.debug("Position (Microseg.): "
								+ AudioDataField.this.audioPlayer.line.getMicrosecondPosition());
						AudioDataField.logger
						.debug("Length (MicroSeg): " + AudioDataField.this.audioPlayer.line.getMicrosecondLength());
					}
					if (AudioDataField.this.audioPlayer.line
							.getMicrosecondPosition() >= AudioDataField.this.audioPlayer.line.getMicrosecondLength()) {
						this.stopPlayer();
					}
				}
			} catch (final Exception e) {
				AudioDataField.logger.error(null, e);
				AudioDataField.this.audioPlayer.status.setText("Cannot use device");
				this.stopPlayer();
			}
		}

		public void stopPlayer() {
			this.stop = true;
			this.offset = 0;
			AudioDataField.this.audioPlayer.line.stop();
			AudioDataField.this.audioPlayer.line.setMicrosecondPosition(0);
			AudioDataField.this.audioPlayer.slider.setValue(0);
			AudioDataField.this.audioPlayer.stopButton.setEnabled(false);
			AudioDataField.this.audioPlayer.pauseButton.setEnabled(false);
			AudioDataField.this.audioPlayer.playButton.setEnabled(true);
		}

	};

	protected class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {

		List extensions = new Vector();

		String description = "";

		public ExtensionFileFilter(final Vector acceptedExtensions, final String description) {
			this.extensions = acceptedExtensions;
			this.description = description;
		}

		public ExtensionFileFilter() {
		}

		public void addExtension(final String extension) {
			this.extensions.add(extension);
		}

		@Override
		public String getDescription() {
			return this.description;
		}

		public void setDescription(final String description) {
			this.description = description;
		}

		@Override
		public boolean accept(final File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				final int iDotIndex = file.getPath().lastIndexOf(".");
				final String extension = file.getPath().substring(iDotIndex + 1);
				if (this.extensions.contains(extension)) {
					return true;
				} else {
					return false;
				}
			}
		}

	};

	protected class AudioPlayer extends JDialog {

		JButton playButton = new JButton();

		JButton pauseButton = new JButton();

		JButton stopButton = new JButton();

		JButton rewindButton = new JButton();

		JButton fordwardButton = new JButton();

		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);

		JLabel status = new JLabel();

		Clip line = null;

		AudioFormat audioFormat = null;

		public boolean paused = false;

		public long microsecondPosition = 0;

		public AudioPlayer() {
			super(AudioDataField.this.parentFrame, true);
			this.fordwardButton.setMargin(new Insets(1, 1, 1, 1));
			this.pauseButton.setMargin(new Insets(1, 1, 1, 1));
			this.playButton.setMargin(new Insets(1, 1, 1, 1));
			this.rewindButton.setMargin(new Insets(1, 1, 1, 1));
			this.stopButton.setMargin(new Insets(1, 1, 1, 1));
			final JPanel jbButtonsPanel = new JPanel();
			jbButtonsPanel.add(this.rewindButton);
			jbButtonsPanel.add(this.playButton);
			jbButtonsPanel.add(this.pauseButton);
			jbButtonsPanel.add(this.stopButton);
			jbButtonsPanel.add(this.fordwardButton);

			final ImageIcon playIcon = ImageManager.getIcon(ImageManager.PLAY);
			if (playIcon != null) {
				this.playButton.setIcon(playIcon);
			} else {
				AudioDataField.logger.debug(this.getClass().toString() + " : images/play.png icon cannot be found");
			}

			final ImageIcon pauseIcon = ImageManager.getIcon(ImageManager.PAUSE);
			if (pauseIcon != null) {
				this.pauseButton.setIcon(pauseIcon);
			} else {
				AudioDataField.logger.debug(this.getClass().toString() + " : images/pause.png icon cannot be found");
			}

			final ImageIcon stopIcon = ImageManager.getIcon(ImageManager.STOP);
			if (stopIcon != null) {
				this.stopButton.setIcon(stopIcon);
			} else {
				AudioDataField.logger.debug(this.getClass().toString() + ": images/stop.png cannot be found");
			}

			final ImageIcon rewindIcon = ImageManager.getIcon(ImageManager.REWIND);
			if (rewindIcon != null) {
				this.rewindButton.setIcon(rewindIcon);
			} else {
				AudioDataField.logger.debug(this.getClass().toString() + " :  images/rewind.png icon cannot be found");
			}

			final ImageIcon forwardIcon = ImageManager.getIcon(ImageManager.FORWARD);
			if (forwardIcon != null) {
				this.fordwardButton.setIcon(forwardIcon);
			} else {
				AudioDataField.logger.debug(this.getClass().toString() + " :  images/forward.png icon cannot be found");
			}

			this.getContentPane().add(this.status, BorderLayout.NORTH);
			this.getContentPane().add(jbButtonsPanel, BorderLayout.SOUTH);
			this.getContentPane().add(this.slider);
			this.setResizable(false);
			this.playButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					AudioPlayer.this.play();
				}
			});
			this.stopButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					AudioPlayer.this.stop();
				}
			});
			this.pauseButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					AudioPlayer.this.line.stop();
					AudioPlayer.this.microsecondPosition = AudioPlayer.this.line.getMicrosecondPosition();
					AudioPlayer.this.paused = true;
					AudioPlayer.this.stopButton.setEnabled(true);
					AudioPlayer.this.pauseButton.setEnabled(false);
					AudioPlayer.this.playButton.setEnabled(true);
				}
			});
		}

		public void setSource(final AudioInputStream ff) {
			if (this.line != null) {
				this.line.close();
				this.line = null;
				this.audioFormat = null;
			}
			if (ff == null) {
				this.fordwardButton.setEnabled(false);
				this.pauseButton.setEnabled(false);
				this.playButton.setEnabled(false);
				this.rewindButton.setEnabled(false);
				this.stopButton.setEnabled(false);
				this.status.setText("No source");
			} else {
				this.audioFormat = ff.getFormat();
				this.fordwardButton.setEnabled(true);
				this.pauseButton.setEnabled(false);
				this.playButton.setEnabled(true);
				this.rewindButton.setEnabled(true);
				this.stopButton.setEnabled(false);
				final DataLine.Info info = new DataLine.Info(Clip.class, this.audioFormat);
				if (!AudioSystem.isLineSupported(info)) {
					this.setSource(null);
					this.status.setText("Unsupported line. No device matching format.");
				} else {
					try {
						this.line = (Clip) AudioSystem.getLine(info);
						this.line.open(ff);
						if (AudioDataField.DEBUG_AUDIO) {
							AudioDataField.logger.debug("Position (Microseg.): " + this.line.getMicrosecondPosition());
							AudioDataField.logger.debug("Length (MicroSeg): " + this.line.getMicrosecondLength());
						}
						// Calculate sound length
						final int channels = this.audioFormat.getChannels();
						final float hz = this.audioFormat.getSampleRate();
						final int bits = this.audioFormat.getSampleSizeInBits();
						final double seconds = (AudioDataField.this.bytesSound.length * 8) / (channels * hz * bits);
						this.slider.setMaximum((int) (seconds * 1000));
						AudioDataField.this.format.setMaximumFractionDigits(2);
						this.status.setText(this.audioFormat.toString() + "  : "
								+ AudioDataField.this.format.format(seconds) + " s.");
					} catch (final IllegalStateException e) {
						AudioDataField.logger.error(null, e);
						this.setSource(null);
						this.status.setText("Cannot access the device. The device is in use");
					} catch (final Exception e) {
						AudioDataField.logger.error(null, e);
						this.setSource(null);
						this.status.setText("Cannot access the device.");
					}
				}
			}
		}

		public void play() {

			if (!this.paused) {
				this.stop();
				AudioDataField.this.playerThread = new AudioPlayerThread();
				AudioDataField.this.playerThread.start();
			} else {
				AudioDataField.this.playerThread = new AudioPlayerThread();
				AudioDataField.this.playerThread.setMicrosecondPosition(this.microsecondPosition);
				AudioDataField.this.playerThread.start();
			}
			this.paused = false;
		}

		public void stop() {
			if ((AudioDataField.this.playerThread != null) && AudioDataField.this.playerThread.isAlive()) {
				AudioDataField.this.playerThread.stopPlayer();
				this.microsecondPosition = 0;
				AudioDataField.this.playerThread.setMicrosecondPosition(this.microsecondPosition);
				try {
					AudioDataField.this.playerThread.join();
				} catch (final Exception e) {
					AudioDataField.logger.trace(null, e);
				}
				AudioDataField.this.playerThread = null;
			}
		}

	};

	public AudioDataField(final Map parameters) {
		this.dataField = new JLabel();
		this.init(parameters);
		final JPanel jpButtonsPanel = new JPanel(new GridBagLayout());
		jpButtonsPanel.add(this.loadButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0));
		jpButtonsPanel.add(this.deleteButton,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0));
		this.add(jpButtonsPanel);

		final ImageIcon exploreIcon = ImageManager.getIcon(ImageManager.EXPLORE);
		if (exploreIcon != null) {
			this.loadButton.setIcon(exploreIcon);
		} else {
			this.loadButton.setText("..");
		}

		this.loadButton.setMargin(new Insets(0, 0, 0, 0));
		final ImageIcon deleteIcon = ImageManager.getIcon(ImageManager.DELETE);
		if (deleteIcon != null) {
			this.deleteButton.setIcon(deleteIcon);
		} else {
			this.deleteButton.setText("..");
		}
		this.deleteButton.setMargin(new Insets(0, 0, 0, 0));
		this.deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				AudioDataField.this.deleteDataButton();
			}
		});

		this.loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				// Open a dialog to select the audio files

				if (AudioDataField.this.chooser == null) {
					AudioDataField.this.chooser = new JFileChooser();
				}
				if (AudioDataField.this.lastPath != null) {
					AudioDataField.this.chooser.setCurrentDirectory(AudioDataField.this.lastPath);
				}
				final ExtensionFileFilter filter = new ExtensionFileFilter();
				filter.addExtension("wav");
				filter.addExtension("au");
				filter.addExtension("aiff");
				filter.setDescription("Archivos de sonido");
				AudioDataField.this.chooser.setAccessory(new ImagePreview(AudioDataField.this.chooser));
				AudioDataField.this.chooser.setFileFilter(filter);
				final int selection = AudioDataField.this.chooser.showOpenDialog(AudioDataField.this.parentFrame);
				if (selection == JFileChooser.APPROVE_OPTION) {
					// Update the image
					final File selectedFile = AudioDataField.this.chooser.getSelectedFile();
					AudioDataField.this.lastPath = selectedFile;
					if (!selectedFile.isDirectory()) {
						try {
							final FileInputStream fisFlux = new FileInputStream(selectedFile);
							final BufferedInputStream bi = new BufferedInputStream(fisFlux);
							final List bytes = new Vector();
							int by = -1;
							while ((by = bi.read()) != -1) {
								bytes.add(bytes.size(), new Byte((byte) by));
							}
							final byte[] bytesSoundNew = new byte[bytes.size()];
							for (int i = 0; i < bytesSoundNew.length; i++) {
								bytesSoundNew[i] = ((Byte) bytes.get(i)).byteValue();
							}
							final BytesBlock bytesBlock = new BytesBlock(bytesSoundNew, BytesBlock.NO_COMPRESSION);
							AudioDataField.this.setValueButton(bytesBlock);
							bi.close();
							fisFlux.close();
							final AudioFileFormat af = AudioSystem.getAudioFileFormat(selectedFile);
							AudioDataField.logger
							.debug("Open file " + selectedFile.toString() + " : " + af.getFormat().toString());
						} catch (final Exception e) {
							AudioDataField.logger.trace(null, e);
							MessageDialog.showMessage(AudioDataField.this.parentFrame, "Error reading file",
									JOptionPane.ERROR_MESSAGE, null);
						}
					}
				}
			}
		});
	}

	/**
	 * Method that configures the component. The parameter <code>Hashtable</code> contains the values
	 * set in the XML in which the <code>Form</code> is placed.
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *
	 * <tr>
	 * <td>borderbuttons</td>
	 * <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover, it is
	 * also allowed a border defined in #BorderManager</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The border for buttons in Form</td>
	 * </tr>
	 * <tr>
	 * <td>highlightbuttons</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Sets the highlight in button property when mouse is entered. See
	 * {@link AbstractButton#setContentAreaFilled(boolean))}. This parameter requires opaque='no'.</td>
	 * </tr>
	 * <tr>
	 * <td>opaquebuttons</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Data field opacity condition for Form buttons</td>
	 * </tr>
	 * </table>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		final ImageIcon soundIcon = ImageManager.getIcon(ImageManager.SOUND);
		if (soundIcon != null) {
			this.icon = soundIcon;
			// ((JLabel) this.dataField).setIcon(icon);
		} else {
			AudioDataField.logger.debug(this.getClass().toString() + " : images/sound.png icon cannot be found");
		}

		final ImageIcon emptySoundIcon = ImageManager.getIcon(ImageManager.EMPTY_SOUND);
		if (emptySoundIcon != null) {
			this.emptyIco = emptySoundIcon;
			((JLabel) this.dataField).setIcon(this.emptyIco);
		} else {
			AudioDataField.logger.debug(this.getClass().toString() + " : images/emptysound.gif icon cannot be found");
		}
		this.initFieldListener();

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
		this.changeButton(this.deleteButton, borderbuttons, opaquebuttons, listenerHighlightButtons);

	}

	protected void initFieldListener() {
		this.dataField.addMouseListener(this.fieldListener);
	}

	@Override
	protected void changeButton(final AbstractButton button, final boolean borderbuttons, final boolean opaquebuttons,
			final MouseListener listenerHighlightButtons) {
		if (button != null) {
			button.setFocusPainted(false);
			if (!borderbuttons) {
				button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			}
			if (!opaquebuttons) {
				button.setOpaque(false);
				button.setContentAreaFilled(false);
			}
			if (listenerHighlightButtons != null) {
				button.addMouseListener(listenerHighlightButtons);
			}
		}
	}

	protected void showAudioPlayer() {
		try {
			// Check if the new sound is already loaded
			if ((this.currentSound == null)
					|| ((this.currentSound != null) && !Arrays.equals(this.currentSound, this.bytesSound))) {
				this.currentSound = this.bytesSound;
				final ByteArrayInputStream bi = new ByteArrayInputStream(this.bytesSound);
				final AudioInputStream ain = AudioSystem.getAudioInputStream(bi);
				AudioDataField.logger.debug(ain.getFormat().toString());
				bi.close();
				this.audioPlayer.setSource(ain);
			}

		} catch (final Exception e) {
			AudioDataField.logger.error(null, e);
			this.audioPlayer.setSource(null);
			this.audioPlayer.status.setText("Unsupported Audio File");
		}
		this.audioPlayer.pack();
		this.audioPlayer.setVisible(true);
	}

	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.parentFrame = parentFrame;
	}

	/**
	 * Return the value as a BytesBlock Object
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		// Without compression
		final BytesBlock bytesBlock = new BytesBlock(this.bytesSound, BytesBlock.NO_COMPRESSION);
		return bytesBlock;
	}

	/**
	 * Value must be a BytesBlock Object
	 */
	@Override
	public void setValue(final Object value) {
		if (value instanceof BytesBlock) {
			final Object previousValue = this.getValue();
			this.bytesSound = null;
			this.bytesSound = ((BytesBlock) value).getBytes();
			this.valueSave = this.getValue();
			this.setEnabled(true);
			this.fireValueChanged(this.getValue(), previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} else if (value == null) {
			this.deleteData();
		} else {
			this.deleteData();
			AudioDataField.logger.debug(this.getClass().toString() + " value is not a valid BytesBlock object");
		}

	}

	public void setValueButton(final Object value) {
		if (value instanceof BytesBlock) {
			final Object previousValue = this.getValue();
			this.bytesSound = null;
			this.bytesSound = ((BytesBlock) value).getBytes();
			this.setEnabled(true);
			this.fireValueChanged(this.getValue(), previousValue, ValueEvent.USER_CHANGE);
		} else if (value == null) {
			this.deleteData();
		} else {
			this.deleteData();
			AudioDataField.logger.debug(this.getClass().toString() + " value is not a valid BytesBlock object");
		}
	}

	@Override
	public void deleteData() {
		final Object oPreviousValue = this.getValue();
		this.bytesSound = null;
		this.valueSave = null;
		this.currentSound = null;
		this.setEnabled(false);
		this.fireValueChanged(this.getValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	public void deleteDataButton() {
		final Object oPreviousValue = this.getValue();
		this.bytesSound = null;
		this.setEnabled(false);
		this.fireValueChanged(this.getValue(), oPreviousValue, ValueEvent.USER_CHANGE);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);

		if (this.getValue() != null) {
			if (enabled) {
				this.loadButton.setEnabled(true);
				this.deleteButton.setEnabled(true);
			} else {
				this.loadButton.setEnabled(false);
				this.deleteButton.setEnabled(false);
			}
		} else {
			super.setEnabled(false);
			this.loadButton.setEnabled(true);
			this.deleteButton.setEnabled(false);
		}
	}

	@Override
	public boolean isEmpty() {
		if (this.bytesSound == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.LONGVARBINARY;
	}

	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();

		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		} else if ((oValue == null) && (this.valueSave != null)) {
			return true;
		} else if ((oValue != null) && (this.valueSave == null)) {
			return true;
		} else {
			byte[] oValueBytes = null;
			byte[] valueSaveBytes = null;
			oValueBytes = ((BytesBlock) oValue).getBytes();
			valueSaveBytes = ((BytesBlock) this.valueSave).getBytes();

			final boolean value = Arrays.equals(oValueBytes, valueSaveBytes);
			if (!value) {
				return true;
			} else {
				return false;
			}
		}
	}

}
