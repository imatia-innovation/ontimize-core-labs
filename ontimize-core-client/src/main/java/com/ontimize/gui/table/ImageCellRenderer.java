package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.ImageDataField;
import com.ontimize.jee.common.db.NullValue;

public class ImageCellRenderer extends ImageDataField implements TableCellRenderer {

	protected boolean lineRemark = true;

	public ImageCellRenderer(final Map parameters) {
		super(parameters);
	}

	public void setLineRemark(final boolean lineRemark) {
		this.lineRemark = lineRemark;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {
		if ((value != null) && (!(value instanceof NullValue))) {
			this.setValue(value);
		} else {
			this.deleteData();
		}
		if (selected) {
			this.dataField.setForeground(CellRenderer.selectedFontColor);
			this.dataField.setBackground(CellRenderer.selectedBackgroundColor);
		} else {
			if ((row % 2) == 0) { // odd row
				if ((table == null) || table.isEnabled()) {
					this.dataField.setBackground(CellRenderer.oddRowBackgroundColor);
				} else {
					this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
				}
			} else {
				if (this.lineRemark) {
					if ((table == null) || table.isEnabled()) {
						this.dataField.setBackground(CellRenderer.evenRowBackgroundColor);
					} else {
						this.dataField.setBackground(CellRenderer.getDarker(CellRenderer.evenRowBackgroundColor));
					}
				} else {
					if ((table == null) || table.isEnabled()) {
						this.dataField.setBackground(CellRenderer.evenRowBackgroundColor);
					} else {
						this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
					}
				}
			}
			this.dataField.setForeground(CellRenderer.fontColor);
		}
		if (hasFocus) {
			this.dataField.setBorder(CellRenderer.focusBorder);
		} else {
			this.dataField.setBorder(CellRenderer.emptyBorder);
		}
		this.dataField.setOpaque(true);
		return this.dataField;
	}

	@Override
	public Dimension getPreferredSize() {
		if (this.dataField != null) {
			final Dimension d = this.dataField.getPreferredSize();
			return d;
		} else {
			return super.getPreferredSize();
		}
	}

}
