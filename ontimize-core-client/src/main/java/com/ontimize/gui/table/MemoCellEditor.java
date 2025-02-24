package com.ontimize.gui.table;

import java.util.Map;

import com.ontimize.gui.field.MemoDataField;

public class MemoCellEditor extends CellEditor {

	public MemoCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), new MemoDataField(parameters));
	}

}
