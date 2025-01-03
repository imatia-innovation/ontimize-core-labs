package com.ontimize.jee.desktopclient.dms.viewer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.ontimize.jee.desktopclient.dms.upload.disk.LocalDiskDmsUploadable;
import com.ontimize.jee.desktopclient.dms.util.DataWrapper;
import com.ontimize.jee.desktopclient.dms.util.DataWrapperTransferable;

/**
 * The Class DocumentationTableTransferHandler.
 */
public class DocumentationTableTransferHandler extends TransferHandler {

	/** The Constant logger. */
	private static final Logger			logger						= LoggerFactory.getLogger(DocumentationTableTransferHandler.class);

	/** The Constant serialVersionUID. */
	private static final long			serialVersionUID			= 1L;

	/** The Constant TRANSFER_HANLDER_HUMAN_ID. */
	public static final String			TRANSFER_HANLDER_HUMAN_ID	= "filesToMove";

	/** The table. */
	private final DocumentationTable	table;

	/**
	 * Instantiates a new documentation table transfer handler.
	 *
	 * @param table
	 *            the table
	 */
	public DocumentationTableTransferHandler(final DocumentationTable table) {
		super(null);
		this.table = table;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	protected DocumentationTable getTable() {
		return this.table;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(final JComponent c) {
		return TransferHandler.MOVE;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(final JComponent c) {
		final ArrayList<Object> fileIds = new ArrayList<>();
		for (final int row : this.getTable().getJTable().getSelectedRows()) {
			fileIds.add(this.getTable().getJTable().getValueAt(row, this.getTable().getColumnIndex(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE)));
		}

		return new DataWrapperTransferable<>(fileIds, this.getTable(), DocumentationTableTransferHandler.TRANSFER_HANLDER_HUMAN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	 */
	@Override
	protected void exportDone(final JComponent c, final Transferable transferable, final int act) {
		if (act == TransferHandler.MOVE) {
			for (final DataFlavor flavor : transferable.getTransferDataFlavors()) {
				if (DocumentationTableTransferHandler.TRANSFER_HANLDER_HUMAN_ID.equals(flavor.getHumanPresentableName())) {
					this.exportDoneFlavor(transferable, flavor);
				}
			}
		}
	}

	private void exportDoneFlavor(final Transferable transferable, final DataFlavor flavor) {
		try {
			final DataWrapper<ArrayList<Object>> transferData = (DataWrapper<ArrayList<Object>>) transferable.getTransferData(flavor);
			final ArrayList<Object> data = transferData.getData();
			for (final Object idDocumentFileVersion : data) {
				final Map<String, Object> kv = new Hashtable<>();
				kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, idDocumentFileVersion);
				final int index = this.getTable().getRowForKeys(kv);
				this.getTable().deleteRow(index);
			}

		} catch (final Exception error) {
			DocumentationTableTransferHandler.logger.error(null, error);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(final TransferSupport support) {
		final DataFlavor[] dataFlavors = support.getDataFlavors();
		for (final DataFlavor df : dataFlavors) {
			if (df.getMimeType().toLowerCase().startsWith("application/x-java-file")) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean importData(final JComponent comp, final Transferable t) {
		final Serializable idDocument = this.getTable().getCurrentIdDocument();
		final Serializable idCategory = this.getTable().getCurrentIdCategory();
		boolean someImported = false;
		for (final DataFlavor df : t.getTransferDataFlavors()) {
			if (!df.getMimeType().toLowerCase().startsWith("application/x-java-file")) {
				continue;
			}
			someImported |= this.importDataFlavor(t, df, idDocument, idCategory);
		}
		return someImported;
	}

	private boolean importDataFlavor(final Transferable t, final DataFlavor df, final Serializable idDocument, final Serializable idCategory) {
		boolean someImported = false;
		try {
			final List<Path> transferData = FileTools.toPath((List<File>) t.getTransferData(df));
			final String description = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.descriptioninput"));
			for (final Path file : transferData) {
				final DocumentIdentifier docIdf = new DocumentIdentifier(idDocument);
				final LocalDiskDmsUploadable transferable = new LocalDiskDmsUploadable(file, description, docIdf, idCategory);
				transferable.addObserver(new Observer() {

					@Override
					public void update(final Observable observable, final Object arg) {
						// TODO intentar sólo añadir la información de la nueva fila sin necesidad de refrescar toda la tabla
						CheckingTools.failIf(!(observable instanceof AbstractDmsUploadable), "observable not instnaceof AbstractDmsUploadable");
						final AbstractDmsUploadable uploadable = (AbstractDmsUploadable) observable;
						if (uploadable.getStatus()
								.equals(Status.COMPLETED) && (DocumentationTableTransferHandler.this.table
										.getCurrentIdDocument() != null) && DocumentationTableTransferHandler.this.table
								.getCurrentIdDocument()
								.equals(uploadable.getDocumentIdentifier()
										.getDocumentId()) && ((DocumentationTableTransferHandler.this.table
												.getCurrentIdCategory() == null) || DocumentationTableTransferHandler.this.table.getCurrentIdCategory()
												.equals(uploadable.getCategoryId()))) {
							DocumentationTableTransferHandler.this.table.refreshInThread(0);
						}
					}
				});
				DmsTransfererManagerFactory.getInstance().transfer(transferable);
				someImported = true;
			}
		} catch (final Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, DocumentationTableTransferHandler.logger);
		}
		return someImported;
	}
}