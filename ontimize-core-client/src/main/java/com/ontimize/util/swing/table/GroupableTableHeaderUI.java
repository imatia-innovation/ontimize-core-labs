package com.ontimize.util.swing.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ontimize.gui.i18n.Internationalization;

public class GroupableTableHeaderUI extends BasicTableHeaderUI implements Internationalization {

	protected List painted = new Vector();

	@Override
	public void paint(final Graphics g, final JComponent c) {
		this.painted.clear();

		final Rectangle clipBounds = g.getClipBounds();
		if (this.header.getColumnModel() == null) {
			return;
		}
		((GroupableTableHeader) this.header).setColumnMargin();
		int column = 0;
		final Dimension size = this.header.getSize();
		final Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
		final Map h = new Hashtable();
		final int columnMargin = 0;
		final Enumeration enumeration = this.header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			cellRect.height = size.height;
			cellRect.y = 0;
			final TableColumn aColumn = (TableColumn) enumeration.nextElement();
			final Enumeration cGroups = ((GroupableTableHeader) this.header).getColumnGroups(aColumn);
			if (cGroups != null) {
				int groupHeight = 0;
				while (cGroups.hasMoreElements()) {
					final GroupableColumnGroup cGroup = (GroupableColumnGroup) cGroups.nextElement();
					Rectangle groupRect = (Rectangle) h.get(cGroup);
					if (groupRect == null) {
						groupRect = new Rectangle(cellRect);
						final Dimension d = cGroup.getSize(this.header.getTable());
						groupRect.width = d.width;
						groupRect.height = d.height;
						h.put(cGroup, groupRect);
					}
					if (!this.isPainted(cGroup)) {
						final Rectangle rBg = new Rectangle(groupRect.x + 1, groupRect.y, groupRect.width, cellRect.height);
						this.paintBackgroundCell(g, rBg, column, this.getRenderer(cGroup, aColumn));
						this.paintGroupCell(g, groupRect, cGroup, this.getRenderer(cGroup, aColumn));
					}
					groupHeight += groupRect.height;
					cellRect.height = size.height - groupHeight;
					cellRect.y = groupHeight;
				}
			} else {
				cellRect.width = aColumn.getWidth() + columnMargin;
				this.paintBackgroundCell(g, cellRect, column, this.getColumnHeaderRenderer(aColumn, this.header));
			}
			cellRect.width = aColumn.getWidth() + columnMargin;
			if (cellRect.intersects(clipBounds)) {
				this.paintCell(g, cellRect, column);
			}
			cellRect.x += cellRect.width;
			column++;
		}
	}

	private TableCellRenderer getColumnHeaderRenderer(final TableColumn column, final JTableHeader header) {
		TableCellRenderer renderer = column.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}
		return renderer;
	}

	private TableCellRenderer getRenderer(final GroupableColumnGroup group, final TableColumn column) {
		TableCellRenderer renderer = group.getHeaderRenderer();
		if (renderer instanceof IGroupableTableHeaderCellRenderer) {
			return renderer;
		} else {
			renderer = column.getHeaderRenderer();
			if (renderer == null) {
				renderer = this.header.getDefaultRenderer();
			}
			if (renderer instanceof IGroupableTableHeaderCellRenderer) {
				return renderer;
			}
			return group.getHeaderRenderer();
		}

	}

	private void paintCell(final Graphics g, final Rectangle cellRect, final int columnIndex) {
		final TableColumn aColumn = this.header.getColumnModel().getColumn(columnIndex);
		final TableCellRenderer renderer = this.getColumnHeaderRenderer(aColumn, this.header);
		final Component component = renderer.getTableCellRendererComponent(this.header.getTable(), aColumn.getHeaderValue(),
				false, false, -1, columnIndex);
		this.rendererPane.paintComponent(g, component, this.header, cellRect.x, cellRect.y, cellRect.width,
				cellRect.height, true);
	}

	private void paintGroupCell(final Graphics g, final Rectangle cellRect, final GroupableColumnGroup cGroup,
			final TableCellRenderer renderer) {
		this.painted.add(cGroup);
		Component c = null;
		if (renderer instanceof IGroupableTableHeaderCellRenderer) {
			c = ((IGroupableTableHeaderCellRenderer) renderer).getTableHeaderCellRendererComponent(
					this.header.getTable(), cGroup.getHeaderValue(), false, false, -1, 0);
		} else {
			c = renderer.getTableCellRendererComponent(this.header.getTable(), cGroup.getHeaderValue(), false, false,
					-1, 0);
		}

		this.rendererPane.paintComponent(g, c, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
				true);
	}

	private void paintBackgroundCell(final Graphics g, final Rectangle cellRect, final int columnIndex, final TableCellRenderer renderer) {
		if (renderer instanceof IGroupableTableHeaderCellRenderer) {
			final Component c = ((IGroupableTableHeaderCellRenderer) renderer).getTableHeaderBackgroundCellRendererComponent(
					this.header.getTable(), null, false, false, -1, columnIndex);
			this.rendererPane.paintComponent(g, c, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
					true);
		}
	}

	private boolean isPainted(final GroupableColumnGroup group) {
		return this.painted.contains(group);
	}

	private int getHeaderHeight() {
		int height = 0;
		final TableColumnModel columnModel = this.header.getColumnModel();
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			final TableColumn aColumn = columnModel.getColumn(column);
			final TableCellRenderer renderer = this.getColumnHeaderRenderer(aColumn, this.header);
			if (renderer == null) {
				return 19;
			}

			final Component comp = renderer.getTableCellRendererComponent(this.header.getTable(), aColumn.getHeaderValue(),
					false, false, -1, column);
			int cHeight = comp.getPreferredSize().height;
			final Enumeration e = ((GroupableTableHeader) this.header).getColumnGroups(aColumn);
			if (e != null) {
				while (e.hasMoreElements()) {
					final GroupableColumnGroup cGroup = (GroupableColumnGroup) e.nextElement();
					cHeight += cGroup.getSize(this.header.getTable()).height;
				}
			}
			height = Math.max(height, cHeight);
		}
		return height;
	}

	private Dimension createHeaderSize(long width) {
		final TableColumnModel columnModel = this.header.getColumnModel();
		width += columnModel.getColumnMargin() * columnModel.getColumnCount();
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, this.getHeaderHeight());
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		long width = 0;
		final Enumeration enumeration = this.header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			final TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return this.createHeaderSize(width);
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale locale) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourcebundle) {
		final Enumeration enumeration = this.header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			final TableColumn aColumn = (TableColumn) enumeration.nextElement();
			final Enumeration cGroups = ((GroupableTableHeader) this.header).getColumnGroups(aColumn);
			if (cGroups != null) {
				while (cGroups.hasMoreElements()) {
					final GroupableColumnGroup cGroup = (GroupableColumnGroup) cGroups.nextElement();
					cGroup.setResourceBundle(resourcebundle);
				}
			}
		}
	}

}
