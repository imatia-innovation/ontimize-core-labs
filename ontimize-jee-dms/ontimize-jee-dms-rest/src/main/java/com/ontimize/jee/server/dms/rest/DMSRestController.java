package com.ontimize.jee.server.dms.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.ontimize.jee.server.dao.common.INameConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.rest.FileListParameter;
import com.ontimize.jee.server.rest.InsertParameter;
import com.ontimize.jee.server.rest.QueryParameter;
import com.ontimize.jee.server.rest.UpdateFileParameter;

public abstract class DMSRestController<T extends IDMSService, N extends IDMSNameConverter> {

    protected static final String CATEGORY_FILTER_KEY = "FM_FOLDER_PARENT_KEY";

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(DMSRestController.class);

    public abstract T getService();

    /** The bean property converter. */
    @Autowired(required = false)
    private N dmsNameConverter;


    /**
     * Get document files. Only active files/versions by default
     * @param workspaceId the work space identifier
     * @param queryParam the query parameters
     * @return
     */
    @RequestMapping(path = "/queryFiles/{workspaceId}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<? extends OFile>> documentGetFiles(@PathVariable("workspaceId") Integer workspaceId,
            @RequestBody QueryParameter queryParam) {
        try {
            List<OFile> files = new ArrayList<>();
            Map<Object, Object> filter = queryParam.getFilter();
            List<Object> columns = queryParam.getColumns();

            Object parentCategory = null;
            if (filter.containsKey(DMSRestController.CATEGORY_FILTER_KEY)) {
                parentCategory = filter.remove(DMSRestController.CATEGORY_FILTER_KEY);
                filter.put(this.dmsNameConverter.getCategoryIdColumn(), parentCategory);
            } else {
                filter.put(this.dmsNameConverter.getCategoryIdColumn(), new SearchValue(SearchValue.NULL, null));
            }

            // Get categories and add them as folders
            DMSCategory categories = this.getService()
                .categoryGetForDocument(workspaceId, this.getCategoryColumns(columns));
            this.getCategoriesAsFiles(categories, parentCategory, files, false);

            // Get files and add them
            EntityResult er = this.getService().documentGetFiles(workspaceId, filter, this.getFileColumns(columns));
            if (EntityResult.OPERATION_WRONG != er.getCode()) {
                for (int i = 0; i < er.calculateRecordNumber(); i++) {
                    files.add(this.dmsNameConverter.createOFile(er.getRecordValues(i)));
                }
            }
            return new ResponseEntity<List<? extends OFile>>(files, HttpStatus.OK);
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get file content
     * @param fileId the file identifier
     * @param response
     */
    @RequestMapping(path = "/getFile/{fileId}", method = RequestMethod.GET, produces = "application/octet-stream")
    public @ResponseBody void fileGetContent(@PathVariable("fileId") Integer fileId, HttpServletResponse response) {
        InputStream fis = null;
        try {
            Map<Object, Object> criteria = new HashMap<>();
            criteria.put(this.dmsNameConverter.getFileIdColumn(), fileId);
            EntityResult er = this.getService().fileQuery(criteria, this.getFileColumns(null));
            if (EntityResult.OPERATION_WRONG != er.getCode() && er.calculateRecordNumber() > 0) {
                fis = this.getService().fileGetContent(fileId);
                FileCopyUtils.copy(fis, response.getOutputStream());

                String fileName = (String) er.getRecordValues(0).get(this.dmsNameConverter.getFileNameColumn());
                Number fileSize = (Number) er.getRecordValues(0).get(this.dmsNameConverter.getFileSizeColumn());

                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentLengthLong(fileSize.longValue());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    DMSRestController.logger.error("{}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Get files and folders compressed on a zip file
     * @param workspaceId the work space identifier
     * @param files the file list for downloading
     * @return
     */
    @RequestMapping(path = "/getFiles/{workspaceId}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Map<String, String>> fileGetContent(
            @PathVariable("workspaceId") Integer workspaceId, @RequestBody List<OFile> files) {
        ZipOutputStream zos = null;
        try {
            File zipFile = File.createTempFile("download_", ".zip");
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            this.addFilesToZip(workspaceId, files, zos, null);

            if (zipFile != null) {
                String zipFileName = zipFile.getName();
                Map<String, String> response = new HashMap<>();
                response.put("file", zipFileName);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                DMSRestController.logger.error("{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Download the zip file provided stored in temporary files folder.
     * @param file the file name
     * @param response
     */
    @RequestMapping(path = "/getZipFile/{file}", method = RequestMethod.GET, produces = "application/octet-stream")
    public @ResponseBody void fileGetZip(@PathVariable("file") String file, HttpServletResponse response) {
        InputStream fis = null;
        File zipFile = null;
        try {
            zipFile = new File(System.getProperty("java.io.tmpdir"), file + ".zip");
            fis = new FileInputStream(zipFile);
            FileCopyUtils.copy(fis, response.getOutputStream());

            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"");
            response.setContentLengthLong(file.length());
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (zipFile != null) {
                zipFile.delete();
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    DMSRestController.logger.error("{}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Insert a file in the specified folder for the specified document
     * @param workspaceId the work space identifier
     * @param multipart the file
     * @param folderId the folder identifier
     * @return
     */
    @RequestMapping(path = "/insertFile/{workspaceId}", method = RequestMethod.POST)
    public ResponseEntity<DocumentIdentifier> fileInsert(@PathVariable("workspaceId") Integer workspaceId,
            @RequestParam("file") MultipartFile multipart,
            @RequestParam(name = "folderId", required = false) Object folderId) {
        try {
            InputStream is = new ByteArrayInputStream(multipart.getBytes());
            Map<Object, Object> av = new HashMap<>();
            av.put(this.dmsNameConverter.getFileNameColumn(), multipart.getOriginalFilename());
            if (folderId != null) {
                av.put(this.dmsNameConverter.getCategoryIdColumn(), folderId);
            }
            DocumentIdentifier id = this.getService().fileInsert(workspaceId, av, is);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete the files provided
     * @param workspaceId the work space identifier
     * @param deleteParameter
     * @return
     */
    @RequestMapping(value = "/deleteFiles/{workspaceId}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("workspaceId") Integer workspaceId,
            @RequestBody FileListParameter deleteParameter) throws DmsException {
        try {
            List<OFile> fileList = deleteParameter.getFileList();
            for (OFile file : fileList) {
                if (file.isDirectory()) {
                    List<OFile> categoryFileList = this.getCategoryFiles(workspaceId, file.getId(), true);
                    for (OFile cFile : categoryFileList) {
                        this.deleteOFile(cFile);
                    }
                }
                this.deleteOFile(file);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Inserts a folder in the workspace with the provided work space identifier
     * @param workspaceId the work space identifier
     * @param name the folder name
     * @param insertParam
     * @return
     */
    @RequestMapping(path = "/insertFolder/{workspaceId}/{name}", method = RequestMethod.POST)
    public ResponseEntity<Void> folderInsert(@PathVariable("workspaceId") Integer workspaceId,
            @PathVariable("name") String name, @RequestBody InsertParameter insertParam) {
        try {
            Map<Object, Object> data = insertParam.getData();
            Object categoryId = null;
            if (data.containsKey(DMSRestController.CATEGORY_FILTER_KEY)) {
                categoryId = data.remove(DMSRestController.CATEGORY_FILTER_KEY);
            }
            this.getService().categoryInsert(workspaceId, name, (Serializable) categoryId, null);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/fileUpdate", method = RequestMethod.POST)
    public ResponseEntity<Void> fileUpdate(@RequestBody UpdateFileParameter updateFileParameter) {
        try {
            OFile file = updateFileParameter.getFile();
            if (file != null) {
                Map<Object, Object> data = updateFileParameter.getParams();
                Map<String, Object> av = new HashMap<>();
                if (data.containsKey("name")) {
                    if (file.isDirectory()) {
                        av.put(this.dmsNameConverter.getNameConvention().convertName(DMSNaming.CATEGORY_CATEGORY_NAME), data.get("name"));
                        this.getService().categoryUpdate(file.getId(), av);
                    } else {
                        av.put(this.dmsNameConverter.getNameConvention().convertName(DMSNaming.DOCUMENT_FILE_NAME), data.get("name"));
                        this.getService().fileUpdate(file.getId(), av, this.getService().fileGetContent(file.getId()));
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(path = "/createDocument/{name}", method = RequestMethod.GET)
    public ResponseEntity<Number> createDocument(@PathVariable(required = false) String name) {
        try {
            Map<String, Object> av = new HashMap<>();
            av.put(DMSNaming.DOCUMENT_DOCUMENT_NAME, name == null ? "docname" : name);
            DocumentIdentifier documentIdentifier = this.getService().documentInsert(av);
            return new ResponseEntity<>((Number) documentIdentifier.getDocumentId(), HttpStatus.OK);
        } catch (Exception e) {
            DMSRestController.logger.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ----------------------------------------------------------------------------------------------------------------------------
    // //
    // -------------------------------------------------------- Converters
    // -------------------------------------------------------- //
    // ----------------------------------------------------------------------------------------------------------------------------
    // //

    protected List<?> getFileColumns(List<?> columns) {
        if (this.dmsNameConverter != null) {
            return this.dmsNameConverter.getFileColumns(columns);
        }
        return columns;
    }

    protected List<?> getCategoryColumns(List<?> columns) {
        if (this.dmsNameConverter != null) {
            return this.dmsNameConverter.getCategoryColumns(columns);
        }
        return columns;
    }

    // ----------------------------------------------------------------------------------------------------------------------------
    // //
    // -------------------------------------------------------- Utilities
    // --------------------------------------------------------- //
    // ----------------------------------------------------------------------------------------------------------------------------
    // //

    protected void getCategoriesAsFiles(DMSCategory category, Object parentId, List<OFile> files, boolean recursive) {
        for (DMSCategory cat : category.getChildren()) {
            Object catId = category.getIdCategory();
            if ((catId == null && parentId == null) || (catId != null && parentId != null && catId.equals(parentId))) {
                files.add(this.categoryToFile(cat));
                if (recursive) {
                    this.getCategoriesAsFiles(cat, cat.getIdCategory(), files, recursive);
                }
            } else {
                this.getCategoriesAsFiles(cat, parentId, files, recursive);
            }
        }
    }

    protected OFile categoryToFile(DMSCategory cat) {
        OFile dmsFile = new OFile();
        dmsFile.setId((Integer) cat.getIdCategory());
        dmsFile.setName(cat.getName());
        dmsFile.setDirectory(true);
        return dmsFile;
    }

    protected void deleteOFile(OFile file) throws DmsException {
        if (file.isDirectory()) {
            this.getService().categoryDelete(file.getId());
        } else {
            this.getService().fileDelete(file.getId());
        }
    }

    /**
     * Get the files contained in a category
     * @param documentId the document identifier
     * @param categoryId the category identifier
     * @param includeSubCategories include the files in sub-categories or not
     * @return
     * @throws DmsException
     */
    protected List<OFile> getCategoryFiles(Integer documentId, Integer categoryId, boolean includeSubCategories)
            throws DmsException {
        List<OFile> files = new ArrayList<>();

        DMSCategory categories = this.getService().categoryGetForDocument(documentId, this.getCategoryColumns(null));
        this.getCategoriesAsFiles(categories, categoryId, files, includeSubCategories);

        Map<Object, Object> filter = new HashMap<>();
        List<Object> categoriesIds = new ArrayList<>();
        categoriesIds.add(categoryId);
        if (includeSubCategories) {
            for (OFile file : files) {
                categoriesIds.add(file.getId());
            }
        }

        filter.put(this.dmsNameConverter.getCategoryIdColumn(), new SearchValue(SearchValue.IN, categoriesIds));

        EntityResult er = this.getService().documentGetFiles(documentId, filter, this.getFileColumns(null));
        if (EntityResult.OPERATION_WRONG != er.getCode()) {
            for (int i = 0; i < er.calculateRecordNumber(); i++) {
                files.add(this.dmsNameConverter.createOFile(er.getRecordValues(i)));
            }
        }
        return files;
    }

    protected void addFilesToZip(Integer documentId, List<OFile> oFiles, ZipOutputStream zos, String parentPath)
            throws DmsException, IOException {
        if (parentPath == null) {
            parentPath = "";
        }
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), parentPath);
        for (OFile oFile : oFiles) {
            if (oFile.isDirectory()) {
                File directory = new File(tmpDir, oFile.getName());
                directory.mkdirs();
                List<OFile> categoryFiles = this.getCategoryFiles(documentId, oFile.getId(), false);
                String folderName = parentPath + directory.getName() + File.separatorChar;
                zos.putNextEntry(new ZipEntry(folderName));
                this.addFilesToZip(documentId, categoryFiles, zos, folderName);
            } else {
                byte[] buffer = new byte[1024];
                Map<Object, Object> criteria = new HashMap<>();
                criteria.put(this.dmsNameConverter.getFileIdColumn(), oFile.getId());
                EntityResult er = this.getService().fileQuery(criteria, this.getFileColumns(null));
                if (EntityResult.OPERATION_WRONG != er.getCode() && er.calculateRecordNumber() > 0) {
                    InputStream fis = this.getService().fileGetContent(oFile.getId());
                    String fileName = parentPath + oFile.getName();
                    zos.putNextEntry(new ZipEntry(fileName));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
            }
        }
    }

}
