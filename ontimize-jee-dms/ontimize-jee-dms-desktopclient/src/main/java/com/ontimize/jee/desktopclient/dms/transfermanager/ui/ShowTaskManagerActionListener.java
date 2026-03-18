package com.ontimize.jee.desktopclient.dms.transfermanager.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.Button;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.components.taskmanager.TaskManagerGUI;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.ITransferQueueListener;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.TransferQueueChangedEvent;
import com.ontimize.jee.desktopclient.dms.util.AnimateIconButton;

public class ShowTaskManagerActionListener implements ITransferQueueListener, ActionListener {

	private static final Logger		logger							= LoggerFactory.getLogger(ShowTaskManagerActionListener.class);
	
	protected static final String	ICO_DOWNLOAD_MANAGER_DEFAULT	= "ontimize-dms-images/download_manager_22x22.png";
	protected static final String	GIF_DOWNLOAD_MANAGER_PROGRESS	= "ontimize-dms-images/roller_22x22.gif";
	
	/** Parameter to limit button enable when interaction manager is in INSERT_MODE. By default false. */
	public static final String	PARAM_ENABLE_FIM_INSERT			= "enable.insert";
	/** Parameter to limit button enable when interaction manager is in QUERY_MODE. By default false. */
	public static final String	PARAM_ENABLE_FIM_QUERY			= "enable.query";
	/** Parameter to limit button enable when interaction manager is in QUERY_INSERT_MODE. By default false. */
	public static final String	PARAM_ENABLE_FIM_QUERY_INSERT	= "enable.queryinsert";
	/** Parameter to limit button enable when interaction manager is in UPDATE_MODE. By default false. */
	public static final String	PARAM_ENABLE_FIM_UPDATE			= "enable.update";
	
	protected Button button;
	
	/** Limit button enable when interaction manager is in INSERT_MODE */
	protected boolean			isInsertModeValidToEnable;
	/** Limit button enable when interaction manager is in QUERY_MODE */
	protected boolean			isQueryModeValidToEnable;
	/** Limit button enable when interaction manager is in QUERY_INSERT_MODE */
	protected boolean			isQueryInsertModeValidToEnable;
	/** Limit button enable when interaction manager is in UPDATE_MODE */
	protected boolean			isUpdateModeValidToEnable;

	protected void init(Map<?, ?> params) throws Exception {
		this.isInsertModeValidToEnable = ParseUtilsExtended.getBoolean((String) params.get(ShowTaskManagerActionListener.PARAM_ENABLE_FIM_INSERT), true);
		this.isQueryModeValidToEnable = ParseUtilsExtended.getBoolean((String) params.get(ShowTaskManagerActionListener.PARAM_ENABLE_FIM_QUERY), true);
		this.isQueryInsertModeValidToEnable = ParseUtilsExtended.getBoolean((String) params.get(ShowTaskManagerActionListener.PARAM_ENABLE_FIM_QUERY_INSERT), true);
		this.isUpdateModeValidToEnable = ParseUtilsExtended.getBoolean((String) params.get(ShowTaskManagerActionListener.PARAM_ENABLE_FIM_UPDATE), true);
		DmsTransfererManagerFactory.getInstance().addTransferQueueListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() instanceof Button) {
				this.button = (Button) e.getSource();
			}
			this.toggleDialog();
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, ShowTaskManagerActionListener.logger);
		}
	}

	public void toggleDialog() {
		TaskManagerGUI.getInstance().showWindow();
	}

	@Override
	public void onTransferQueueChanged(TransferQueueChangedEvent transferEvent) {
		if (transferEvent.hasPendingTransfers()) {
			AnimateIconButton.animateGIF(this.button, ShowTaskManagerActionListener.GIF_DOWNLOAD_MANAGER_PROGRESS);
		} else {
			AnimateIconButton.animate(this.button, ShowTaskManagerActionListener.ICO_DOWNLOAD_MANAGER_DEFAULT, null, 10, 500);
		}

	}
}
