package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

public class ButtonCellEditor extends JButton implements TableCellEditor {

	protected List listeners = new Vector();

	protected Object lastValue = null;

	public ButtonCellEditor(final Icon icon) {
		super(icon);
		this.setFocusPainted(false);
		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ButtonCellEditor.this.stopCellEditing();
			}
		});
	}

	@Override
	public Object getCellEditorValue() {
		return this.lastValue;
	}

	@Override
	public boolean isCellEditable(final EventObject anEvent) {
		if (anEvent == null) {
			return true;
		} else if (anEvent instanceof MouseEvent) {
			return true;
		} else {
			return true;
		}
	}

	@Override
	public boolean shouldSelectCell(final EventObject e) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		this.fireEditingStopped();
		return true;
	}

	@Override
	public void cancelCellEditing() {
		this.fireEditingCanceled();
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		this.lastValue = value;
		return this;
	}

	@Override
	public void addCellEditorListener(final CellEditorListener l) {
		this.listeners.add(l);
	}

	@Override
	public void removeCellEditorListener(final CellEditorListener l) {
		this.listeners.remove(l);
	}

	protected void fireEditingStopped() {
		for (int i = 0; i < this.listeners.size(); i++) {
			((CellEditorListener) this.listeners.get(i)).editingStopped(new ChangeEvent(this));
		}
	}

	protected void fireEditingCanceled() {
		for (int i = 0; i < this.listeners.size(); i++) {
			((CellEditorListener) this.listeners.get(i)).editingCanceled(new ChangeEvent(this));
		}
	}

}
