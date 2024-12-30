package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.ITransferQueueListener;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.TransferQueueChangedEvent;
import com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox.DropboxDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox.DropboxDmsUploader;
import com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive.GoogleDriveDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive.GoogleDriveDmsUploader;
import com.ontimize.jee.desktopclient.dms.upload.disk.LocalDiskDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.disk.LocalDiskDmsUploader;
import com.ontimize.jee.desktopclient.dms.upload.web.WebDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.web.WebDmsUploader;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 *
 * This class implements all funcionality to manage downloaders. It saves a list with all downloaders and methods to know status and list all of them.
 *
 */
public class DmsTransfererManagerDefault implements Observer, IDmsTransfererManager {

	private static final Logger																								logger					= LoggerFactory
	        .getLogger(DmsTransfererManagerDefault.class);

	// Constant variables
	private static final Path																								V_SYNC_FOLDER_DEFAULT	= Paths
	        .get(System.getProperty("user.home"), "DMS");

	// Member variables
	private Path																											syncFolder;
	private final List<AbstractDmsTransferable>																				transfersList;
	private final Map<Class<? extends AbstractDmsTransferable>, AbstractDmsTransferer<? extends AbstractDmsTransferable>>	transferersFactory;
	private final HashSet<ITransferQueueListener>																			transferQueueListeners;
	private final ScheduledThreadPoolExecutor																				executor;

	/** Protected constructor */
	protected DmsTransfererManagerDefault() {
		super();
		this.syncFolder = DmsTransfererManagerDefault.V_SYNC_FOLDER_DEFAULT;
		this.transfersList = new ArrayList<>();
		this.transferersFactory = new HashMap<>();
		this.transferQueueListeners = new HashSet<>();
		this.executor = new ScheduledThreadPoolExecutor(10);
		this.registerDefaultTransferers();
	}

	@Override
	public void init() {
		// do nothing
	}

	protected void registerDefaultTransferers() {
		this.transferersFactory.put(LocalDiskDmsUploadable.class, new LocalDiskDmsUploader());
		this.transferersFactory.put(WebDmsUploadable.class, new WebDmsUploader());
		this.transferersFactory.put(DmsDownloadable.class, new DmsDownloader());
		this.transferersFactory.put(GoogleDriveDmsUploadable.class, new GoogleDriveDmsUploader());
		this.transferersFactory.put(DropboxDmsUploadable.class, new DropboxDmsUploader());
	}

	public void registerTransferer(Class<AbstractDmsTransferable> transferable, AbstractDmsTransferer<AbstractDmsTransferable> transferer) {
		this.transferersFactory.put(transferable, transferer);
	}

	protected <T extends AbstractDmsTransferable> AbstractDmsTransferer<T> getTransfererForTransferable(Class<T> clazz) throws DmsException {
		AbstractDmsTransferer<T> transferer = (AbstractDmsTransferer<T>) this.transferersFactory.get(clazz);
		if (transferer == null) {
			throw new DmsException("Transferer not found for " + clazz);
		}
		return transferer;
	}

	public Path getSyncFolder() {
		return this.syncFolder;
	}

	public void setSyncFolder(Path syncFolder) {
		this.syncFolder = syncFolder;
	}

	/**
	 * Get the download list
	 *
	 * @return <code>ArrayList</code> with all <code>Downloaders</code> included
	 */
	public List<AbstractDmsTransferable> getDownloadList() {
		return Collections.unmodifiableList(this.transfersList);
	}

	/**
	 * Execute transfer in thread
	 *
	 * @param transferable
	 * @throws DmsException
	 */
	@Override
	public void transfer(AbstractDmsTransferable transferable) throws DmsException {
		if (!transferable.getStatus().equals(Status.ON_PREPARE)) {
			throw new DmsException("Transferable already initiated");
		}
		this.executor.execute(new TransferJob(transferable));
	}

