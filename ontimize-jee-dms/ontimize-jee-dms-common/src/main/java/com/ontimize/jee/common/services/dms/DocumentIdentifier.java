package com.ontimize.jee.common.services.dms;

import java.io.Serializable;

/**
 * This class must identify by unique way a file: Document + File + Version
 */
public class DocumentIdentifier implements Serializable {

    /**
     * ID_DMS_DOC
     */
    protected Serializable documentId;

    /**
     * ID_DMS_DOC_FILE
     */
    protected Serializable fileId;

    /**
     * ID_DMS_DOC_FILE_VERSION
     */
    protected Serializable versionId;

    public DocumentIdentifier() {
        this(null);
    }

    public DocumentIdentifier(Serializable documentId) {
        this(documentId, null);
    }

    public DocumentIdentifier(Serializable documentId, Serializable fileId) {
        this(documentId, fileId, null);
    }

    public DocumentIdentifier(Serializable documentId, Serializable fileId, Serializable versionId) {
        this.documentId = documentId;
        this.fileId = fileId;
        this.versionId = versionId;
    }

    public Serializable getDocumentId() {
        return this.documentId;
    }

    public void setDocumentId(Serializable documentId) {
        this.documentId = documentId;
    }

    public Serializable getFileId() {
        return this.fileId;
    }

    public void setFileId(Serializable fileId) {
        this.fileId = fileId;
    }

    public Serializable getVersionId() {
        return this.versionId;
    }

    public void setVersionId(Serializable versionId) {
        this.versionId = versionId;
    }

}
