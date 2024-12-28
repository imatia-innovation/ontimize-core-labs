package com.ontimize.gui.formtemplates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.images.ImageManager;

public class PopupPrintingTemplateListCellRenderer extends JLabel implements ListCellRenderer {

	private static final Logger logger = LoggerFactory.getLogger(PopupPrintingTemplateListCellRenderer.class);

	public static final int NONE = -1;

	public static final int CHECK = 0;

	public static final int DELETE = 1;

	public static final int FILE = 2;

	public static final int SAVE = 3;

	public static final int PRIVATE = 4;

	protected int point = -1;

	protected int sel = -1;

	protected boolean paintBorder = false;

	public class Label extends JLabel {

		boolean paintBorder = false;

		public void setBorderPainted(final boolean paint) {
			this.paintBorder = paint;
		}

		@Override
		public void paintBorder(final Graphics g) {
			if (this.paintBorder) {
				super.paintBorder(g);
			}
		}

	}

	protected Color selBgColor = UIManager.getColor("List.selectionBackground");

	protected Color selFgColor = UIManager.getColor("List.selectionForeground");

	protected Color notSelectedForegroundColor = UIManager.getColor("List.foreground");

	protected Color notSelectedBackgroundColor = UIManager.getColor("List.background");

	public static final String DEFAULT_PROP_URI = "com/ontimize/gui/resources/fileextensionsicons.properties";

	public static final String UNKNOWN_EXTENSION_ICON = ImageManager.UNKNOWN_EXTENSION;

	public static final String TRASH = ImageManager.RECYCLER;

	protected static boolean initIcons = true;

	protected static Map icons = new Hashtable();

	protected Icon trashIcon = null;

	protected Icon saveIcon = null;

	protected String keySelected = null;

	protected String keyNameFile = null;

	protected JRadioButton check = new JRadioButton();

	protected JCheckBox privatetemplate = new JCheckBox();

	protected Label delete = new Label();

	protected Label save = new Label();

	protected JPanel panelCellRenderer = null;

	protected boolean enablePrivateTemplates = false;

