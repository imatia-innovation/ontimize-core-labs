package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;

import com.dropbox.core.DbxEntry;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.button.Button;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;

public class DropboxUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	protected static Dimension PREFERRED_SIZE = new Dimension(500, 600);
	protected Form formDialog;

	public DropboxUploadableSelectionActionListener(Button button) throws Exception {
		super(button);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {

		if (this.formDialog == null) {
			this.formDialog = DropboxUploadableSelectionActionListener.getCurrentActiveForm().getFormManager()
					.getFormCopy("ontimize-dms-forms/formDropbox.form", IMDropbox.class.getName());
			this.formDialog.putInModalDialog(this.formDialog.getFormTitle(), this.button.getParentForm());
			this.formDialog.getJDialog().setSize(DropboxUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setPreferredSize(DropboxUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setMaximumSize(DropboxUploadableSelectionActionListener.PREFERRED_SIZE);
		}
		this.formDialog.deleteDataField("CLOUD_FILE");
		this.formDialog.getJDialog().setVisible(true);
		DbxEntry file = (DbxEntry) this.formDialog.getDataFieldValue("CLOUD_FILE");
		if (file != null) {
			try {
				return new DropboxDmsUploadable(file, null, file.name, file.asFile().numBytes);
			} catch (MalformedURLException error) {
				throw new DmsException(error);
			}
		}
		return null;
	}

	public static Form getCurrentActiveForm() {
		String gfAct = ((MainApplication) ApplicationManager.getApplication()).getActiveFMName();
		if (gfAct != null) {
			return ApplicationManager.getApplication().getFormManager(gfAct).getActiveForm();
		}
		return null;
	}
}
