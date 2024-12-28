package com.ontimize.gui.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.FreeableUtils;
import com.ontimize.jee.common.db.NullValue;

/**
 * Renderer used to show Object data in a table.
 *
 * @version 1.0 01/04/2001
 */
public class ObjectCellRenderer extends CellRenderer implements Freeable {

	protected JTextField textField = new JTextField();

	protected boolean multiLine = true;

	public ObjectCellRenderer() {
		if (ApplicationManager.jvmVersionHigherThan_1_4_0()) {
			this.multiLine = false;
		}
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {

		if ((value != null) && (!(value instanceof NullValue))) {
			final String sText = value.toString();
			int rowsNumber = 1;
			if (table != null) {

				for (int i = 0; i < sText.length(); i++) {
					if (sText.charAt(i) == '\n') {
						rowsNumber++;
					}
				}
			}

			if (!this.multiLine) {
				rowsNumber = 1;
			}
			if (rowsNumber > 1) {
				this.setJComponent(this.textField);
				final Component component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row,
						column);
				int fontHeight = component.getFontMetrics(component.getFont()).getHeight();
				fontHeight = fontHeight * rowsNumber;
				final int currentSize = table.getRowHeight(row);
				if ((currentSize + 2) < fontHeight) {
					table.setRowHeight(row, fontHeight);
				}

				((JTextField) component).setText(sText);
				this.setTipWhenNeeded(table, value, column);
				return component;
			} else {
				this.setJComponent(this);
				final Component component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row,
						column);
				((JLabel) component).setText(sText);
				this.setTipWhenNeeded(table, value, column);
				return component;
			}
		} else {
			this.setJComponent(this);
			final Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
			((JLabel) c).setText("");
			this.setTipWhenNeeded(table, value, column);
			return c;
		}

	}

	@Override
	public void free() {
		this.component = null;
		FreeableUtils.freeComponent(textField);
	}

}
