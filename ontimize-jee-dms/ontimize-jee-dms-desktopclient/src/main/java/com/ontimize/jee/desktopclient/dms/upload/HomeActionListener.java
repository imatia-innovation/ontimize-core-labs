package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.event.ActionEvent;
import java.util.Map;

import com.ontimize.gui.button.Button;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class HomeActionListener extends AbstractActionListenerButton {

	public HomeActionListener(final UButton button, final Map params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() instanceof Button) {
			this.button = (Button) e.getSource();
			((IMMultipleFiles) this.getForm().getInteractionManager()).showCardPanel("mainpanel");
		}
	}

}
