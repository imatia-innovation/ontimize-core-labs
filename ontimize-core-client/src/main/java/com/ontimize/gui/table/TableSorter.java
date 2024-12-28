package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.TextComponent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.table.TableSorter.GroupTableModel.GroupItem;
import com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.util.ArrayUtils;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.ParseUtils;

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel) and itself implements
 * TableModel. TableSorter does not store or copy the data in the TableModel, instead it maintains
 * an array of integers which it keeps the same size as the number of rows in its model. When the
 * model changes it notifies the sorter that something has changed e.g. "rowsAdded" so that its
 * internal array of integers can be reallocated. As requests are made of the sorter (like
 * getValueAt(row, col) it redirects them to its model via the mapping array. That way the
 * TableSorter appears to hold another copy of the table with the rows in a different order. The
 * sorting algorithm used is stable which means that it does not move around rows when its
 * comparison function returns 0 to denote that they are equivalent. The sorter presents as well
 * filtering and grouping features. That means that the TableSorter has a GroupTableModel inside
 * that provides the grouping functionality and the GroupTableModel itself has a FilterTableModel
 * inside. The table row indexes corresponds to the indexes in the TableSorter. Because if the
 * sorting and grouping behaviors, the row indexes in the view might not be equal to the indexes in
 * the table model, that is, the more internal model of those.
 */
public class TableSorter extends TableMap implements Sortable, Freeable, Internationalization, OTableModel {

	private static final Logger logger = LoggerFactory.getLogger(TableSorter.class);

	protected boolean localSorter = true;

	/**
	 * A grouping type configuration parameter
	 */
	public static final int YEAR = 0;

	/**
	 * A grouping type configuration parameter
	 */
	public static final int YEAR_MONTH = 1;

	/**
	 * A grouping type configuration parameter
	 */
	public static final int YEAR_MONTH_DAY = 2;

	/**
	 * A grouping type configuration parameter
	 */
	public static final int QUARTER_YEAR = 3;

	/**
	 * A grouping type configuration parameter
	 */
	public static final int QUARTER = 4;

	/**
	 * A grouping type configuration parameter
	 */
	public static final int MONTH = 5;

	/**
	 * A grouping type configuration parameter
	 */
	public static final int WEEK_YEAR = 6;

	/**
	 * Filtering preference key
	 */
	public static final String filterKey = "table.filter";

	/**
	 * Grouping preference key
	 * @deprecated
	 */
	@Deprecated
	public static final String groupKey = "AgruparPorEstaColumna";

	public static boolean DEBUG = false;

	public ResourceBundle bundle = null;

	/**
	 * Allows or denies sorting the table by several columns
	 */
	public static boolean MULTIORDER_PERMIT = true;

	/**
	 * Grouping operation type
	 */
	public static final int SUM = 0;

	/**
	 * Grouping operation type
	 */
	public static final int AVG = 1;

	/**
	 * Grouping operation type
	 */
	public static final int MAX = 2;

	/**
	 * Grouping operation type
	 */
	public static final int MIN = 3;

	/**
	 * Grouping operation type
	 */
	public static final int COUNT = 4;

	/**
	 * Class that contains date information with day, month and year, to allow fast comparisons when
	 * sorting.
	 */

	protected static class DayMonthYear implements Comparable {

		protected String rep = null;

		protected int d = 0;

		protected int m = 0;

		protected int y = 0;

		protected int t = 0;

		protected long time = 0;

		protected int w = 0;

		public DayMonthYear(final int d, final int m, final int y, final long time) {
			this.d = d;
			this.m = m + 1;
			this.y = y;
			this.time = time;
		}

		public long getTime() {
			return this.time;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) {
				return true;
			} else if (o instanceof DayMonthYear) {
				final DayMonthYear dmy = (DayMonthYear) o;
				return (this.d == dmy.d) && (this.m == dmy.m) && (this.y == dmy.y) && (this.t == dmy.t)
						&& (this.w == dmy.w);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public String toString() {
			if (this.rep == null) {
				this.rep = (this.d > 9 ? "" + this.d : "0" + this.d) + "/" + (this.m > 9 ? "" + this.m : "0" + this.m)
						+ "/" + this.y;
			}
			return this.rep;
		}

		@Override
		public int compareTo(final Object o) {
			if (o == null) {
				throw new NullPointerException();
			}
			if (this.equals(o)) {
				return 0;
			}
			if (o instanceof DayMonthYear) {
				final DayMonthYear dmY = (DayMonthYear) o;
				if (this.y > dmY.y) {
					return 1;
				} else if (this.y < dmY.y) {
					return -1;
				}

				if (this.m > dmY.m) {
					return 1;
				} else if (this.m < dmY.m) {
					return -1;
				}

				if (this.d > dmY.d) {
					return 1;
				} else if (this.d < dmY.d) {
					return -1;
				}

				if (this.t > dmY.t) {
					return 1;
				} else if (this.t < dmY.t) {
					return -1;
				}

				if (this.w > dmY.w) {

					return 1;
				} else if (this.w < dmY.w) {
					return -1;

					// int w = this.w;
					// int y = this.y;
					// boolean i = (((((DayMonthYear) o).w == w) && (y ==
					// ((DayMonthYear) o).y)) || ((w == 1) && (y ==
					// ((DayMonthYear) o).y)) || ((((DayMonthYear) o).w == 1) &&
					// (((DayMonthYear) o).y == (y + 1))));
					// if (i) {
					// return 0;
					// } else {
					// if (((DayMonthYear) o).y == y) {
					// if (((DayMonthYear) o).w < w) {
					// return 1;
					// } else {
					// return -1;
					// }
					// } else if (((DayMonthYear) o).y < y) {
					// return 1;
					// } else {
					// return -1;
					// }
					// }

				}

				return 0;
			} else {
				throw new ClassCastException(this.getClass().getName() + " Object type is different that DayMonthYear");
			}
		}

	}

	/**
	 * Class that constains date information with week and year, to allow fast comparision when sorting.
	 */
	protected static class WeekYear extends DayMonthYear {

		public WeekYear(final int d, final int m, final int y, final long time) {
			super(-1, -1, y, time);
			final Calendar cal = Calendar.getInstance();
			cal.set(y, m, d, 0, 0, 0);
			this.w = cal.get(Calendar.WEEK_OF_YEAR);
			if ((m == 0) && (this.w > 10)) {
				this.y = y - 1;
			}
		}

		@Override
		public String toString() {
			if (this.rep == null) {
				this.rep = (this.w > 9 ? "" + this.w : "0" + this.w) + " (" + this.y + ")";
			}
			return this.rep;
		}

		@Override
		public int compareTo(final Object o) {
			if (o == null) {
				throw new NullPointerException();
			}
			if (this.equals(o)) {
				return 0;
			}
			if (o instanceof DayMonthYear) {
				final int w = this.w;
				final int y = this.y;
				final boolean i = ((((DayMonthYear) o).w == w) && (y == ((DayMonthYear) o).y))
						|| ((w == 1) && (y == ((DayMonthYear) o).y))
						|| ((((DayMonthYear) o).w == 1) && (((DayMonthYear) o).y == (y + 1)));
				if (i) {
					return 0;
				} else {
					if (((DayMonthYear) o).y == y) {
						if (((DayMonthYear) o).w < w) {
							return 1;
						} else {
							return -1;
						}
					} else if (((DayMonthYear) o).y < y) {
						return 1;
					} else {
						return -1;
					}
				}
			} else {
				throw new ClassCastException(this.getClass().getName() + " Object type is different that DayMonthYear");
			}
		}

	}

	/**
	 * Class that contains date information with month and year, to allow fast comparisons when sorting.
	 */
	protected static class MonthYear extends DayMonthYear {

		public MonthYear(final int m, final int y, final long time) {
			super(-1, m, y, time);
		}

		@Override
		public String toString() {
			if (this.rep == null) {
				this.rep = (this.m > 9 ? "" + this.m : "0" + this.m) + "/" + this.y;
			}
			return this.rep;
		}

	}

	/**
	 * Class that contains date information, concretely dates, to allow fast comparisons when sorting.
	 */
	protected static class Month extends MonthYear {

		public Month(final int m, final long time) {
			super(m, -1, time);
		}

		@Override
		public String toString() {
			if (this.rep == null) {
				this.rep = this.m > 9 ? "" + this.m : "0" + this.m;
			}
			return this.rep;
		}

	}

	/**
	 * Class that contains date information, concretely months, to allow fast comparisons when sorting.
	 */
	protected static class Year extends MonthYear {

		public Year(final int y, final long time) {
			super(-1, y, time);
		}

		@Override
		public String toString() {
			if (this.rep == null) {
				this.rep = "" + this.y;
			}
			return this.rep;
		}

	}

	/**
	 * Class that contains date information, concretely quarters, to allow fast comparisons when
	 * sorting.
	 */
	protected static class Quarter extends QuarterYear {

		public Quarter(final int m, final long time) {
			super(m, -1, time);
		}

		@Override
		public String toString() {
			if (this.rep == null) {
				switch (this.t) {
				case 1:
					this.rep = "1,2,3";
					break;
				case 2:
					this.rep = "4,5,6";
					break;
				case 3:
					this.rep = "7,8,9";
					break;
				case 4:
					this.rep = "10,11,12";
					break;
				}
			}
			return this.rep;
		}

	}

	/**
	 * Class that contains date information, concretely quarters and years, to allow fast comparisons
	 * when sorting.
	 */
	protected static class QuarterYear extends MonthYear {

		public QuarterYear(final int m, final int y, final long time) {
			super(-1, y, time);
			switch (m) {
			case 0:
			case 1:
			case 2:
				this.t = 1;
				break;
			case 3:
			case 4:
			case 5:
				this.t = 2;
				break;
			case 6:
			case 7:
			case 8:
				this.t = 3;
				break;
			case 9:
			case 10:
			case 11:
				this.t = 4;
				break;
			}
		}

		@Override
		public String toString() {
			if (this.rep == null) {
				switch (this.t) {
				case 1:
					this.rep = "1,2,3/" + this.y;
					break;
				case 2:
					this.rep = "4,5,6/" + this.y;
					break;
				case 3:
					this.rep = "7,8,9/" + this.y;
					break;
				case 4:
					this.rep = "10,11,12/" + this.y;
					break;
				}
			}
			return this.rep;
		}

	}

	/**
	 * Class that stores the cell values when these are obtained by groping several rows into one. In
	 * this concrete case, the values must be dates, in the format {@link #DayMonthYear}
	 */
	protected static class ValueByGroupDate extends ValueByGroup {

		public ValueByGroupDate(final DayMonthYear v, final int row) {
			super(v, row);
		}

	}

	/**
	 * Class that stores the cell values when these are obtained by groping several rows into one.
	 */
	public static class ValueByGroup {

		int number = 0;

		int[] rows = new int[0];

		Object value;

		public ValueByGroup(final Object v, final int row) {
			this.value = v;
			this.increment(row);
		}

		public void increment(final int row) {
			this.number++;
			final int[] temp = new int[this.rows.length + 1];
			System.arraycopy(this.rows, 0, temp, 0, this.rows.length);
			temp[this.rows.length] = row;
			this.rows = temp;
		}

		public int[] getGroupRows() {
			return this.rows;
		}

		public int getElementCount() {
			return this.number;
		}

		public Object getValue() {
			return this.value;
		}

		@Override
		public boolean equals(final Object o) {
			if (o != null) {
				if (o instanceof ValueByGroup) {
					if ((this.value == null) && (((ValueByGroup) o).getValue() == null)) {
						return true;
					} else if ((this.value != null) && (((ValueByGroup) o).getValue() != null)) {
						return ((ValueByGroup) o).getValue().equals(this.value);
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else if (this.value == null) {
				return true;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public String toString() {
			if (this.value == null) {
				return "";
			} else {
				return this.value.toString();
			}
		}

	}

	public class GroupTableModel extends TableMap {

		protected int groupColumn = -1;

		protected boolean group = false;

		protected int type = 0;

		protected String noGroupFieldValue = null;

		// Map id column, id function
		protected Map<Integer, Integer> functionsColumns = new HashMap();

		// Map id column, id function
		protected Map<Integer, Integer> defaultFunctionsColumns = new HashMap();

		/**
		 * In general any column can be used to group, but the grouping result will have sense only for
		 * numerical values (average, sum,...). In the columns which are not numerical, the resulted shown
		 * value will be a list containing the different values contained in the cells.
		 */

		protected Object[][] groupData = new Object[0][0];

		protected Map operationColumn = null;

		protected List sumColumn = null;

		protected Object getColumnOperation(final String columnIdentifier, final String operation) {
			return ((FilterTableModel) this.model).getColumnOperation(columnIdentifier, operation);
		}

		public void setFilterValidator(final FilterValidator f) {
			((FilterTableModel) this.model).setFilterValidator(f);
		}

		public FilterValidator getFilterValidator() {
			return ((FilterTableModel) this.model).getFilterValidator();
		}

		public void setDefaultGroupedColumnFunction(final int col, final int function) {
			this.defaultFunctionsColumns.put(col, function);
		}

		public void setGroupedColumnFunction(final int col, final int function) {
			this.functionsColumns.put(col, function);
			this.recalculateGroupedData();
		}

		public int getGroupColumnFunction(final int col) {
			if (this.isGrouped(col)) {
				return -1;
			}
			if (this.functionsColumns.containsKey(col)) {
				return this.functionsColumns.get(col);
			}

			if (this.defaultFunctionsColumns.containsKey(col)) {
				return this.defaultFunctionsColumns.get(col);
			}

			if ((this.getColumnClass(col).getSuperclass() == Number.class)
					&& (!(TableSorter.this.sourceTable.getColumnModel()
							.getColumn(col)
							.getCellRenderer() instanceof ComboReferenceCellRenderer))) {
				return TableSorter.SUM;
			}
			return -1;
		}

		public void setOperationColumns(final Map operationColumns) {
			this.operationColumn = operationColumns;
			final Enumeration enumKeys = Collections.enumeration(this.operationColumn.keySet());
			this.sumColumn.clear();
			while (enumKeys.hasMoreElements()) {
				final Object o = enumKeys.nextElement();
				this.sumColumn.add(o);
			}
			this.fireTableDataChanged();
		}

		public Map getCalculatedColumns() {
			return ((FilterTableModel) this.model).getCalculatedColumns();
		}

		public List getCalculatedColumnsName() {
			return ((FilterTableModel) this.model).getCalculatedColumnsName();
		}

		public List getRequiredColumnsToCalculatedColumns() {
			return ((FilterTableModel) this.model).getRequiredColumnsToCalculatedColumns();
		}

		public Object getCalculatedValue(final int column, final Map rowValues) {
			return ((FilterTableModel) this.model).getCalculatedValue(column, rowValues);
		}

		public String getCalculatedColumnExpression(final String col) {
			return ((FilterTableModel) this.model).getCalculatedColumnExpression(col);
		}

		public void setCalculatedColumnExpression(final String col, final String expression) {
			((FilterTableModel) this.model).setCalculatedColumnExpression(col, expression);
		}

		public int getGroupColumn() {
			if (this.group) {
				return this.groupColumn;
			}
			return -1;
		}

		public List getFilteredColumns() {
			return ((FilterTableModel) this.model).getFiltersColumns();
		}

		public GroupTableModel(final FilterTableModel m) {
			TableSorter.this.operations = new Hashtable();
			// register usual operations
			TableSorter.this.operations.put(new Integer(TableSorter.SUM), new SumGroupOperation());
			TableSorter.this.operations.put(new Integer(TableSorter.MIN), new MinGroupOperation());
			TableSorter.this.operations.put(new Integer(TableSorter.MAX), new MaxGroupOperation());
			TableSorter.this.operations.put(new Integer(TableSorter.AVG), new AvgGroupOperation());
			TableSorter.this.operations.put(new Integer(TableSorter.COUNT), new CountGroupOperation());
			this.setModel(m);
		}

		public void setDatos(final Map data) {
			((FilterTableModel) this.model).setData(data);
		}

		public boolean isGrouped(final int col) {
			return this.group && (this.groupColumn == col);
		}

		public boolean isGrouped() {
			return this.group;
		}

		public void deleteRows(final int[] rows) {
			if (!this.group) {
				((FilterTableModel) this.model).deleteRows(rows);
			} else {
				throw new IllegalArgumentException(this.getClass().toString()
						+ "deleteRows() cannot be called with table in grouped mode. It is due to relation is 1-n.");
			}
		}

		public void deleteRow(final int f) {
			if (!this.group) {
				((FilterTableModel) this.model).deleteRow(f);
			} else {
				throw new IllegalArgumentException(this.getClass().toString()
						+ "deleteRow() cannot be called with table in grouped mode. It is due to relation is 1-n.");
			}
		}

		public void updateRowData(final Map kv, final List v) {
			if (!this.group) {
				((FilterTableModel) this.model).updateRowData(kv, v);
			} else {
				throw new IllegalArgumentException(this.getClass().toString()
						+ "updateRowData() cannot be called with table in grouped mode. It is due to relation is 1-n.");
			}
		}

		public void updateRowData(final Map data, final Map keys) {
			if (!this.group) {
				((FilterTableModel) this.model).updateRowData(data, keys);
			} else {
				throw new IllegalArgumentException(this.getClass().toString()
						+ "updateRowData() cannot be called with table in grouped mode. It is due to relation is 1-n.");
			}
		}

		public void updateRowData(final Map rowData, final List columns, final Map keys) {
			if (!this.group) {
				((FilterTableModel) this.model).updateRowData(rowData, columns, keys);
			} else {
				throw new IllegalArgumentException(this.getClass().toString()
						+ "updateRowData() cannot be called with table in grouped mode. It is due to relation is 1-n.");
			}
		}

		public void applyFilter(final int c, final Object f) {
			((FilterTableModel) this.model).applyFilter(c, f);
		}

		public boolean lastFilterOr() {
			return ((FilterTableModel) this.model).lastFilterOr();
		}

		public void applyFilter(final Map filters) {
			((FilterTableModel) this.model).applyFilter(filters);
		}

		public void applyFilter(final Map filters, final boolean or) {
			((FilterTableModel) this.model).applyFilter(filters, or);
		}

		public void addRow(final Map kv) {
			if (this.group) {
				this.resetGroup();
			}
			((FilterTableModel) this.model).addRow(kv);
		}

		public void addRow(final int f, final Map kv) {
			if (this.group) {
				this.resetGroup();
			}
			((FilterTableModel) this.model).addRow(f, kv);
		}

		public void addRows(final List v) {
			if (this.group) {
				this.resetGroup();
			}
			((FilterTableModel) this.model).addRows(v);
		}

		public void addRows(final int[] f, final List v) {
			if (this.group) {
				this.resetGroup();
			}
			((FilterTableModel) this.model).addRows(f, v);
		}

		public boolean isFiltered(final int col) {
			return ((FilterTableModel) this.model).isFiltered(col);
		}

		public boolean isFiltered() {
			return ((FilterTableModel) this.model).isFiltered();
		}

		public TableCellRenderer getSumCellRenderer(final boolean currency) {
			return ((FilterTableModel) this.model).getSumCellRenderer(currency);
		}

		public void setEditableColumn(final String col) {
			((FilterTableModel) this.model).setEditableColumn(col);
		}

		public void setEditableColumn(final String col, final boolean editable) {
			((FilterTableModel) this.model).setEditableColumn(col, editable);
		}

		public Object getColumnIdentifier(final int col) {
			return ((FilterTableModel) this.model).getColumnIdentifier(col);
		}

		public List getColumnsText() {
			return ((FilterTableModel) this.model).getColumnText();
		}

		public List getColumnNames() {
			return ((FilterTableModel) this.model).getColumnNames();
		}

		public void addColumn(final String col) {
			((FilterTableModel) this.model).addColumn(col);
		}

		public void addColumn(final String col, final boolean event) {
			((FilterTableModel) this.model).addColumn(col, event);
		}

		public void addCalculatedColumn(final String col, final String expression) {
			((FilterTableModel) this.model).addCalculatedColumn(col, expression);
		}

		public void addTotalRowOperation(final TotalRowOperation operation) {
			((FilterTableModel) this.model).addTotalRowOperation(operation);
		}

		public List getTotalRowOperation() {
			return ((FilterTableModel) this.model).getTotalRowOperation();
		}

		public void deleteColumn(final String col, final boolean fireEvent) {
			((FilterTableModel) this.model).deleteColumn(col, fireEvent);
		}

		public void deleteColumn(final String col) {
			((FilterTableModel) this.model).deleteColumn(col);
		}

		public void deleteCalculatedColumn(final String col) {
			((FilterTableModel) this.model).deleteCalculatedColumn(col);
		}

		public int getCurrentRowCount() {
			if (this.group) {
				return this.getRowCount();
			} else {
				return ((FilterTableModel) this.model).getCurrentRowCount();
			}
		}

		public int getRealRecordNumber() {
			return ((FilterTableModel) this.model).getRealRecordNumber();
		}

		public Map getData() {
			return ObjectTools.clone(((FilterTableModel) this.model).getData());
		}

		public Map getRowData(final int f) {
			return ((FilterTableModel) this.model).getRowData(f);
		}

		public Map getRowDataForKeys(final List keys, final Map keysValues) {
			return ((FilterTableModel) this.model).getRowDataForKeys(keys, keysValues);
		}

		public Map getRowData(final int[] f) {
			return ((FilterTableModel) this.model).getRowData(f);
		}

		public Map getGroupRowData(final int row) {
			if (this.isGrouped()) {
				if (this.groupColumn >= 0) {
					final Object va = this.getValueAt(row, this.groupColumn);
					if (va instanceof ValueByGroup) {
						final int[] rows = ((ValueByGroup) va).getGroupRows();
						return this.getRowData(rows);
					}
				}
				return null;
			} else {
				return null;
			}
		}

		public Map getCalculatedRowData(final int row) {
			if (TableSorter.this.isInsertingRow(row)) {

				// Get the calculated values for inserting row (The inserting
				// row exist only in this data model but not in the others)
				final List calculatedColumnsName = this.getCalculatedColumnsName();
				if (calculatedColumnsName != null) {
					final Map result = new Hashtable();
					for (int i = 0; i < this.getColumnCount(); i++) {
						final String columnName = this.getColumnName(i);
						if (calculatedColumnsName.contains(columnName)) {
							final Object calculatedValue = this.getCalculatedValue(i, TableSorter.this.insertingRowData);
							if (calculatedValue != null) {
								result.put(columnName, calculatedValue);
							}
						}
					}
					return result;
				}
				return null;
			}

			return ((FilterTableModel) this.model).getCalculatedRowData(row);
		}

		public void resetFilter() {
			((FilterTableModel) this.model).resetFilter();
		}

		public void resetFilter(final String col) {
			((FilterTableModel) this.model).resetFilter(col);
		}

		public int convertRowIndexToModel(final int f) {
			if (!this.group) {
				return ((FilterTableModel) this.model).convertRowIndexToModel(f);
			} else {
				throw new IllegalArgumentException(
						this.getClass().toString()
						+ "convertRowIndexToModel() cannot be called with table in grouped mode. It is due to relation is 1-n.");
			}
		}

		public Map getFilteredData() {
			if (this.group) {
				final Map hFilteredData = new Hashtable();
				for (int i = 0; i < this.getColumnCount(); i++) {
					final Object id = this.getColumnIdentifier(i);
					final List vData = new Vector();
					for (int j = 0; j < this.getRowCount(); j++) {
						vData.add(j, this.getValueAt(j, i));
					}
					hFilteredData.put(id, vData);
				}
				return hFilteredData;
			}
			return ((FilterTableModel) this.model).getFilteredData();
		}

		public class GroupItem implements Serializable {

			protected Object value;

			protected String renderedString;

			public GroupItem(final Object value, final String renderedString) {
				this.value = value;
				this.renderedString = renderedString;
			}

			public Object getValue() {
				return this.value;
			}

			public void setValue(final Object value) {
				this.value = value;
			}

			public String getRenderedString() {
				return this.renderedString;
			}

			public void setRenderedString(final String renderedString) {
				this.renderedString = renderedString;
			}

			@Override
			public String toString() {
				return this.renderedString;
			}

		}

		class GroupList extends ArrayList {

			String description = null;

			public boolean containsGroupItem(final GroupItem gItem) {

				for (int i = 0; i < this.size(); i++) {
					final Object actualObject = this.get(i);
					if (actualObject instanceof GroupItem) {
						final GroupItem actualGroup = (GroupItem) actualObject;
						if ((actualGroup.getValue() == null) || (gItem.getValue() == null)) {
							return true;
						}

						if ((actualGroup.getValue() != null)
								&& actualGroup.getValue().toString().equals(gItem.getValue().toString())
								&& actualGroup.getRenderedString()
								.equals(gItem.getRenderedString())) {
							return true;
						}
					}
				}
				return false;
			}

			public void setDescription() {
				final StringBuilder buffer = new StringBuilder();
				final int s = this.size();
				for (int i = 0; i < s; i++) {
					if (this.get(i) == null) {
						buffer.append(" ");
					} else {
						if (this.get(i) instanceof GroupItem) {
							buffer.append(((GroupItem) this.get(i)).getRenderedString());
						} else {
							buffer.append(this.get(i));
						}
					}
					if ((i + 1) < s) {
						buffer.append(", ");
					}
				}
				this.description = buffer.toString();
			}

			@Override
			public String toString() {
				if (this.description != null) {
					return this.description;
				}
				final StringBuilder buffer = new StringBuilder();
				final int s = this.size();
				for (int i = 0; i < s; i++) {
					if (this.get(i) == null) {
						buffer.append(" ");
					} else {
						buffer.append(this.get(i));
					}
					if ((i + 1) < s) {
						buffer.append(", ");
					}
				}
				return buffer.toString();
			}

		}

		protected Object getNotNumericalGroupValues(final Object rowValue, final int column, final int type) {
			final TableCellRenderer render = ((EJTable) TableSorter.this.sourceTable).getNoGroupedCellRenderer(0,
					TableSorter.this.sourceTable.convertColumnIndexToView(column));

			// if (!(render != null && render instanceof
			// ComboReferenceCellRenderer )) {
			// render = null;
			// }

			final GroupList temp = new GroupList();
			final int rows = super.getRowCount();
			for (int j = 0; j < rows; j++) {
				Object rowValueJ = null;
				if (rowValue instanceof ValueByGroupDate) {
					final Object o = super.getValueAt(j, this.groupColumn);
					Date vD = null;
					if (o != null) {
						vD = (Date) o;
						this.calendar.setTime(vD);
					}
					if (vD != null) {
						switch (type) {
						case TableSorter.YEAR_MONTH_DAY:
							rowValueJ = new ValueByGroupDate(new DayMonthYear(
									this.calendar.get(Calendar.DAY_OF_MONTH), this.calendar.get(Calendar.MONTH),
									this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()), j);
							break;
						case TableSorter.YEAR_MONTH:
							rowValueJ = new ValueByGroupDate(
									new MonthYear(this.calendar.get(Calendar.MONTH),
											this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
									j);
							break;
						case TableSorter.MONTH:
							rowValueJ = new ValueByGroupDate(
									new Month(this.calendar.get(Calendar.MONTH), this.calendar.getTimeInMillis()),
									j);
							break;
						case TableSorter.YEAR:
							rowValueJ = new ValueByGroupDate(
									new Year(this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()), j);
							break;
						case TableSorter.QUARTER_YEAR:
							rowValueJ = new ValueByGroupDate(
									new QuarterYear(this.calendar.get(Calendar.MONTH),
											this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
									j);
							break;
						case TableSorter.QUARTER:
							rowValueJ = new ValueByGroupDate(
									new Quarter(this.calendar.get(Calendar.MONTH), this.calendar.getTimeInMillis()),
									j);
							break;
						case TableSorter.WEEK_YEAR:
							rowValueJ = new ValueByGroupDate(new WeekYear(this.calendar.get(Calendar.DAY_OF_MONTH),
									this.calendar.get(Calendar.MONTH),
									this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()), j);
							break;
						default:
							throw new IllegalArgumentException("incorrect compare dates type");
						}
					} else {
						rowValueJ = new ValueByGroupDate(null, j);
					}
				} else {
					rowValueJ = new ValueByGroup(super.getValueAt(j, this.groupColumn), j);
				}

				if (rowValueJ.equals(rowValue)) {

					final Object value = super.getValueAt(j, column);
					String renderedString = value == null ? "" : value.toString();
					if ((render != null) && (value != null)) {
						if (render instanceof ComboReferenceCellRenderer) {
							renderedString = ((ComboReferenceCellRenderer) render).getCodeDescription(value);
						}
						if (render instanceof DateCellRenderer) {
							try {
								renderedString = ((DateCellRenderer) render).dateFormat.format((Date) value);
							} catch (final Exception e) {
								TableSorter.logger.error("Date cell renderer value cannot be formatted", e);
							}
						}
					}
					final GroupItem groupItem = new GroupItem(value, renderedString);
					if (!temp.containsGroupItem(groupItem)) {
						temp.add(groupItem);
					}
				}
			}
			temp.setDescription();
			return temp;

		}

		protected Object getGroupValue(final Object rowValue, final int column, final int type) {
			return this.getGroupValue(rowValue, column, type, TableSorter.SUM);
		}

		protected Object getGroupValue(final Object rowValue, final int column, final int type, final int function) {
			// For all model rows with value.equals(rowValue)
			// calculate the value for the grouped column

			final Class clase = this.getColumnClass(column);
			if ((clase == Double.class) || (clase == Float.class) || (clase == BigDecimal.class)
					|| (clase == BigInteger.class) || (clase == Integer.class)) {
				// Apply the appropriate function
				final ArrayList validValues = new ArrayList();
				final ArrayList validIndexes = new ArrayList();
				final Map requiredColsValues = new HashMap();
				final List<String> requiredCols = TableSorter.this.operations.get(new Integer(function))
						.getRequiredColumns();
				if (requiredCols != null) {
					for (final String col : requiredCols) {
						requiredColsValues.put(col, new ArrayList());
					}
				}
				final int rows = super.getRowCount();
				for (int j = 0; j < rows; j++) {
					Object rowValueJ = null;
					if (rowValue instanceof ValueByGroupDate) {
						final Object o = super.getValueAt(j, this.groupColumn);

						Date vD = null;
						if (o != null) {
							vD = (Date) o;
							this.calendar.setTime(vD);
						}
						if (vD != null) {
							switch (type) {
							case TableSorter.YEAR_MONTH_DAY:
								rowValueJ = new ValueByGroupDate(
										new DayMonthYear(this.calendar.get(Calendar.DAY_OF_MONTH),
												this.calendar.get(Calendar.MONTH),
												this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
										j);
								break;
							case TableSorter.YEAR_MONTH:
								rowValueJ = new ValueByGroupDate(
										new MonthYear(this.calendar.get(Calendar.MONTH),
												this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
										j);
								break;
							case TableSorter.MONTH:
								rowValueJ = new ValueByGroupDate(new Month(this.calendar.get(Calendar.MONTH),
										this.calendar.getTimeInMillis()), j);
								break;
							case TableSorter.YEAR:
								rowValueJ = new ValueByGroupDate(
										new Year(this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
										j);
								break;
							case TableSorter.QUARTER_YEAR:
								rowValueJ = new ValueByGroupDate(
										new QuarterYear(this.calendar.get(Calendar.MONTH),
												this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
										j);
								break;
							case TableSorter.QUARTER:
								rowValueJ = new ValueByGroupDate(new Quarter(this.calendar.get(Calendar.MONTH),
										this.calendar.getTimeInMillis()), j);
								break;
							case TableSorter.WEEK_YEAR:
								rowValueJ = new ValueByGroupDate(
										new WeekYear(this.calendar.get(Calendar.DAY_OF_MONTH),
												this.calendar.get(Calendar.MONTH),
												this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
										j);
								break;
							default:
								throw new IllegalArgumentException("incorrect compare dates type");
							}
						} else {
							rowValueJ = new ValueByGroupDate(null, j);
						}
					} else {
						rowValueJ = new ValueByGroup(super.getValueAt(j, this.groupColumn), j);
					}

					if (rowValueJ.equals(rowValue)) {
						validValues.add(super.getValueAt(j, column));
						validIndexes.add(new Integer(j));
						if (requiredCols != null) {
							for (final String col : requiredCols) {
								final int colIndex = this.getColumnNames().indexOf(col);
								((List) requiredColsValues.get(col)).add(super.getValueAt(j, colIndex));
							}
						}
					}
				}
				return TableSorter.this.operations.get(new Integer(function))
						.getOperationValue(validValues, validIndexes, requiredColsValues);
				// // Now the function
				// switch (function) {
				// case SUM:
				// return getSum(validValues);
				// case AVG:
				// return getAvg(validValues);
				// case MAX:
				// return getMax(validValues);
				// case MIN:
				// return getMin(validValues);
				// case COUNT:
				// return getCount(validValues);
				// default:
				// return getSum(validValues);
				// }
			} else {
				return this.noGroupFieldValue;
			}
		}

		/**
		 * @param list
		 * @return
		 * @deprecated since 5.2078EN. Use instead {@link SumGroupOperation}
		 */
		@Deprecated
		protected Number getSum(final java.util.List list) {
			double d = 0.0;
			for (int i = 0; i < list.size(); i++) {
				final Object v = list.get(i);
				if ((v != null) && (v instanceof Number)) {
					d = d + ((Number) v).doubleValue();
				}
			}
			return new Double(d);
		}

		/**
		 * @param list
		 * @return
		 * @deprecated since 5.2078EN. Use instead {@link CountGroupOperation}
		 */
		@Deprecated
		protected Number getCount(final java.util.List list) {
			int i = 0;
			if (list != null) {
				i = list.size();
			}
			return new Integer(i);
		}

		/**
		 * @param list
		 * @return
		 * @deprecated since 5.2078EN. Use instead {@link AvgGroupOperation}
		 */
		@Deprecated
		protected Number getAvg(final java.util.List list) {
			double d = 0.0;
			for (int i = 0; i < list.size(); i++) {
				final Object v = list.get(i);
				if ((v != null) && (v instanceof Number)) {
					d = d + ((Number) v).doubleValue();
				}
			}
			return new Double(d / list.size());
		}

		/**
		 * @param list
		 * @return
		 * @deprecated since 5.2078EN. Use instead {@link MaxGroupOperation}
		 */
		@Deprecated
		protected Number getMax(final java.util.List list) {
			if ((list == null) || list.isEmpty()) {
				return null;
			}
			double d = Double.MIN_VALUE;
			for (int i = 0; i < list.size(); i++) {
				final Object v = list.get(i);
				if ((v != null) && (v instanceof Number)) {
					if (((Number) v).doubleValue() > d) {
						d = ((Number) v).doubleValue();
					}
				}
			}
			if (Double.compare(d, Double.MIN_VALUE) == 0) {
				return null;
			}
			return new Double(d);
		}

		/**
		 * @param list
		 * @return
		 * @deprecated since 5.2078EN. Use instead {@link MinGroupOperation}
		 */
		@Deprecated
		protected Number getMin(final java.util.List list) {
			if ((list == null) || list.isEmpty()) {
				return null;
			}
			double d = Double.MAX_VALUE;
			for (int i = 0; i < list.size(); i++) {
				final Object v = list.get(i);
				if ((v != null) && (v instanceof Number)) {
					if (((Number) v).doubleValue() < d) {
						d = ((Number) v).doubleValue();
					}
				}
			}
			if (Double.compare(d, Double.MAX_VALUE) == 0) {
				return null;
			}
			return new Double(d);
		}

		public void addGroupedFunction(final int id, final GroupOperation operation) {
			TableSorter.this.operations.put(id, operation);
		}

		public GroupOperation getGroupedFunction(final int id) {
			return TableSorter.this.operations.get(id);
		}

		public void group(final int column, final int type) {
			if ((column >= 0) && (column < this.getColumnCount())) {
				this.group = true;
				this.type = type;
				this.groupColumn = column;
				this.recalculateGroupedData(type);
				this.fireTableChanged(new TableModelEvent(this));
			}
		}

		public void group(final int column) {
			this.group(column, 0);
		}

		@Override
		public void tableChanged(final TableModelEvent e) {
			this.recalculateGroupedData(this.type);
			super.tableChanged(e);
		}

		protected void recalculateGroupedData() {
			this.recalculateGroupedData(this.type);
		}

		protected Calendar calendar = Calendar.getInstance();


		protected ValueByGroup createValueByGroup(final int type, final int i) {
			switch (type) {
			case TableSorter.YEAR_MONTH_DAY:
				return new ValueByGroupDate(new DayMonthYear(this.calendar.get(Calendar.DAY_OF_MONTH),
						this.calendar.get(Calendar.MONTH),
						this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()), i);
			case TableSorter.YEAR_MONTH:
				return new ValueByGroupDate(
						new MonthYear(this.calendar.get(Calendar.MONTH), this.calendar.get(Calendar.YEAR),
								this.calendar.getTimeInMillis()),
						i);
			case TableSorter.MONTH:
				return new ValueByGroupDate(
						new Month(this.calendar.get(Calendar.MONTH), this.calendar.getTimeInMillis()), i);
			case TableSorter.YEAR:
				return new ValueByGroupDate(
						new Year(this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()), i);
			case TableSorter.QUARTER_YEAR:
				return new ValueByGroupDate(
						new QuarterYear(this.calendar.get(Calendar.MONTH), this.calendar.get(Calendar.YEAR),
								this.calendar.getTimeInMillis()),
						i);
			case TableSorter.QUARTER:
				return new ValueByGroupDate(
						new Quarter(this.calendar.get(Calendar.MONTH), this.calendar.getTimeInMillis()), i);
			case TableSorter.WEEK_YEAR:
				return new ValueByGroupDate(
						new WeekYear(this.calendar.get(Calendar.DAY_OF_MONTH), this.calendar.get(Calendar.MONTH),
								this.calendar.get(Calendar.YEAR), this.calendar.getTimeInMillis()),
						i);
			default:
				throw new IllegalArgumentException("incorrect compare dates type");
			}
		}

		protected void recalculateGroupedData(final int type) {
			if ((this.group) && (this.groupColumn >= 0)) {
				// Get the grouped column values to count the different ones
				final List differentValues = new Vector();
				final Class columnClass = super.getColumnClass(this.groupColumn);

				for (int i = 0; i < super.getRowCount(); i++) {
					final Object oValue = super.getValueAt(i, this.groupColumn);
					ValueByGroup current = null;
					if ((oValue instanceof Date) || ((oValue == null) && (columnClass == Date.class))
							|| (columnClass == Timestamp.class)) {
						Date vD = null;
						if (oValue != null) {
							vD = (Date) oValue;
							this.calendar.setTime(vD);
						}
						if (vD != null) {
							current = this.createValueByGroup(type, i);


						} else {
							current = new ValueByGroupDate(null, i);
						}
					} else {
						current = new ValueByGroup(oValue, i);
					}

					if (!differentValues.contains(current)) {
						differentValues.add(current);
					} else {
						final int j = differentValues.indexOf(current);
						final ValueByGroup element = (ValueByGroup) differentValues.get(j);
						element.increment(i);
					}
				}

				final int rowCount = differentValues.size();

				TableSorter.logger.debug("Grouping by the column: {}. Number of rows: {}", this.groupColumn, rowCount);

				this.groupData = new Object[rowCount][this.getColumnCount()];

				for (int i = 0; i < rowCount; i++) {
					this.groupData[i][this.groupColumn] = differentValues.get(i);
				}
				// Calculate values for Double, Float, BigDecimal and BigInteger
				// columns

				for (int i = 0; i < this.getColumnCount(); i++) {
					if (i == this.groupColumn) {
						continue;
					}
					final Class clase = this.getColumnClass(i);
					if ((clase == Double.class) || (clase == Float.class) || (clase == BigDecimal.class)
							|| (clase == BigInteger.class) || (clase == Integer.class)) {
						// Apply the function
						TableCellRenderer render = TableSorter.this.sourceTable.getColumnModel()
								.getColumn(i)
								.getCellRenderer();
						if ((render != null) && (!(render instanceof ComboReferenceCellRenderer))) {
							render = null;
						}
						if (render == null) {
							for (int j = 0; j < rowCount; j++) {
								final Object rowValue = differentValues.get(j);
								this.groupData[j][i] = this.getGroupValue(rowValue, i, type,
										this.getGroupColumnFunction(i));
							}
							continue;
						}
					}

					for (int j = 0; j < rowCount; j++) {
						final Object oRowValue = differentValues.get(j);
						this.groupData[j][i] = this.getNotNumericalGroupValues(oRowValue, i, type);
					}

				}
			} else {
				this.groupData = new Object[0][0];
			}
		}

		@Override
		public void setValueAt(final Object v, final int row, final int column) {
			if (this.group) {
				return;
			} else {
				super.setValueAt(v, row, column);
			}
		}

		public void resetGroup() {
			if (this.group) {
				this.group = false;
				this.groupColumn = -1;
				this.fireTableChanged(new TableModelEvent(this));
			}
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			if (!this.group) {
				return this.model.getValueAt(row, column);
			} else {
				// Group data
				return this.groupData[row][column];
			}
		}

		@Override
		public int getRowCount() {
			if (!this.group) {
				return this.model.getRowCount();
			} else {
				// Groups exist. We have to calculate the row count
				// For the grouped column count the different values
				return this.groupData.length;
			}
		}

		public Object getFilters() {
			return ((FilterTableModel) this.model).getFilters();
		}

		public Object getColumnFilter(final String col) {
			return ((FilterTableModel) this.model).getColumnFilter(col);
		}

	}

	public class FilterTableModel extends TableMap {

		protected FilterValidator filterValidator = new DefaultFilterValidator();

		protected int[] map = new int[0];

		protected Map filters = new Hashtable();

		protected boolean lastFilterOr = false;

		public FilterTableModel(final ExtendedTableModel m) {
			super();
			this.setModel(m);
			this.resetFilter();
		}

		public boolean lastFilterOr() {
			return this.lastFilterOr;
		}

		public void setFilterValidator(final FilterValidator f) {
			this.filterValidator = f;
		}

		public FilterValidator getFilterValidator() {
			return this.filterValidator;
		}

		public void addColumn(final String col) {
			((ExtendedTableModel) this.model).addColumn(col);
		}

		public void addColumn(final String col, final boolean event) {
			((ExtendedTableModel) this.model).addColumn(col, event);
		}

		public void addCalculatedColumn(final String col, final String expression) {
			((ExtendedTableModel) this.model).addCalculatedColumn(col, expression);
		}

		public void deleteColumn(final String col) {
			((ExtendedTableModel) this.model).deleteColumn(col);
		}

		public void deleteColumn(final String col, final boolean fireEvent) {
			((ExtendedTableModel) this.model).deleteColumn(col, fireEvent);
		}

		public void deleteCalculatedColumn(final String col) {
			((ExtendedTableModel) this.model).deleteCalculatedColumn(col);
		}

		public Map getCalculatedColumns() {
			return ((ExtendedTableModel) this.model).getCalculatedColumns();
		}

		public List getCalculatedColumnsName() {
			return ((ExtendedTableModel) this.model).getCalculatedColumnsName();
		}

		public List getRequiredColumnsToCalculatedColumns() {
			return ((ExtendedTableModel) this.model).getRequiredColumnsToCalculatedColumns();
		}

		public Object getCalculatedValue(final int column, final Map rowValues) {
			return ((ExtendedTableModel) this.model).getCalculatedValue(column, rowValues);
		}

		public String getCalculatedColumnExpression(final String col) {
			return ((ExtendedTableModel) this.model).getCalculatedColumnExpression(col);
		}

		public void setCalculatedColumnExpression(final String col, final String expression) {
			((ExtendedTableModel) this.model).setCalculatedColumnExpression(col, expression);
		}

		protected Object getColumnOperation(final String columnIdentifier, final String operation) {
			return ((ExtendedTableModel) this.model).getColumnOperation(columnIdentifier, operation);
		}

		public List getTotalRowOperation() {
			return ((ExtendedTableModel) this.model).getTotalRowOperation();
		}

		public void addTotalRowOperation(final TotalRowOperation operation) {
			((ExtendedTableModel) this.model).addTotalRowOperation(operation);
		}

		public int convertRowIndexToModel(final int i) {
			return this.getModelIndex(i);
		}

		public TableCellRenderer getSumCellRenderer(final boolean currency) {
			return ((ExtendedTableModel) this.model).getSumCellRenderer(currency, TableSorter.this.bundle);
		}

		public Map getData() {
			return ObjectTools.clone(((ExtendedTableModel) this.model).getData());
		}

		@Override
		public void tableChanged(final TableModelEvent e) {
			TableSorter.logger.debug("FilterTableModel: tableChanged");

			int firstIndex = e.getFirstRow();
			int lastIndex = e.getLastRow();

			if (e.getType() == TableModelEvent.DELETE) {
				int index = ArrayUtils.findElementIndex(this.map, e.getFirstRow());
				if (index >= 0) {
					firstIndex = index;
				}
				index = ArrayUtils.findElementIndex(this.map, e.getLastRow());
				if (index >= 0) {
					lastIndex = index;
				}
				final int deleteNumber = (lastIndex - firstIndex) + 1;
				final int[] newMap = new int[this.map.length - deleteNumber];
				if (firstIndex > 0) {
					System.arraycopy(this.map, 0, newMap, 0, firstIndex);
				}
				System.arraycopy(this.map, lastIndex + 1, newMap, firstIndex, this.map.length - (lastIndex + 1));
				for (int i = 0; i < newMap.length; i++) {
					newMap[i] = newMap[i] > e.getFirstRow() ? newMap[i] - deleteNumber : newMap[i];
				}
				this.map = newMap;
			} else {
				final boolean wasFiltered = this.filters.size() > 0;
				final boolean wasLastFilterOr = this.lastFilterOr;
				final Map hPreviousFilters = ObjectTools.clone(this.filters);
				this.resetFilter(false);
				if (wasFiltered) {
					TableSorter.logger.debug("FilterTableModel: tableChanged: applying filter");
					this.applyFilter(hPreviousFilters, wasLastFilterOr, false);
				}
			}
			if (this.model.getRowCount() > 0) {
				if (e.getType() != TableModelEvent.DELETE) {
					firstIndex = ArrayUtils.findElementIndex(this.map, e.getFirstRow());

					if ((firstIndex < 0) && (e.getFirstRow() >= 0)) {
						// HIDDEN
						firstIndex = ExtendedTableModelEvent.HIDDEN_ROW;
					}
					if (lastIndex != Integer.MAX_VALUE) {
						lastIndex = ArrayUtils.findElementIndex(this.map, e.getLastRow());
						if ((lastIndex < 0) && (e.getLastRow() >= 0)) {
							// HIDDEN
							lastIndex = ExtendedTableModelEvent.HIDDEN_ROW;
						}
					}
				}
			}
			super.tableChanged(
					new TableModelEvent((TableModel) e.getSource(), firstIndex, lastIndex, e.getColumn(), e.getType()));
		}

		public Map getFilteredData() {
			if (this.filters.size() == 0) {
				return this.getData();
			}
			final Map hFilteredData = new Hashtable();
			final Map hDataWithoutFilter = this.getData();
			if (hDataWithoutFilter != null) {
				final Enumeration enumKeys = Collections.enumeration(hDataWithoutFilter.keySet());
				while (enumKeys.hasMoreElements()) {
					final Object oKey = enumKeys.nextElement();
					final List vWithoutFilter = (List) hDataWithoutFilter.get(oKey);
					final List vFiltered = new Vector();
					for (int i = 0; i < this.map.length; i++) {
						vFiltered.add(vFiltered.size(), vWithoutFilter.get(this.map[i]));
					}
					hFilteredData.put(oKey, vFiltered);
				}
				return hFilteredData;
			} else {
				return null;
			}
		}

		protected int getModelIndex(final int row) {
			if ((row < 0) || (row >= this.map.length)) {
				return -1;
			}
			return this.map[row];
		}

		public int getCurrentRowCount() {
			if (this.isFiltered()) {
				return this.getRowCount();
			} else {
				return ((ExtendedTableModel) this.model).getRowsNumber();
			}
		}

		public int getRealRecordNumber() {
			return ((ExtendedTableModel) this.model).getRowsNumber();
		}

		public void setEditableColumn(final String col) {
			((ExtendedTableModel) this.model).setEditableColumn(col);
		}

		public void setEditableColumn(final String col, final boolean editable) {
			((ExtendedTableModel) this.model).setEditableColumn(col, editable);
		}

		public boolean isFiltered(final int col) {
			final String colStr = this.model.getColumnName(col);
			return this.filters.containsKey(colStr);
		}

		public Object getColumnIdentifier(final int col) {
			return ((ExtendedTableModel) this.model).getColumnIdentifier(col);
		}

		public List getColumnText() {
			return ((ExtendedTableModel) this.model).getColumnTexts();
		}

		public List getColumnNames() {
			return ((ExtendedTableModel) this.model).getColumnNames();
		}

		public void deleteRow(final int row) {
			// Delete the row in the model
			final int modelRow = this.getModelIndex(row);
			((ExtendedTableModel) this.model).deleteRow(modelRow);
		}

		public void addRow(final Map hData) {
			((ExtendedTableModel) this.model).addRow(hData);
		}

		public void deleteRows(final int[] rows) {
			final int[] modelRows = new int[rows.length];
			for (int i = 0; i < rows.length; i++) {
				modelRows[i] = this.getModelIndex(rows[i]);
			}
			((ExtendedTableModel) this.model).deleteRows(modelRows);
		}

		public void addRows(final int[] pos, final List rowsData) {
			((ExtendedTableModel) this.model).addRows(rowsData);
		}

		public void addRows(final List rowsData) {
			((ExtendedTableModel) this.model).addRows(rowsData);
		}

		public boolean isFiltered() {
			return this.filters.size() > 0;
		}

		public void updateRowData(final Map data, final List keys) {
			((ExtendedTableModel) this.model).updateRowData(data, keys);
		}

		public void updateRowData(final Map data, final Map keys) {
			((ExtendedTableModel) this.model).updateRowData(data, keys);
		}

		public void updateRowData(final Map rowData, final List columns, final Map keysValues) {
			((ExtendedTableModel) this.model).updateRowData(rowData, columns, keysValues);
		}

		public void setData(final Map data) {
			((ExtendedTableModel) this.model).setData(data);
		}

		public void addRow(final int row, final Map data) {
			((ExtendedTableModel) this.model).addRow(row, data);
		}

		public Map getRowData(final int row) {
			final int modelRow = this.getModelIndex(row);
			return ((ExtendedTableModel) this.model).getRowData(modelRow);
		}

		public Map getRowDataForKeys(final List keys, final Map keysValues) {
			return ((ExtendedTableModel) this.model).getRowDataForKeys(keys, keysValues);
		}

		public Map getRowData(final int[] f) {
			if (f == null) {
				return null;
			}
			final int modelRow[] = new int[f.length];
			for (int i = 0; i < f.length; i++) {
				modelRow[i] = this.getModelIndex(f[i]);
			}
			return ((ExtendedTableModel) this.model).getRowData(modelRow);
		}

		public Map getCalculatedRowData(final int row) {
			final int modelRow = this.getModelIndex(row);
			return ((ExtendedTableModel) this.model).getCalculatedRowData(modelRow);
		}

		protected int[] packMap(final int[] mapAll, final int nMatchs) {
			final int[] ma = new int[nMatchs];
			System.arraycopy(mapAll, 0, ma, 0, nMatchs);
			return ma;
		}

		protected void resetFilter(final boolean fireEvent) {
			// ApplicationManager.printCurrentThreadMethods(10);
			this.filters.clear();
			this.lastFilterOr = false;
			this.map = new int[this.model.getRowCount()];
			for (int i = 0; i < this.map.length; i++) {
				this.map[i] = i;
			}
			if (TableSorter.this.sourceTable != null) {
				TableSorter.this.sourceTable.getTableHeader().repaint();
			}
			if (fireEvent) {
				this.fireTableChanged(new TableModelEvent(this));
			}
		}

		public void resetFilter() {
			this.resetFilter(true);
		}

		public void resetFilter(final String col) {
			if (this.filters.containsKey(col)) {
				this.filters.remove(col);
			} else {
				return;
			}
			this.map = this.evaluateFilter(this.getData(), this.filters);
			this.fireTableChanged(new TableModelEvent(this));
		}

		public void applyFilter(final Map filters) {
			this.applyFilter(filters, false);
		}

		public void applyFilter(final Map filters, final boolean or) {
			this.applyFilter(filters, or, true);
		}

		protected void applyFilter(final Map filters, final boolean or, final boolean fireEvents) {
			this.filters = ObjectTools.clone(filters);
			this.lastFilterOr = or;
			this.map = this.evaluateFilter(this.getData(), this.filters, or);
			if (fireEvents) {
				this.fireTableChanged(new TableModelEvent(this));
			}
		}

		public void applyFilter(final int columnIndex, final Object filterValue) {
			if (columnIndex < 0) {
				TableSorter.logger.info("Filter column index < 0");
				this.resetFilter();
				return;
			} else {
				final String col = this.model.getColumnName(columnIndex);
				if (filterValue == null) {
					TableSorter.logger.info("Filter value is NULL");
					this.resetFilter(col);
					return;
				}

				if (TableSorter.this.sourceTable != null) {
					TableSorter.this.sourceTable.getTableHeader().repaint();
				}

				// int viewColumn =
				// sourceTable.convertColumnIndexToView(columnIndex);

				// Now apply the filter to the data
				final Map hData = ((ExtendedTableModel) this.model).getData();
				if (this.filters.containsKey(col)) {
					final Object oOldFilter = this.filters.get(col);
					if (oOldFilter instanceof MultipleFilter) {
						((MultipleFilter) oOldFilter).addOR(filterValue);
					} else {
						final MultipleFilter f = new MultipleFilter();
						f.addOR(oOldFilter);
						f.addOR(filterValue);
						this.filters.put(col, f);
					}
				} else {
					this.filters.put(col, filterValue);
				}

				// Evaluate the filters for all row data
				final int[] mapped = this.evaluateFilter(hData, this.filters);

				this.map = mapped;

				this.fireTableChanged(new TableModelEvent(this));
			}
		}

		protected int[] evaluateFilter(final Map data, final Map filters) {
			return this.evaluateFilter(data, filters, false);
		}

		protected int[] evaluateFilter(final Map data, final Map filters, final boolean or) {
			// For each row evaluate the filters
			if (filters.isEmpty() || data.isEmpty()) {
				final int[] mapAux = new int[this.model.getRowCount()];
				for (int i = 0; i < mapAux.length; i++) {
					mapAux[i] = i;
				}
				return mapAux;
			}

			final Enumeration enumFilterCols = Collections.enumeration(filters.keySet());
			final List vFilterColsName = new Vector();

			while (enumFilterCols.hasMoreElements()) {
				vFilterColsName.add(enumFilterCols.nextElement());
			}
			Object oFirst = vFilterColsName.get(0);

			List vColumData = (List) data.get(oFirst);
			int k = 1;
			final int sizeFilterColsName = vFilterColsName.size();
			while ((vColumData == null) && (k < sizeFilterColsName)) {
				oFirst = vFilterColsName.get(k);
				vColumData = (List) data.get(oFirst);
				k++;
			}
			final int[] mapAux = new int[this.model.getRowCount()];
			int matchsNumber = 0;
			if (vColumData == null) {
				this.resetFilter();
				for (int i = 0; i < mapAux.length; i++) {
					mapAux[i] = i;
				}
				return mapAux;
			}
			final int sizeColumnData = vColumData.size();
			for (int i = 0; i < sizeColumnData; i++) {
				// Get the data for each filter key
				boolean match = true;
				boolean atLeastOneMatch = false;
				for (int j = 0; j < sizeFilterColsName; j++) {
					final List vFilterData = (List) data.get(vFilterColsName.get(j));
					if (vFilterData == null) {
						continue;
					}
					int modelIndex = -1;
					final int sizeColumnCount = this.getColumnCount();
					for (int l = 0; l < sizeColumnCount; l++) {
						if (vFilterColsName.get(j).equals(this.getColumnIdentifier(l))) {
							modelIndex = l;
							break;
						}
					}

					final Object oFilterValue = filters.get(vFilterColsName.get(j));
					Object oValue = vFilterData.get(i);
					if (TableSorter.this.sourceTable.getCellRenderer(0, TableSorter.this.sourceTable
							.convertColumnIndexToView(modelIndex)) instanceof ComboReferenceCellRenderer) {
						oValue = ((ComboReferenceCellRenderer) TableSorter.this.sourceTable.getCellRenderer(0,
								TableSorter.this.sourceTable.convertColumnIndexToView(modelIndex)))
								.getCodeDescription(vFilterData.get(i));
					}

					if (oFilterValue instanceof SimpleFilter) {
						final Class columClass = TableSorter.this.sourceTable
								.getColumnClass(TableSorter.this.sourceTable.convertColumnIndexToView(modelIndex));
						if ((columClass != null) && Date.class.isAssignableFrom(columClass)) {
							final TableCellRenderer cellRenderer = TableSorter.this.sourceTable.getCellRenderer(0,
									TableSorter.this.sourceTable.convertColumnIndexToView(modelIndex));
							if (cellRenderer != null) {
								final Component comp = cellRenderer.getTableCellRendererComponent(
										TableSorter.this.sourceTable, oValue, false, false, -1,
										TableSorter.this.sourceTable.convertColumnIndexToView(modelIndex));
								if ((comp != null) && (comp instanceof JLabel)) {
									oValue = ((JLabel) cellRenderer).getText();
								} else if ((comp != null) && (comp instanceof TextComponent)) {
									oValue = ((TextComponent) cellRenderer).getText();
								}
							}
						}
						// //TODO The row to the cellrenderer is -1. In the next
						// version a Object to manage the filter data is
						// necessary.
						// TableCellRenderer cellRenderer =
						// sourceTable.getCellRenderer(0,
						// sourceTable.convertColumnIndexToView(modelIndex));
						// if (cellRenderer instanceof CellRenderer){
						// Component comp =
						// cellRenderer.getTableCellRendererComponent(sourceTable,
						// oValue, false, false, -1, sourceTable
						// .convertColumnIndexToView(modelIndex));
						// if (comp != null && comp instanceof JLabel) {
						// oValue = ((JLabel) comp).getText();
						// }else if (comp != null && comp instanceof
						// TextComponent){
						// oValue = ((TextComponent) comp).getText();
						// }
						// }
					}

					final boolean isOk = this.matchsFilter(oValue, oFilterValue);
					if (!isOk) {
						match = false;
						if (!or) {
							break;
						}
					} else {
						atLeastOneMatch = true;
						if (or) {
							break;
						}
					}
				}
				if (match || (or && atLeastOneMatch)) {
					// Put the index in the mapped Array
					mapAux[matchsNumber] = i;
					matchsNumber++;
				}
			}
			return this.packMap(mapAux, matchsNumber);

		}

		public List getFiltersColumns() {
			final Enumeration enumFilterColumns = Collections.enumeration(this.filters.keySet());
			final List vFilterColumnNames = new Vector();
			while (enumFilterColumns.hasMoreElements()) {
				vFilterColumnNames.add(enumFilterColumns.nextElement());
			}
			return vFilterColumnNames;
		}

		public Object getFilters() {
			return this.filters;
		}

		public Object getColumnFilter(final String col) {
			return this.filters.get(col);
		}

		protected boolean matchsFilter(final Object value, final Object filterValue) {
			return this.filterValidator.matchsFilter(value, filterValue);
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			return this.model.getValueAt(this.map[row], column);
		}

		@Override
		public void setValueAt(final Object aValue, final int aRow, final int aColumn) {
			this.model.setValueAt(aValue, this.map[aRow], aColumn);
		}

		@Override
		public int getRowCount() {
			return this.map.length;
		}

		@Override
		public int getColumnCount() {
			return this.model.getColumnCount();
		}

	}

	public static Collator comparator = Collator.getInstance();

	/**
	 * Class that contains the event information corresponding to the change of the size of a column.
	 * Contains the column model index that launched the event.
	 */
	public static class ColumnSizeEvent extends EventObject {

		int col = -1;

		/**
		 * Creates a column size event.
		 * @param source the source of the event
		 * @param col the column that launches the event
		 */
		public ColumnSizeEvent(final Object source, final int col) {
			super(source);
			this.col = col;
		}

		/**
		 * Returns the model column index affected by this event.
		 * @return
		 */
		public int getModelColumnIndex() {
			return this.col;
		}

	};

	/**
	 * Interface that must be implemented by the objects that can be used as table filters
	 */
	public static interface FilterValidator {

		/**
		 * Checks whether the object matches with the filter.
		 * @param object the object to evaluate
		 * @param filter the filter to be applied
		 * @return true if the object matches the filter
		 */
		public boolean matchsFilter(Object object, Object filter);

	}

	/**
	 * The default implementation of the FilterValidator interface. Checks whether the object matches
	 * the filter. The object can be an instance of the following classes: {@link DateFilter},
	 * {@link MultipleFilter}, {@link DifferentSimpleFilter}, {@link DifferentFilter},
	 * {@link SimpleFilter} (wrapping a String or a Boolean) or a {@link Filter}
	 */
	public static class DefaultFilterValidator implements FilterValidator {

		protected boolean matchsStringFilter(final String oFilterValue, final Object oValue) {
			if (oValue == null) {
				return false;
			}
			final String stringValue = oValue.toString();

			String filterText = oFilterValue;

			filterText = filterText.replaceAll("[aáAÁ]", "[aáAÁ]");
			filterText = filterText.replaceAll("[eéEÉ]", "[eéEÉ]");
			filterText = filterText.replaceAll("[iíIÍ]", "[iíIÍ]");
			filterText = filterText.replaceAll("[oóOÓ]", "[oóOÓ]");
			filterText = filterText.replaceAll("[uúUÚ]", "[uúUÚ]");
			filterText = filterText.replaceAll("[ñÑ]", "[ñÑ]");

			String pattern = filterText.replaceAll("\\*", ".*");
			pattern = pattern.replaceAll("\\+", "\\\\+");
			pattern = pattern.replaceAll("\\?", "\\\\?");
			pattern = pattern.replaceAll("\\(", "\\\\(");
			pattern = pattern.replaceAll("\\)", "\\\\)");
			pattern = "(?i)" + pattern;

			final Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
			final Matcher m = p.matcher(stringValue);
			return m.matches();
		}

		protected boolean matchsBooleanFilter(final Boolean oFilterValue, final Object oValue) {
			if (oValue == null) {
				return false;
			}
			// Compare Booleans.
			if (oValue instanceof Boolean) {
				if (oValue.equals(oFilterValue)) {
					return true;

				}
			}
			return false;
		}

		protected boolean matchsNumberFilter(final Number oFilterValue, final Object oValue, final int condition,
				final Object[] oFilterValues) {
			if (oValue == null) {
				return false;
			}
			if (oValue instanceof Number) {
				final Number minNumber = oFilterValue;
				final Number numberValue = (Number) oValue;
				// Check if it fulfill the condition
				boolean fulfill = false;
				if (condition == Filter.EQUAL) {
					if (Double.compare(minNumber.doubleValue(), numberValue.doubleValue()) == 0) {
						fulfill = true;
					}
				}

				if (condition == Filter.LESS) {
					if (numberValue.doubleValue() < minNumber.doubleValue()) {
						fulfill = true;
					}
				}

				if (condition == Filter.GREATER) {
					if (numberValue.doubleValue() > minNumber.doubleValue()) {
						fulfill = true;
					}
				}

				if (condition == Filter.GREATER_EQUAL) {
					if (numberValue.doubleValue() >= minNumber.doubleValue()) {
						fulfill = true;
					}
				}

				if (condition == Filter.LESS_EQUAL) {
					if (numberValue.doubleValue() <= minNumber.doubleValue()) {
						fulfill = true;
					}
				}

				if (condition == Filter.RANGE) {
					final Number maxNumber = (Number) oFilterValues[1];
					if ((numberValue.doubleValue() >= minNumber.doubleValue())
							&& (numberValue.doubleValue() <= maxNumber.doubleValue())) {
						fulfill = true;
					}
				}

				return fulfill;
			}
			return false;
		}

		protected boolean matchsDateFilter(final Date oFilterValue, final Object oValue, final int condition, final Object[] oFilterValues) {
			if (oValue == null) {
				return false;
			}
			if ((oValue instanceof java.sql.Date) || (oValue instanceof java.util.Date)
					|| (oValue instanceof java.sql.Timestamp)) {

				final java.util.Date startDate = oFilterValue;
				final java.util.Date dateValue = (java.util.Date) oValue;
				// Check if fulfill the condition
				boolean fulfill = false;
				if (condition == Filter.EQUAL) {
					if (startDate.getTime() == dateValue.getTime()) {
						fulfill = true;
					}
				}

				if (condition == Filter.LESS) {

					if (dateValue.getTime() < startDate.getTime()) {
						fulfill = true;
					}
				}

				if (condition == Filter.GREATER) {

					if (dateValue.getTime() > startDate.getTime()) {
						fulfill = true;
					}
				}

				if (condition == Filter.GREATER_EQUAL) {
					if (dateValue.getTime() >= startDate.getTime()) {
						fulfill = true;
					}
				}

				if (condition == Filter.LESS_EQUAL) {
					if (dateValue.getTime() <= startDate.getTime()) {
						fulfill = true;
					}
				}

				if (condition == Filter.RANGE) {
					final java.util.Date fechaFin = (java.util.Date) oFilterValues[1];
					if ((dateValue.getTime() >= startDate.getTime()) && (dateValue.getTime() <= fechaFin.getTime())) {
						fulfill = true;
					}
				}

				return fulfill;
			}
			return false;
		}

		@Override
		public boolean matchsFilter(final Object object, final Object filterValue) {
			final Object oFilteredValue = filterValue;
			final Object oValue = object;
			// If value is simple filter then it is string or boolean

			if (oFilteredValue instanceof DateFilter) {
				return this.matchsDateFilter(oFilteredValue, oValue);
			} else if (oFilteredValue instanceof MultipleFilter) {
				return this.matchsMultipleFilter(oFilteredValue, oValue);
			} else if (oFilteredValue instanceof DifferentSimpleFilter) {
				return !this.matchsFilter(oValue,
						new SimpleFilter(((DifferentSimpleFilter) oFilteredValue).getValue()));
			} else if (oFilteredValue instanceof DifferentFilter) {
				return !this.matchsFilter(oValue,
						new Filter(Filter.EQUAL, ((DifferentFilter) oFilteredValue).getValues()));
			} else if (oFilteredValue instanceof SimpleFilter) {
				return this.matchsSimpleFilter(oFilteredValue, oValue);
			} else if (oFilteredValue instanceof Filter) {
				final Object[] oFilterValues = ((Filter) oFilteredValue).values;
				final int condition = ((Filter) oFilteredValue).condition;
				final Object oFilterValue = oFilterValues[0];

				if (oFilterValue == null) {
					TableSorter.logger.debug("Filter value is NULL -> error");
					return true;
				}
				if ((oFilterValue instanceof java.sql.Date) || (oFilterValue instanceof java.util.Date)
						|| (oFilterValue instanceof java.sql.Timestamp)) {
					return this.matchsDateFilter((java.util.Date) oFilterValue, oValue, condition, oFilterValues);

				} else if (oFilterValue instanceof Number) {
					return this.matchsNumberFilter((Number) oFilterValue, oValue, condition, oFilterValues);
				} else {
					TableSorter.logger.debug(
							"Filter value neither is String nor Boolean -> Invalid filter value --> {}", oFilterValue);
					return true;
				}
			} else {
				TableSorter.logger.debug("Filter value neither is Filter nor SimpleFilter --> {}", oFilteredValue);
				return true;
			}

		}

		protected boolean matchsSimpleFilter(final Object oFilteredValue, final Object oValue) {
			final Object oFilterValue = ((SimpleFilter) oFilteredValue).value;
			if (oFilterValue == null) {
				if ((oValue == null) || (oValue.toString().length() == 0)) {
					return true;
				} else {
					return false;
				}
			} else if (oFilterValue instanceof String) {
				return this.matchsStringFilter((String) oFilterValue, oValue);
			} else if (oFilterValue instanceof Boolean) {
				return this.matchsBooleanFilter((Boolean) oFilterValue, oValue);
			} else {
				TableSorter.logger.debug("Neither string nor boolean -> Invalid filter value --> {}", oFilteredValue);
				return true;
			}
		}

		protected boolean matchsMultipleFilter(final Object oFilteredValue, final Object oValue) {
			final MultipleFilter f = (MultipleFilter) oFilteredValue;
			// Check OR and AND
			boolean bFinalValue = false;
			for (int i = 0; i < f.size(); i++) {
				final boolean temp = this.matchsFilter(oValue, f.get(i));
				if ("AND".equalsIgnoreCase(f.getCondition(i))) {
					if (i == 0) {
						bFinalValue = true;
					}
					bFinalValue = bFinalValue && temp;
				} else {
					bFinalValue = bFinalValue || temp;
				}
			}
			return bFinalValue;
		}

		protected boolean matchsDateFilter(final Object oFilteredValue, final Object oValue) {
			if ((oValue == null) || (!(oValue instanceof Date))) {
				return false;
			} else {
				final Date dV = (Date) oValue;
				if (dV.getTime() >= ((DateFilter) oFilteredValue).getInitTime()) {
					return true;
				}
				return false;
			}
		}

	}

	/**
	 * Interface used to notify the TableSorter that a column size change event.
	 */
	public static interface ColumnSizeListener extends java.util.EventListener {

		/**
		 * Method that must be call to indicate that a concrete column must adjust its size.
		 * @param e the event that wraps the column
		 */
		public void columnToFitSize(TableSorter.ColumnSizeEvent e);

	}

	/**
	 * Class that represents a filter to a table column. The filter has multiple entries and in each is
	 * defined an object that indicates the value and the logic operation to be performed, in this case
	 * or <code>AND</code> or <code>OR</code>.
	 */
	public static class MultipleFilter implements java.io.Serializable {

		protected ArrayList list = new ArrayList();

		protected ArrayList conditionList = new ArrayList();

		/**
		 * Adds a value to the filter with an OR condition.
		 * @param filterValue the value to add to the filter
		 */
		public void addOR(final Object filterValue) {
			this.list.add(filterValue);
			this.conditionList.add("OR");
		}

		/**
		 * Adds a value to the filter with an AND condition.
		 * @param filterValue the value to add to the filter
		 */
		public void addAND(final Object filterValue) {
			this.list.add(filterValue);
			this.conditionList.add("AND");
		}

		/**
		 * Removes one condition from the filter.
		 * @param index the condition index
		 */
		public void remove(final int index) {
			this.list.remove(index);
			this.conditionList.remove(index);
		}

		/**
		 * Returns the number of conditions configured to this filter.
		 * @return the number of conditions configured to this filter
		 */
		public int size() {
			return this.list.size();
		}

		/**
		 * Returns the filter at the specified position in this list.
		 * @param index index of element to return
		 * @return the element at the specified position in this list.
		 */
		public Object get(final int index) {
			return this.list.get(index);
		}

		/**
		 * Returns the condition at the specified position in this list.
		 * @param index index of element to return
		 * @return the element at the specified position in this list.
		 */
		public String getCondition(final int index) {
			return (String) this.conditionList.get(index);
		}

		@Override
		public String toString() {
			final StringBuilder s = new StringBuilder();
			for (int i = 0; i < this.list.size(); i++) {
				s.append(this.list.get(i));
				if (i < (this.list.size() - 1)) {
					s.append(" " + this.conditionList.get(i + 1) + " ");
				}
			}
			return s.toString();
		}

	};

	/**
	 * Class that represents a filter to a table column. The filter sets a list of values that will be
	 * different to the target column.
	 */
	public static class DifferentFilter extends Filter {

		public static final int DIFFERENT = 6;

		/**
		 * Creates a new filter.
		 * @param condition the condition to apply
		 * @param values the objects to use when checking the condition
		 */
		public DifferentFilter(final int condition, final Object[] values) {
			super(condition, values);
		}

		@Override
		public String toString() {
			if ((this.values != null) && (this.values[0] != null)) {
				if (this.values[0] instanceof java.util.Date) {
					return "<> " + Filter.dateFormat.format(this.values[0]);
				}
				return "<> " + this.values[0].toString();
			}
			return "";
		}

	}

	/**
	 * Class that represents a filter to a table column. This filter will be used to filter by date.
	 */
	public static class DateFilter implements java.io.Serializable {

		public static final int DAY = 0;

		public static final int MONTH = 1;

		public static final int YEAR = 2;

		public static final int LAST = 7;

		public static Calendar date = Calendar.getInstance();

		public static String string = "table.last";

		protected String head = "";

		protected String unit = "";

		protected long begin = 0;

		protected int type = 0;

		protected int amount = 0;

		/**
		 * Creates a filter that uses dates.
		 * @param type the type of the filter, to be by DAY, MONTH, YEAR or LAST.
		 * @param amount the amount of date or time to be added to the field
		 * @param bundle the language file
		 */
		public DateFilter(final int type, final int amount, final ResourceBundle bundle) {
			this.type = type;
			this.amount = amount;
			final long time = System.currentTimeMillis();
			this.head = ApplicationManager.getTranslation(DateFilter.string, bundle);
			DateFilter.date.setTimeInMillis(time);
			DateFilter.date.set(Calendar.HOUR_OF_DAY, 0);
			DateFilter.date.set(Calendar.MINUTE, 0);
			DateFilter.date.set(Calendar.MILLISECOND, 0);
			switch (type) {
			case DAY:
				this.unit = ApplicationManager.getTranslation("days", bundle);
				DateFilter.date.add(Calendar.DAY_OF_MONTH, -amount);
				break;
			case MONTH:
				this.unit = ApplicationManager.getTranslation("months", bundle);
				DateFilter.date.add(Calendar.MONTH, -amount);
				break;
			case YEAR:
				this.unit = ApplicationManager.getTranslation("table.years", bundle);
				DateFilter.date.add(Calendar.YEAR, -amount);
				break;
			default:
				throw new IllegalArgumentException("Data type of the filter is invalid.");
			}
			this.begin = DateFilter.date.getTimeInMillis();
		}

		/**
		 * Returns the filter type.
		 * @return the filter type
		 */
		public int getType() {
			return this.type;
		}

		/**
		 * Returns the amount of date or time that is added to the field.
		 * @return the amount of date or time that is added to the field
		 */
		public int getAmount() {
			return this.amount;
		}

		/**
		 * Provides the time that the filter uses as origin to perform the filtering.
		 * @return the origin time of the filter.
		 */
		public long getInitTime() {
			return this.begin;
		}

		@Override
		public String toString() {
			return this.head + " " + this.amount + " " + this.unit;
		}

	}

	/**
	 * Class that represents a filter to a table column. This filter stores a condition to set to a list
	 * of values. The condition types are defined within the class and are<br>
	 * <ul>
	 * <li>LESS</li>
	 * <li>LESS_EQUAL</li>
	 * <li>EQUAL</li>
	 * <li>GREATER_EQUAL</li>
	 * <li>GREATER</li>
	 * <li>RANGE</li>
	 * </ul>
	 * <p>
	 * The configured condition will be applied for all the values in this filter.
	 */
	public static class Filter implements java.io.Serializable {

		public static final int LESS = 0;

		public static final int LESS_EQUAL = 1;

		public static final int EQUAL = 2;

		public static final int GREATER_EQUAL = 3;

		public static final int GREATER = 4;

		public static final int RANGE = 5;

		protected int condition = 2;

		protected Object[] values = null;

		protected static DateFormat dateFormat = null;

		/**
		 * Creates a filter.
		 * @param condition the condition to apply
		 * @param values the values to use when filtering
		 */
		public Filter(final int condition, final Object[] values) {
			this.condition = condition;
			this.values = values;
			if (Filter.dateFormat == null) {
				Filter.dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
			}
		}

		/**
		 * Returns the filter condition.
		 * @return the filter condition
		 */
		public int getCondition() {
			return this.condition;
		}

		/**
		 * Returns the objects that are being used by this filter to perform the filtering operations.
		 * @return the values to use when filtering
		 */
		public Object[] getValues() {
			return this.values;
		}

		@Override
		public String toString() {
			if (this.condition == Filter.LESS) {
				if (this.values[0] instanceof java.util.Date) {
					if (Filter.dateFormat != null) {
						return "<" + Filter.dateFormat.format(this.values[0]);
					} else {
						return "<" + DateFormat.getDateInstance(DateFormat.SHORT).format(this.values[0]);
					}
				}
				return "<" + this.values[0];
			} else if (this.condition == Filter.LESS_EQUAL) {
				if (this.values[0] instanceof java.util.Date) {
					if (Filter.dateFormat != null) {
						return "<=" + Filter.dateFormat.format(this.values[0]);
					} else {
						return "<=" + DateFormat.getDateInstance(DateFormat.SHORT).format(this.values[0]);
					}
				}
				return "<=" + this.values[0];
			} else if (this.condition == Filter.EQUAL) {
				if (this.values[0] instanceof java.util.Date) {
					if (Filter.dateFormat != null) {
						return "=" + Filter.dateFormat.format(this.values[0]);
					} else {
						return "=" + DateFormat.getDateInstance(DateFormat.SHORT).format(this.values[0]);
					}
				}
				return "=" + this.values[0];
			} else if (this.condition == Filter.GREATER_EQUAL) {
				if (this.values[0] instanceof java.util.Date) {
					if (Filter.dateFormat != null) {
						return ">=" + Filter.dateFormat.format(this.values[0]);
					} else {
						return ">=" + DateFormat.getDateInstance(DateFormat.SHORT).format(this.values[0]);
					}
				}
				return ">=" + this.values[0];
			} else if (this.condition == Filter.GREATER) {
				if (this.values[0] instanceof java.util.Date) {
					if (Filter.dateFormat != null) {
						return ">" + Filter.dateFormat.format(this.values[0]);
					} else {
						return ">" + DateFormat.getDateInstance(DateFormat.SHORT).format(this.values[0]);
					}
				}
				return ">" + this.values[0];
			} else if (this.condition == Filter.RANGE) {
				if (this.values[0] instanceof java.util.Date) {
					if (Filter.dateFormat != null) {
						return ">=" + Filter.dateFormat.format(this.values[0]) + " <"
								+ Filter.dateFormat.format(this.values[1]);
					} else {
						return ">=" + DateFormat.getDateInstance(DateFormat.SHORT).format(this.values[0]) + " <"
								+ DateFormat.getDateInstance(DateFormat.SHORT)
								.format(this.values[1]);
					}

				}
				return ">=" + this.values[0] + " <=" + this.values[1];
			} else {
				return super.toString();
			}

		}

	};

	/**
	 * Class that represents a simple filter for a table column. The class just stores a value and
	 * implements some auxiliary methods. It is used to filter the values equal to the one stored in the
	 * filter.
	 */
	public static class SimpleFilter implements java.io.Serializable {

		protected Object value = null;

		/**
		 * Creates a simple filter, that compares if the objects are equal.
		 * @param value the value that will be used in the comparisons.
		 */
		public SimpleFilter(final Object value) {
			this.value = value;
		}

		/**
		 * Returns the value that is used by this filter to do comparisons.
		 * @return the value that is used by this filter to do comparisons.
		 */
		public Object getValue() {
			return this.value;
		}

		@Override
		public String toString() {
			if (this.value == null) {
				return "NULL";
			} else if (this.value instanceof String) {
				return this.value.toString();
			} else {
				return this.value.toString();
			}
		}

	};

	/**
	 * Class that represents a simple filter for a table column. It is used to filter the values
	 * different from the one stored in the filter.
	 */
	public static class DifferentSimpleFilter extends SimpleFilter {

		/**
		 * Creates a filter to indicate that the objects are not equal.
		 * @param value
		 */
		public DifferentSimpleFilter(final Object value) {
			super(value);
		}

		@Override
		public String toString() {
			return "<> " + super.toString();
		}

	};

	// TODO generate javadoc for the indexes array
	protected int indexes[] = new int[0];

	protected int rowsNumberToOrder = 0;

	/*
	 * Mapping between filtered data and non-filtered data. Index of array will be the index of filtered
	 * data, and corresponding integer in that position will be index of non-filtered data.
	 */
	protected List sortingColumns = new Vector();

	protected List ascendants = new Vector();

	protected int compares;

	protected JTable sourceTable = null;

	protected boolean orderEnabled = true;

	protected boolean filterEnabled = true;

	protected List sumColumns = null;

	/**
	 * Contains a Map with (column_id, operation_id), e.g. (23, 0) implies that column with model
	 * identifier equals to 23 has the group operation 0 (SUM)
	 */
	protected Map operationColumns = null;

	/**
	 * Stores operation identifiers and instances of {@link GroupOperation}. E.g. for SUM operation
	 * operation_id = 0 and object will be an instance of {@link SumGroupOperation}
	 */
	protected Hashtable<Integer, GroupOperation> operations;

	protected boolean lastIsSum = true;

	protected int rowsNumber = 0;

	protected Object auxHeadValue = null;

	protected ColumnSizeListener sizeColumnListener = null;

	protected FilterDialog filterDialog = null;

	// Map with the values to insert
	protected Map insertingRowData;

	// List with all the columns to insert (Columns enabled in inserting mode)
	protected List insertableCols;

	// Boolean to know then the inserting mode is enabled
	protected boolean insertingEnabled;

	// Boolean to know if it is possible to show the total row and the inserting
	// row at the same time
	protected boolean allowTotalInserting;

	public boolean isFilterEnabled() {
		return this.filterEnabled;
	}

	public void setFilterDialog(final FilterDialog filterDialog) {
		this.filterDialog = filterDialog;
	}

	public FilterDialog getFilterDialog() {
		return this.filterDialog;
	}

	public void setSourceTable(final JTable table) {
		this.sourceTable = table;
	}

	/**
	 * Constructor.
	 * @param model the table model
	 */
	public TableSorter(final ExtendedTableModel model) {
		this(model, null);
	}

	/**
	 * Constructor.
	 * <p>
	 * @param model
	 * @param sumColumns columns to which operations can be applied
	 */
	public TableSorter(final ExtendedTableModel model, final List sumColumns) {
		this(model, sumColumns, null);
	}

	protected TableModel createModel(final ExtendedTableModel model) {
		return new GroupTableModel(new FilterTableModel(model));
	}

	public TableSorter(final ExtendedTableModel model, final List sumColumns, final Map params) {
		this.setModel(this.createModel(model));
		this.sumColumns = sumColumns;
		if (this.sumColumns != null) {
			this.operationColumns = new Hashtable();
			for (int i = 0; i < this.sumColumns.size(); i++) {
				this.operationColumns.put(this.sumColumns.get(i), Table.SUM_es_ES);
			}
		}

		// Inserting configuration
		if (params != null) {
			this.insertingEnabled = ParseUtils.getBoolean((String) params.get("inserttable"), false);
			if (this.insertingEnabled) {
				this.insertingRowData = new Hashtable();
				String insertCols = (String) params.get("insertablecols");
				if (insertCols == null) {
					insertCols = (String) params.get("cols");
				}
				this.insertableCols = ApplicationManager.getTokensAt(insertCols, ";");

				this.allowTotalInserting = ParseUtils.getBoolean((String) params.get("showtotalinserting"), true);
			}
		}
	}

	/**
	 * Sets a new GroupTableModel to this TableSorter.
	 * @param model
	 */
	public void setModel(final GroupTableModel model) {
		super.setModel(model);
		this.reallocateIndexes();
	}

	protected int compareBooleanRowsByColumn(final int rowIndex1, final Object o1, final int rowIndex2, final Object o2, final int columnIndex) {
		final TableModel data = this.model;

		Boolean bool1 = null;
		Boolean bool2 = null;

		Object temp = data.getValueAt(rowIndex1, columnIndex);
		if (temp instanceof ValueByGroup) {
			bool1 = (Boolean) ((ValueByGroup) temp).getValue();
		} else if (temp instanceof com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) {
			bool1 = (Boolean) ((GroupItem) ((com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) temp).get(0))
					.getValue();
		} else {
			bool1 = (Boolean) temp;
		}

		temp = data.getValueAt(rowIndex2, columnIndex);
		if (temp instanceof ValueByGroup) {
			bool2 = (Boolean) ((ValueByGroup) temp).getValue();
		} else if (temp instanceof com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) {
			bool2 = (Boolean) ((GroupItem) ((com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) temp).get(0))
					.getValue();
		} else {
			bool2 = (Boolean) temp;
		}

		final boolean b1 = bool1.booleanValue();
		final boolean b2 = bool2.booleanValue();
		if (b1 == b2) {
			return 0;
		} else if (b1) { // Define false < true
			return 1;
		} else {
			return -1;
		}
	}

	protected int compareStringRowsByColumn(final int rowIndex1, final Object o1, final int rowIndex2, final Object o2, final int columnIndex) {
		final TableModel data = this.model;

		String s1 = null;
		String s2 = null;

		Object temp = data.getValueAt(rowIndex1, columnIndex);
		if (temp instanceof ValueByGroup) {
			s1 = (String) ((ValueByGroup) temp).getValue();
		} else if (temp instanceof com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) {
			s1 = (String) ((GroupItem) ((com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) temp).get(0))
					.getValue();
		} else {
			s1 = (String) temp;
		}

		temp = data.getValueAt(rowIndex2, columnIndex);
		if (temp instanceof ValueByGroup) {
			s2 = (String) ((ValueByGroup) temp).getValue();
		} else if (temp instanceof com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) {
			s2 = (String) ((GroupItem) ((com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) temp).get(0))
					.getValue();
		} else {
			s2 = (String) temp;
		}

		final int result = TableSorter.comparator.compare(s1, s2);

		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}

	}

	protected int compareDateRowsByColumn(final int rowIndex1, final Object o1, final int rowIndex2, final Object o2, final int columnIndex) {
		final TableModel data = this.model;

		Object temp = data.getValueAt(rowIndex1, columnIndex);
		java.util.Date d1 = null;
		long n1 = Long.MIN_VALUE;
		if (temp instanceof ValueByGroup) {
			final DayMonthYear d = (DayMonthYear) ((ValueByGroup) temp).getValue();
			n1 = d.getTime();
		} else if (temp instanceof com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) {
			final java.util.Date oDate = (java.util.Date) ((GroupItem) ((com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) temp)
					.get(0)).getValue();
			if (oDate != null) {
				n1 = oDate.getTime();
			}
		} else {
			d1 = (java.util.Date) temp;
			n1 = d1.getTime();
		}

		temp = data.getValueAt(rowIndex2, columnIndex);

		java.util.Date d2 = null;
		long n2 = Long.MIN_VALUE;

		if (temp instanceof ValueByGroup) {
			final DayMonthYear d = (DayMonthYear) ((ValueByGroup) temp).getValue();
			n2 = d.getTime();
		} else if (temp instanceof com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) {
			final java.util.Date oDate = (java.util.Date) ((GroupItem) ((com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList) temp)
					.get(0)).getValue();
			if (oDate != null) {
				n2 = oDate.getTime();
			}
		} else {
			d2 = (java.util.Date) temp;
			n2 = d2.getTime();
		}

		if (n1 < n2) {
			return -1;
		} else if (n1 > n2) {
			return 1;
		} else {
			return 0;
		}

	}

	protected int compareNumberRowsByColumn(final int rowIndex1, final Object o1, final int rowIndex2, final Object o2, final int columnIndex) {
		final TableModel data = this.model;
		if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
			if (o1.getClass() == o2.getClass()) {
				final Comparable n1 = (Comparable) o1;
				final Comparable n2 = (Comparable) o2;
				return n1.compareTo(n2);
			} else {
				TableSorter.logger.debug("WARNING: Two number compared, but with different classes: {} and {}",
						o1.getClass(), o2.getClass());
				Object temp = data.getValueAt(rowIndex1, columnIndex);
				Number n1 = null;
				if (temp instanceof ValueByGroup) {
					n1 = (Number) ((ValueByGroup) temp).getValue();
				} else {
					n1 = (Number) temp;
				}
				final double d1 = n1.doubleValue();

				temp = data.getValueAt(rowIndex2, columnIndex);
				Number n2 = null;
				if (temp instanceof ValueByGroup) {
					n2 = (Number) ((ValueByGroup) temp).getValue();
				} else {
					n2 = (Number) temp;
				}

				final double d2 = n2.doubleValue();
				if (d1 < d2) {
					return -1;
				} else if (d1 > d2) {
					return 1;
				} else {
					return 0;
				}

			}
		} else {

			Object temp = data.getValueAt(rowIndex1, columnIndex);
			Number n1 = null;
			if (temp instanceof ValueByGroup) {
				n1 = (Number) ((ValueByGroup) temp).getValue();
			} else {
				n1 = (Number) temp;
			}
			final double d1 = n1.doubleValue();

			temp = data.getValueAt(rowIndex2, columnIndex);
			Number n2 = null;
			if (temp instanceof ValueByGroup) {
				n2 = (Number) ((ValueByGroup) temp).getValue();
			} else {
				n2 = (Number) temp;
			}

			final double d2 = n2.doubleValue();

			if (d1 < d2) {
				return -1;
			} else if (d1 > d2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Compares to row values of the same column.
	 * @param rowIndex1
	 * @param rowIndex2
	 * @param columnIndex
	 * @return 0 if both values are null or equal<br>
	 *         -1 if the first value is null or less than the second<br>
	 *         1 if the second value is null or less than the first<br>
	 */
	public int compareRowsByColumn(final int rowIndex1, final int rowIndex2, final int columnIndex) {
		final Class type = this.model.getColumnClass(columnIndex);
		final TableModel data = this.model;

		// Check for nulls.

		Object o1 = null;
		o1 = data.getValueAt(rowIndex1, columnIndex);

		if ((o1 != null) && (o1 instanceof ValueByGroup)) {
			o1 = ((ValueByGroup) o1).getValue();
		}

		Object o2 = null;
		o2 = data.getValueAt(rowIndex2, columnIndex);

		if ((o2 != null) && (o2 instanceof ValueByGroup)) {
			o2 = ((ValueByGroup) o2).getValue();
		}

		if (o1 instanceof NullValue) {
			o1 = null;
		}

		if (o2 instanceof NullValue) {
			o2 = null;
		}

		// If both values are null, return 0.
		if ((o1 == null) && (o2 == null)) {
			return 0;
		} else if (o1 == null) { // Define null less than everything.
			return -1;
		} else if (o2 == null) {
			return 1;
		}

		/*
		 * We copy all returned values from the getValue call in case an optimised model is reusing one
		 * object to return many values. The Number subclasses in the JDK are immutable and so will not be
		 * used in this way but other subclasses of Number might want to do this to save space and avoid
		 * unnecessary heap allocation.
		 */

		if ((type.getSuperclass() == java.lang.Number.class || type == RowHeadCellRenderer.class)) {
			return this.compareNumberRowsByColumn(rowIndex1, o1, rowIndex2, o2, columnIndex);
		} else if ((type == java.util.Date.class) || (type == java.sql.Date.class)
				|| (type == java.sql.Timestamp.class)) {
			return this.compareDateRowsByColumn(rowIndex1, o1, rowIndex2, o2, columnIndex);
		} else if (type == String.class) {
			return this.compareStringRowsByColumn(rowIndex1, o1, rowIndex2, o2, columnIndex);
		} else if (type == Boolean.class) {
			return this.compareBooleanRowsByColumn(rowIndex1, o1, rowIndex2, o2, columnIndex);
		} else {
			final Object v1 = data.getValueAt(rowIndex1, columnIndex);
			final String s1 = v1.toString();
			final Object v2 = data.getValueAt(rowIndex2, columnIndex);
			final String s2 = v2.toString();
			final int result = TableSorter.comparator.compare(s1, s2);

			if (result < 0) {
				return -1;
			} else if (result > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Compares two rows column by column, following the sorting of the columns.
	 *
	 * @see #compareRowsByColumn(int, int, int)
	 * @param rowIndex1
	 * @param rowIndex2
	 * @return 0 if the rows are equal<br>
	 *         1 if the first row has a null or a bigger value than the second<br>
	 *         -1 if the first row has a null or a lower value than the second<br>
	 */
	public int compare(final int rowIndex1, final int rowIndex2) {
		this.compares++;
		for (int level = 0; level < this.sortingColumns.size(); level++) {
			final Integer column = (Integer) this.sortingColumns.get(level);
			final boolean ascending = ((Boolean) this.ascendants.get(level)).booleanValue();
			final int result = this.compareRowsByColumn(rowIndex1, rowIndex2, column.intValue());
			if (result != 0) {
				return ascending ? result : -result;
			}
		}
		return 0;
	}

	/**
	 * Sets up a new array of indexes with the right number of elements for the new data model.
	 */
	public void reallocateIndexes() {
		final int rowCount = this.model.getRowCount();

		this.indexes = new int[rowCount];

		for (int row = 0; row < rowCount; row++) {
			this.indexes[row] = row;
		}
	}

	/**
	 * Processes the table change events for this model. Reallocates the row indexes and in case rows
	 * were added, removed or changed, sorts the model according the new values and the column sorting
	 * information.
	 */
	@Override
	public void tableChanged(final TableModelEvent e) {
		if ((this.sourceTable != null) && this.sourceTable.isEditing()) {
			this.sourceTable.removeEditor();
		}

		int firstIndex = e.getFirstRow();
		int lastIndex = e.getLastRow();
		if (e.getType() == TableModelEvent.DELETE) {
			int index = ArrayUtils.findElementIndex(this.indexes, e.getFirstRow());
			if (index >= 0) {
				firstIndex = index;
			}
			index = ArrayUtils.findElementIndex(this.indexes, e.getLastRow());
			if (index >= 0) {
				lastIndex = index;
			}
		}

		this.reallocateIndexes();
		if (!this.sortingColumns.isEmpty()) {
			this.sortByColumn(((Integer) this.sortingColumns.get(0)).intValue(),
					((Boolean) this.ascendants.get(0)).booleanValue(), false);
		}

		if ((this.indexes != null) && (this.indexes.length > 0)) {
			if (e.getType() != TableModelEvent.DELETE) {
				firstIndex = ArrayUtils.findElementIndex(this.indexes, firstIndex);
				if (e.getFirstRow() == ExtendedTableModelEvent.HIDDEN_ROW) {
					firstIndex = ExtendedTableModelEvent.HIDDEN_ROW;
				}

				if ((lastIndex != Integer.MAX_VALUE) && (lastIndex != ExtendedTableModelEvent.HIDDEN_ROW)) {
					lastIndex = ArrayUtils.findElementIndex(this.indexes, lastIndex);
				}

			}
		}

		super.tableChanged(
				new TableModelEvent((TableModel) e.getSource(), firstIndex, lastIndex, e.getColumn(), e.getType()));
	}

	/**
	 * Checks the model to ensure that all the changes in the model have been processed. This means that
	 * the Sorting model and the table model are in sync.
	 * @return true if the model is ok, false in case the number of record of the model is different to
	 *         the number of records of of the sorter.
	 */
	public boolean checkModel() {
		if (this.indexes.length != this.model.getRowCount()) {
			TableSorter.logger.debug("Sorter not informed of a change in model." + this.indexes.length + " , "
					+ this.model.getRowCount());
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Sorts the model.
	 *
	 * @see #shuttleSort(int[], int[], int, int)
	 * @param sender unused
	 */
	public void sort(final Object sender) {
		this.compares = 0;
		final long t = System.currentTimeMillis();
		this.rowsNumberToOrder = this.indexes.length;
		TableSorter.logger.debug("Sorting of an array of {} records from 0 up to {}", this.indexes.length,
				this.rowsNumberToOrder);
		this.shuttleSort(this.indexes.clone(), this.indexes, 0, this.rowsNumberToOrder);
		final long t2 = System.currentTimeMillis();
		TableSorter.logger.trace(" Sorting time ShuttleSort : {} millisecs", t2 - t);

		this.checkModel();
	}

	/**
	 * Sorts the table indexes.
	 *
	 * @see #swap(int, int)
	 * @deprecated
	 */
	@Deprecated
	public void n2sort() {
		for (int i = 0; i < this.getRowCount(); i++) {
			for (int j = i + 1; j < this.getRowCount(); j++) {
				if (this.compare(this.indexes[i], this.indexes[j]) == -1) {
					this.swap(i, j);
				}
			}
		}
	}

	// This is a home-grown implementation which we have not had time
	// to research - it may perform poorly in some circumstances. It
	// requires twice the space of an in-place algorithm and makes
	// NlogN assigments shuttling the values between the two
	// arrays. The number of compares appears to vary between N-1 and
	// NlogN depending on the initial order but the main reason for
	// using it here is that, unlike qsort, it is stable.

	/**
	 * Fast algorithm to sort an array.
	 * @param from the original array
	 * @param to the sorted array
	 * @param low the starting index (typically 0)
	 * @param high the ending index (typically from.length)
	 */
	public void shuttleSort(final int from[], final int to[], final int low, final int high) {
		if ((high - low) < 2) {
			return;
		}
		final int middle = (low + high) / 2;
		this.shuttleSort(to, from, low, middle);
		this.shuttleSort(to, from, middle, high);

		int p = low;
		int q = middle;

		/*
		 * This is an optional short-cut; at each recursive call, check to see if the elements in this
		 * subset are already ordered. If so, no further comparisons are needed; the sub-array can just be
		 * copied. The array must be copied rather than assigned otherwise sister calls in the recursion
		 * might get out of sinc. When the number of elements is three they are partitioned so that the
		 * first set, [low, mid), has one element and and the second, [mid, high), has two. We skip the
		 * optimisation when the number of elements is three or less as the first compare in the normal
		 * merge will produce the same sequence of steps. This optimisation seems to be worthwhile for
		 * partially ordered lists but some analysis is needed to find out how the performance drops to
		 * Nlog(N) as the initial order diminishes - it may drop very quickly.
		 */

		if (((high - low) >= 4) && (this.compare(from[middle - 1], from[middle]) <= 0)) {
			for (int i = low; i < high; i++) {
				to[i] = from[i];
			}
			return;
		}

		// A normal merge.

		for (int i = low; i < high; i++) {
			if ((q >= high) || ((p < middle) && (this.compare(from[p], from[q]) <= 0))) {
				to[i] = from[p++];
			} else {
				to[i] = from[q++];
			}
		}
	}

	/**
	 * Swaps two indexes of the table.
	 * @param i
	 * @param j
	 */
	public void swap(final int i, final int j) {
		final int tmp = this.indexes[i];
		this.indexes[i] = this.indexes[j];
		this.indexes[j] = tmp;
	}

	// The mapping only affects the contents of the data rows.
	// Pass all requests to these rows through the mapping array: "indexes".

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		this.checkModel();
		if ((rowIndex < 0) || (rowIndex >= this.getRowCount())) {
			return null;
		}
		if ((columnIndex < 0) || (columnIndex >= this.getColumnCount())) {
			return null;
		}
		if (this.isInsertingRow(rowIndex)) {
			if (columnIndex == 0) {
				return "*";
			}
			Object value = this.insertingRowData.get(this.getColumnName(columnIndex));
			if (value == null) {
				// If this is a calculated value then it is necessary to
				// calculate it
				final String columnName = this.getColumnName(columnIndex);
				final List calculatedColumns = this.getCalculatedColumnsName();
				if ((calculatedColumns != null) && (columnName != null) && calculatedColumns.contains(columnName)) {
					// If the columns depends on other calculated columns then
					// insertingRowData is not enough information to calculate
					// the value
					final Map dependence = this.getCalculatedColumnsDependence(columnName);
					if ((dependence == null) || (dependence.size() == 0)) {
						value = this.getCalculatedValue(columnIndex, this.insertingRowData);
					} else {
						final Map data = new Hashtable(this.insertingRowData);
						final Enumeration dependenceKeys = Collections.enumeration(dependence.keySet());
						while (dependenceKeys.hasMoreElements()) {
							final Object key = dependenceKeys.nextElement();
							final Object valueAt = this.getValueAt(rowIndex, ((Number) key).intValue());
							if (valueAt != null) {
								data.put(dependence.get(key), valueAt);
							}
						}
						value = this.getCalculatedValue(columnIndex, data);
					}
				}
			}
			return value;
		}
		/**
		 * Delete sumRow if ((this.sumColumns != null) && (!this.sumColumns.isEmpty())) { if
		 * ((this.rowsNumber == rowIndex && (lastIsSum)) || ((rowIndex == 0) && (lastIsSum == false))) { if
		 * (columnIndex == 0) { return "T"; } else return getColumnOperation(columnIndex); } if (!lastIsSum)
		 * { rowIndex--; } }
		 */

		if (this.indexes.length == 0) {
			return this.model.getValueAt(rowIndex, columnIndex);
		} else {
			return this.model.getValueAt(this.indexes[rowIndex], columnIndex);
		}
	}

	/**
	 * Get the columns that exist in the expression to calculate the specified column
	 * @param columnName Name of the calculated column
	 * @return a Map which keys are the indices of the columns and values are the colum names
	 */
	protected Map getCalculatedColumnsDependence(final String columnName) {
		final String expression = this.getCalculatedColumnExpression(columnName);
		if (expression != null) {
			final Map result = new Hashtable();
			final int count = this.getColumnCount();
			final List calcNames = this.getCalculatedColumnsName();
			for (int i = 0; i < count; i++) {
				final String colDep = this.getColumnName(i);
				if (calcNames.contains(colDep) && ExtendedTableModel.expressionContainsColName(colDep, expression,
						ExtendedTableModel.availableCalculatedColumnNameCharacterPattern)) {
					result.put(new Integer(i), colDep);
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		this.checkModel();
		if (this.isInsertingRow(rowIndex)) {
			if (aValue != null) {
				this.insertingRowData.put(this.getColumnName(columnIndex), aValue);
			} else {
				this.insertingRowData.remove(this.getColumnName(columnIndex));
			}
		} else {
			this.model.setValueAt(aValue, this.indexes[rowIndex], columnIndex);
		}
	}

	/**
	 * Sorts the table by the columns specified as parameter. The sorting will be ascending.
	 * @param column the column that will be used to sort
	 */
	public void sortByColumn(final int column) {
		this.sortByColumn(column, true);
	}

	/**
	 * Sorts the table by the columns specified as parameter.
	 * @param column the column that will be used to sort
	 * @param ascending if true, the values will go from lower to higher; reverse when true
	 */
	public void sortByColumn(final int column, final boolean ascending) {
		this.sortByColumn(column, ascending, true);
	}

	protected void sortByColumn(final int column, final boolean ascending, final boolean fireEvent) {

		final Integer col = new Integer(column);

		if (!this.sortingColumns.contains(col)) {
			this.sortingColumns.add(col);
			if (ascending) {
				this.ascendants.add(Boolean.TRUE);
			} else {
				this.ascendants.add(Boolean.FALSE);
			}
		} else {
			final int index = this.sortingColumns.indexOf(col);
			this.ascendants.set(index, new Boolean(ascending));
		}
		TableSorter.logger.debug("TableSorter -> ordenando por: {} , {}", this.sortingColumns, this.ascendants);
		if (this.isLocalSorter()) {
			this.sort(this);
		}
		if (fireEvent) {
			this.fireTableChanged(new TableModelEvent(TableSorter.this));
		}
	}

	// /**
	// * Adds a default mouse listener to the table header and configures the
	// table
	// * header rendering. Controls all the click dependent behaviour of the
	// table
	// * header.
	// *
	// * @param table
	// * the table which header will have the listener
	// */
	// public void addTableHeaderMouseListener(JTable table) {
	// this.sourceTable = table;
	//
	// SortTableCellRenderer rend = new SortTableCellRenderer(sourceTable);
	// rend.setMaxLinesNumber(SortTableCellRenderer.MAX_VALUE_HEAD_RENDERER_LINES);
	// sourceTable.getTableHeader().setDefaultRenderer(rend);
	// TableColumnModel tcModel = sourceTable.getColumnModel();
	// for (int i = 0; i < tcModel.getColumnCount(); i++) {
	// TableColumn tc = tcModel.getColumn(i);
	// tc.setHeaderRenderer(rend);
	// }
	// sourceTable.getTableHeader().repaint();
	// final TableSorter sorter = this;
	// final JTable tableView = table;
	// tableView.setColumnSelectionAllowed(false);
	//
	// // Column listener
	// listMouseListener = new
	// SortTableCellRenderer.ListMouseListener(tableView);
	// JTableHeader th = tableView.getTableHeader();
	// th.addMouseListener(listMouseListener);
	// }
	//
	// /**
	// * Compares the point with the header of the column specified by the
	// * columnIndex. If the point corresponds to the header, the method returns
	// * the related TableColumn.
	// *
	// * @param header
	// * the table header
	// * @param point
	// * a point coming from a mouse event
	// * @param columnIndex
	// * the columnIndex to compare return the TableColumn corresponding
	// * to the columnIndex in case the mouse event was performed into
	// * the column header; null otherwise
	// */
	// private TableColumn getResizingColumn(JTableHeader header, Point point,
	// int columnIndex) {
	// if (columnIndex == -1) {
	// return null;
	// }
	// Rectangle r = header.getHeaderRect(columnIndex);
	// r.grow(-3, 0);
	// if (r.contains(point)) {
	// return null;
	// }
	// int midPoint = r.x + r.width / 2;
	// int columnIndexLocal;
	// if (header.getComponentOrientation().isLeftToRight()) {
	// columnIndexLocal = (point.x < midPoint) ? columnIndex - 1 : columnIndex;
	// } else {
	// columnIndexLocal = (point.x < midPoint) ? columnIndex : columnIndex - 1;
	// }
	// if (columnIndexLocal == -1) {
	// return null;
	// }
	// return header.getColumnModel().getColumn(columnIndexLocal);
	// }

	/**
	 * Removes the table header listener and the model.
	 */
	@Override
	public void free() {
		// if (sourceTable != null) {
		// this.sourceTable.getTableHeader().removeMouseListener(listMouseListener);
		// }
		this.sourceTable = null;
		TableSorter.logger.debug("Released class");
	}

	/**
	 * Enables or disables sorting in the table
	 * @param enable
	 */
	public void enableSort(final boolean enable) {
		this.orderEnabled = enable;
	}

	/**
	 * Checks if the sorting is enable.
	 * @return true if sorting is enabled, false otherwise
	 */
	public boolean isSortEnabled() {
		return this.orderEnabled;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		// Insert row
		if (this.isInsertingRow(rowIndex)) {
			if ((this.insertableCols == null) || (this.insertableCols.size() == 0)) {
				return !(columnIndex == 0);
			} else {
				final String columnName = this.getColumnName(columnIndex);
				return this.insertableCols.contains(columnName);
			}
			// Sum row
		} else if (this.isSumCell(rowIndex, columnIndex) || this.isGrouped()) {
			return false;
		} else {
			return super.isCellEditable(rowIndex, columnIndex);
		}
	}

	/**
	 * Deletes the specified rows from the model
	 * @param rowIndex the indexes that identifies the rows to remove
	 */
	public void deleteRows(int[] rowIndex) {
		// To avoid delete the insert row if exist
		for (int i = 0; i < rowIndex.length; i++) {
			if (this.isInsertingRow(rowIndex[i])) {
				final int[] newRows = new int[rowIndex.length - 1];
				System.arraycopy(rowIndex, 0, newRows, 0, i);
				if (i != (rowIndex.length - 1)) {
					System.arraycopy(rowIndex, i + 1, newRows, 0, newRows.length - i);
				}
				rowIndex = newRows;
				break;
			}
		}
		final int[] filteredIndex = new int[rowIndex.length];
		for (int i = 0; i < rowIndex.length; i++) {
			filteredIndex[i] = this.indexes[rowIndex[i]];
		}
		((GroupTableModel) this.model).deleteRows(filteredIndex);
		// this.tableChanged(new TableModelEvent(this));
	}

	/**
	 * Deletes the specified row.
	 * @param rowIndex the row index
	 */
	public void deleteRow(final int rowIndex) {
		if ((rowIndex < 0) || (rowIndex > this.getRowCount())) {
			return;
		}
		((GroupTableModel) this.model).deleteRow(this.indexes[rowIndex]);
		// this.tableChanged(new TableModelEvent(this, rowIndex, rowIndex,
		// TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
		// this.tableChanged(new TableModelEvent(this));

	}

	/**
	 * Applies a filter to a column.
	 * @param columnIndex the index of the column to be filtered
	 * @param filter the filter to be applied
	 */
	public void applyFilter(final int columnIndex, final Object filter) {
		((GroupTableModel) this.model).applyFilter(columnIndex, filter);
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	public boolean lastFilterOr() {
		return ((GroupTableModel) this.model).lastFilterOr();
	}

	public void addTotalRowOperation(final TotalRowOperation operation) {
		((GroupTableModel) this.model).addTotalRowOperation(operation);
	}

	public List getTotalRowOperation() {
		return ((GroupTableModel) this.model).getTotalRowOperation();
	}

	/**
	 * Applies a group of filters to the table. Each key of the Map contains the name of the
	 * column to be filtered, and the corresponding value will be the filter to apply to that column.
	 * @param filters the filters to apply to the table
	 */
	public void applyFilter(final Map filters) {
		((GroupTableModel) this.model).applyFilter(filters);
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	/**
	 * Applies a group of filters to the table. Each key of the Map contains the name of the
	 * column to be filtered, and the corresponding value will be the filter to apply to that column.
	 * @param filters the filters to apply to the table
	 * @param or true to be established OR-condition.
	 */

	public void applyFilter(final Map filters, final boolean or) {
		((GroupTableModel) this.model).applyFilter(filters, or);
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	/**
	 * Returns the columns that are being filtered.
	 * @return the filtered columns' names
	 */
	public List getFilteredColumns() {
		return ((GroupTableModel) this.model).getFilteredColumns();
	}

	/**
	 * Method fired to update the table after a sorting.
	 */
	@Override
	public void fireTableChanged(final TableModelEvent e) {
		super.fireTableChanged(e);
	}

	/**
	 * Removes all the filters from the table.
	 */
	public void resetFilter() {
		((GroupTableModel) this.model).resetFilter();
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	/**
	 * Removes the filters from the column specified by its name.
	 * @param columnName the column name
	 */
	public void resetFilter(final String columnName) {
		TableSorter.logger.debug("Resert filter column: {}", columnName);
		((GroupTableModel) this.model).resetFilter(columnName);
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	/**
	 * Removes the filters from the column specified by its index.
	 * @param columnIndex the column index
	 */
	public void resetFilter(final int columnIndex) {
		final String col = this.model.getColumnName(columnIndex);
		this.resetFilter(col);
	}

	/**
	 * Removes the sorting applied to the table.
	 */
	public void resetOrder() {
		this.reallocateIndexes();
		this.sortingColumns.clear();
		this.ascendants.clear();
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
		this.fireTableChanged(new TableModelEvent(this));
	}

	/**
	 * Removes the sorting applied to the column specified by its index.
	 * @param columnIndex the column index
	 */
	public void resetOrder(final int columnIndex) {
		TableSorter.logger.debug("Reset column order: {}  Current order : {} , {}->", columnIndex, this.sortingColumns,
				this.ascendants);

		final Integer c = new Integer(columnIndex);
		if (this.sortingColumns.contains(c)) {
			if (this.sortingColumns.size() == 1) {
				this.resetOrder();
			} else {
				final int index = this.sortingColumns.indexOf(c);
				try {
					this.sortingColumns.remove(index);
					this.ascendants.remove(index);
					if (this.sourceTable != null) {
						this.sourceTable.getTableHeader().repaint();
					}
					this.sort(this);
					this.fireTableChanged(new TableModelEvent(TableSorter.this));
				} catch (final Exception ex) {
					TableSorter.logger.error("ResertOrder error", ex);
				}
			}
		}
	}

	/**
	 * Determines whether a column is sorted or not.
	 * @param columnIndex
	 * @return true is the column is sorted, false otherwise
	 */
	public boolean isSorted(final int columnIndex) {
		final Integer c = new Integer(columnIndex);
		if (this.sortingColumns.contains(c)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getRowCount() {
		final TableModel model = this.getModel();
		if (model == null) {
			this.rowsNumber = 0;
			return this.rowsNumber;
		} else {
			this.rowsNumber = ((GroupTableModel) model).getRowCount();
			int rowCount = this.rowsNumber;

			// **Delete sumRow from model 5.3.8
			/*
			 * if ((sumColumns != null) && (!sumColumns.isEmpty())) { if (this.rowsNumber == 0) { rowCount =
			 * this.rowsNumber; } else { rowCount = this.rowsNumber + 1; } }
			 */

			if ((this.sourceTable != null) && this.sourceTable.isEnabled() && this.insertingEnabled
					&& (!this.isSum() || this.allowTotalInserting)) {
				rowCount = rowCount + 1;
			}

			return rowCount;
		}
	}

	/**
	 * Returns the columns that have a calculated expression and the expression.
	 * @return a Map which keys are the column names of the the columns that have a calculated
	 *         expression set, and which values are the corresponding calculates expression to each
	 *         column
	 */
	public Map getCalculatedColumns() {
		return ((GroupTableModel) this.model).getCalculatedColumns();
	}

	/**
	 * Returns the names of the columns that have a calculated expression set.
	 * @return a List with the column names
	 */
	public List getCalculatedColumnsName() {
		return ((GroupTableModel) this.model).getCalculatedColumnsName();
	}

	public List getRequiredColumnsToCalculatedColumns() {
		return ((GroupTableModel) this.model).getRequiredColumnsToCalculatedColumns();
	}

	public Object getCalculatedValue(final int column, final Map rowValues) {
		return ((GroupTableModel) this.model).getCalculatedValue(column, rowValues);
	}

	/**
	 * Returns the column expression for the specified column name.
	 * @param columnName the column name
	 * @return the expression corresponding to the column name passed as parameter; null if no
	 *         expression set
	 */
	public String getCalculatedColumnExpression(final String columnName) {
		return ((GroupTableModel) this.model).getCalculatedColumnExpression(columnName);
	}

	/**
	 * Sets a calculated expression to a colum.
	 * @param columnName the column name
	 * @param expression the calculated expression
	 */
	public void setCalculatedColumnExpression(final String columnName, final String expression) {
		((GroupTableModel) this.model).setCalculatedColumnExpression(columnName, expression);
	}

	/**
	 * Returns the information contained in the specified row.
	 * @param rowIndex the row
	 * @return a Map containing the information corresponding to the row, where the Map keys
	 *         are the columns of the table, and the values are the values for the specified row
	 */
	public Map getRowData(final int rowIndex) {
		final int rowData = this.convertRowIndexToFilteredModel(rowIndex);
		return ((GroupTableModel) this.model).getRowData(rowData);
	}

	public Map getRowDataForKeys(final List keys, final Map keysValues) {
		return ((GroupTableModel) this.model).getRowDataForKeys(keys, keysValues);
	}

	/**
	 * Returns the information contained in the specified row, in case that the table is grouped. When
	 * grouping, several model rows can be grouped into a single row. This method returns all the model
	 * information for the rows, even if the rows are grouped.
	 * @param rowIndex the row index
	 * @return the values corresponding to the grouped index; when the table is grouped, the keys are
	 *         the column names, and because several rows can be grouped, the value is a List with the
	 *         values for the corresponding rows
	 */
	public Map getGroupedRowData(final int rowIndex) {
		if (this.isGrouped()) {
			final int rowData = this.convertRowIndexToFilteredModel(rowIndex);
			return ((GroupTableModel) this.model).getGroupRowData(rowData);
		} else {
			return null;
		}
	}

	/**
	 * Returns a Map with the information of the calculated columns for this row.
	 * @param rowIndex
	 * @return the Map keys are the calculated columns names and the values are the corresponding
	 *         calculated values.
	 */
	public Map getCalculatedRowData(final int rowIndex) {
		final int rowData = this.convertRowIndexToFilteredModel(rowIndex);
		return ((GroupTableModel) this.model).getCalculatedRowData(rowData);
	}

	/**
	 * Enables or disables that filteringcan be applied to the table.
	 * @param enable
	 */
	public void enableFiltering(final boolean enable) {
		this.filterEnabled = enable;
		if (!enable) {
			this.resetFilter();
		}
	}

	/**
	 * Updates the information of a row. The Map contains the new information where the keys are
	 * the column names and the values the corresponding ones for each column. The columns Vector
	 * specifies the columns that are keys, that is, determines the row to be updated. Also the values
	 * for the keys are stored in the Hashtable. The other columns in the Map that are not keys
	 * will have the values to update.
	 * @param rowData the row information
	 * @param keys the columns that are keys and so identifies each row
	 */
	public void updateRowData(final Map rowData, final List keys) {
		final GroupTableModel m = (GroupTableModel) this.model;
		m.updateRowData(rowData, keys);
	}

	public void updateRowData(final Map rowData, final Map keys) {
		final GroupTableModel m = (GroupTableModel) this.model;
		m.updateRowData(rowData, keys);
	}

	public void updateRowData(final Map rowData, final List columns, final Map keys) {
		final GroupTableModel m = (GroupTableModel) this.model;
		m.updateRowData(rowData, columns, keys);
	}

	/**
	 * Adds a new row to the model.
	 * @param rowData a Map containing the information for the new row, when the keys are the
	 *        columns names and the values the corresponding value for each column.
	 */
	public void addRow(final Map rowData) {
		final GroupTableModel m = (GroupTableModel) this.model;
		m.addRow(rowData);
		// TableSorter.this.tableChanged(new TableModelEvent(this));
	}

	/**
	 * Adds a new row in the position specified.
	 * @param viewRowIndex the position that will have the row in the view
	 * @param rowData a Map containing the information for the new row, when the keys are the
	 *        columns names and the values the corresponding value for each column.
	 */
	public void addRow(final int viewRowIndex, final Map rowData) {
		final GroupTableModel m = (GroupTableModel) this.model;
		m.addRow(this.convertRowIndexToFilteredModel(viewRowIndex), rowData);
		// TableSorter.this.tableChanged(new TableModelEvent(this));
	}

	/**
	 * The List contains Hashtables, each one stores the information of the column, this is, the keys
	 * are the column names and the values the values for each column. Each Map corresponds to a
	 * table row. If the table is grouped, grouping is removed.
	 * @param viewRowIndex unused
	 * @param rowData
	 * @deprecated
	 * @see #addRows(List)
	 */
	@Deprecated
	public void addRows(final int[] viewRowIndex, final List rowData) {
		/*
		 * This method at the ends does not use the viewRowIndex, so its behaviour is just like the method
		 * without viewRowIndex
		 */
		final GroupTableModel m = (GroupTableModel) this.model;
		m.addRows(viewRowIndex, rowData);
		// TableSorter.this.tableChanged(new TableModelEvent(this));
	}

	/**
	 * The List contains Hashtables, each one stores the information of the column, this is, the keys
	 * are the column names and the values the values for each column. Each Map corresponds to a
	 * table row. If the table is grouped, grouping is removed. Adds several rows to the table model.
	 * @param rowData
	 */
	public void addRows(final List rowData) {
		final GroupTableModel m = (GroupTableModel) this.model;
		m.addRows(rowData);
		// TableSorter.this.tableChanged(new TableModelEvent(this));
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		if (this.filterDialog != null) {
			this.filterDialog.setResourceBundle(res);
		}
		this.bundle = res;
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(TableSorter.filterKey);
		return v;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.filterDialog.setComponentLocale(l);
		TableSorter.comparator = Collator.getInstance(l);
	}

	/**
	 * Determines whether the specified column us grouped or not.
	 * @param columnIndex the index of the column to check
	 * @return true if the column is grouped, false otherwise
	 */
	public boolean isFiltered(final int columnIndex) {
		if (((GroupTableModel) this.model).isFiltered(columnIndex)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Converts the view index into the corresponding <b>model index</b>. The view index might not be
	 * the same as the model index because of the sorting and the grouping operations that can be
	 * performed in the table view.
	 * @param viewRowIndex the index of the row in the view
	 * @return the model index corresponding to view index passed as parameter; <br>
	 *         -1 in case no match exist
	 */
	public int convertRowIndexToModel(final int viewRowIndex) {
		if ((viewRowIndex < 0) || (viewRowIndex >= this.indexes.length)) {
			return -1;
		}
		return ((GroupTableModel) this.model).convertRowIndexToModel(this.indexes[viewRowIndex]);
	}

	/**
	 * Converts the view index to the corresponding index in the <b>filtered model</b>. The view index
	 * might not be the same as the filtered model index because of the sorting and the grouping
	 * operations that can be performed in the table view.
	 * @param viewRowIndex the index of the row in the view
	 * @return the index of the view row index in the FilterTableModel
	 */
	@Override
	public int convertRowIndexToFilteredModel(final int viewRowIndex) {
		if (this.isInsertingRow(viewRowIndex)) {
			return viewRowIndex;
		}
		if ((viewRowIndex == 0) && (this.indexes.length == 0)) {
			return 0;
		}
		if (viewRowIndex >= this.indexes.length) {
			return this.indexes.length;
		}
		return this.indexes[viewRowIndex];
	}

	/**
	 * Sets a new group of data to the model. The Map containing the data will have as keys the
	 * column names and as values, a List containing one value per row.
	 * @param data
	 */
	public void setData(final Map data) {
		final GroupTableModel m = (GroupTableModel) this.model;
		m.setDatos(data);
	}

	/**
	 * Sums all the values for a specified column.
	 * @param columnName
	 * @return the sum of the values, or null in case the column does not exist
	 */
	public Number getColumnSum(final Object columnName) {
		int columnIndex = -1;
		for (int i = 0; i < this.getColumnCount(); i++) {
			if (this.getColumnIdentifier(i).equals(columnName)) {
				columnIndex = i;
				break;
			}
		}
		if (columnIndex >= 0) {
			double d = 0.0;
			for (int i = 0; i < super.getRowCount(); i++) {
				final Object OValue = this.getValueAt(i, columnIndex);
				if (OValue instanceof Number) {
					d = d + ((Number) OValue).doubleValue();
				}
			}
			return new Double(d);
		} else {
			return null;
		}
	}

	/**
	 * For each column that has an operation configured, returns the result of perform the operation in
	 * the rows passed as parameter.
	 *
	 * @see #getSelectedColumnOperation(Object, int[])
	 * @param rowIndex the row indexes
	 * @return a Map where the keys are the operation columns, and the values the result of
	 *         performing the configured operation in the selected rows
	 */
	protected Map getSelectedColumnOperation(final int[] rowIndex) {
		final Map h = this.getOperationColumn();
		final Map totals = new Hashtable();
		for (int i = 0; i < this.getColumnCount(); i++) {
			final String columnName = this.getColumnName(i);
			if (h.containsKey(columnName)) {
				final Number numericValue = this.getSelectedColumnOperation(columnName, rowIndex);
				if (numericValue != null) {
					totals.put(columnName, numericValue);
				}
			}
		}
		return totals;
	}

	/**
	 * Returns the result of the application of the configured operation (sum, maximum, etc.) to the
	 * rows passed as parameter for the specified column.
	 * @param columnName
	 * @param rowIndex
	 * @return the result of the operation applied to the column restricted by the rows passed as
	 *         parameter; null if the column does not exist
	 */
	@Override
	public Number getSelectedColumnOperation(final Object columnName, final int[] rowIndex) {
		int columnIndex = -1;
		for (int i = 0; i < this.getColumnCount(); i++) {
			if (this.getColumnIdentifier(i).equals(columnName)) {
				columnIndex = i;
				break;
			}
		}
		if ((columnIndex == -1) || ((rowIndex != null) && (rowIndex.length == 1) && this.isInsertingRow(rowIndex[0]))) {
			// If the columns does not exist or the row in the inserting row
			return null;
		}

		if (!this.operationColumns.containsKey(columnName)) {
			return null;
		} else {
			final Object operation = this.operationColumns.get(columnName);
			if (Table.SUM_es_ES.equals(operation)) {
				return this.getColumnSum(columnIndex, false, rowIndex);
			} else if (Table.MAXIMUM_es_ES.equals(operation)) {
				return this.getMaximumMinimumColumn(columnIndex, true, rowIndex);
			} else if (Table.MINIMUM_es_ES.equals(operation)) {
				return this.getMaximumMinimumColumn(columnIndex, false, rowIndex);
			} else {
				return this.getColumnSum(columnIndex, true, rowIndex);
			}
		}
	}

	/**
	 * Returns the value of the operation applied to the specified column.
	 * @param columnName the name of the column
	 * @return the result of performing the operation in the column
	 * @see #getColumnOperation(int)
	 */
	public Number getOperationColumn(final Object columnName) {
		int columnIndex = -1;
		for (int i = 0; i < this.getColumnCount(); i++) {
			if (this.getColumnIdentifier(i).equals(columnName)) {
				columnIndex = i;
				break;
			}
		}
		if (columnIndex == -1) {
			return null;
		}
		if (!this.operationColumns.containsKey(columnName)) {
			return null;
		} else {
			return this.getColumnOperation(columnIndex);
		}
	}

	/**
	 * Returns the operation configured for a column.
	 * @param columnName
	 * @return the configured operation, or null if the operation does not exist.
	 */
	protected Object getColumnType(final Object columnName) {
		return this.operationColumns.get(columnName);
	}

	protected Object getColumnOperation(final String columnIdentifier, final String operation) {
		return ((GroupTableModel) this.model).getColumnOperation(columnIdentifier, operation);
	}

	@Override
	public Number getColumnOperation(final int columnIndex) {
		if (this.isGrouped(columnIndex)) {
			return null;
		}
		final Object column = this.getColumnIdentifier(columnIndex);
		if (!this.operationColumns.containsKey(column)) {
			return null;
		} else {
			final Object operation = this.operationColumns.get(column);
			if (Table.SUM_es_ES.equals(operation)) {
				return this.getColumnSum(columnIndex, false);
			} else if (Table.AVERAGE_es_ES.equals(operation)) {
				return this.getColumnSum(columnIndex, true);
			} else if (Table.MAXIMUM_es_ES.equals(operation)) {
				return this.getMaximumMinimumColumn(columnIndex, true);
			} else if (Table.MINIMUM_es_ES.equals(operation)) {
				return this.getMaximumMinimumColumn(columnIndex, false);
			} else {
				final Object value = this.getColumnOperation(column.toString(), operation.toString());
				if (value instanceof Number) {
					return (Number) value;
				} else {
					return this.getColumnSum(columnIndex, false);
				}
			}
		}
	}

	/**
	 * Calculates the sum or the average for a column.
	 * @param modelColumnIndex the index of the column to calculate the operation
	 * @param average if false, the total value; otherwise, the average value
	 * @return the sum or the average value of the column
	 */
	protected Number getColumnSum(final int modelColumnIndex, final boolean average) {
		if (this.isGrouped(modelColumnIndex)) {
			return null;
		}
		final Object column = this.getColumnIdentifier(modelColumnIndex);

		if (!this.sumColumns.contains(column)) {
			return null;
		}
		if (modelColumnIndex >= 0) {
			double d = 0.0;
			int count = 0;
			for (int i = 0; i < super.getRowCount(); i++) {
				final Object oValue = super.getValueAt(i, modelColumnIndex);
				if (oValue instanceof Number) {
					d = d + ((Number) oValue).doubleValue();
					count++;
				}
			}
			if (average) {
				if (count == 0) {
					return null;
				}
				return new Double(d / count);
			} else {
				return new Double(d);
			}
		} else {
			return null;
		}
	}

	/**
	 * Calculates the sum or the average value for a column; only the values of the specified rows will
	 * be used.
	 * @param columnIndex the column
	 * @param average if true, returns the average value; otherwise, returns the sum
	 * @param rowIndexes the rows that will be used in the calculus
	 * @return
	 */
	protected Number getColumnSum(final int columnIndex, final boolean average, final int[] rowIndexes) {
		if (this.isGrouped(columnIndex)) {
			return null;
		}
		final Object col = this.getColumnIdentifier(columnIndex);

		if (!this.sumColumns.contains(col)) {
			return null;
		}
		if (columnIndex >= 0) {
			double d = 0.0;
			int count = 0;
			for (int i = 0; i < rowIndexes.length; i++) {
				final int indSel = rowIndexes[i];
				if (this.isSumRow(indSel) || this.isInsertingRow(indSel)) {
					continue;
				}
				final Object oValue = super.getValueAt(indSel, columnIndex);
				if (oValue instanceof Number) {
					d = d + ((Number) oValue).doubleValue();
					count++;
				}
			}
			if (average) {
				if (count == 0) {
					return null;
				}
				return new Double(d / count);
			} else {
				return new Double(d);
			}
		} else {
			return null;
		}
	}

	/*
	 * private
	 */
	private Number getMaximumMinimumColumn(final int column, final boolean max, final int[] rows) {
		final Object col = this.getColumnIdentifier(column);

		if (!this.sumColumns.contains(col)) {
			return null;
		}

		if (column >= 0) {
			final List listValues = new Vector();
			for (int i = 0; i < rows.length; i++) {
				final int indSel = rows[i];
				if (this.isSumRow(indSel)) {
					continue;
				}
				final Object oValue = super.getValueAt(indSel, column);
				if (oValue instanceof Number) {
					listValues.add(oValue);
				}
			}

			if (listValues.size() == 0) {
				return null;
			}
			Object o = null;
			if (max) {
				o = Collections.max(listValues);
			} else {
				o = Collections.min(listValues);
			}
			return (Number) o;
		} else {
			return null;
		}
	}

	/*
	 *
	 */
	protected Number getMaximumMinimumColumn(final int index, final boolean max) {
		final Object column = this.getColumnIdentifier(index);

		if (!this.sumColumns.contains(column)) {
			return null;
		}

		if (index >= 0) {
			final List listValues = new Vector();
			for (int i = 0; i < super.getRowCount(); i++) {
				final Object oValue = super.getValueAt(i, index);
				if (oValue instanceof Number) {
					listValues.add(oValue);
				}
			}

			if (listValues.size() == 0) {
				return null;
			}
			Object o = null;
			if (max) {
				o = Collections.max(listValues);
			} else {
				o = Collections.min(listValues);
			}
			if (o == null) {
				return null;
			}
			return (Number) o;
		} else {
			return null;
		}
	}

	/**
	 * Determines whether exists sum columns in the model. Sum columns are special columns that are
	 * calculated in the model, and came from operations between other columns.
	 * @return true if sum column exits, false if not.
	 */
	@Override
	public boolean isSum() {
		if ((this.sumColumns != null) && !this.sumColumns.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines whether a cell belongs to a sumrow or not. Sumrow are rows that operates with the
	 * information contained in the column, and display as result, for example, the sum of all the
	 * values of the column.
	 * @param rowIndex the row index
	 * @param colIndex the column index
	 * @return true if the cell is a sumrow, false otherwise
	 * @deprecated in version 5.3.8 sumrow was removed from TableSorter
	 */
	@Deprecated
	public boolean isSumCell(final int rowIndex, final int colIndex) {
		// if (isInsertingRow(rowIndex) ||colIndex == 0) {
		// return false;
		// }
		// if (this.sumColumns != null && this.sumColumns.isEmpty(!)) {
		// if (((rowIndex == this.rowsNumber) && (lastIsSum)) || ((rowIndex ==
		// 0) && (!lastIsSum))) {
		// return true;
		// }
		// return false;
		// }
		return false;
	}

	/**
	 * Determines whether a row is a sumrow, that is, is a row that displays the result of performing
	 * operations with the columns. Not all the cells in a row are sumcells.
	 * @param rowIndex
	 * @return true if the row has some sumcell, false otherwise
	 * @deprecated in version 5.3.8
	 */
	@Deprecated
	public boolean isSumRow(final int rowIndex) {
		if (this.isInsertingRow(rowIndex)) {
			return false;
		}
		for (int i = 0; i < this.getColumnCount(); i++) {
			if (this.isSumCell(rowIndex, i)) {
				return true;
			}
		}
		return false;
	}

	public boolean isInsertingRow(final int row) {
		if (this.insertingEnabled && (row == (this.getRowCount() - 1)) && this.sourceTable.isEnabled()
				&& (!this.isSum() || this.allowTotalInserting)) {
			return true;
		}
		return false;
	}

	/**
	 * Provides a renderer for the rum row. In the information to display is monetary, a cell renderer
	 * with currency features can be retrieved.
	 * @param currency if true, the renderer accounts for currency information; if false, renders
	 *        numbers
	 * @return the renderer
	 */
	@Override
	public TableCellRenderer getSumCellRenderer(final boolean currency) {
		return ((GroupTableModel) this.model).getSumCellRenderer(currency);
	}

	/**
	 * Fires a {@link ColumnSizeEvent} to be processed by the {@link ColumnSizeListener} configured in
	 * the model.
	 * @param columnIndex the column that produces the event
	 */
	public void fireSizeColumnToFit(final int columnIndex) {
		if (this.sizeColumnListener != null) {
			this.sizeColumnListener.columnToFitSize(new ColumnSizeEvent(this, columnIndex));
		}
	}

	/**
	 * Sets the {@link ColumnSizeListener} to this TableSorter.
	 * @param columnSizeListener
	 */
	public void setSizeColumnListener(final ColumnSizeListener columnSizeListener) {
		this.sizeColumnListener = columnSizeListener;
	}

	/**
	 * Determines whether the model is filtered or not.
	 * @return true if the model is filtered, false otherwise
	 */
	public boolean isFiltered() {
		return ((GroupTableModel) this.model).isFiltered();
	}

	/**
	 * Returns the information contained in the model but the sumrows.
	 * @return the information in the view, what the client is seeing , where the keys are the column
	 *         names and the values a List with the information of each column, as it is stored in the
	 *         model.
	 */
	public Map getShownValue() {
		// TODO recheck the javadoc
		final Map fData = new Hashtable();
		for (int i = 0; i < this.getColumnCount(); i++) {
			final Object id = this.getColumnIdentifier(i);
			final List vData = new Vector();
			for (int j = 0; j < this.getRowCount(); j++) {
				if (!this.isSumRow(j) && !this.isInsertingRow(j)) {
					Object cellValue = this.getValueAt(j, i);
					if (cellValue instanceof GroupList) {
						cellValue = ((GroupList) cellValue).toString();
					} else if (cellValue instanceof ValueByGroup) {
						cellValue = ((ValueByGroup) cellValue).getValue();
					}
					vData.add(j, cellValue);
				}
			}
			fData.put(id, vData);
		}
		return fData;
	}

	public Map getShownValue(final String cols[]) {
		if (cols == null) {
			return this.getShownValue();
		}
		final Map fData = new Hashtable();
		for (int i = 0; i < this.getColumnCount(); i++) {
			final Object id = this.getColumnIdentifier(i);
			boolean found = false;
			for (int k = 0; k < cols.length; k++) {
				if ((cols[k] != null) && cols[k].equals(id)) {
					found = true;
					break;
				}
			}
			if (!found) {
				continue;
			}
			final List vData = new Vector();
			for (int j = 0; j < this.getRowCount(); j++) {
				if (!this.isSumRow(j) && !this.isInsertingRow(j)) {
					Object cellValue = this.getValueAt(j, i);
					if ((cellValue != null) && (cellValue instanceof GroupList)) {
						cellValue = ((GroupList) cellValue).description;
					} else if ((cellValue != null) && (cellValue instanceof ValueByGroup)) {
						cellValue = ((ValueByGroup) cellValue).getValue().toString();
					}
					vData.add(j, cellValue);
				}
			}
			fData.put(id, vData);
		}

		return fData;
	}

	/**
	 * Returns all the information stored in the model, including the calculated columns.
	 * @return the information in the model
	 */
	public Map getData() {
		return ((GroupTableModel) this.model).getData();
	}

	/**
	 * Returns the data contained in the model but according to the filters that are being applied to
	 * that model
	 * @return the filtered information
	 */
	public Map getFilteredData() {
		return ((GroupTableModel) this.model).getFilteredData();
	}

	/**
	 * Returns the index of the sumrow
	 * @return the index of the sum row
	 */
	public int getCurrentRowCount() {
		return ((GroupTableModel) this.model).getCurrentRowCount();
	}

	/**
	 * Returns the number of records or rows that are actually stored in the model.
	 * @return the real number of rows contained by the model
	 */
	public int getRealRecordNumber() {
		return ((GroupTableModel) this.model).getRealRecordNumber();
	}

	/**
	 * Allows a column to be editable, that is, sets a editor to the column in order that the column
	 * value can be editable from the GUI.
	 * @param columnName the column name to be editable
	 */
	public void setEditableColumn(final String columnName) {
		((GroupTableModel) this.model).setEditableColumn(columnName);
	}

	/**
	 * Configures a column to be editable or not.
	 * @param columnName the column name
	 * @param editable if true, the column could be edited from the GUI; if false, the column can't be
	 *        edited from the GUI
	 */
	public void setEditableColumn(final String columnName, final boolean editable) {
		((GroupTableModel) this.model).setEditableColumn(columnName, editable);
	}

	/**
	 * Returns the column name that corresponds to a specified index.
	 * @param modelColumnIndex the column index
	 * @return the corresponding column name
	 */
	public Object getColumnIdentifier(final int modelColumnIndex) {
		return ((GroupTableModel) this.model).getColumnIdentifier(modelColumnIndex);
	}

	/**
	 * Returns the column texts.
	 * @return the column texts
	 */
	public List getColumnsText() {
		return ((GroupTableModel) this.model).getColumnsText();
	}

	/**
	 * Returns the column names.
	 * @return the column names
	 */
	public List getColumnNames() {
		return ((GroupTableModel) this.model).getColumnNames();
	}

	/**
	 * Adds a new column to the model.
	 * @param columnName the new column name
	 */
	public void addColumn(final String columnName) {
		((GroupTableModel) this.model).addColumn(columnName);
	}

	public void addColumn(final String columnName, final boolean event) {
		((GroupTableModel) this.model).addColumn(columnName, event);
	}

	public void addCalculatedColumn(final String columnName, final String expression) {
		((GroupTableModel) this.model).addCalculatedColumn(columnName, expression);
	}

	/**
	 * Register new operation for group operation. If the programmer want to use this operation as
	 * default operation must set the id operation like default operation.
	 *
	 * @see #setDefaultGroupedColumnFunction(int modelColumnIndex, int function)
	 * @param id identifier for operation. It must be higher than 4.
	 * @param operation an instanceof <code>GroupOperation</code>
	 */
	public void addGroupedFunction(final int id, final GroupOperation operation) {
		((GroupTableModel) this.model).addGroupedFunction(id, operation);
	}

	/**
	 * Deletes a column from the model.
	 * @param columnName
	 */
	public void deleteColumn(final String columnName) {
		this.deleteColumn(columnName, true);
	}

	public void deleteColumn(final String columnName, final boolean fireEvent) {
		int ind = -1;
		for (int i = 0; i < this.getColumnCount(); i++) {
			if (columnName.equals(this.getColumnIdentifier(i))) {
				ind = i;
				break;
			}
		}
		if (ind >= 0) {
			this.resetOrder(ind);
		}
		((GroupTableModel) this.model).deleteColumn(columnName, fireEvent);
	}

	public void deleteCalculatedColumn(final String columnName) {
		int ind = -1;
		for (int i = 0; i < this.getColumnCount(); i++) {
			if (columnName.equals(this.getColumnIdentifier(i))) {
				ind = i;
				break;
			}
		}
		if (ind >= 0) {
			this.resetOrder(ind);
		}
		((GroupTableModel) this.model).deleteCalculatedColumn(columnName);
	}

	/**
	 * Groups the model by a column. The grouping will be by year, assuming that the column is a date
	 * column.
	 * @param modelColumnIndex
	 */
	public void group(final int modelColumnIndex) {
		((GroupTableModel) this.model).group(modelColumnIndex);
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	/**
	 * Groups the model by a column and using a concrete grouping approach. Types are defined in the
	 * TableSorter class, and can be at least:
	 * <ul>
	 * <li>YEAR</li>
	 * <li>YEAR_MONTH</li>
	 * <li>YEAR_MONTH_DAY</li>
	 * <li>QUARTER_YEAR</li>
	 * <li>QUARTER</li>
	 * <li>MONTH</li>
	 * </ul>
	 * @param modelColumnIndex the column used to group the table
	 * @param type the column type
	 */
	public void group(final int modelColumnIndex, final int type) {
		((GroupTableModel) this.model).group(modelColumnIndex, type);
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	public Map getOperations() {
		return this.operations;
	}

	/**
	 * Applies a function to a column. Types are defined in the TableSorter class, and can be at least:
	 * <ul>
	 * <li>SUM</li>
	 * <li>AVG</li>
	 * <li>MAX</li>
	 * <li>MIN</li>
	 * <li>COUNT</li>
	 * </ul>
	 * @param modelColumnIndex
	 * @param function
	 */
	public void setGroupedColumnFunction(final int modelColumnIndex, final int function) {
		((GroupTableModel) this.model).setGroupedColumnFunction(modelColumnIndex, function);
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	/**
	 * Applies a DEFAULT function to a column. Types are defined in the TableSorter class, and can be at
	 * least:
	 * <ul>
	 * <li>SUM</li>
	 * <li>AVG</li>
	 * <li>MAX</li>
	 * <li>MIN</li>
	 * <li>COUNT</li>
	 * </ul>
	 * @param modelColumnIndex
	 * @param function
	 */

	public void setDefaultGroupedColumnFunction(final int modelColumnIndex, final int function) {
		((GroupTableModel) this.model).setDefaultGroupedColumnFunction(modelColumnIndex, function);
	}

	/**
	 * Provides the function applied to the specified column
	 * @param modelColumnIndex the column index
	 * @return the operation type
	 *         <ul>
	 *         <li>SUM</li>
	 *         <li>AVG</li>
	 *         <li>MAX</li>
	 *         <li>MIN</li>
	 *         <li>COUNT</li>
	 *         </ul>
	 */
	public int getGroupedColumnFunction(final int modelColumnIndex) {
		return ((GroupTableModel) this.model).getGroupColumnFunction(modelColumnIndex);
	}

	/**
	 * Deletes all the grouping in the table.
	 */
	public void resetGroup() {
		((GroupTableModel) this.model).resetGroup();
		if (this.sourceTable != null) {
			this.sourceTable.getTableHeader().repaint();
		}
	}

	/**
	 * Determines whether a column is grouped or not.
	 * @param col
	 * @return
	 */
	public boolean isGrouped(final int col) {
		return ((GroupTableModel) this.model).isGrouped(col);
	}

	/**
	 * Determines whether the table has a grouping applied or not.
	 * @return
	 */
	public boolean isGrouped() {
		return ((GroupTableModel) this.model).isGrouped();
	}

	/**
	 * Sets a new group of operation columns.
	 * @param operationColumns the new operation columns configuration, where the keys determines the
	 *        column names and the values are the operations to be applied to those columns.
	 */
	public void setOperationColumns(final Map operationColumns) {
		this.operationColumns = operationColumns;
		final Enumeration enu = Collections.enumeration(this.operationColumns.keySet());
		this.sumColumns.clear();
		while (enu.hasMoreElements()) {
			final Object o = enu.nextElement();
			this.sumColumns.add(o);
		}
		this.fireTableDataChanged();
	}

	/**
	 * Determines whether the first sorting in the table is ascending or descending.
	 * @return true if the first ordenation in the table is ascending; false otherwise
	 */
	public boolean isAscending() {
		if (!this.sortingColumns.isEmpty()) {
			return ((Boolean) this.ascendants.get(0)).booleanValue();
		} else {
			return false;
		}
	}

	/**
	 * Determines whether a column sorting is ascending or not.
	 * @param columnIndex
	 * @return
	 */
	public boolean isAscending(final int columnIndex) {
		if (this.isSorted(columnIndex)) {
			final int index = this.sortingColumns.indexOf(new Integer(columnIndex));
			return ((Boolean) this.ascendants.get(index)).booleanValue();
		} else {
			return false;
		}
	}

	protected boolean fitHeadSize = false;

	/**
	 * Configures the table to auto adjust the header size.
	 * @param fit
	 */
	public void setFitHeadSize(final boolean fit) {
		this.fitHeadSize = fit;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	protected void setPreferredHeadSize() {
		if (this.fitHeadSize) {
		}
	}

	/**
	 * Returns the filters for the table.
	 * @return
	 */
	public Map getFilters() {
		return (Map) ((GroupTableModel) this.model).getFilters();
	}

	/**
	 * Returns the filters applied to a column.
	 * @param columnName
	 * @return
	 */
	public Object getColumnFilter(final String columnName) {
		return ((GroupTableModel) this.model).getColumnFilter(columnName);
	}

	/**
	 * Determines whether the table is being sorted or not.
	 * @return true if the table is being sorted; false otherwise.
	 */
	public boolean isSorted() {
		return !this.sortingColumns.isEmpty();
	}

	/**
	 * Returns the index of the first sorted column.
	 * @return the index of the first sorted column, -1 if no sorting exists.
	 */
	public int getFirstSortedColumn() {
		if (this.isSorted()) {
			return ((Integer) this.sortingColumns.get(0)).intValue();
		} else {
			return -1;
		}
	}

	/**
	 * Returns the indexes of all the columns that has a sorting in the table, following the sorting
	 * order.
	 * @return the indexes of the sorting columns, in order; if no indexes are being applied, returns an
	 *         empty array.
	 */
	@Override
	public int[] getSortingColumns() {
		if (this.isSorted()) {
			final int[] res = new int[this.sortingColumns.size()];
			for (int i = 0; i < res.length; i++) {
				res[i] = ((Integer) this.sortingColumns.get(i)).intValue();
			}
			return res;
		} else {
			return new int[0];
		}
	}

	/**
	 * Returns the indexes of all the columns that has an <b>ascending</b> sorting in the table,
	 * following the sorting order.
	 * @return the indexes of the sorting columns, in the order the are being applied; if no indexes are
	 *         being applied, returns an empty array.
	 */
	public boolean[] getAscendent() {
		if (this.isSorted()) {
			final boolean[] res = new boolean[this.sortingColumns.size()];
			for (int i = 0; i < res.length; i++) {
				res[i] = ((Boolean) this.ascendants.get(i)).booleanValue();
			}
			return res;
		} else {
			return new boolean[0];
		}
	}

	protected static class IconN implements Icon {

		Icon icon = null;

		int index = -1;

		public IconN(final Icon i, final int ind) {
			this.icon = i;
			this.index = ind;
		}

		@Override
		public int getIconHeight() {
			return this.icon.getIconHeight();
		}

		@Override
		public int getIconWidth() {
			return this.icon.getIconWidth();
		}

		@Override
		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			this.icon.paintIcon(c, g, x, y);
			if (TableSorter.MULTIORDER_PERMIT) {
				// Color color = g.getColor();
				g.setColor(Color.red);
				final Font fAnt = g.getFont();
				try {
					g.setFont(g.getFont().deriveFont((float) 9));
					g.drawString(Integer.toString(this.index + 1), x + 11, y + 6);
				} catch (final Exception e) {
					TableSorter.logger.trace(null, e);
				}
				g.setFont(fAnt);
			}
		}

	}

	/**
	 * Sets a {@link FilterValidator} to the model.
	 * @param filterValidator the filter validator
	 */
	public void setFilterValidator(final FilterValidator filterValidator) {
		((GroupTableModel) this.model).setFilterValidator(filterValidator);
	}

	/**
	 * Returns the filter validator configured in the model.
	 * @return the current filter validator
	 */
	public FilterValidator getFilterValidator() {
		return ((GroupTableModel) this.model).getFilterValidator();
	}

	/**
	 * Returns the current operation columns.
	 * @return the current operation columns, where the keys are the column names, and the values the
	 *         operations performed in each column.
	 */
	public Map getOperationColumn() {
		return this.operationColumns;
	}

	/**
	 * Enable or disable the inserting row
	 * @param enabled
	 */
	public void setInsertEnabled(final boolean enabled) {
		if (this.insertingEnabled != enabled) {
			this.insertingEnabled = enabled;

			this.fireTableRowsInserted(this.getRowCount() - 1, this.getRowCount() - 1);
		}
	}

	public boolean isInsertingEnabled() {
		return this.insertingEnabled;
	}

	public Map getInsertingData() {
		if (this.insertingEnabled) {
			return this.insertingRowData;
		}
		return null;
	}

	public void clearInsertingRow(final Map parentkeys) {
		this.insertingRowData.clear();
		if ((parentkeys != null) && !parentkeys.isEmpty()) {
			this.insertingRowData.putAll(parentkeys);
		}
	}

	/**
	 * Checks if the sorter is local. If the table is pageable the sorter is implemented via a SQL
	 * statements.
	 * @return
	 */
	public boolean isLocalSorter() {
		return this.localSorter;
	}

	public void setLocalSorter(final boolean localSorter) {
		this.localSorter = localSorter;
	}

}
