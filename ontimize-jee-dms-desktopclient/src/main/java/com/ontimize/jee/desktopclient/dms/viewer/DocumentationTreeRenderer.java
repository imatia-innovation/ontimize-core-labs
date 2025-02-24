package com.ontimize.jee.desktopclient.dms.viewer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.tree.BasicTreeCellRenderer;
import com.ontimize.jee.common.services.dms.DMSCategory;

/**
 * The Class DocumentationTreeRenderer.
 */
public class DocumentationTreeRenderer extends BasicTreeCellRenderer {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.ontimize.gui.tree.BasicTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree jTree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		JLabel label = (JLabel) super.getTreeCellRendererComponent(jTree, "", selected, expanded, leaf, row, hasFocus);
		label.setIcon(ImageManager.getIcon("ontimize-dms-images/back_22x22.png"));
		if (value instanceof DMSCategory) {
			DMSCategory category = (DMSCategory) value;
			if (category.getIdCategory() == null) {
				label.setIcon(ImageManager.getIcon("ontimize-dms-images/folder_open.png"));
				label.setText(ApplicationManager.getTranslation("dms.uncategorized"));
			} else {
				label.setIcon(ImageManager.getIcon("ontimize-dms-images/folder_document.png"));
				label.setText(category.getName());
			}
		} else if (value != null) {
			label.setIcon(ImageManager.getIcon("ontimize-dms-images/folders2.png"));
			label.setText(ApplicationManager.getTranslation(value.toString()));
		}
		return label;
	}

}
