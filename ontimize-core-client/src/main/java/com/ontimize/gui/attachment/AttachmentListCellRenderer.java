package com.ontimize.gui.attachment;

import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.swing.icon.CompoundIcon;

public class AttachmentListCellRenderer implements ListCellRenderer {

	private static final Logger logger = LoggerFactory.getLogger(AttachmentListCellRenderer.class);

	public static final String EDIT_DESCRIPTION_TIP = "attachmentlistcellrenderer.editdescription";

	public static final int CHECK = 0;

	public static final int SAVE = 1;

	public static final int OPEN = 2;

	public static final int DELETE = 3;

	public static final int EDIT_DESCRIPTION = 4;

	public static final String URI_KEY = ImageManager.URI_KEY;

	public static final String DEFAULT_URI_PROP = "com/ontimize/gui/resources/fileextensionsicons.properties";

	public static final String UNKNOWN_EXTENSION_ICON = ImageManager.UNKNOWN_EXTENSION;

	protected static Map icons = new Hashtable();

	protected static Map privateIcons = new Hashtable();

	protected static ImageIcon unknownIcon = null;

	protected static Icon privateUnknownIcon = null;

	protected static ImageIcon key = null;

	protected static boolean initIcons = true;

	protected AttachmentComponent component = new AttachmentComponent();

	private ResourceBundle bundle = null;

	protected static void initIcons() {
		if (AttachmentListCellRenderer.initIcons) {
			final ImageIcon temp = ApplicationManager.getIcon(AttachmentListCellRenderer.URI_KEY);
			if (temp != null) {
				AttachmentListCellRenderer.key = temp;
			}
			final URL url = AttachmentListCellRenderer.class.getClassLoader()
					.getResource(AttachmentListCellRenderer.DEFAULT_URI_PROP);
			if (url == null) {
				AttachmentListCellRenderer.logger.debug("No encontrado " + AttachmentListCellRenderer.DEFAULT_URI_PROP);
			} else {
				final Properties prop = new Properties();
				try {
					prop.load(url.openStream());
					final Enumeration enu = prop.keys();
					while (enu.hasMoreElements()) {
						final Object oKey = enu.nextElement();
						final Object oValue = prop.get(oKey);
						final ImageIcon applicationIcon = ApplicationManager.getIcon((String) oValue);
						final CompoundIcon compound = new CompoundIcon(applicationIcon, AttachmentListCellRenderer.key);
						AttachmentListCellRenderer.icons.put(oKey, applicationIcon);
						AttachmentListCellRenderer.privateIcons.put(oKey, compound);
					}
					AttachmentListCellRenderer.unknownIcon = ApplicationManager
							.getIcon(AttachmentListCellRenderer.UNKNOWN_EXTENSION_ICON);
					AttachmentListCellRenderer.privateUnknownIcon = new CompoundIcon(
							AttachmentListCellRenderer.unknownIcon, AttachmentListCellRenderer.key);

				} catch (final IOException e) {
					AttachmentListCellRenderer.logger.error(null, e);
				}
			}
		}
	}

	public static Map getIcons() {
		return AttachmentListCellRenderer.icons;
	}

	public static Map getPrivateIcons() {
		return AttachmentListCellRenderer.privateIcons;
	}

	public static ImageIcon getUnknownIcon() {
		return AttachmentListCellRenderer.unknownIcon;
	}

	public static Icon getPrivateUnknownIcon() {
		return AttachmentListCellRenderer.privateUnknownIcon;
	}

	public AttachmentListCellRenderer(final ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public void setResourceBundle(final ResourceBundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
			final boolean cellHasFocus) {
		if (value == null) {
			this.component.setRecord(new Hashtable());
			return this.component;
		}

		if (list instanceof AttachmentListPopup) {
			final AttachmentListPopup attachmentList = (AttachmentListPopup) list;
			if (index == attachmentList.currentSelectionIndex) {
				this.component.setState(attachmentList.currentAction);
			} else {
				this.component.setState(-1);
			}

			final Map h = attachmentList.getRecord(value);
			if (h == null) {
				return null;
			}
			this.component.setRecord(h);
			if (this.component.getState() == AttachmentListCellRenderer.CHECK) {
				this.component.setToolTipText(
						ApplicationManager.getTranslation(AttachmentComponent.ATTACHMENT_PRIVATE_TIP, this.bundle));
			} else if (this.component.getState() == AttachmentListCellRenderer.SAVE) {
				this.component.setToolTipText(ApplicationManager.getTranslation("save", this.bundle));
			} else if (this.component.getState() == AttachmentListCellRenderer.DELETE) {
				this.component.setToolTipText(ApplicationManager.getTranslation("delete", this.bundle));
			} else if (this.component.getState() == AttachmentListCellRenderer.OPEN) {
				this.component.setFileTooltip(this.bundle);
			} else if (this.component.getState() == AttachmentListCellRenderer.EDIT_DESCRIPTION) {
				this.component.setToolTipText(ApplicationManager
						.getTranslation(AttachmentListCellRenderer.EDIT_DESCRIPTION_TIP, this.bundle));
			} else {
				this.component.setToolTipText(null);
			}
			return this.component;
		} else {
			this.component.setRecord(new Hashtable());
			return this.component;
		}
	}

}
