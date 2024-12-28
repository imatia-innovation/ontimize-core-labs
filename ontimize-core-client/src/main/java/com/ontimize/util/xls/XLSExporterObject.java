package com.ontimize.util.xls;

import java.util.Map;

import javax.swing.table.TableCellRenderer;

public class XLSExporterObject {

	public XLSExporterObject(final String columnName, final TableCellRenderer rendererColumn, final Map properties) {
		this.columnName = columnName;
		this.rendererColumn = rendererColumn;
		this.properties = properties;
	}

	public XLSExporterObject(final String columnName, final TableCellRenderer rendererColumn) {
		this(columnName, rendererColumn, null);
	}

	protected String columnName;

	protected TableCellRenderer rendererColumn;

	protected Map properties;

	public String getColumnName() {
		return this.columnName;
	}

	public void setColumnName(final String columnName) {
		this.columnName = columnName;
	}

	public TableCellRenderer getRendererColumn() {
		return this.rendererColumn;
	}

	public void setRendererColumn(final TableCellRenderer rendererColumn) {
		this.rendererColumn = rendererColumn;
	}

	public Map getProperties() {
		return this.properties;
	}

	public void setProperties(final Map properties) {
		this.properties = properties;
	}

}
