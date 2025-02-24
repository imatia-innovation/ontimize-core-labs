package com.ontimize.jee.desktopclient.dms.upload;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.CardPanel;
import com.ontimize.gui.manager.IFormManager;

public class IMMultipleFiles extends BasicInteractionManager {

	@FormComponent(attr = "cardpanel")
	private CardPanel cp;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.showCardPanel("mainpanel");
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
	}

	public void showCardPanel(String panel) {
		this.cp.show(panel);
		this.managedForm.getButton("HOME").setVisible(!"mainpanel".equals(panel));
	}
}
