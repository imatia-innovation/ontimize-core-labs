package com.ontimize.db;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.TableSorter;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.tools.ImageTools;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.swing.image.BooleanImage;

public class EntityResultUtils extends com.ontimize.jee.common.util.EntityResultUtils {

	private static final Logger logger = LoggerFactory.getLogger(EntityResultUtils.class);

	public static class EntityResultTableModel extends AbstractTableModel {

		private static final Image check = ImageManager.getIcon(ImageManager.CHECK_SELECTED).getImage();

		private static final Image uncheck = ImageManager.getIcon(ImageManager.CHECK_UNSELECTED).getImage();

		protected List columns = new Vector();

		protected List dataVectors = new Vector();

		protected int rowsNumber = 0;

		protected EntityResult res = null;

		protected boolean returnEmptyStrings = false;

		public EntityResultTableModel(final EntityResult res) {
			this(res, false, true, true);
		}

		public EntityResultTableModel(final EntityResult res, final boolean returnEmptyStrings, final boolean convertBytesBlockToIm,
				final boolean convertBooleanToIm) {
			super();
			this.res = res;
			this.rowsNumber = res.calculateRecordNumber();
			final Enumeration enumKeys = res.keys();
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				this.columns.add(oKey);
				this.dataVectors.add(res.get(oKey));
				if (convertBytesBlockToIm || convertBooleanToIm) {
					final List vector = (List) res.get(oKey);
					for (int i = 0; i < vector.size(); i++) {
						final Object v = vector.get(i);
						if ((v instanceof BytesBlock) && convertBytesBlockToIm) {
							try {
								final Image im = new ImageIcon(((BytesBlock) v).getBytes()).getImage();
								vector.set(i, im);
							} catch (final Exception ex) {
								EntityResultUtils.logger.trace(null, ex);
							}
						} else if ((v instanceof Boolean) && convertBooleanToIm) {
							try {
								final boolean bValue = ((Boolean) v).booleanValue();
								final Image image = bValue ? EntityResultTableModel.check : EntityResultTableModel.uncheck;
								final BufferedImage bImage = ImageTools.imageToBufferedImage(image);
								final BooleanImage booleanImage = new BooleanImage(bValue, bImage);
								vector.set(i, booleanImage);
							} catch (final Exception ex) {
								EntityResultUtils.logger.trace(null, ex);
							}
						}
					}
				}
			}
			this.returnEmptyStrings = returnEmptyStrings;
		}

		@Override
		public Class getColumnClass(final int column) {
			if (column >= this.dataVectors.size()) {
				return super.getColumnClass(column);
			}
			final List v = (List) this.dataVectors.get(column);
			for (int i = 0; i < v.size(); i++) {
				if (v.get(i) != null) {
					return v.get(i).getClass();
				}
			}
			return super.getColumnClass(column);
		}

		@Override
		public String getColumnName(final int c) {
			if (c < this.columns.size()) {
				return this.columns.get(c).toString();
			} else {
				return super.getColumnName(c);
			}
		}

		@Override
		public int getRowCount() {
			return this.rowsNumber;
		}

		@Override
		public int getColumnCount() {
			return this.columns.size();
		}

		@Override
		public Object getValueAt(final int f, final int c) {
			try {
				if (c >= this.dataVectors.size()) {
					return null;
				}
				final List v = (List) this.dataVectors.get(c);
				if (f >= v.size()) {
					return this.returnEmptyStrings ? "" : null;
				}
				return v.get(f);
			} catch (final Exception e) {
				EntityResultUtils.logger.trace(null, e);
				return this.returnEmptyStrings ? "" : null;
			}
		}

