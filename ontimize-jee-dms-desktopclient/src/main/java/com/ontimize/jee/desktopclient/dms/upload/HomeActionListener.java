package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ontimize.gui.button.Button;

public class HomeActionListener implements ActionListener {

	protected Button button;
	
	public HomeActionListener(Button button) throws Exception {
		super();
		this.button = button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof Button) {
			this.button = (Button) e.getSource();
			((IMMultipleFiles) this.button.getParentForm().getInteractionManager()).showCardPanel("mainpanel");
		}
	}

}
