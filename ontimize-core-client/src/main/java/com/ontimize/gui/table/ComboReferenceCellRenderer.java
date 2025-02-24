package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.ReferenceComboDataField;
import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;
import com.ontimize.gui.table.CellRenderer.CellRendererFontManager;
import com.ontimize.util.ObjectTools;

/**
 * Renderer used to show information of other entities in tables.
 *
 * @see com.ontimize.gui.table.Table#setRendererForColumn
 * @version 1.0 15/05/2002
 */

public class ComboReferenceCellRenderer extends ReferenceComboDataField implements TableCellRenderer, Cloneable {

	private static final Logger logger = LoggerFactory.getLogger(ComboReferenceCellRenderer.class);

	protected boolean remarkLines = true;

	protected boolean remarkEditable = true;

	public static Border focusBorder;

	protected Map parameters = new Hashtable();

	protected CellRendererColorManager cellRendererColorManager;

	protected CellRendererFontManager cellRendererFontManager;

	public ComboReferenceCellRenderer(final Map parameters) {
		super(parameters);
		this.useCacheManager = true;
		this.initCacheOnSetValue = true;
		this.parameters = ObjectTools.clone(parameters);
		this.showErrorMessages = false;
	}

	public void setLineRemark(final boolean lineRemark) {
		this.remarkLines = lineRemark;
	}

	public void setEditableRemark(final boolean editableRemark) {
		this.remarkEditable = editableRemark;
	}

	protected void insertingRowRenderer(final JTable table, final Component c, final int row, final int column) {
		if (CellRenderer.isRequiredInsertingRow(row, column, table)) {
			c.setBackground(CellRenderer.requiredInsertColumns);
		} else {
			c.setBackground(CellRenderer.noRequiredInsertColumns);
		}
	}

	protected void selectedRenderer(final JTable table, final Component c, final int row, final int column) {
		c.setForeground(CellRenderer.selectedFontColor);
		if (this.remarkEditable && table.isCellEditable(row, column)) {
			c.setBackground(CellRenderer.selectedEditableBackgroundColor);
		} else {
			c.setBackground(CellRenderer.selectedBackgroundColor);
		}
	}

	protected void configureToolTip(final JTable table, final Component c, final int column) {
		// TIP
		try {
			if (c instanceof JLabel) {
				((JComponent) c).setToolTipText(null);
				if (table != null) {
					final TableColumn tc = table.getColumn(table.getColumnName(column));
					if (tc.getWidth() < CellRenderer.calculatePreferredTextWidth((JLabel) c)) {
						((JComponent) c).setToolTipText(((JLabel) c).getText());
					}
				}
			}
		} catch (final Exception e) {
			ComboReferenceCellRenderer.logger.error(null, e);
		}
	}

	protected void configureFontManager(final JTable table, final Component c, final int row, final int column, final boolean selected) {
		if (this.cellRendererFontManager != null) {
			final Font f = this.cellRendererFontManager.getFont(table, row, column, selected);
			if (f != null) {
				c.setFont(f);
			}
		}
	}

