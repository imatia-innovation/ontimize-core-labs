package com.ontimize.gui.table;

import java.util.List;
import java.util.Map;

import com.ontimize.gui.field.TextComboDataField;

public class ComboCellEditor extends CellEditor {

	public ComboCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), new TextComboDataField(parameters));
	}

	public void setValues(final List values) {
		((TextComboDataField) this.field).setValues(values);
	}

}
