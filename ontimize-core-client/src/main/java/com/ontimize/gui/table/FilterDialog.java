package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.RealDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.FilterDialog.FilterList.FilterModel;
import com.ontimize.gui.table.TableSorter.DateFilter;
import com.ontimize.gui.table.TableSorter.DifferentFilter;
import com.ontimize.gui.table.TableSorter.DifferentSimpleFilter;
import com.ontimize.gui.table.TableSorter.Filter;
import com.ontimize.gui.table.TableSorter.MultipleFilter;
import com.ontimize.gui.table.TableSorter.SimpleFilter;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.swing.list.I18nListCellRenderer;

/**
 * Class that contains the GUI to filter the table. This class is accessed by the table when some
 * event of filtering is launched, opening the dialog in the corresponding configuration, in order
 * to perform the filtering in the model.
 */
public class FilterDialog extends EJDialog implements Internationalization, ActionListener {

	private static final Logger logger = LoggerFactory.getLogger(FilterDialog.class);

	private final String WITHOUTDATA = "table.without_data";

	private final String WITHDATA = "filtercomponent.with_data";

	private final String XTIMEAGO = "table.last";

	private final String COLUMN = "table.columns";

	private final String FILTER = "table.filters";

	private final String DISTINCT = "filtercomponent.distinct";

	protected Table table = null;

	protected TableSorter sorter = null;

	protected boolean dateClass = false;

	protected boolean booleanClass = false;

	protected JLabel label = new JLabel();

	protected JComboBox conditions = new JComboBox();

	protected String less = "<";

	protected String lessEqual = "<=";

	protected String equal = "=";

	protected String greater = ">";

	protected String greaterequal = ">=";

	protected String range = "table.interval";

	protected String distinct = "<>";

	protected String last = this.XTIMEAGO;

	protected JComboBox filterConditions = new JComboBox();

	protected JLabel lInfo = new JLabel("Current filter: ");

	protected JCheckBox cbNull = null;

	protected JCheckBox cbNotNull = null;

	protected ButtonGroup group = null;

	protected JCheckBox cbDay = null;

	protected JCheckBox cbMonth = null;

	protected JCheckBox cbYear = null;

	protected JPanel dMY = new JPanel(new GridLayout());

	protected JCheckBox cdDistinct = null;

	protected DateDataField fieldLessDate = null;

	protected DateDataField fieldGreaterDate = null;

	protected IntegerDataField fieldLessInteger = null;

	protected IntegerDataField fieldGreaterInteger = null;

	protected RealDataField fieldLessReal = null;

	protected RealDataField fieldGreaterReal = null;

	protected CheckDataField fieldCheck = null;

	protected TextDataField fieldText = null;

	protected IntegerDataField fieldInteger = null;

	protected DataField field = null;

	protected Object filter = null;

	protected JButton buttonOk = new JButton();

	protected JButton buttonReset = new JButton();

	protected JButton buttonDelete = new JButton();

	protected JButton buttonDeleteAll = new JButton();

	protected JButton buttonEdit = new JButton();

	protected JButton buttonAdd = new JButton();

	protected ResourceBundle bundle = null;

	protected JPanel filterConfigurationListPanel = null;

	protected JPanel selectionFilterPanel = null;

	protected JPanel controlButtonsPanel = new JPanel(new GridBagLayout());

	protected JPanel columnListPanel = null;

	protected Map filters = null;

	protected FilterList filterList = new FilterList();

	protected JList columnsList = new ColumnList(this.filterList);

	protected class ColumnList extends JList {

		FilterList filterList = null;

		public ColumnList(final FilterList filterList) {
			this.filterList = filterList;
		}

		public boolean hasFilter(final String col) {
			return this.filterList.hasFilter(col);
		}

	}

	protected static class FilterListRenderer extends DefaultListCellRenderer {

		ResourceBundle bundle = null;

		public FilterListRenderer(final ResourceBundle bundle) {
			this.setOpaque(true);
			this.bundle = bundle;
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {

			final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (comp instanceof JLabel) {
				this.setText(ApplicationManager.getTranslation(value.toString(), this.bundle));
				if (list instanceof ColumnList) {
					if (value instanceof FilterDialog.TranslatedItem) {
						final String item = value.toString();
						if (((ColumnList) list).hasFilter(item)) {
							((JLabel) comp).setForeground(Color.RED);
						} else {
							if (isSelected) {
								((JLabel) comp).setForeground(list.getSelectionForeground());
							} else {
								((JLabel) comp).setForeground(list.getForeground());
							}
						}
					}
				}
			}
			return comp;
		}

	}

	protected class FilterList extends JList {

		public FilterList() {
		}

		public void setFilters(final Map filters, final String col) {
			final FilterModel model = new FilterModel(filters, col);
			this.setModel(model);
		}

		public boolean hasFilter(final String col) {
			final Map h = this.getFilters();
			if (h == null) {
				return false;
			}
			if (h.containsKey(col)) {
				return true;
			}
			return false;
		}

		public void setColumn(final String col) {
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				((FilterModel) model).setColumn(col);
			}
		}

		public Map getFilters() {
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				return ((FilterModel) model).getFilters();
			} else {
				return null;
			}
		}

