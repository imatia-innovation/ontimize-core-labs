package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractButton;

import com.ontimize.gui.button.Button;
import com.ontimize.jee.desktopclient.dms.upload.IMMultipleFiles;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class ShowCloudPanelActionListener extends AbstractActionListenerButton {

	public ShowCloudPanelActionListener() throws Exception {
		super();
	}

	public ShowCloudPanelActionListener(final AbstractButton button, final IUFormComponent formComponent, final Map params) throws Exception {
		super(button, formComponent, params);
	}

	public ShowCloudPanelActionListener(final Map params) throws Exception {
		super(params);
	}

	public ShowCloudPanelActionListener(final UButton button, final Map params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		if (e.getSource() instanceof Button) {
			((IMMultipleFiles) this.getForm().getInteractionManager()).showCardPanel("hostingpanel");
		}
	}
}
