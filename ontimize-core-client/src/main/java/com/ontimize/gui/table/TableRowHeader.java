package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import com.ontimize.util.swing.border.SoftButtonBorder;

public class TableRowHeader extends JList {

	protected class HeaderListener extends MouseAdapter {

		TableRowHeader header;

		ButtonRenderer renderer;

		public HeaderListener(final TableRowHeader header, final ButtonRenderer renderer) {
			this.header = header;
			this.renderer = renderer;
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			final int celda = this.header.locationToIndex(e.getPoint());
			this.renderer.setPressedCell(celda);
			this.header.repaint();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			final int pressedCell = this.renderer.pressedCell;

			this.renderer.setPressedCell(-1);
			this.header.repaint();
			if (pressedCell >= 0) {
				if (!this.renderer.enabledValue.equals(this.header.getModel().getElementAt(pressedCell))) {
					return;
				}
				this.header.rowHeaderClicked(pressedCell);
			}
		}

	}

	protected class ButtonRenderer extends JButton implements ListCellRenderer {

		protected ImageIcon icon = null;

		protected Object enabledValue = null;

		protected int pressedCell = -1;

		public ButtonRenderer(final ImageIcon icon, final Object enabledValue) {
			this.icon = icon;
			this.enabledValue = enabledValue;
			this.setIcon(icon);
			this.setBorder(new SoftButtonBorder());
		}

		@Override
		public Component getListCellRendererComponent(final JList table, final Object value, final int index, final boolean isSelected,
				final boolean hasFocus) {
			if ((value == null) && (this.enabledValue == null)) {
				this.getModel().setPressed(false);
				this.getModel().setArmed(false);
				this.setEnabled(false);
				return this;
			}
			if ((this.enabledValue == null) || (value == null)) {
				this.getModel().setPressed(false);
				this.getModel().setArmed(false);
				this.setEnabled(false);
				return this;
			}
			if (!this.enabledValue.equals(value)) {
				this.getModel().setPressed(false);
				this.getModel().setArmed(false);
				this.setEnabled(false);
			} else {
				this.setEnabled(true);
				if (index == this.pressedCell) {
					this.getModel().setPressed(true);
					this.getModel().setArmed(true);

				} else {
					this.setBorderPainted(true);
					this.getModel().setPressed(false);

				}
			}
			return this;
		}

		public void setPressedCell(final int cell) {
			this.pressedCell = cell;
		}

	}

	protected JTable table = null;

	protected Object columnId = null;

	public TableRowHeader(final JTable table, final Object columnId, final ImageIcon icon, final Object enabledValue) {
		super();
		this.table = table;
		this.columnId = columnId;
		table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(final TableModelEvent e) {
				TableRowHeader.this.updateModel();
			}
		});
		final ButtonRenderer renderer = new ButtonRenderer(icon, enabledValue);
		this.setCellRenderer(renderer);
		this.addMouseListener(new HeaderListener(this, renderer));
		this.setOpaque(false);
		this.setFixedCellHeight(this.table.getRowHeight());
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		d.width = 20;
		return d;
	}

	protected void updateModel() {
		final Vector vData = new Vector();
		final TableColumn tableColumn = this.table.getColumn(this.columnId);
		final int columnIndex = this.table.convertColumnIndexToView(tableColumn.getModelIndex());
		for (int i = 0; i < this.table.getRowCount(); i++) {
			vData.add(this.table.getValueAt(i, columnIndex));
		}
		super.setListData(vData);
		this.setFixedCellHeight(this.table.getRowHeight());
	}

	public void rowHeaderClicked(final int row) {

	}

}
