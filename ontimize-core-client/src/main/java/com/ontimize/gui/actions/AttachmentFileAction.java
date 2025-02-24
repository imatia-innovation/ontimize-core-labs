package com.ontimize.gui.actions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MessageDialog;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.FileManagementEntity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.remote.BytesBlock;

/**
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 *
 * @version 1.0
 */

public class AttachmentFileAction extends AbstractButtonAction {

	private static final Logger logger = LoggerFactory.getLogger(AttachmentFileAction.class);

	public static final String M_INSERT_DESCRIPTION_ATTACHMENT_FILE = "attachment.provide_description_attached_file";

	protected int sizeBlock = 64 * 1024;

	protected String entityName = null;

	protected JFileChooser fileChooser = null;

	protected boolean refreshForm = false;

	protected boolean addDescription = false;

	protected boolean synchronize = true;

	protected String uriSound = null;

	protected static File lastDirectory = null;

	protected class SendThread extends ExtendedOperationThread {

		protected Form currentForm = null;

		protected File selectedForm = null;

		protected String description = null;

		protected String uriSoundPath = null;

		public SendThread(final Form f, final File selectedFile, final String sDescription, final String uriSound) {
			super(ApplicationManager.getTranslation("attach_file", f.getResourceBundle()) + " "
					+ selectedFile.getName());
			this.description = sDescription;
			this.currentForm = f;
			this.selectedForm = selectedFile;
			this.uriSoundPath = uriSound;
		}

		protected void uploadFinished() {

		}

		@Override
		public void run() {

			this.setPriority(Thread.MIN_PRIORITY);
			this.hasStarted = true;
			BufferedInputStream bIn = null;
			ByteArrayOutputStream bOut = null;
			final long totalSize = this.selectedForm.length();
			String rId = null;
			EntityReferenceLocator loc = null;
			FileManagementEntity entF = null;
			try {
				loc = this.currentForm.getFormManager().getReferenceLocator();
				final Entity ent = loc.getEntityReference(AttachmentFileAction.this.entityName);
				if (!(ent instanceof FileManagementEntity)) {
					this.hasFinished = true;
					this.status = ApplicationManager.getTranslation(
							"Error: the entity does not implement FileManagementEntity ",
							this.currentForm.getResourceBundle());
					return;
				}
				entF = (FileManagementEntity) ent;
				final Map kv = AttachmentFileAction.this.getAttachmentValuesKeys(this.currentForm);
				this.status = ApplicationManager.getTranslation("attachment.initiating_transfer",
						this.currentForm.getResourceBundle());

				synchronized (entF) {
					rId = entF.prepareToReceive(kv, this.selectedForm.getName(), this.selectedForm.getPath(),
							this.description, loc.getSessionId());
				}
				try {
					Thread.sleep(100);
				} catch (final Exception ex) {
					AttachmentFileAction.logger.trace(null, ex);
				}

				if (this.isCancelled()) {
					entF.cancelReceiving(rId, loc.getSessionId());
					this.hasFinished = true;
					this.status = ApplicationManager.getTranslation("cancelled", this.currentForm.getResourceBundle());
					return;
				}
				final FileInputStream fIn = new FileInputStream(this.selectedForm);

				this.status = ApplicationManager.getTranslation("attachment.reading_input_file",
						this.currentForm.getResourceBundle());
				this.progressDivisions = (int) totalSize;
				bIn = new BufferedInputStream(fIn);
				bOut = new ByteArrayOutputStream();
				int by = -1;
				int read = 0;
				this.status = ApplicationManager.getTranslation("attachment.sending_input_file",
						this.currentForm.getResourceBundle());
				int totalRead = 0;
				final long tIni = System.currentTimeMillis();
				long spendTime = 0;
				while ((by = bIn.read()) != -1) {
					Thread.yield();
					bOut.write(by);
					read++;
					totalRead++;
					this.currentPosition = totalRead;
					if (read >= AttachmentFileAction.this.sizeBlock) {
						Thread.yield();
						read = 0;
						synchronized (entF) {
							entF.putBytes(rId, new BytesBlock(bOut.toByteArray()), loc.getSessionId());
						}
						bOut.reset();
						spendTime = System.currentTimeMillis() - tIni;
						this.estimatedTimeLeft = (int) (((totalSize - totalRead) * spendTime) / (float) totalRead);
						if (this.isCancelled()) {
							entF.cancelReceiving(rId, loc.getSessionId());
							this.status = ApplicationManager.getTranslation("Cancelled",
									this.currentForm.getResourceBundle());
							this.hasFinished = true;
							return;
						}
						try {
							Thread.sleep(25);
						} catch (final Exception ex) {
							AttachmentFileAction.logger.trace(null, ex);
						}

					}
				}
				if (bOut.size() > 0) {
					entF.putBytes(rId, new BytesBlock(bOut.toByteArray()), loc.getSessionId());
				}
				entF.finishReceiving(rId, loc.getSessionId());
				this.currentPosition = this.progressDivisions;
				this.uploadFinished();
				this.status = ApplicationManager.getTranslation("finished", this.currentForm.getResourceBundle());
				if (this.uriSoundPath != null) {
					ApplicationManager.playSound(this.uriSoundPath);
				}
			} catch (final Exception e) {
				AttachmentFileAction.logger.error(null, e);
				this.res = new EntityResultMapImpl();
				((EntityResult) this.res).setCode(EntityResult.OPERATION_WRONG);
				((EntityResult) this.res).setMessage(e.getMessage());
				this.status = ApplicationManager.getTranslation("error", this.currentForm.getResourceBundle()) + " : "
						+ e.getMessage();
				try {
					if ((rId != null) && (entF != null)) {
						entF.cancelReceiving(rId, loc.getSessionId());
					}
				} catch (final Exception ex) {
					AttachmentFileAction.logger.error(null, ex);
				}
			} finally {
				this.hasFinished = true;

				try {
					if (bIn != null) {
						bIn.close();
					}
					if (bOut != null) {
						bOut.close();
					}
				} catch (final Exception e) {
					AttachmentFileAction.logger.trace(null, e);
				}
			}
		}

	};

