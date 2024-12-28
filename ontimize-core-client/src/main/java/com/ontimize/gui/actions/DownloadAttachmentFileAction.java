package com.ontimize.gui.actions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.FileManagementEntity;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.remote.BytesBlock;

/**
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 *
 * @version 1.0
 */

public class DownloadAttachmentFileAction extends AbstractButtonAction {

	private static final Logger logger = LoggerFactory.getLogger(DownloadAttachmentFileAction.class);

	protected int blockSize = 64 * 1024;

	protected String entityName = null;

	protected JFileChooser fileChooser = null;

	protected boolean openFile = false;

	protected String fileFieldName = null;

	protected boolean synchronize = true;

	protected String uriSound = null;

	protected boolean queryOpen = true;

	protected boolean tempFile = false;

	protected static File lastDirectory = null;

	protected class DownloadThread extends ExtendedOperationThread {

		protected Form currentForm = null;

		protected File selectedForm = null;

		protected FileManagementEntity entF = null;

		protected Map keysValues = null;

		protected EntityReferenceLocator locator = null;

		public DownloadThread(final Form f, final File selectedFile, final Map kv, final FileManagementEntity entF,
				final EntityReferenceLocator locator) {
			super(ApplicationManager.getTranslation("attachment.download_attached_file", f.getResourceBundle()) + " "
					+ selectedFile.getName());
			this.currentForm = f;
			this.selectedForm = selectedFile;
			this.keysValues = kv;
			this.entF = entF;
			this.locator = locator;
		}

		protected void downloadFinished() {
		}

		@Override
		public void run() {
			this.setPriority(Thread.MIN_PRIORITY);
			this.hasStarted = true;
			BufferedOutputStream bOut = null;

			try {
				this.status = ApplicationManager.getTranslation("attachment.initiating_transfer",
						this.currentForm.getResourceBundle());
				final String rId = this.entF.prepareToTransfer(this.keysValues, this.locator.getSessionId());
				final long totalSize = this.entF.getSize(rId);
				final FileOutputStream fOut = new FileOutputStream(this.selectedForm);
				this.status = ApplicationManager.getTranslation("attachment.downloading_file",
						this.currentForm.getResourceBundle());
				this.progressDivisions = (int) totalSize;
				bOut = new BufferedOutputStream(fOut);
				BytesBlock by = null;
				int totalRead = 0;
				final long tIni = System.currentTimeMillis();
				long spendTime = 0;
				while ((by = this.entF.getBytes(rId, totalRead, DownloadAttachmentFileAction.this.blockSize,
						this.locator.getSessionId())) != null) {
					Thread.yield();
					bOut.write(by.getBytes());
					totalRead = totalRead + by.getBytes().length;
					if (ApplicationManager.DEBUG) {
						DownloadAttachmentFileAction.logger
						.debug(this.getClass().toString() + " -> Downloaded " + totalRead + " bytes");
					}
					this.currentPosition = totalRead;
					if (this.isCancelled()) {
						this.hasFinished = true;
						this.status = ApplicationManager.getTranslation("cancelled",
								this.currentForm.getResourceBundle());
						bOut.close();
						this.selectedForm.delete();
						return;
					}
					try {
						Thread.sleep(20);
					} catch (final Exception ex) {
						DownloadAttachmentFileAction.logger.trace(null, ex);
					}
					spendTime = System.currentTimeMillis() - tIni;
					this.estimatedTimeLeft = (int) (((totalSize - totalRead) * spendTime) / (float) totalRead);
				}

				this.currentPosition = this.progressDivisions;
				this.status = ApplicationManager.getTranslation("finished", this.currentForm.getResourceBundle());
				this.downloadFinished();
				if (DownloadAttachmentFileAction.this.uriSound != null) {
					ApplicationManager.playSound(DownloadAttachmentFileAction.this.uriSound);
				}
			} catch (final Exception e) {
				DownloadAttachmentFileAction.logger.error(null, e);
				this.res = e.getMessage();
				this.status = ApplicationManager.getTranslation("error", this.currentForm.getResourceBundle()) + " : "
						+ e.getMessage();
			} finally {
				this.hasFinished = true;
				try {
					if (bOut != null) {
						bOut.close();
					}
				} catch (final Exception e) {
					DownloadAttachmentFileAction.logger.trace(null, e);
				}
			}
		}

	};

