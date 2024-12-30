package com.ontimize.jee.desktopclient.dms.transfermanager.events;

/**
 * Interface to show information about status of the DownloadManager.
 *
 * @see ITransferMangerStatusEvent
 */
public interface ITransferQueueListener {

	/**
	 */
	public void onTransferQueueChanged(TransferQueueChangedEvent transferEvent);
}
