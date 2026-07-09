package com.ontimize.gui.table;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tools to reuse in CellRendererColorManager implementations, to rescue info about columns and/or cells.
 */
public class CellRendererColorManagerTools {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(CellRendererColorManagerTools.class);

	/**
	 * Returns the index of the column with the specified name "IN VIEW" (visible columns), or -1 if the column does not exist, based on column name.
	 * 
	 * @param columnName
	 * @return
	 */
	public static int getColumnIndex(final JTable table, final String columnName) {
		return CellRendererColorManagerTools.getColumnIndex(table, columnName, false);
	}

	public static int getColumnIndex(final JTable table, final String columnName, final boolean silent) {
		try {
			final TableColumn column = table.getColumn(columnName);
			if ((column == null) && !silent) {
				CellRendererColorManagerTools.logger.warn("W_COLUMN_NOT_FOUND__{}", columnName);
				return -1;
			}
			return table.convertColumnIndexToView(column.getModelIndex());
		} catch (final Exception ex) {
			if (!silent) {
				CellRendererColorManagerTools.logger.warn("W_COLUMN_NOT_FOUND__{}", columnName, ex);
			}
			return -1;
		}
	}

	/**
	 * Returns the value of cell in the table at the specified row and columnName. If the columnName does not exist, it returns null (and log
	 * warning).
	 * 
	 * @param table
	 * @param row
	 * @param columnName
	 * @return
	 */
	public static Object getValueAt(final JTable table, final int row, final String columnName) {
		return CellRendererColorManagerTools.getValueAt(table, row, columnName, false);
	}

	public static Object getValueAt(final JTable table, final int row, final String columnName, final boolean silent) {
		final int columnIndex = CellRendererColorManagerTools.getColumnIndex(table, columnName, silent);
		if (columnIndex < 0) {
			if (!silent) {
				CellRendererColorManagerTools.logger.warn("W_COLUMN_NOT_FOUND_TO_RESCUE_VALUE", columnName);
			}
			return null;
		}
		return table.getValueAt(row, columnIndex);
	}

	/**
	 * Returns true if the column (index in view) in the table is the same as the columnName, false otherwise.
	 * 
	 * @param table
	 * @param col
	 * @param columnName
	 * @return
	 */
	public static boolean isThisColumn(final JTable table, final int col, final String columnName) {
		return columnName.equals(table.getColumnName(col));
	}

}
