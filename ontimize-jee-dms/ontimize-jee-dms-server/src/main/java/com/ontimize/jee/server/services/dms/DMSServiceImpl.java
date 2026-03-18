package com.ontimize.jee.server.services.dms;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.server.spring.namespace.OntimizeDMSConfiguration;

/**
 * The DMS service generic facade. This will delegate to configured engine : OntimizeDMSEngine ...
 */
@Service("DMSService")
@Lazy(value = true)
public class DMSServiceImpl implements IDMSServiceServer, ApplicationContextAware {

    /** The implementation. */
    private IDMSServiceServer engine;

    /**
     * Gets the engine.
     * @return the engine
     */
    protected IDMSServiceServer getEngine() {
        CheckingTools.failIfNull(this.engine, "Not engine defined for dms.");
        return this.engine;
    }

    /**
     * Sets the engine.
     * @param engine the engine
     */
    public void setEngine(IDMSServiceServer engine) {
        this.engine = engine;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.setEngine(applicationContext.getBean(OntimizeDMSConfiguration.class).getDmsConfiguration().getEngine());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContent(java.lang .Object)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public InputStream fileGetContent(Serializable fileId) throws DmsException {
        return this.getEngine().fileGetContent(fileId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
     */
    @Override
    public Path fileGetPath(Serializable fileId) throws DmsException {
        return this.getEngine().fileGetPath(fileId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public InputStream fileGetContentOfVersion(Serializable fileVersionId) throws DmsException {
        return this.getEngine().fileGetContentOfVersion(fileVersionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
     */
    @Override
    public Path fileGetPathOfVersion(Serializable fileVersionId) throws DmsException {
        return this.getEngine().fileGetPathOfVersion(fileVersionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileInsert(java.util.Map,
     * java.io.InputStream)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DocumentIdentifier fileInsert(Serializable documentId, Map<?, ?> av, InputStream is) throws DmsException {
        return this.getEngine().fileInsert(documentId, av, is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object ,
     * java.util.Map, java.io.InputStream)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DocumentIdentifier fileUpdate(Serializable fileId, Map<?, ?> attributesValues, InputStream is)
            throws DmsException {
        return this.getEngine().fileUpdate(fileId, attributesValues, is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object ,
     * java.util.Map, java.io.InputStream)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public DocumentIdentifier fileVersionOverrideContent(Serializable fileVersionId, InputStream is)
            throws DmsException {
        return this.getEngine().fileVersionOverrideContent(fileVersionId, is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) throws DmsException {
        return this.getEngine().fileQuery(criteria, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void fileDelete(Serializable fileId) throws DmsException {
        this.getEngine().fileDelete(fileId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileVersionQuery(java.lang.Object,
     * java.util.List)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult fileVersionQuery(Serializable fileVersionId, List<?> attributes) throws DmsException {
        return this.getEngine().fileVersionQuery(fileVersionId, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetVersions(java.lang.Object,
     * java.util.Map, java.util.List)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult fileGetVersions(Serializable fileId, Map<?, ?> kv, List<?> attributes) throws DmsException {
        return this.getEngine().fileGetVersions(fileId, kv, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ontimize.jee.common.services.dms.IDMSService#fileRecoverPreviousVersion(java.lang.Object)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion) throws DmsException {
        this.getEngine().fileRecoverPreviousVersion(fileId, acceptNotPreviousVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentQuery(java.util.List,
     * java.util.Map)
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) throws DmsException {
        return this.getEngine().documentQuery(attributes, criteria);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentInsert(java.util.Map)
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    public DocumentIdentifier documentInsert(Map<?, ?> av) throws DmsException {
        return this.getEngine().documentInsert(av);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentUpdate(java.lang.Object,
     * java.util.Map)
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    public void documentUpdate(Serializable documentId, Map<?, ?> attributesValues) throws DmsException {
        this.getEngine().documentUpdate(documentId, attributesValues);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentDelete(java.lang.Object)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void documentDelete(Serializable documentId) throws DmsException {
        this.getEngine().documentDelete(documentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentAddProperties(java.lang.Object,
     * java.util.Map)
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    public void documentAddProperties(Serializable documentId, Map<String, String> properties) throws DmsException {
        this.getEngine().documentAddProperties(documentId, properties);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentDeleteProperties(java.lang.Object,
     * java.util.List)
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    public void documentDeleteProperties(Serializable documentId, List<String> propertyKeys) throws DmsException {
        this.getEngine().documentDeleteProperties(documentId, propertyKeys);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperty(java.lang.Object,
     * java.lang.String)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public String documentGetProperty(Serializable documentId, String propertyKey) throws DmsException {
        return this.getEngine().documentGetProperty(documentId, propertyKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperties(java.lang.Object,
     * java.util.Map)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public Map<String, String> documentGetProperties(Serializable documentId, Map<?, ?> kv) throws DmsException {
        return this.getEngine().documentGetProperties(documentId, kv);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetFiles(java.lang.Object,
     * java.util.Map, java.util.List)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult documentGetFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes)
            throws DmsException {
        return this.getEngine().documentGetFiles(documentId, kv, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetAllFiles(java.lang.Object,
     * java.util.Map, java.util.List)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult documentGetAllFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes)
            throws DmsException {
        return this.getEngine().documentGetAllFiles(documentId, kv, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#getRelatedDocument(java.lang.Object)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult getRelatedDocument(Serializable documentId) throws DmsException {
        return this.getEngine().getRelatedDocument(documentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#setRelatedDocuments(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    public void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId) throws DmsException {
        this.getEngine().setRelatedDocuments(masterDocumentId, childDocumentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryGetForDocument(java.lang.Object,
     * java.util.List)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public DMSCategory categoryGetForDocument(Serializable documentId, List<?> attributes) throws DmsException {
        return this.getEngine().categoryGetForDocument(documentId, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryInsert(java.lang.String,
     * java.lang.Object, java.util.Map)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Serializable categoryInsert(Serializable documentId, String name, Serializable parentCategoryId,
            Map<?, ?> otherData) throws DmsException {
        return this.getEngine().categoryInsert(documentId, name, parentCategoryId, otherData);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryUpdate(java.lang.Object,
     * java.util.Map)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void categoryUpdate(Serializable categoryId, Map<?, ?> av) throws DmsException {
        this.getEngine().categoryUpdate(categoryId, av);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#categoryDelete(java.lang.Object)
     */
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void categoryDelete(Serializable idCategory) throws DmsException {
        this.getEngine().categoryDelete(idCategory);
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    @Transactional(rollbackFor = Exception.class)
    public void moveFilesToCategory(Serializable idCategory, List<Serializable> idFiles) throws DmsException {
        this.getEngine().moveFilesToCategory(idCategory, idFiles);
    }

    @Override
    public void updateSettings() throws DmsException {
        this.getEngine().updateSettings();
    }

}
