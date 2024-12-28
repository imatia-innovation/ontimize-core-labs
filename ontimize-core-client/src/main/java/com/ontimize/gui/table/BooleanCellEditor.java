package com.ontimize.gui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import com.ontimize.gui.field.CheckDataField;
import com.ontimize.util.ParseUtils;

public class BooleanCellEditor extends CellEditor {

	public static final String AUTO_STOP_EDITING = "autostopediting";

	public static final String SELECTED_EDITOR = "selectededitor";

	protected boolean autoStopEditing = false;

	protected boolean selectedEditor;

	public static boolean defaultSelectedEditor = false;

	/**
	 * 'autostopediting': Indicates if the edition finishes when the editor value changes. No validation
	 * is needed.
	 *
	 * 'selectededitor': Indicates if the editor has to be selected to start the edition.
	 * @param parameters
	 */
	public BooleanCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), new CheckDataField(parameters));
		((JCheckBox) this.field.getDataField()).setHorizontalAlignment(SwingConstants.CENTER);

		// autostopediting
		final Object autostopediting = parameters.get(BooleanCellEditor.AUTO_STOP_EDITING);
		this.autoStopEditing = ParseUtils.getBoolean((String) autostopediting, true);

		((JCheckBox) this.field.getDataField()).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (BooleanCellEditor.this.autoStopEditing) {
					BooleanCellEditor.this.stopCellEditing();
				}
			}
		});

		this.selectedEditor = BooleanCellEditor.defaultSelectedEditor;

		if (parameters.containsKey(BooleanCellEditor.SELECTED_EDITOR)) {
			this.selectedEditor = ParseUtils.getBoolean((String) parameters.get(BooleanCellEditor.SELECTED_EDITOR),
					BooleanCellEditor.defaultSelectedEditor);
		}

	}

	public void setAutoStopCellEditing(final boolean b) {
		this.autoStopEditing = b;
	}

	@Override
	public boolean isCellEditable(final EventObject anEvent) {
		if (this.selectedEditor) {
			return super.isCellEditable(anEvent);
		}

		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= this.clickNumber;
		}
		return true;
	}

}
