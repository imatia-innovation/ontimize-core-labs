package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.CardPanel;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.upload.cloud.CloudDrivePanel;
import com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudFileSelectionListener;

public class IMGoogleDrive extends BasicInteractionManager implements ICloudFileSelectionListener<GoogleFile> {

	private static final long				serialVersionUID	= 1L;

	private static final Logger				logger				= LoggerFactory.getLogger(IMGoogleDrive.class);

	@FormComponent(attr = "WEB")
	private LoginIntoGoogleDriveComponent	moduleWeb;
	@FormComponent(attr = "cardpanel")
	private CardPanel						cp;
	@FormComponent(attr = "remotepanel")
	private CloudDrivePanel<GoogleFile>		remotePanel;

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
		} catch (Exception error) {
			IMGoogleDrive.logger.error(null, error);
		}
	}

	public void showRemoteDir() throws DmsException {
		this.remotePanel.startNavigation();
		this.cp.show("remotedir");
	}

	public void showCardPanel(String panel) {
		this.cp.show(panel);
	}

	@Override
	public void onFileSelected(GoogleFile file) {
		if (file != null) {
			this.managedForm.setDataFieldValue("CLOUD_FILE", file);
		}
		this.managedForm.getJDialog().setVisible(false);
	}

}
