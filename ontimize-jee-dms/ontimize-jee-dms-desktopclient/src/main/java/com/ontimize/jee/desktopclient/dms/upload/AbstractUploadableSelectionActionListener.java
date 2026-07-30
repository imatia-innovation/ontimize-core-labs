package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public abstract class AbstractUploadableSelectionActionListener extends AbstractActionListenerButton {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUploadableSelectionActionListener.class);

	public AbstractUploadableSelectionActionListener() throws Exception {
		super();
	}

	public AbstractUploadableSelectionActionListener(final AbstractButton button, final IUFormComponent formComponent, final Map params) throws Exception {
		super(button, formComponent, params);
	}

	public AbstractUploadableSelectionActionListener(final Map params) throws Exception {
		super(params);
	}

	public AbstractUploadableSelectionActionListener(final UButton button, final Map params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		try {
			final AbstractDmsUploadable transferable = this.acquireTransferable(ev);
			this.getForm().setDataFieldValue(OpenUploadableChooserActionListener.TRANSFERABLE, transferable);
			if (transferable != null) {
				this.getForm().getJDialog().setVisible(false);
			}
		} catch (final Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, AbstractUploadableSelectionActionListener.LOGGER);
		}
	}

	protected abstract AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException;

}
