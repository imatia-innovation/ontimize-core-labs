package com.ontimize.report;

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel) and itself implements
 * TableModel. TableSorter does not store or copy the data in the TableModel, instead it maintains
 * an array of integers which it keeps the same size as the number of rows in its model. When the
 * model changes it notifies the sorter that something has changed eg. "rowsAdded" so that its
 * internal array of integers can be reallocated. As requests are made of the sorter (like
 * getValueAt(row, col) it redirects them to its model via the mapping array. That way the
 * TableSorter appears to hold another copy of the table with the rows in a different order. The
 * sorting algorthm used is stable which means that it does not move around rows when its comparison
 * function returns 0 to denote that they are equivalent.
 *
 * @version 1.12 01/23/03
 * @author Philip Milne
 */

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils.EntityResultTableModel;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

public class TableSorter extends TableMap {

	private static final Logger logger = LoggerFactory.getLogger(TableSorter.class);

	protected int indexes[];

	public int[] getIndexes() {
		return this.indexes;
	}

	protected List sortingColumns = new Vector();

	protected List ascendings = new Vector();

	int compares;

	public TableSorter() {
		this.indexes = new int[0]; // For consistency.
	}

	public TableSorter(final TableModel model) {
		this.setModel(model);
	}

	@Override
	public void setModel(final TableModel model) {
		super.setModel(model);
		this.reallocateIndexes();
	}

	public EntityResult getOrderedEntityResult(final TableModel model) {
		final EntityResult res = new EntityResultMapImpl();
		for (int i = 0; i < this.indexes.length; i++) {
			if (model == null) {
				break;
			}
			res.addRecord(((EntityResultTableModel) model).getEntityResult().getRecordValues(this.indexes[i]));
		}
		return res;
	}

	public EntityResult getEntityResult(final TableModel model) {
		final EntityResult res = new EntityResultMapImpl();
		for (int i = 0; i < this.indexes.length; i++) {
			res.addRecord(((EntityResultTableModel) model).getEntityResult().getRecordValues(this.indexes[i]));
		}
		return res;
	}

	public int compareRowsByColumn(final int row1, final int row2, final int column) {
		final Class type = this.model.getColumnClass(column);
		final TableModel data = this.model;

		// Check for nulls

		final Object o1 = data.getValueAt(row1, column);
		final Object o2 = data.getValueAt(row2, column);
		// If both values are null return 0
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
		if (type.getSuperclass() == java.lang.Number.class) {
			final Number n1 = (Number) data.getValueAt(row1, column);
			final double d1 = n1.doubleValue();
			final Number n2 = (Number) data.getValueAt(row2, column);
			final double d2 = n2.doubleValue();

			if (d1 < d2) {
				return -1;
			} else if (d1 > d2) {
				return 1;
			} else {
				return 0;
			}
		} else if ((type == java.util.Date.class) || (type == java.sql.Date.class)
				|| (type == java.sql.Timestamp.class)) {
			final Date d1 = (Date) data.getValueAt(row1, column);
			final long n1 = d1.getTime();
			final Date d2 = (Date) data.getValueAt(row2, column);
			final long n2 = d2.getTime();

			if (n1 < n2) {
				return -1;
			} else if (n1 > n2) {
				return 1;
			} else {
				return 0;
			}
		} else if (type == String.class) {
			final String s1 = (String) data.getValueAt(row1, column);
			final String s2 = (String) data.getValueAt(row2, column);
			final int result = s1.compareTo(s2);
			if (result < 0) {
				TableSorter.logger.debug("{} less than {}", s1, s2);
				return -1;
			} else if (result > 0) {
				TableSorter.logger.debug("{} higher than {}", s1, s2);
				return 1;
			} else {
				TableSorter.logger.debug("{} equals to {}", s1, s2);
				return 0;
			}
		} else if (type == Boolean.class) {
			final Boolean bool1 = (Boolean) data.getValueAt(row1, column);
			final boolean b1 = bool1.booleanValue();
			final Boolean bool2 = (Boolean) data.getValueAt(row2, column);
			final boolean b2 = bool2.booleanValue();

			if (b1 == b2) {
				return 0;
			} else if (b1) {// Define false < true
				return 1;
			} else {
				return -1;
			}
		} else {
			final Object v1 = data.getValueAt(row1, column);
			final String s1 = v1.toString();
			final Object v2 = data.getValueAt(row2, column);
			final String s2 = v2.toString();
			final int result = s1.compareTo(s2);

			if (result < 0) {
				TableSorter.logger.debug("{} less than {}", s1, s2);
				return -1;
			} else if (result > 0) {
				TableSorter.logger.debug("{} higher than {}", s1, s2);
				return 1;
			} else {
				TableSorter.logger.debug("{} equals to {}", s1, s2);
				return 0;
			}
		}
	}

	public int compare(final int row1, final int row2) {
		this.compares++;
		for (int level = 0; level < this.sortingColumns.size(); level++) {
			final Integer column = (Integer) this.sortingColumns.get(level);
			final boolean ascending = ((Boolean) this.ascendings.get(level)).booleanValue();
			final int result = this.compareRowsByColumn(row1, row2, column.intValue());
			if (result != 0) {
				return ascending ? result : -result;
			}
		}
		return 0;
	}

	public void reallocateIndexes() {
		final int rowCount = this.model.getRowCount();

		// Set up a new array of indexes with the right number of elements
		// for the new data model.
		this.indexes = new int[rowCount];

		// Initialize with the identity mapping.
		for (int row = 0; row < rowCount; row++) {
			this.indexes[row] = row;
		}
	}

	@Override
	public void tableChanged(final TableModelEvent e) {
		TableSorter.logger.info("Sorter: tableChanged");
		this.reallocateIndexes();

		super.tableChanged(e);
	}

	public void checkModel() {
		if (this.indexes.length != this.model.getRowCount()) {
			TableSorter.logger.error("Sorter not informed of a change in model.");
		}
	}

	public void sort(final Object sender) {
		this.checkModel();

		this.compares = 0;
		this.shuttlesort(this.indexes.clone(), this.indexes, 0, this.indexes.length);
	}

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
	// NlogN assignments shuttling the values between the two
	// arrays. The number of compares appears to vary between N-1 and
	// NlogN depending on the initial order but the main reason for
	// using it here is that, unlike sort, it is stable.
	public void shuttlesort(final int from[], final int to[], final int low, final int high) {
		if ((high - low) < 2) {
			return;
		}
		final int middle = (low + high) / 2;
		this.shuttlesort(to, from, low, middle);
		this.shuttlesort(to, from, middle, high);

		int p = low;
		int q = middle;

		/*
		 * This is an optional short-cut; at each recursive call, check to see if the elements in this
		 * subset are already ordered. If so, no further comparisons are needed; the sub-array can just be
		 * copied. The array must be copied rather than assigned otherwise sister calls in the recursion
		 * might get out of sinc. When the number of elements is three they are partitioned so that the
		 * first set, [low, mid), has one element and and the second, [mid, high), has two. We skip the
		 * optimization when the number of elements is three or less as the first compare in the normal
		 * merge will produce the same sequence of steps. This optimization seems to be worthwhile for
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

	public void swap(final int i, final int j) {
		final int tmp = this.indexes[i];
		this.indexes[i] = this.indexes[j];
		this.indexes[j] = tmp;
	}

	// The mapping only affects the contents of the data rows.
	// Pass all requests to these rows through the mapping array: "indexes".

	@Override
	public Object getValueAt(final int aRow, final int aColumn) {
		this.checkModel();
		return this.model.getValueAt(this.indexes[aRow], aColumn);
	}

	@Override
	public void setValueAt(final Object aValue, final int aRow, final int aColumn) {
		this.checkModel();
		this.model.setValueAt(aValue, this.indexes[aRow], aColumn);
	}

	public void sortByColumn(final int column) {
		this.sortByColumn(column, true);
	}

	public void sortByColumn(final int column, final boolean ascending) {

		final Integer col = new Integer(column);

		if (!this.sortingColumns.contains(col)) {
			this.sortingColumns.add(col);
			if (ascending) {
				this.ascendings.add(Boolean.TRUE);
			} else {
				this.ascendings.add(Boolean.FALSE);
			}
		} else {
			final int iIndex = this.sortingColumns.indexOf(col);
			this.ascendings.set(iIndex, new Boolean(ascending));
		}
		this.sort(this);
		this.fireTableChanged(new TableModelEvent(TableSorter.this));
	}

	// There is no-where else to put this.
	// Add a mouse listener to the Table to trigger a table sort
	// when a column heading is clicked in the JTable.
	public void addMouseListenerToHeaderInTable(final JTable table) {
		final TableSorter sorter = this;
		final JTable tableView = table;
		tableView.setColumnSelectionAllowed(false);
		final MouseAdapter listMouseListener = new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				final TableColumnModel columnModel = tableView.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = tableView.convertColumnIndexToModel(viewColumn);
				if ((e.getClickCount() == 1) && (column != -1)) {
					TableSorter.logger.info("Sorting ...");
					final int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
					final boolean ascending = shiftPressed == 0;
					sorter.sortByColumn(column, ascending);
				}
			}
		};
		final JTableHeader th = tableView.getTableHeader();
		th.addMouseListener(listMouseListener);
	}

	public void resetOrder() {
		this.reallocateIndexes();
		this.sortingColumns.clear();
		this.ascendings.clear();
		this.fireTableChanged(new TableModelEvent(this));
	}

	public void resetOrder(final int col) {
		final Integer c = new Integer(col);
		if (this.sortingColumns.contains(c)) {
			if (this.sortingColumns.size() == 1) {
				this.resetOrder();
			} else {
				final int iIndex = this.sortingColumns.indexOf(c);
				// this.sortedColumn = -1;
				// this.ascending= true;
				this.sortingColumns.remove(iIndex);
				this.ascendings.remove(iIndex);
				this.sort(this);
				this.fireTableChanged(new TableModelEvent(TableSorter.this));

			}
		}
	}

}
