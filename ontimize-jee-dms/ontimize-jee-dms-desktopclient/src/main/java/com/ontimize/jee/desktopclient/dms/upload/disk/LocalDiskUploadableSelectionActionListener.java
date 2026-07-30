package com.ontimize.jee.desktopclient.dms.upload.disk;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.attachment.JDescriptionPanel;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class LocalDiskUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	protected EJFileSaveLastDirectory fileChooser;

	public LocalDiskUploadableSelectionActionListener() throws Exception {
		super();
	}

	public LocalDiskUploadableSelectionActionListener(final AbstractButton button, final IUFormComponent formComponent, final Map params) throws Exception {
		super(button, formComponent, params);
	}

	public LocalDiskUploadableSelectionActionListener(final Map params) throws Exception {
		super(params);
	}

	public LocalDiskUploadableSelectionActionListener(final UButton button, final Map params) throws Exception {
		super(button, params);
	}


	/**
	 * Get <code>File</code> in disk.
	 *
	 * @return <code>File</code> with the selected document
	 * @throws IOException
	 */

	@Override
	protected AbstractDmsUploadable acquireTransferable(final ActionEvent ev) throws DmsException {
		if (this.fileChooser == null) {
			this.fileChooser = new EJFileSaveLastDirectory();
			this.fileChooser.setMultiSelectionEnabled(false);
			this.fileChooser.setPanel(new JDescriptionPanel(this.getForm().getResourceBundle()));
			this.fileChooser.setAccessory(this.fileChooser.getPanel());
			this.fileChooser.setLocale(ApplicationManager.getLocale());
		}
		this.fileChooser.getPanel().clearValues();
		this.fileChooser.setSelectedFile(null);
		if ((JFileChooser.APPROVE_OPTION == this.fileChooser.showOpenDialog(this.getForm())) && (this.fileChooser.getSelectedFile() != null)) {
			final String description = this.fileChooser.getPanel().getDescription();
			final Path file = this.fileChooser.getSelectedFile().toPath();
			if (file != null) {
				return new LocalDiskDmsUploadable(file, description);
			} else {
				return null;
			}
		}
		return null;
	}
}
