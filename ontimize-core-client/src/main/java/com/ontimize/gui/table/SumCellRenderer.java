/**
 *
 */
package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.db.NullValue;

public class SumCellRenderer extends com.ontimize.gui.table.RealCellRenderer {

	public static Color defaultForegroundColor = Color.red;

	public static Color backgroundColor;

	public static Color disabledBackgroundColor;

	public static Color foregroundColor;

	public SumCellRenderer() {
		super();
	}

	@Override
	public void setTipWhenNeeded(final JTable jTable, final Object value, final int column) {
		if ((value == null) || (value instanceof NullValue)) {
			return;
		}
		if (value instanceof Number) {
			final TableModel model = jTable.getModel();
			if ((model != null) && (model instanceof TableSorter)) {
				final TableSorter sorter = (TableSorter) model;
				final int cIndex = jTable.convertColumnIndexToModel(column);
				final Object oColumnName = sorter.getColumnIdentifier(cIndex);
				Object operation = sorter.getColumnType(oColumnName);
				final Object[] v = new Object[4];
				v[0] = ApplicationManager.getTranslation((String) oColumnName, this.bundle);
				if (operation == null) {
					operation = "";
				}
				final String sTranslatedOperation = ApplicationManager.getTranslation((String) operation, this.bundle);
				v[1] = sTranslatedOperation;
				v[2] = this.format.format(value);

				final int[] index = jTable.getSelectedRows();
				Object oSelValue = null;
				if (index.length > 0) {
					oSelValue = sorter.getSelectedColumnOperation(oColumnName, jTable.getSelectedRows());
				}
				if ((oSelValue == null) || (oSelValue instanceof NullValue) || !(oSelValue instanceof Number)) {
					v[3] = "";
				} else {
					final Object[] vS = new Object[2];
					vS[0] = sTranslatedOperation;
					vS[1] = this.format.format(oSelValue);
					v[3] = ApplicationManager.getTranslation("ToolTipSumSelectedRow", this.bundle, vS);
				}
				this.setToolTipText(ApplicationManager.getTranslation("ToolTipSumRow", this.bundle, v));
			}
		} else {
			this.setToolTipText("");
		}
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {
		final Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
		final Object oHeaderText = table.getColumnModel().getColumn(column).getHeaderValue();
		if ((value == null) && (c instanceof JComponent)) {
			((JComponent) c).setBackground(SumCellRenderer.disabledBackgroundColor != null
					? SumCellRenderer.disabledBackgroundColor : DataComponent.VERY_LIGHT_GRAY);
			((JComponent) c).setToolTipText(null);
		} else {
			c.setForeground(SumCellRenderer.foregroundColor != null ? SumCellRenderer.foregroundColor
					: SumCellRenderer.defaultForegroundColor);
			if (SumCellRenderer.backgroundColor != null) {
				c.setBackground(SumCellRenderer.backgroundColor);
			}
			if (c instanceof JComponent) {
				if (oHeaderText == null) {
					((JComponent) c).setToolTipText("Total ");
				} else {
					final StringBuilder tip = new StringBuilder("Total ");
					tip.append(oHeaderText.toString());
					((JComponent) c).setToolTipText(tip.toString());
				}
			}
		}
		return c;
	}

}