	public AttachmentFileAction(final String entity, final boolean addDescription, final boolean refreshForm) {
		this(entity, addDescription, refreshForm, true);
	}

	public AttachmentFileAction(final String entity, final boolean addDescription, final boolean refreshForm, final boolean wait) {
		this(entity, addDescription, refreshForm, wait, null);
	}

	public AttachmentFileAction(final String entity, final boolean addDescription, final boolean refreshForm, final boolean wait,
			final String uriSound) {
		this.entityName = entity;
		this.refreshForm = refreshForm;
		this.addDescription = addDescription;

		this.synchronize = wait;
		this.uriSound = uriSound;
	}

	protected Map getAttachmentValuesKeys(final Form f) throws Exception {

		if (f == null) {
			throw new Exception("parent form is null");
		}

		final Map kv = new Hashtable();
		final List vKeys = f.getKeys();
		if (vKeys.isEmpty()) {
			throw new Exception("'keys' parameter ir required in the parent form");
		}
		for (int i = 0; i < vKeys.size(); i++) {
			final Object oKeyValue = f.getDataFieldValueFromFormCache(vKeys.get(i).toString());
			if (oKeyValue == null) {
				throw new Exception("Value of the key " + vKeys.get(i) + " not found in parent form");
			}
			kv.put(vKeys.get(i), oKeyValue);
		}
		return kv;
	}

	protected ExtendedOperationThread createSendThread(final Form f, final File selectedFile, final String description,
			final String uriSound) {
		return new SendThread(f, selectedFile, description, uriSound);
	}