		public Object getCondition(final int index) {
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				return ((FilterModel) model).getCondition(index);
			}
			return null;
		}

		public Object getFilter(final int index) {
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				return ((FilterModel) model).getFilter(index);
			}
			return null;
		}

		public void deleteSelectedIndex() {
			final int[] index = this.getSelectedIndices();
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				((FilterModel) model).deleteSelectedIndex(index);
			}
		}

		public void deleteAll() {
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				((FilterModel) model).deleteAll();
			}
		}

		public void setFilter(final Object condition, final Object f, final int index) {
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				((FilterModel) model).sertFilter(condition, f, index);
			}
		}

		public void addFilter(final Object condition, final Object f) {
			final ListModel model = this.getModel();
			if ((model != null) && (model instanceof FilterModel)) {
				((FilterModel) model).addFilter(condition, f);
			}
		}

		protected class FilterModel extends javax.swing.AbstractListModel {

			private Map filters = null;

			private String column = null;

			private Object filterValue = null;

			protected class FiltroItem {

				String storedOperation = null;

				Object filter;

				public FiltroItem(final String operation, final Object filter) {
					this.storedOperation = operation;
					this.filter = filter;
				}

				@Override
				public String toString() {
					if (this.storedOperation == null) {
						return this.filter.toString();
					} else {
						return this.storedOperation.toString() + "(" + this.filter.toString() + ")";
					}
				}

			}

			public FilterModel(final Map filter, final String column) {
				this.filters = filter;
				this.column = column;
				this.filterValue = this.filters.get(this.column);
			}

			public Map getFilters() {
				return this.filters;
			}

			public void setColumn(final String col) {
				this.column = col;
				this.filterValue = this.filters.get(this.column);
				this.fireContentsChanged(this, 0, this.getSize());
			}

			public Object getCondition(final int index) {
				if (this.filterValue instanceof MultipleFilter) {
					final MultipleFilter multipleFilter = (MultipleFilter) this.filterValue;
					return multipleFilter.getCondition(index);
				} else {
					return null;
				}

			}

			public Object getFilter(final int index) {
				if (this.filterValue instanceof MultipleFilter) {
					final MultipleFilter multipleFilter = (MultipleFilter) this.filterValue;
					return multipleFilter.get(index);
				} else {
					return this.filterValue;
				}
			}

			public void deleteSelectedIndex(final int[] index) {
				for (int i = index.length - 1; i >= 0; i--) {
					this.delete(index[i]);
				}
			}

			public void delete(final int index) {
				if (this.filterValue instanceof MultipleFilter) {
					final MultipleFilter multipleFilter = (MultipleFilter) this.filterValue;
					if (multipleFilter.size() == 2) {
						this.filterValue = multipleFilter.get(index == 0 ? 1 : 0);
						this.filters.put(this.column, this.filterValue);
						this.fireIntervalRemoved(this, 0, 1);
					} else {
						multipleFilter.remove(index);
						this.filterValue = multipleFilter;
						this.filters.put(this.column, this.filterValue);
						this.fireIntervalRemoved(this, index, index + 1);
					}
				} else {
					this.filterValue = null;
					this.filters.remove(this.column);
					this.fireIntervalRemoved(this, 0, 1);
				}
			}

			public void deleteAll() {
				final int size = this.getSize();
				for (int i = size - 1; i >= 0; i--) {
					this.delete(i);
				}
			}

			public void addFilter(final Object condition, final Object f) {
				if (this.filterValue == null) {
					this.filterValue = f;
					this.filters.put(this.column, this.filterValue);
					this.fireIntervalAdded(this, 0, 1);
				} else if (this.filterValue instanceof MultipleFilter) {
					final MultipleFilter multipleFilter = (MultipleFilter) this.filterValue;
					if (((String) condition).equalsIgnoreCase("AND")) {
						multipleFilter.addAND(f);
					} else {
						multipleFilter.addOR(f);
					}

					this.filterValue = multipleFilter;
					this.filters.put(this.column, this.filterValue);
					this.fireIntervalAdded(this, multipleFilter.size(), multipleFilter.size() - 1);
				} else {
					final MultipleFilter multipleFilter = new MultipleFilter();
					multipleFilter.addOR(this.filterValue);
					if (((String) condition).equalsIgnoreCase("AND")) {
						multipleFilter.addAND(f);
					} else {
						multipleFilter.addOR(f);
					}
					this.filterValue = multipleFilter;
					this.filters.put(this.column, this.filterValue);
					this.fireIntervalAdded(this, multipleFilter.size(), multipleFilter.size() - 1);
				}

			}

			public void sertFilter(final Object condition, final Object f, final int index) {
				if (this.filterValue instanceof MultipleFilter) {
					final MultipleFilter multipleFilter = (MultipleFilter) this.filterValue;
					final MultipleFilter newFilter = new MultipleFilter();
					for (int i = 0; i < multipleFilter.size(); i++) {
						Object cond = multipleFilter.getCondition(i);
						Object oValue = multipleFilter.get(i);
						if (i == index) {
							cond = condition;
							oValue = f;
						}
						if ("OR".equals(cond)) {
							newFilter.addOR(oValue);
						} else {
							newFilter.addAND(oValue);
						}
					}
					this.filterValue = newFilter;
					this.filters.put(this.column, this.filterValue);
					this.fireContentsChanged(this, index, index);
				} else {
					this.filterValue = f;
					this.filters.put(this.column, this.filterValue);
					this.fireContentsChanged(this, 0, 1);
				}
			}

			/**
			 * getSize
			 * @return int
			 */
			@Override
			public int getSize() {
				if (this.filterValue == null) {
					return 0;
				} else if (this.filterValue instanceof MultipleFilter) {
					return ((MultipleFilter) this.filterValue).size();
				} else if (this.filterValue instanceof SimpleFilter) {
					return 1;
				} else if (this.filterValue instanceof Filter) {
					return 1;
				} else if (this.filterValue instanceof DateFilter) {
					return 1;
				}
				return 0;
			}

			/**
			 * getElementAt
			 * @param index int
			 * @return Object
			 */
			@Override
			public Object getElementAt(final int index) {
				if (this.filterValue instanceof MultipleFilter) {
					final Object o = ((MultipleFilter) this.filterValue).get(index);
					return new FiltroItem(index == 0 ? null : ((MultipleFilter) this.filterValue).getCondition(index),
							o);
				} else {
					return new FiltroItem(null, this.filterValue);
				}
			}

		}

	}

	JLabel conditionLabel = new JLabel("filtercomponent.condition");

	JPanel columnPanel = null;

	JPanel filterPanel = null;

	int column = -1;

	JPanel radiosPanel = new JPanel();

	ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FilterDialog.this.conditions.getSelectedIndex() != Filter.RANGE) {
				FilterDialog.this.field.getLabelComponent()
				.setText(ApplicationManager.getTranslation("table.value", FilterDialog.this.bundle) + " ");
				FilterDialog.this.fieldGreaterDate.setVisible(false);
				FilterDialog.this.fieldGreaterInteger.setVisible(false);
				FilterDialog.this.fieldGreaterReal.setVisible(false);

				if (FilterDialog.this.dateClass) {
					if (FilterDialog.this.last.equals(FilterDialog.this.conditions.getSelectedItem())) {
						FilterDialog.this.setLast(true);
					} else {
						FilterDialog.this.setLast(false);
					}
				}
			} else {
				FilterDialog.this.setLast(false);
				if (FilterDialog.this.fieldLessDate.isVisible()) {
					FilterDialog.this.fieldLessDate.setVisible(true);
					FilterDialog.this.fieldGreaterDate.setVisible(true);
					FilterDialog.this.fieldLessDate.getLabelComponent()
					.setText(ApplicationManager.getTranslation("filtercomponent.between", FilterDialog.this.bundle)
							+ " ");
					FilterDialog.this.fieldGreaterDate.getLabelComponent()
					.setText(ApplicationManager.getTranslation("Y", FilterDialog.this.bundle) + " ");
				}
				if (FilterDialog.this.fieldLessInteger.isVisible()) {
					FilterDialog.this.fieldLessInteger.setVisible(true);
					FilterDialog.this.fieldGreaterInteger.setVisible(true);
					FilterDialog.this.fieldLessInteger.getLabelComponent()
					.setText(ApplicationManager.getTranslation("filtercomponent.between", FilterDialog.this.bundle)
							+ " ");
					FilterDialog.this.fieldGreaterInteger.getLabelComponent()
					.setText(ApplicationManager.getTranslation("Y", FilterDialog.this.bundle) + " ");
				}
				if (FilterDialog.this.fieldLessReal.isVisible()) {
					FilterDialog.this.fieldLessReal.setVisible(true);
					FilterDialog.this.fieldGreaterReal.setVisible(true);
					FilterDialog.this.fieldLessReal.getLabelComponent()
					.setText(ApplicationManager.getTranslation("filtercomponent.between", FilterDialog.this.bundle)
							+ " ");
					FilterDialog.this.fieldGreaterReal.getLabelComponent()
					.setText(ApplicationManager.getTranslation("Y", FilterDialog.this.bundle) + " ");
				}

			}
		}
	};

	public FilterDialog(final Frame frame, final Table table) {
		super(frame, TableSorter.filterKey, true);
		this.init(table);
	}

	public FilterDialog(final Dialog d, final Table table) {
		super(d, TableSorter.filterKey, true);
		this.init(table);
	}

	/**
	 * Panel with all table columns and the appropriate listeners to select them
	 */

	protected static class TranslatedItem implements Comparable {

		ResourceBundle bundle = null;

		String column = null;

		String translateColumn = null;

		public TranslatedItem(final String column, final ResourceBundle bundle) {
			this.column = column;
			this.bundle = bundle;
			this.translateColumn = ApplicationManager.getTranslation(column, bundle);
		}

		public TranslatedItem(final String column) {
			this(column, null);
		}

		@Override
		public int compareTo(final Object o) {
			if (o instanceof FilterDialog.TranslatedItem) {
				return this.translateColumn.compareTo(((FilterDialog.TranslatedItem) o).getTranslation());
			}
			return 0;
		}

		public String getTranslation() {
			return this.translateColumn;
		}

		@Override
		public String toString() {
			return this.column;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof FilterDialog.TranslatedItem) {
				return this.column.equals(((FilterDialog.TranslatedItem) o).toString());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	protected JPanel getColumnListPanel() {
		this.columnPanel = new JPanel(new BorderLayout());
		this.columnPanel.setBorder(
				new javax.swing.border.TitledBorder(ApplicationManager.getTranslation(this.COLUMN, this.bundle)));
		this.columnsList.setVisibleRowCount(10);
		this.columnsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.columnsList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				final Object o = FilterDialog.this.columnsList.getSelectedValue();
				FilterDialog.this.configureByTypeOfColumn(((FilterDialog.TranslatedItem) o).toString());
				FilterDialog.this.setColumn(((FilterDialog.TranslatedItem) o).toString());
				if (FilterDialog.this.filterList != null) {
					FilterDialog.this.filterList.clearSelection();
				}
				FilterDialog.this.buttonDelete.setEnabled(false);
				FilterDialog.this.buttonDeleteAll.setEnabled(true);
				FilterDialog.this.buttonEdit.setEnabled(false);
				FilterDialog.this.buttonAdd.setEnabled(true);
				FilterDialog.this.filterConditions.setEnabled(true);
			}
		});
		this.columnsList.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.getModifiers() == InputEvent.META_MASK) {
					return;
				}
				if ((e.getKeyCode() == 40) || (e.getKeyCode() == 38)) {
					// up and down
					final Object o = FilterDialog.this.columnsList.getSelectedValue();
					FilterDialog.this.configureByTypeOfColumn(((FilterDialog.TranslatedItem) o).toString());
					FilterDialog.this.setColumn(((FilterDialog.TranslatedItem) o).toString());
					if (FilterDialog.this.filterList != null) {
						FilterDialog.this.filterList.clearSelection();
					}
					FilterDialog.this.buttonDelete.setEnabled(false);
					FilterDialog.this.buttonEdit.setEnabled(false);
					FilterDialog.this.buttonAdd.setEnabled(true);
					FilterDialog.this.filterConditions.setEnabled(true);
				}
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					if (FilterDialog.this.buttonDelete.isEnabled()) {
						FilterDialog.this.buttonDelete.doClick(10);
					}
				}
			}

			@Override
			public void keyTyped(final KeyEvent e) {
			}
		});
		final List vColumns = this.table.getOriginallyVisibleColumns();
		final Vector cT = new Vector();

		for (int i = 0; i < vColumns.size(); i++) {
			final FilterDialog.TranslatedItem item = new TranslatedItem((String) vColumns.get(i), this.bundle);
			cT.add(item);
		}

		Collections.sort(cT);
		if (vColumns != null) {
			this.columnsList.setListData(cT);
		}
		if (this.bundle != null) {
			this.columnsList.setCellRenderer(new FilterListRenderer(this.bundle));
		}
		final JScrollPane scroll = new JScrollPane(this.columnsList);
		this.columnPanel.add(scroll);
		return this.columnPanel;
	}

	protected JPanel getPanelsWithFilterList() {
		this.filterPanel = new JPanel(new BorderLayout());
		this.filterPanel.setBorder(
				new javax.swing.border.TitledBorder(ApplicationManager.getTranslation(this.FILTER, this.bundle)));
		this.filterList.setVisibleRowCount(7);
		this.filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.filterList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				FilterDialog.this.splitFilter();
				FilterDialog.this.buttonDelete.setEnabled(FilterDialog.this.filterList.getSelectedIndex() > -1);
				FilterDialog.this.buttonEdit.setEnabled(FilterDialog.this.filterList.getSelectedIndex() > -1);
			}
		});
		this.filterList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				// int index = filterList.locationToIndex(e.getPoint());
				FilterDialog.this.buttonDelete.setEnabled(FilterDialog.this.filterList.getSelectedIndex() > -1);
				FilterDialog.this.buttonEdit.setEnabled(FilterDialog.this.filterList.getSelectedIndex() > -1);
			}
		});
		final JScrollPane scroll = new JScrollPane(this.filterList);
		this.filterPanel.add(scroll);
		return this.filterPanel;
	}

	protected JPanel getSelectionFilterPanel() {

		final JPanel aux = new JPanel(new BorderLayout());

		this.conditions.setRenderer(new I18nListCellRenderer(this.bundle));
		this.conditions.addItem(this.less);
		this.conditions.addItem(this.lessEqual);
		this.conditions.addItem(this.equal);
		this.conditions.addItem(this.greaterequal);
		this.conditions.addItem(this.greater);
		this.conditions.addItem(this.range);
		this.conditions.addItem(this.distinct);
		this.conditions.addItem(this.last);
		this.conditions.addActionListener(this.actionListener);

		this.filterConditions.addItem("AND");
		this.filterConditions.addItem("OR");

		this.cdDistinct = new JCheckBox(this.DISTINCT);

		final ImageIcon iconDelete = ImageManager.getIcon(ImageManager.FUNNEL_DELETE);
		final ImageIcon iconDeleteAll = ImageManager.getIcon(ImageManager.FUNNEL_DELETE_ALL);

		final Map param = new Hashtable();

		param.put("attr", "");
		param.put("returnboolean", "yes");
		param.put("dim", "text");

		this.fieldLessDate = new DateDataField(param);
		this.fieldGreaterDate = new DateDataField(param);

		param.put("numbertype", "long");
		this.fieldLessInteger = new IntegerDataField(param);
		this.fieldGreaterInteger = new IntegerDataField(param);
		param.remove("numbertype");

		final Map hIntegerParam = new Hashtable();

		hIntegerParam.put("attr", "");
		hIntegerParam.put("labelvisible", "no");
		this.fieldInteger = new IntegerDataField(hIntegerParam);

		this.fieldLessReal = new RealDataField(param);
		this.fieldGreaterReal = new RealDataField(param);

		this.fieldCheck = new CheckDataField(param);
		this.fieldText = new TextDataField(param);
		this.fieldText.getLabelComponent().setText(ApplicationManager.getTranslation("table.value", this.bundle) + " ");
		this.cbNull = new JCheckBox(this.WITHOUTDATA);
		this.cbNotNull = new JCheckBox(this.WITHDATA);

		this.group = new ButtonGroup();

		this.cbDay = new JCheckBox(ApplicationManager.getTranslation("days", this.bundle), true);
		this.cbMonth = new JCheckBox(ApplicationManager.getTranslation("months", this.bundle));
		this.cbYear = new JCheckBox(ApplicationManager.getTranslation("ANHOS", this.bundle));
		this.group.add(this.cbDay);
		this.group.add(this.cbMonth);
		this.group.add(this.cbYear);

		this.buttonDelete.setIcon(iconDelete);
		this.buttonDelete.setMargin(new Insets(0, 0, 0, 0));
		this.buttonDelete.setToolTipText(ApplicationManager.getTranslation("table.delete_filter", this.bundle));

		this.buttonDeleteAll.setIcon(iconDeleteAll);
		this.buttonDeleteAll.setMargin(new Insets(0, 0, 0, 0));
		this.buttonDeleteAll.setToolTipText(ApplicationManager.getTranslation(Table.DELETE_FILTER_COLUMN, this.bundle));

		this.buttonAdd.setIcon(ImageManager.getIcon(ImageManager.FUNNEL_ADD));
		this.buttonAdd.setMargin(new Insets(0, 0, 0, 0));
		this.buttonAdd.setToolTipText(ApplicationManager.getTranslation("table.add_filter", this.bundle));

		this.buttonEdit.setIcon(ImageManager.getIcon(ImageManager.FUNNEL_EDIT));
		this.buttonEdit.setMargin(new Insets(0, 0, 0, 0));
		this.buttonEdit.setToolTipText(ApplicationManager.getTranslation("table.edit_filter", this.bundle));

		final JPanel pButtonsPanel = new JPanel(new GridBagLayout());
		pButtonsPanel.add(this.cbNotNull, new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pButtonsPanel.add(this.cbNull, new GridBagConstraints(2, 0, 4, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

		pButtonsPanel.add(this.filterConditions, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pButtonsPanel.add(this.buttonAdd, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
		pButtonsPanel.add(this.buttonEdit, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
		pButtonsPanel.add(this.buttonDelete, new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
		pButtonsPanel.add(this.buttonDeleteAll, new GridBagConstraints(5, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

		pButtonsPanel.setBorder(new javax.swing.border.TitledBorder(""));

		aux.add(pButtonsPanel, BorderLayout.NORTH);

		final JPanel pConfiguracion = new JPanel(new GridBagLayout());

		pConfiguracion.add(this.cdDistinct,
				new GridBagConstraints(0, 0, GridBagConstraints.RELATIVE, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

		pConfiguracion.add(this.conditionLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 5), 0, 0));
		pConfiguracion.add(this.conditions,
				new GridBagConstraints(1, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldLessDate, new GridBagConstraints(0, 2, 3, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldGreaterDate, new GridBagConstraints(3, 2, 3, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldLessInteger, new GridBagConstraints(0, 3, 3, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldGreaterInteger,
				new GridBagConstraints(3, 3, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldLessReal, new GridBagConstraints(0, 4, 3, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldGreaterReal, new GridBagConstraints(3, 4, 3, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldCheck, new GridBagConstraints(0, 5, 3, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldText, new GridBagConstraints(0, 6, 3, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		pConfiguracion.add(this.fieldInteger, new GridBagConstraints(0, 7, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

		this.dMY.add(this.cbDay);
		this.dMY.add(this.cbMonth);
		this.dMY.add(this.cbYear);

		pConfiguracion.add(this.dMY, new GridBagConstraints(1, 7, 2, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

		this.cbNull.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterDialog.this.changeCbNull();
			}
		});

		this.cbNotNull.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterDialog.this.changeCbNotNull();
			}
		});

		this.buttonAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterDialog.this.insertFilter(false);
				if (FilterDialog.this.filterList.getFilters() == null) {
					FilterDialog.this.filterConditions.setEnabled(false);
				} else {
					FilterDialog.this.filterConditions.setEnabled(true);
				}
				FilterDialog.this.clearComponents();

			}
		});

		this.buttonDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterDialog.this.filterList.deleteSelectedIndex();
				if (FilterDialog.this.filterList.getFilters() == null) {
					FilterDialog.this.filterConditions.setEnabled(false);
				} else {
					FilterDialog.this.filterConditions.setEnabled(true);
				}
			}
		});
		this.buttonDeleteAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterDialog.this.filterList.deleteAll();
			}
		});

		this.buttonEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterDialog.this.insertFilter(true);
				if (FilterDialog.this.filterList.getFilters() == null) {
					FilterDialog.this.filterConditions.setEnabled(false);
				} else {
					FilterDialog.this.filterConditions.setEnabled(true);
				}
				FilterDialog.this.clearComponents();

			}
		});

		aux.add(pConfiguracion);
		return aux;
	}

	protected JPanel getControlButtonsPanel() {
		final JPanel aux = new JPanel();

		this.buttonOk.setIcon(ImageManager.getIcon(ImageManager.OK));
		this.buttonOk.setActionCommand("ok");
		this.buttonOk.setText(ApplicationManager.getTranslation("OptionPane.okButtonText", this.bundle));

		this.buttonReset.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
		this.buttonReset.setActionCommand("reset");
		this.buttonReset.setText(ApplicationManager.getTranslation("OptionPane.cancelButtonText", this.bundle));

		aux.add(this.buttonOk, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

		aux.add(this.buttonReset, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

		this.buttonOk.addActionListener(this);

		this.buttonReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterDialog.this.setVisible(false);
			}
		});

		return aux;
	}

	protected void init(final Table table) {
		this.table = table;
		if (table != null) {
			final TableModel model = table.getJTable().getModel();
			if ((model != null) && (model instanceof TableSorter)) {
				this.sorter = (TableSorter) model;
			}
		}
		this.bundle = this.table.getResourceBundle();

		this.filterConfigurationListPanel = this.getPanelsWithFilterList();
		this.selectionFilterPanel = this.getSelectionFilterPanel();
		this.controlButtonsPanel = this.getControlButtonsPanel();

		this.columnListPanel = this.getColumnListPanel();

		this.label.setFont(this.label.getFont().deriveFont(Font.BOLD));

		this.getRootPane().setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.selectionFilterPanel.setBorder(null);

		final JPanel rightPanel = new JPanel(new GridBagLayout());

		rightPanel.add(this.filterConfigurationListPanel,
				new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		rightPanel.add(this.selectionFilterPanel, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		rightPanel.add(this.controlButtonsPanel, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		this.lInfo.setBorder(new EmptyBorder(4, 8, 4, 8));
		this.getContentPane().add(this.lInfo, BorderLayout.NORTH);
		this.getContentPane().add(this.columnListPanel, BorderLayout.WEST);
		this.getContentPane().add(rightPanel);

		class EAction extends AbstractAction {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					FilterDialog.this.buttonOk.doClick(1);
				} catch (final Exception ex) {
					FilterDialog.logger.error(null, ex);
				}
			}

		}
		this.setAction(KeyEvent.VK_ENTER, 0, new EAction(), "Accept Filter");
		this.buttonOk.setDefaultCapable(true);

		this.pack();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if ("ok".equalsIgnoreCase(e.getActionCommand())) {
			this.setVisible(false);
			if (!this.booleanClass) {
				this.insertFilter(false);
			}
			this.clearComponents();
			final Map hFilter = ObjectTools.clone(this.filterList.getFilters());
			this.sorter.resetFilter();// If more than one column change at the
			// same
			// time
			this.sorter.applyFilter(hFilter);
		} else if ("reset".equalsIgnoreCase(e.getActionCommand())) {
		}
	}

	public void splitFilter() {
		final int index = this.filterList.getSelectedIndex();
		if (index == -1) {
			this.clearComponents();
			return;
		}

		final Object oValue = this.filterList.getFilter(index);
		final Object condition = this.filterList.getCondition(index);

		final Object o = this.columnsList.getSelectedValue();
		if (o != null) {
			this.configureByTypeOfColumn(((FilterDialog.TranslatedItem) o).toString());
		}

		this.clearComponents();
		this.setLast(false);

		if (oValue instanceof DateFilter) {
			final DateFilter dateFilter = (DateFilter) oValue;
			this.fieldInteger.setValue(new Integer(dateFilter.getAmount()));
			this.conditions.setSelectedItem(this.last);
			final int type = dateFilter.getType();
			switch (type) {
			case DateFilter.DAY:
				this.cbDay.setSelected(true);
				break;
			case DateFilter.MONTH:
				this.cbMonth.setSelected(true);
				break;
			case DateFilter.YEAR:
				this.cbYear.setSelected(true);
				break;
			}
			this.setLast(true);
		} else if (oValue instanceof DifferentFilter) {
			final DifferentFilter differentFilter = (DifferentFilter) oValue;
			final Object[] oValues = differentFilter.getValues();
			this.conditions.setSelectedIndex(differentFilter.condition);
			this.field.setValue(oValues[0]);
		} else if (oValue instanceof DifferentSimpleFilter) {
			this.cdDistinct.setSelected(true);
			final DifferentSimpleFilter dsfFilter = (DifferentSimpleFilter) oValue;
			final Object v = dsfFilter.getValue();
			if (v == null) {
				this.cbNotNull.setSelected(true);
				this.changeCbNotNull();
				if (condition != null) {
					this.filterConditions.setSelectedItem(condition);
				}
			} else {
				this.field.setValue(v);
				if (condition != null) {
					this.filterConditions.setSelectedItem(condition);
				}
			}
		} else if (oValue instanceof SimpleFilter) {

			final SimpleFilter simpleFilter = (SimpleFilter) oValue;
			final Object v = simpleFilter.getValue();
			if (v == null) {
				this.cbNull.setSelected(true);
				this.changeCbNull();
				if (condition != null) {
					this.filterConditions.setSelectedItem(condition);
				}
			} else {
				this.field.setValue(v);
				if (condition != null) {
					this.filterConditions.setSelectedItem(condition);
				}
			}
		} else if (oValue instanceof Filter) {
			final Filter filter = (Filter) oValue;
			this.conditions.setSelectedIndex(filter.condition);
			final Object[] pValues = filter.getValues();
			this.field.setValue(pValues[0]);
			if (filter.condition == Filter.RANGE) {
				if (this.field == this.fieldLessInteger) {
					this.fieldGreaterInteger.setValue(pValues[1]);
				} else if (this.field == this.fieldLessDate) {
					this.fieldGreaterDate.setValue(pValues[1]);
				} else if (this.field == this.fieldLessReal) {
					this.fieldGreaterReal.setValue(pValues[1]);
				}
			}
		}
	}

	public void clearComponents() {
		this.cbNull.setSelected(false);
		this.cbNotNull.setSelected(false);
		this.cdDistinct.setSelected(false);
		this.fieldLessDate.setValue(null);
		this.fieldGreaterDate.setValue(null);
		this.fieldLessInteger.setValue(null);
		this.fieldGreaterInteger.setValue(null);
		this.fieldLessReal.setValue(null);
		this.fieldGreaterReal.setValue(null);
		this.fieldCheck.setValue(null);
		this.fieldText.setValue(null);
		this.field.setValue(null);
		this.fieldCheck.setEnabled(true);
		this.fieldText.setEnabled(true);
		this.fieldLessDate.setEnabled(true);
		this.fieldGreaterDate.setEnabled(true);
		this.fieldLessInteger.setEnabled(true);
		this.fieldGreaterInteger.setEnabled(true);
		this.fieldLessReal.setEnabled(true);
		this.fieldGreaterReal.setEnabled(true);
		this.conditions.setEnabled(true);
		this.conditionLabel.setEnabled(true);
		this.conditions.setEnabled(true);
		this.cbNull.setEnabled(true);
		this.cbNotNull.setEnabled(true);
		this.cdDistinct.setEnabled(true);

		this.cbDay.setSelected(true);
		this.fieldInteger.setValue(null);
	}

	private void changeCbNotNull() {
		if (this.cbNotNull.isSelected()) {
			this.fieldCheck.setEnabled(false);
			this.fieldText.setEnabled(false);
			this.fieldLessDate.setEnabled(false);
			this.fieldGreaterDate.setEnabled(false);
			this.fieldLessInteger.setEnabled(false);
			this.fieldGreaterInteger.setEnabled(false);
			this.fieldLessReal.setEnabled(false);
			this.fieldGreaterReal.setEnabled(false);
			this.conditions.setEnabled(false);
			this.conditionLabel.setEnabled(false);
			this.conditions.setEnabled(false);
			this.cbNull.setEnabled(false);
			this.cdDistinct.setEnabled(false);
			this.label.setEnabled(false);
		} else {
			this.fieldCheck.setEnabled(true);
			this.fieldText.setEnabled(true);
			this.fieldLessDate.setEnabled(true);
			this.fieldGreaterDate.setEnabled(true);
			this.fieldLessInteger.setEnabled(true);
			this.fieldGreaterInteger.setEnabled(true);
			this.fieldLessReal.setEnabled(true);
			this.fieldGreaterReal.setEnabled(true);
			this.conditions.setEnabled(true);
			this.conditionLabel.setEnabled(true);
			this.conditions.setEnabled(true);
			this.cbNull.setEnabled(true);
			this.cdDistinct.setEnabled(true);
			this.label.setEnabled(true);
		}
	}

	private void changeCbNull() {
		if (this.cbNull.isSelected()) {
			this.fieldCheck.setEnabled(false);
			this.fieldText.setEnabled(false);
			this.fieldLessDate.setEnabled(false);
			this.fieldGreaterDate.setEnabled(false);
			this.fieldLessInteger.setEnabled(false);
			this.fieldGreaterInteger.setEnabled(false);
			this.fieldLessReal.setEnabled(false);
			this.fieldGreaterReal.setEnabled(false);
			this.conditions.setEnabled(false);
			this.conditionLabel.setEnabled(false);
			this.conditions.setEnabled(false);
			this.cbNotNull.setEnabled(false);
			this.cdDistinct.setEnabled(false);
			this.label.setEnabled(false);
		} else {
			this.fieldCheck.setEnabled(true);
			this.fieldText.setEnabled(true);
			this.fieldLessDate.setEnabled(true);
			this.fieldGreaterDate.setEnabled(true);
			this.fieldLessInteger.setEnabled(true);
			this.fieldGreaterInteger.setEnabled(true);
			this.fieldLessReal.setEnabled(true);
			this.fieldGreaterReal.setEnabled(true);
			this.conditions.setEnabled(true);
			this.conditionLabel.setEnabled(true);
			this.conditions.setEnabled(true);
			this.cbNotNull.setEnabled(true);
			this.cdDistinct.setEnabled(true);
			this.label.setEnabled(true);
		}
	}

	protected void insertNotRangeFilter(final boolean modif) {

		final Object[] values = new Object[1];
		values[0] = this.field.getValue();
		// Added (5.2061EN-0.6)
		// Solves bug filtering <= because values[0] contains date with
		// hour,minute,second set to 0 and dates that match with upper limit
		// are discarded.
		this.checkDateDataField(values);

		if (values[0] == null) {
			return;
		}
		Filter filter = null;
		switch (this.conditions.getSelectedIndex()) {
		case Filter.LESS:
			filter = new Filter(Filter.LESS, values);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filter);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filter,
						this.filterList.getSelectedIndex());
			}
			return;
		case Filter.LESS_EQUAL:
			filter = new Filter(Filter.LESS_EQUAL, values);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filter);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filter,
						this.filterList.getSelectedIndex());
			}
			return;
		case Filter.EQUAL:
			filter = new Filter(Filter.EQUAL, values);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filter);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filter,
						this.filterList.getSelectedIndex());
			}
			return;
		case Filter.GREATER_EQUAL:
			filter = new Filter(Filter.GREATER_EQUAL, values);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filter);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filter,
						this.filterList.getSelectedIndex());
			}

			return;
		case Filter.GREATER:
			filter = new Filter(Filter.GREATER, values);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filter);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filter,
						this.filterList.getSelectedIndex());
			}
			return;
		case DifferentFilter.DIFFERENT:
			filter = new DifferentFilter(DifferentFilter.DIFFERENT, values);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filter);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filter,
						this.filterList.getSelectedIndex());
			}
			return;
		default:
			break;
		}
	}

	private void checkDateDataField(final Object[] values) {
		if ((this.field instanceof DateDataField) && (this.conditions.getSelectedIndex() == Filter.LESS_EQUAL)
				&& (values[0] instanceof Date)) {
			values[0] = new Date(((Date) values[0]).getTime() + (86399 * 1000) + 999);
		}
	}

	public void insertFilter(final boolean modif) {

		if (this.conditions.getSelectedIndex() == DateFilter.LAST) {
			this.insertLastFilter(modif);
			return;
		}

		if (this.cbNull.isSelected()) {
			this.insertNullFilter(modif);
			return;
		}

		if (this.cbNotNull.isSelected()) {
			this.insertNotNullFilter(modif);
			return;
		}

		// If text is empty then not apply the filter
		// If condition is a range then both must fulfill
		if (((this.field == this.fieldLessDate) || (this.field == this.fieldLessInteger)
				|| (this.field == this.fieldLessReal))
				&& (this.conditions
						.getSelectedIndex() != Filter.RANGE)) {
			this.insertNotRangeFilter(modif);
			return;
		}

		if (this.field == this.fieldLessDate) {
			if (this.fieldLessDate.isEmpty()) {
				return;
			}
			insertDateFilter(modif);
			return;
		}

		if ((this.conditions.getSelectedIndex() == Filter.RANGE) && (this.field == this.fieldLessInteger)) {
			this.insertRangeFilter(modif);
			return;
		}

		if ((this.conditions.getSelectedIndex() == Filter.RANGE) && (this.field == this.fieldLessReal)) {
			this.insertRangeFilter(modif);
			return;
		}

		// This is not a condition filter, it is check or string
		if (this.field.isEmpty()) {
			return;
		}
		final Object value = checkAsterisk();

		SimpleFilter f = null;

		if (this.cdDistinct.isSelected()) {
			f = new DifferentSimpleFilter(value);
		} else {
			f = new SimpleFilter(value);
		}
		if ((this.filterList.getSelectedIndex() == -1) || !modif) {
			this.filterList.addFilter(this.filterConditions.getSelectedItem(), f);
		} else {
			this.filterList.setFilter(this.filterConditions.getSelectedItem(), f, this.filterList.getSelectedIndex());
		}
	}

	protected Object checkAsterisk() {
		Object value = this.field.getValue();
		if ((value instanceof String) && ((String) value).endsWith("*")) {
		} else if (value instanceof Boolean) {
		} else {
			value = (String) value + "*";
		}
		return value;
	}

	protected void insertDateFilter(final boolean modif) {
		if (this.conditions.getSelectedIndex() == Filter.RANGE) {
			if (this.fieldGreaterDate.isEmpty()) {
				return;
			}
			final Object[] oValues = new Object[2];
			oValues[0] = this.fieldLessDate.getValue();
			oValues[1] = this.convertToEndOfDay((Timestamp) this.fieldGreaterDate.getValue());

			// Filter value
			final Filter filterValue = new Filter(Filter.RANGE, oValues);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filterValue);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filterValue,
						this.filterList.getSelectedIndex());
			}
			return;
		} else {
			return;
		}
	}

	protected void insertNotNullFilter(final boolean modif) {
		if ((this.filterList.getSelectedIndex() == -1) || !modif) {
			this.filterList.addFilter(this.filterConditions.getSelectedItem(), new DifferentSimpleFilter(null));
		} else {
			this.filterList.setFilter(this.filterConditions.getSelectedItem(), new DifferentSimpleFilter(null),
					this.filterList.getSelectedIndex());
		}
		this.filterList.clearSelection();
	}

	protected void insertLastFilter(final boolean modif) {
		int type = 0;
		Object desc = null;
		if (this.cbDay.isSelected()) {
			type = DateFilter.DAY;
		} else if (this.cbMonth.isSelected()) {
			type = DateFilter.MONTH;
		} else if (this.cbYear.isSelected()) {
			type = DateFilter.YEAR;
		}
		desc = this.fieldInteger.getValue();
		if (desc == null) {
			return;
		}
		if (!(desc instanceof Integer)) {
			return;
		}

		if ((this.filterList.getSelectedIndex() == -1) || !modif) {
			this.filterList.addFilter(this.filterConditions.getSelectedItem(),
					new DateFilter(type, ((Integer) desc).intValue(), this.bundle));
		} else {
			this.filterList.setFilter(this.filterConditions.getSelectedItem(),
					new DateFilter(type, ((Integer) desc).intValue(), this.bundle), this.filterList.getSelectedIndex());
		}
		this.filterList.clearSelection();
	}

	protected void insertNullFilter(final boolean modif) {
		if ((this.filterList.getSelectedIndex() == -1) || !modif) {
			this.filterList.addFilter(this.filterConditions.getSelectedItem(), new SimpleFilter(null));
		} else {
			this.filterList.setFilter(this.filterConditions.getSelectedItem(), new SimpleFilter(null),
					this.filterList.getSelectedIndex());
		}
		this.filterList.clearSelection();

	}

	protected void insertRangeFilter(final boolean modif) {
		if (this.field == this.fieldLessInteger) {
			if (this.fieldLessInteger.isEmpty() || this.fieldGreaterInteger.isEmpty()) {
				return;
			}
			final Object[] oValues = new Object[2];
			oValues[0] = this.fieldLessInteger.getValue();
			oValues[1] = this.fieldGreaterInteger.getValue();
			// Filter value
			final Filter filterValue = new Filter(Filter.RANGE, oValues);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filterValue);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filterValue,
						this.filterList.getSelectedIndex());
			}
		}

		if (this.field == this.fieldLessReal) {
			if (this.fieldLessReal.isEmpty() || this.fieldGreaterReal.isEmpty()) {
				return;
			}
			final Object[] oValues = new Object[2];
			oValues[0] = this.fieldLessReal.getValue();
			oValues[1] = this.fieldGreaterReal.getValue();
			// filter values
			final Filter filterValue = new Filter(Filter.RANGE, oValues);
			if ((this.filterList.getSelectedIndex() == -1) || !modif) {
				this.filterList.addFilter(this.filterConditions.getSelectedItem(), filterValue);
			} else {
				this.filterList.setFilter(this.filterConditions.getSelectedItem(), filterValue,
						this.filterList.getSelectedIndex());
			}
		}
	}


	protected Timestamp convertToEndOfDay(final Timestamp dayHour) {
		// Add 23 h, 59 min, 59 sec, 999 millis to hour
		final Calendar c = Calendar.getInstance(ApplicationManager.getLocale());
		c.setTime(new Date(dayHour.getTime()));
		c.add(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.MILLISECOND, -1);
		return new Timestamp(c.getTimeInMillis());
	}

	protected void setLast(final boolean last) {
		if (last) {
			this.dMY.setVisible(true);
			this.fieldInteger.setVisible(true);
			this.fieldLessDate.setVisible(false);
			this.label.setVisible(false);
		} else {
			this.dMY.setVisible(false);
			this.fieldInteger.setVisible(false);
			if (this.dateClass) {
				this.fieldLessDate.setVisible(true);
			}
			this.label.setVisible(true);
		}

	}

	protected void configureByTypeOfColumn(final String columnName) {
		int index = -1;
		for (int i = 0; i < this.table.getJTable().getColumnCount(); i++) {
			if (columnName.equalsIgnoreCase(this.table.getJTable().getColumnName(i))) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			this.configureByTypeOfColumn(index);
		}
	}

	protected void configureByTypeOfColumn(final int viewColumn) {
		// Look the data type
		Class columClass = this.table.getJTable().getColumnClass(viewColumn);
		this.dateClass = false;
		this.booleanClass = false;
		// If column has a reference render then class must be a String
		final ComboBoxModel model = this.conditions.getModel();
		DefaultComboBoxModel dModel = null;
		if (model instanceof DefaultComboBoxModel) {
			dModel = (DefaultComboBoxModel) model;
			final int i = dModel.getIndexOf(this.last);
			if (i >= 0) {
				dModel.removeElement(this.last);
			}
		}

		if ((this.table.getJTable().getCellRenderer(0, viewColumn) != null)
				&& (this.table.getJTable().getCellRenderer(0, viewColumn) instanceof ComboReferenceCellRenderer)) {
			columClass = String.class;
		}

		if ((columClass == java.util.Date.class) || (columClass == java.sql.Date.class)
				|| (columClass == java.sql.Timestamp.class)) {
			this.field = this.fieldLessDate;
			this.fieldCheck.setVisible(false);
			this.fieldText.setVisible(false);
			this.fieldLessDate.setVisible(true);
			this.fieldGreaterDate.setVisible(true);
			this.fieldLessInteger.setVisible(false);
			this.fieldGreaterInteger.setVisible(false);
			this.fieldLessReal.setVisible(false);
			this.fieldGreaterReal.setVisible(false);
			this.conditions.setVisible(true);
			this.conditionLabel.setVisible(true);
			this.cdDistinct.setVisible(false);
			this.conditions.setSelectedIndex(Filter.RANGE);
			this.setLast(false);
			if (dModel != null) {
				dModel.addElement(this.last);
			}
			this.dateClass = true;
			this.fieldInteger.setVisible(false);
		} else if ((columClass == Integer.class) || (columClass == Long.class)) {
			this.field = this.fieldLessInteger;
			this.fieldCheck.setVisible(false);
			this.fieldText.setVisible(false);
			this.fieldLessDate.setVisible(false);
			this.fieldGreaterDate.setVisible(false);
			this.fieldLessInteger.setVisible(true);
			this.fieldGreaterInteger.setVisible(true);
			this.fieldLessReal.setVisible(false);
			this.fieldGreaterReal.setVisible(false);
			this.conditions.setVisible(true);
			this.conditionLabel.setVisible(true);
			this.conditions.setSelectedIndex(Filter.RANGE);
			this.setLast(false);
			this.fieldInteger.setVisible(false);
		} else if ((columClass == Number.class) || (columClass == Float.class) || (columClass == Double.class)
				|| (columClass == BigDecimal.class) || (columClass == BigInteger.class)) {
			this.field = this.fieldLessReal;
			this.fieldCheck.setVisible(false);
			this.fieldText.setVisible(false);
			this.fieldLessDate.setVisible(false);
			this.fieldGreaterDate.setVisible(false);
			this.fieldLessInteger.setVisible(false);
			this.fieldGreaterInteger.setVisible(false);
			this.fieldLessReal.setVisible(true);
			this.fieldGreaterReal.setVisible(true);
			this.conditions.setVisible(true);
			this.conditionLabel.setVisible(true);
			this.cdDistinct.setVisible(false);
			this.conditions.setSelectedIndex(Filter.RANGE);

			this.setLast(false);
			this.fieldInteger.setVisible(false);
		} else if (columClass == Boolean.class) {
			this.field = this.fieldCheck;
			this.fieldCheck.setVisible(true);
			this.fieldText.setVisible(false);
			this.fieldLessDate.setVisible(false);
			this.fieldGreaterDate.setVisible(false);
			this.fieldLessInteger.setVisible(false);
			this.fieldGreaterInteger.setVisible(false);
			this.fieldLessReal.setVisible(false);
			this.fieldGreaterReal.setVisible(false);
			this.conditions.setVisible(false);
			this.cdDistinct.setVisible(false);
			this.conditionLabel.setVisible(false);
			this.setLast(false);
			this.fieldInteger.setVisible(false);
			this.booleanClass = true;
		} else {// Like strings
			this.field = this.fieldText;
			this.fieldText.getLabelComponent()
			.setText(ApplicationManager.getTranslation("table.value", this.bundle) + " ");
			this.fieldText.setValue("");
			this.fieldCheck.setVisible(false);
			this.fieldText.setVisible(true);
			this.fieldLessDate.setVisible(false);
			this.fieldGreaterDate.setVisible(false);
			this.fieldLessInteger.setVisible(false);
			this.fieldGreaterInteger.setVisible(false);
			this.fieldLessReal.setVisible(false);
			this.fieldGreaterReal.setVisible(false);
			this.conditions.setVisible(false);
			this.conditionLabel.setVisible(false);
			this.cdDistinct.setVisible(true);
			this.setLast(false);
			this.fieldInteger.setVisible(false);
		}
		// pack();
	}

	protected String getFilterInfo() {
		final Map hFilters = ((FilterModel) this.filterList.getModel()).getFilters();
		if (hFilters.isEmpty()) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		final Enumeration enumKeys = Collections.enumeration(hFilters.keySet());
		int i = 0;
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			sb.append(ApplicationManager.getTranslation((String) oKey, this.bundle));
			sb.append(" '" + hFilters.get(oKey) + "'");
			if (i < (hFilters.size() - 1)) {
				sb.append(", ");
			}
			i++;
		}
		return sb.toString();
	}

	protected void setColumn(final String column) {
		this.filterList.setColumn(column);
	}

	public void updateFilterInfo() {
		this.lInfo
		.setText(ApplicationManager.getTranslation("table.filter", this.bundle) + ": " + this.getFilterInfo());
		this.lInfo.setToolTipText(this.lInfo.getToolTipText());
	}

	public void show(final MouseEvent e) {
		// Search the column that is the event source
		final TableColumnModel columnModel = this.table.getJTable().getTableHeader().getColumnModel();
		final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
		if (viewColumn < 0) {
			return;
		}
		this.column = this.table.getJTable().convertColumnIndexToModel(viewColumn);
		final Object col = this.table.getJTable().getModel().getColumnName(this.column);
		if (col == null) {
			return;
		}

		if (col.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
			return;
		}

		this.columnsList.setSelectedValue(new TranslatedItem((String) col), true);

		this.filters = this.sorter.getFilters();
		this.filterList.setFilters(ObjectTools.clone(this.filters), (String) col);
		this.configureByTypeOfColumn(viewColumn);
		this.filterList.getModel().addListDataListener(new ListDataListener() {

			@Override
			public void contentsChanged(final ListDataEvent e) {
				FilterDialog.this.columnsList.repaint();
				FilterDialog.this.updateFilterInfo();
			}

			@Override
			public void intervalAdded(final ListDataEvent e) {
				FilterDialog.this.columnsList.repaint();
				FilterDialog.this.updateFilterInfo();
			}

			@Override
			public void intervalRemoved(final ListDataEvent e) {
				FilterDialog.this.columnsList.repaint();
				FilterDialog.this.updateFilterInfo();
			}
		});
		this.updateFilterInfo();
		final Dimension pref = this.getPreferredSize();
		final int w = pref.width;
		final int h = pref.height;
		final Dimension d = this.getSize();
		if (d.width < w) {
			d.width = w;
		}
		if (d.height < h) {
			d.height = h;
		}
		this.setSize(d);
		final Point p = new Point(
				Math.min(e.getX(),
						Toolkit.getDefaultToolkit().getScreenSize().width
						- ((JComponent) e.getSource()).getLocationOnScreen().x - this.getWidth()),
				Math.min(e.getY(), Toolkit.getDefaultToolkit().getScreenSize().height
						- ((JComponent) e.getSource()).getLocationOnScreen().y - this.getHeight()));
		final int x = ((Component) e.getSource()).getLocationOnScreen().x;
		final int y = ((Component) e.getSource()).getLocationOnScreen().y;
		super.setLocation(x + p.x, y + p.y);
		super.setVisible(true);
	}

	@Override
	protected void setInitialFocus() {
		if (this.field != null) {
			this.field.requestFocus();
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	protected void applyResourceBundle() {
		String sText = this.WITHOUTDATA;
		sText = ApplicationManager.getTranslation(sText, this.bundle);
		this.cbNull.setText(sText);

		sText = this.WITHDATA;
		sText = ApplicationManager.getTranslation(sText, this.bundle);
		this.cbNotNull.setText(sText);

		this.buttonAdd.setToolTipText(ApplicationManager.getTranslation("table.add_filter", this.bundle));
		this.buttonDelete.setToolTipText(ApplicationManager.getTranslation("table.delete_filter", this.bundle));

		if (this.bundle != null) {
			this.columnsList.setCellRenderer(new FilterListRenderer(this.bundle));
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		try {
			if (res != null) {
				this.setTitle(res.getString(TableSorter.filterKey));
				this.bundle = res;
				this.applyResourceBundle();
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				FilterDialog.logger.debug(e.getMessage(), e);
			}
		}

		this.conditions.setRenderer(new I18nListCellRenderer(res));

		this.conditionLabel.setText(ApplicationManager.getTranslation("filtercomponent.condition", res));
		this.cdDistinct.setText(ApplicationManager.getTranslation(this.DISTINCT, res));

		this.fieldCheck.setResourceBundle(res);
		this.fieldLessDate.setResourceBundle(res);
		this.fieldGreaterDate.setResourceBundle(res);
		this.fieldLessInteger.setResourceBundle(res);
		this.fieldGreaterInteger.setResourceBundle(res);
		this.fieldLessReal.setResourceBundle(res);
		this.fieldGreaterReal.setResourceBundle(res);
		this.fieldText.setResourceBundle(res);

		this.columnPanel.setBorder(
				new javax.swing.border.TitledBorder(ApplicationManager.getTranslation(this.COLUMN, this.bundle)));
		this.filterPanel.setBorder(
				new javax.swing.border.TitledBorder(ApplicationManager.getTranslation(this.FILTER, this.bundle)));
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.fieldCheck.setComponentLocale(l);
		this.fieldLessDate.setComponentLocale(l);
		this.fieldGreaterDate.setComponentLocale(l);
		this.fieldLessInteger.setComponentLocale(l);
		this.fieldGreaterInteger.setComponentLocale(l);
		this.fieldLessReal.setComponentLocale(l);
		this.fieldGreaterReal.setComponentLocale(l);
		this.fieldText.setComponentLocale(l);
	}

}
