package com.ontimize.jee.desktopclient.dms.upload.disk;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.attachment.EJFile;
import com.ontimize.gui.preferences.ApplicationPreferences;

/**
 * The Class EJFileSaveLastDirectory.
 */
public class EJFileSaveLastDirectory extends EJFile {

	/** The Constant serialVersionUID. */
	private static final long		serialVersionUID	= 1L;

	/** The Constant KEY_LAST_DIRECTORY. */
	protected static final String	KEY_LAST_DIRECTORY	= "lastDirectoryJFileChooser";

	/** The last directory. */
	protected static File			lastDirectory		= null;

	/**
	 * Gets the last directory.
	 *
	 * @param currentDirectory
	 *            the current directory
	 * @return the last directory
	 */
	private static File getLastDirectory(File currentDirectory) {
		if (EJFileSaveLastDirectory.lastDirectory != null) {
			return EJFileSaveLastDirectory.lastDirectory;
		}
		ApplicationPreferences preferences = ApplicationManager.getApplication().getPreferences();
		if (preferences.getPreference(null, EJFileSaveLastDirectory.KEY_LAST_DIRECTORY) != null) {
			EJFileSaveLastDirectory.lastDirectory = new File(preferences.getPreference(null, EJFileSaveLastDirectory.KEY_LAST_DIRECTORY));
		}
		return EJFileSaveLastDirectory.lastDirectory != null ? EJFileSaveLastDirectory.lastDirectory : currentDirectory;
	}

	/**
	 * Update last directory.
	 *
	 * @param parent
	 *            the parent
	 */
	private static void updateLastDirectory(String parent) {
		ApplicationManager.getApplication().getPreferences().setPreference(null, EJFileSaveLastDirectory.KEY_LAST_DIRECTORY, parent);
		EJFileSaveLastDirectory.lastDirectory = new File(parent);
	}

	/**
	 * Instantiates a new EJ file save last directory.
	 */
	public EJFileSaveLastDirectory() {
		super();

	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JFileChooser#showOpenDialog(java.awt.Component)
	 */
	@Override
	public int showOpenDialog(Component parent) throws HeadlessException {
		this.setCurrentDirectory(EJFileSaveLastDirectory.getLastDirectory(this.getCurrentDirectory()));
		int returnValue = super.showOpenDialog(parent);
		if ((returnValue != JFileChooser.ERROR_OPTION) && (this.getSelectedFile() != null) && !this.getSelectedFile().getPath().isEmpty()) {
			EJFileSaveLastDirectory.updateLastDirectory(this.getSelectedFile().getParent());
		}
		return returnValue;
	}

}
