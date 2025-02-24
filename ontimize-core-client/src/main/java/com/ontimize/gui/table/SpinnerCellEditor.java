package com.ontimize.gui.table;

import java.util.Map;

import com.ontimize.gui.field.SpinnerDataField;

/**
 * Spinner field to be placed in table cell.
 *
 * @since 5.2075EN
 * @author Imatia Innovation
 */
public class SpinnerCellEditor extends CellEditor {

	public SpinnerCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), new SpinnerDataField(parameters));
	}

}
