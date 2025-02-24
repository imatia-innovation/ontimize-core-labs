package com.ontimize.util.filechooser;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFileChooser extends JFileChooser {

	private static final Logger logger = LoggerFactory.getLogger(CustomFileChooser.class);

	public CustomFileChooser() {
	}

	protected boolean isLNK(final File f) {
		if ((f == null) || f.isDirectory()) {
			return false;
		} else if (f.getName().toLowerCase().endsWith(".lnk")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void approveSelection() {
		try {
			if ((File.separatorChar == '\\') && (this.getFileSelectionMode() == JFileChooser.FILES_ONLY)) {

				File[] selectedFiles = this.getSelectedFiles();
				File selectedFile = null;
				if ((selectedFiles.length == 0) && (this.getSelectedFile() != null)) {
					selectedFiles = new File[] { this.getSelectedFile() };
				}
				if (selectedFiles.length == 1) {
					selectedFile = selectedFiles[0];
				} else {
					super.approveSelection();
					return;
				}
				if (selectedFile.getPath().endsWith(".lnk")) {
					File linkedTo = null;
					try {
						linkedTo = resolveShortcut(selectedFile);
					} catch (final FileNotFoundException ignore) {
						CustomFileChooser.logger.debug("File not found", ignore);
					}
					if (linkedTo != null) {
						if (linkedTo.isDirectory()) {
							this.setCurrentDirectory(linkedTo);
							return;
						} else if (!linkedTo.equals(selectedFile)) {
							this.setCurrentDirectory(linkedTo.getParentFile());
							this.setSelectedFile(linkedTo);
							this.setSelectedFiles(new File[] { linkedTo });
						}
					}
				}
			}
			super.approveSelection();
		} catch (final Exception err) {
			CustomFileChooser.logger.error(null, err);
			super.approveSelection();
		}
	}

	private File resolveShortcut(final File shortcut) throws FileNotFoundException {
		// try {
		//// final String resolvedPath =
		// com.sun.jna.platform.win32.ShellFolder.getShortcutTarget(shortcut.getAbsolutePath());
		// if (resolvedPath != null && !resolvedPath.isEmpty()) {
		// return new File(resolvedPath);
		// }
		// } catch (final Exception err) {
		// CustomFileChooser.logger.error(null, err);
		// }
		throw new FileNotFoundException(shortcut.getAbsolutePath());
	}

}
