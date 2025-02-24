package com.ontimize.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.report.store.ReportProperties;
import com.ontimize.jee.common.report.store.ReportStore;
import com.ontimize.jee.common.report.store.ReportStoreDefinition;

public class BasicReportSelection extends EJDialog {

	private static final Logger logger = LoggerFactory.getLogger(BasicReportSelection.class);

	private EntityReferenceLocator locator = null;

	protected JComboBox comboReportStore = null;

	protected JLabel labelReport = null;

	protected JLabel labelStore = null;

	protected JTextField fieldReport = null;

	protected ResourceBundle bundle = null;

	protected ListReportPanel listReport = null;

	protected JLabel labelList = null;

	protected JButton saveButton = null;

	protected JButton deleteButton = null;

	protected String entity = null;

	protected ReportStoreDefinition rsd = null;

	protected ReportStore[]					rs					= null;

	protected static BasicReportSelection selection = null;

	protected class ListReportPanel extends JPanel {

		protected ModelListReport model = null;

		protected JTable tableCenter = null;

		protected class ModelListReport extends AbstractTableModel {

			String[] columnName = {
					ApplicationManager.getTranslation("ReportDesigner.Name", BasicReportSelection.this.bundle),
					ApplicationManager
					.getTranslation("ReportDesigner.Store", BasicReportSelection.this.bundle) };

			List nameReport = null;

			List descriptionStore = null;

			public ModelListReport() {
				this.nameReport = new Vector();
				this.descriptionStore = new Vector();
			}

			@Override
			public int getColumnCount() {
				return this.columnName.length;
			}

			@Override
			public int getRowCount() {
				if (BasicReportSelection.this.rs == null) {
					return 0;
				} else {
					try {
						this.nameReport.clear();
						this.descriptionStore.clear();
						for (int i = 0; i < BasicReportSelection.this.rs.length; i++) {
							if (BasicReportSelection.this.rs[i] == null) {
								continue;
							}
							final ReportProperties[] aux = BasicReportSelection.this.rs[i].list(
									BasicReportSelection.this.entity, ReportProperties.BASIC,
									BasicReportSelection.this.locator.getSessionId());
							if (aux == null) {
								continue;
							}
							for (int j = 0; j < aux.length; j++) {
								this.nameReport.add(aux[j].getName());
								this.descriptionStore.add(BasicReportSelection.this.rs[i]
										.getDescription(BasicReportSelection.this.locator.getSessionId()));
							}
						}
						return this.nameReport.size();
					} catch (final Exception ex) {
						BasicReportSelection.logger.error(ex.getMessage(), ex);
						return 0;
					}
				}
			}

			@Override
			public String getColumnName(final int columnIndex) {
				return this.columnName[columnIndex];
			}

			public void removeRow(final int rowIndex) {
				final String description = (String) this.descriptionStore.get(rowIndex);
				final ReportStore reportStore = BasicReportSelection.this.getReportStore(description);
				if (reportStore != null) {
					final String id = (String) this.nameReport.get(rowIndex);
					try {
						reportStore.remove(id, BasicReportSelection.this.locator.getSessionId());
						this.fireTableDataChanged();
					} catch (final Exception ex) {
						BasicReportSelection.logger.error(ex.getMessage(), ex);
					}
				}
			}

			@Override
			public Object getValueAt(final int rowIndex, final int columnIndex) {
				if (columnIndex == 0) {
					if ((this.nameReport == null) || (rowIndex >= this.nameReport.size())) {
						return "";
					}
					return this.nameReport.get(rowIndex);
				} else if (columnIndex == 1) {
					if ((this.descriptionStore == null) || (rowIndex >= this.descriptionStore.size())) {
						return "";
					}
					return this.descriptionStore.get(rowIndex);
				} else {
					return "";
				}
			}

