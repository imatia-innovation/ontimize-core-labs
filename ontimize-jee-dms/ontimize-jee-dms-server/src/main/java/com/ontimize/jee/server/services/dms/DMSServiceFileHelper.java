package com.ontimize.jee.server.services.dms;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.ontimize.jee.server.dao.common.INameConvention;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileVersionDao;
import com.ontimize.jee.server.spring.namespace.OntimizeDMSConfiguration;

@Component
@Lazy(value = true)
public class DMSServiceFileHelper extends AbstractDMSServiceHelper {

    @Autowired
    DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private INameConvention nameConvention;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DMSServiceFileHelper.class);

    /** The path name id formatter. */
    private static DecimalFormat pathNameIdFormatter = new DecimalFormat("#");

    /** The ontimize configuration. */
    @Autowired
    protected OntimizeDMSConfiguration ontimizeDMSConfiguration;

    /** The document file version dao. */
    @Autowired
    protected IDMSDocumentFileVersionDao documentFileVersionDao;

    /** The document file dao. */
    @Autowired
    protected IDMSDocumentFileDao documentFileDao;

    /**
     * File get content.
     * @param fileId the file id
     * @return the input stream
     * @throws DmsException
     */
    public InputStream fileGetContent(Serializable fileId) throws DmsException {
        Serializable versionId = this.getCurrentFileVersion(fileId);
        CheckingTools.failIfNull(versionId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        return this.fileGetContentOfVersion(versionId);
    }

    /**
     * Returns the file physical path.
     * @param fileId
     * @return the file path
     * @throws DmsException
     */
    public Path fileGetPath(Serializable fileId) throws DmsException {
        Serializable versionId = this.getCurrentFileVersion(fileId);
        CheckingTools.failIfNull(versionId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        return this.fileGetPathOfVersion(versionId);
    }

    /**
     * File get content of version.
     * @param fileVersionId the file version id
     * @return the input stream
     * @throws DmsException
     */
    public InputStream fileGetContentOfVersion(Serializable fileVersionId) throws DmsException {
        Path file = this.getPhysicalFileFor(fileVersionId);
        try {
            return Files.newInputStream(file);
        } catch (IOException error) {
            throw new DmsException(error);
        }
    }

    /**
     * Returns the file physical path.
     * @param fileVersionId
     * @return the file path
     * @throws DmsException
     */
    public Path fileGetPathOfVersion(Serializable fileVersionId) throws DmsException {
        return this.getPhysicalFileFor(fileVersionId);
    }

    /**
     * File insert.
     * @param documentId the document id
     * @param av the av
     * @param is the is
     * @return the object
     * @throws DmsException
     */
    public DocumentIdentifier fileInsert(Serializable documentId, Map<?, ?> av, InputStream is) throws DmsException {
        String fileName = (String) av.get(nameConvention.convertName(DMSNaming.DOCUMENT_FILE_NAME));
        CheckingTools.failIfNull(fileName, DMSNaming.ERROR_FILE_NAME_MANDATORY);
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);

        // insertamos en la tabla de ficheros
        Map<Object, Object> avFile = new HashMap<>();
        avFile.putAll(av);// Pass other columns (extended implementations)
        avFile.put(this.getColumnHelper().getFileNameColumn(), fileName);
        avFile.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
        EntityResult res = this.daoHelper.insert(this.documentFileDao, avFile);
        Serializable fileId = (Serializable) res.get(this.getColumnHelper().getFileIdColumn());
        CheckingTools.failIfNull(fileName, DMSNaming.ERROR_ERROR_CREATING_FILE);

        // insertamos en las versiones
        Serializable fileVersionId = this.createNewVersionForFile(fileId, av, is);

        DocumentIdentifier result = new DocumentIdentifier(documentId, fileId, fileVersionId);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object ,
     * java.util.Map, java.io.InputStream)
     */
    /**
     * File update.
     * @param fileId the file id
     * @param attributesValues the attributes values
     * @param is the is
     * @throws DmsException
     */
    public DocumentIdentifier fileUpdate(Serializable fileId, Map<?, ?> attributesValues, InputStream is)
            throws DmsException {
        CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);

        // si el inputstream es nulo actualizamos los campos
        if (is == null) {
            // Obtenemos el id de la versi�n actual
            Map<String, Object> kv = new HashMap<>();
            kv.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
            kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
            EntityResult er = this.daoHelper.query(this.documentFileVersionDao, kv,
                    EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()));
            CheckingTools.failIf(er.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
            Serializable currentVersionId = (Serializable) er.getRecordValues(0)
                .get(this.getColumnHelper().getVersionIdColumn());
            this.updateCurrentVersionAttributes(fileId, currentVersionId, attributesValues);
            return this.rescueDocumentIdentifiers(null, fileId, currentVersionId);
        } else {
            // En este caso hay que crear una nueva versi�n
            // Si viene el nombre del fichero lo actualizamos en la tabla de ficheros
            String fileName = (String) attributesValues.remove(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_NAME));
            if (fileName != null) {
                Map<String, Object> kv = new HashMap<>();
                kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
                Map<String, Object> avUpdate = new HashMap<>();
                avUpdate.put(this.getColumnHelper().getFileNameColumn(), fileName);
                this.daoHelper.update(this.documentFileDao, avUpdate, kv);
            }
            Serializable versionId = this.createNewVersionForFile(fileId, attributesValues, is);
            return this.rescueDocumentIdentifiers(null, fileId, versionId);
        }
    }

    /**
     * Upload (override) the content of one version of file.
     * @param versionId
     * @param is
     * @return
     * @throws DmsException
     */
    public DocumentIdentifier fileVersionOverrideContent(Serializable versionId, InputStream is) throws DmsException {
        DocumentIdentifier docIds = this.rescueDocumentIdentifiers(null, null, versionId);
        Path file = this.getPhysicalFileFor(docIds.getDocumentId(), docIds.getFileId(), docIds.getVersionId());
        this.updateFileContent(versionId, file, is);
        return docIds;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
     */
    /**
     * File query.
     * @param criteria the criteria
     * @param attributes the attributes
     * @return the entity result
     */
    public EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) {
        // TODO anadir filtro de dueno?
        return this.daoHelper.query(this.documentFileDao, criteria, attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
     */
    /**
     * File delete.
     * @param fileId the file id
     * @throws DmsException
     */
    public void fileDelete(Serializable fileId) throws DmsException {
        CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        EntityResult res = this.fileGetVersions(fileId, new HashMap<>(),
                EntityResultTools.attributes(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION)));
        List<Serializable> fileVersionIds = (List<Serializable>) res
            .get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION));

        // borramos las versiones, sin borrar los ficheros
        List<Path> toDelete = this.deleteFileVersionsWithoutDeleteFiles(fileId, fileVersionIds);

        // borramos el fichero
        Map<String, Object> kvFile = new HashMap<>();
        kvFile.put(this.getColumnHelper().getFileIdColumn(), fileId);
        this.daoHelper.delete(this.documentFileDao, kvFile);
        // si todo fue bien y no se va a hacer rollback, borramos
        for (Path file : toDelete) {
            FileTools.deleteQuitely(file);
        }
    }

    /**
     * File version query.
     * @param fileVersionId the file version id
     * @param attributes the attributes
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    public EntityResult fileVersionQuery(Serializable fileVersionId, List<?> attributes) {
        HashMap<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getVersionIdColumn(), fileVersionId);
        return this.daoHelper.query(this.documentFileVersionDao, kv, attributes);
    }

    /**
     * File get versions.
     * @param fileId the file id
     * @param kv the kv
     * @param attributes the attributes
     * @return the entity result
     */
    public EntityResult fileGetVersions(Serializable fileId, Map<?, ?> kv, List<?> attributes) {
        CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        ((Map<Object, Object>) kv).put(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE), fileId);
        return this.daoHelper.query(this.documentFileVersionDao, kv, attributes);
    }

    /**
     * File - revocer previous version
     * @param fileId the file id
     * @param acceptNotPreviousVersion
     * @return
     * @throws DmsException
     */
    public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion) throws DmsException {
        EntityResult availableVersions = this.fileGetVersions(fileId, new HashMap(),
                this.getColumnHelper().getVersionColumns());
        if ((!acceptNotPreviousVersion && (availableVersions == null))
                || (availableVersions.calculateRecordNumber() < 2)) {
            throw new DmsException("E_NOT_AVAILABLE_VERSIONS_TO_RECOVER");
        }

        // Detect current version and previous
        List<Serializable> vId = (List) availableVersions
            .get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
        List<Serializable> vActive = (List) availableVersions.get(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE);
        List<Number> vVersion = (List<Number>) availableVersions.get(DMSNaming.DOCUMENT_FILE_VERSION_VERSION);
        int currentVersionIdx = vActive.indexOf(OntimizeDMSEngine.ACTIVE);
        if (currentVersionIdx < 0) {
            throw new DmsException("E_NOT_CURRENT_ACTIVE_VERSION");
        }

        long currentVersion = vVersion.get(currentVersionIdx).longValue();
        // Look for max previous version of current (usually currentVersion-1)
        int previousVersionIdx = -1;
        long previousVersion = 0;
        int idx = 0;
        for (Number version : vVersion) {
            if ((version.longValue() > previousVersion) && (version.longValue() < currentVersion)) {
                previousVersion = version.longValue();
                previousVersionIdx = idx;
            }
            idx++;
        }
        if ((previousVersionIdx < 0) && !acceptNotPreviousVersion) {
            throw new DmsException("E_INVALID_PREVIOUS_VERSION");
        }

        if (previousVersionIdx >= 0) {
            // Mark previous version as current active
            HashMap kv = new HashMap<>();
            kv.put(this.getColumnHelper().getVersionIdColumn(), vId.get(previousVersionIdx));
            Map<String, Object> avUpdate = new HashMap<>();
            avUpdate.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
            this.daoHelper.update(this.documentFileVersionDao, avUpdate, kv);
        }

        // Delete last version
        Path file = this.deleteFileVersionWithoutDeleteFile(fileId, vId.get(currentVersionIdx));
        FileTools.deleteQuitely(file);
    }

    /* ############## */
    /* HELPER METHODS */
    /* ############## */

    /**
     * Obtiene la ruta física a un fichero.
     * @param idVersion the id version
     * @return the physical file for
     * @throws DmsException
     */
    protected Path getPhysicalFileFor(Serializable idVersion) throws DmsException {
        CheckingTools.failIfNull(idVersion, DMSNaming.ERROR_FILE_VERSION_ID_IS_MANDATORY);
        Map<String, Object> kvVersion = new HashMap<>();
        kvVersion.put(this.getColumnHelper().getVersionIdColumn(), idVersion);
        EntityResult res = this.daoHelper.query(this.documentFileVersionDao, kvVersion,
                EntityResultTools.attributes(this.getColumnHelper().getVersionVersionColumn(),
                        this.getColumnHelper().getFileIdColumn()));
        CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_VERSION_NOT_FOUND);
        Map<?, ?> rv = res.getRecordValues(0);
        Serializable fileId = (Serializable) rv.get(this.getColumnHelper().getFileIdColumn());

        Map<String, Object> kvFile = new HashMap<>();
        kvFile.put(this.getColumnHelper().getFileIdColumn(), fileId);
        res = this.daoHelper.query(this.documentFileDao, kvFile,
                EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn()), "allfiles");
        CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_NOT_FOUND);

        rv = res.getRecordValues(0);
        Serializable documentId = (Serializable) rv.get(this.getColumnHelper().getDocumentIdColumn());
        return this.getPhysicalFileFor(documentId, fileId, idVersion);
    }

    /**
     * Obtiene la ruta física a un fichero.
     * @param documentId the document id
     * @param fileId the file id
     * @param versionId the version id
     * @return the physical file for
     * @throws DmsException
     */
    protected Path getPhysicalFileFor(Serializable documentId, Serializable fileId, Serializable versionId)
            throws DmsException {
        CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        CheckingTools.failIfNull(versionId, DMSNaming.ERROR_NO_FILE_VERSION);
        String id = DMSServiceFileHelper.pathNameIdFormatter.format(fileId);
        String version = DMSServiceFileHelper.pathNameIdFormatter.format(versionId);
        return this.getDocumentBasePathForDocumentId(documentId).resolve(id + "_" + version);
    }

    /**
     * Gets the document base path for document id.
     * @param documentId the document id
     * @return the document base path for document id
     * @throws DmsException
     */
    protected Path getDocumentBasePathForDocumentId(Serializable documentId) throws DmsException {
        CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
        return this.getDocumentsBasePath().resolve(DMSServiceFileHelper.pathNameIdFormatter.format(documentId));
    }

    /**
     * Gets the documents base path.
     * @return the documents base path
     * @throws DmsException
     */
    protected Path getDocumentsBasePath() throws DmsException {
        Path path = ((OntimizeDMSEngine) this.ontimizeDMSConfiguration.getDmsConfiguration().getEngine())
            .getDocumentsBasePath();
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new DmsException("Could not create base folder for DMS", e);
            }
        }
        return path;
    }

    /**
     * Delete file versions without delete files.
     * @param fileId the file id
     * @param fileVersionIds the file version ids
     * @return the list
     * @throws DmsException
     */
    public List<Path> deleteFileVersionsWithoutDeleteFiles(Serializable fileId, List<Serializable> fileVersionIds)
            throws DmsException {
        List<Path> toDelete = new ArrayList<>(fileVersionIds.size());
        for (Serializable idVersion : fileVersionIds) {
            Path deleteFileVersion = this.deleteFileVersionWithoutDeleteFile(fileId, idVersion);
            toDelete.add(deleteFileVersion);
        }
        return toDelete;
    }

    /**
     * Delete a file version.
     * @param fileId the file id
     * @param idVersion the id version
     * @return the file that need to be deleted
     * @throws DmsException
     */
    protected Path deleteFileVersionWithoutDeleteFile(Serializable fileId, Serializable idVersion) throws DmsException {
        Map<String, Object> kvVersion = new HashMap<>();
        Path physicalFileFor = this.getPhysicalFileFor(idVersion);
        kvVersion.put(this.getColumnHelper().getVersionIdColumn(), idVersion);
        this.daoHelper.delete(this.documentFileVersionDao, kvVersion);
        return physicalFileFor;
    }

    /**
     * Update current version attributes.
     * @param fileId the file id
     * @param currentVersionId the current version id
     * @param attributesValues the attributes values
     * @throws DmsException
     */
    protected void updateCurrentVersionAttributes(Serializable fileId, Serializable currentVersionId,
            Map<?, ?> attributesValues) throws DmsException {
        // Split columns for FILE and for VERSION daos
        List<String> columnsDocumentFile = this.getColumnHelper().getFileColumns();
        List<String> columnsDocumentFileVersion = this.getColumnHelper().getVersionColumns();
        Map<String, Object> avFile = new HashMap<>();
        Map<String, Object> avVersion = new HashMap<>();
        attributesValues = this.getColumnHelper().translate(attributesValues);
        for (Entry<?, ?> entry : attributesValues.entrySet()) {
            if (columnsDocumentFile.contains(entry.getKey())) {
                avFile.put((String) entry.getKey(), entry.getValue());
            }
            if (columnsDocumentFileVersion.contains(entry.getKey())) {
                avVersion.put((String) entry.getKey(), entry.getValue());
            }
        }
        if (avFile.isEmpty() && avVersion.isEmpty()) {
            throw new DmsException("dms.E_NO_DATA_TO_UPDATE");
        }

        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
        this.daoHelper.update(this.documentFileDao, avFile, kv);

        kv = new HashMap<>();
        kv.put(this.getColumnHelper().getVersionIdColumn(), currentVersionId);
        this.daoHelper.update(this.documentFileVersionDao, avVersion, kv);
    }

    /**
     * Returns current active version of a file
     * @param fileId the file id
     * @return the current file version
     */
    protected Serializable getCurrentFileVersion(Serializable fileId) {
        CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
        kv.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
        EntityResult res = this.daoHelper.query(this.documentFileVersionDao, kv,
                EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()));
        CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
        Serializable versionId = (Serializable) res.getRecordValues(0).get(this.getColumnHelper().getVersionIdColumn());
        return versionId;
    }

    /**
     * Ensures to deprecate current ACTIVE version of file (if exists) and return next DEFAULT AUTOMATIC
     * version.
     * @param fileId the file id
     * @return the current file version and deprecate
     */
    protected Number getCurrentFileVersionAndDeprecate(Serializable fileId) {
        Number fileVersion = Long.valueOf(1);

        // cogemos la referencia a la versi�n actual
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
        EntityResult er = this.fileQuery(kv, EntityResultTools.attributes(
                this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION),  this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_VERSION)));
        CheckingTools.failIf(er.calculateRecordNumber() > 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);

        // Si existe la marcamos como no activa y sumamos 1 a la versi�n que vamos a insertar
        if (er.calculateRecordNumber() == 1) {
            Map<?, ?> record = er.getRecordValues(0);
            Serializable oldVersionId = (Serializable) record
                .get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION));
            if (oldVersionId != null) {
                Number oldVersion = (Number) record.get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_VERSION));
                fileVersion = Long.valueOf(oldVersion.longValue() + 1);

                kv = new HashMap<>();
                kv.put(this.getColumnHelper().getVersionIdColumn(), oldVersionId);
                Map<String, Object> avUpdate = new HashMap<>();
                avUpdate.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.INACTIVE);
                this.daoHelper.update(this.documentFileVersionDao, avUpdate, kv);
            }
        }
        return fileVersion;
    }

    /**
     * Move files to a new idDmsDoc. It ensures to change the reference in database and move files to
     * the new directory
     * @param idDmsDocFiles
     * @param idDmsDoc
     * @throws DmsException
     */
    public void moveFilesToDoc(List<Serializable> idDmsDocFiles, Serializable idDmsDoc) throws DmsException {
        // Comprobamos par�metros
        if ((idDmsDocFiles == null) || idDmsDocFiles.isEmpty()) {
            throw new DmsException("ErrorNoDocFiles");
        }
        if ((idDmsDoc == null) || (idDmsDoc instanceof NullValue)) {
            throw new DmsException("ErrorNoIdDmsDoc");
        }
        Map<String, Object> av = new HashMap<>();
        av.put(this.getColumnHelper().getDocumentIdColumn(), idDmsDoc);
        Map<String, Object> kv = new HashMap<>();

        for (final Serializable idDmsDocFile : idDmsDocFiles) {
            // Cogemos todas las versiones del fichero que vamos a mover
            Serializable documentId = this.getDocumentIdForFile(idDmsDocFile);
            Path pathIdDMSDocFile = this.getDocumentBasePathForDocumentId(documentId);
            File dir = pathIdDMSDocFile.toFile();
            File[] foundFiles = dir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(idDmsDocFile.toString());
                }
            });
            // Movemos los ficheros al nuevo directorio determinado por el par�metros idDmsDoc
            for (File file : foundFiles) {
                try {
                    FileUtils.moveToDirectory(file, this.getDocumentBasePathForDocumentId(idDmsDoc).toFile(), true);
                } catch (IOException error) {
                    throw new DmsException(error);
                }
            }
            // Actualizamos las referencias en base de datos
            kv.put(this.getColumnHelper().getFileIdColumn(), idDmsDocFile);
            this.daoHelper.update(this.documentFileDao, av, kv);
        }
    }

    /**
     * Creates the new version for file.
     * @param fileId the file id
     * @param attributes the attributes
     * @param is the is
     * @return the Object
     * @throws DmsException
     */
    protected Serializable createNewVersionForFile(Serializable fileId, Map<?, ?> attributes, InputStream is)
            throws DmsException {
        Serializable documentId = this.getDocumentIdForFile(fileId);
        Serializable fileVersion = null;

        // Si recibimos la versi�n por parte del usuario utilizamos esa, siempre y cuando no exista ya
        if (attributes.containsKey(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_VERSION))) {
            fileVersion = (Serializable) attributes.get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_VERSION));
            // Check if conflict
            Map<Object, Object> kvCheck = new HashMap<>();
            kvCheck.put(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_VERSION), fileVersion);
            EntityResult resVersions = this.fileGetVersions(fileId, kvCheck,
                    EntityResultTools.attributes(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_VERSION)));
            CheckingTools.failIf(resVersions.calculateRecordNumber() > 0, DMSNaming.ERROR_VERSION_ALREADY_EXISTS);
        } else {
            fileVersion = this.getCurrentFileVersionAndDeprecate(fileId);
        }

        CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        CheckingTools.failIfNull(is, DMSNaming.ERROR_INPUTSTREAM_IS_MANDATORY);
        Map<String, Object> avVersion = new HashMap<>();
        avVersion.put(this.getColumnHelper().getFileIdColumn(), fileId);
        avVersion.put(this.getColumnHelper().getVersionAddedDateColumn(), new Date());
        avVersion.put(this.getColumnHelper().getVersionAddedUserColumn(), this.getUser()); // TODO pendiente de tener la
                                                                                           // informaci�n en el userinfo
        avVersion.put(this.getColumnHelper().getVersionDescriptionColumn(),
                attributes.get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION)));
        avVersion.put(this.getColumnHelper().getVersionPathColumn(),
                attributes.get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH)));
        avVersion.put(this.getColumnHelper().getVersionActiveColumn(),
                attributes.containsKey(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE))
                        ? attributes.get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE)) : OntimizeDMSEngine.ACTIVE);
        avVersion.put(this.getColumnHelper().getVersionVersionColumn(), fileVersion);
        MapTools.safePut(avVersion, this.getColumnHelper().getVersionThumbnailColumn(),
                attributes.get(this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL)));
        EntityResult resVersion = this.daoHelper.insert(this.documentFileVersionDao, avVersion);
        Serializable versionId = (Serializable) resVersion.get(this.getColumnHelper().getVersionIdColumn());
        CheckingTools.failIfNull(versionId, DMSNaming.ERROR_CREATING_FILE_VERSION);
        Path file = this.getPhysicalFileFor(documentId, fileId, versionId);
        CheckingTools.failIf(Files.exists(file), DMSNaming.ERROR_FILE_ALREADY_EXISTS);
        this.updateFileContent(versionId, file, is);
        return versionId;
    }

    private void updateFileContent(Serializable versionId, Path file, InputStream is) throws DmsException {
        try {
            Files.createDirectories(file.getParent());
            // TODO ver si es necesario hacer esto antes para no ocupar memoria,
            // aunque las operaciones anteriores deber?an ser inmediatas
            long time = System.currentTimeMillis();
            try (OutputStream output = Files.newOutputStream(file)) {
                IOUtils.copy(is, output);
            }
            // update filesize
            long fileSize = Files.size(file);
            Map<String, Object> kvVersion = new HashMap<>();
            kvVersion.put(this.getColumnHelper().getVersionIdColumn(), versionId);
            Map<String, Object> avVersionSize = new HashMap<>();
            avVersionSize.put(this.getColumnHelper().getVersionSizeColumn(), fileSize);
            this.daoHelper.update(this.documentFileVersionDao, avVersionSize, kvVersion);

            DMSServiceFileHelper.logger.debug("Time copying file: {}", System.currentTimeMillis() - time);
        } catch (Exception error) {
            FileTools.deleteQuitely(file);
            throw new DmsException(error);
        }
    }

    private Object getUser() {
        try {
            Map<Object, Object> otherData = ((UserInformation) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()).getOtherData();
            Object userId = ObjectTools.coalesce(otherData.get("USER_ID"), otherData.get("USR_ID"),
                    otherData.get("ID_USER"));
            CheckingTools.failIfNull(userId, "E_NULL_USER", new Object[0]);
            return userId;
        } catch (Exception ex) {
            return Integer.valueOf(1);
        }
    }

    /**
     * Gets the document id for file.
     * @param fileId the file id
     * @return the document id for file
     */
    protected Serializable getDocumentIdForFile(Serializable fileId) {
        CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
        EntityResult res = this.daoHelper.query(this.documentFileDao, kv,
                EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn()), "allfiles");
        CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_DOCUMENT_NOT_FOUND);
        return (Serializable) res.getRecordValues(0).get(this.getColumnHelper().getDocumentIdColumn());
    }

    public void moveFilesToCategory(Serializable idCategory, List<Serializable> idFiles) {
        if ((idFiles == null) || idFiles.isEmpty()) {
            return;
        }
        Map<String, Object> av = new HashMap<>();
        av.put(this.getColumnHelper().getCategoryIdColumn(), idCategory == null ? new NullValue() : idCategory);
        Map<String, Object> kv = new HashMap<>();
        kv.put(this.getColumnHelper().getFileIdColumn(), new SearchValue(SearchValue.IN, new Vector<>(idFiles)));
        this.documentFileDao.unsafeUpdate(av, kv);
    }

    /**
     * Utility to rescue full DocumentIdentifier values from some filter (docId, fileId or VersionId)
     * @param docId
     * @param fileId
     * @param versionId
     * @return
     * @throws DmsException
     */
    protected DocumentIdentifier rescueDocumentIdentifiers(Serializable docId, Serializable fileId,
            Serializable versionId) throws DmsException {
        CheckingTools.failIf((docId == null) && (fileId == null) && (versionId == null),
                "E_INVALID_FILTER_TO_LOCATE_FILE");
        if (versionId != null) {
            Map<Object, Object> kv = EntityResultTools.keysvalues(this.getColumnHelper().getVersionIdColumn(),
                    versionId);
            MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), docId);
            MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), fileId);
            List av = EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn(),
                    this.getColumnHelper().getFileIdColumn(),
                    this.getColumnHelper().getVersionIdColumn());
            EntityResult resQuery = this.daoHelper.query(this.documentFileVersionDao, kv, av);
            CheckingTools.failIf((resQuery == null) || (resQuery.calculateRecordNumber() < 1), "E_INVALID_FILTERS");
            Map values = resQuery.getRecordValues(0);
            return new DocumentIdentifier(//
                    (Serializable) values.get(this.getColumnHelper().getDocumentIdColumn()), //
                    (Serializable) values.get(this.getColumnHelper().getFileIdColumn()), //
                    (Serializable) values.get(this.getColumnHelper().getVersionIdColumn()));
        } else if (fileId != null) {
            Map<Object, Object> kv = EntityResultTools.keysvalues(this.getColumnHelper().getFileIdColumn(), fileId);
            MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), docId);
            List<String> av = EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn());
            EntityResult resQuery = this.fileQuery(kv, av);
            CheckingTools.failIf((resQuery == null) || (resQuery.calculateRecordNumber() < 1), "E_INVALID_FILTERS");
            Map values = resQuery.getRecordValues(0);
            return new DocumentIdentifier(//
                    (Serializable) values.get(this.getColumnHelper().getDocumentIdColumn()), //
                    (Serializable) values.get(this.getColumnHelper().getFileIdColumn()), //
                    (Serializable) values.get(this.getColumnHelper().getFileIdColumn()));
        } else if (docId != null) {
            return new DocumentIdentifier(docId);
        }
        throw new DmsException("E_INVALID_FILTERS");
    }

}
