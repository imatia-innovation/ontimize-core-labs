package com.ontimize.util.swing.table;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.util.ObjectTools;

public class GroupableColumnGroup implements Internationalization {

	protected TableCellRenderer renderer;

	protected List list;

	protected String text;

	protected int margin = 0;

	protected ResourceBundle resourcebundle;

	public GroupableColumnGroup(final String text) {
		this(null, text);
	}

	public GroupableColumnGroup(final TableCellRenderer renderer, final String text) {
		if (renderer == null) {
			this.renderer = new DefaultTableCellRenderer() {

				@Override
				public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
						final boolean hasFocus, final int row, final int column) {
					final JTableHeader header = table.getTableHeader();
					if (header != null) {
						this.setForeground(header.getForeground());
						this.setBackground(header.getBackground());
						this.setFont(header.getFont());
					}
					this.setHorizontalAlignment(SwingConstants.CENTER);
					this.setText(value == null ? "" : ApplicationManager.getTranslation(value.toString(),
							GroupableColumnGroup.this.resourcebundle));
					this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
					return this;
				}
			};
		} else {
			this.renderer = renderer;
		}
		this.text = text;
		this.list = new Vector();
	}

	/**
	 * @param obj TableColumn or ColumnGroup
	 */
	public void add(final Object obj) {
		if (obj == null) {
			return;
		}
		this.list.add(obj);
	}

	/**
	 * @param c TableColumn
	 * @param v ColumnGroups
	 */
	public List getColumnGroups(final TableColumn c, final List g) {
		g.add(this);
		if (this.list.contains(c)) {
			return g;
		}
		final Enumeration e = Collections.enumeration(this.list);
		while (e.hasMoreElements()) {
			final Object obj = e.nextElement();
			if (obj instanceof GroupableColumnGroup) {
				final List groups = ((GroupableColumnGroup) obj).getColumnGroups(c, ObjectTools.clone(g));
				if (groups != null) {
					return groups;
				}
			}
		}
		return null;
	}

	public TableCellRenderer getHeaderRenderer() {
		return this.renderer;
	}

	public void setHeaderRenderer(final TableCellRenderer renderer) {
		if (renderer != null) {
			this.renderer = renderer;
		}
	}

	public Object getHeaderValue() {
		return this.text;
	}

	public boolean hasTableColumn(final TableColumn tc) {
		final TableColumn root = (TableColumn) this.list.get(0);
		if (root.getHeaderValue().equals(tc.getHeaderValue())) {
			return true;
		}
		return false;
	}

	public Dimension getSize(final JTable table) {
		final Component comp = this.renderer.getTableCellRendererComponent(table, this.getHeaderValue(), false, false, -1, 0);
		final int hMin = comp.getFontMetrics(comp.getFont()).getHeight();
		int height = comp.getPreferredSize().height;

		if (hMin > height) {
			height = hMin;
		}

		int width = 0;
		final Enumeration e = Collections.enumeration(this.list);
		while (e.hasMoreElements()) {
			final Object obj = e.nextElement();
			if (obj instanceof TableColumn) {
				final TableColumn aColumn = (TableColumn) obj;
				width += aColumn.getWidth();
			} else {
				width += ((GroupableColumnGroup) obj).getSize(table).width;
			}
		}
		return new Dimension(width, height);
	}

	public void setColumnMargin(final int margin) {
		this.margin = margin;
		final Enumeration e = Collections.enumeration(this.list);
		while (e.hasMoreElements()) {
			final Object obj = e.nextElement();
			if (obj instanceof GroupableColumnGroup) {
				((GroupableColumnGroup) obj).setColumnMargin(margin);
			}
		}
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
		this.resourcebundle = resourcebundle;
	}

}
