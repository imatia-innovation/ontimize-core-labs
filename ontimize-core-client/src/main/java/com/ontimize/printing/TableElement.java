package com.ontimize.printing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.table.ObjectCellRenderer;
import com.ontimize.util.ObjectTools;

public class TableElement extends RectangleElement {

	private static final Logger logger = LoggerFactory.getLogger(TableElement.class);

	private com.ontimize.gui.table.Table table = null;

	private BufferedImage bi = null;

	private Color headerBackgroundColor = new Color(230, 230, 250);

	public TableElement(final Map parameters) {
		super(parameters);
		// Create table:
		final Object cols = parameters.get("cols");
		if (cols == null) {
			TableElement.logger.debug(this.getClass().toString() + ": Parameter 'cols' required");
		} else {
			final StringTokenizer st = new StringTokenizer(cols.toString(), ";");
			final String sKey = st.nextToken();
			final Map hTable = ObjectTools.clone(parameters);
			hTable.put("entity", "not_used");
			hTable.put("cols", cols);
			hTable.put("key", sKey);

			try {
				this.table = new com.ontimize.gui.table.Table(hTable);
				if (AbstractPrintingElement.defaultFont != null) {
					this.table.setFont(AbstractPrintingElement.defaultFont);
				}
				this.table.setFont(this.table.getFont().deriveFont((float) this.fontSize));

				final Object headerbg = parameters.get("headerbg");
				if (headerbg != null) {
					this.headerBackgroundColor = ColorConstants.parseColor(headerbg.toString());
				}
				// Bundle.
				final Object bundle = parameters.get("bundle");
				if (bundle != null) {
					final ResourceBundle res = ResourceBundle.getBundle(bundle.toString());
					this.table.setResourceBundle(res);
				}

				final Object painthlines = parameters.get("painthlines");
				if (painthlines != null) {
					if (painthlines.equals("no")) {
						this.table.getJTable().setShowHorizontalLines(false);
						this.table.getJTable()
						.setIntercellSpacing(new Dimension(this.table.getJTable().getIntercellSpacing().width, 0));
					}
				}
				final Object paintvlines = parameters.get("paintvlines");
				if (paintvlines != null) {
					if (paintvlines.equals("no")) {
						this.table.getJTable().setShowVerticalLines(false);
						this.table.getJTable()
						.setIntercellSpacing(new Dimension(0, this.table.getJTable().getIntercellSpacing().height));
					}
				}
			} catch (final Exception e) {
				TableElement.logger.error(e.getMessage(), e);
			}
			try {
				if (this.italics && this.bold) {
					this.table.setFont(this.table.getFont().deriveFont(Font.ITALIC | Font.BOLD));
				} else if (this.italics) {
					this.table.setFont(this.table.getFont().deriveFont(Font.ITALIC));
				} else if (this.bold) {
					this.table.setFont(this.table.getFont().deriveFont(Font.BOLD));
				}
			} catch (final Exception e) {
				TableElement.logger.error(e.getMessage(), e);
			}
			this.table.setLineRemark(false);
			final ObjectCellRenderer rend = new ObjectCellRenderer() {

				Border borde = new LineBorder(Color.darkGray);

				@Override
				public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected,
						final boolean hasFocus, final int row, final int column) {
					final Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
					((JComponent) c).setBorder(this.borde);
					((JComponent) c).setBackground(TableElement.this.headerBackgroundColor);
					return c;
				}
			};
			rend.setLineRemark(false);
			this.table.getJTable().getTableHeader().setDefaultRenderer(rend);
			final TableColumnModel tcm = this.table.getJTable().getColumnModel();
			for (int i = 0; i < tcm.getColumnCount(); i++) {
				tcm.getColumn(i).setHeaderRenderer(rend);
			}
			this.table.getJTable().setGridColor(Color.darkGray);
			if ((this.width != -1) && (this.high != -1)) {
				this.table.getJTable()
				.getTableHeader()
				.setSize(AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						this.table.getJTable().getTableHeader().getPreferredSize().height);
				this.table.getJTable()
				.setSize(AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						AbstractPrintingElement.millimeterToPagePixels(this.high) * 1);
				this.table.doLayout();
			} else {
				final int iTableWidth = Math.max(this.table.getJTable().getPreferredSize().width,
						this.table.getJTable().getTableHeader().getPreferredSize().width);
				this.table.getJTable().setSize(iTableWidth, this.table.getJTable().getPreferredSize().height);
				this.table.getJTable()
				.getTableHeader()
				.setSize(iTableWidth, this.table.getJTable().getTableHeader().getPreferredSize().height);
				this.table.doLayout();
			}
		}
	}

	@Override
	public void setContent(final Object content) {
		if (this.table != null) {
			this.table.setValue(content);
			if ((this.width != -1) && (this.high != -1)) {
				this.table.setSize(AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						AbstractPrintingElement.millimeterToPagePixels(this.high) * 1);
				this.table.getJTable()
				.getTableHeader()
				.setSize(AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						this.table.getJTable().getTableHeader().getPreferredSize().height);

				this.table.getJTable()
				.setBounds(0, 0, AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						AbstractPrintingElement.millimeterToPagePixels(this.high) * 1);

				this.table.initColumnsWidth();

				this.table.getJTable().doLayout();

			} else if (this.width != -1) {
				this.table.setSize(AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						AbstractPrintingElement.millimeterToPagePixels(this.high) * 1);
				this.table.getJTable()
				.getTableHeader()
				.setSize(AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						this.table.getJTable().getTableHeader().getPreferredSize().height);
				this.table.getJTable()
				.setBounds(0, 0, AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						this.table.getJTable().getPreferredSize().height);
				this.table.initColumnsWidth();
				this.table.getJTable().doLayout();

			} else {
				this.table.setSize(AbstractPrintingElement.millimeterToPagePixels(this.width) * 1,
						AbstractPrintingElement.millimeterToPagePixels(this.high) * 1);
				final int iTableWidth = Math.max(this.table.getJTable().getPreferredSize().width,
						this.table.getJTable().getTableHeader().getPreferredSize().width);
				this.table.getJTable().setBounds(0, 0, iTableWidth, this.table.getJTable().getPreferredSize().height);
				this.table.getJTable()
				.getTableHeader()
				.setSize(iTableWidth, this.table.getJTable().getTableHeader().getPreferredSize().height);
				this.table.initColumnsWidth();
				this.table.getJTable().doLayout();

			}
			// Calculates the cell width
			int iCellwidth = 0;
			for (int i = 0; i < this.table.getJTable().getColumnCount(); i++) {
				iCellwidth += this.table.getJTable().getCellRect(0, i, true).width;
			}
			this.bi = new BufferedImage(iCellwidth - this.table.getJTable().getCellRect(0, 0, false).width,
					this.table.getJTable().getHeight() + this.table.getJTable().getTableHeader().getHeight(),
					BufferedImage.TYPE_3BYTE_BGR);
			final Graphics g = this.bi.getGraphics();
			g.translate(-this.table.getJTable().getCellRect(0, 0, false).width, 0);
			g.setClip(this.table.getJTable().getCellRect(0, 0, false).width, 0, this.bi.getWidth(),
					this.bi.getHeight());
			this.table.getJTable().getTableHeader().paint(g);
			g.translate(0, this.table.getJTable().getTableHeader().getHeight());
			this.table.setDoubleBuffered(false);
			this.table.getJTable().paint(g);
		}
	}

	@Override
	public void paint(final Graphics g, final double scale) {
		if (this.table != null) {
			final Color c = g.getColor();
			g.setColor(this.color);

			g.translate((int) (AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale));
			if (Double.compare(scale, 1.0) != 0) {
				if (this.bi != null) {
					g.drawImage(this.bi, 0, 0, (int) (this.bi.getWidth() * scale), (int) (this.bi.getHeight() * scale),
							this.table);
				}
			} else {
				if (this.table != null) {
					final Shape r = g.getClip();
					final int x = this.table.getJTable().getCellRect(0, 0, false).width;
					g.translate(-x, 0);
					g.setClip(this.table.getJTable().getCellRect(0, 0, false).width, 0,
							this.bi == null ? 0 : this.bi.getWidth(),
									AbstractPrintingElement.millimeterToPagePixels(this.high));

					this.table.getJTable().getTableHeader().paint(g);
					g.translate(0, this.table.getJTable().getTableHeader().getHeight());
					this.table.setDoubleBuffered(false);
					this.table.getJTable().paint(g);
					g.translate(x, 0);
					g.translate(0, -this.table.getJTable().getTableHeader().getHeight());
					g.setClip(r);

				}
			}

			g.translate((int) (-AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
					(int) (-AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale));
			g.setColor(c);
		}
	}

}
