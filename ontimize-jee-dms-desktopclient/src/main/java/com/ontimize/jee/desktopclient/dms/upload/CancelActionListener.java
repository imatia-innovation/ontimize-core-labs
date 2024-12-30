package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ontimize.gui.IDetailForm;
import com.ontimize.gui.button.Button;

public class CancelActionListener implements ActionListener {

	protected Button button;

	public CancelActionListener(Button button) {
		super();
		this.button = button;
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		if (ev.getSource() instanceof Button) {
			this.button = (Button) ev.getSource();
		}
		if (this.button.getParentForm() != null) {
			IDetailForm detailComponent = this.button.getParentForm().getDetailComponent();
			if (detailComponent != null) {
				detailComponent.hideDetailForm();
			} else if (this.button.getParentForm().getJDialog() != null) {
				this.button.getParentForm().getJDialog().setVisible(false);
			}
		}
	}
}
