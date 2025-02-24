package com.ontimize.jee.desktopclient.dms.upload.web;

import java.net.MalformedURLException;
import java.net.URI;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;

/**
 * The Class LocalDiskDmsTransferable.
 */
public class WebDmsUploadable extends AbstractDmsUploadable {

	/** The file. */
	private final URI uri;

	/**
	 * Instantiates a new local disk dms transferable.
	 *
	 * @param uri
	 *            the uri
	 * @param description
	 *            the description
	 * @throws DmsException
	 *             the dms exception
	 */
	public WebDmsUploadable(URI uri, String description) throws DmsException {
		super(description, WebDmsUploadable.extractFileName(uri), null);
		this.uri = uri;
	}

	/**
	 * Extract file name.
	 *
	 * @param uri
	 *            the uri
	 * @return the string
	 * @throws DmsException
	 *             the dms exception
	 */
	private static String extractFileName(URI uri) throws DmsException {
		String path;
		try {
			path = uri.toURL().getPath();
		} catch (MalformedURLException error) {
			throw new DmsException(error);
		}
		int indx = path.lastIndexOf('/');
		if (indx >= 0) {
			return path.substring(indx + 1);
		}
		return path;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public URI getUri() {
		return this.uri;
	}
}