	public DownloadAttachmentFileAction(final String entity, final boolean openFile) {
		this(entity, openFile, null);
	}

	public DownloadAttachmentFileAction(final String entity, final boolean openFile, final String sugestFieldName) {
		this(entity, openFile, sugestFieldName, true);
	}

	public DownloadAttachmentFileAction(final String entity, final boolean openFile, final String sugestFieldName, final boolean wait) {
		this(entity, openFile, sugestFieldName, wait, null);
	}

	public DownloadAttachmentFileAction(final String entity, final boolean openFile, final String sugestFieldName, final boolean wait,
			final String uriSound) {
		this(entity, openFile, sugestFieldName, wait, uriSound, true);
	}

	public DownloadAttachmentFileAction(final String entity, final boolean openFile, final String sugestFieldName, final boolean wait,
			final String uriSound, final boolean askToOpen) {
		this(entity, openFile, sugestFieldName, wait, uriSound, askToOpen, false);
	}

	public DownloadAttachmentFileAction(final String entity, final boolean openFile, final String sugestFieldName, final boolean wait,
			final String uriSound, final boolean askToOpen, final boolean temporalFile) {
		this.entityName = entity;
		this.openFile = openFile;
		this.fileFieldName = sugestFieldName;
		this.synchronize = wait;
		this.uriSound = uriSound;
		this.queryOpen = askToOpen;
		this.tempFile = temporalFile;
	}

	protected Map getAttachmentValuesKeys(final Form f) throws Exception {
		final Map kv = new Hashtable();
		final List vKeys = f.getKeys();
		if (vKeys.isEmpty()) {
			throw new Exception("The 'keys' parameter ir required in parent form");
		}
		for (int i = 0; i < vKeys.size(); i++) {
			final Object oKeyValue = f.getDataFieldValueFromFormCache(vKeys.get(i).toString());
			if (oKeyValue == null) {
				throw new Exception("Value for key " + vKeys.get(i) + " not found in parent form");
			}
			kv.put(vKeys.get(i), oKeyValue);
		}
		return kv;
	}

