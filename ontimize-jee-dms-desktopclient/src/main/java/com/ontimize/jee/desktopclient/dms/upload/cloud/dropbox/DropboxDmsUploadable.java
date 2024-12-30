package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.net.MalformedURLException;

import com.dropbox.core.DbxEntry;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;

/**
 * The Class LocalDiskDmsTransferable.
 */
public class DropboxDmsUploadable extends AbstractDmsUploadable {

	/** The file. */
	private final DbxEntry file;

	/**
	 * Instantiates a new local disk dms transferable.
	 *
	 * @param uri
	 *            the uri
	 * @param description
	 *            the description
	 * @param fileName
	 *            the file name
	 * @param fileSize
	 *            the file size
	 * @throws MalformedURLException
	 *             the malformed url exception
	 */
	public DropboxDmsUploadable(DbxEntry file, String description, String fileName, Long fileSize) throws MalformedURLException {
		super(description, fileName, fileSize);
		this.file = file;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public DbxEntry getFile() {
		return this.file;
	}
}
