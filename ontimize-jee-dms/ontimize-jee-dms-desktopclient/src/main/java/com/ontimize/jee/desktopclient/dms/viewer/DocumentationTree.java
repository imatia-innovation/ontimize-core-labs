package com.ontimize.jee.desktopclient.dms.viewer;

import java.io.Serializable;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 * The Class DocumentationTree.
 */
public class DocumentationTree extends JTree {

	/**
	 * Instantiates a new documentation tree.
	 */
	public DocumentationTree() {
		super(new DocumentationTreeModel());
		this.setRootVisible(false);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addMouseListener(new DocumentationTreePopupMenuManager(this));
		this.setDropMode(DropMode.ON);
		this.setTransferHandler(new DocumentationTreeTransferHandler(this));
		this.setCellRenderer(new DocumentationTreeRenderer());
	}

	/**
	 * Gets the documentation model.
	 *
	 * @return the documentation model
	 */
	public DocumentationTreeModel getDocumentationModel() {
		return (DocumentationTreeModel) this.getModel();
	}

	public void deleteData() {
		this.getDocumentationModel().setCategoryTree(null, null);
		this.setSelectionRow(0);
		this.expandRow(0);
	}

	public void refreshModel(Serializable idDocument) throws DmsException {
		if (idDocument == null) {
			this.deleteData();
			return;
		}
		// consultamos el árbol
		DMSCategory rootCategory = BeansFactory.getBean(IDMSService.class).categoryGetForDocument(idDocument, null);
		this.getDocumentationModel().setCategoryTree(rootCategory, idDocument);

		// Not fire events or ignore it
		this.setSelectionRow(0);
		this.expandRow(0);
	}

}
