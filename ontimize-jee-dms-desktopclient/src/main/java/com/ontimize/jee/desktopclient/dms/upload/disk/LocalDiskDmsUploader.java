package com.ontimize.jee.desktopclient.dms.upload.disk;

import java.io.InputStream;
import java.nio.file.Files;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploader;

/**
 *
 * This class implements the Uploader for localdisk requests
 *
 */
public class LocalDiskDmsUploader extends AbstractDmsUploader<LocalDiskDmsUploadable> {

	@Override
	protected InputStream doDownloadFromSource(LocalDiskDmsUploadable transferable) throws DmsException {
		try {
			return Files.newInputStream(transferable.getFile());
		} catch (Exception error) {
			throw new DmsException("E_DOWNLOADING", error);
		}
	}
}
