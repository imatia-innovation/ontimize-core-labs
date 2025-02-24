package com.ontimize.gui.table;

import java.util.EventObject;
import java.util.Map;

public class InsertTableInsertRowEvent extends EventObject {

	Map rowData = null;

	public InsertTableInsertRowEvent(final InsertTableInsertRowChange source, final Map rowData) {
		super(source);

		this.rowData = rowData;
	}

	public Map getRowData() {
		return this.rowData;
	}

}