		@Override
		public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
			if (columnIndex >= this.dataVectors.size()) {
				return;
			}
			final List v = (List) this.dataVectors.get(columnIndex);
			if (rowIndex >= v.size()) {
				return;
			}
			v.set(rowIndex, value);
			this.dataVectors.set(columnIndex, v);
		}

		public EntityResult getEntityResult() {
			return this.res;
		}

	}

	public static TableModel createTableModel(final EntityResult res) {
		return new EntityResultTableModel(res);
	}

	public static TableModel createTableModel(final EntityResult res, final List cols) {
		return EntityResultUtils.createTableModel(res, cols, false);
	}

	public static TableModel createTableModel(final EntityResult res, final List cols, final boolean returnEmptyStrings) {
		return EntityResultUtils.createTableModel(res, cols, returnEmptyStrings, true, true);
	}

	public static TableModel createTableModel(final EntityResult res, final List cols, final boolean returnEmptyStrings,
			final boolean convertBytesBlockToIm, final boolean convertBooleanToIm) {
		final EntityResult resN = new EntityResultMapImpl();
		for (int i = 0; i < cols.size(); i++) {
			if (res.containsKey(cols.get(i))) {
				resN.put(cols.get(i), res.get(cols.get(i)));
			}
		}
		resN.setColumnOrder(cols);
		return new EntityResultTableModel(resN, returnEmptyStrings, convertBytesBlockToIm, convertBooleanToIm);
	}

	/**
	 * @param res Values of this object must be List elements
	 * @return
	 */
	public static TableModel createTableModel(final Map res) {
		return EntityResultUtils.createTableModel(res, false);
	}

	public static TableModel createTableModel(final Map res, final boolean returnEmptyStrings) {
		return EntityResultUtils.createTableModel(res, returnEmptyStrings, true, true);
	}

	public static TableModel createTableModel(final Map<?, ?> res, final boolean returnEmptyStrings,
			final boolean convertBB2Im,
			final boolean convertBooleanToIm) {
		final EntityResult result = new EntityResultMapImpl();
		for (final Entry<?, ?> entry : res.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return new EntityResultTableModel(result, returnEmptyStrings, convertBB2Im, convertBooleanToIm);
	}

	public static TableModel createTableModel(final Map res, final List cols) {
		return EntityResultUtils.createTableModel(res, cols, false);
	}

	public static TableModel createTableModel(final Map res, final List cols, final boolean returnEmptyStrings) {
		return EntityResultUtils.createTableModel(res, cols, returnEmptyStrings, true);
	}

	public static TableModel createTableModel(final Map res, final List cols, final boolean returnEmptyStrings,
			final boolean convertBB2Im) {
		final EntityResult resN = new EntityResultMapImpl();
		for (int i = 0; i < cols.size(); i++) {
			if (res.containsKey(cols.get(i))) {
				resN.put(cols.get(i), res.get(cols.get(i)));
			}
		}
		resN.setColumnOrder(cols);
		return new EntityResultTableModel(resN, returnEmptyStrings, convertBB2Im, true);
	}


	public static class Order implements Serializable {

		protected String columnName = null;

		protected boolean ascendent = true;

		public Order(final String columnName) {
			this.columnName = columnName;
		}

		public Order(final String columnName, final boolean ascendent) {
			this.columnName = columnName;
			this.ascendent = ascendent;
		}

		public String getColumnName() {
			return this.columnName;
		}

		public boolean isAscendent() {
			return this.ascendent;
		}

		@Override
		public String toString() {
			return this.columnName;
		}

	}

	public static void sort(final EntityResult entityResult, final List<Order> order) {
		final EntityResultSorter sorter = new EntityResultSorter(entityResult, order);
		sorter.sort();
	}

	public static class EntityResultSorter {

		protected List<Order> order;

		protected EntityResult entityResult;

		protected int totalRecord;

		protected int[] indexes;

		protected int compares;

		public EntityResultSorter(final EntityResult entityResult, final List<Order> order) {
			this.entityResult = entityResult;
			this.order = order;
			this.init();
		}

		protected void init() {
			this.totalRecord = this.entityResult.calculateRecordNumber();
			this.indexes = new int[this.totalRecord];
			for (int row = 0; row < this.totalRecord; row++) {
				this.indexes[row] = row;
			}
		}

		public void sort() {
			this.compares = 0;
			final long t = System.currentTimeMillis();
			EntityResultUtils.logger.debug("Sorting of an array of {} records from 0 up to {}", this.indexes.length,
					this.indexes.length - 1);
			this.shuttleSort(this.indexes.clone(), this.indexes, 0, this.totalRecord);
			EntityResultUtils.logger.debug(" Indexes result : {}", this.indexes);
			this.relocatedRecords();
			final long t2 = System.currentTimeMillis();
			EntityResultUtils.logger.trace(" Sorting time ShuttleSort : {} millisecs", t2 - t);

		}

		protected void relocatedRecords() {
			final Object[] records = new Object[this.totalRecord];
			for (int t = this.totalRecord - 1; t >= 0; t--) {
				records[t] = this.entityResult.getRecordValues(t);
				this.entityResult.deleteRecord(t);
			}
			for (int i = 0; i < this.indexes.length; i++) {
				this.entityResult.addRecord((Map) records[this.indexes[i]], i);
			}

		}

		/**
		 * Fast algorithm to sort an array.
		 * @param from the original array
		 * @param to the sorted array
		 * @param low the starting index (typically 0)
		 * @param high the ending index (typically from.length)
		 */
		public void shuttleSort(final int[] from, final int[] to, final int low, final int high) {
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
		 * Compares two rows column by column, following the sorting of the columns.
		 *
		 * @see #compareRowsByColumn(int, int, int)
		 * @param rowIndex1
		 * @param rowIndex2
		 * @return 0 if the rows are equal<br>
		 *         1 if the first row has a null or a bigger value than the second<br>
		 *         -1 if the first row has a null or a lower value than the second<br>
		 */
		protected int compare(final int rowIndex1, final int rowIndex2) {
			this.compares++;
			for (int level = 0; level < this.order.size(); level++) {
				final Order order = this.order.get(level);
				final String columnName = order.getColumnName();
				final boolean ascending = order.isAscendent();
				final int result = this.compareRowsByColumn(rowIndex1, rowIndex2, columnName);
				if (result != 0) {
					return ascending ? result : -result;
				}
			}
			return 0;
		}

		// TODO Revisar si es necesario utilizar para comparar 2 textos ->public
		// static Collator comparator = Collator.getInstance();
		/**
		 * Compares to row values of the same column.
		 * @param rowIndex1
		 * @param rowIndex2
		 * @param columnName
		 * @return 0 if both values are null or equal<br>
		 *         -1 if the first value is null or less than the second<br>
		 *         1 if the second value is null or less than the first<br>
		 */
		public int compareRowsByColumn(final int rowIndex1, final int rowIndex2, final String columnName) {
			if (this.entityResult.containsKey(columnName)) {
				final List columnData = (List) this.entityResult.get(columnName);
				final Object o1 = columnData.get(rowIndex1);
				final Object o2 = columnData.get(rowIndex2);

				// If both values are null, return 0.
				if (((o1 == null) || (o1 instanceof NullValue)) && ((o2 == null) || (o2 instanceof NullValue))) {
					return 0;
				} else if ((o1 == null) || (o1 instanceof NullValue)) { // Define
					// null
					// less
					// than
					// everything.
					return -1;
				} else if ((o2 == null) || (o2 instanceof NullValue)) {
					return 1;
				}

				if ((o1 instanceof String) && (o2 instanceof String)) {
					final int result = TableSorter.comparator.compare(o1, o2);
					if (result < 0) {
						return -1;
					} else if (result > 0) {
						return 1;
					} else {
						return 0;
					}

				} else if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
					if (o1.getClass() == o2.getClass()) {
						final Comparable n1 = (Comparable) o1;
						final Comparable n2 = (Comparable) o2;
						return n1.compareTo(n2);
					} else {
						EntityResultUtils.logger.debug("WARNING: Two comparable, but with different classes: {} and {}",
								o1.getClass(), o2.getClass());
					}
				}

			}
			return 0;
		}

	}

}
