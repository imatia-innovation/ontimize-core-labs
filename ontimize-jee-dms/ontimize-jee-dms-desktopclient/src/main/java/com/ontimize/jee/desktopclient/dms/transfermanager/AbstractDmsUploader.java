package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.util.ProgressInputStream;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

public abstract class AbstractDmsUploader<T extends AbstractDmsUploadable> extends AbstractDmsTransferer<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDmsUploader.class);

	@Override
	protected void doTransfer(final T transferable) throws DmsException {
		transferable.setStatus(Status.DOWNLOADING);
		final InputStream is = this.doDownloadFromSource(transferable);
		transferable.setStatus(Status.UPLOADING);
		this.doUploadToDms(is, transferable);
	}

	protected abstract InputStream doDownloadFromSource(T transferable) throws DmsException;

	// @formatter:off
	/**
	 * Depending on values received in transferable DocumentIdentifier we will decide between: · (A) Insert complete Document + File + Version ---> When no identifier data · (B)
	 * Insert File + Version ---> When identifier only has document identifier · (C) Insert only new Version ---> When identifier has document and file ids Note: has no sense to
	 * send version identifier Finally information will be notified in own transferable document identifier object
	 *
	 * @param is
	 * @param transferable
	 * @throws DmsException
	 */
	// @formatter:on
	protected void doUploadToDms(final InputStream is, final T transferable) throws DmsException {
		try {
			DocumentIdentifier sourceDocIdF = transferable.getDocumentIdentifier();
			if (sourceDocIdF == null) {
				sourceDocIdF = new DocumentIdentifier();
			}

			final IDMSService dmsService = BeansFactory.getBean(IDMSService.class);
			if (sourceDocIdF.getDocumentId() == null) {
				// Case (A)
				final Map<String, Object> av = new HashMap<>();
				av.put(DMSNaming.DOCUMENT_DOCUMENT_NAME, transferable.getName());
				final DocumentIdentifier newDocIdf = dmsService.documentInsert(av);

				sourceDocIdF.setDocumentId(newDocIdf.getDocumentId());
			}

			final Map<String, Object> attrs = this.getAVFromTransferable(transferable);
			final boolean continue_ = this.checkForNewVersionofExistentFile(dmsService, attrs, sourceDocIdF);
			if (!continue_) {
				return;
			}

			if (sourceDocIdF.getFileId() == null) {
				// Case (B)
				final DocumentIdentifier newFileInfo = dmsService.fileInsert(sourceDocIdF.getDocumentId(), attrs, new ProgressInputStream(is, transferable, transferable.getSize()));

				// Update source document identifier
				sourceDocIdF.setFileId(newFileInfo.getFileId());
				sourceDocIdF.setVersionId(newFileInfo.getVersionId());
			} else {
				final DocumentIdentifier newFileInfo = dmsService.fileUpdate(sourceDocIdF.getFileId(), attrs, new ProgressInputStream(is, transferable, transferable.getSize()));

				// Update source document identifier
				sourceDocIdF.setVersionId(newFileInfo.getVersionId());
			}
		} finally {
			try {
				is.close();
			} catch (final IOException ex) {
				AbstractDmsUploader.logger.error(null, ex);
			}
		}
	}

	private Map<String, Object> getAVFromTransferable(final T transferable) {
		final Map<String, Object> attrs = new HashMap<>();
		MapTools.safePut(attrs, DMSNaming.DOCUMENT_FILE_NAME, transferable.getName());
		MapTools.safePut(attrs, DMSNaming.CATEGORY_ID_CATEGORY, transferable.getCategoryId());
		MapTools.safePut(attrs, DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION, transferable.getDescription());
		return attrs;
	}

	/**
	 * Feature: Check for new version of same file: if exists the same fileName in the same category, ask user for use new version or another file
	 *
	 * @param dmsService
	 * @param attrs
	 * @param sourceDocIdF
	 * @throws DmsException
	 */
	private boolean checkForNewVersionofExistentFile(final IDMSService dmsService, final Map<String, Object> attrs, final DocumentIdentifier sourceDocIdF) throws DmsException {
		final Map<String, Object> attrs2 = new HashMap<>();
		MapTools.safePut(attrs2, DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, sourceDocIdF.getDocumentId());
		MapTools.safePut(attrs2, DMSNaming.DOCUMENT_FILE_NAME, attrs.get(DMSNaming.DOCUMENT_FILE_NAME));
		MapTools.safePut(attrs2, DMSNaming.CATEGORY_ID_CATEGORY, attrs.get(DMSNaming.CATEGORY_ID_CATEGORY));
		final EntityResult fileQuery = dmsService.fileQuery(attrs2, EntityResultTools.attributes(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE));
		if (fileQuery.calculateRecordNumber() > 0) {
			// Special case: already exists one file with the same fileName in the same category -> Ask user if it is a new version or a distinct file.
			final int confirm = JOptionPane.showConfirmDialog(null, ApplicationManager.getTranslation("dms.isnewversionquestion"));
			if (confirm == JOptionPane.CANCEL_OPTION) {
				return false;
			} else if (confirm == JOptionPane.OK_OPTION) {
				sourceDocIdF.setFileId(((List<Serializable>) fileQuery.get(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE)).get(0));// TODO allow user to select moret han one match
			}
		}
		return true;
	}

}
