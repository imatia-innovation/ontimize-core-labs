package com.ontimize.jee.server.services.dms;

import java.io.Serializable;
import java.nio.file.Path;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.IDMSService;

/**
 * Funcionalidades extendidas del DMS s�lo disponibles desde el lado del servidor
 *
 */
public interface IDMSServiceServer extends IDMSService {

    /**
     * Obtiene el path de un fichero.
     * @param fileId the file id
     * @return the input stream
     * @throws DmsException the ontimize jee runtime exception
     * @throws DmsException
     */
    Path fileGetPath(Serializable fileId) throws DmsException;

    /**
     * Obtiene el path de una versi�n dada de un fichero.
     * @param fileVersionId the file version id
     * @return the input stream
     * @throws DmsException the ontimize jee runtime exception
     */
    Path fileGetPathOfVersion(Serializable fileVersionId) throws DmsException;

    /**
     * Update settings.
     */
    void updateSettings() throws DmsException;

}
