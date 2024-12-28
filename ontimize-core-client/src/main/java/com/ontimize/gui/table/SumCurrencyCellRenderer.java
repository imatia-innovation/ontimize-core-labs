/**
 *
 */
package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.text.Format;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.db.NullValue;

public class SumCurrencyCellRenderer extends com.ontimize.gui.table.CurrencyCellRenderer {

	public SumCurrencyCellRenderer() {
		super();
	}

	@Override
	public void setTipWhenNeeded(final JTable table, final Object value, final int column) {
		if ((value == null) || (value instanceof NullValue)) {
			return;
		}
		if (value instanceof Number) {
			final TableModel model = table.getModel();
			if ((model != null) && (model instanceof TableSorter)) {
				final TableSorter sorter = (TableSorter) model;
				final int cIndex = table.convertColumnIndexToModel(column);
				final Object columnName = sorter.getColumnIdentifier(cIndex);
				final Object operation = sorter.getColumnType(columnName);
				final Object[] v = new Object[5];
				v[0] = ApplicationManager.getTranslation((String) columnName, this.bundle);
				final String transOp = ApplicationManager.getTranslation((String) operation, this.bundle);
				v[1] = transOp;
				v[2] = this.format.format(value);
				final double ptas = ((Number) value).doubleValue() * CurrencyCellRenderer.EURO;
				v[3] = this.pstFormatter.format(new Double(ptas));
				final int[] index = table.getSelectedRows();
				Object oSelValue = null;
				if (index.length > 0) {
					oSelValue = sorter.getSelectedColumnOperation(columnName, table.getSelectedRows());
				}
				if ((oSelValue == null) || (oSelValue instanceof NullValue) || !(oSelValue instanceof Number)) {
					v[4] = "";
				} else {
					final Object[] vS = new Object[3];
					vS[0] = transOp;
					vS[1] = this.format.format(oSelValue);
					vS[2] = this.pstFormatter
							.format(new Double(((Number) value).doubleValue() * CurrencyCellRenderer.EURO));
					v[4] = ApplicationManager.getTranslation("ToolTipSumRowSelectedCurrency", this.bundle, vS);
				}
				this.setToolTipText(ApplicationManager.getTranslation("ToolTipSumRowCurrency", this.bundle, v));
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
		String tipAnt = null;
		if ((value == null) && (c instanceof JComponent)) {
			((JComponent) c).setBackground(DataComponent.VERY_LIGHT_GRAY);
			((JComponent) c).setToolTipText(null);
		} else {
			tipAnt = ((JComponent) c).getToolTipText();
			c.setForeground(Color.red);
			if (c instanceof JComponent) {
				this.setTipWhenNeeded(table, value, column);
			}
		}
		return c;
	}

	@Override
	public void setComponentLocale(final Locale loc) {
		super.setComponentLocale(loc);
	}

	@Override
	public void setFormater(final Format f) {
		// TODO Auto-generated method stub
		super.setFormater(f);
	}

}
