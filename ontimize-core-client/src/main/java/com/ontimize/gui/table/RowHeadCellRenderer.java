package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.ontimize.util.ParseUtils;

public class RowHeadCellRenderer extends JLabel implements TableCellRenderer {

	public static final String ROWNUMBERFONT = "rownumberfont";

	public static final String ROWNUMBERFG = "rownumberfg";

	public static final String ROWNUMBERBG = "rownumberbg";

	public static final String ROWNUMBERBGIMAGE = "rownumberbgimage";

	public static final String ROWNUMBERBORDER = "rownumberborder";

	public static Color rowNumberForeground;

	public static Font rowNumberFont;

	public static Color rowNumberBackground;

	public static Border rowNumberBorder;

	protected ImageIcon bgImage;

	public RowHeadCellRenderer(final JTable table) {
		this(table, new Hashtable());
	}

	@Override
	public void setOpaque(final boolean isOpaque) {
		super.setOpaque(isOpaque);
	}

	public RowHeadCellRenderer(final JTable table, final Map parameters) {
		final JTableHeader header = table.getTableHeader();

		this.setOpaque(true);
		this.setHorizontalAlignment(SwingConstants.CENTER);

		Color foreground = ParseUtils.getColor((String) parameters.get(RowHeadCellRenderer.ROWNUMBERFG),
				RowHeadCellRenderer.rowNumberForeground);
		if (foreground == null) {
			foreground = header.getForeground();
		}
		this.setForeground(foreground);

		Color background = ParseUtils.getColor((String) parameters.get(RowHeadCellRenderer.ROWNUMBERBG),
				RowHeadCellRenderer.rowNumberBackground);
		if (background == null) {
			background = header.getBackground();
		}
		this.setBackground(background);

		Font font = ParseUtils.getFont((String) parameters.get(RowHeadCellRenderer.ROWNUMBERFONT),
				RowHeadCellRenderer.rowNumberFont);
		if (font == null) {
			font = header.getFont();
		}
		this.setFont(font);

		Border border = ParseUtils.getBorder((String) parameters.get(RowHeadCellRenderer.ROWNUMBERBORDER),
				RowHeadCellRenderer.rowNumberBorder);
		if (border == null) {
			border = UIManager.getBorder("TableHeader.cellBorder");
		}
		this.setBorder(border);

		this.bgImage = ParseUtils.getImageIcon((String) parameters.get(RowHeadCellRenderer.ROWNUMBERBGIMAGE), null);
		if (this.bgImage != null) {
			this.setOpaque(false);
		}
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {
		this.setText(value == null ? "" : value.toString());
		return this;
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (this.bgImage != null) {
			g.drawImage(this.bgImage.getImage(), 0, 0, this.getSize().width, this.getSize().height, this);
		}
		super.paintComponent(g);
	}

}
