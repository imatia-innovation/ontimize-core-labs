package com.ontimize.jee.desktopclient.dms.transfermanager;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;

public abstract class AbstractDmsTransferer<T extends AbstractDmsTransferable> {

	public void transfer(T transferable) throws DmsException {
		try {
			transferable.setStatus(Status.DOWNLOADING);
			this.doTransfer(transferable);
			transferable.setStatus(Status.COMPLETED);
		} catch (Exception ex) {
			transferable.setStatus(Status.ERROR);
			if (ex instanceof DmsException) {
				throw ex;
			} else {
				throw new DmsException(ex.getMessage(), ex);
			}
		}
	}

	protected abstract void doTransfer(T transferable) throws DmsException;

}