	protected String getProposedFileName(final ActionEvent e) {
		final Form f = this.getForm(e);
		if ((this.fileFieldName != null) && (f.getDataFieldValue(this.fileFieldName) != null)) {
			return (String) f.getDataFieldValue(this.fileFieldName);
		} else {
			return null;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		try {
			if (this.fileChooser == null) {
				this.fileChooser = new com.ontimize.util.filechooser.CustomFileChooser();
				this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			if (DownloadAttachmentFileAction.lastDirectory != null) {
				if (DownloadAttachmentFileAction.lastDirectory.isDirectory()) {
					this.fileChooser.setCurrentDirectory(DownloadAttachmentFileAction.lastDirectory);
				} else {
					this.fileChooser.setCurrentDirectory(DownloadAttachmentFileAction.lastDirectory.getParentFile());
				}
			}
			final Form f = this.getForm(e);

			final EntityReferenceLocator loc = f.getFormManager().getReferenceLocator();
			final Entity ent = loc.getEntityReference(this.entityName);
			if (!(ent instanceof FileManagementEntity)) {
				throw new Exception("Entity " + this.entityName + " does not implement FileManagementEntity");
			}
			if (f == null) {
				throw new Exception("parent form is null");
			}

			final Map kv = this.getAttachmentValuesKeys(f);
			final ActionEvent eAux = e;
			final FileManagementEntity entF = (FileManagementEntity) ent;
			File fSelec = null;
			final boolean temporal = this.synchronize && this.tempFile && this.openFile;
			if (!temporal) {
				final String sugName = this.getProposedFileName(e);
				if (sugName != null) {
					this.fileChooser.setSelectedFile(new File(this.fileChooser.getCurrentDirectory(), sugName));
					this.fileChooser.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY,
							new PropertyChangeListener() {

						@Override
						public void propertyChange(final PropertyChangeEvent e) {
							if (e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
								DownloadAttachmentFileAction.this.fileChooser.setSelectedFile(
										new File(
												DownloadAttachmentFileAction.this.fileChooser
												.getCurrentDirectory(),
												DownloadAttachmentFileAction.this.getProposedFileName(eAux)));
							}
						}
					});
				}
				final int option = this.fileChooser.showSaveDialog(f);
				DownloadAttachmentFileAction.lastDirectory = this.fileChooser.getCurrentDirectory();
				if (option != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fSelec = this.fileChooser.getSelectedFile();
			} else {
				final String n = this.getProposedFileName(eAux);

				if (n != null) {
					final int indexOfDot = n.lastIndexOf('.');
					final String name = n.substring(0, indexOfDot);
					final String ext = n.substring(indexOfDot, n.length());
					final StringBuilder builder = new StringBuilder();
					builder.append(name.replace(' ', '_'));
					builder.append("~");
					builder.append(System.currentTimeMillis());
					builder.append(ext);
					fSelec = new File(System.getProperty("java.io.tmpdir", "."), builder.toString());
				} else {
					final StringBuilder builder = new StringBuilder();
					builder.append("download");
					builder.append("~");
					builder.append(System.currentTimeMillis());
					fSelec = File.createTempFile(builder.toString(), null);
				}

				fSelec.deleteOnExit();
			}

			final File fSelected = fSelec;
			if (!temporal && fSelected.exists()) {
				if (!f.question("attachment.file_exists_proceed_anyway")) {
					return;
				}
			}
			if (ApplicationManager.DEBUG) {
				DownloadAttachmentFileAction.logger.debug("DownloadAttachmentFile " + fSelected.getCanonicalPath());
			}
			final ExtendedOperationThread eop = this.createDownloadThread(f, fSelected, kv, entF, loc);
			if (this.synchronize) {
				final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
				if (w instanceof Dialog) {
					ApplicationManager.proccessOperation((Dialog) w, eop, 0);
				} else {
					ApplicationManager.proccessOperation((Frame) w, eop, 0);
				}
				if (eop.getResult() != null) {
					f.message(eop.getResult().toString(), Form.ERROR_MESSAGE);
				} else {
					if (!temporal && !this.openFile) {
						f.message("attachment.file_downloaded_successfully", Form.INFORMATION_MESSAGE);
					}
					if (this.openFile) {
						if (temporal || !this.queryOpen || f.question("attachment.would_you_like_to_open_file_now")) {
							com.ontimize.windows.office.WindowsUtils.openFile_Script(fSelected);
						}
					}
				}
			} else {
				final ApplicationManager.ExtOpThreadsMonitor m = ApplicationManager
						.getExtOpThreadsMonitor((Component) e.getSource());
				m.addExtOpThread(eop);
				if ((f.getFormManager() != null) && (f.getFormManager().getApplication() instanceof MainApplication)) {
					((MainApplication) f.getFormManager().getApplication()).registerExtOpThreadsMonitor(m);
				}
				ApplicationManager.getExtOpThreadsMonitor((Component) e.getSource()).setVisible(true);
			}
		} catch (final Exception ex) {
			DownloadAttachmentFileAction.logger.error(null, ex);
			final Form f = this.getForm(e);
			if (f != null) {
				f.message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
			}
		}
	}

	protected ExtendedOperationThread createDownloadThread(final Form f, final File selectedFile, final Map kv,
			final FileManagementEntity entF, final EntityReferenceLocator loc) {
		return new DownloadThread(f, selectedFile, kv, entF, loc);
	}

}
