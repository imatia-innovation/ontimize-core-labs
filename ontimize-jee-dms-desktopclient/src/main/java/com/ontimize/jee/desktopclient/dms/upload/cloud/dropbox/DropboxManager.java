package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxClient.Downloader;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.exceptions.DmsRuntimeException;
import com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager;

/**
 * A basic class for download and upload text files with dropbox API.
 *
 */
public class DropboxManager implements ICloudManager<DbxEntry> {

	private static final Logger		logger				= LoggerFactory.getLogger(DropboxManager.class);

	/** The Constant APPLICATION_NAME. */
	protected static final String	APPLICATION_NAME	= "OntimizeEEdrpbx";

	/** The Constant APP_KEY. */
	// Get your app key and secret from the Dropbox developers website.
	static final String				APP_KEY				= "6fwbab6m3ehjfed";

	/** The Constant APP_SECRET. */
	static final String				APP_SECRET			= "l8jhgqcb1lcfxyv";

	/** The instance. */
	protected static DropboxManager	instance;

	/**
	 * Gets the single instance of DropboxManager.
	 *
	 * @return single instance of DropboxManager
	 */
	public static DropboxManager getInstance() {
		if (DropboxManager.instance == null) {
			DropboxManager.instance = new DropboxManager();
		}
		return DropboxManager.instance;
	}

	/**
	 * Reset.
	 */
	public static void reset() {
		if (DropboxManager.instance != null) {
			DropboxManager.instance.webAuth = null;
			DropboxManager.instance.service = null;
			DropboxManager.instance.httpTransport = null;
			DropboxManager.instance.jsonFactory = null;
		}
		DropboxManager.instance = null;
	}

	/** The http transport. */
	protected HttpTransport			httpTransport;

	/** The json factory. */
	protected JsonFactory			jsonFactory;

	/** The web auth. */
	protected DbxWebAuthNoRedirect	webAuth;

	/** The service. */
	protected DbxClient				service	= null;

	/** The token. */
	protected String				token	= null;

	/**
	 * Initialize initials attributes.
	 */
	public DropboxManager() {
		this.getFlow();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager#getRootFolderId()
	 */
	@Override
	public String getRootFolderId() {
		return "/";
	}

	/**
	 * Checks if is syncronized.
	 *
	 * @return true, if is syncronized
	 */
	public boolean isSyncronized() {
		return this.service != null;
	}

	/**
	 * Build an authorization flow and store it as a static class attribute.
	 *
	 * @return GoogleAuthorizationCodeFlow instance.
	 */
	private void getFlow() {
		try {
			DropboxManager.reset();
			DbxAppInfo appInfo = new DbxAppInfo(DropboxManager.APP_KEY, DropboxManager.APP_SECRET);
			DbxRequestConfig config = new DbxRequestConfig(DropboxManager.APPLICATION_NAME, Locale.getDefault().toString());
			this.webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		} catch (Exception ex) {
			throw new DmsRuntimeException(ex);
		}
	}

	/**
	 * Get the authorization URL for authorize the application.
	 *
	 * @return String URL for authorize the application.
	 */
	public String getAuthorizationUrl() {
		return this.webAuth.start();
	}

	/**
	 * Set the authorization code and create the service.
	 *
	 * @param code
	 *            the new authorization code
	 * @throws DbxException
	 *             the dbx exception
	 */
	public void setAuthorizationCode(String code) throws DbxException {
		DbxAuthFinish authFinish = this.webAuth.finish(code);
		this.token = authFinish.accessToken;
		DbxRequestConfig config = new DbxRequestConfig(DropboxManager.APPLICATION_NAME, Locale.getDefault().toString());
		this.service = new DbxClient(config, this.token);
		DropboxManager.logger.debug("Linked account: " + this.service.getAccountInfo().displayName);
	}

	/**
	 * Download a file's content.
	 *
	 * @param file
	 *            Drive File instance.
	 * @return InputStream containing the file's content if successful, {@code null} otherwise.
	 * @throws DmsException
	 *             the dms exception
	 */
	@Override
	public InputStream downloadFile(DbxEntry file) throws DmsException {
		Downloader downloader;
		try {
			downloader = this.service.startGetFile(file.path, null);
		} catch (DbxException error) {
			throw new DmsException(error);
		}
		return downloader.body;
	}

	/**
	 * Retrieve files in a folder.
	 *
	 * @param folderName
	 *            the folder name
	 * @return the list
	 * @throws DmsException
	 *             the dms exception
	 */
	@Override
	public List<DbxEntry> retrieveFilesInFolder(String folderName) throws DmsException {
		DbxEntry.WithChildren listing;
		try {
			listing = this.service.getMetadataWithChildren(folderName);
		} catch (DbxException error) {
			throw new DmsException(error);
		}
		return listing.children;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager#isFolder(java.io.Serializable)
	 */
	@Override
	public boolean isFolder(DbxEntry file) {
		return file.isFolder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager#getFolderId(java.io.Serializable)
	 */
	@Override
	public String getFolderId(DbxEntry file) {
		return file.path;
	}

	/** The Constant BACK_FOLDER. */
	public static final DbxEntry.Folder BACK_FOLDER = new DbxEntry.Folder("/..", null, false);

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager#getBackFile()
	 */
	@Override
	public DbxEntry getBackFile() {
		return DropboxManager.BACK_FOLDER;
	}

	/** The Constant RENDERER. */
	public static final DropboxTableRenderer RENDERER = new DropboxTableRenderer();

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager#getTableRenderer()
	 */
	@Override
	public TableCellRenderer getTableRenderer() {
		return DropboxManager.RENDERER;
	}
}