	public PopupPrintingTemplateListCellRenderer(final String keySelected, final String keyNameFile, final ResourceBundle bundle,
			final boolean enablePrivateTemplates) {

		this.enablePrivateTemplates = enablePrivateTemplates;
		this.keySelected = keySelected;
		this.keyNameFile = keyNameFile;

		if (PopupPrintingTemplateListCellRenderer.initIcons) {
			this.trashIcon = ImageManager.getIcon(PopupPrintingTemplateListCellRenderer.TRASH);
			this.saveIcon = ImageManager.getIcon(ImageManager.SAVE_TABLE_FILTER);

			final URL url = this.getClass()
					.getClassLoader()
					.getResource(PopupPrintingTemplateListCellRenderer.DEFAULT_PROP_URI);
			if (url == null) {
				PopupPrintingTemplateListCellRenderer.logger
				.debug("Not found " + PopupPrintingTemplateListCellRenderer.DEFAULT_PROP_URI);
			} else {
				final Properties prop = new Properties();
				try {
					prop.load(url.openStream());
					final Enumeration enu = prop.keys();
					while (enu.hasMoreElements()) {
						final Object oKey = enu.nextElement();
						final Object oValue = prop.get(oKey);
						final ImageIcon applicationIcon = ApplicationManager.getIcon((String) oValue);
						PopupPrintingTemplateListCellRenderer.icons.put(oKey, applicationIcon);
					}
				} catch (final IOException e) {
					PopupPrintingTemplateListCellRenderer.logger.error(null, e);
				}
			}
		}

		if (this.trashIcon != null) {
			this.delete.setIcon(this.trashIcon);
		}
		if (this.saveIcon != null) {
			this.save.setIcon(this.saveIcon);
		}

		final Border border = new EtchedBorder(EtchedBorder.RAISED);
		this.setBorder(border);
		this.setBorderPainted(false);
		this.check.setBorder(border);
		this.check.setBorderPainted(false);
		if (enablePrivateTemplates) {
			this.privatetemplate.setBorder(border);
			this.privatetemplate.setBorderPainted(false);
		}
		this.delete.setBorder(border);
		this.delete.setBorderPainted(false);
		this.save.setBorder(border);
		this.save.setBorderPainted(false);

		this.panelCellRenderer = new JPanel(new GridBagLayout());
		if (ApplicationManager.useOntimizePlaf) {
			this.panelCellRenderer.setOpaque(false);
		}
		int pos = 0;
		this.panelCellRenderer.add(this.check, new GridBagConstraints(pos++, 0, 1, 1, 0, 1, GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
		if (enablePrivateTemplates) {
			this.panelCellRenderer.add(this.privatetemplate,
					new GridBagConstraints(pos++, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
							new Insets(0, 0, 0, 0), 0, 0));
		}
		this.panelCellRenderer.add(this.save, new GridBagConstraints(pos++, 0, 1, 1, 0, 1, GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
		this.panelCellRenderer.add(this.delete, new GridBagConstraints(pos++, 0, 1, 1, 0, 1, GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
		this.panelCellRenderer.add(this, new GridBagConstraints(pos, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	public void setBorderPainted(final boolean paint) {
		this.paintBorder = paint;
	}

	@Override
	public void paintBorder(final Graphics g) {
		if (this.paintBorder) {
			super.paintBorder(g);
		}
	}

	protected void setSelected(final boolean selected) {
		this.check.setSelected(selected);
	}

	protected void setPointed(final int point, final int sel) {
		this.point = point;
		this.sel = sel;
		this.repaint();
	}

	protected void setBorderPaint(final int r) {
		if (this.sel == r) {
			switch (this.point) {
			case CHECK:
				this.check.setBorderPainted(true);
				if (this.enablePrivateTemplates) {
					this.privatetemplate.setBorderPainted(false);
				}
				this.delete.setBorderPainted(false);
				this.save.setBorderPainted(false);
				this.setBorderPainted(false);
				break;
			case PRIVATE:
				this.check.setBorderPainted(false);
				if (this.enablePrivateTemplates) {
					this.privatetemplate.setBorderPainted(true);
				}
				this.delete.setBorderPainted(false);
				this.save.setBorderPainted(false);
				this.setBorderPainted(false);
				break;
			case DELETE:
				this.check.setBorderPainted(false);
				if (this.enablePrivateTemplates) {
					this.privatetemplate.setBorderPainted(false);
				}
				this.delete.setBorderPainted(true);
				this.save.setBorderPainted(false);
				this.setBorderPainted(false);
				break;
			case FILE:
				this.check.setBorderPainted(false);
				if (this.enablePrivateTemplates) {
					this.privatetemplate.setBorderPainted(false);
				}
				this.delete.setBorderPainted(false);
				this.save.setBorderPainted(false);
				this.setBorderPainted(true);
				break;
			case SAVE:
				this.check.setBorderPainted(false);
				if (this.enablePrivateTemplates) {
					this.privatetemplate.setBorderPainted(false);
				}
				this.delete.setBorderPainted(false);
				this.save.setBorderPainted(true);
				this.setBorderPainted(false);
				break;
			case NONE:
				this.check.setBorderPainted(false);
				if (this.enablePrivateTemplates) {
					this.privatetemplate.setBorderPainted(false);
				}
				this.delete.setBorderPainted(false);
				this.save.setBorderPainted(false);
				this.setBorderPainted(false);
				break;
			}
		} else {
			this.check.setBorderPainted(false);
			if (this.enablePrivateTemplates) {
				this.privatetemplate.setBorderPainted(false);
			}
			this.delete.setBorderPainted(false);
			this.save.setBorderPainted(false);
			this.setBorderPainted(false);
		}
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int row, final boolean selected,
			final boolean hasFocus) {

		final Color noSelFgColor = UIManager.getColor("List.foreground");
		final Color noSelBgColor = UIManager.getColor("List.background");

		this.setBorderPaint(row);

		this.setForeground(noSelFgColor);
		this.setBackground(noSelBgColor);

		if (value instanceof Map) {
			final Map h = (Map) value;
			if (h.containsKey(this.keyNameFile)) {
				final String sName = (String) h.get(this.keyNameFile);

				this.setText(sName);
				int index = -1;
				if ((index = sName.lastIndexOf(".")) != -1) {
					final String ext = sName.substring(index + 1);
					if (PopupPrintingTemplateListCellRenderer.icons.containsKey(ext)) {
						final Icon image = (Icon) PopupPrintingTemplateListCellRenderer.icons.get(ext);
						this.setIcon(image);
					}
				}
				if (h.containsKey(Form.TEMPLATE_PRIVATE)) {
					this.privatetemplate.setEnabled(true);
					final Object o = h.get(Form.TEMPLATE_PRIVATE);
					if (o instanceof Boolean) {
						final Boolean b = (Boolean) o;
						this.privatetemplate.setSelected(b.booleanValue());
					} else if (o instanceof Integer) {
						final Integer i = (Integer) o;
						this.privatetemplate.setSelected(i.intValue() == 0 ? false : true);
					}
				}
			}

			this.setSelected(true);
			if (h.containsKey(this.keySelected)) {
				final Object o = h.get(this.keySelected);
				if (o instanceof Boolean) {
					final Boolean b = (Boolean) o;
					this.setSelected(b.booleanValue());
				} else if (o instanceof Integer) {
					final Integer i = (Integer) o;
					this.setSelected(i.intValue() == 0 ? false : true);
				}
			} else {
				this.setSelected(false);
			}
		}

		return this.panelCellRenderer;
	}

}
