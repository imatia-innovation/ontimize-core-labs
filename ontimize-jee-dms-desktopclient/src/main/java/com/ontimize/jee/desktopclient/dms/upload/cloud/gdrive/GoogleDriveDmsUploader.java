package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.io.InputStream;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploader;

/**
 *
 * This class implements the Downloader for HTTP and FTP request
 *
 */
public class GoogleDriveDmsUploader extends AbstractDmsUploader<GoogleDriveDmsUploadable> {

	public GoogleDriveDmsUploader() {
		super();
	}

	@Override
	protected InputStream doDownloadFromSource(GoogleDriveDmsUploadable transferable) throws DmsException {
		try {
			return GoogleDriveManager.getInstance().downloadFile(transferable.getFile());
		} catch (Exception error) {
			throw new DmsException(error.getMessage(), error);
		}
	}
}