package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 * The Class DmsDownloadTransferable.
 */
public class DmsDownloadable extends AbstractDmsTransferable {

	/** The id version file. */
	private final Serializable	idVersionFile;
	private final Path			targetFile;

	/**
	 * Instantiates a new dms download transferable.
	 *
	 * @param idVersionFile
	 *            the id version file
	 * @param targetFile
	 *            the target file
	 * @throws DmsException
	 */
	public DmsDownloadable(Serializable idVersionFile, Path targetFile) throws DmsException {
		super(null, null);
		this.idVersionFile = idVersionFile;
		this.targetFile = targetFile;
		this.init();
	}

	/**
	 * Instantiates a new dms download transferable.
	 *
	 * @param idVersionFile
	 *            the id version file
	 * @param targetFile
	 *            the target file
	 * @throws DmsException
	 */
	public DmsDownloadable(Serializable idVersionFile, Path targetFile, String name, Long size) throws DmsException {
		super(name, size);
		this.idVersionFile = idVersionFile;
		this.targetFile = targetFile;
		this.init();
	}

	/**
	 * Inits the name and size.
	 *
	 * @throws DmsException
	 */
	protected void init() throws DmsException {
		if ((this.getName() == null) || (this.getSize() == null)) {
			EntityResult er = BeansFactory.getBean(IDMSService.class).fileVersionQuery(this.idVersionFile,
			        Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, DMSNaming.DOCUMENT_FILE_NAME }));
			this.setSize(((List<Number>) er.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE)).get(0).longValue());
			this.setName(((List<String>) er.get(DMSNaming.DOCUMENT_FILE_NAME)).get(0));
		}
	}

	/**
	 * Gets the id version file.
	 *
	 * @return the id version file
	 */
	public Serializable getIdVersionFile() {
		return this.idVersionFile;
	}

	/**
	 * Gets the target file.
	 *
	 * @return the target file
	 */
	public Path getTargetFile() {
		return this.targetFile;
	}
}
