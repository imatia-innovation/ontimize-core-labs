package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.util.TranslatedTableCellRenderer;

/**
 * The Class CloudDriveTable.
 *
 * @param <T>
 *            the generic type
 */
public class CloudDriveTable<T extends Serializable> extends JTable {

	/** The Constant serialVersionUID. */
	private static final long						serialVersionUID	= 1L;

	/** The Constant logger. */
	private static final Logger						LOGGER				= LoggerFactory.getLogger(CloudDriveTable.class);

	/** The selection listener. */
	private final ICloudFileSelectionListener<T>	selectionListener;

	/** The navigation stack. */
	private final Stack<String>						navigationStack		= new Stack<>();

	/** The current folder id. */
	private String									currentFolderId;

	/** The cloud manager. */
	private final transient ICloudManager<T>		cloudManager;

	/**
	 * Instantiates a new cloud drive table.
	 *
	 * @param selectionListener
	 *            the selection listener
	 * @param cloudManager
	 *            the cloud manager
	 */
	public CloudDriveTable(ICloudFileSelectionListener<T> selectionListener, ICloudManager<T> cloudManager) {
		super(new CloudDriveTableModel<T>());
		this.cloudManager = cloudManager;
		this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableCellRenderer tableRenderer = cloudManager.getTableRenderer();
		for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {
			this.getColumnModel().getColumn(i).setCellRenderer(tableRenderer);
		}
		this.getTableHeader().setDefaultRenderer(new TranslatedTableCellRenderer(this.getTableHeader().getDefaultRenderer()));
		this.addMouseListener(new NavigationMouseListener());
		this.selectionListener = selectionListener;
	}

	/**
	 * Fire selection event.
	 *
	 * @param selectedFile
	 *            the selected file
	 */
	private void fireSelectionEvent(T selectedFile) {
		if (this.selectionListener != null) {
			this.selectionListener.onFileSelected(selectedFile);
		}
	}

	/**
	 * Gets the remote drive model.
	 *
	 * @return the remote drive model
	 */
	public CloudDriveTableModel<T> getRemoteDriveModel() {
		return (CloudDriveTableModel<T>) this.getModel();
	}

	/**
	 * Gets the selected file.
	 *
	 * @return the selected file
	 */
	public T getSelectedFile() {
		return this.getRemoteDriveModel().getFileAtRow(this.getSelectedRow());
	}

	/**
	 * Start navigation.
	 *
	 * @throws DmsException
	 *             the dms exception
	 */
	public void startNavigation() throws DmsException {
		this.navigationStack.clear();
		this.browseFolder(this.cloudManager.getRootFolderId());
	}

	/**
	 * Browse folder.
	 *
	 * @param folderId
	 *            the folder id
	 */
	private void browseFolder(final String folderId) {
		new SwingWorker<List<T>, Void>() {

			@Override
			protected List<T> doInBackground() throws Exception {
				return CloudDriveTable.this.cloudManager.retrieveFilesInFolder(folderId);
			}

			@Override
			protected void done() {
				try {
					List<T> files = this.get();
					if (!CloudDriveTable.this.cloudManager.getRootFolderId().equals(folderId)) {
						T backFile = CloudDriveTable.this.cloudManager.getBackFile();
						files.add(0, backFile);
					}
					CloudDriveTable.this.getRemoteDriveModel().setDataList(files);
					CloudDriveTable.this.currentFolderId = folderId;
				} catch (Exception error) {
					MessageManager.getMessageManager().showExceptionMessage(error, CloudDriveTable.LOGGER);
				}
			};
//		}.executeOperation(this);
		}.execute();
	}

	/**
	 * On file selected.
	 */
	public void onFileSelected() {
		T selectedFile = this.getSelectedFile();
		if (CloudDriveTable.this.cloudManager.getBackFile().equals(selectedFile)) {
			this.browseFolder(this.navigationStack.pop());
		} else if (this.cloudManager.isFolder(selectedFile)) {
			this.navigationStack.push(this.currentFolderId);
			this.browseFolder(this.cloudManager.getFolderId(selectedFile));
		} else {
			this.fireSelectionEvent(selectedFile);
		}
	}

	/**
	 * The listener interface for receiving navigationMouse events. The class that is interested in processing a navigationMouse event implements this interface, and the object
	 * created with that class is registered with a component using the component's <code>addNavigationMouseListener<code> method. When the navigationMouse event occurs, that
	 * object's appropriate method is invoked.
	 *
	 * @see NavigationMouseEvent
	 */
	private class NavigationMouseListener extends MouseAdapter {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent event) {
			if ((event.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(event)) {
				CloudDriveTable.this.onFileSelected();
			}
		}
	}
}
