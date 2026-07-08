package com.ontimize.jee.desktopclient.dms.upload.scanner;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.ontimize.jee.desktopclient.dms.upload.disk.LocalDiskDmsUploadable;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.capture.UAcquisitionDataField;


public class ScannerUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	private UAcquisitionDataField acquisitionDataField;

	public ScannerUploadableSelectionActionListener() throws Exception {
		super();
	}

	public ScannerUploadableSelectionActionListener(final AbstractButton button, final IUFormComponent formComponent, final Map params) throws Exception {
		super(button, formComponent, params);
	}

	public ScannerUploadableSelectionActionListener(final Map params) throws Exception {
		super(params);
	}

	public ScannerUploadableSelectionActionListener(final UButton button, final Map params) throws Exception {
		super(button, params);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(final ActionEvent ev) throws DmsException {
		if (this.acquisitionDataField == null) {
			final Map<String, Object> params = new HashMap<>();
			params.put("opengenerated", "false");
			params.put("closeOnGenerate", "true");
			try {
				this.acquisitionDataField = new UAcquisitionDataField(params);
			} catch (final Exception error) {
				throw new DmsException(error);
			} catch (final Error error) {
				throw new DmsException("dms.e_invalid_scanner_library", error);
			}
		}
		final List<File> resFile = this.acquisitionDataField.showAcquireDialog(this.getButton(), "dms.scanner_acquisition", this.getResourceBundle());
		if ((resFile != null) && !resFile.isEmpty()) {
			final List<Path> res = FileTools.toPath(resFile);
			String fileName = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.fileNameInput"));
			final String description = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.descriptioninput"));
			final LocalDiskDmsUploadable uploadable = new LocalDiskDmsUploadable(res.get(0), description);
			if (!fileName.endsWith(".pdf")) {
				fileName = fileName + ".pdf";
			}
			uploadable.setName(fileName);
			return uploadable;
		}
		return null;
	}

	@Override
	public ResourceBundle getResourceBundle() {
		final ResourceBundle resourceBundle = this.getForm().getResourceBundle();
		if (resourceBundle == null) {
			return this.getForm().getFormManager().getResourceBundle();
		}
		return ApplicationManager.getApplicationBundle();
	}
}
