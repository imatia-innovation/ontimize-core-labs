package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.io.InputStream;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploader;

/**
 *
 * This class implements the Downloader for HTTP and FTP request
 *
 */
public class DropboxDmsUploader extends AbstractDmsUploader<DropboxDmsUploadable> {

	public DropboxDmsUploader() {
		super();
	}

	@Override
	protected InputStream doDownloadFromSource(DropboxDmsUploadable transferable) throws DmsException {
		try {
			return DropboxManager.getInstance().downloadFile(transferable.getFile());
		} catch (Exception error) {
			throw new DmsException(error.getMessage(), error);
		}
	}
}