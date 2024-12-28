package com.ontimize.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;

/**
 * Class that implements a buffer message window
 */

public class BufferedMessageDialog extends JDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(BufferedMessageDialog.class);

	protected JList messageList = new JList();

	protected JScrollPane scroll = null;

	protected ResourceBundle resources = null;

	protected ImageIcon infoIcon = null;

	protected ImageIcon errorIcon = null;

	protected DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	protected JLabel labelTitle = new JLabel("MessageList");

	protected String titleLabelKey = "MessageList";

	protected JButton buttonSave = new JButton();

	protected String saveKey = "SaveToFile";

	class MyMessage {

		String message = null;

		long hour = 0;

		boolean error = false;

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		public MyMessage(final String message) {
			this.message = message;
			this.hour = System.currentTimeMillis();
		}

		public MyMessage(final String message, final boolean error) {
			this.message = message;
			this.hour = System.currentTimeMillis();
			this.error = error;
		}

		public String getText() {
			return this.message;
		}

		public long getHour() {
			return this.hour;
		}

		public boolean isError() {
			return this.error;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			if (this.error) {
				sb.append("ERROR: ");
			} else {
				sb.append("INFO: ");
			}
			sb.append(this.message);
			sb.append(this.df.format(new Date(this.getHour())));
			return sb.toString();
		}

	}

	class Render extends JPanel implements ListCellRenderer {

		JLabel text = new JLabel();

		JLabel hour = new JLabel();

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		public Render() {
			this.setLayout(new GridBagLayout());
			this.add(this.text, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
			this.add(this.hour, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			if (value == null) {
				return null;
			} else {
				this.setBackground(isSelected ? Color.blue : Color.white);
				this.text.setForeground(isSelected ? Color.white : Color.black);
				this.hour.setForeground(isSelected ? Color.white : Color.blue);
				if (value instanceof MyMessage) {
					if (((MyMessage) value).isError()) {
						this.text.setIcon(BufferedMessageDialog.this.errorIcon);
						this.text.setForeground(Color.red);
					} else {
						this.text.setIcon(BufferedMessageDialog.this.infoIcon);
					}
					this.text.setText(((MyMessage) value).getText());
					this.hour.setText(this.df.format(new Date(((MyMessage) value).getHour())));
				} else {
					this.text.setText(value.toString());
				}
				return this;
			}
		}

	}

	private BufferedMessageDialog(final Frame parent, final ResourceBundle res) {
		super(parent, true);
		this.resources = res;
		this.messageList.setModel(new DefaultListModel());
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.scroll = new JScrollPane(this.messageList);
		this.getContentPane().add(this.scroll);
		this.scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.buttonSave.setMargin(new Insets(1, 1, 1, 1));

		this.labelTitle.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.labelTitle.setPreferredSize(new Dimension(this.labelTitle.getPreferredSize().width, 20));
		this.scroll.getVerticalScrollBar()
		.setPreferredSize(new Dimension(20, this.scroll.getVerticalScrollBar().getPreferredSize().height));
		this.scroll.setColumnHeaderView(this.labelTitle);
		this.scroll.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, this.buttonSave);
		this.installIcons();
		this.installSaveListener();
		this.messageList.setCellRenderer(new Render());
		this.setResourceBundle(res);
		this.pack();
		ApplicationManager.center(this);
	}

	private BufferedMessageDialog(final Dialog parent, final ResourceBundle res) {
		super(parent, true);
		this.resources = res;
		this.messageList.setModel(new DefaultListModel());
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.scroll = new JScrollPane(this.messageList);
		this.getContentPane().add(this.scroll);
		this.scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.buttonSave.setMargin(new Insets(1, 1, 1, 1));

		this.labelTitle.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.labelTitle.setPreferredSize(new Dimension(this.labelTitle.getPreferredSize().width, 20));
		this.scroll.getVerticalScrollBar()
		.setPreferredSize(new Dimension(20, this.scroll.getVerticalScrollBar().getPreferredSize().height));
		this.scroll.setColumnHeaderView(this.labelTitle);
		this.scroll.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, this.buttonSave);
		this.installIcons();
		this.installSaveListener();
		this.messageList.setCellRenderer(new Render());
		this.setResourceBundle(res);
		this.pack();
		ApplicationManager.center(this);
	}

	protected void installSaveListener() {
		this.buttonSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				BufferedMessageDialog.this.save();
			}
		});
	}

	protected void save() {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		final int option = fc.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			this.save(fc.getSelectedFile());
		}
	}

	protected void save(final File f) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < this.messageList.getModel().getSize(); i++) {
				final Object mens = this.messageList.getModel().getElementAt(i);
				if (mens != null) {
					bw.write(mens.toString());
					bw.newLine();
				}
			}
			bw.flush();
			bw.close();

			MessageDialog.showMessage(this, "BufferedMessageDialog.file_save_ok", null, JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.OK_OPTION, this.resources);
		} catch (final Exception e) {
			BufferedMessageDialog.logger.trace(null, e);
			MessageDialog.showMessage(this, "BufferedMessageDialog.error_saving_file", null, JOptionPane.ERROR_MESSAGE,
					JOptionPane.OK_OPTION, this.resources);
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (final Exception e) {
				BufferedMessageDialog.logger.trace(null, e);
			}
		}
	}

	protected void installIcons() {
		final ImageIcon saveDiscIcon = ImageManager.getIcon(ImageManager.SAVE_DISC);
		if (saveDiscIcon != null) {
			this.buttonSave.setIcon(saveDiscIcon);
		}

		final ImageIcon info16Icon = ImageManager.getIcon(ImageManager.INFO_16);
		if (info16Icon != null) {
			this.infoIcon = info16Icon;
		}

		final ImageIcon error = ImageManager.getIcon(ImageManager.ERROR);
		if (error != null) {
			this.errorIcon = error;
		}
	}

	/**
	 * Creates a BufferedMessageDialog. The default action when close the window (x) is HIDE_ON_CLOSE
	 */
	public static BufferedMessageDialog createBufferedMessageDialog(final Frame parent, final ResourceBundle res) {
		return new BufferedMessageDialog(parent, res);
	}

	/**
	 * Creates a BufferedMessageDialog. The default action when close the window (x) is HIDE_ON_CLOSE
	 */
	public static BufferedMessageDialog createBufferedMessageDialog(final JDialog parent, final ResourceBundle res) {
		return new BufferedMessageDialog(parent, res);
	}

	@Override
	public void setTitle(final String title) {
		if (this.resources != null) {
			String sTitle = null;
			try {
				sTitle = this.resources.getString(title);
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					BufferedMessageDialog.logger.debug(null, e);
				} else {
					BufferedMessageDialog.logger.trace(null, e);
				}
			}
			super.setTitle(sTitle);
		} else {
			super.setTitle(title);
		}
	}

	public void addMessage(final String message) {
		if (this.resources != null) {
			String sMessage = null;
			try {
				sMessage = this.resources.getString(message);
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					BufferedMessageDialog.logger.debug(null, e);
				} else {
					BufferedMessageDialog.logger.trace(null, e);
				}
			}
			((DefaultListModel) this.messageList.getModel()).add(0, new MyMessage(sMessage));
			// this.messageList.setSelectedIndex(0);
		} else {
			((DefaultListModel) this.messageList.getModel()).add(0, new MyMessage(message));
			// this.messageList.setSelectedIndex(0);
		}
	}

	public void addMessages(final Vector messages) {
		if (messages != null) {
			for (int i = 0; i < messages.size(); i++) {
				final Object m = messages.get(i);
				if (m != null) {
					this.addMessage(m.toString());
				}
			}
		}
	}

	public void addErrorMessage(final String message) {
		if (this.resources != null) {
			String sMessage = null;
			try {
				sMessage = this.resources.getString(message);
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					BufferedMessageDialog.logger.debug(null, e);
				} else {
					BufferedMessageDialog.logger.trace(null, e);
				}
			}
			((DefaultListModel) this.messageList.getModel()).add(0, new MyMessage(sMessage, true));
			// this.messageList.setSelectedIndex(0);
		} else {
			((DefaultListModel) this.messageList.getModel()).add(0, new MyMessage(message, true));
			// this.messageList.setSelectedIndex(0);
		}
	}

	public void clear() {
		((DefaultListModel) this.messageList.getModel()).removeAllElements();
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector();
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.resources = res;
		try {
			if (res != null) {
				this.labelTitle.setText(res.getString(this.titleLabelKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				BufferedMessageDialog.logger.debug(e.getMessage(), e);
			} else {
				BufferedMessageDialog.logger.trace(null, e);
			}
		}

		try {
			if (res != null) {
				this.buttonSave.setToolTipText(res.getString(this.saveKey));
			} else {
				this.buttonSave.setToolTipText(this.saveKey);
			}
		} catch (final Exception e) {
			this.buttonSave.setToolTipText(this.saveKey);
			if (ApplicationManager.DEBUG) {
				BufferedMessageDialog.logger.debug(e.getMessage(), e);
			} else {
				BufferedMessageDialog.logger.trace(null, e);
			}
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

}