	@Override
	public void update(Observable observable, Object arg) {
		CheckingTools.failIf(!(observable instanceof AbstractDmsTransferable), "source is not AbstractDmsTransferable");
		AbstractDmsTransferable transfer = (AbstractDmsTransferable) observable;
		Status state = transfer.getStatus();
		if (state.isFinishState()) {
			this.fireTransferQueueChangedEvent(new TransferQueueChangedEvent(Collections.unmodifiableList(DmsTransfererManagerDefault.this.transfersList), transfer, null));
			this.transfersList.remove(transfer);
		}
	}

	@Override
	public Path obtainDmsFileVersion(Serializable idFileVersion) throws DmsException {
		EntityResult er = BeansFactory.getBean(IDMSService.class).fileVersionQuery(idFileVersion,
		        Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, DMSNaming.DOCUMENT_FILE_NAME }));
		Long fileSize = ((List<Number>) er.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE)).get(0).longValue();
		String fileName = ((List<String>) er.get(DMSNaming.DOCUMENT_FILE_NAME)).get(0);
		return this.obtainDmsFileVersion(idFileVersion, fileName, fileSize);
	}

	@Override
	public Path obtainDmsFileVersion(Serializable idFileVersion, String fileName, Long fileSize) throws DmsException {
		Path localFile = this.computeLocalFile(idFileVersion, fileName);
		if (!Files.exists(localFile)) {
			final DmsDownloadable dmsDownloadable = new DmsDownloadable(idFileVersion, localFile, fileName, fileSize);
			synchronized (dmsDownloadable) {
				dmsDownloadable.addObserver(new Observer() {

					@Override
					public void update(Observable o, Object arg) {
						if (dmsDownloadable.getStatus().isFinishState()) {
							synchronized (dmsDownloadable) {
								dmsDownloadable.notify();
							}
						}
					}
				});
				this.transfer(dmsDownloadable);
				try {
					dmsDownloadable.wait();
				} catch (InterruptedException error) {
					DmsTransfererManagerDefault.logger.error(null, error);
				}
			}
			if (!Status.COMPLETED.equals(dmsDownloadable.getStatus())) {
				throw new DmsException("E_GETTING_FILE");
			}
		}
		return localFile;
	}

	private Path computeLocalFile(Object idFileVersion, String filename) throws DmsException {
		// TODO posiblemete haya un límete para el número máximo de ficheros por directorio
		if (!Files.exists(this.syncFolder)) {
			try {
				Files.createDirectories(this.syncFolder);
			} catch (IOException error) {
				throw new DmsException(error.getMessage(), error);
			}
		}
		return this.syncFolder.resolve(idFileVersion.toString() + "_" + filename);
	}

	@Override
	public synchronized void addTransferQueueListener(ITransferQueueListener transferQueueListener) {
		this.transferQueueListeners.add(transferQueueListener);
	}

	@Override
	public synchronized void removeTransferQueueListener(ITransferQueueListener transferQueueListener) {
		this.transferQueueListeners.remove(transferQueueListener);
	}

	public synchronized void fireTransferQueueChangedEvent(TransferQueueChangedEvent evt) {
		for (ITransferQueueListener listener : this.transferQueueListeners) {
			listener.onTransferQueueChanged(evt);
		}
	}

	protected class TransferJob<T extends AbstractDmsTransferable> implements Runnable {

		protected T transferable;

		public TransferJob(T transferable) {
			super();
			this.transferable = transferable;
		}

		@Override
		public void run() {
			try {
				AbstractDmsTransferer<T> transferer = (AbstractDmsTransferer<T>) DmsTransfererManagerDefault.this.getTransfererForTransferable(this.transferable.getClass());
				DmsTransfererManagerDefault.this.transfersList.add(this.transferable);
				this.transferable.addObserver(DmsTransfererManagerDefault.this);
				DmsTransfererManagerDefault.this.fireTransferQueueChangedEvent(
				        new TransferQueueChangedEvent(Collections.unmodifiableList(DmsTransfererManagerDefault.this.transfersList), null, this.transferable));
				transferer.transfer(this.transferable);
			} catch (Exception ex) {
				this.transferable.setStatus(Status.ERROR);
				DmsTransfererManagerDefault.logger.error(null, ex);
			}
		}
	}

}
