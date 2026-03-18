package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxEntry;
import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.CardPanel;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.upload.cloud.CloudDrivePanel;
import com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudFileSelectionListener;

public class IMDropbox extends BasicInteractionManager implements ICloudFileSelectionListener<DbxEntry> {

	private static final Logger			logger	= LoggerFactory.getLogger(IMDropbox.class);

	@FormComponent(attr = "WEB")
	private LoginIntoDropboxComponent	moduleWeb;
	@FormComponent(attr = "cardpanel")
	private CardPanel					cardPanel;
	@FormComponent(attr = "remotepanel")
	private CloudDrivePanel				remotePanel;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.remotePanel.setSelectionListener(this);
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
		try {
			this.moduleWeb.showLoginScreen();
			this.showCardPanel("login");
		} catch (Exception e) {
			IMDropbox.logger.error(null, e);
		}
	}

	public void showRemoteDir() throws DmsException {
		this.remotePanel.startNavigation();
		this.cardPanel.show("remotedir");
	}

	public void showCardPanel(String panel) {
		this.cardPanel.show(panel);
	}

	@Override
	public void onFileSelected(DbxEntry file) {
		if (file != null) {
			this.managedForm.setDataFieldValue("CLOUD_FILE", file);
		}
		this.managedForm.getJDialog().setVisible(false);
	}
}
