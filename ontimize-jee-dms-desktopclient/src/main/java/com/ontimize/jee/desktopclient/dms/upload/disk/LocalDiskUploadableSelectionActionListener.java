package com.ontimize.jee.desktopclient.dms.upload.disk;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFileChooser;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.attachment.JDescriptionPanel;
import com.ontimize.gui.button.Button;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;

public class LocalDiskUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	protected EJFileSaveLastDirectory fileChooser;

	public LocalDiskUploadableSelectionActionListener(Button button) throws Exception {
		super(button);
	}


	/**
	 * Get <code>File</code> in disk.
	 *
	 * @return <code>File</code> with the selected document
	 * @throws IOException
	 */

	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {
		if (this.fileChooser == null) {
			this.fileChooser = new EJFileSaveLastDirectory();
			this.fileChooser.setMultiSelectionEnabled(false);
			this.fileChooser.setPanel(new JDescriptionPanel(this.button.getParentForm().getResourceBundle()));
			this.fileChooser.setAccessory(this.fileChooser.getPanel());
			this.fileChooser.setLocale(ApplicationManager.getLocale());
		}
		this.fileChooser.getPanel().clearValues();
		this.fileChooser.setSelectedFile(null);
		if ((JFileChooser.APPROVE_OPTION == this.fileChooser.showOpenDialog(this.button.getParentForm())) && (this.fileChooser.getSelectedFile() != null)) {
			String description = this.fileChooser.getPanel().getDescription();
			Path file = this.fileChooser.getSelectedFile().toPath();
			if (file != null) {
				return new LocalDiskDmsUploadable(file, description);
			} else {
				return null;
			}
		}
		return null;
	}
}
