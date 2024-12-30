package com.ontimize.jee.desktopclient.dms.upload.web;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ontimize.gui.button.Button;
import com.ontimize.jee.desktopclient.dms.upload.IMMultipleFiles;

public class ShowWebPanelActionListener implements ActionListener{

	public ShowWebPanelActionListener() throws Exception {
		super();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof Button) {
			Button b = (Button) e.getSource();
			b.getParentForm().deleteDataField("URL");
			b.getParentForm().deleteDataField("URL_DESCRIPTION");
			((IMMultipleFiles) b.getParentForm().getInteractionManager()).showCardPanel("webpanel");
		}
	}
}
