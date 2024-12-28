package com.ontimize.gui.table;

import java.util.Map;
import java.util.Vector;

import com.ontimize.gui.field.TextComboDataField;

public class ComboCellEditor extends CellEditor {

	public ComboCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), new TextComboDataField(parameters));
	}

	public void setValues(final Vector values) {
		((TextComboDataField) this.field).setValues(values);
	}

}
