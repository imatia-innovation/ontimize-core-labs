package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.Button;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;

public abstract class AbstractUploadableSelectionActionListener implements ActionListener {

	protected Button button;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUploadableSelectionActionListener.class);

	public AbstractUploadableSelectionActionListener(Button button) throws Exception {
		super();
		this.button = button;
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		try {
			if (ev.getSource() instanceof Button) {
				this.button = (Button) ev.getSource();
			}
			AbstractDmsUploadable transferable = this.acquireTransferable(ev);
			this.button.getParentForm().setDataFieldValue(OpenUploadableChooserActionListener.TRANSFERABLE,
					transferable);
			if (transferable != null) {
				this.button.getParentForm().getJDialog().setVisible(false);
			}
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex,
					AbstractUploadableSelectionActionListener.LOGGER);
		}
	}

	protected abstract AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException;

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

}