	protected void sendFile(final Form f, final File file, final String descr, final String uriSound) {
		final ExtendedOperationThread eop = this.createSendThread(f, file, descr, uriSound);
		final ApplicationManager.ExtOpThreadsMonitor m = ApplicationManager.getExtOpThreadsMonitor(f);
		m.addExtOpThread(eop);
		if ((f.getFormManager() != null) && (f.getFormManager().getApplication() instanceof MainApplication)) {
			((MainApplication) f.getFormManager().getApplication()).registerExtOpThreadsMonitor(m);
		}
		ApplicationManager.getExtOpThreadsMonitor(f).setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		try {
			final Form f = this.getForm(e);

			if (this.fileChooser == null) {
				this.fileChooser = new com.ontimize.util.filechooser.CustomFileChooser();
				this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			if (AttachmentFileAction.lastDirectory != null) {
				if (AttachmentFileAction.lastDirectory.isDirectory()) {
					this.fileChooser.setCurrentDirectory(AttachmentFileAction.lastDirectory);
				} else {
					this.fileChooser.setCurrentDirectory(AttachmentFileAction.lastDirectory.getParentFile());
				}
			}

			final EntityReferenceLocator b = f.getFormManager().getReferenceLocator();
			final Entity ent = b.getEntityReference(this.entityName);
			if (!(ent instanceof FileManagementEntity)) {
				throw new Exception("The entity " + this.entityName + " does not implement FileManagementEntity");
			}
			if (f == null) {
				throw new Exception("paremt form is null");
			}

			File fSel = null;
			File[] fil = null;
			if (this.synchronize) {
				this.fileChooser.setMultiSelectionEnabled(false);
			} else {
				this.fileChooser.setMultiSelectionEnabled(true);
			}
			final int option = this.fileChooser.showOpenDialog(f);
			AttachmentFileAction.lastDirectory = this.fileChooser.getCurrentDirectory();
			if (option != JFileChooser.APPROVE_OPTION) {
				return;
			}
			fSel = this.fileChooser.getSelectedFile();
			fil = this.fileChooser.getSelectedFiles();
			Object oDescription = null;
			if (this.addDescription) {
				oDescription = MessageDialog.showInputMessage(
						SwingUtilities.getWindowAncestor((Component) e.getSource()),
						AttachmentFileAction.M_INSERT_DESCRIPTION_ATTACHMENT_FILE, f.getResourceBundle());
			}
			final File fSelected = fSel;
			final File[] files = fil;
			if (this.synchronize) {
				final ExtendedOperationThread eop = this.createSendThread(f, fSelected, (String) oDescription, this.uriSound);
				final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
				if (w instanceof Dialog) {
					ApplicationManager.proccessOperation((Dialog) w, eop, 0);
				} else {
					ApplicationManager.proccessOperation((Frame) w, eop, 0);
				}
				if (eop.getResult() != null) {
					f.message(eop.getResult().toString(), Form.ERROR_MESSAGE);
				} else {
					f.message("attachment.file_has_been_attached_successfully", Form.INFORMATION_MESSAGE);
					if (this.refreshForm) {
						f.refreshCurrentDataRecord();
					}
				}
			} else {
				for (int i = 0; i < files.length; i++) {
					final ExtendedOperationThread eop = this.createSendThread(f, files[i], (String) oDescription,
							this.uriSound);
					final ApplicationManager.ExtOpThreadsMonitor m = ApplicationManager
							.getExtOpThreadsMonitor((Component) e.getSource());
					m.addExtOpThread(eop);
				}
				final ApplicationManager.ExtOpThreadsMonitor m = ApplicationManager
						.getExtOpThreadsMonitor((Component) e.getSource());
				if ((f.getFormManager() != null) && (f.getFormManager().getApplication() instanceof MainApplication)) {
					((MainApplication) f.getFormManager().getApplication()).registerExtOpThreadsMonitor(m);
				}
				ApplicationManager.getExtOpThreadsMonitor((Component) e.getSource()).setVisible(true);
			}
		} catch (final Exception ex) {
			AttachmentFileAction.logger.error(null, ex);
			final Form f = this.getForm(e);
			if (f != null) {
				f.message(ex.getMessage(), Form.ERROR_MESSAGE);
			}
		}
	}

}
