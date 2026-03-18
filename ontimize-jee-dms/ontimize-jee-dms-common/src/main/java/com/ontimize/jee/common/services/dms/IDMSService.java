package com.ontimize.jee.common.services.dms;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;

/**
 * Servicio para la gestion de documentos, propiedades de documentos, ficheros y versiones de
 * ficheros.
 *
 */
public interface IDMSService {

    /**
     * Obtiene el contenido de un fichero.
     * @param fileId the file id
     * @return the input stream
     * @throws DmsException the ontimize jee runtime exception
     */
    InputStream fileGetContent(Serializable fileId) throws DmsException;

    /**
     * Inserta un fichero al documento indicado.
     * @param documentId the document id
     * @param av the av
     * @param file the file
     * @return el identificador del fichero insertado
     * @throws DmsException the ontimize jee runtime exception
     */
    DocumentIdentifier fileInsert(Serializable documentId, Map<?, ?> av, InputStream file) throws DmsException;

    /**
     * Búsqueda sobre el repositorio de ficheros.
     * @param criteria the criteria
     * @param attributes the attributes
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) throws DmsException;

    /**
     * Elimina un fichero.
     * @param fileId the file id
     * @throws DmsException the ontimize jee runtime exception
     */
    void fileDelete(Serializable fileId) throws DmsException;

    /**
     * Actualiza un fichero. Si file != null, se crea una nueva versi�n
     * @param fileId the file id
     * @param attributesValues the attributes values
     * @param file the file
     * @throws DmsException the ontimize jee runtime exception
     */
    DocumentIdentifier fileUpdate(Serializable fileId, Map<?, ?> attributesValues, InputStream file)
            throws DmsException;

    /**
     * Allows to modify file version content, that is override current version content.
     * @param fileVersionId
     * @param toChange
     * @param bufferedInputStream
     * @return
     * @throws DmsException the ontimize jee runtime exception
     */
    DocumentIdentifier fileVersionOverrideContent(Serializable fileVersionId, InputStream file) throws DmsException;

    /**
     * Obtiene las versiones de un fichero.
     * @param fileId the file id
     * @param kv the kv
     * @param attributes the attributes
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    EntityResult fileGetVersions(Serializable fileId, Map<?, ?> kv, List<?> attributes) throws DmsException;

    /**
     * Obtiene los datos de una versi�n concreta de un fichero.
     * @param fileVersionId the file version id
     * @param attributes the attributes
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    EntityResult fileVersionQuery(Serializable fileVersionId, List<?> attributes) throws DmsException;

    /**
     * Obtiene una versi�n dada de un fichero.
     * @param fileVersionId the file version id
     * @return the input stream
     * @throws DmsException the ontimize jee runtime exception
     */
    InputStream fileGetContentOfVersion(Serializable fileVersionId) throws DmsException;

    /**
     * Delete last version of this file and mark previous as current active. If no previous version
     * available, operation must be aborted and error must be raised.
     * @param fileVersionId the file version id
     * @param acceptNotPreviousVersion if false and not previous version located (for instance the only
     *        one) exception will be raise, else file will continue without any version
     * @return
     * @throws DmsException
     */
    void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion) throws DmsException;

    /**
     * Consulta documentos en base a un criterio.
     * @param attributes the attributes
     * @param criteria the criteria
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) throws DmsException;

    /**
     * Inserta un documento.
     * @param av the av
     * @return el id del documento insertado
     * @throws DmsException the ontimize jee runtime exception
     */
    DocumentIdentifier documentInsert(Map<?, ?> av) throws DmsException;

    /**
     * Actualiza un documento.
     * @param documentId the document id
     * @param attributesValues the attributes values
     * @throws DmsException the ontimize jee runtime exception
     */
    void documentUpdate(Serializable documentId, Map<?, ?> attributesValues) throws DmsException;

    /**
     * Elimina un documento.
     * @param documentId the document id
     * @throws DmsException the ontimize jee runtime exception
     */
    void documentDelete(Serializable documentId) throws DmsException;

    /**
     * Añade propiedades a un documento.
     * @param documentId the document id
     * @param properties the properties
     * @throws DmsException the ontimize jee runtime exception
     */
    void documentAddProperties(Serializable documentId, Map<String, String> properties) throws DmsException;

    /**
     * Elimina propiedades de un documento.
     * @param documentId the document id
     * @param propertyKeys the property keys
     * @throws DmsException the ontimize jee runtime exception
     */
    void documentDeleteProperties(Serializable documentId, List<String> propertyKeys) throws DmsException;

    /**
     * Obtiene una propiedad de un documento.
     * @param documentId the document id
     * @param propertyKey the property key
     * @return the string
     * @throws DmsException the ontimize jee runtime exception
     */
    String documentGetProperty(Serializable documentId, String propertyKey) throws DmsException;

    /**
     * Otiene las propiedades de un documento.
     * @param documentId the document id
     * @param kv the kv
     * @return the map
     * @throws DmsException the ontimize jee runtime exception
     */
    Map<String, String> documentGetProperties(Serializable documentId, Map<?, ?> kv) throws DmsException;

    /**
     * Obtiene los ficheros de un documento. Por defecto s�lo devuelve los ficheros/versiones activos
     * @param documentId the document id
     * @param kv the kv
     * @param attributes the attributes
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    EntityResult documentGetFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes) throws DmsException;

    /**
     * Obtiene TODOS los ficheros de un documento.
     * @param documentId the document id
     * @param kv the kv
     * @param attributes the attributes
     * @return the entity result
     * @throws DmsException the ontimize jee runtime exception
     */
    EntityResult documentGetAllFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes) throws DmsException;

    /**
     * Obtiene los documentos relacionados con un documento.
     * @param documentId the document id
     * @return the related document
     * @throws DmsException the ontimize jee runtime exception
     */
    EntityResult getRelatedDocument(Serializable documentId) throws DmsException;

    /**
     * Establece una relación entre dos documentos.
     * @param documentId the document id
     * @param otherDocumentId the other document id
     * @throws DmsException the ontimize jee runtime exception
     */
    void setRelatedDocuments(Serializable documentId, Serializable otherDocumentId) throws DmsException;

    /**
     * Category get for document.
     * @param documentId the document id
     * @param attributes the attributes
     * @return the DMS category
     * @throws DmsException the ontimize jee runtime exception
     */
    DMSCategory categoryGetForDocument(Serializable documentId, List<?> attributes) throws DmsException;

    /**
     * Category insert.
     * @param documentId the document id
     * @param name the name
     * @param parentCategoryId the parent category id
     * @param otherData the other data
     * @return the object
     * @throws DmsException the ontimize jee runtime exception
     */
    Serializable categoryInsert(Serializable documentId, String name, Serializable parentCategoryId,
            Map<?, ?> otherData) throws DmsException;

    /**
     * Category update.
     * @param idCategory the id category
     * @param av the av
     * @throws DmsException the ontimize jee runtime exception
     */
    void categoryUpdate(Serializable idCategory, Map<?, ?> av) throws DmsException;

    /**
     * Category delete.
     * @param idCategory the id category
     * @throws DmsException the ontimize jee runtime exception
     */
    void categoryDelete(Serializable idCategory) throws DmsException;

    /**
     * Move files to cagetory.
     * @param idCategory the id category
     * @param idFiles the id files
     * @throws DmsException the ontimize jee runtime exception
     */
    void moveFilesToCategory(Serializable idCategory, List<Serializable> idFiles) throws DmsException;

}
