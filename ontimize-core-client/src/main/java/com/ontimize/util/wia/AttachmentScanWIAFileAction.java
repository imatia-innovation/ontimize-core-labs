package com.ontimize.util.wia;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.actions.AbstractButtonAction;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.FileManagementEntity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.FileUtils;

import eu.gnome.morena.Device;
import eu.gnome.morena.Scanner;

public class AttachmentScanWIAFileAction extends AbstractButtonAction {

	private static final Logger logger = LoggerFactory.getLogger(AttachmentScanWIAFileAction.class);

	public static final String M_INSERT_DESCRIPTION_SCAN_ATTACHMENT_FILE = "attachment.provide_description_scan_attached_file";

	protected int sizeBlock = 64 * 1024;

	protected String entityName = null;

	protected boolean addDescription = true;

	protected boolean refreshForm = true;

	protected boolean synchronize = true;

	protected String uriSound = null;

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	protected ArrayList<File> feederFiles = new ArrayList<File>();

	public ArrayList<File> getFeederFiles() {
		return this.feederFiles;
	}

	public void setFeederFiles(final ArrayList<File> feederFiles) {
		this.feederFiles = feederFiles;
	}

	protected class ScanThread extends ExtendedOperationThread {

		protected ScanSession session = null;

		protected Device device = null;

		protected int feederUnit = -1;

		public ScanThread(final ScanSession session, final Device device, final int feederUnit) {
			this.session = session;
			this.device = device;
			this.feederUnit = feederUnit;
		}

		@Override
		public void run() {
			this.hasStarted = true;
			final List<File> filesToDelete = new ArrayList<File>();
			try {
				this.status = ApplicationManager.getTranslation("scan_feeder");
				this.session.startSession(this.device, this.feederUnit);
				File file = null;
				while (null != (file = this.session.getImageFile())) {
					AttachmentScanWIAFileAction.this.feederFiles.add(file);
					filesToDelete.add(file);
				}
			} catch (final Exception e) {
				AttachmentScanWIAFileAction.logger.error(null, e);
			} finally {
				this.status = ApplicationManager.getTranslation("deleting_temporal_objects");
				for (final File fileDelete : filesToDelete) {
					fileDelete.deleteOnExit();
				}
				this.hasFinished = true;
			}
		}

	}

	protected class WIASendThread extends ExtendedOperationThread {

		protected Form currentForm = null;

		protected String description = null;

		protected String uriSoundPath = null;

		protected List<File> multiFile = null;

