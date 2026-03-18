package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import com.dropbox.core.DbxEntry;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.CellRenderer;
import com.ontimize.jee.common.tools.FileTools;

public class DropboxTableRenderer extends CellRenderer {

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public DropboxTableRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", selected, hasFocus, row, column);
		if (value instanceof DbxEntry) {
			DbxEntry file = (DbxEntry) value;
			component.setIcon(null);
			component.setText(null);
			switch (column) {
				case 0:
					component.setIcon(this.getIcon(file.iconName));
					component.setText(file.name);
					break;
				case 1:
					if (file.isFile()) {
						component.setText(FileTools.readableFileSize(file.asFile().numBytes));
					}
					break;
				case 2:
					if (file.isFile()) {
						Date modifiedDate = file.asFile().lastModified;
						if (modifiedDate != null) {
							component.setText(this.sdf.format(modifiedDate));
						}
					}
					break;
				case 3:
					break;
				case 4:
					break;
				default:
					break;
			}
		}
		return component;
	}

	private ImageIcon getIcon(String iconLink) {
		return ImageManager.getIcon("ontimize-dms-images/dropbox/" + iconLink + ".gif");
	}
}
