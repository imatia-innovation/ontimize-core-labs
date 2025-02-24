package com.ontimize.jee.desktopclient.dms.upload.web;

import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;

public class IMWebAddURL extends BasicInteractionManager {

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
	}

}
