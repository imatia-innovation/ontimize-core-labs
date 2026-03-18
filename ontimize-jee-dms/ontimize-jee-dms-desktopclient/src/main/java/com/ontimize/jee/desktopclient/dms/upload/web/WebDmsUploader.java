package com.ontimize.jee.desktopclient.dms.upload.web;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploader;

/**
 *
 * This class implements the Downloader for HTTP and FTP request
 *
 */
public class WebDmsUploader extends AbstractDmsUploader<WebDmsUploadable> {

	private static final Logger logger = LoggerFactory.getLogger(WebDmsUploader.class);

	public WebDmsUploader() {
		super();
	}

	@Override
	protected InputStream doDownloadFromSource(WebDmsUploadable transferable) throws DmsException {
		try {
			// Open connection to URL
			HttpURLConnection conn = (HttpURLConnection) transferable.getUri().toURL().openConnection();
			conn.setConnectTimeout(10000);
			// Connect to server
			conn.connect();
			// Make sure the response code is in the 200 range.
			WebDmsUploader.logger.info("response: {}", conn.getResponseCode());

			if ((conn.getResponseCode() / 100) != 2) {
				throw new DmsException("Error code " + conn.getResponseCode() + ": " + conn.getResponseMessage() + " received from server");
			}

			// Check for valid content length.
			long contentLength = conn.getContentLengthLong();
			transferable.setSize(contentLength);
			if (contentLength < 1) {
				throw new DmsException("Error no content-length received from server");
			}

			return conn.getInputStream();
		} catch (DmsException ex) {
			throw ex;
		} catch (Exception error) {
			throw new DmsException(error.getMessage(), error);
		}
	}
}
