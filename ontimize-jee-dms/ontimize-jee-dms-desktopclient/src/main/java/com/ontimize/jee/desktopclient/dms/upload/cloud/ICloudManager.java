package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.swing.table.TableCellRenderer;

import com.ontimize.jee.common.exceptions.DmsException;

/**
 * The Interface ICloudManager.
 *
 * @param <T>
 *            the generic type
 */
public interface ICloudManager<T extends Serializable> {

	/**
	 * Download a file's content.
	 *
	 * @param file
	 *            the file
	 * @return the input stream
	 * @throws Exception
	 *             the exception
	 */
	InputStream downloadFile(T file) throws DmsException;

	/**
	 * Retrieve files in a folder.
	 *
	 * @param folderId
	 *            the folder id
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	List<T> retrieveFilesInFolder(String folderId) throws DmsException;

	/**
	 * Checks if is folder.
	 *
	 * @param file
	 *            the file
	 * @return true, if is folder
	 */
	boolean isFolder(T file);

	/**
	 * Gets the folder id.
	 *
	 * @param file
	 *            the file
	 * @return the folder id
	 */
	String getFolderId(T file);

	/**
	 * Gets the root folder id.
	 *
	 * @return the root folder id
	 * @throws Exception
	 *             the exception
	 */
	String getRootFolderId() throws DmsException;

	/**
	 * Gets the back file.
	 *
	 * @return the back file
	 */
	T getBackFile();

	/**
	 * Gets the table renderer.
	 *
	 * @return the table renderer
	 */
	TableCellRenderer getTableRenderer();

}
