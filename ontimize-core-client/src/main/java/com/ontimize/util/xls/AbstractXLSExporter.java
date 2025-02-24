package com.ontimize.util.xls;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ontimize.gui.table.CurrencyCellRenderer;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.ImageCellRenderer;
import com.ontimize.gui.table.PercentCellRenderer;
import com.ontimize.gui.table.RealCellRenderer;
import com.ontimize.gui.table.Table;

public abstract class AbstractXLSExporter {

	protected static List currencySymbols = new Vector(Arrays.asList(new String[] { "€", "$" }));

	public static String currencyPattern = "##,##0.00";

	public static final int TEXT_CELL = 0;

	public static final int NUMERIC_CELL = 1;

	public static final int DATE_CELL = 2;

	public static final int CURRENCY_CELL = 3;

	public static final int DECIMAL_CELL = 4;

	public static final int DATE_HOUR_CELL = 5;

	public static final int IMAGE_CELL = 6;

	public static final int PERCENT_CELL = 7;

	public static final int REAL_CELL = 8;

	public int getCellType(final String columnName, final Object columnValue, final Map hColumnRenderers, final Map hColumnTypes) {
		if ((columnValue == null) || (columnValue instanceof Table.KeyObject) || (hColumnTypes == null)
				|| (((Integer) hColumnTypes
						.get(columnName) != null)
						&& (Types.VARCHAR == ((Integer) hColumnTypes.get(columnName)).intValue()))) {
			return AbstractXLSExporter.TEXT_CELL;
		}
		final Object cellType = hColumnRenderers.get(columnName);
		if (cellType != null) {

			return getCellTypeFromRenderer(cellType);

		} else {
			if (hColumnTypes.get(columnName) == null) {
				return AbstractXLSExporter.TEXT_CELL;
			}
			return getCellTypeFromColumnType(columnName, hColumnTypes);
		}
	}

	protected boolean isTextCell(final int columnType) {
		switch (columnType) {
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.CLOB:
		case Types.LONGVARBINARY:
		case Types.VARBINARY:
		case Types.ARRAY:
		case Types.BLOB:
		case Types.OTHER:
			return true;
		}
		return false;
	}

	protected boolean isNumericCell(final int columnType) {
		switch (columnType) {
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.BIGINT:
		case Types.BIT:
		case Types.BOOLEAN:
			return true;
		}
		return false;
	}

	protected boolean isDecimalCell(final int columnType) {
		switch (columnType) {
		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.REAL:
		case Types.NUMERIC:
		case Types.FLOAT:
			return true;
		}
		return false;
	}

	protected int getCellTypeFromColumnType(final String columnName, final Map hColumnTypes) {
		final int columnType = ((Integer) hColumnTypes.get(columnName)).intValue();

		if (isTextCell(columnType)) {
			return AbstractXLSExporter.TEXT_CELL;
		}

		if (isNumericCell(columnType)) {
			return AbstractXLSExporter.NUMERIC_CELL;
		}

		if (isDecimalCell(columnType)) {
			return AbstractXLSExporter.DECIMAL_CELL;
		}

		switch (columnType) {
		case Types.DATE:
			return AbstractXLSExporter.DATE_CELL;
		case Types.TIME:
			return AbstractXLSExporter.DATE_HOUR_CELL;
		case Types.TIMESTAMP:
			return AbstractXLSExporter.DATE_HOUR_CELL;

		case Types.BINARY:
			return AbstractXLSExporter.IMAGE_CELL;
		}
		return AbstractXLSExporter.TEXT_CELL;
	}

	protected int getCellTypeFromRenderer(final Object cellType) {
		if (cellType instanceof CurrencyCellRenderer) {
			return AbstractXLSExporter.CURRENCY_CELL;
		}
		if (cellType instanceof DateCellRenderer) {
			return AbstractXLSExporter.DATE_CELL;
		}
		if (cellType instanceof ImageCellRenderer) {
			return AbstractXLSExporter.IMAGE_CELL;
		}
		if (cellType instanceof PercentCellRenderer) {
			return AbstractXLSExporter.PERCENT_CELL;
		}

		if (cellType instanceof RealCellRenderer) {
			return AbstractXLSExporter.REAL_CELL;
		}
		return AbstractXLSExporter.TEXT_CELL;
	}

	public String getCurrencySymbol(final String value) {
		if ((value == null) || (value.length() == 0)) {
			return "";
		}
		return value.substring(value.length() - 1, value.length());
	}

	public String getPercentSymbol(final String value) {
		if ((value == null) || (value.length() == 0)) {
			return "";
		}

		return value.substring(value.length() - 1, value.length());
	}

}
