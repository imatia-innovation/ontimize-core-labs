package com.ontimize.jee.server.services.dms;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.dao.IDMSCategoryDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentPropertyDao;
import com.ontimize.jee.server.services.dms.dao.IDMSRelatedDocumentDao;

/**
 * The Class DMSServiceDocumentHelper.
 */
@Component
@Lazy(value = true)
public class DMSServiceDocumentHelper extends AbstractDMSServiceHelper {

    @Autowired
    DefaultOntimizeDaoHelper daoHelper;

    /** The document dao. */
    @Autowired
    protected IDMSDocumentDao documentDao;

    /** The related document dao. */
    @Autowired
    protected IDMSRelatedDocumentDao relatedDocumentDao;

    /** The document property dao. */
    @Autowired
    protected IDMSDocumentPropertyDao documentPropertyDao;

    /** The document file dao. */
    @Autowired
    protected IDMSDocumentFileDao documentFileDao;

    @Autowired
    protected IDMSCategoryDao categoryDao;

    /** The service file helper. */
    @Autowired
    protected DMSServiceFileHelper serviceFileHelper;

    /**
     * Document query.
     * @param attributes the attributes
     * @param criteria the criteria
     * @return the entity result
     */
    public EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) {
        return this.daoHelper.query(this.documentDao, criteria, attributes);
    }

    /**
     * Document insert.
     * @param av the av
     * @return the object
     */
    public DocumentIdentifier documentInsert(Map<?, ?> av) {
        // TODO cubir la información del usuario actual
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // UserInformation userInfo = (UserInformation) authentication.getPrincipal();
        // userInfo.getUsername();
        if (av.get(this.getColumnHelper().getDocumentOwnerColumn()) == null) {
            ((Map<Object, Object>) av).put(this.getColumnHelper().getDocumentOwnerColumn(), Integer.valueOf(1));
        }

        EntityResult res = this.daoHelper.insert(this.documentDao, av);

        DocumentIdentifier result = new DocumentIdentifier(
                (Serializable) res.get(this.getColumnHelper().getDocumentIdColumn()));
        return result;
    }

    /**
     * Document update.
     * @param documentId the document id
     * @param attributesValues the attributes values
     */
    public void documentUpdate(Serializable documentId, Map<?, ?> attributesValues) {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        this.daoHelper.update(this.documentDao, attributesValues, kv);
    }

    /**
     * Document delete.
     * @param documentId the document id
     * @throws DmsException
     */
    public void documentDelete(Serializable documentId) throws DmsException {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);

        // borramos las relaciones
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentRelatedMasterColumn(), documentId);
        this.relatedDocumentDao.unsafeDelete(kv);
        kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentRelatedChildColumn(), documentId);
        this.relatedDocumentDao.unsafeDelete(kv);
        kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        this.categoryDao.unsafeDelete(kv);

        // borramos los ficheros
        EntityResult resFiles = this.documentGetFiles(documentId, new HashMap<>(),
                EntityResultTools.attributes(this.getColumnHelper().getFileIdColumn()));
        List<Serializable> vDocumentId = (List<Serializable>) resFiles.get(this.getColumnHelper().getFileIdColumn());
        List<Path> toDelete = new ArrayList<>();
        if (vDocumentId != null) {
            for (Serializable fileId : vDocumentId) {

                EntityResult res = this.serviceFileHelper.fileGetVersions(fileId, new HashMap<>(),
                        EntityResultTools.attributes(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION));
                List<Serializable> fileVersionIds = (List<Serializable>) res
                    .get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);

                // borramos las versiones, sin borrar los ficheros
                List<Path> toDeletePartial = this.serviceFileHelper.deleteFileVersionsWithoutDeleteFiles(fileId,
                        fileVersionIds);

                // borramos el fichero
                Map<String, Object> kvFile = new HashMap<>();
                kvFile.put(this.getColumnHelper().getFileIdColumn(), fileId);
                this.daoHelper.delete(this.documentFileDao, kvFile);

                toDelete.addAll(toDeletePartial);
            }
        }
        // borramos las propiedades
        kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        this.documentPropertyDao.unsafeDelete(kv);

        // Borramos el documento
        kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        this.daoHelper.delete(this.documentDao, kv);

        // si todo fue bien y no se va a hacer rollback, borramos
        for (Path path : toDelete) {
            FileTools.deleteQuitely(path);
        }
    }

    /**
     * Document add properties.
     * @param documentId the document id
     * @param properties the properties
     */
    public void documentAddProperties(Serializable documentId, Map<String, String> properties) {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        CheckingTools.failIfNull(properties, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);

        Map<String, Object>[] batch = new HashMap[properties.size()];
        int i = 0;
        for (Entry<String, String> entry : properties.entrySet()) {
            Map<String, Object> av = new HashMap<>();
            av.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
            av.put(this.getColumnHelper().getPropertyKeyColumn(), entry.getKey());
            av.put(this.getColumnHelper().getPropertyValueColumn(), entry.getValue());
            batch[i++] = av;
        }
        this.documentPropertyDao.insertBatch(batch);
    }

    /**
     * Document delete properties.
     * @param documentId the document id
     * @param propertyKeys the property keys
     */
    public void documentDeleteProperties(Serializable documentId, List<String> propertyKeys) {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        CheckingTools.failIfNull(propertyKeys, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);
        if (propertyKeys.isEmpty()) {
            return;
        }

        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        kv.put(this.getColumnHelper().getPropertyKeyColumn(), new SearchValue(SearchValue.IN, propertyKeys));
        this.documentPropertyDao.unsafeDelete(kv);
    }

    /**
     * Document get property.
     * @param documentId the document id
     * @param propertyKey the property key
     * @return the string
     */
    public String documentGetProperty(Serializable documentId, String propertyKey) {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        CheckingTools.failIfNull(propertyKey, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        kv.put(this.getColumnHelper().getPropertyKeyColumn(), propertyKey);
        EntityResult rs = this.daoHelper.query(this.documentPropertyDao, kv,
                this.getColumnHelper().getPropertyColumns());
        if (rs.calculateRecordNumber() == 0) {
            return null;
        }
        return (String) rs.getRecordValues(0).get(this.getColumnHelper().getPropertyValueColumn());
    }

    /**
     * Document get properties.
     * @param documentId the document id
     * @param kv the kv
     * @return the map
     */
    public Map<String, String> documentGetProperties(Serializable documentId, Map<?, ?> kv) {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        ((Map<Object, Object>) kv).put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        EntityResult rs = this.daoHelper.query(this.documentPropertyDao, kv,
                this.getColumnHelper().getPropertyColumns());
        Map<String, String> res = new HashMap<>();
        int nregs = rs.calculateRecordNumber();
        for (int i = 0; i < nregs; i++) {
            Map<?, ?> record = rs.getRecordValues(i);
            res.put((String) record.get(this.getColumnHelper().getPropertyKeyColumn()),
                    (String) record.get(this.getColumnHelper().getPropertyValueColumn()));
        }
        return res;
    }

    /**
     * Document get files.
     * @param documentId the document id
     * @param kv the kv
     * @param attributes the attributes
     * @return the entity result
     */
    public EntityResult documentGetFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes) {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        ((Map<Object, Object>) kv).put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        return this.daoHelper.query(this.documentFileDao,kv,
                    attributes);
    }

    /**
     * Document get all files.
     * @param documentId the document id
     * @param kv the kv
     * @param attributes the attributes
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    public EntityResult documentGetAllFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes)
            throws DmsException {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        ((Map<Object, Object>) kv).put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        return this.daoHelper.query(this.documentFileDao, kv, attributes, "allfiles");
    }

    /**
     * Gets the related document.
     * @param documentId the document id
     * @return the related document
     */
    public EntityResult getRelatedDocument(Serializable documentId) {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getDocumentRelatedMasterColumn(), documentId);
        return this.daoHelper.query(this.relatedDocumentDao, kv,
                    this.getColumnHelper().getDocumentRelatedColumns());
    }

    /**
     * Sets the related documents.
     * @param masterDocumentId the master document id
     * @param childDocumentId the child document id
     */
    public void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId) {
        CheckingTools.failIfNull(masterDocumentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        CheckingTools.failIfNull(childDocumentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        Map<String, Object> av = new HashMap<>();
        av.put(this.getColumnHelper().getDocumentRelatedMasterColumn(), masterDocumentId);
        av.put(this.getColumnHelper().getDocumentRelatedChildColumn(), childDocumentId);
        this.daoHelper.insert(this.relatedDocumentDao, av);
    }

}
