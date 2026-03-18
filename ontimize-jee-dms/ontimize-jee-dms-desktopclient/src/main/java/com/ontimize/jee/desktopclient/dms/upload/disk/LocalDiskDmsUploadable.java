package com.ontimize.jee.desktopclient.dms.upload.disk;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;

/**
 * The Class LocalDiskDmsTransferable.
 */
public class LocalDiskDmsUploadable extends AbstractDmsUploadable {

	/** The file. */
	private final Path file;

	/**
	 * Instantiates a new local disk dms transferable.
	 *
	 * @param file
	 *            the file
	 * @param description
	 *            the description
	 * @throws IOException
	 */
	public LocalDiskDmsUploadable(Path file, String description) throws DmsException {
		super(description, file.getFileName().toString(), LocalDiskDmsUploadable.getFileSize(file));
		this.file = file;
	}

	private static Long getFileSize(Path file) throws DmsException {
		try {
			return Files.size(file);
		} catch (IOException e) {
			throw new DmsException("ERROR", e);
		}
	}

	public LocalDiskDmsUploadable(Path file, String description, DocumentIdentifier documentIdentifier, Serializable idCategory) throws DmsException {
		this(file, description);
		this.setDocumentIdentifier(documentIdentifier);
		this.setCategoryId(idCategory);
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public Path getFile() {
		return this.file;
	}
}
