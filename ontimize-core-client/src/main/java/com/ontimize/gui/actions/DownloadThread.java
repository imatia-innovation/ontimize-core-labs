package com.ontimize.gui.actions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.jee.common.db.FileManagementEntity;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.remote.BytesBlock;

public class DownloadThread extends ExtendedOperationThread {

	private static final Logger logger = LoggerFactory.getLogger(DownloadThread.class);

	public static int DEFAULT_BLOCK_SIZE = 256 * 1024;

	public static boolean TRY_MAX_SPEED = true;

	protected int blockSize = DownloadThread.DEFAULT_BLOCK_SIZE;

	protected String uriSound = null;

	protected ResourceBundle resources = null;

	protected File selectFile = null;

	protected FileManagementEntity entF = null;

	protected Map keysValues = null;

	protected EntityReferenceLocator locator = null;

	public DownloadThread(final ResourceBundle res, final File selFile, final Map kv, final FileManagementEntity entF,
			final EntityReferenceLocator locator) {
		super(ApplicationManager.getTranslation("attachment.download_attached_file", res) + " " + selFile.getName());
		this.resources = res;
		this.selectFile = selFile;
		this.keysValues = kv;
		this.entF = entF;
		this.locator = locator;
	}

	public DownloadThread(final ResourceBundle res, final File seltFile, final Map kv, final FileManagementEntity entF,
			final EntityReferenceLocator locator, final int blockSize, final String uriSound) {
		super(ApplicationManager.getTranslation("attachment.download_attached_file", res) + " " + seltFile.getName());
		this.selectFile = seltFile;
		this.keysValues = kv;
		this.entF = entF;
		this.locator = locator;
		this.blockSize = blockSize;
		this.uriSound = uriSound;
	}

	protected void downloadFinished() {

	}

	@Override
	public void run() {
		this.setPriority(Thread.MIN_PRIORITY);
		this.hasStarted = true;
		BufferedOutputStream bOut = null;

		try {
			this.status = ApplicationManager.getTranslation("attachment.initiating_transfer", this.resources);
			final String rId = this.entF.prepareToTransfer(this.keysValues, this.locator.getSessionId());
			final long totalSize = this.entF.getSize(rId);
			final FileOutputStream fOut = new FileOutputStream(this.selectFile);
			this.status = ApplicationManager.getTranslation("attachment.downloading_file", this.resources);
			this.progressDivisions = (int) totalSize;
			bOut = new BufferedOutputStream(fOut);
			BytesBlock by = null;
			int totalRead = 0;
			final long tIni = System.currentTimeMillis();
			long lTime = 0;
			while ((by = this.entF.getBytes(rId, totalRead, this.blockSize, this.locator.getSessionId())) != null) {
				Thread.yield();
				bOut.write(by.getBytes());
				totalRead = totalRead + by.getBytes().length;
				if (ApplicationManager.DEBUG) {
					DownloadThread.logger.debug(this.getClass().toString() + " -> Downloaded " + totalRead + " bytes");
				}
				this.currentPosition = totalRead;
				if (this.isCancelled()) {
					this.hasFinished = true;
					this.status = ApplicationManager.getTranslation("cancelled", this.resources);
					bOut.close();
					this.selectFile.delete();
					return;
				}
				try {
					if (!DownloadThread.TRY_MAX_SPEED) {
						Thread.sleep(25);
					} else {
						Thread.sleep(10);
					}
				} catch (final Exception ex) {
					DownloadThread.logger.trace(null, ex);
				}
				lTime = System.currentTimeMillis() - tIni;
				this.estimatedTimeLeft = (int) (((totalSize - totalRead) * lTime) / (float) totalRead);
			}

			this.currentPosition = this.progressDivisions;
			this.status = ApplicationManager.getTranslation("finished", this.resources);
			this.downloadFinished();
			if (this.uriSound != null) {
				ApplicationManager.playSound(this.uriSound);
			}
		} catch (final Exception e) {
			DownloadThread.logger.error(null, e);
			this.res = e.getMessage();
			this.status = ApplicationManager.getTranslation("error", this.resources) + " : " + e.getMessage();
		} finally {
			this.hasFinished = true;
			try {
				if (bOut != null) {
					bOut.close();
				}
			} catch (final Exception e) {
				DownloadThread.logger.trace(null, e);
			}
		}
	}

};
