package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.io.Serializable;
import java.nio.file.Path;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.ITransferQueueListener;

/**
 * The Interface IDmsTransfererManager.
 */
public interface IDmsTransfererManager {

	/**
	 * Inits the.
	 */
	void init();

	/**
	 * Transfer.
	 *
	 * @param transferable
	 *            the transferable
	 * @throws DmsException
	 *             the dms exception
	 */
	void transfer(AbstractDmsTransferable transferable) throws DmsException;

	/**
	 * Adds the transfer queue listener.
	 *
	 * @param transferQueueListener
	 *            the transfer queue listener
	 */
	void addTransferQueueListener(ITransferQueueListener transferQueueListener);

	/**
	 * Removes the transfer queue listener.
	 *
	 * @param transferQueueListener
	 *            the transfer queue listener
	 */
	void removeTransferQueueListener(ITransferQueueListener transferQueueListener);

	/**
	 * Gets the dms file version.
	 *
	 * @param idFileVersion
	 *            the id file version
	 * @return the dms file version
	 * @throws DmsException
	 */
	Path obtainDmsFileVersion(Serializable idFileVersion) throws DmsException;

	/**
	 * Gets the dms file version.
	 *
	 * @param idFileVersion
	 *            the id file version
	 * @param fileName
	 *            the file name
	 * @param fileSize
	 *            the file size
	 * @return the dms file version
	 * @throws DmsException
	 *             the dms exception
	 */
	Path obtainDmsFileVersion(Serializable idFileVersion, String fileName, Long fileSize) throws DmsException;
}
