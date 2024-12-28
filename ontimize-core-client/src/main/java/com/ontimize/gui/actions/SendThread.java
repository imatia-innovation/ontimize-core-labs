package com.ontimize.gui.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.jee.common.db.FileManagementEntity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.remote.BytesBlock;

public class SendThread extends ExtendedOperationThread {

	private static final Logger logger = LoggerFactory.getLogger(SendThread.class);

	public static int DEFAULT_BLOCK_SIZE = 256 * 1024;

	public static boolean TRY_MAX_SPEED = true;

	protected ResourceBundle resources = null;

	protected File selectFile = null;

	protected String description = null;

	protected String uriSound = null;

	protected EntityReferenceLocator locator = null;

	protected FileManagementEntity entity = null;

	protected Map keysValues = null;

	protected int blockSize = SendThread.DEFAULT_BLOCK_SIZE;

	/**
	 * Variable for the temporal ID
	 */
	protected String rId = null;

	/**
	 * This method return the ID of the temporal operation
	 * @return An {@link String} with the temporal ID
	 */
	public String getrId() {
		return this.rId;
	}

	public SendThread(final ResourceBundle res, final File selFile, final Map kv, final FileManagementEntity entF, final String description,
			final EntityReferenceLocator locator) {
		super(ApplicationManager.getTranslation("attach_file", res) + " " + selFile.getName());
		this.description = description;
		this.resources = res;
		this.locator = locator;
		this.selectFile = selFile;
		this.entity = entF;
		this.keysValues = kv;
	}

	protected void uploadFinished() {

	}

	@Override
	public void run() {

		this.setPriority(Thread.MIN_PRIORITY);
		this.hasStarted = true;
		BufferedInputStream bIn = null;
		final long totalSize = this.selectFile.length();

		try {
			if (this.entity == null) {
				this.hasFinished = true;
				this.status = ApplicationManager.getTranslation("Error: entity not found ", this.resources);
				return;
			}

			this.status = ApplicationManager.getTranslation("attachment.initiating_transfer", this.resources);

			synchronized (this.entity) {
				this.rId = this.entity.prepareToReceive(this.keysValues, this.selectFile.getName(),
						this.selectFile.getPath(), this.description, this.locator.getSessionId());
			}
			try {
				Thread.sleep(100);
			} catch (final Exception ex) {
				SendThread.logger.trace(null, ex);
			}

			if (this.isCancelled()) {
				this.entity.cancelReceiving(this.getrId(), this.locator.getSessionId());
				this.hasFinished = true;
				this.status = ApplicationManager.getTranslation("cancelled", this.resources);
				return;
			}
			final FileInputStream fIn = new FileInputStream(this.selectFile);

			this.status = ApplicationManager.getTranslation("attachment.reading_input_file", this.resources);
			this.progressDivisions = (int) totalSize;
			bIn = new BufferedInputStream(fIn);
			int read = 0;
			this.status = ApplicationManager.getTranslation("attachment.sending_input_file", this.resources);
			int totalRead = 0;
			final long tIni = System.currentTimeMillis();
			long passTime = 0;
			final byte[] buffer = new byte[this.blockSize];
			while ((read = bIn.read(buffer)) != -1) {
				Thread.yield();
				totalRead = totalRead + read;
				this.currentPosition = totalRead;
				Thread.yield();
				final byte[] bufferAux = new byte[read];
				System.arraycopy(buffer, 0, bufferAux, 0, read);
				synchronized (this.entity) {
					this.entity.putBytes(this.getrId(), new BytesBlock(bufferAux), this.locator.getSessionId());
				}
				passTime = System.currentTimeMillis() - tIni;
				this.estimatedTimeLeft = (int) (((totalSize - totalRead) * passTime) / (float) totalRead);
				if (this.isCancelled()) {
					this.entity.cancelReceiving(this.getrId(), this.locator.getSessionId());
					this.status = ApplicationManager.getTranslation("cancelled", this.resources);
					this.hasFinished = true;
					return;
				}
				try {
					if (!SendThread.TRY_MAX_SPEED) {
						Thread.sleep(25);
					} else {
						Thread.sleep(10);
					}
				} catch (final Exception ex) {
					SendThread.logger.trace(null, ex);
				}
				read = 0;
			}

			this.entity.finishReceiving(this.getrId(), this.locator.getSessionId());
			this.currentPosition = this.progressDivisions;
			this.uploadFinished();
			this.status = ApplicationManager.getTranslation("finished", this.resources);
			if (this.uriSound != null) {
				ApplicationManager.playSound(this.uriSound);
			}
		} catch (final Exception e) {
			SendThread.logger.error(null, e);
			this.res = new EntityResultMapImpl();
			((EntityResult) this.res).setCode(EntityResult.OPERATION_WRONG);
			((EntityResult) this.res).setMessage(e.getMessage());
			this.status = ApplicationManager.getTranslation("error", this.resources) + " : " + e.getMessage();
			try {
				if ((this.getrId() != null) && (this.entity != null)) {
					this.entity.cancelReceiving(this.getrId(), this.locator.getSessionId());
				}
			} catch (final Exception ex) {
				SendThread.logger.trace(null, ex);
			}
		} finally {
			this.hasFinished = true;

			try {
				if (bIn != null) {
					bIn.close();
				}
			} catch (final Exception e) {
				SendThread.logger.trace(null, e);
			}
		}
	}

}
