package com.ontimize.jee.server.services.dms;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.spring.parser.AbstractPropertyResolver;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.server.configuration.OntimizeConfiguration;

/**
 * The Ontimize standard DMS implementation. Splitted implementation over helpers.
 */
public class OntimizeDMSEngine implements IDMSServiceServer, InitializingBean {

    /** The CONSTANTLOGGER */
    private static final Logger logger = LoggerFactory.getLogger(OntimizeDMSEngine.class);

    /** The Constant ACTIVE. */
    public static final String ACTIVE = "Y";

    /** The Constant INACTIVE. */
    public static final String INACTIVE = "N";

    /** The ontimize configuration. */
    @Autowired
    protected OntimizeConfiguration ontimizeConfiguration;

    /** The file helper. */
    @Autowired
    protected DMSServiceFileHelper fileHelper;

    /** The document helper. */
    @Autowired
    protected DMSServiceDocumentHelper documentHelper;

    /** The category helper. */
    @Autowired
    protected DMSServiceCategoryHelper categoryHelper;

    /** The documents base path. */
    protected Path documentsBasePath;

    protected AbstractPropertyResolver<String> documentsBasePathResolver;

    public OntimizeDMSEngine() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.updateSettings();
    }

    @Override
    public void updateSettings() {
        OntimizeDMSEngine.logger.debug("Updating settings...");
        if (this.documentsBasePathResolver != null) {
            CheckingTools.failIfNull(this.documentsBasePathResolver,
                    "DMS documents base path resolver is not configured.");
            Path resolvedValue = this.getResolverValue(this.documentsBasePathResolver);
            CheckingTools.failIfNull(resolvedValue, "DMS documents base path is not configured.");
            this.documentsBasePath = resolvedValue;
        }
    }

    /**
     * Gets the documents base path.
     * @return the documents base path
     */
    public Path getDocumentsBasePath() {
        CheckingTools.failIfNull(this.documentsBasePath, "DMS documents base path is not configured.");
        return this.documentsBasePath;
    }

    /**
     * Sets the documents base path.
     * @param documentsBasePath the documents base path
     */
    public void setDocumentsBasePath(final String documentsBasePath) {
        this.documentsBasePathResolver = new AbstractPropertyResolver<String>() {

            @Override
            public String getValue() {
                return documentsBasePath;
            }
        };
    }

    /**
     * @return
     */
    public AbstractPropertyResolver<String> getDocumentsBasePathResolver() {
        return this.documentsBasePathResolver;
    }

    /**
     * Sets the documents base path resolver.
     * @param documentsBasePathResolver the base path resolver
     */
    public void setDocumentsBasePathResolver(AbstractPropertyResolver<String> documentsBasePathResolver) {
        this.documentsBasePathResolver = documentsBasePathResolver;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContent(java.lang .Object)
     */
    @Override
    public InputStream fileGetContent(Serializable fileId) throws DmsException {
        return this.fileHelper.fileGetContent(fileId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
     */
    @Override
    public Path fileGetPath(Serializable fileId) throws DmsException {
        return this.fileHelper.fileGetPath(fileId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
     */
    @Override
    public InputStream fileGetContentOfVersion(Serializable fileVersionId) throws DmsException {
        return this.fileHelper.fileGetContentOfVersion(fileVersionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
     */
    @Override
    public Path fileGetPathOfVersion(Serializable fileVersionId) throws DmsException {
        return this.fileHelper.fileGetPathOfVersion(fileVersionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileInsert(java.util.Map,
     * java.io.InputStream)
     */
    @Override
    public DocumentIdentifier fileInsert(Serializable documentId, Map<?, ?> av, InputStream is) throws DmsException {
        return this.fileHelper.fileInsert(documentId, av, is);
    }

    /**
     * Ensures to deprecate current ACTIVE version of file (if exists) and return next DEFAULT AUTOMATIC
     * version.
     * @param fileId the file id
     * @return the current file version and deprecate
     */
    public Number getCurrentFileVersionAndDeprecate(Serializable fileId) {
        return this.fileHelper.getCurrentFileVersionAndDeprecate(fileId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object ,
     * java.util.Map, java.io.InputStream)
     */
    @Override
    public DocumentIdentifier fileUpdate(Serializable fileId, Map<?, ?> attributesValues, InputStream is)
            throws DmsException {
        return this.fileHelper.fileUpdate(fileId, attributesValues, is);
    }

    @Override
    public DocumentIdentifier fileVersionOverrideContent(Serializable versionId, InputStream is) throws DmsException {
        return this.fileHelper.fileVersionOverrideContent(versionId, is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
     */
    @Override
    public EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) {
        return this.fileHelper.fileQuery(criteria, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
     */
    @Override
    public void fileDelete(Serializable fileId) throws DmsException {
        this.fileHelper.fileDelete(fileId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileVersionQuery(java.lang.Object,
     * java.util.List)
     */
    @Override
    public EntityResult fileVersionQuery(Serializable fileVersionId, List<?> attributes) {
        return this.fileHelper.fileVersionQuery(fileVersionId, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetVersions(java.lang.Object,
     * java.util.Map, java.util.List)
     */
    @Override
    public EntityResult fileGetVersions(Serializable fileId, Map<?, ?> kv, List<?> attributes) {
        return this.fileHelper.fileGetVersions(fileId, kv, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ontimize.jee.common.services.dms.IDMSService#fileRecoverPreviousVersion(java.lang.Object)
     */
    @Override
    public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion) throws DmsException {
        this.fileHelper.fileRecoverPreviousVersion(fileId, acceptNotPreviousVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentQuery(java.util.List,
     * java.util.Map)
     */
    @Override
    public EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) {
        return this.documentHelper.documentQuery(attributes, criteria);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentInsert(java.util.Map)
     */
    @Override
    public DocumentIdentifier documentInsert(Map<?, ?> av) {
        return this.documentHelper.documentInsert(av);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentUpdate(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public void documentUpdate(Serializable documentId, Map<?, ?> attributesValues) {
        this.documentHelper.documentUpdate(documentId, attributesValues);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentDelete(java.lang.Object)
     */
    @Override
    public void documentDelete(Serializable documentId) throws DmsException {
        this.documentHelper.documentDelete(documentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentAddProperties(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public void documentAddProperties(Serializable documentId, Map<String, String> properties) {
        this.documentHelper.documentAddProperties(documentId, properties);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentDeleteProperties(java.lang.Object,
     * java.util.List)
     */
    @Override
    public void documentDeleteProperties(Serializable documentId, List<String> propertyKeys) {
        this.documentHelper.documentDeleteProperties(documentId, propertyKeys);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperty(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public String documentGetProperty(Serializable documentId, String propertyKey) {
        return this.documentHelper.documentGetProperty(documentId, propertyKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperties(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public Map<String, String> documentGetProperties(Serializable documentId, Map<?, ?> kv) {
        return this.documentHelper.documentGetProperties(documentId, kv);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetFiles(java.lang.Object,
     * java.util.Map, java.util.List)
     */
    @Override
    public EntityResult documentGetFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes) {
        return this.documentHelper.documentGetFiles(documentId, kv, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetAllFiles(java.lang.Object,
     * java.util.Map, java.util.List)
     */
    @Override
    public EntityResult documentGetAllFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes)
            throws DmsException {
        return this.documentHelper.documentGetAllFiles(documentId, kv, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#getRelatedDocument(java.lang.Object)
     */
    @Override
    public EntityResult getRelatedDocument(Serializable documentId) {
        return this.documentHelper.getRelatedDocument(documentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#setRelatedDocuments(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId) {
        this.documentHelper.setRelatedDocuments(masterDocumentId, childDocumentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryGetForDocument(java.lang.Object,
     * java.util.List)
     */
    @Override
    public DMSCategory categoryGetForDocument(Serializable documentId, List<?> attributes) throws DmsException {
        return this.categoryHelper.categoryGetForDocument(documentId, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryInsert(java.lang.String,
     * java.lang.Object, java.util.Map)
     */
    @Override
    public Serializable categoryInsert(Serializable documentId, String name, Serializable parentCategoryId,
            Map<?, ?> otherData) {
        return this.categoryHelper.categoryInsert(documentId, name, parentCategoryId, otherData);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryUpdate(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public void categoryUpdate(Serializable categoryId, Map<?, ?> av) {
        this.categoryHelper.categoryUpdate(categoryId, av);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryDelete(java.lang.Object)
     */
    @Override
    public void categoryDelete(Serializable idCategory) {
        this.categoryHelper.categoryDelete(idCategory);
    }

    @Override
    public void moveFilesToCategory(Serializable idCategory, List<Serializable> idFiles) {
        this.fileHelper.moveFilesToCategory(idCategory, idFiles);
    }

    /**
     * Gets the resolver value.
     * @param resolver the resolver
     * @return the resolver value
     */
    protected Path getResolverValue(AbstractPropertyResolver<String> resolver) {
        if (resolver != null) {
            try {
                String basePath = resolver.getValue();
                if (!basePath.endsWith("/")) {
                    basePath += "/";
                }
                Path path = Paths.get(new URI(basePath));
                OntimizeDMSEngine.logger.info("Final path / class {} / {}", path, path.getClass().getName());
                return path;
            } catch (Exception ex) {
                OntimizeDMSEngine.logger.error(null, ex);
            }
        }
        return null;
    }

}
