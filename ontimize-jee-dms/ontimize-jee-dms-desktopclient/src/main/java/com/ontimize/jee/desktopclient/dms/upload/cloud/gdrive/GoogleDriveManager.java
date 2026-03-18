package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.exceptions.DmsRuntimeException;
import com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager;

/**
 * A basic class for download and upload text files with google drive API.
 *
 * @see https://developers.google.com/drive/examples/java#making_authorized_api_requests
 * @see https://developers.google.com/drive/quickstart-java"urn:ietf:wg:oauth:2.0:oob";
 */
public class GoogleDriveManager implements ICloudManager<GoogleFile> {

	private static final Logger				logger					= LoggerFactory.getLogger(GoogleDriveManager.class);
	protected static final String			APPLICATION_NAME		= "Application";
	protected static GoogleDriveManager		instance;
	protected static final String			CLIENTSECRETS_LOCATION	= "/ontimize-dms-gdrive/client_secrets.json";

	// AccessType
	protected static final String			AUTO					= "auto";
	protected static final String			FORCE					= "force";

	// ApprovalPrompt
	protected static final String			ONLINE					= "online";
	protected static final String			OFFLINE					= "offline";

	// RedirectUri
	protected static final String			REDIRECT_URI			= "urn:ietf:wg:oauth:2.0:oob";
	protected static final String			REDIRECT_URI_AUTO		= "urn:ietf:wg:oauth:2.0:oob:auto";

	// Scopes
	protected static final List<String>		SCOPES					= Arrays.asList(DriveScopes.DRIVE);

	protected HttpTransport					httpTransport;
	protected JsonFactory					jsonFactory;
	protected GoogleAuthorizationCodeFlow	flow;
	protected Drive							service					= null;
	protected String						token					= null;

	/**
	 * Initialize initials attributes.
	 *
	 * @param String
	 *            basic configuration parameters.
	 */
	public GoogleDriveManager() {
		this.createFlow();
	}

	public static GoogleDriveManager getInstance() {
		if (GoogleDriveManager.instance == null) {
			GoogleDriveManager.instance = new GoogleDriveManager();
		}
		return GoogleDriveManager.instance;
	}

	public static void reset() {
		if (GoogleDriveManager.instance != null) {
			GoogleDriveManager.instance.flow = null;
			GoogleDriveManager.instance.service = null;
			GoogleDriveManager.instance.httpTransport = null;
			GoogleDriveManager.instance.jsonFactory = null;
		}
		GoogleDriveManager.instance = null;
	}

	@Override
	public String getRootFolderId() throws DmsException {
		try {
			return this.service.about().get().execute().getRootFolderId();
		} catch (IOException error) {
			throw new DmsException(error);
		}
	}

	public boolean isSyncronized() {
		return this.service != null;
	}

