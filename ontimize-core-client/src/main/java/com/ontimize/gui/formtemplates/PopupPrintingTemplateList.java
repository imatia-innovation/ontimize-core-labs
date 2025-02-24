package com.ontimize.gui.formtemplates;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.gui.DataFile;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.CollectionTools;
import com.ontimize.util.FileUtils;
import com.ontimize.util.swing.popuplist.PopupItem;
import com.ontimize.util.swing.popuplist.PopupList;
import com.ontimize.util.swing.popuplist.PopupListModel;
import com.ontimize.util.templates.TemplateGeneratorFactory;
import com.ontimize.util.templates.TemplateUtils;

public class PopupPrintingTemplateList extends PopupList implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(PopupPrintingTemplateList.class);

	public static final String M_ERROR_CHANGE_PRIVATE_TEMPLATE = "popupprintingtemplatelist.errorchangingprivatetemplate";

	public static final String M_CHANGE_USER_TEMPLATE = "popupprintingtemplatelist.changeusertemplate";

	private JFileChooser fileChooser = null;

	protected MouseHandlerPopupList mouseHandler = null;

	protected PopupItem helpItem;

	protected PopupItem wordItem;

	protected PopupItem docxItem;

	protected PopupItem openofficeItem;

	protected PopupItem addTemplateItem;

	protected JDialog helpFieldsDialog;

	protected class MouseHandlerPopupList extends MouseAdapter implements MouseMotionListener {

		@Override
		public void mouseClicked(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof PopupList) {
				final PopupList list = (PopupList) o;
				final int sel = list.locationToIndex(e.getPoint());
				final int x = e.getX();
				if ((x > 1) && (x < 18)) {
					// Save the default template
					PopupPrintingTemplateList.this.chooseDefaultTemplate(list, sel);
					PopupPrintingTemplateList.this.setVisible(false);

				} else if ((x > 18) && (x <= 35)) {
					// Private template
					if (PopupPrintingTemplateList.this.enabledPrivateTemplates) {
						PopupPrintingTemplateList.this.configurePrivateAction(list, sel);
					} else {
						PopupPrintingTemplateList.this.saveTemplate(list, sel, e);
					}
					PopupPrintingTemplateList.this.setVisible(false);

				} else if ((x > 35) && (x <= 52)) {
					// Save the template
					if (PopupPrintingTemplateList.this.enabledPrivateTemplates) {
						PopupPrintingTemplateList.this.saveTemplate(list, sel, e);
					} else {
						PopupPrintingTemplateList.this.deleteTemplate(list, sel);
					}
					PopupPrintingTemplateList.this.setVisible(false);

				} else if ((x > 52) && (x <= 69)) {
					// Remove the template
					if (PopupPrintingTemplateList.this.enabledPrivateTemplates) {
						PopupPrintingTemplateList.this.deleteTemplate(list, sel);
					} else {
						PopupPrintingTemplateList.this.openTemplate(list, sel);
					}
					PopupPrintingTemplateList.this.setVisible(false);

				} else if (x > 69) {
					// Open template
					PopupPrintingTemplateList.this.openTemplate(list, sel);
					PopupPrintingTemplateList.this.setVisible(false);
				}
			}

		}

		@Override
		public void mouseDragged(final MouseEvent e) {

		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof PopupList) {
				final PopupList list = (PopupList) o;
				final int sel = list.locationToIndex(e.getPoint());
				final int x = e.getX();
				if ((x > 1) && (x < 18)) {
					((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
					.setPointed(PopupPrintingTemplateListCellRenderer.CHECK, sel);
				} else if ((x >= 18) && (x < 35)) {
					if (PopupPrintingTemplateList.this.enabledPrivateTemplates) {
						((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
						.setPointed(PopupPrintingTemplateListCellRenderer.PRIVATE, sel);
					} else {
						((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
						.setPointed(PopupPrintingTemplateListCellRenderer.SAVE, sel);
					}
				} else if ((x >= 35) && (x <= 52)) {
					if (PopupPrintingTemplateList.this.enabledPrivateTemplates) {
						((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
						.setPointed(PopupPrintingTemplateListCellRenderer.SAVE, sel);
					} else {
						((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
						.setPointed(PopupPrintingTemplateListCellRenderer.DELETE, sel);
					}
				} else if ((x > 52) && (x <= 69)) {
					if (PopupPrintingTemplateList.this.enabledPrivateTemplates) {
						((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
						.setPointed(PopupPrintingTemplateListCellRenderer.DELETE, sel);
					} else {
						((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
						.setPointed(PopupPrintingTemplateListCellRenderer.FILE, sel);
					}
				} else if (x > 69) {
					((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
					.setPointed(PopupPrintingTemplateListCellRenderer.FILE, sel);
				} else {
					((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
					.setPointed(PopupPrintingTemplateListCellRenderer.NONE, sel);
				}
			}

		}

		@Override
		public void mouseExited(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof PopupList) {
				final PopupList list = (PopupList) o;
				((PopupPrintingTemplateListCellRenderer) list.getCellRenderer())
				.setPointed(PopupPrintingTemplateListCellRenderer.NONE, -1);
			}
		}

	}

	public PopupPrintingTemplateList(final Form f, final boolean bPrivateTemplates) {
		super(f.getResourceBundle(), f, bPrivateTemplates);
		final Font font = f.getFont();
		final JPanel templateFieldsPanel = new JPanel(new GridBagLayout());
		final JTable tableMarkers = new JTable() {

			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		};
		tableMarkers.setBackground(templateFieldsPanel.getBackground());
		tableMarkers.setFont(font.deriveFont(Font.BOLD));
		tableMarkers.setRowHeight(font.getSize() + 5);
		tableMarkers.setShowGrid(false);
		final JLabel templatelabel = new JLabel();
		final Window w = SwingUtilities.getWindowAncestor(this.form);
		if (w instanceof Frame) {
			this.helpFieldsDialog = new JDialog((Frame) w);
		} else if (w instanceof Dialog) {
			this.helpFieldsDialog = new JDialog((Dialog) w);
		}

		final TableModel model = new DefaultTableModel(this.calculateMarkerNumber(this.form.getDataComponents()), 2);
		tableMarkers.setModel(model);
		tableMarkers.setCellSelectionEnabled(true);
		templateFieldsPanel.add(templatelabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, 11,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		templateFieldsPanel.add(tableMarkers, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1.0, 11,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		final JScrollPane templateFieldsScroll = new JScrollPane(templateFieldsPanel);
		this.helpFieldsDialog.add(templateFieldsScroll);
		this.helpFieldsDialog.setSize(480, 640);
		ApplicationManager.center(this.helpFieldsDialog);
		this.helpItem = new PopupItem(Form.messageHelpTemplate, ImageManager.getIcon(ImageManager.HELP), this.bundle);
		this.addPopupItem(this.helpItem);
		this.helpItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Iterator formComponents = PopupPrintingTemplateList.this.form.getDataComponents().iterator();
				final String labeltext = new String(ApplicationManager.getTranslation(Form.messageNameFieldTemplate,
						PopupPrintingTemplateList.this.bundle));
				templatelabel.setFont(font.deriveFont(Font.BOLD));
				templatelabel.setText(labeltext);
				int row = 0;
				while (formComponents.hasNext()) {
					final Object c = formComponents.next();
					final Object o = ((DataComponent) c).getAttribute();

					if ((c == null) || ((c != null) && (c instanceof Table))) {
						continue;
					}
					tableMarkers.setValueAt(
							ApplicationManager.getTranslation(o.toString(), PopupPrintingTemplateList.this.bundle), row,
							0);
					tableMarkers.setValueAt(o.toString(), row++, 1);
				}

				PopupPrintingTemplateList.this.helpFieldsDialog.setVisible(true);
			}
		});

		if (Form.WORD_TEMPLATES == 1) {
			this.wordItem = new PopupItem(Form.messageCreateWordTemplate, ImageManager.getIcon(ImageManager.DOC),
					this.bundle);
			this.addPopupItem(this.wordItem);
			this.wordItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent event) {
					try {
						TemplateUtils.createTemplate(PopupPrintingTemplateList.this.form,
								TemplateUtils.TEMPLATE_TYPE_DOC);
					} catch (final Exception e) {
						PopupPrintingTemplateList.logger.error(null, e);
					}
				}
			});
		}

		if (TemplateGeneratorFactory.hasTemplateGenerator(TemplateGeneratorFactory.OPEN_OFFICE)) {
			this.openofficeItem = new PopupItem(Form.messageCreateOpenOfficeTemplate,
					ImageManager.getIcon(ImageManager.ODT), this.bundle);
			this.addPopupItem(this.openofficeItem);
			this.openofficeItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent event) {
					try {
						TemplateUtils.createTemplate(PopupPrintingTemplateList.this.form,
								TemplateUtils.TEMPLATE_TYPE_ODT);
					} catch (final Exception ex) {
						PopupPrintingTemplateList.logger.error(null, ex);
					}

				}
			});
		}

		if (Form.DOCX_TEMPLATES == 1) {
			this.docxItem = new PopupItem(Form.messageCreateDocxTemplate, ImageManager.getIcon(ImageManager.DOCX),
					this.bundle);
			this.addPopupItem(this.docxItem);
			this.docxItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						TemplateUtils.createTemplate(PopupPrintingTemplateList.this.form,
								TemplateUtils.TEMPLATE_TYPE_DOCX);
					} catch (final Exception ex) {
						PopupPrintingTemplateList.logger.error(null, ex);
					}

				}
			});
		}

		this.addTemplateItem = new PopupItem(Form.messageAddTemplate, ImageManager.getIcon(ImageManager.ADD),
				this.bundle);
		this.addPopupItem(this.addTemplateItem);
		this.addTemplateItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final EntityReferenceLocator locator = PopupPrintingTemplateList.this.form.getFormManager()
						.getReferenceLocator();
				try {
					final Entity templateEntity = ((UtilReferenceLocator) locator)
							.getPrintingTemplateEntity(locator.getSessionId());
					if (PopupPrintingTemplateList.this.fileChooser == null) {
						PopupPrintingTemplateList.this.createFileChooser();
					}
					PopupPrintingTemplateList.this.setVisible(false);

					final int option = PopupPrintingTemplateList.this.fileChooser.showOpenDialog((Component) e.getSource());
					if (option != JFileChooser.APPROVE_OPTION) {
						return;
					}

					final File selectedFile = PopupPrintingTemplateList.this.fileChooser.getSelectedFile();
					if (selectedFile.length() >= Integer.MAX_VALUE) {
						PopupPrintingTemplateList.this.form.message("form.file_too_big", Form.ERROR_MESSAGE,
								"Max: " + Integer.MAX_VALUE);
						PopupPrintingTemplateList.this.setVisible(false);
						return;
					}

					final Map kv = new Hashtable();

					if ((selectedFile != null) && !selectedFile.isDirectory()) {
						final DataFile dataFile = new DataFile(selectedFile);
						kv.put(Form.TEMPLATE_WAREHOUSE, dataFile.getBytesBlock());
						kv.put(Form.TEMPLATE_NAME, selectedFile.getName());
						kv.put(Form.TEMPLATE_FORM, PopupPrintingTemplateList.this.form.getArchiveName());
						kv.put(Form.TEMPLATE_PRIVATE, new Integer(0));
						final EntityResult res = templateEntity.insert(kv, locator.getSessionId());
						if (res.getCode() == EntityResult.OPERATION_WRONG) {
							PopupPrintingTemplateList.this.form.message(
									ApplicationManager.getTranslation(Form.messageErrorInsert,
											PopupPrintingTemplateList.this.bundle)
									+ " "
									+ ApplicationManager.getTranslation(res.getMessage(),
											PopupPrintingTemplateList.this.bundle),
									Form.ERROR_MESSAGE);
							return;
						} else {
							PopupPrintingTemplateList.this.form.message(Form.messageInsertionSucessful,
									Form.INFORMATION_MESSAGE);
						}
					}
				} catch (final Exception ex) {
					PopupPrintingTemplateList.logger.error(null, ex);
				}
			}
		});

	}

	protected int calculateMarkerNumber(final List vDataComponents) {
		final Iterator formComponents = vDataComponents.iterator();
		int iMarkerNumber = 0;
		while (formComponents.hasNext()) {
			final Object c = formComponents.next();

			if ((c == null) || ((c != null) && (c instanceof Table))) {
				continue;
			} else {
				iMarkerNumber++;
			}
		}
		return iMarkerNumber;
	}

	protected void createFileChooser() {
		this.fileChooser = new JFileChooser();
		this.fileChooser.setMultiSelectionEnabled(false);
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.fileChooser.setFileFilter(new FileFilter() {

			protected String getExtension(final File f) {
				final String name = f.getName();
				final int index = name.lastIndexOf(".");
				if (index >= 0) {
					return name.substring(index);
				}
				return "";
			}

			@Override
			public boolean accept(final File f) {
				if (f.isDirectory()) {
					return true;
				}
				final String extension = this.getExtension(f);
				if (extension != null) {
					if (extension.equals(".pdf") || extension.equals(".doc") || extension.equals(".docx")
							|| extension.equals(".odt")) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "*.pdf,*.doc,*.docx,*.odt";
			}
		});
	}

	@Override
	protected void installListModel() {
		super.setModel(new PopupListModel(Form.TEMPLATE_ID));
	}

	@Override
	protected void installCellRenderer() {
		super.setCellRenderer(new PopupPrintingTemplateListCellRenderer(Form.TEMPLATE_DEFAULT, Form.TEMPLATE_NAME,
				this.bundle, this.enabledPrivateTemplates));
	}

	@Override
	protected void installMouseHandler() {
		this.mouseHandler = new MouseHandlerPopupList();
		this.addMouseListener(this.mouseHandler);
		this.addMouseMotionListener(this.mouseHandler);

	}

	@Override
	protected JLabel getEmptyMessage() {
		if (this.emptyLabel == null) {
			this.emptyLabel = new JLabel(ApplicationManager.getTranslation(Form.messageEmptyTemplate, this.bundle));
			this.emptyLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		return this.emptyLabel;
	}

	@Override
	protected void setTextLabelInfo(final ResourceBundle res, final ListModel model) {
		final JLabel label = this.getInfoMessage();
		label.setText(ApplicationManager.getTranslation(Form.messageInfoTemplate, res,
				new Object[] { new Integer(this.getModel().getSize()) }));
		label.setFont(label.getFont().deriveFont(Font.BOLD));
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.bundle = resourceBundle;
		if (this.helpItem != null) {
			this.helpItem.setText(ApplicationManager.getTranslation(Form.messageHelpTemplate, resourceBundle));
		}
		if (this.wordItem != null) {
			this.wordItem.setText(ApplicationManager.getTranslation(Form.messageCreateWordTemplate, resourceBundle));
		}
		if (this.docxItem != null) {
			this.docxItem.setText(ApplicationManager.getTranslation(Form.messageCreateDocxTemplate, resourceBundle));
		}
		if (this.openofficeItem != null) {
			this.openofficeItem
			.setText(ApplicationManager.getTranslation(Form.messageCreateOpenOfficeTemplate, resourceBundle));
		}
		if (this.addTemplateItem != null) {
			this.addTemplateItem.setText(ApplicationManager.getTranslation(Form.messageAddTemplate, resourceBundle));
		}
	}

	public void configurePrivateAction(final PopupList list, final int sel) {
		final ListModel model = list.getModel();
		if (model instanceof PopupListModel) {
			final Map hRecord = (Map) ((PopupListModel) model).getElementAt(sel);
			final EntityReferenceLocator locator = this.form.getFormManager().getReferenceLocator();

			final Map kv = new Hashtable();
			final Map av = new Hashtable();

			if (hRecord.containsKey(Form.TEMPLATE_PRIVATE)) {
				final Object o = hRecord.get(Form.TEMPLATE_PRIVATE);
				if (o instanceof Number) {
					final boolean bPrivate = ((Number) o).intValue() > 0;

					if (bPrivate) {
						av.put(Form.TEMPLATE_PRIVATE, new Integer(0));
					} else {
						final int value = this.form.message(PopupPrintingTemplateList.M_CHANGE_USER_TEMPLATE,
								Form.QUESTION_MESSAGE);
						if (value == Form.YES) {
							av.put(Form.TEMPLATE_PRIVATE, new Integer(1));
							av.put(Form.TEMPLATE_USER, ((ClientReferenceLocator) locator).getUser());
						}
					}
					kv.put(Form.TEMPLATE_ID, hRecord.get(Form.TEMPLATE_ID));

					try {
						((UtilReferenceLocator) locator).getPrintingTemplateEntity(locator.getSessionId())
						.update(av, kv, locator.getSessionId());
					} catch (final Exception ex) {
						PopupPrintingTemplateList.logger.error(null, ex);
					}
				}
			} else {
				this.form.message(PopupPrintingTemplateList.M_ERROR_CHANGE_PRIVATE_TEMPLATE, Form.ERROR_MESSAGE);
			}

		}
	}

	public void chooseDefaultTemplate(final PopupList list, final int sel) {
		final ListModel model = list.getModel();
		if (model instanceof PopupListModel) {
			final Object data = ((PopupListModel) model).getElementAt(sel);
			try {
				final EntityReferenceLocator locator = this.form.getFormManager().getReferenceLocator();
				final Entity templateEntity = ((UtilReferenceLocator) locator)
						.getPrintingTemplateEntity(locator.getSessionId());
				final Map kv = new Hashtable();
				kv.put(Form.TEMPLATE_DEFAULT, new Integer(1));
				final List av = new Vector();
				av.add(Form.TEMPLATE_DEFAULT);
				av.add(Form.TEMPLATE_ID);

				final EntityResult res = templateEntity.query(kv, av, locator.getSessionId());
				if (res.getCode() != EntityResult.OPERATION_WRONG) {
					Object oPreviousKey = null;
					if (res.calculateRecordNumber() > 0) {
						if (res.containsKey(Form.TEMPLATE_ID)) {
							oPreviousKey = CollectionTools.firstElement((List) res.get(Form.TEMPLATE_ID));
							final Map kvA = new Hashtable();
							kvA.put(Form.TEMPLATE_ID, oPreviousKey);
							final Map avA = new Hashtable();
							avA.put(Form.TEMPLATE_DEFAULT, new Integer(0));
							final EntityResult result = templateEntity.update(avA, kvA, locator.getSessionId());
							if (result.getCode() == EntityResult.OPERATION_WRONG) {
								throw new Exception("ERROR TO ESTABLISH THE DEFAULT TEMPLATE");
							}
						}
					}

					final Map hData = (Map) data;
					if (hData.containsKey(Form.TEMPLATE_ID)) {
						final Object key = hData.get(Form.TEMPLATE_ID);
						if ((oPreviousKey != null) && oPreviousKey.equals(key)) {
							PopupPrintingTemplateList.this.setVisible(false);
							return;
						}
						final Map kvA = new Hashtable();
						kvA.put(Form.TEMPLATE_ID, key);
						final Map avA = new Hashtable();
						avA.put(Form.TEMPLATE_DEFAULT, new Integer(1));
						final EntityResult result = templateEntity.update(avA, kvA, locator.getSessionId());
						if (result.getCode() == EntityResult.OPERATION_WRONG) {
							throw new Exception("ERROR TO ESTABLISH THE DEFAULT TEMPLATE");
						}
					}
					PopupPrintingTemplateList.this.setVisible(false);
					return;
				}
			} catch (final Exception ex) {
				PopupPrintingTemplateList.logger.error(null, ex);
				this.form.message("ERROR TO CHECK THE DEFAULT TEMPLATE: " + ex.getMessage(), Form.ERROR_MESSAGE);
				PopupPrintingTemplateList.this.setVisible(false);
				return;
			}

		}
	}

	public void saveTemplate(final PopupList list, final int sel, final MouseEvent e) {
		final ListModel model = list.getModel();
		if (model instanceof PopupListModel) {
			final Object oData = ((PopupListModel) model).getElementAt(sel);
			if (oData instanceof Map) {
				final Map hData = (Map) oData;
				if (hData.containsKey(Form.TEMPLATE_ID)) {
					try {
						final EntityReferenceLocator locator = this.form.getFormManager().getReferenceLocator();
						final Entity templateEntity = ((UtilReferenceLocator) locator)
								.getPrintingTemplateEntity(locator.getSessionId());
						final EntityResult res = templateEntity.query(hData, new Vector(), locator.getSessionId());
						if ((res.getCode() != EntityResult.OPERATION_WRONG) && (res.calculateRecordNumber() == 1)) {
							if (res.containsKey(Form.TEMPLATE_NAME) && res.containsKey(Form.TEMPLATE_WAREHOUSE)) {
								final String templateName = (String) CollectionTools
										.firstElement((List) res.get(Form.TEMPLATE_NAME));
								final DataFile file = new DataFile(templateName,
										(BytesBlock) CollectionTools
										.firstElement((List) res.get(Form.TEMPLATE_WAREHOUSE)));

								if (this.fileChooser == null) {
									this.createFileChooser();
								}

								// Hide the popup
								PopupPrintingTemplateList.this.setVisible(false);

								File selectedFile = null;
								final Object oOriginalName = templateName;
								if (oOriginalName != null) {
									this.fileChooser.setSelectedFile(
											new File(this.fileChooser.getCurrentDirectory(), (String) oOriginalName));
								}
								final int option = this.fileChooser.showSaveDialog((Component) e.getSource());
								if (option == JFileChooser.CANCEL_OPTION) {
									return;
								}
								selectedFile = this.fileChooser.getSelectedFile();

								if (selectedFile.exists()) {
									if (!this.form.question("templates.file_exists_proceed_anyway")) {
										return;
									}
								}

								FileUtils.saveFile(selectedFile, file.getBytesBlock().getBytes(), false);

								this.form.message("templates.template_download_ok", Form.INFORMATION_MESSAGE);
							}
						}
					} catch (final Exception exc) {
						PopupPrintingTemplateList.logger.error(null, exc);
						this.form.message("ERROR DELETING TEMPLATE: " + exc.getMessage(), Form.ERROR_MESSAGE, exc);
						PopupPrintingTemplateList.this.setVisible(false);
						return;
					}
				}
			}
		}
	}

	public void deleteTemplate(final PopupList list, final int sel) {
		final ListModel model = list.getModel();
		if (model instanceof PopupListModel) {
			final Object oData = ((PopupListModel) model).getElementAt(sel);
			if (oData instanceof Map) {
				final Map hData = (Map) oData;
				if (hData.containsKey(Form.TEMPLATE_ID)) {
					final Object key = hData.get(Form.TEMPLATE_ID);
					try {
						if (!this.form.question(Form.messageQueryDeleted)) {
							PopupPrintingTemplateList.this.setVisible(false);
							return;
						}
						final EntityReferenceLocator locator = this.form.getFormManager().getReferenceLocator();
						final Entity templateEntity = ((UtilReferenceLocator) locator)
								.getPrintingTemplateEntity(locator.getSessionId());
						final Map kv = new Hashtable();
						kv.put(Form.TEMPLATE_ID, key);
						final EntityResult res = templateEntity.delete(kv, locator.getSessionId());
						if (res.getCode() != EntityResult.OPERATION_WRONG) {
							this.form.message(Form.messageDeletedTemplate, Form.INFORMATION_MESSAGE);
							PopupPrintingTemplateList.this.setVisible(false);
							return;
						}
					} catch (final Exception ex) {
						PopupPrintingTemplateList.logger.error(null, ex);
						this.form.message("ERROR DELETING TEMPLATE: " + ex.getMessage(), Form.ERROR_MESSAGE, ex);
						PopupPrintingTemplateList.this.setVisible(false);
						return;
					}
				}

			}
		}

	}

	protected void retrieveNotQueryAttributes() {
		final List attributesToQuery = this.form.getNotQueriedDataFieldAttributes();
		if (attributesToQuery.size() > 0) {
			final OperationThread t = new OperationThread() {

				@Override
				public void run() {
					try {
						this.hasStarted = true;
						this.status = ApplicationManager.getTranslation("tabpanel.updating_data",
								PopupPrintingTemplateList.this.form.getResourceBundle());
						final EntityResult res = PopupPrintingTemplateList.this.form
								.query(PopupPrintingTemplateList.this.form.getCurrentIndex(), attributesToQuery);
						if (res.getCode() == EntityResult.OPERATION_WRONG) {
							PopupPrintingTemplateList.this.form.message(res.getMessage(), Form.ERROR_MESSAGE);
							this.hasFinished = true;
							return;
						}
						if (res.isEmpty()) {
							return;
						}

						final boolean oldValueChangeListener = PopupPrintingTemplateList.this.form.getInteractionManager()
								.isValueChangeListenerEnabled();
						try {
							PopupPrintingTemplateList.this.form.getInteractionManager()
							.setValueChangeEventListenerEnabled(false);
							final Enumeration enumKeys = res.keys();
							while (enumKeys.hasMoreElements()) {
								final Object oKey = enumKeys.nextElement();
								final List vector = (List) res.get(oKey);
								if ((vector == null) || vector.isEmpty()) {
									continue;
								}
								if (attributesToQuery.contains(oKey)) {
									PopupPrintingTemplateList.this.form.setDataFieldValue(oKey, vector.get(0));
									PopupPrintingTemplateList.this.form.setDataFieldValueToFormCache(oKey,
											vector.get(0));
								}
							}
							PopupPrintingTemplateList.this.form.clearNotQueriedTabs();
						} finally {
							PopupPrintingTemplateList.this.form.getInteractionManager()
							.setValueChangeEventListenerEnabled(oldValueChangeListener);
						}

						this.hasFinished = true;
					} catch (final Exception e2) {
						PopupPrintingTemplateList.logger.error(null, e2);
						this.hasFinished = true;
					}
				}
			};
			final Window w = SwingUtilities.getWindowAncestor(this.form);
			if (w instanceof Frame) {
				ApplicationManager.proccessNotCancelableOperation((Frame) w, t, 600);
			} else if (w instanceof Dialog) {
				ApplicationManager.proccessNotCancelableOperation((Dialog) w, t, 600);
			}
		}
	}

	public void openTemplate(final PopupList list, final int sel) {
		final ListModel model = list.getModel();
		if (model instanceof PopupListModel) {
			final Object oData = ((PopupListModel) model).getElementAt(sel);
			if (oData instanceof Map) {
				final Map hData = (Map) oData;
				if (hData.containsKey(Form.TEMPLATE_ID)) {
					try {
						final EntityReferenceLocator locator = this.form.getFormManager().getReferenceLocator();
						final Entity templateEntity = ((UtilReferenceLocator) locator)
								.getPrintingTemplateEntity(locator.getSessionId());
						final EntityResult res = templateEntity.query(hData, new Vector(), locator.getSessionId());
						if ((res.getCode() != EntityResult.OPERATION_WRONG) && (res.calculateRecordNumber() == 1)) {
							if (res.containsKey(Form.TEMPLATE_NAME) && res.containsKey(Form.TEMPLATE_WAREHOUSE)) {
								// Retrieve
								this.retrieveNotQueryAttributes();
								final String templateName = (String) CollectionTools
										.firstElement((List) res.get(Form.TEMPLATE_NAME));
								final DataFile file = new DataFile(templateName,
										(BytesBlock) CollectionTools
												.firstElement((List) res.get(Form.TEMPLATE_WAREHOUSE)));
								final String userDirectory = System.getProperty("java.io.tmpdir");
								final String fileName = file.getFileName();
								final File f = new File(userDirectory, fileName);
								final File outputFile = new File(userDirectory, "" + System.currentTimeMillis() + fileName);
								if ((!f.exists()) || (f.length() != file.getBytesBlock().getBytes().length)) {
									FileUtils.saveFile(f, file.getBytesBlock().getBytes(), false);
								}
								final int index = templateName.lastIndexOf(".");
								final String ext = templateName.substring(index);

								if (".pdf".equalsIgnoreCase(ext)) {
									try {
										this.form.fillPDF(f, outputFile);
									} catch (final Exception ex) {
										PopupPrintingTemplateList.logger.error(null, ex);
										this.form.message(Form.messageErrorGeneratePDF, Form.ERROR_MESSAGE, ex);
									}
								} else if (".doc".equalsIgnoreCase(ext) || ".docx".equalsIgnoreCase(ext)) {
									try {
										this.form.fillDocSust(f);
									} catch (final Exception ex) {
										PopupPrintingTemplateList.logger.error(null, ex);
										this.form.message("WordError: " + ex.getMessage(), Form.ERROR_MESSAGE);
									}
								} else if (".odt".equalsIgnoreCase(ext)) {
									try {
										this.form.fillODT(f);
									} catch (final Exception ex) {
										PopupPrintingTemplateList.logger.error(null, ex);
										this.form.message("OpennOfficeError: " + ex.getMessage(), Form.ERROR_MESSAGE);
									}
								}
							}
						} else {
							this.form.message("form.error_getting_template", Form.ERROR_MESSAGE);
						}
					} catch (final Exception ex) {
						PopupPrintingTemplateList.logger.error(null, ex);
						this.form.message("Error getting template: " + ex.getMessage(), Form.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	public boolean isEnabledPrivateTemplate() {
		return this.enabledPrivateTemplates;
	}

	public void setEnabledPrivateTemplate(final boolean enabledPrivateTemplate) {
		this.enabledPrivateTemplates = enabledPrivateTemplate;
	}

}
