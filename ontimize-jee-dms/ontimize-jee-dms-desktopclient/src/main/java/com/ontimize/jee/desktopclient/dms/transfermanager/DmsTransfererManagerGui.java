package com.ontimize.jee.desktopclient.dms.transfermanager;

import com.ontimize.jee.desktopclient.components.taskmanager.TaskManagerGUI;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.ITransferQueueListener;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.TransferQueueChangedEvent;

/**
 *
 * This class implements all funcionality to manage downloaders. It saves a list with all downloaders and methods to know status and list all of them.
 *
 */
public class DmsTransfererManagerGui extends DmsTransfererManagerDefault {

	/** Protected constructor */
	protected DmsTransfererManagerGui() {
		super();
	}

	@Override
	public void init() {
		super.init();
		// Start GUI
		TaskManagerGUI.getInstance();

		DmsTransfererManagerFactory.getInstance().addTransferQueueListener(new ITransferQueueListener() {

			@Override
			public void onTransferQueueChanged(TransferQueueChangedEvent transferEvent) {
				if (transferEvent.getAddedTransferable() != null) {
					TaskManagerGUI.getInstance().getTaskTable().getTaskModel().addTask(new DmsTransferTask(transferEvent.getAddedTransferable()));
					TaskManagerGUI.getInstance().showWindow();
				}
			}
		});
	}
}
