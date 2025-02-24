package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.awt.Component;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.DateTime;
import com.ontimize.gui.table.CellRenderer;
import com.ontimize.jee.common.tools.FileTools;

public class GoogleDriveTableRenderer extends CellRenderer {

	private static final Logger					logger		= LoggerFactory.getLogger(GoogleDriveTableRenderer.class);
	private static final Map<String, ImageIcon>	ICON_CACHE	= new HashMap<>();
	private final SimpleDateFormat				sdf			= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public GoogleDriveTableRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", selected, hasFocus, row, column);
		if (value instanceof GoogleFile) {
			GoogleFile file = (GoogleFile) value;
			component.setIcon(null);
			component.setText(null);
			switch (column) {
				case 0:
					component.setIcon(this.getIcon(file.getIconLink()));
					component.setText(file.getTitle());
					break;
				case 1:
					component.setText(file.getFileSize() == null ? null : FileTools.readableFileSize(file.getFileSize()));
					break;
				case 2:
					DateTime modifiedDate = file.getModifiedDate();
					if (modifiedDate != null) {
						component.setText(this.sdf.format(modifiedDate.getValue()));
					}
					break;
				case 3:
					component.setText(file.getLastModifyingUserName());
					break;
				case 4:
					component.setText(file.getDescription());
					break;
				default:
					break;
			}
		}
		return component;
	}

	private ImageIcon getIcon(String iconLink) {
		ImageIcon res = GoogleDriveTableRenderer.ICON_CACHE.get(iconLink);
		if (res == null) {
			try {
				res = new ImageIcon(ImageIO.read(new URL(iconLink)));
			} catch (Exception error) {
				GoogleDriveTableRenderer.logger.warn("Image not found {}", iconLink, error);
			}
			GoogleDriveTableRenderer.ICON_CACHE.put(iconLink, res);
		}
		return res;
	}
}
