package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.HeadCellRenderer.EText;
import com.ontimize.gui.table.TableSorter.IconN;
import com.ontimize.gui.table.blocked.BlockedTable;
import com.ontimize.gui.table.blocked.BlockedTableModel;
import com.ontimize.util.ParseUtils;

/**
 * This class renders the table header according to the information that must be shown. For example,
 * changes the label and the icon in the header depending on the sorting, grouping, etc., of the
 * information managed by the table.
 */
// HeadCellRenderer
// DefaultTableCellRenderer
public class SortTableCellRenderer extends DefaultTableCellRenderer {

	private static final Logger logger = LoggerFactory.getLogger(SortTableCellRenderer.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String HEADCELLRENDERER_NAME = "TableHeader.renderer";

	/**
	 * Maximum number of lines that the headers can occupy
	 */
	public static int MAX_VALUE_HEAD_RENDERER_LINES = 2;

	/**
	 * Vertical margin to use in the table header
	 */
	public static int defaultVerticalHeaderMargin = 4;

	public static Color defaultForegroundFilterColor = Color.red;

	public static boolean paintSortIcon = false;

	protected int verticalHeaderMargin = SortTableCellRenderer.defaultVerticalHeaderMargin;

	protected int minHeaderHeight;

	protected JTable currentTable;

	protected Font predFont = null;

	protected Color groupBackgroundColor = new Color(201, 203, 235);

	protected Color backgroundColor = null;

	protected Color foregroundColor;

	protected Color foregroundFilterColor;

	protected ImageIcon bgImage;

	protected ImageIcon bgGroupingImage;

	protected Image bgCurrentImage;

	protected Border defaultBorder;

	protected Border lastColumnBorder;

	protected Border firstColumnBorder;

	public static Border emptyBorder = new EmptyBorder(0, 2, 0, 2);

	public SortTableCellRenderer(final JTable table, final Map params) {
		this(table);
		this.init(params);
	}

	public SortTableCellRenderer(final JTable table) {
		super();
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.currentTable = table;
		if (ApplicationManager.useOntimizePlaf) {
			this.predFont = UIManager.getFont("TableHeader:\"TableHeader.renderer\".font") != null
					? UIManager.getFont("TableHeader:\"TableHeader.renderer\".font")
							: this.getFont();
		} else {
			this.predFont = this.getFont();
		}
		this.backgroundColor = (Color) UIManager.get("TableHeader.background");
		this.foregroundColor = (Color) UIManager.get("TableHeader:\"TableHeader.renderer\".TextForeground");
		this.minHeaderHeight = 1;
		this.foregroundFilterColor = SortTableCellRenderer.defaultForegroundFilterColor;
	}

	/**
	 * Adds configurable parameters for class that renders the table header according to the information
	 * that must be shown. This parameters are specified in .xml definition of {@link Table}.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *
	 *        <p>
	 *
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>headerheight</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The height for header.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>headerfont</td>
	 *        <td><i></td>
	 *        <td><code></code></td>
	 *        <td>no</td>
	 *        <td>Font for header text. It is accepted a string like : 'Arial-BOLD-18'. Similar to
	 *        Font.decode</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>headerfg</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Color for foreground in header.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>headerbg</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Color for background in header.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>fontshadowcolor</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Color of shadow for font.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>headerbgimage</td>
	 *        <td><i></td>
	 *        <td></code></td>
	 *        <td>no</td>
	 *        <td>Path to background image.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>headerbggroupimage</td>
	 *        <td><i></td>
	 *        <td></code></td>
	 *        <td>no</td>
	 *        <td>Path to background image when the column is grouped.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>headerborder</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Border of header.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>headerlastcolumnborder</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Border for last column in header</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>headerfirstcolumnborder</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Border for first column in header</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 */
	protected void init(final Map parameters) {
		this.minHeaderHeight = ParseUtils.getInteger((String) parameters.get("headerheight"), this.minHeaderHeight);
		this.predFont = ParseUtils.getFont((String) parameters.get("headerfont"), this.predFont);
		this.setFont(this.predFont);
		this.foregroundColor = ParseUtils.getColor((String) parameters.get("headerfg"), this.foregroundColor);
		this.backgroundColor = ParseUtils.getColor((String) parameters.get("headerbg"), this.backgroundColor);
		final Color shadowColor = ParseUtils.getColor((String) parameters.get("fontshadowcolor"), null);
		if (shadowColor != null) {
			this.setUI(new CustomHeaderUI(shadowColor));
		}

		this.bgImage = ParseUtils.getImageIcon((String) parameters.get("headerbgimage"), null);
		this.bgGroupingImage = ParseUtils.getImageIcon((String) parameters.get("headerbggroupimage"), null);
		this.defaultBorder = ParseUtils.getBorder((String) parameters.get("headerborder"), null);
		this.lastColumnBorder = ParseUtils.getBorder((String) parameters.get("headerlastcolumnborder"),
				this.defaultBorder);
		this.firstColumnBorder = ParseUtils.getBorder((String) parameters.get("headerfirstcolumnborder"),
				this.defaultBorder);
	}

	@Override
	public String getName() {
		return SortTableCellRenderer.HEADCELLRENDERER_NAME;
	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
	}

	@Override
	public void setBorder(final Border border) {
		super.setBorder(border);
	}

	@Override
	public void updateUI() {
		super.updateUI();
		// predFont = getFont();
		this.backgroundColor = this.getBackground();
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (this.bgCurrentImage != null) {
			g.drawImage(this.bgCurrentImage, 0, 0, this.getSize().width, this.getSize().height, this);
		}
		super.paintComponent(g);
	}

	public void setTipWhenNeeded(final JTable table, final Object value, final int column) {
		// TIP
		try {
			this.setToolTipText(null);
			if (table != null) {
				if (this instanceof JLabel) {
					final TableColumn tc = table.getColumn(table.getColumnName(column));
					if (tc.getWidth() < (this.getPreferredSize().width + 4)) {
						this.setToolTipText(this.getText());
					}
				}
			}
		} catch (final Exception e) {
			SortTableCellRenderer.logger.error(null, e);
		}

	}

	protected void configureBackgroundColor(final Component rend, final TableSorter sorter, final int column) {
		if (sorter.isGrouped(this.currentTable.convertColumnIndexToModel(column))) {
			if (this.bgGroupingImage == null) {
				rend.setBackground(this.groupBackgroundColor);
			} else {
				this.bgCurrentImage = this.bgGroupingImage.getImage();
			}
		} else {
			if (this.bgImage == null) {
				rend.setBackground(this.backgroundColor);
			} else {
				this.bgCurrentImage = this.bgImage.getImage();
			}
		}
	}

	protected void configureGroupRenderer(final Component rend, final TableSorter sorter, final int column) {
		if (sorter.isGrouped()) {
			final int func = sorter.getGroupedColumnFunction(this.currentTable.convertColumnIndexToModel(column));
			String funcStr = null;
			if (func >= 0) {
				final GroupOperation operation = (GroupOperation) sorter.getOperations().get(new Integer(func));
				funcStr = " (" + operation.getHeaderText() + ")";
			}
			if (funcStr != null) {
				if (rend instanceof JLabel) {
					((JLabel) rend).setText(((JLabel) rend).getText() + funcStr);
				} else if (rend instanceof JTextComponent) {
					((JTextComponent) rend).setText(((JTextComponent) rend).getText() + funcStr);
				}
			}
		}
	}

	protected void configureSortRenderer(final Component rend, final TableSorter sorter, final int column) {
		boolean isSort = false;
		int sortIndex = -1;
		for (int i = 0; i < sorter.sortingColumns.size(); i++) {
			final int c = ((Integer) sorter.sortingColumns.get(i)).intValue();
			if (column == this.currentTable.convertColumnIndexToView(c)) {
				isSort = true;
				sortIndex = i;
				break;
			}
		}

		if (!isSort) {
			if (rend instanceof EText) {
				((EText) rend).setIcon(null);
			}
			if (rend instanceof JLabel) {
				((JLabel) rend).setIcon(null);
			}
			rend.setFont(this.predFont);
		} else {

			final boolean ascending = ((Boolean) sorter.ascendants.get(sortIndex)).booleanValue();
			final int index = sortIndex;

			if (ascending) {
				Icon ascIcon = null;
				if (SortTableCellRenderer.paintSortIcon) {
					ascIcon = UIManager.getIcon("Table.ascendingSortIcon");
				} else {
					ascIcon = new IconN(ImageManager.getIcon(ImageManager.UPWARD), index);
				}
				if (rend instanceof EText) {
					((EText) rend).setIcon(ascIcon);
				} else if (rend instanceof JLabel) {
					((JLabel) rend).setIcon(ascIcon);
				}
			} else {
				Icon desIcon = null;
				if (SortTableCellRenderer.paintSortIcon) {
					desIcon = UIManager.getIcon("Table.descendingSortIcon");
				} else {
					desIcon = new IconN(ImageManager.getIcon(ImageManager.DOWN_GREEN), index);
				}
				if (rend instanceof EText) {
					((EText) rend).setIcon(desIcon);
				} else if (rend instanceof JLabel) {
					((JLabel) rend).setIcon(desIcon);
				}
			}
			rend.setFont(this.predFont.deriveFont(Font.BOLD));
		}

	}

	protected void configureBorderRenderer(final Component rend, final int column) {
		if ((this.defaultBorder != null) && (rend != null) && (rend instanceof JComponent)) {
			final Table ontimizeTable = this.getOntimizeTable(this.currentTable);
			if ((ontimizeTable != null) && (column == this.getLastColumnIndex())) {
				((JComponent) rend).setBorder(this.lastColumnBorder);
			} else if ((ontimizeTable != null) && (column == this.getFirstColumnIndex())) {
				((JComponent) rend).setBorder(this.firstColumnBorder);
			} else {
				((JComponent) rend).setBorder(this.defaultBorder);
			}
		}
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {
		final Component rend = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
		((JComponent) rend).setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		this.setTipWhenNeeded(table, value, column);

		try {
			final int modelIndex = table.convertColumnIndexToModel(column);
			if (modelIndex > 0) {
				if (value != null) {
					if (rend instanceof JLabel) {
						((JLabel) rend).setText(value.toString());
					} else if (rend instanceof JTextComponent) {
						((JTextComponent) rend).setText(value.toString());
					} else if (rend instanceof EText) {
						((EText) rend).setText(value.toString());
					}
				}
			} else {
				if (rend instanceof JLabel) {
					((JLabel) rend).setText("Nº");
				} else if (rend instanceof JTextComponent) {
					((JTextComponent) rend).setText("Nº");
				} else if (rend instanceof EText) {
					((EText) rend).setText("Nº");
				}
			}

			TableModel model = table.getModel();
			if (model instanceof BlockedTableModel) {
				model = ((BlockedTableModel) model).getTableSorter();
			}
			if ((model != null) && (model instanceof TableSorter)) {
				final TableSorter sorter = (TableSorter) model;
				this.configureBackgroundColor(rend, sorter, column);

				this.configureGroupRenderer(rend, sorter, column);

				if (sorter.isFiltered(table.convertColumnIndexToModel(column))) {
					rend.setForeground(this.foregroundFilterColor);
					if (!sorter.lastFilterOr()) {
						// Connect filter
						final Object filter = sorter.getColumnFilter(table.getColumnName(column));
						if (rend instanceof JLabel) {
							((JLabel) rend).setText(((JLabel) rend).getText() + " '" + filter + "'");
						} else if (rend instanceof JTextComponent) {
							((JTextComponent) rend).setText(((JTextComponent) rend).getText() + " '" + filter + "'");
						}
					}
				} else {
					rend.setForeground(this.foregroundColor);
				}

				this.configureSortRenderer(rend, sorter, column);
			}

			this.configureBorderRenderer(rend, column);

			// TODO BlockedTable
			// if (table instanceof BlockedTable) {
			// BlockedTable bTable = (BlockedTable) table;
			// if (bTable.getBlockedColumnIndex() == column) {
			// rend.setBackground(Color.ORANGE);
			// }
			// }

			return rend;
		} catch (final Exception e) {
			SortTableCellRenderer.logger.error(null, e);
			return rend;
		}
	}

	protected int getLastColumnIndex() {
		final int columnCount = this.currentTable.getColumnCount();
		int lastColumnIndex = -1;
		for (int i = 0; i < columnCount; i++) {
			final int width = this.currentTable.getColumnModel().getColumn(i).getWidth();
			if (width > 0) {
				lastColumnIndex = i;
			}
		}
		return lastColumnIndex;
	}

	protected int getFirstColumnIndex() {
		final int columnCount = this.currentTable.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			final int width = this.currentTable.getColumnModel().getColumn(i).getWidth();
			if (width > 0) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		final int height = Math.max(this.minHeaderHeight, d.height + this.verticalHeaderMargin);
		return new Dimension(d.width + 10, height);
	}

	public int getVerticalHeaderMargin() {
		return this.verticalHeaderMargin;
	}

	public void setVerticalHeaderMargin(final int verticalHeaderMargin) {
		this.verticalHeaderMargin = verticalHeaderMargin;
	}

	protected Table getOntimizeTable(final JTable table) {
		return (Table) SwingUtilities.getAncestorOfClass(Table.class, table);
	}

	public static class ListMouseListener extends MouseAdapter {

		public ListMouseListener() {
		}

		// If the selected column is the same that the previous click then
		// change the sort mode. If it is a different column then ascending

		int previousColumnIndex = -1;

		@Override
		public void mouseReleased(final MouseEvent e) {
		}

		protected TableSorter retrieveTableSorter(final JTable table) {
			if (table instanceof BlockedTable) {
				return ((BlockedTableModel) table.getModel()).getTableSorter();
			}
			return (TableSorter) table.getModel();
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			final JTableHeader tableHeader = (JTableHeader) e.getSource();
			final JTable currentTable = tableHeader.getTable();
			final Table table = (Table) SwingUtilities.getAncestorOfClass(Table.class, currentTable);

			if (e.getClickCount() == 2) {
				final TableColumnModel columnModel = currentTable.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = currentTable.convertColumnIndexToModel(viewColumn);
				if (this.getResizingColumn(currentTable.getTableHeader(), e.getPoint(), viewColumn) != null) {
					retrieveTableSorter(currentTable).fireSizeColumnToFit(column);
				}
				return;
			}
			boolean ascending = true;
			// Right button
			final TableSorter sorter = retrieveTableSorter(currentTable);
			if ((e.getModifiers() == InputEvent.META_MASK) && sorter.isFilterEnabled()) {
				if (sorter.isGrouped() || sorter.lastFilterOr()) {
					return;
				}
				if (sorter.getFilterDialog() == null) {

					final Window w = SwingUtilities.getWindowAncestor(currentTable);
					if (table != null) {
						if (w instanceof Dialog) {
							sorter.setFilterDialog(new FilterDialog((Dialog) w, table));
						} else {
							sorter.setFilterDialog(new FilterDialog((Frame) w, table));
						}
						sorter.getFilterDialog().setResourceBundle(sorter.bundle);
					}
				}

				if (!(currentTable.getTableHeader().getDefaultRenderer() instanceof SortTableCellRenderer)) {
					final SortTableCellRenderer rend = new SortTableCellRenderer(currentTable);
					// rend.setMaxLinesNumber(SortTableCellRenderer.MAX_VALUE_HEAD_RENDERER_LINES);
					currentTable.getTableHeader().setDefaultRenderer(rend);
					SortTableCellRenderer.logger.debug("--");
				}

				currentTable.getTableHeader().repaint();
				SortTableCellRenderer.logger.debug("Showing filter window");
				if (sorter.getFilterDialog() != null) {
					sorter.getFilterDialog().show(e);
				}
			} else if (e.isControlDown() && e.isAltDown() && ApplicationManager.DEBUG) {
				final TableColumnModel columnModel = currentTable.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = currentTable.convertColumnIndexToModel(viewColumn);
				final TableColumn col = columnModel.getColumn(viewColumn);
				final Object id = col.getIdentifier();
				final String sName = currentTable.getColumnName(viewColumn);
				final TableCellRenderer renderer = col.getCellRenderer();
				String descrRenderer = "null";
				if (renderer != null) {
					descrRenderer = renderer.getClass().toString();
				}
				MessageDialog.showErrorMessage(null,
						"Column: View index:" + viewColumn + ", Model index: " + column + "Id: " + id + " , Name: "
								+ sName + " , Renderer: "
								+ descrRenderer + " PreferredWidth: " + col.getPreferredWidth() + " Width: "
								+ col.getWidth() + " Max width: " + col.getMaxWidth());

				return;
			} else if (e.isControlDown() && table != null && table.isBlockedEnabled()) {
				final TableColumnModel columnModel = currentTable.getColumnModel();
				final int column = columnModel.getColumnIndexAtX(e.getX());
				// int column =
						// currentTable.convertColumnIndexToModel(viewColumn);
				final TableColumn col = columnModel.getColumn(column);
				table.setBlockedColumnIndex(column);
			} else {
				final long t = System.currentTimeMillis();
				final TableColumnModel columnModel = currentTable.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = currentTable.convertColumnIndexToModel(viewColumn);
				if (this.getResizingColumn(currentTable.getTableHeader(), e.getPoint(), viewColumn) != null) {
					return;
				}
				if ((e.getClickCount() == 1) && (column != -1) && sorter.isSortEnabled()) {
					// If column is yet sorted then make swap
					// ascending-descending-nothing
					if (sorter.isSorted(column)) {
						ascending = sorter.isAscending(column);
						ascending = !ascending;
						if (ascending == !e.isShiftDown()) {
							sorter.resetOrder(column);
							this.previousColumnIndex = column;
							return;
						}
					} else {
						ascending = !e.isShiftDown();
					}
					if (!TableSorter.MULTIORDER_PERMIT) {
						sorter.resetOrder();
					}
					try {
						sorter.sortByColumn(column, ascending);
						// Update the previous index
						this.previousColumnIndex = column;
						// Icon to show the current sort type
						// In not sorted columns icon is null
						if (!(currentTable.getTableHeader().getDefaultRenderer() instanceof SortTableCellRenderer)) {
							final SortTableCellRenderer rend = new SortTableCellRenderer(currentTable);
							// rend.setMaxLinesNumber(SortTableCellRenderer.MAX_VALUE_HEAD_RENDERER_LINES);
							currentTable.getTableHeader().setDefaultRenderer(rend);
						} else {
						}
						currentTable.requestFocus();
					} catch (final Exception ex) {
						SortTableCellRenderer.logger.trace(null, ex);
					} finally {
						currentTable.getTableHeader().repaint();
					}
				}
				if (ApplicationManager.DEBUG_TIMES) {
					SortTableCellRenderer.logger.debug("TableSorter: Time to sort the table (Total event): "
							+ (System.currentTimeMillis() - t) + " ms");
				}
			}
		}

		/**
		 * Compares the point with the header of the column specified by the columnIndex. If the point
		 * corresponds to the header, the method returns the related TableColumn.
		 * @param header the table header
		 * @param point a point coming from a mouse event
		 * @param columnIndex the columnIndex to compare return the TableColumn corresponding to the
		 *        columnIndex in case the mouse event was performed into the column header; null otherwise
		 */
		private TableColumn getResizingColumn(final JTableHeader header, final Point point, final int columnIndex) {
			if (columnIndex == -1) {
				return null;
			}
			final Rectangle r = header.getHeaderRect(columnIndex);
			r.grow(-3, 0);
			if (r.contains(point)) {
				return null;
			}
			final int midPoint = r.x + (r.width / 2);
			int columnIndexLocal;
			if (header.getComponentOrientation().isLeftToRight()) {
				columnIndexLocal = point.x < midPoint ? columnIndex - 1 : columnIndex;
			} else {
				columnIndexLocal = point.x < midPoint ? columnIndex : columnIndex - 1;
			}
			if (columnIndexLocal == -1) {
				return null;
			}
			return header.getColumnModel().getColumn(columnIndexLocal);
		}

	}

	public static class CustomHeaderUI extends BasicLabelUI {

		protected Color shadowColor;

		public CustomHeaderUI(final Color shadowColor) {
			this.shadowColor = shadowColor;
		}

		@Override
		protected void paintEnabledText(final JLabel l, final Graphics g, final String s, final int textX, final int textY) {
			final int mnemIndex = l.getDisplayedMnemonicIndex();
			g.setColor(this.shadowColor);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemIndex, textX, textY + 1);
			g.setColor(l.getForeground());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemIndex, textX, textY);
		}

	}

}
