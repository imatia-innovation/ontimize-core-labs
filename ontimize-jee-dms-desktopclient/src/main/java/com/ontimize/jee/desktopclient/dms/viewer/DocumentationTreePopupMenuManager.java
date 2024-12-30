package com.ontimize.jee.desktopclient.dms.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 * The Class DocumentationTreePopupMenuManager.
 */
public class DocumentationTreePopupMenuManager extends MouseAdapter {

	/** The Constant logger. */
	private static final Logger		logger	= LoggerFactory.getLogger(DocumentationTreePopupMenuManager.class);

	/** The tree. */
	private final DocumentationTree	tree;

	/** The current popup category. */
	private DMSCategory				currentPopupCategory;

	/** The current tree path. */
	private TreePath				currentTreePath;

	/** The popup. */
	private JPopupMenu				popup;
	private JMenuItem				menuItemAdd;
	private JMenuItem				menuItemDelete;

	/**
	 * Instantiates a new documentation tree popup menu manager.
	 *
	 * @param documentationTree
	 *            the documentation tree
	 */
	public DocumentationTreePopupMenuManager(DocumentationTree documentationTree) {
		super();
		this.tree = documentationTree;
	}

	/**
	 * Gets the tree.
	 *
	 * @return the tree
	 */
	protected DocumentationTree getTree() {
		return this.tree;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		if (SwingUtilities.isRightMouseButton(e)) {
			TreePath pathForLocation = this.getTree().getPathForLocation(e.getX(), e.getY());
			this.showPopup(pathForLocation, e.getX(), e.getY());
		}
	}

	/**
	 * Show popup.
	 *
	 * @param treePath
	 *            the tree path
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	protected void showPopup(TreePath treePath, int x, int y) {
		if (this.popup == null) {
			this.buildPopup();
		}

		this.currentTreePath = null;
		this.currentPopupCategory = null;
		this.menuItemAdd.setEnabled(false);
		this.menuItemDelete.setEnabled(false);
		if (this.tree.getDocumentationModel().getIdDocument() != null && treePath != null) {
			if (treePath.getLastPathComponent() instanceof String) {
				this.menuItemAdd.setEnabled(true);
			} else if (treePath.getLastPathComponent() instanceof DMSCategory) {
				this.currentTreePath = treePath;
				this.currentPopupCategory = (DMSCategory) treePath.getLastPathComponent();
				if (this.currentPopupCategory.getIdCategory() != null) {
					this.menuItemAdd.setEnabled(true);
					this.menuItemDelete.setEnabled(true);
				}
			}
		}

		this.popup.show(this.getTree(), x, y);
	}

	/**
	 * Builds the popup.
	 */
	protected void buildPopup() {
		this.popup = new JPopupMenu("Menu");
		this.menuItemAdd = new JMenuItem(ApplicationManager.getTranslation("dms.newCategory"));
		this.menuItemAdd.addActionListener(new AddCategoryAction());

		this.menuItemDelete = new JMenuItem(ApplicationManager.getTranslation("dms.deleteCategory"));
		this.menuItemDelete.addActionListener(new DeleteCategoryAction());

		this.popup.add(this.menuItemAdd);
		this.popup.add(this.menuItemDelete);
	}

	/**
	 * The Class AddCategoryAction.
	 */
	protected class AddCategoryAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String categoryName = JOptionPane.showInputDialog(DocumentationTreePopupMenuManager.this.getTree(), ApplicationManager.getTranslation("dms.categorynameinput"));
				if (categoryName != null) {
					Serializable idDocument = DocumentationTreePopupMenuManager.this.tree.getDocumentationModel().getIdDocument();
					Serializable idParentCategory = DocumentationTreePopupMenuManager.this.currentPopupCategory == null ? null : DocumentationTreePopupMenuManager.this.currentPopupCategory
					        .getIdCategory();
					Serializable idCategory = BeansFactory.getBean(IDMSService.class).categoryInsert(idDocument, categoryName, idParentCategory, null);
					DMSCategory newCategory = new DMSCategory(idDocument, idCategory, categoryName, null, DocumentationTreePopupMenuManager.this.currentPopupCategory);
					DocumentationTreePopupMenuManager.this.tree.getDocumentationModel().insertCategory(DocumentationTreePopupMenuManager.this.currentPopupCategory, newCategory);
				}
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(ex, DocumentationTreePopupMenuManager.logger);
			}
		}
	}

	/**
	 * The Class AddCategoryAction.
	 */
	protected class DeleteCategoryAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (JOptionPane.showConfirmDialog(DocumentationTreePopupMenuManager.this.getTree(), ApplicationManager.getTranslation("dms.questiondeletecategory"),
				        ApplicationManager.getTranslation("dms.questiondeletecategorytitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
					return;
				}

				BeansFactory.getBean(IDMSService.class).categoryDelete(DocumentationTreePopupMenuManager.this.currentPopupCategory.getIdCategory());
				DocumentationTreePopupMenuManager.this.getTree().refreshModel(DocumentationTreePopupMenuManager.this.currentPopupCategory.getIdDocument());
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(ex, DocumentationTreePopupMenuManager.logger);
			}
		}
	}
}
