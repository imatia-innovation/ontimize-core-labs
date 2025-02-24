package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.dms.util.ProgressInputStream;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 *
 * This class implements the Downloader to download files from server
 *
 */
public class DmsDownloader extends AbstractDmsTransferer<DmsDownloadable> {

	private static final Logger logger = LoggerFactory.getLogger(DmsDownloader.class);

	@Override
	protected void doTransfer(DmsDownloadable transferable) throws DmsException {
		try {
			Path targetFile = transferable.getTargetFile();
			if (Files.exists(targetFile)) {
				FileTools.deleteQuitely(targetFile);
			}
			InputStream is = BeansFactory.getBean(IDMSService.class).fileGetContentOfVersion(transferable.getIdVersionFile());

			Path tmpFile = Files.createTempFile("dmsdown", "tmp");
			try (OutputStream os = Files.newOutputStream(tmpFile)) {
				IOUtils.copy(new ProgressInputStream(is, transferable, transferable.getSize()), os);
			}

			try {
				Files.move(tmpFile, targetFile);
			} catch (IOException ex) {
				DmsDownloader.logger.warn("Move option not work, using copy option", ex);
				Files.copy(tmpFile, targetFile);
				FileTools.deleteQuitely(tmpFile);
			}
		} catch (Exception ex) {
			throw new DmsException(ex.getMessage(), ex);
		}
	}
}