			public int contain(final String name, final String description) {
				if (this.nameReport == null) {
					return -1;
				}

				for (int i = 0; i < this.nameReport.size(); i++) {
					if (name.equalsIgnoreCase((String) this.nameReport.get(i))) {
						if (description.equalsIgnoreCase((String) this.descriptionStore.get(i))) {
							return i;
						}
					}
				}
				return -1;
			}

		}

		public ListReportPanel() {
			this.model = new ModelListReport();
			this.tableCenter = new JTable(this.model);
			this.tableCenter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.setLayout(new GridLayout());
			final JScrollPane pane = new JScrollPane(this.tableCenter) {

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(500, 200);
				}

				@Override
				public Dimension getSize() {
					return new Dimension(500, 200);
				}
			};

			final ListSelectionModel modelSelection = this.tableCenter.getSelectionModel();
			modelSelection.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					final int index = ListReportPanel.this.tableCenter.getSelectedRow();
					if (index >= 0) {
						Object o = ListReportPanel.this.tableCenter.getModel().getValueAt(index, 0);
						BasicReportSelection.this.fieldReport.setText((String) o);
						o = ListReportPanel.this.tableCenter.getModel().getValueAt(index, 1);
						BasicReportSelection.this.comboReportStore.setSelectedItem(o);
					}
				}
			});

			this.add(pane);
			final Border bordeOUT = BorderFactory.createEmptyBorder(5, 5, 5, 5);
			final Border bordeIN = BorderFactory.createEtchedBorder();
			this.setBorder(new javax.swing.border.CompoundBorder(bordeOUT, bordeIN));

		}

		public void updateStores() {
			this.model.fireTableChanged(new TableModelEvent(this.model));
		}

		public boolean contain(final String name, final String descriptionStore) {
			final int i = this.model.contain(name, descriptionStore);
			if (i >= 0) {
				return true;
			} else {
				return false;
			}
		}

		public void remove(final String name, final String description) {
			final int i = this.model.contain(name, description);
			if (i >= 0) {
				this.model.removeRow(i);
			}
		}

		public JTable getTable() {
			return this.tableCenter;
		}

	}

	public BasicReportSelection(final Frame f, final EntityReferenceLocator referenceLocator, final ResourceBundle bundle,
			final ReportStore[] rs, final String entity, final ReportStoreDefinition rsd) {
		super(f, true);
		this.bundle = bundle;
		this.rs = rs;
		this.entity = entity;
		this.rsd = rsd;
		this.locator = referenceLocator;
		this.init();
	}

	protected void init() {
		this.setTitle(ApplicationManager.getTranslation(ReportUtils.REPORT_SAVE, this.bundle));
		this.labelReport = new JLabel(ApplicationManager.getTranslation(ReportUtils.REPORT_NAME, this.bundle));
		this.fieldReport = new JTextField(this.rsd.getName());

		this.fieldReport.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {
				if (e.getDocument().getLength() == 0) {
					BasicReportSelection.this.saveButton.setEnabled(false);

					BasicReportSelection.this.deleteButton.setEnabled(false);
				} else {
					if (BasicReportSelection.this.listReport.contain(BasicReportSelection.this.fieldReport.getText(),
							(String) BasicReportSelection.this.comboReportStore.getSelectedItem())) {
						BasicReportSelection.this.deleteButton.setEnabled(true);
					} else {
						BasicReportSelection.this.deleteButton.setEnabled(false);
					}
					BasicReportSelection.this.saveButton.setEnabled(true);
				}
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				if (e.getDocument().getLength() == 0) {
					BasicReportSelection.this.saveButton.setEnabled(false);
					BasicReportSelection.this.deleteButton.setEnabled(false);
				} else {
					if (BasicReportSelection.this.listReport.contain(BasicReportSelection.this.fieldReport.getText(),
							(String) BasicReportSelection.this.comboReportStore.getSelectedItem())) {
						BasicReportSelection.this.deleteButton.setEnabled(true);
					} else {
						BasicReportSelection.this.deleteButton.setEnabled(false);
					}

					BasicReportSelection.this.saveButton.setEnabled(true);
				}
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				if (e.getDocument().getLength() == 0) {
					BasicReportSelection.this.saveButton.setEnabled(false);
					BasicReportSelection.this.deleteButton.setEnabled(false);
				} else {
					if (BasicReportSelection.this.listReport.contain(BasicReportSelection.this.fieldReport.getText(),
							(String) BasicReportSelection.this.comboReportStore.getSelectedItem())) {
						BasicReportSelection.this.deleteButton.setEnabled(true);
					} else {
						BasicReportSelection.this.deleteButton.setEnabled(false);
					}

					BasicReportSelection.this.saveButton.setEnabled(true);
				}
			}
		});

		this.labelStore = new JLabel(ApplicationManager.getTranslation(ReportUtils.REPORT_STORE, this.bundle));

		if ((this.rs != null) && (this.rs.length > 0)) {
			final java.util.Vector v = new java.util.Vector();
			for (int i = 0; i < this.rs.length; i++) {
				try {
					v.add(this.rs[i].getDescription(this.locator.getSessionId()));
				} catch (final Exception ex) {
					BasicReportSelection.logger.error(ex.getMessage(), ex);
				}
				final DefaultComboBoxModel model = new DefaultComboBoxModel(v);
				this.comboReportStore = new JComboBox(model);
			}
		} else {
			this.comboReportStore = new JComboBox();
		}

		this.labelList = new JLabel(ApplicationManager.getTranslation(ReportUtils.STORED_REPORT_LIST, this.bundle));
		this.labelList.setFont(this.labelList.getFont().deriveFont(Font.BOLD));
		this.labelList.setFont(this.labelList.getFont().deriveFont(this.labelList.getFont().getSize() + 3L));
		this.labelList.setForeground(Color.red);
		this.labelList.setBorder(new EmptyBorder(4, 2, 2, 2));
		this.listReport = new ListReportPanel();

		this.getContentPane().setLayout(new BorderLayout());

		final JPanel nothPanel = new JPanel(new GridLayout(2, 2));
		this.labelReport.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.labelReport.setBackground(Color.white);
		this.labelReport.setOpaque(true);
		this.labelReport.setFont(this.labelReport.getFont().deriveFont(Font.BOLD));
		nothPanel.add(this.labelReport);
		nothPanel.add(this.fieldReport);
		this.labelStore.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.labelStore.setBackground(Color.white);
		this.labelStore.setOpaque(true);
		this.labelStore.setFont(this.labelStore.getFont().deriveFont(Font.BOLD));
		nothPanel.add(this.labelStore);
		nothPanel.add(this.comboReportStore);

		final Border borderOUT = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		final Border borderIN = BorderFactory.createEtchedBorder();
		nothPanel.setBorder(new javax.swing.border.CompoundBorder(borderOUT, borderIN));

		this.getContentPane().add(nothPanel, BorderLayout.NORTH);
		final JPanel panelCentral = new JPanel(new BorderLayout());
		panelCentral.add(this.listReport);
		panelCentral.add(this.labelList, BorderLayout.NORTH);
		this.getContentPane().add(panelCentral, BorderLayout.CENTER);
		final JPanel southPanel = new JPanel(new GridBagLayout());

		this.saveButton = new JButton(ImageManager.getIcon(ImageManager.SAVE));
		this.saveButton.setText(ApplicationManager.getTranslation("ReportDesigner.Guardar", this.bundle));
		this.saveButton.setEnabled(false);
		this.saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Object v = BasicReportSelection.this.comboReportStore.getSelectedItem();
				final String sInsertName = BasicReportSelection.this.fieldReport.getText();

				if ((v != null) && (sInsertName.length() > 0)) {
					final ReportStore store = BasicReportSelection.this.getReportStore((String) v);
					try {
						if (store.exists(sInsertName, BasicReportSelection.this.locator.getSessionId())) {
							BasicReportSelection.logger.warn("The file already exists: overwriting it");
							final int answer = JOptionPane.showConfirmDialog((Component) e.getSource(),
									ApplicationManager.getTranslation("mOtherReportExist",
											BasicReportSelection.this.bundle),
									"", JOptionPane.YES_NO_OPTION);
							if (answer == 1) {
								return;
							}
						}
						BasicReportSelection.this.rsd.setName(sInsertName);
						store.remove(sInsertName, BasicReportSelection.this.locator.getSessionId());
						store.add(sInsertName, BasicReportSelection.this.rsd,
								BasicReportSelection.this.locator.getSessionId());
						BasicReportSelection.this.listReport.updateStores();
						BasicReportSelection.this.fieldReport.setText("");
					} catch (final Exception ex) {
						BasicReportSelection.logger.error(ex.getMessage(), ex);
					}

				}
			}
		});
		southPanel.add(this.saveButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

		this.deleteButton = new JButton(ImageManager.getIcon(ImageManager.REPORT_DELETE));
		this.deleteButton.setText(ApplicationManager.getTranslation("ReportDesigner.Delete", this.bundle));
		this.deleteButton.setEnabled(false);
		this.deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Object v = BasicReportSelection.this.comboReportStore.getSelectedItem();
				final String sDeleteName = BasicReportSelection.this.fieldReport.getText();
				if ((v != null) && (sDeleteName.length() > 0)) {
					final int option = JOptionPane.showConfirmDialog((Component) e.getSource(),
							ApplicationManager.getTranslation("ReportDesigner.M_DeleteReport",
									BasicReportSelection.this.bundle));
					if (option != JOptionPane.OK_OPTION) {
						return;
					}
					BasicReportSelection.this.listReport.remove(sDeleteName, (String) v);
					BasicReportSelection.this.fieldReport.setText("");
				}
			}
		});
		southPanel.add(this.deleteButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		this.pack();
		ApplicationManager.center(this);
	}

	protected ReportStore getReportStore(final String description) {
		if (this.rs == null) {
			return null;
		}
		for (int i = 0; i < this.rs.length; i++) {
			if (this.rs[i] == null) {
				continue;
			}
			try {
				if (description.equalsIgnoreCase(this.rs[i].getDescription(this.locator.getSessionId()))) {
					return this.rs[i];
				}
			} catch (final Exception ex) {
				BasicReportSelection.logger.error(ex.getMessage(), ex);
			}
		}
		return null;
	}

	protected void setReportStore(final ReportStore[] rs) {
		this.rs = rs;
		if (rs != null) {
			final java.util.Vector v = new java.util.Vector();
			for (int i = 0; i < rs.length; i++) {
				try {
					v.add(rs[i].getDescription(this.locator.getSessionId()));
				} catch (final Exception ex) {
					BasicReportSelection.logger.error(ex.getMessage(), ex);
				}
			}
			final DefaultComboBoxModel model = new DefaultComboBoxModel(v);
			this.comboReportStore.setModel(model);
		} else {
			this.comboReportStore.setModel(new DefaultComboBoxModel(new Vector()));
		}
		this.listReport.updateStores();
	}

	public static void showSelection(final Component c, final EntityReferenceLocator referenceLocator, final ResourceBundle bundle,
			final ReportStore[] rs, final String entity, final ReportStoreDefinition rsd) {
		if (BasicReportSelection.selection == null) {
			final Window w = SwingUtilities.getWindowAncestor(c);
			if (w instanceof Frame) {
				BasicReportSelection.selection = new BasicReportSelection((Frame) w, referenceLocator, bundle, rs,
						entity, rsd);
			}
		}

		if (BasicReportSelection.selection != null) {
			BasicReportSelection.selection.setReportStore(rs);
			BasicReportSelection.selection.setVisible(true);
		}
	}

}
