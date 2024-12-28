package com.ontimize.gui.table;

import java.awt.Component;
import java.util.Map;

import javax.swing.JTable;

import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.TextDataField;

public class IntegerCellEditor extends CellEditor {

	public IntegerCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), IntegerCellEditor.initializeDataField(parameters));
	}

	protected static IntegerDataField initializeDataField(final Map parameters) {
		final IntegerDataField tdf = new IntegerDataField(parameters);
		if (tdf.getDataField() instanceof TextDataField.EJTextField) {
			((TextDataField.EJTextField) tdf.getDataField()).setCaretPositionOnFocusLost(false);
		}
		return tdf;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

}