	/**
	 * Build an authorization flow and store it as a static class attribute.
	 *
	 * @return GoogleAuthorizationCodeFlow instance.
	 * @throws IOException
	 *             Unable to load client_secrets.json.
	 */
	private void createFlow() {
		try {
			GoogleDriveManager.reset();
			this.httpTransport = new NetHttpTransport();
			this.jsonFactory = new JacksonFactory();
			InputStreamReader readerSecretsLocation = new InputStreamReader(GoogleDriveManager.class.getResourceAsStream(GoogleDriveManager.CLIENTSECRETS_LOCATION));

			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(this.jsonFactory, readerSecretsLocation);
			this.flow = new GoogleAuthorizationCodeFlow.Builder(this.httpTransport, this.jsonFactory, clientSecrets, GoogleDriveManager.SCOPES)
			        .setAccessType(GoogleDriveManager.ONLINE).setApprovalPrompt(GoogleDriveManager.AUTO).build();
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
		return this.flow.newAuthorizationUrl().setRedirectUri(GoogleDriveManager.REDIRECT_URI_AUTO).build();
	}

	/**
	 * Get root URL
	 *
	 * @return
	 */
	public String getRootUrl() {
		return this.service.getRootUrl();
	}

	/**
	 * Set the authorization code and create the service.
	 *
	 * @param String
	 *            authorization code.
	 */
	public void setAuthorizationCode(String code) throws DmsException {
		GoogleTokenResponse response;
		try {
			response = this.flow.newTokenRequest(code).setRedirectUri(GoogleDriveManager.REDIRECT_URI_AUTO).execute();
		} catch (IOException error) {
			throw new DmsException(error);
		}
		GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

		// Create a new authorized API client
		this.service = new Drive.Builder(this.httpTransport, this.jsonFactory, credential).setApplicationName(GoogleDriveManager.APPLICATION_NAME).build();
	}

	/**
	 * Upload a text file.
	 *
	 * @param String
	 *            path of the file.
	 * @return String name of the file in google drive.
	 */
	public String uploadTextFile(String filePath, String title) throws DmsException {
		GoogleFile body = new GoogleFile();
		body.setTitle(title);
		body.setDescription("A test document");
		body.setMimeType("text/plain");
		java.io.File fileContent = new java.io.File(filePath);
		FileContent mediaContent = new FileContent("text/plain", fileContent);
		GoogleFile file;
		try {
			file = GoogleFile.toGoogleFile(this.service.files().insert(body.getFile(), mediaContent).execute());
		} catch (IOException error) {
			throw new DmsException(error);
		}
		return file.getId();
	}

	/**
	 * Get the content of a file.
	 *
	 * @param File
	 *            to get the content.
	 * @return String content of the file.
	 */
	public String downloadTextFile(GoogleFile gFile) throws DmsException {
		GenericUrl url = new GenericUrl(gFile.getDownloadUrl());
		HttpResponse response;
		try {
			response = this.service.getRequestFactory().buildGetRequest(url).execute();
		} catch (IOException error) {
			throw new DmsException(error);
		}
		try (Scanner sc = new Scanner(response.getContent())) {
			return sc.useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException error) {
			GoogleDriveManager.logger.warn(null, error);
			return "";
		} catch (IOException error) {
			throw new DmsException(error);
		}
	}

	/**
	 * Get the content of a file.
	 *
	 * @param String
	 *            the file ID.
	 * @return String content of the file.
	 */
	public String downloadTextFile(String fileID) throws DmsException {
		GoogleFile file;
		try {
			file = GoogleFile.toGoogleFile(this.service.files().get(fileID).execute());
		} catch (IOException error) {
			throw new DmsException(error);
		}
		return this.downloadTextFile(file);
	}

	/**
	 * Retrieve a list of File resources.
	 *
	 * @param service
	 *            Drive API service instance.
	 * @return List of File resources.
	 * @author Google
	 * @throws IOException
	 */
	public List<GoogleFile> retrieveAllFiles() throws DmsException {
		List<GoogleFile> result = new ArrayList<>();
		Files.List request = null;

		try {
			request = this.service.files().list();
		} catch (IOException error) {
			throw new DmsException(error);
		}

		do {
			try {
				FileList files = request.execute();
				result.addAll(GoogleFile.toGoogleFile(files.getItems()));
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				GoogleDriveManager.logger.error("An error occurred: ", e);
				request.setPageToken(null);
			}
		} while ((request.getPageToken() != null) && (request.getPageToken().length() > 0));

		return result;
	}

	/**
	 * Download a file's content.
	 *
	 * @param file
	 *            Drive File instance.
	 * @return InputStream containing the file's content if successful, {@code null} otherwise.
	 */
	@Override
	public InputStream downloadFile(GoogleFile file) throws DmsException {
		if ((file.getDownloadUrl() != null) && (file.getDownloadUrl().length() > 0)) {
			try {
				HttpResponse resp = this.service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
				return resp.getContent();
			} catch (Exception error) {
				throw new DmsException(error);
			}
		} else {
			// The file doesn't have any content stored on Drive.
			return null;
		}
	}

	/**
	 * Retrieve files in a folder.
	 *
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<GoogleFile> retrieveFilesInFolder(String folderName) throws DmsException {
		List<GoogleFile> result = new ArrayList<>();
		Files.List request;
		try {
			request = this.service.files().list();
		} catch (IOException error) {
			throw new DmsException(error);
		}
		request.setQ("'" + folderName + "' in parents");
		do {
			try {
				FileList files = request.execute();
				result.addAll(GoogleFile.toGoogleFile(files.getItems()));
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				GoogleDriveManager.logger.error("An error occurred: ", e);
				request.setPageToken(null);
			}
		} while ((request.getPageToken() != null) && (request.getPageToken().length() > 0));
		Collections.sort(result, new Comparator<GoogleFile>() {

			@Override
			public int compare(GoogleFile o1, GoogleFile o2) {
				if (o1.getMimeType().endsWith(".folder")) {
					if (o2.getMimeType().endsWith(".folder")) {
						return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
					} else {
						return -1;
					}
				} else if (o2.getMimeType().endsWith(".folder")) {
					return 1;
				} else {
					return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
				}
			}
		});
		return result;
	}

	@Override
	public boolean isFolder(GoogleFile file) {
		return file.getMimeType().endsWith(".folder");
	}

	@Override
	public String getFolderId(GoogleFile file) {
		return file.getId();
	}

	protected static final GoogleFile BACK_FILE = new GoogleFile();
	static {
		GoogleDriveManager.BACK_FILE.setTitle("..");
	}

	@Override
	public GoogleFile getBackFile() {
		return GoogleDriveManager.BACK_FILE;
	}

	public static final GoogleDriveTableRenderer TABLECELLRENDERER = new GoogleDriveTableRenderer();

	@Override
	public TableCellRenderer getTableRenderer() {
		return GoogleDriveManager.TABLECELLRENDERER;
	}
}