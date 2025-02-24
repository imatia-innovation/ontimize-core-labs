package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.FileManagementEntity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.util.gd.GDUtilities;

public class SearchWindow extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(SearchWindow.class);

	public final String L_CONTAIN = "contain";

	public final String L_PRIVATE = "private";

	public final String L_SEARCH = "search";

	public final String L_CLOSE = "close";

	public final String L_ONLYPRIVATE = "only_private";

	public final String L_GOOGLE_DESKTOP = "google_desktop";

	protected JLabel lContain = null;

	protected JTextField containText = null;

	protected JButton searchButton = null;

	protected JButton closeButton = null;

	protected EntityReferenceLocator locator = null;

	protected JCheckBox onlyPrivate = null;

	protected JCheckBox googleDesktop = null;

	protected ResourceBundle bundle = null;

	protected SearchResultWindow wResult = null;

	public SearchWindow(final Dialog owner, final EntityReferenceLocator referenceLocator) {
		super(owner, true);
		this.init(referenceLocator);
	}

	public SearchWindow(final Frame owner, final EntityReferenceLocator referenceLocator) {
		super(owner, true);
		this.init(referenceLocator);
	}

	protected void init(final EntityReferenceLocator referenceLocator) {
		this.locator = referenceLocator;

		this.lContain = new JLabel(this.L_CONTAIN);
		this.containText = new JTextField();
		this.containText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(final KeyEvent event) {
				if (KeyEvent.VK_ENTER == event.getKeyCode()) {
					SearchWindow.this.searchButton.doClick();
				}
			}
		});
		ImageIcon icon = ImageManager.getIcon(ImageManager.SEARCH);

		if (icon != null) {
			this.searchButton = new JButton(this.L_SEARCH, icon);
		} else {
			this.searchButton = new JButton(this.L_SEARCH);
		}
		this.searchButton.addActionListener(new SearchActionListener());

		icon = ImageManager.getIcon(ImageManager.ERROR);
		if (icon != null) {
			this.closeButton = new JButton(this.L_CLOSE, icon);
		} else {
			this.closeButton = new JButton(this.L_CLOSE);
		}
		this.closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if ((SearchWindow.this.wResult != null) && SearchWindow.this.wResult.isVisible()) {
					SearchWindow.this.wResult.setVisible(false);
					SearchWindow.this.closeButton.setEnabled(false);
				}
			}
		});

		this.onlyPrivate = new JCheckBox(this.L_ONLYPRIVATE);
		this.googleDesktop = new JCheckBox(this.L_GOOGLE_DESKTOP);
		this.googleDesktop.setVisible(false);

		this.getContentPane().setLayout(new GridBagLayout());
		final JPanel line1 = new JPanel(new GridBagLayout());
		line1.add(this.lContain, new GridBagConstraints(0, 0, 1, 1, 0.2, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 30, 0));
		line1.add(this.containText,
				new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 0.8, 0, GridBagConstraints.NORTHEAST,
						GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 100, 0));
		this.getContentPane()
		.add(line1,
				new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.NORTHEAST,
						GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		final JPanel line2 = new JPanel(new GridBagLayout());
		line2.add(this.onlyPrivate, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		line2.add(this.googleDesktop, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		this.getContentPane()
		.add(line2,
				new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

		final JPanel jpButtonsLine = new JPanel(new GridBagLayout());
		jpButtonsLine.add(this.searchButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		jpButtonsLine.add(this.closeButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		this.getContentPane()
		.add(jpButtonsLine,
				new GridBagConstraints(0, 3, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.SOUTHEAST,
						GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
	}

	protected class SearchActionListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if ((SearchWindow.this.wResult != null) && SearchWindow.this.wResult.isVisible()) {
				SearchWindow.this.wResult.setVisible(false);
			}
			final String sText = SearchWindow.this.containText.getText();
			final Map kv = new Hashtable();
			final java.util.List av = new Vector();
			av.add(Form.ORIGINAL_FILE_NAME);
			av.add(Form.ATTACHMENT_ID);
			av.add(Form.USER);
			av.add(Form.PRIVATE_ATTACHMENT);
			av.add(Form.ATTACHMENT_DATE);

			if ((sText != null) && (sText.length() > 0)) {
				kv.put(Form.ORIGINAL_FILE_NAME, "*" + sText + "*");
			}

			if (SearchWindow.this.onlyPrivate.isSelected()) {
				kv.put(Form.PRIVATE_ATTACHMENT, new Integer(1));
				kv.put(Form.USER, ((ClientReferenceLocator) SearchWindow.this.locator).getUser());
			}

			final Entity eAttachment = SearchWindow.this.getAttachmentEntity();
			if (eAttachment != null) {
				try {
					final EntityResult res = eAttachment.query(kv, av, SearchWindow.this.locator.getSessionId());
					if (res.calculateRecordNumber() == 0) {
						JOptionPane.showMessageDialog((Component) e.getSource(), "No results found");
						return;
					}
					SearchWindow.this.wResult = SearchResultWindow.showResult(SearchWindow.this, res,
							SearchWindow.this.locator);
					SearchWindow.this.closeButton.setEnabled(true);
				} catch (final Exception ex) {
					SearchWindow.logger.error(null, ex);
				}
			}

		}

	}

	protected Entity getAttachmentEntity() {
		if (this.locator instanceof UtilReferenceLocator) {
			try {
				final Entity eAttachment = ((UtilReferenceLocator) this.locator)
						.getAttachmentEntity(this.locator.getSessionId());
				return eAttachment;
			} catch (final Exception e) {
				SearchWindow.logger.error(null, e);
			}
		}
		return null;
	}

	protected List getResultAttachment(final String contain) {
		return null;
	}

	protected void initWindowShow() {
		this.closeButton.setEnabled(false);
	}

	protected static SearchWindow swindow = null;

	public static void showSearchWindow(final Component c, final EntityReferenceLocator locator, final ResourceBundle bundle) {
		if (SearchWindow.swindow == null) {

			final Window w = SwingUtilities.getWindowAncestor(c);
			if (c instanceof Frame) {
				SearchWindow.swindow = new SearchWindow((Frame) c, locator);
			} else if (w instanceof Frame) {
				SearchWindow.swindow = new SearchWindow((Frame) w, locator);
			} else if (w instanceof Dialog) {
				SearchWindow.swindow = new SearchWindow((Dialog) w, locator);
			}
			SearchWindow.swindow.setTitle("Search attachments");
			SearchWindow.swindow.setResourceBundle(bundle);
			SearchWindow.swindow.pack();
			ApplicationManager.center(SearchWindow.swindow);
		}
		SearchWindow.swindow.initWindowShow();
		SearchWindow.swindow.setVisible(true);
	}

	@Override
	public java.util.List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.bundle = resources;
		this.lContain.setText(ApplicationManager.getTranslation(this.L_CONTAIN, this.bundle));
		this.searchButton.setText(ApplicationManager.getTranslation(this.L_SEARCH, this.bundle));
		this.onlyPrivate.setText(ApplicationManager.getTranslation(this.L_ONLYPRIVATE, this.bundle));
		this.googleDesktop.setText(ApplicationManager.getTranslation(this.L_GOOGLE_DESKTOP, this.bundle));
		this.closeButton.setText(ApplicationManager.getTranslation(this.L_CLOSE, this.bundle));
	}

	protected static class MouseDownLoadHandler extends MouseAdapter {

		private JFileChooser fileChooser = null;

		private EntityReferenceLocator locator = null;

		public MouseDownLoadHandler(final EntityReferenceLocator referenceLocator) {
			this.locator = referenceLocator;
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof JList) {
				final JList list = (JList) o;
				if ((e.getClickCount() == 2) && (list.getSelectedIndex() != -1)) {
					final int index = list.getSelectedIndex();
					final Object oI = list.getModel().getElementAt(index);
					if (oI instanceof com.ontimize.gui.SearchWindow.SearchResultWindow.ResultSearch) {
						try {
							final com.ontimize.gui.SearchWindow.SearchResultWindow.ResultSearch result = (com.ontimize.gui.SearchWindow.SearchResultWindow.ResultSearch) oI;
							if (this.fileChooser == null) {
								this.fileChooser = new JFileChooser();
								this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							}
							File fSelectedFile = null;
							final Object oSuggestedName = result.getName();
							if (oSuggestedName != null) {
								this.fileChooser.setSelectedFile(
										new File(this.fileChooser.getCurrentDirectory(), (String) oSuggestedName));
							}

							final int iOption = this.fileChooser.showSaveDialog((Component) e.getSource());
							if (iOption == JFileChooser.CANCEL_OPTION) {
								return;
							}
							fSelectedFile = this.fileChooser.getSelectedFile();
							if (fSelectedFile.exists()) {
								if (JOptionPane.showConfirmDialog((Component) e.getSource(),
										"attachment.file_exists_proceed_anyway") != JOptionPane.YES_OPTION) {
									return;
								}
							}
							final Map kv = new Hashtable();
							kv.put(Form.ATTACHMENT_ID, result.getId());

							final com.ontimize.gui.actions.DownloadThread eop = new com.ontimize.gui.actions.DownloadThread(
									null, fSelectedFile, kv,
									(FileManagementEntity) ((UtilReferenceLocator) this.locator)
									.getAttachmentEntity(this.locator.getSessionId()),
									this.locator);
							final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
							if (w instanceof Dialog) {
								ApplicationManager.proccessOperation((Dialog) w, eop, 0);
							} else {
								ApplicationManager.proccessOperation((Frame) w, eop, 0);
							}
							if (eop.getResult() != null) {
								JOptionPane.showMessageDialog((Component) e.getSource(), eop.getResult().toString());
							} else {
								JOptionPane.showMessageDialog((Component) e.getSource(),
										"M_FICHERO_DESCARGADO_CORRECTAMENTE");
							}
						} catch (final Exception ex) {
							SearchWindow.logger.error(null, ex);
						}
					}

					SearchWindow.logger.debug("Download:  " + oI.toString());
				}
			}
		}

	}

	protected static class SearchResultWindow extends JDialog {

		protected JListResult resultList = null;

		protected JScrollPane scroll = null;

		protected static SearchResultWindow sWindow = null;

		public SearchResultWindow(final Dialog d, final EntityReferenceLocator referenceLocator) {
			super(d);
			this.resultList = new JListResult();
			this.resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.resultList.addMouseListener(new MouseDownLoadHandler(referenceLocator));
			this.scroll = new JScrollPane(this.resultList);

			final Insets in = d.getInsets();
			final MatteBorder border = new MatteBorder(in.left, in.left, in.bottom, in.right, Color.blue);

			this.scroll.setBorder(border);
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(this.scroll);
			this.setUndecorated(true);
			this.pack();
		}

		public void setEntityResult(final EntityResult res) {
			this.resultList.setListData(res);
			// scroll.revalidate();
			this.pack();
		}

		protected static class JListResult extends JList {

			public JListResult() {
				this.setCellRenderer(new ResultRenderer());

			}

			public void setListData(final EntityResult res) {
				final Vector v = new Vector();
				for (int i = 0; i < res.calculateRecordNumber(); i++) {
					Integer id = null;
					String name = null;
					String description = null;
					Date date = null;
					int privateA = 0;
					String user = null;
					final Map h = res.getRecordValues(i);

					if (h.containsKey(Form.ATTACHMENT_ID)) {
						id = (Integer) h.get(Form.ATTACHMENT_ID);
					}
					if (h.containsKey(Form.ORIGINAL_FILE_NAME)) {
						name = h.get(Form.ORIGINAL_FILE_NAME).toString();
					}

					if (h.containsKey(Form.DESCRIPTION_FILE)) {
						description = h.get(Form.DESCRIPTION_FILE).toString();
					}

					if (h.containsKey(Form.ATTACHMENT_DATE)) {
						date = (Date) h.get(Form.ATTACHMENT_DATE);
					}

					if (h.containsKey(Form.PRIVATE_ATTACHMENT)) {
						final Object o = h.get(Form.PRIVATE_ATTACHMENT);
						if (o instanceof Integer) {
							privateA = ((Integer) o).intValue();
						}
					}

					if (h.containsKey(Form.USER)) {
						user = h.get(Form.USER).toString();
					}
					v.add(new ResultSearch(id, name, description, date, privateA, user));
				}
				this.setListData(v);
			}

			@Override
			public int getVisibleRowCount() {
				final int countVisible = super.getVisibleRowCount();
				final int size = this.getModel().getSize();
				if (size < countVisible) {
					return size;
				}
				return countVisible;
			}

		}

		protected static class ResultSearch {

			private Integer id = null;

			private String name = null;

			private String description = null;

			private Date date = null;

			private int privateA = 0;

			private String user = null;

			public ResultSearch(final Integer id, final String name, final String description, final Date date, final int privateA, final String user) {
				this.id = id;
				this.name = name;
				this.description = description;
				this.date = date;
				this.privateA = privateA;
				this.user = user;
			}

			public Integer getId() {
				return this.id;
			}

			public String getName() {
				return this.name;

			}

			public String getDescription() {
				return this.description;
			}

			public Date getDate() {
				return this.date;
			}

			public boolean isPrivate() {
				return this.privateA > 0;
			}

			public String getUser() {
				return this.user;
			}

			@Override
			public String toString() {
				return this.name + " " + this.date.toString();
			}

		}

		private static class ResultRenderer extends DefaultListCellRenderer {

			static SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();

			public ResultRenderer() {
				ResultRenderer.df.applyPattern("HH:mm dd/MM/yyyy");
			}

			@Override
			public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected,
					final boolean cellHasFocus) {
				if (value instanceof GDUtilities.Result) {
					final StringBuilder sb = new StringBuilder(
							"<HTML><BODY  style=\"font-family: arial,sans-serif;margin-top:6;margin-left:6;margin-right:6;margin-bottom:6\">");
					sb.append("<U><font color=#3399CC size=4>");
					sb.append(((GDUtilities.Result) value).getTitle());
					sb.append("</font></U>&nbsp;&nbsp;&nbsp;");

					if (((GDUtilities.Result) value).getSnippet() != null) {
						sb.append("<BR><I><font size=3>");
						sb.append(((GDUtilities.Result) value).getSnippet());
						sb.append("</font></I>");
					}
					sb.append("<BR><font size=3>");
					sb.append(ResultRenderer.df.format(new Date(((GDUtilities.Result) value).getTime())));
					sb.append("</FONT></BODY></HTML>");
					value = sb.toString();
				} else if (value instanceof ResultSearch) {
					final StringBuilder sb = new StringBuilder(
							"<HTML><BODY  style=\"font-family: arial,sans-serif;margin-top:6;margin-left:6;margin-right:6;margin-bottom:6\">");
					sb.append("<U><font color=#3399CC size=4>");
					sb.append(((ResultSearch) value).getName());
					sb.append("</font></U>&nbsp;&nbsp;&nbsp;");

					if (((ResultSearch) value).getDescription() != null) {
						sb.append("<BR><I><font size=3>");
						sb.append(((ResultSearch) value).getDescription());
						sb.append("</font></I>");
					}
					sb.append("<BR><font size=3>");
					sb.append(ResultRenderer.df.format(((ResultSearch) value).getDate()));
					sb.append("</FONT></BODY></HTML>");
					value = sb.toString();
				}

				final Component c = super.getListCellRendererComponent(list, value, index, false, cellHasFocus);
				if (isSelected) {
					((JComponent) c)
					.setBorder(new CompoundBorder(new EmptyBorder(3, 3, 3, 3), new LineBorder(Color.lightGray)));
				} else {
					((JComponent) c).setBorder(new EmptyBorder(3, 3, 3, 3));
				}
				return c;
			}

		}

		public static SearchResultWindow showResult(final Dialog d, final EntityResult res, final EntityReferenceLocator b) {
			if (SearchResultWindow.sWindow == null) {
				SearchResultWindow.sWindow = new SearchResultWindow(d, b);
			}
			d.addComponentListener(new ComponentListener() {

				@Override
				public void componentHidden(final ComponentEvent e) {
				}

				@Override
				public void componentMoved(final ComponentEvent e) {
					final Component comp = (Component) e.getSource();
					final Point p = new Point(0, comp.getHeight());
					SwingUtilities.convertPointToScreen(p, comp);
					SearchResultWindow.sWindow.setLocation(p);
				}

				@Override
				public void componentResized(final ComponentEvent e) {
					final Component comp = (Component) e.getSource();
					final Point p = new Point(0, comp.getHeight());
					SwingUtilities.convertPointToScreen(p, comp);
					SearchResultWindow.sWindow.setLocation(p);
				}

				@Override
				public void componentShown(final ComponentEvent e) {
				}
			});
			SearchResultWindow.sWindow.setEntityResult(res);
			final Point p = new Point(0, d.getHeight());
			SwingUtilities.convertPointToScreen(p, d);
			SearchResultWindow.sWindow.setLocation(p);
			SearchResultWindow.sWindow.setVisible(true);
			return SearchResultWindow.sWindow;
		}

	}

}
