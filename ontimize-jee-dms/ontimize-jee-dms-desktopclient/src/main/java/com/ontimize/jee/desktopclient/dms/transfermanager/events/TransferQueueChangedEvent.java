package com.ontimize.jee.desktopclient.dms.transfermanager.events;

import java.util.List;

import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable;

/**
 * The Class TransferChangedEvent.
 */
public class TransferQueueChangedEvent {

	/** The dms transfer list. */
	private final List<AbstractDmsTransferable>	dmsTransferList;

	/** The finished transfer. */
	private final AbstractDmsTransferable		finishedTransferable;

	/** The added transfer. */
	private final AbstractDmsTransferable		addedTransferable;

	/**
	 * Instantiates a new transfer changed event.
	 *
	 * @param dmsTransferList
	 *            the dms transfer list
	 * @param finishedTransferable
	 *            the finished transferable
	 * @param addedTransferable
	 *            the added transferable
	 */
	public TransferQueueChangedEvent(List<AbstractDmsTransferable> dmsTransferList, AbstractDmsTransferable finishedTransferable, AbstractDmsTransferable addedTransferable) {
		super();
		this.dmsTransferList = dmsTransferList;
		this.finishedTransferable = finishedTransferable;
		this.addedTransferable = addedTransferable;
	}

	/**
	 * Gets the transfer list.
	 *
	 * @return the transfer list
	 */
	public List<AbstractDmsTransferable> getTransferList() {
		return this.dmsTransferList;
	}

	/**
	 * Checks for pending transfers.
	 *
	 * @return true, if successful
	 */
	public boolean hasPendingTransfers() {
		for (AbstractDmsTransferable transferable : this.dmsTransferList) {
			if (!transferable.isFinished()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the added transferable.
	 *
	 * @return the added transferable
	 */
	public AbstractDmsTransferable getAddedTransferable() {
		return this.addedTransferable;
	}

	/**
	 * Gets the finished transferable.
	 *
	 * @return the finished transferable
	 */
	public AbstractDmsTransferable getFinishedTransferable() {
		return this.finishedTransferable;
	}

}
