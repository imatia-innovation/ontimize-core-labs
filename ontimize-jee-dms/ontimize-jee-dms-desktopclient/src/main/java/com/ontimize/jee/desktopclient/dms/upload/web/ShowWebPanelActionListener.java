package com.ontimize.jee.desktopclient.dms.upload.web;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractButton;

import com.ontimize.gui.button.Button;
import com.ontimize.jee.desktopclient.dms.upload.IMMultipleFiles;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class ShowWebPanelActionListener extends AbstractActionListenerButton {

	public ShowWebPanelActionListener() throws Exception {
		super();
	}

	public ShowWebPanelActionListener(final AbstractButton button, final IUFormComponent formComponent, final Map params) throws Exception {
		super(button, formComponent, params);
	}

	public ShowWebPanelActionListener(final Map params) throws Exception {
		super(params);
	}

	public ShowWebPanelActionListener(final UButton button, final Map params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() instanceof Button) {
			final Button b = (Button) e.getSource();
			b.getParentForm().deleteDataField("URL");
			b.getParentForm().deleteDataField("URL_DESCRIPTION");
			((IMMultipleFiles) b.getParentForm().getInteractionManager()).showCardPanel("webpanel");
		}
	}
}
