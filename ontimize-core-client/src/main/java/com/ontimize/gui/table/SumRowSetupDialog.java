package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.swing.selectablelist.SelectableItemRenderer;

/**
 * This class determines the dialog that shows up from the table to configure the sum rows.
 */
public class SumRowSetupDialog extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(SumRowSetupDialog.class);

	public static Color defaultNonModifiableItemColor = Color.RED;

	public static Color defaultSelectedItemFgColor = Color.WHITE;

	public static Color defaultSelectedItemBgColor = new Color(0x3399ff);

	public static Color defaultItemFgColor = Color.BLACK;

	protected static class SelectableItemModifiableRenderer extends SelectableItemRenderer {

		public SelectableItemModifiableRenderer() {
			super();
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof SumRowSetupDialog.SelectableFunctionItem) {
				((JCheckBox) c).setOpaque(false);
				if (!(((SumRowSetupDialog.SelectableFunctionItem) value).isModificable())) {
					((JCheckBox) c).setForeground(SumRowSetupDialog.defaultNonModifiableItemColor);
				} else {
					if (isSelected) {
						((JCheckBox) c).setOpaque(true);
						((JCheckBox) c).setForeground(SumRowSetupDialog.defaultSelectedItemFgColor);
						((JCheckBox) c).setBackground(SumRowSetupDialog.defaultSelectedItemBgColor);
					} else {
						((JCheckBox) c).setForeground(SumRowSetupDialog.defaultItemFgColor);
					}
				}
			}
			return c;
		}

	}

	public static class SelectableFunctionItem extends SelectableItemRenderer.SelectableItem implements Comparable {

		protected boolean selected = false;

		protected String operation = Table.SUM_es_ES;

		protected String opText = Table.SUM_es_ES;

		protected boolean modifiable = true;

		public SelectableFunctionItem(final String text, final ResourceBundle res, final boolean modifiable) {
			super(text, res);
			this.setTextOp();
			this.modifiable = modifiable;
			if (!modifiable) {
				this.selected = true;
			}
		}

		@Override
		public boolean isSelected() {
			if (!this.modifiable) {
				return true;
			}
			return this.selected;
		}

		@Override
		public void setResourceBundle(final ResourceBundle res) {
			super.setResourceBundle(res);
			this.setTextOp();
		}

		public boolean isModificable() {
			return this.modifiable;
		}

		@Override
		public void setSelected(final boolean sel) {
			if (this.modifiable) {
				this.selected = sel;
			}
		}

		public void setOperation(final String op) {
			this.operation = op;
			this.setTextOp();
		}

		protected void setTextOp() {
			if (this.res != null) {
				try {
					this.opText = this.res.getString(this.operation);
				} catch (final Exception e) {
					SumRowSetupDialog.logger.error(null, e);
					this.opText = this.operation;
				}
			}
		}

		public String getOperation() {
			return this.operation;
		}

		@Override
		public String toString() {
			if (!this.isSelected()) {
				return this.translatedText;
			}
			return this.translatedText + " - " + this.opText;
		}

		@Override
		public int compareTo(final Object o2) {
			if (o2 instanceof SumRowSetupDialog.SelectableFunctionItem) {
				final String t2 = ((SumRowSetupDialog.SelectableFunctionItem) o2).translatedText;
				return this.translatedText.compareTo(t2);
			}
			return 0;
		}

		@Override
		public boolean equals(final Object o) {
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	private static final String keyDialogMessage = "table.configure_sum_column";

	private static final String helpDialogMessage = "table.operation_help_dialog";

	protected Table table = null;

	protected ResourceBundle bundle = null;

	protected JList columnsList = null;

	private final int LIST_MOUSE_X_MAX = 24;

	protected JLabel labelHelp = null;

	protected int indexPress = -1;

	protected JPopupMenu menu = null;

	protected JMenuItem sumMenu = null;

	protected JMenuItem averageMenu = null;

	protected JMenuItem maximumMenu = null;

	protected JMenuItem minimumMenu = null;

	protected JButton resetButton = null;

	protected JButton okButton = null;

	protected List additionalTotalRowOperations;

	public SumRowSetupDialog(final Frame f, final Table t) {
		super(f, SumRowSetupDialog.keyDialogMessage, true);
		this.init(t);
	}

	public SumRowSetupDialog(final Dialog d, final Table t) {
		super(d, SumRowSetupDialog.keyDialogMessage, true);
		this.init(t);
	}

	protected void selectionOperation(final int x, final int y) {
		if (this.menu != null) {
			this.menu.show(this.columnsList, x, y);
		}
	}

	protected void installOperationMenu() {
		this.menu = new JPopupMenu();
		this.sumMenu = new JMenuItem(Table.SUM_es_ES);
		this.sumMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SumRowSetupDialog.SelectableFunctionItem it = (SumRowSetupDialog.SelectableFunctionItem) SumRowSetupDialog.this.columnsList
						.getModel()
						.getElementAt(SumRowSetupDialog.this.indexPress);
				it.setOperation(Table.SUM_es_ES);
			}
		});
		this.menu.add(this.sumMenu);
		this.averageMenu = new JMenuItem(Table.AVERAGE_es_ES);
		this.averageMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SumRowSetupDialog.SelectableFunctionItem it = (SumRowSetupDialog.SelectableFunctionItem) SumRowSetupDialog.this.columnsList
						.getModel()
						.getElementAt(SumRowSetupDialog.this.indexPress);
				it.setOperation(Table.AVERAGE_es_ES);
			}
		});

		this.menu.add(this.averageMenu);
		this.maximumMenu = new JMenuItem(Table.MAXIMUM_es_ES);
		this.maximumMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SumRowSetupDialog.SelectableFunctionItem it = (SumRowSetupDialog.SelectableFunctionItem) SumRowSetupDialog.this.columnsList
						.getModel()
						.getElementAt(SumRowSetupDialog.this.indexPress);
				it.setOperation(Table.MAXIMUM_es_ES);
			}
		});

		this.menu.add(this.maximumMenu);
		this.minimumMenu = new JMenuItem(Table.MINIMUM_es_ES);

		this.minimumMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SumRowSetupDialog.SelectableFunctionItem it = (SumRowSetupDialog.SelectableFunctionItem) SumRowSetupDialog.this.columnsList
						.getModel()
						.getElementAt(SumRowSetupDialog.this.indexPress);
				it.setOperation(Table.MINIMUM_es_ES);
			}
		});

		this.menu.add(this.minimumMenu);
		this.additionalTotalRowOperations = this.table.getTotalRowOperation();
		for (int i = 0; i < this.additionalTotalRowOperations.size(); i++) {
			final TotalRowOperation totalRowOperation = (TotalRowOperation) this.additionalTotalRowOperations.get(i);
			final JMenuItem currentItem = totalRowOperation.getItem();
			if (!(currentItem instanceof JMenu)) {
				currentItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						final SumRowSetupDialog.SelectableFunctionItem it = (SumRowSetupDialog.SelectableFunctionItem) SumRowSetupDialog.this.columnsList
								.getModel()
								.getElementAt(SumRowSetupDialog.this.indexPress);
						it.setOperation(totalRowOperation.getOperationText());
					}
				});
			}
			this.menu.add(currentItem);
		}
	}

	public int getIndexPress() {
		return this.indexPress;
	}

	protected void init(final Table t) {
		this.table = t;
		this.bundle = t.getResourceBundle();
		this.table = t;
		// List v = new Vector();
		this.installOperationMenu();
		this.columnsList = new JList();
		this.columnsList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				try {
					SumRowSetupDialog.this.indexPress = SumRowSetupDialog.this.columnsList
							.locationToIndex(e.getPoint());
					if (SumRowSetupDialog.this.indexPress < 0) {
						return;
					}
					if (SwingUtilities.isRightMouseButton(e)) {
						final SumRowSetupDialog.SelectableFunctionItem it = (SumRowSetupDialog.SelectableFunctionItem) SumRowSetupDialog.this.columnsList
								.getModel()
								.getElementAt(SumRowSetupDialog.this.indexPress);
						if (it.isSelected()) {
							SumRowSetupDialog.this.selectionOperation(e.getX(), e.getY());
						}
						return;
					}

					if (e.getX() > SumRowSetupDialog.this.LIST_MOUSE_X_MAX) {
						return;
					}
					final SelectableItemRenderer.SelectableItem it = (SelectableItemRenderer.SelectableItem) SumRowSetupDialog.this.columnsList
							.getModel()
							.getElementAt(SumRowSetupDialog.this.indexPress);
					it.setSelected(!it.isSelected());
					// If more than one are selected then unique series are not
					// allowed
					int selected = 0;
					final ListModel model = SumRowSetupDialog.this.columnsList.getModel();
					for (int i = 0; i < model.getSize(); i++) {
						if (((SelectableItemRenderer.SelectableItem) model.getElementAt(i)).isSelected()) {
							selected++;
						}
					}
					final Rectangle rect = SumRowSetupDialog.this.columnsList.getCellBounds(SumRowSetupDialog.this.indexPress,
							SumRowSetupDialog.this.indexPress);
					SumRowSetupDialog.this.columnsList.repaint(rect);
				} catch (final Exception ex) {
					SumRowSetupDialog.logger.trace(null, ex);
				}
			}
		});

		this.columnsList.setCellRenderer(new SelectableItemModifiableRenderer());

		this.getContentPane().setLayout(new BorderLayout());

		// this.getContentPane().add(this.labelInfo, BorderLayout.NORTH);
		this.getContentPane().add(new JScrollPane(this.columnsList));
		this.getContentPane().add(this.getButtonsPanel(), BorderLayout.SOUTH);
		this.applyBundle();
		this.pack();
	}

	public JList getColumnsList() {
		return this.columnsList;
	}

	protected JPanel getButtonsPanel() {
		final JPanel aux = new JPanel(new GridBagLayout());

		this.labelHelp = new JLabel(
				ApplicationManager.getTranslation(SumRowSetupDialog.helpDialogMessage, this.bundle));
		this.labelHelp.setFont(this.labelHelp.getFont().deriveFont(10F));
		this.labelHelp.setFont(this.labelHelp.getFont().deriveFont(Font.BOLD));
		this.labelHelp.setBorder(new LineBorder(Color.BLACK));
		// this.labelHelp.setForeground(Color.red);

		aux.add(this.labelHelp,
				new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.okButton = new JButton(ImageManager.getIcon(ImageManager.OK));
		this.okButton.setActionCommand("ok");
		this.okButton.setText(ApplicationManager.getTranslation("application.accept", this.bundle));

		aux.add(this.okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Map hSumColumns = new Hashtable();
				for (int i = 0; i < SumRowSetupDialog.this.columnsList.getModel().getSize(); i++) {
					if (SumRowSetupDialog.this.columnsList.getModel()
							.getElementAt(i) instanceof SumRowSetupDialog.SelectableFunctionItem) {
						final SumRowSetupDialog.SelectableFunctionItem col = (SumRowSetupDialog.SelectableFunctionItem) SumRowSetupDialog.this.columnsList
								.getModel()
								.getElementAt(i);
						if (col.isSelected()) {
							hSumColumns.put(col.getText(), col.getOperation());
						}
					}
				}

				final TableModel model = SumRowSetupDialog.this.table.getJTable().getModel();
				if ((model != null) && (model instanceof TableSorter)) {
					((TableSorter) model).setOperationColumns(hSumColumns);
				}
				SumRowSetupDialog.this.setVisible(false);
				SumRowSetupDialog.this.table.saveOperations(hSumColumns);
			}
		});
		this.resetButton = new JButton(ImageManager.getIcon(ImageManager.CANCEL));
		this.resetButton.setActionCommand("application.cancel");
		this.resetButton.setText(ApplicationManager.getTranslation("application.cancel", this.bundle));
		aux.add(this.resetButton, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				SumRowSetupDialog.this.setVisible(false);
			}
		});
		return aux;
	}

	public void setColumn() {
		Map h = null;
		final List operationCols = this.table.getOperationColumns();
		final List vFixedColumns = this.table.getOriginalSumRowCols();

		final TableModel model = this.table.getJTable().getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			h = ((TableSorter) model).getOperationColumn();
			final Vector list = new Vector();

			for (int i = 0; i < operationCols.size(); i++) {
				final String sKey = (String) operationCols.get(i);

				SumRowSetupDialog.SelectableFunctionItem item = null;
				if (vFixedColumns.contains(sKey)) {
					item = new SelectableFunctionItem(sKey, this.table.getResourceBundle(), false);
				} else {
					item = new SelectableFunctionItem(sKey, this.table.getResourceBundle(), true);
				}

				if (h.containsKey(sKey)) {
					item.setSelected(true);
					item.setOperation((String) h.get(sKey));
				} else {
					item.setSelected(false);
				}
				list.add(item);
			}
			Collections.sort(list);
			this.columnsList.setListData(list);
		}
	}

	public void applyBundle() {
		String sText = ApplicationManager.getTranslation(SumRowSetupDialog.keyDialogMessage, this.bundle);
		// this.labelInfo.setText(sText);
		sText = ApplicationManager.getTranslation(Table.SUM_es_ES, this.bundle);
		this.sumMenu.setText(sText);
		sText = ApplicationManager.getTranslation(Table.AVERAGE_es_ES, this.bundle);
		this.averageMenu.setText(sText);
		sText = ApplicationManager.getTranslation(Table.MAXIMUM_es_ES, this.bundle);
		this.maximumMenu.setText(sText);
		sText = ApplicationManager.getTranslation(Table.MINIMUM_es_ES, this.bundle);
		this.minimumMenu.setText(sText);
		sText = ApplicationManager.getTranslation(Table.COUNT_es_ES, this.bundle);
		this.okButton.setText(ApplicationManager.getTranslation("application.accept", this.bundle));
		this.resetButton.setText(ApplicationManager.getTranslation("application.cancel", this.bundle));
		this.setTitle(ApplicationManager.getTranslation(SumRowSetupDialog.keyDialogMessage, this.bundle));
		for (int i = 0; i < this.additionalTotalRowOperations.size(); i++) {
			final TotalRowOperation totalRowOperation = (TotalRowOperation) this.additionalTotalRowOperations.get(i);
			sText = ApplicationManager.getTranslation(totalRowOperation.getOperationText(), this.bundle);
			totalRowOperation.getItem().setText(sText);
		}
	}

	/**
	 * getTextsToTranslate
	 * @return Vector
	 */
	@Override
	public List getTextsToTranslate() {
		return null;
	}

	/**
	 * setLocaleComponente
	 * @param locale Locale
	 */
	@Override
	public void setComponentLocale(final Locale locale) {
	}

	/**
	 * setResourceBundle
	 * @param resourceBundle ResourceBundle
	 */
	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.bundle = resourceBundle;
		this.applyBundle();
	}

}
