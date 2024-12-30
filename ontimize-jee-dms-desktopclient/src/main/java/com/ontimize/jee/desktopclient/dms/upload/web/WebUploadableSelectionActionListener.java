package com.ontimize.jee.desktopclient.dms.upload.web;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import com.ontimize.gui.button.Button;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;

public class WebUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	public WebUploadableSelectionActionListener(Button button) throws Exception {
		super(button);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {
		String url = (String) this.button.getParentForm().getDataFieldValue("URL");
		String description = (String) this.button.getParentForm().getDataFieldValue("URL_DESCRIPTION");
		URI verifiedUrl;
		try {
			verifiedUrl = WebUploadableSelectionActionListener.verifyURL(url);
		} catch (MalformedURLException | URISyntaxException error) {
			throw new DmsException(error);
		}
		if (verifiedUrl == null) {
			throw new DmsException("dms.e_invalidurl");
		}
		return new WebDmsUploadable(verifiedUrl, description);
	}

	/**
	 * Verify whether an URL is valid
	 *
	 * @param fileURL
	 * @return the verified URL, null if invalid
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public static URI verifyURL(String fileURL) throws MalformedURLException, URISyntaxException {
		// Only allow HTTP or FTP URLs.
		if (!fileURL.toLowerCase().startsWith("http://") && !fileURL.toLowerCase().startsWith("https://")
				&& !fileURL.toLowerCase().startsWith("ftp://")) {
			return null;
		}

		// Verify format of URL.
		URI verifiedUrl = new URI(fileURL);

		// Make sure URL specifies a file.
		if (verifiedUrl.toURL().getFile().length() < 2) {
			return null;
		}

		return verifiedUrl;
	}
}
