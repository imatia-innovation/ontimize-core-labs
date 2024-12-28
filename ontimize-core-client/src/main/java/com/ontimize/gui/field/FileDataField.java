package com.ontimize.gui.field;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.gui.DataFile;
import com.ontimize.util.FileUtils;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a specific text data field for files. This data field permits insert files
 * into database table, but the Entity must have established the {@link TableEntity#FILE_COLUMNS}
 * property.
 * <p>
 *
 * @see {@link TableEntity#readProperties()}
 * @author Imatia Innovation
 */
public class FileDataField extends TextFieldDataField implements OpenDialog {

	private static final Logger logger = LoggerFactory.getLogger(FileDataField.class);

	private static String SELECTION_FILE_TIP = "datafield.select_file";

	private static String OPEN_FILE_TIP = "datafield.open_file_tip";

	private static String SAVE_FILE_TIP = "datafield.save_file_tip";

	/**
	 * The instance of selection button.
	 */
	protected FieldButton selectionButton = new FieldButton();

	/**
	 * The instance of open button.
	 */
	protected FieldButton openButton = new FieldButton();

	/**
	 * The save button. By default, null.
	 */
	protected FieldButton saveButton = null;

	/**
	 * A reference for a frame. By default, null.
	 */
	protected Frame parentFrame = null;

	/**
	 * The reference for a file. By default, null.
	 */
	protected DataFile file = null;

	/**
	 * A reference for chooser element. By default, null.
	 */
	protected JFileChooser chooser = null;

	/**
	 * A reference for chooser save element. By default, null.
	 */
	protected JFileChooser chooserSave = null;

	/**
	 * The limitation in bytes. By default, {@link Integer#MAX_VALUE}.
	 */
	protected int limitSizeBytes = Integer.MAX_VALUE;

	/**
	 * This class implements a <CODE>Thread</CODE> to manage the file operations (open, priority).
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class OpenAppThread extends Thread {

		private DataFile file = null;

		/**
		 * The class constructor. Fixes the file name and set priority to minimum.
		 * <p>
		 * @param file the file
		 */
		public OpenAppThread(final DataFile file) {
			this.file = file;
			this.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run() {
			try {
				final String userDirectory = System.getProperty("java.io.tmpdir");
				String sFileName = this.file.getFileName();
				try {
					Thread.sleep(50);
				} catch (final Exception e) {
					FileDataField.logger.trace(null, e);
				}
				sFileName = sFileName.replace(' ', '_');
				final File f = new File(userDirectory, sFileName);
				// delete hypothetical previous versions of same file in temp dir - since 5.2068EN-0.1
				if (f.exists()) {
					f.delete();
				}
				try {
					Thread.sleep(50);
				} catch (final Exception e) {
					FileDataField.logger.trace(null, e);
				}
				FileUtils.saveFile(f, this.file.getBytesBlock().getBytes(), true);
				try {
					Thread.sleep(50);
				} catch (final Exception e) {
					FileDataField.logger.trace(null, e);
				}
				com.ontimize.windows.office.WindowsUtils.openFile_Script(f);
			} catch (final Exception e) {
				FileDataField.logger.error(null, e);
				MessageDialog.showErrorMessage(null, "FileDataField.errorOpeningFile");
			}
		}

	}

	/**
	 * The reference for open thread. By default, null.
	 */
	protected OpenAppThread openThread = null;

	/**
	 * The class constructor. Calls to <code>super()</code> and inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public FileDataField(final Map parameters) {
		super();

		this.init(parameters);
		this.dataField.setEnabled(false);

	}

	/**
	 * Inits parameters and installs field buttons.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
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
	 *        <td>savebutton</td>
	 *        <td><i>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The save button presence.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>sizelimit</td>
	 *        <td><i></td>
	 *        <td><code>Integer.MAX_VALUE</code></td>
	 *        <td>no</td>
	 *        <td>The size limit for file.</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		final boolean createSaveButton = ParseUtils.getBoolean((String) parameters.get("savebutton"), true);
		if (createSaveButton) {
			this.saveButton = new FieldButton();
		}

		final Object lim = parameters.get("sizelimit");
		if (lim != null) {
			try {
				this.limitSizeBytes = Integer.parseInt(lim.toString());
			} catch (final Exception e) {
				FileDataField.logger.error("Error in parameter 'sizelimit': ", e);
			}
		}
		this.installButtons();

	}

	/**
	 * Gets icons, sets tooltip and margins and adds listeners for field buttons.
	 */
	protected void installButtons() {

		this.selectionButton.setIcon(ImageManager.getIcon(ImageManager.EXPLORE));
		this.openButton.setIcon(ImageManager.getIcon(ImageManager.OPEN_FILE));

		this.selectionButton.setMargin(new Insets(0, 0, 0, 0));
		this.openButton.setMargin(new Insets(0, 0, 0, 0));
		this.openButton.setToolTipText(FileDataField.OPEN_FILE_TIP);
		this.selectionButton.setToolTipText(FileDataField.SELECTION_FILE_TIP);
		this.selectionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FileDataField.this.selectionFile();
			}
		});

		this.openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FileDataField.this.openFile();
			}
		});

		super.add(this.selectionButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		super.add(this.openButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		if (this.saveButton != null) {
			this.saveButton.setIcon(ImageManager.getIcon(ImageManager.SAVE_FILE));
			this.saveButton.setMargin(new Insets(0, 0, 0, 0));
			this.saveButton.setToolTipText(FileDataField.SAVE_FILE_TIP);
			this.saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					FileDataField.this.saveFile();
				}
			});
			super.add(this.saveButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		if (this.labelPosition != SwingConstants.LEFT) {
			this.validateComponentPositions();
		}

	}

	/**
	 * Implements the selection field operation, checking the size limit of file.
	 */
	public void selectionFile() {
		final Object oPreviousValue = this.getValue();

		if (this.chooser == null) {
			this.chooser = new JFileChooser();
		}

		this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		final int selection = this.chooser.showOpenDialog(this.parentFrame);
		if (selection == JFileChooser.APPROVE_OPTION) {
			// Update the image
			final File selectedFile = this.chooser.getSelectedFile();
			if (selectedFile.length() >= this.limitSizeBytes) {
				this.parentForm.message("M_FILE_SIZE_TOO_LARGE", Form.ERROR_MESSAGE, "Max: " + this.limitSizeBytes);
				return;
			}

			if (!selectedFile.isDirectory()) {

				try {
					this.file = new DataFile(selectedFile);
					((JTextComponent) this.dataField).setText(this.file.getFileName());
					if (this.isEnabled()) {
						this.openButton.setEnabled(true);
					}
					this.fireValueChanged(this.getValue(), oPreviousValue, ValueEvent.USER_CHANGE);
				} catch (final Exception e) {
					FileDataField.logger.error(null, e);
					MessageDialog.showMessage(this.parentFrame, "Error loading file", JOptionPane.ERROR_MESSAGE, null);
					this.openButton.setEnabled(false);
				}
			}
		}
	}

	public JFileChooser getChooser() {
		return this.chooser;
	}

	public JFileChooser getChooserSave() {
		return this.chooserSave;
	}

	/**
	 * Opens the file.
	 * <p>
	 * @param file the file
	 * @throws Exception when a <CODE>Thread</CODE> Exception occurs
	 */
	public static void openFile(final DataFile file) throws Exception {
		final String userDirectory = System.getProperty("java.io.tmpdir");
		String sFileName = file.getFileName();
		try {
			Thread.sleep(50);
		} catch (final Exception e) {
			FileDataField.logger.trace(null, e);
		}
		sFileName = sFileName.replace(' ', '_');
		final File f = new File(userDirectory, sFileName);
		if (!f.exists() || (f.length() != file.getBytesBlock().getBytes().length)) {
			try {
				Thread.sleep(50);
			} catch (final Exception e) {
				FileDataField.logger.error(null, e);
			}
			FileUtils.saveFile(f, file.getBytesBlock().getBytes(), true);
			try {
				Thread.sleep(50);
			} catch (final Exception e) {
				FileDataField.logger.error(null, e);
			}
			com.ontimize.windows.office.WindowsUtils.openFile_Script(f);
		} else {
			com.ontimize.windows.office.WindowsUtils.openFile_Script(f);
		}
	}

	/**
	 * Opens the file specified in <code>file</code>
	 */
	public void openFile() {
		if (this.file == null) {
			this.parentForm.message("FileDataField.errorOpeningFileNotFileFound", Form.WARNING_MESSAGE);
			return;
		}
		// Save the file in the disk
		try {
			if ((this.openThread != null) && this.openThread.isAlive()) {
				this.parentForm.message("FileDataField.waitOpeningFile", Form.WARNING_MESSAGE);
				return;
			} else {
				this.openThread = new OpenAppThread(this.file);
				this.openThread.start();
			}
		} catch (final Exception e) {
			this.parentForm.message(e.getMessage(), Form.ERROR_MESSAGE);
			FileDataField.logger.error(null, e);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.dataField.setEnabled(false);
		this.selectionButton.setEnabled(enabled);
		if (enabled) {
			if (!this.isEmpty()) {
				this.openButton.setEnabled(true);
				if (this.saveButton != null) {
					this.saveButton.setEnabled(true);
				}
			} else {
				this.openButton.setEnabled(false);
				if (this.saveButton != null) {
					this.saveButton.setEnabled(false);
				}
			}
		} else {
			this.openButton.setEnabled(false);
			if (this.saveButton != null) {
				this.saveButton.setEnabled(false);
			}
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		} else {
			return this.file;
		}
	}

	@Override
	public boolean isEmpty() {
		if (this.file == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void deleteData() {
		final Object oPreviousValue = this.getValue();
		this.file = null;
		this.valueSave = null;
		((JTextComponent) this.dataField).setText("");
		this.openButton.setEnabled(false);
		this.fireValueChanged(this.getValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	@Override
	public void setValue(final Object o) {
		if (o == null) {
			this.deleteData();
		} else if (o instanceof DataFile) {
			final Object previousValue = this.getValue();
			this.file = (DataFile) o;
			// Since 5.2066EN-0.1 ) value events are disabled because setText
			// should not fire event in FileDataField
			final boolean currentFireValueEvents = this.fireValueEvents;
			this.fireValueEvents = false;
			((JTextComponent) this.dataField).setText(this.file.getFileName());
			this.fireValueEvents = currentFireValueEvents;
			if (this.isEnabled()) {
				this.openButton.setEnabled(true);
			}
			this.valueSave = this.file;
			this.fireValueChanged(this.getValue(), previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} else {
			FileDataField.logger.debug(this.getClass().toString() + " value is not a file object");
			this.deleteData();
		}
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.LONGVARBINARY;
	}

	@Override
	public void setParentFrame(final Frame frame) {
		this.parentFrame = frame;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		super.setResourceBundle(resource);
		if (resource != null) {
			this.selectionButton
			.setToolTipText(ApplicationManager.getTranslation(FileDataField.SELECTION_FILE_TIP, resource));
			this.openButton.setToolTipText(ApplicationManager.getTranslation(FileDataField.OPEN_FILE_TIP, resource));
			if (this.saveButton != null) {
				this.saveButton
				.setToolTipText(ApplicationManager.getTranslation(FileDataField.SAVE_FILE_TIP, resource));
			}
		}
	}

	/**
	 * Implements the saving operation for file.
	 */
	public void saveFile() {
		if (this.isEmpty()) {
			return;
		}
		if (this.file.getBytesBlock() == null) {
			return;
		}

		if (this.chooserSave == null) {
			this.chooserSave = new JFileChooser();
			this.chooserSave.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY,
					new PropertyChangeListener() {

				@Override
				public void propertyChange(final PropertyChangeEvent e) {
					if (e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
						FileDataField.this.chooserSave
						.setSelectedFile(new File(FileDataField.this.chooserSave.getCurrentDirectory(),
								FileDataField.this.file.getFileName()));
					}
				}
			});
		}

		final int selection = this.chooserSave.showSaveDialog(this.parentFrame);
		if (selection == JFileChooser.APPROVE_OPTION) {
			// Update the image
			File selectedFile = this.chooserSave.getSelectedFile();

			if (selectedFile.isDirectory()) {
				selectedFile = new File(selectedFile, this.file.getFileName());
			}
			if (selectedFile.exists()) {
				final boolean resp = this.parentForm.question("fielddata.overwrite_existing_file");
				if (!resp) {
					return;
				}
			}
			try {
				FileUtils.saveFile(selectedFile, this.file.getBytesBlock().getBytes());
			} catch (final Exception e) {
				FileDataField.logger.error(null, e);
				MessageDialog.showMessage(this.parentFrame, "Error saving file", JOptionPane.ERROR_MESSAGE, null);
			}
		}
	}

	/**
	 * Provides access to file.
	 * @return A {@link DataFile} instance.
	 * @since 5.2070EN-0.2
	 */
	public DataFile getFile() {
		return this.file;
	}

}