		public WIASendThread(final Form f, final List<File> listOfFiles, final String sDescription, final String uriSound) {
			super(ApplicationManager.getTranslation("attach_file", ApplicationManager.getApplicationBundle()));
			this.description = sDescription;
			this.currentForm = f;
			this.multiFile = listOfFiles;
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
			String rId = null;
			EntityReferenceLocator loc = null;
			FileManagementEntity entF = null;

			try {

				loc = this.currentForm.getFormManager().getReferenceLocator();
				final Entity ent = loc.getEntityReference(AttachmentScanWIAFileAction.this.entityName);
				if (!(ent instanceof FileManagementEntity)) {
					this.hasFinished = true;
					this.status = ApplicationManager.getTranslation("entity_not_implemets_fileManagementEntity");
					return;
				}

				entF = (FileManagementEntity) ent;
				final Map kv = AttachmentScanWIAFileAction.this.getAttachmentValuesKeys(this.currentForm);
				this.status = ApplicationManager.getTranslation("attachment.initiating_transfer");
				for (final File selectedFile : this.multiFile) {
					final long totalSize = selectedFile.length();
					synchronized (entF) {
						rId = entF.prepareToReceive(kv, selectedFile.getName(), selectedFile.getPath(),
								this.description, loc.getSessionId());
					}
					try {
						Thread.sleep(100);
					} catch (final Exception ex) {
						AttachmentScanWIAFileAction.logger.trace(null, ex);
					}

					if (this.isCancelled()) {
						entF.cancelReceiving(rId, loc.getSessionId());
						this.hasFinished = true;
						this.status = ApplicationManager.getTranslation("cancelled");
						return;
					}

					final FileInputStream fIn = new FileInputStream(selectedFile);
					this.status = ApplicationManager.getTranslation("attachment.reading_input_file");
					bIn = new BufferedInputStream(fIn);
					bOut = new ByteArrayOutputStream();
					int by = -1;
					int read = 0;
					this.status = ApplicationManager.getTranslation("attachment.sending_input_file");
					int totalRead = 0;
					final long tIni = System.currentTimeMillis();
					long spendTime = 0;
					while ((by = bIn.read()) != -1) {
						Thread.yield();
						bOut.write(by);
						read++;
						totalRead++;
						this.currentPosition = totalRead;
						if (read >= AttachmentScanWIAFileAction.this.sizeBlock) {
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
								this.status = ApplicationManager.getTranslation("cancelled");
								this.hasFinished = true;
								return;
							}
							try {
								Thread.sleep(25);
							} catch (final Exception ex) {
								AttachmentScanWIAFileAction.logger.trace(null, ex);
							}
						}
					}
					if (bOut.size() > 0) {
						entF.putBytes(rId, new BytesBlock(bOut.toByteArray()), loc.getSessionId());
					}
					entF.finishReceiving(rId, loc.getSessionId());
				}

				this.uploadFinished();
				this.status = ApplicationManager.getTranslation("finished");
				if (this.uriSoundPath != null) {
					ApplicationManager.playSound(this.uriSoundPath);
				}

			} catch (final Exception e) {
				AttachmentScanWIAFileAction.logger.error(null, e);
				this.res = new EntityResultMapImpl();
				((EntityResult) this.res).setCode(EntityResult.OPERATION_WRONG);
				((EntityResult) this.res).setMessage(e.getMessage());
				this.status = ApplicationManager.getTranslation("error_scanning_image");
				try {
					if ((rId != null) && (entF != null)) {
						entF.cancelReceiving(rId, loc.getSessionId());
					}
				} catch (final Exception ex) {
					AttachmentScanWIAFileAction.logger.error(null, ex);
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
					AttachmentScanWIAFileAction.logger.error(null, e);
				}
			}
		}

	}

	public AttachmentScanWIAFileAction(final String entity) {
		this(entity, true);
	}

	public AttachmentScanWIAFileAction(final String entity, final boolean scanAddDescription) {
		this(entity, scanAddDescription, true);
	}

	public AttachmentScanWIAFileAction(final String entity, final boolean scanAddDescription, final boolean scanRefreshForm) {
		this(entity, scanAddDescription, scanRefreshForm, true);
	}

	public AttachmentScanWIAFileAction(final String entity, final boolean scanAddDescription, final boolean scanRefreshForm,
			final boolean synchronize) {
		this(entity, scanAddDescription, scanRefreshForm, synchronize, null);
	}

	public AttachmentScanWIAFileAction(final String entity, final boolean scanAddDescription, final boolean scanRefreshForm,
			final boolean synchronize, final String uriSound) {
		this.entityName = entity;
		this.addDescription = scanAddDescription;
		this.refreshForm = scanRefreshForm;
		this.synchronize = synchronize;
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

	protected ExtendedOperationThread createSendThread(final Form f, final List listOfFiles, final String description,
			final String uriSound) {
		return new WIASendThread(f, listOfFiles, description, uriSound);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (WIAUtilities.isWIAEnabled()) {
			final ByteArrayOutputStream bOut = null;
			final WIAManager wManager = new WIAManager();
			try {

				final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());

				wManager.selectWIASource(w);

				// TODO If not selected device, do not continue

				final Form parentForm = this.getForm(e);

				final EntityReferenceLocator loc = parentForm.getFormManager().getReferenceLocator();
				final Entity ent = loc.getEntityReference(this.entityName);
				if (!(ent instanceof FileManagementEntity)) {
					throw new Exception("The entity " + this.entityName + " does not implement FileManagementEntity");
				} else {

					if (parentForm == null) {
						throw new Exception("Parent form is null");
					} else {

						Object oDescription = null;
						if (this.addDescription) {
							oDescription = MessageDialog.showInputMessage(
									SwingUtilities.getWindowAncestor((Component) e.getSource()),
									AttachmentScanWIAFileAction.M_INSERT_DESCRIPTION_SCAN_ATTACHMENT_FILE,
									parentForm.getResourceBundle());
						}

						final Device device = wManager.selectedDevice;
						final List<File> filesToDelete = new ArrayList<File>();

						if (device != null) {

							// for scanner device set the scanning parameters
							if (device instanceof Scanner) {
								final Scanner scanner = (Scanner) device;
								scanner.setMode(Scanner.RGB_8);
								scanner.setResolution(200);
								// find feeder unit
								int feederUnit = scanner.getFeederFunctionalUnit();
								AttachmentScanWIAFileAction.logger.debug("Feeder unit : {}",
										feederUnit >= 0 ? feederUnit : "none found - trying 0");
								if (feederUnit < 0) {
									feederUnit = 0;
									// 0 designates a default unit
								}

								if (scanner.isDuplexSupported()) {
									scanner.setDuplexEnabled(true);
								}

								final int pageNo = 1;
								final ScanSession session = new ScanSession();
								// start batch scan
								try {

									final ExtendedOperationThread eopScanThread = new ScanThread(session, device, feederUnit);
									ApplicationManager.proccessNotCancelableOperation(parentForm, eopScanThread, 0);

									AttachmentScanWIAFileAction.logger.debug("Feeder used: {}", session.isFeederUsed());
									if (!session.isFeederUsed()) {
										((Scanner) wManager.selectedDevice).setFunctionalUnit(0);
										final java.awt.Image im = wManager.acquire(w);
										final List flatbedFile = new ArrayList<File>();
										final File f = new File(System.getProperty("java.io.tmpdir"), "scntemp.jpg");
										f.deleteOnExit();
										ImageIO.write((RenderedImage) im, "jpg", f);
										flatbedFile.add(f);
										this.transformAndUploadImage(flatbedFile, parentForm, bOut, oDescription, w, e);
									} else {
										this.transformAndUploadImage(this.feederFiles, parentForm, bOut, oDescription,
												w, e);
									}
								} catch (final Exception ex) {
									// check if error is related to empty ADF
									if (session.isEmptyFeeder()) {
										AttachmentScanWIAFileAction.logger
										.debug("No more sheets in the document feeder", ex);
									} else {
										AttachmentScanWIAFileAction.logger.error(null, ex);
									}
								}
							}
						}
					}
				}

			} catch (final IOException ex) {
				AttachmentScanWIAFileAction.logger.error(null, ex);
			} catch (final Exception ex) {
				AttachmentScanWIAFileAction.logger.error(null, ex);
				final Form f = this.getForm(e);
				if (f != null) {
					f.message(ex.getMessage(), Form.ERROR_MESSAGE);
				}
			} finally {
				try {
					this.feederFiles.clear();
					if (bOut != null) {
						bOut.close();
					}
				} catch (final Exception ex) {
					AttachmentScanWIAFileAction.logger.trace(null, ex);
				}
			}
		} else {
			AttachmentScanWIAFileAction.logger
			.error("The Twain libraries are not present in this application. This button not perform any action");
		}
	}

	private void transformAndUploadImage(final List<File> files, final Form parentForm, ByteArrayOutputStream bOut,
			final Object oDescription, final Window w, final ActionEvent e) throws IOException {

		if (files.isEmpty()) {
			parentForm.message("image_not_acquired", Form.INFORMATION_MESSAGE);
		} else {

			final List<File> jpegImages = new ArrayList<File>();

			int i = 0;

			for (final File f : files) {
				i++;
				final Image im = ImageIO.read(f);
				bOut = new ByteArrayOutputStream();
				FileUtils.saveJPEGImage(im, bOut);
				bOut.flush();
				final InputStream in = new ByteArrayInputStream(bOut.toByteArray());
				final BufferedImage bImageFromConvert = ImageIO.read(in);
				final Date actualDate = new Date(System.currentTimeMillis());
				final String name = this.sdf.format(actualDate);
				final File tmpFile = new File(System.getProperty("java.io.tmpdir"), "SCN_" + name + "_" + i + ".jpg");
				ImageIO.write(bImageFromConvert, "jpg", tmpFile);
				jpegImages.add(tmpFile);
				tmpFile.deleteOnExit();
			}

			if (this.synchronize) {
				final ExtendedOperationThread eop = this.createSendThread(parentForm, jpegImages, (String) oDescription,
						this.uriSound);
				if (w instanceof Dialog) {
					ApplicationManager.proccessNotCancelableOperation((Dialog) w, eop, 0);
				} else {
					ApplicationManager.proccessNotCancelableOperation((Frame) w, eop, 0);
				}
				if (eop.getResult() != null) {
					parentForm.message(eop.getResult().toString(), Form.ERROR_MESSAGE);
				} else {
					parentForm.message("attachment.file_has_been_attached_successfully", Form.INFORMATION_MESSAGE);
					if (this.refreshForm) {
						parentForm.refreshCurrentDataRecord();
					}
				}
			} else {
				final ExtendedOperationThread eop = this.createSendThread(parentForm, jpegImages, (String) oDescription,
						this.uriSound);
				final ApplicationManager.ExtOpThreadsMonitor m = ApplicationManager
						.getExtOpThreadsMonitor((Component) e.getSource());
				m.addExtOpThread(eop);
				if ((parentForm.getFormManager() != null)
						&& (parentForm.getFormManager().getApplication() instanceof MainApplication)) {
					((MainApplication) parentForm.getFormManager().getApplication()).registerExtOpThreadsMonitor(m);
				}
				ApplicationManager.getExtOpThreadsMonitor((Component) e.getSource()).setVisible(true);
			}
		}

	}

}
