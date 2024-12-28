package com.ontimize.gui.table;

import java.awt.Component;
import java.util.Map;

import javax.swing.JTable;

import com.ontimize.gui.field.TextDataField;

public class StringCellEditor extends CellEditor {

	public StringCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), StringCellEditor.initializeDataField(parameters));
	}

	protected static TextDataField initializeDataField(final Map parameters) {
		final TextDataField tdf = new TextDataField(parameters);
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