	protected void configureColorManager(final JTable table, final Component c, final int row, final int column, final boolean selected) {
		if (this.cellRendererColorManager != null) {
			final Color bg = this.cellRendererColorManager.getBackground(table, row, column, selected);
			if (bg != null) {
				c.setBackground(bg);
			}
			final Color fg = this.cellRendererColorManager.getForeground(table, row, column, selected);
			if (fg != null) {
				c.setForeground(fg);
			}
		}
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {

		// Uses a label to improve the performance
		final Component c = super.renderer.getRendererComponentForTable(value, selected, hasFocus);

		if (table != null) {
			c.setFont(CellRenderer.font == null ? table.getFont() : CellRenderer.font);
		}

		if (CellRenderer.isInsertingRow(row, table)) {
			this.insertingRowRenderer(table, c, row, column);
		} else if (selected) {
			this.selectedRenderer(table, c, row, column);
		} else {
			if ((table != null) && this.remarkEditable && table.isCellEditable(row, column)) {
				if (table.isEnabled()) {
					if ((row % 2) == 0) { // odd row
						c.setBackground(CellRenderer.oddEditableBackgroundColor);
					} else {
						c.setBackground(CellRenderer.evenEditableBackgroundColor);
					}
				} else {
					if ((row % 2) == 0) { // odd row
						c.setBackground(CellRenderer.getSoftDarker(CellRenderer.oddEditableBackgroundColor));
					} else {
						c.setBackground(CellRenderer.getSoftDarker(CellRenderer.evenEditableBackgroundColor));
					}
				}
				c.setForeground(CellRenderer.editableFontColor);
			} else {

				if ((row % 2) == 0) { // odd row
					if ((table == null) || table.isEnabled()) {
						c.setBackground(CellRenderer.oddRowBackgroundColor);
					} else {
						c.setBackground(DataComponent.VERY_LIGHT_GRAY);
					}
				} else {
					if (this.remarkLines) {
						if ((table == null) || table.isEnabled()) {
							c.setBackground(CellRenderer.evenRowBackgroundColor);
						} else {
							c.setBackground(CellRenderer.getDarker(CellRenderer.evenRowBackgroundColor));
						}
					} else {
						if ((table == null) || table.isEnabled()) {
							c.setBackground(CellRenderer.oddRowBackgroundColor);
						} else {
							c.setBackground(DataComponent.VERY_LIGHT_GRAY);
						}
					}
				}
				c.setForeground(CellRenderer.fontColor);

			}
		}

		if (hasFocus) {
			((JComponent) c).setBorder(this.getDefaultFocusBorder());
		} else {
			((JComponent) c).setBorder(CellRenderer.emptyBorder);
		}

		this.configureToolTip(table, c, column);

		this.configureColorManager(table, c, row, column, selected);

		this.configureFontManager(table, c, row, column, selected);

		return c;
	}

	public Border getDefaultFocusBorder() {
		if (ComboReferenceCellRenderer.focusBorder == null) {
			ComboReferenceCellRenderer.focusBorder = CellRenderer.focusBorder;
			if (ComboReferenceCellRenderer.focusBorder == null) {
				ComboReferenceCellRenderer.focusBorder = BorderFactory.createLineBorder(CellRenderer.focusBorderColor,
						2);
			}
		}
		return ComboReferenceCellRenderer.focusBorder;
	}

	@Override
	public Dimension getPreferredSize() {
		if (this.renderer != null) {
			final Dimension d = this.renderer.getPreferredSize();
			final Insets i = this.renderer.getInsets();
			final FontMetrics fm = this.renderer.getFontMetrics(this.renderer.getFont());
			final int rowHeight = fm.getHeight();
			return new Dimension(d.width - i.left - i.right, rowHeight);
		} else {
			return super.getPreferredSize();
		}
	}

	@Override
	public void setFont(final Font f) {
		super.setFont(f);
		if (this.renderer != null) {
			this.renderer.setFont(f);
		}
		if (this.dataField != null) {
			this.dataField.setFont(f);

		}
	}

	@Override
	public Font getFont() {
		if (this.renderer != null) {
			return this.renderer.getFont();
		} else {
			return super.getFont();
		}
	}

	@Override
	public FontMetrics getFontMetrics(final Font f) {
		if (this.renderer != null) {
			return this.renderer.getFontMetrics(f);
		} else {
			return super.getFontMetrics(f);
		}
	}

	@Override
	public Object clone() {
		final ComboReferenceCellRenderer c = new ComboReferenceCellRenderer(this.parameters);
		c.setParentForm(this.parentForm);
		c.setReferenceLocator(this.locator);
		c.setCacheManager(this.cacheManager);
		if (this.dataCache != null) {
			c.dataCacheInitialized = this.dataCacheInitialized;
			c.dataCache = ObjectTools.clone(this.dataCache);
		}
		return c;
	}

	public static ComboReferenceCellRenderer newInstance(final String entity, final String attr, final String cod, final String cols) {
		return ComboReferenceCellRenderer.newInstance(entity, attr, cod, cols, null, null, null);
	}

	public static ComboReferenceCellRenderer newInstance(final String entity, final String attr, final String cod, final String cols,
			final String visibleCols) {
		return ComboReferenceCellRenderer.newInstance(entity, attr, cod, cols, visibleCols, null, null);
	}

	public static ComboReferenceCellRenderer newInstance(final String entity, final String attr, final String cod, final String cols,
			final String visibleCols, final String parentkeys) {
		return ComboReferenceCellRenderer.newInstance(entity, attr, cod, cols, visibleCols, parentkeys, null);
	}

	public static ComboReferenceCellRenderer newInstance(final String entity, final String attr, final String cod, final String cols,
			final String visibleCols, final String parentkeys, final String separator) {
		final Map param = new Hashtable();
		param.put("entity", entity);
		if (attr != null) {
			param.put("attr", attr);
		}
		param.put("cod", cod);
		param.put("cols", cols);
		if (parentkeys != null) {
			param.put("parentkeys", parentkeys);
		}
		if (visibleCols != null) {
			param.put("visiblecols", visibleCols);
		}
		if (separator != null) {
			param.put("separator", separator);
		}
		return new ComboReferenceCellRenderer(param);
	}

	public void setCellRendererColorManager(final CellRendererColorManager rend) {
		this.cellRendererColorManager = rend;
	}

	public void setCellRendererFontManager(final CellRendererFontManager rend) {
		this.cellRendererFontManager = rend;
	}

}
