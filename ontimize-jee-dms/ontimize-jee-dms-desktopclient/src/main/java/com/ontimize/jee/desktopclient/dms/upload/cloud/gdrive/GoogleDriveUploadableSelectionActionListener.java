package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.util.Map;

import javax.swing.AbstractButton;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.MainApplication;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class GoogleDriveUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	protected static Dimension PREFERRED_SIZE = new Dimension(500, 600);
	protected Form formDialog;

	public GoogleDriveUploadableSelectionActionListener() throws Exception {
		super();
	}

	public GoogleDriveUploadableSelectionActionListener(final AbstractButton button, final IUFormComponent formComponent, final Map params) throws Exception {
		super(button, formComponent, params);
	}

	public GoogleDriveUploadableSelectionActionListener(final Map params) throws Exception {
		super(params);
	}

	public GoogleDriveUploadableSelectionActionListener(final UButton button, final Map params) throws Exception {
		super(button, params);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(final ActionEvent ev) throws DmsException {

		if (this.formDialog == null) {
			this.formDialog = GoogleDriveUploadableSelectionActionListener.getCurrentActiveForm().getFormManager()
					.getFormCopy("ontimize-dms-forms/formGoogleDrive.form", IMGoogleDrive.class.getName());
			this.formDialog.putInModalDialog(this.formDialog.getFormTitle(), this.getForm());
			this.formDialog.getJDialog().setSize(GoogleDriveUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setPreferredSize(GoogleDriveUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setMaximumSize(GoogleDriveUploadableSelectionActionListener.PREFERRED_SIZE);
		}
		this.formDialog.deleteDataField("CLOUD_FILE");
		this.formDialog.getJDialog().setVisible(true);
		final GoogleFile file = (GoogleFile) this.formDialog.getDataFieldValue("CLOUD_FILE");
		if (file != null) {
			try {
				return new GoogleDriveDmsUploadable(file, file.getDescription(), file.getTitle(), file.getFileSize());
			} catch (final MalformedURLException error) {
				throw new DmsException(error);
			}
		}
		return null;
	}

	public static Form getCurrentActiveForm() {
		final String gfAct = ((MainApplication) ApplicationManager.getApplication()).getActiveFMName();
		if (gfAct != null) {
			return ApplicationManager.getApplication().getFormManager(gfAct).getActiveForm();
		}
		return null;
	}

}
