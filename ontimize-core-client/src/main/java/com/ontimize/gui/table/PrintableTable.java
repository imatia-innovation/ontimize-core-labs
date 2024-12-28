package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;

/**
 * This class wraps a Table and adds the logic necessary to allow the table to be printable, this
 * is, to have the interface to extract the table information.
 */
public class PrintableTable implements Printable {

	private static final Logger logger = LoggerFactory.getLogger(PrintableTable.class);

	protected static final String PAGES_READY = "table.?_pages_ready";

	protected static final String PREPARING_PRINTING_PAGES = "table.preparing_printing";

	ProgressPrintingWindow printingProgressWindow = null;

	Table table = null;

	private final int lateralMargin = 4;

	private final int verticalMargin = 4;

	private final int pageNumberMargin = 20;

	private final int pageTitleMargin = 20;

	private int[] pageIndexes = null;

	private PageFormat pf = null;

	private PrinterJob pj = PrinterJob.getPrinterJob();

	private List printedColumns = null;

	private String pageTitles = null;

	private boolean isPrinting = false;

	private final int MIN_PAGE_HEIGHT = 50;

	protected boolean fitInPage = false;

	protected int totalWidth = -1;

	protected String date = null;

	protected SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();

	public PrintableTable(final Table t, final boolean scaleToFit) {
		this.df.applyPattern("HH:mm  dd/MM/yyyy");
		this.date = this.df.format(new java.util.Date());
		this.table = t;
		this.table.setLineRemark(false);
		this.table.setRowNumberColumnVisible(false);

		this.table.getJTable().getTableHeader().setOpaque(false);
		final ObjectCellRenderer rend = new ObjectCellRenderer() {

			protected Border borde = new LineBorder(Color.darkGray);

			protected Color bgColor = new Color(218, 215, 177);

			@Override
			public Component getTableCellRendererComponent(final JTable jTable, Object oValue, final boolean selected,
					final boolean hasFocus, final int row, final int column) {
				if ((oValue != null) && oValue.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
					oValue = null;
				}

				final Component c = super.getTableCellRendererComponent(jTable, oValue, selected, hasFocus, row, column);
				((JComponent) c).setBorder(this.borde);
				((JComponent) c).setBackground(this.bgColor);
				return c;
			}
		};

		rend.setFont(rend.getFont().deriveFont(Font.BOLD));
		rend.setLineRemark(false);
		this.table.getJTable().getTableHeader().setDefaultRenderer(rend);
		final TableColumnModel tcm = this.table.getJTable().getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setHeaderRenderer(rend);
		}
		this.table.getJTable().setGridColor(Color.black);

		final TableColumn tc = this.table.getJTable().getColumn(ExtendedTableModel.ROW_NUMBERS_COLUMN);
		if (tc != null) {
			tc.setCellRenderer(rend);
		}
		this.pf = this.pj.defaultPage();
	}

	public void setRemarkLine(final boolean remark) {
		this.table.setLineRemark(remark);
	}

	public void dispose() throws Exception {
		this.table.free();
		this.table = null;
		if (this.printingProgressWindow != null) {
			this.printingProgressWindow.setVisible(false);
			this.printingProgressWindow.dispose();
		}
		this.printedColumns = null;
		this.pf = null;
		this.pj = null;
	}

	public void cancelPrinting() {
		if (this.printingProgressWindow != null) {
			this.printingProgressWindow.setVisible(false);
		}
		this.pj.cancel();
	}

	public void setPageTitles(final String t) {
		this.pageTitles = t;
	}

	public PageFormat getPageFormat() {
		return this.pf;
	}

	public int setPageFormat(final PageFormat pf) {
		if (pf.getImageableHeight() < this.MIN_PAGE_HEIGHT) {
			return 0;
		}
		this.pf = pf;
		this.pageIndexes = this.preparePages(pf);
		return this.pageIndexes.length;
	}

	private int preparePagesToPrinting(final PageFormat pf) {
		if (pf.getImageableHeight() < this.MIN_PAGE_HEIGHT) {
			return 0;
		}
		this.pf = pf;
		this.pageIndexes = this.preparePages(pf);
		return this.pageIndexes.length;
	}

	public boolean fitInPage() {
		return this.fitInPage;
	}

	public boolean fitInPage(final int[] columnWidths) {
		int totalWidth = 0;
		for (int i = 0; i < columnWidths.length; i++) {
			totalWidth += columnWidths[i];
		}
		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Total width required by table: " + totalWidth);
		}
		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Available width: " + (int) (this.pf.getImageableWidth() - this.lateralMargin));
		}

		if (totalWidth > (int) (this.pf.getImageableWidth() - this.lateralMargin)) {
			return false;
		} else {
			return true;
		}
	}

	private boolean fitInPage(final PageFormat pf) {

		final int[] columnWidths = this.table.setPreferredTableColumnWidths(this.printedColumns,
				this.printingProgressWindow.progressBar);
		// Calculates the total width
		this.totalWidth = 0;
		for (int i = 0; i < columnWidths.length; i++) {
			this.totalWidth += columnWidths[i];
		}
		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Total width required by table: " + this.totalWidth);
		}
		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Available width: " + (int) (pf.getImageableWidth() - this.lateralMargin));
		}

		if (this.totalWidth > (int) (pf.getImageableWidth() - this.lateralMargin)) {
			if (this.pf.equals(pf)) {
				this.fitInPage = false;
			}
			return false;
		} else {
			if (this.pf.equals(pf)) {
				this.fitInPage = true;
			}
			return true;
		}
	}

	public int getTotalWidth() {
		return this.totalWidth;
	}

	public int getAvailableWidth() {
		return (int) (this.pf.getImageableWidth() - this.lateralMargin);
	}

	void setTablePageWidth() {
		this.table.getJTable()
		.setBounds(0, 0, (int) this.pf.getImageableWidth() - this.lateralMargin,
				this.table.getJTable().getPreferredSize().height);
		this.table.getJTable()
		.getTableHeader()
		.setSize((int) this.pf.getImageableWidth() - this.lateralMargin,
				this.table.getJTable().getTableHeader().getPreferredSize().height);
		this.table.getJTable().doLayout();
	}

	private int[] preparePages(final PageFormat pf) {
		final long t = System.currentTimeMillis();
		if (pf.getImageableHeight() < this.MIN_PAGE_HEIGHT) {
			return new int[1];
		}
		this.pf = pf;
		if (this.printingProgressWindow == null) {
			this.printingProgressWindow = new ProgressPrintingWindow(this);
			if (this.table.parentFrame != null) {
				this.printingProgressWindow.setIconImage(this.table.parentFrame.getIconImage());
			}
		}
		if (this.printingProgressWindow != null) {
			this.printingProgressWindow.setVisible(true);
		}
		if (this.printingProgressWindow != null) {
			this.printingProgressWindow
			.setStateText(ApplicationManager.getTranslation(PrintableTable.PREPARING_PRINTING_PAGES));
		}
		Thread.yield();
		// table.setLineRemark(false);
		this.table.getJTable().setDoubleBuffered(false);
		if (Table.DEBUG_MEMORY) {
			PrintableTable.logger.debug(
					"PrintableTable. Setting columns width: Memory used: "
							+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0)
							+ " kbytes");
		}
		final int[] columnWidths = this.table.setPreferredTableColumnWidths(this.printedColumns,
				this.printingProgressWindow.progressBar);
		if (Table.DEBUG_MEMORY) {
			PrintableTable.logger
			.debug("PrintableTable. Column width set: Memory used: "
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0)
					+ " kbytes");
		}
		this.fitInPage = this.fitInPage(columnWidths);
		Thread.yield();

		this.setTablePageWidth();

		int statimatedTotalPages = (int) (this.table.getJTable().getPreferredSize().height / pf.getImageableHeight());
		if (statimatedTotalPages == 0) {
			statimatedTotalPages++;
		}
		if (this.printingProgressWindow != null) {
			this.printingProgressWindow.setMaxProgressBar(statimatedTotalPages);
		}

		Thread.yield();
		final int rowsNumber = this.table.getJTable().getRowCount();
		if (rowsNumber == 0) {
			return new int[0];
		}

		if (this.table.getJTable().getColumnCount() <= 1) {
			return new int[0];
		}
		final int headerHeight = this.table.getJTable().getTableHeader().getHeight();
		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Header height = " + headerHeight);
		}
		// Calculates the index of the first row in the page.
		// If the first row is in two pages it is printed. If the last row is
		// between two pages
		// it is printed in the next page
		int pagesNumber = 0;
		int lastPageRowIndex = -1;
		int[] lastPageRowsIndex = new int[statimatedTotalPages * 2];
		for (int i = 0; i < lastPageRowsIndex.length; i++) {
			lastPageRowsIndex[i] = -1;
		}

		if (Table.DEBUG_MEMORY) {
			PrintableTable.logger.debug("PrintableTable. Calculating limit rows per page: Memory used: "
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
							.freeMemory()) / 1024.0)
					+ " kbytes");
		}
		while (lastPageRowIndex < (rowsNumber - 1)) {
			if (this.printingProgressWindow != null) {
				this.printingProgressWindow.setStateText("Page ready " + (pagesNumber + 1));
				this.printingProgressWindow.setPosProgressBar(pagesNumber + 1);
			}
			final int posPageBottom = (pagesNumber + 1) * ((int) pf.getImageableHeight() - headerHeight - this.verticalMargin
					- this.pageNumberMargin - this.pageTitleMargin);
			final int stimatedLastRowPageIndex = this.table.getJTable().rowAtPoint(new Point(0, posPageBottom));
			if (stimatedLastRowPageIndex > 0) {
				lastPageRowIndex = stimatedLastRowPageIndex - 1;
			}

			final long tIni = System.currentTimeMillis();
			for (int i = lastPageRowIndex; i < rowsNumber; i++) {
				Thread.yield();

				final Rectangle rect = this.table.getJTable().getCellRect(i, 1, true);
				// Now see the situation in the top corner
				final int lastRowPos = rect.y + rect.height;
				if (lastRowPos > posPageBottom) {
					if (ApplicationManager.DEBUG_TIMES) {
						PrintableTable.logger
						.debug("Page time: " + pagesNumber + " " + (System.currentTimeMillis() - tIni));
					}
					// This is the row to the next page
					lastPageRowIndex = i - 1;
					if (pagesNumber >= (lastPageRowsIndex.length - 1)) {
						// Change the size
						final int[] bufferNew = new int[lastPageRowsIndex.length * 2];
						for (int a = lastPageRowsIndex.length; a < bufferNew.length; a++) {
							bufferNew[a] = -1;
						}
						System.arraycopy(lastPageRowsIndex, 0, bufferNew, 0, lastPageRowsIndex.length);
						lastPageRowsIndex = bufferNew;
					}
					lastPageRowsIndex[pagesNumber] = lastPageRowIndex;
					pagesNumber++;
					if (ApplicationManager.DEBUG) {
						PrintableTable.logger.debug("Page ready" + pagesNumber + " . Last row= " + lastPageRowIndex);
					}
					break;
				}
				lastPageRowIndex = i;
				lastPageRowsIndex[pagesNumber] = lastPageRowIndex;
			}
		}

		if (Table.DEBUG_MEMORY) {
			PrintableTable.logger
			.debug("PrintableTable. Limit rows caculated: Memory use: "
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0)
					+ " kbytes");
		}
		// Create an array with integer values
		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Number of pages to print the table= " + pagesNumber);
		}

		// Search for -1 index
		final int[] index = new int[pagesNumber + 1];
		for (int i = 0; i < (pagesNumber + 1); i++) {
			index[i] = lastPageRowsIndex[i];
		}

		if (ApplicationManager.DEBUG_TIMES) {
			PrintableTable.logger
			.debug("Time to prepare " + index.length + " pages: " + (System.currentTimeMillis() - t));
		}
		if (this.printingProgressWindow != null) {
			this.printingProgressWindow
			.setStateText(ApplicationManager.getTranslation(PrintableTable.PAGES_READY,
					this.table.getResourceBundle(), new Object[] { "" + index.length }));
		}
		this.pageIndexes = index;
		return index;
	}

	public void setPaintGrid(final boolean paint) {
		this.table.getJTable().setShowGrid(paint);
	}

	public void setPaintRowNumber(final boolean paintRowNumber) {
		this.table.setRowNumberColumnVisible(paintRowNumber);
		this.preparePagesToPrinting(this.pf);
		if (this.printingProgressWindow != null) {
			this.printingProgressWindow.toBack();
		}
	}

	public int getPagesNumber() {
		return this.pageIndexes.length;
	}

	public int getFontSize() {
		return this.table.getFont().getSize();
	}

	public void setFontSize(final int fontSize) {
		this.table.setFont(this.table.getFont().deriveFont((float) fontSize));
		this.preparePagesToPrinting(this.pf);
	}

	public int configurePage() {
		final PageFormat previousPF = this.pf;
		this.pf = this.pj.pageDialog(this.pf);
		if (this.pf != previousPF) {
			this.setPageFormat(this.pf);
		}
		return this.pageIndexes.length;
	}

	public void setPrintingColumns(final List cols) {
		this.printedColumns = cols;
		this.preparePagesToPrinting(this.pf);
	}

	public void setPrintingColumns(final List cols, final boolean prepare) {
		this.printedColumns = cols;
		if (prepare) {
			this.preparePagesToPrinting(this.pf);
		}
	}

	public void moveColumnLeft(final Object col) {
		final TableColumnModel m = this.table.getJTable().getColumnModel();

		final int previousIndex = m.getColumnIndex(col);
		if (previousIndex > 0) {
			m.moveColumn(previousIndex, previousIndex - 1);
		}
	}

	public void moveColumnRight(final Object col) {
		final TableColumnModel m = this.table.getJTable().getColumnModel();
		final int previousIndex = m.getColumnIndex(col);
		if (previousIndex < (m.getColumnCount() - 1)) {
			m.moveColumn(previousIndex, previousIndex + 1);
		}
	}

	public List getColumnsToPrintingByOrder() {
		final TableColumnModel m = this.table.getJTable().getColumnModel();
		final List columns = new Vector();
		for (int i = 0; i < m.getColumnCount(); i++) {
			final TableColumn tc = m.getColumn(i);
			if (this.printedColumns.contains(tc.getIdentifier())) {
				columns.add(columns.size(), tc.getIdentifier());
			}
		}
		return columns;
	}

	public boolean isPrinting() {
		return this.isPrinting;
	}

	public boolean print(final String jobName) throws Exception {
		if (this.printingProgressWindow == null) {
			this.printingProgressWindow = new ProgressPrintingWindow(this);
			if (this.table.parentFrame != null) {
				this.printingProgressWindow.setIconImage(this.table.parentFrame.getIconImage());
			}
		}
		this.printingProgressWindow.setStateText(ApplicationManager.getTranslation(Table.PRINT_START));
		this.printingProgressWindow.setPosProgressBar(0);
		this.printingProgressWindow.setVisible(true);
		this.printingProgressWindow.cancelButton.setEnabled(true);
		this.isPrinting = true;
		if (jobName != null) {
			this.pj.setJobName(jobName);
		}
		if (this.pj.printDialog()) {
			final short idPrintProcess = ApplicationManager.startedPrintingProcess();
			this.date = this.df.format(new java.util.Date());
			try {
				if (this.pf.getImageableHeight() < this.MIN_PAGE_HEIGHT) {
					return false;
				}
				Thread.yield();
				this.pj.setPrintable(this, this.pf);
				Thread.yield();
				this.pj.print();
				this.isPrinting = false;
				ApplicationManager.endedPrintingProcess(idPrintProcess);
				// Show a message because the printing process is finished
				if (this.printingProgressWindow != null) {
					this.printingProgressWindow.setVisible(false);
				}
				if (this.table.getParentForm() != null) {
					this.table.getParentForm().message(Table.M_PRINTING_FINISHED, Form.INFORMATION_MESSAGE);
				}
			} finally {
				ApplicationManager.endedPrintingProcess(idPrintProcess);
				this.isPrinting = false;
				if (this.printingProgressWindow != null) {
					this.printingProgressWindow.setVisible(false);
				}
			}
			return true;
		} else {
			if (this.printingProgressWindow != null) {
				this.printingProgressWindow.setStateText(Table.M_PRINTING_CANCELED);
			}
			if (this.table.getParentForm() != null) {
				this.table.getParentForm().message(Table.M_PRINTING_CANCELED, Form.INFORMATION_MESSAGE);
			}
			if (this.printingProgressWindow != null) {
				this.printingProgressWindow.setVisible(false);
			}
			this.isPrinting = false;
			return false;
		}
	}

	public boolean print() throws Exception {
		return this.print((String) null);
	}

	int printInPage(final Graphics g, final PageFormat pf, final int pageIndex, final boolean updateProgress, final double scale) {
		if (pageIndex >= this.pageIndexes.length) {
			return Printable.NO_SUCH_PAGE;
		}
		if (updateProgress) {
			if (this.printingProgressWindow != null) {
				this.printingProgressWindow.setMaxProgressBar(this.pageIndexes.length * 2);
			}
			if (this.printingProgressWindow != null) {
				this.printingProgressWindow.setPosProgressBar(((pageIndex + 1) * 2) - 1);
			}
			if (this.printingProgressWindow != null) {
				this.printingProgressWindow.setStateText("Printing page " + (pageIndex + 1));
			}
			Thread.yield();
		}
		try {
			Thread.sleep(10);
		} catch (final Exception e) {
			PrintableTable.logger.trace(null, e);
		}

		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Printing page = " + (pageIndex + 1));
		}
		final int headerHeight = this.table.getJTable().getTableHeader().getHeight();
		// Now in order to the page number print the rows:
		int firstPageRowIndex = 0;
		int lastPageRowIndex = 0;
		if (pageIndex > 0) {
			firstPageRowIndex = this.pageIndexes[pageIndex - 1] + 1;
		}
		lastPageRowIndex = this.pageIndexes[pageIndex];
		// Print
		if (ApplicationManager.DEBUG) {
			PrintableTable.logger.debug("Printing rows: " + firstPageRowIndex + " to " + lastPageRowIndex);
		}
		final Graphics2D g2D = (Graphics2D) g;
		if (Double.compare(scale, 1.0) != 0) {
			g2D.scale(scale, scale);
		}
		g2D.translate(pf.getImageableX() + (this.lateralMargin / 2),
				pf.getImageableY() + (this.verticalMargin / 2) + this.pageTitleMargin);
		// Title
		if (this.pageTitles != null) {
			g2D.setColor(Color.black);
			g2D.setFont(g2D.getFont().deriveFont(Font.BOLD));
			g2D.drawString(this.pageTitles,
					(int) ((pf.getImageableWidth() - g2D.getFontMetrics().stringWidth(this.pageTitles)
							- this.lateralMargin) / 2),
					0 - g2D.getFontMetrics().getDescent() - 2);
		}
		Thread.yield();
		// Page number
		final StringBuilder pageNumberSB = new StringBuilder();
		pageNumberSB.append(pageIndex + 1);
		pageNumberSB.append(" / ");
		pageNumberSB.append(this.pageIndexes.length);
		final String pageNumberText = pageNumberSB.toString();

		g2D.setColor(Color.black);
		g2D.drawLine(this.lateralMargin,
				(int) (pf.getImageableHeight() - g2D.getFontMetrics().getDescent() - (this.verticalMargin / 2)
						- this.pageTitleMargin) - 10,
				(int) (pf.getImageableWidth() - this.lateralMargin),
				(int) (pf.getImageableHeight() - g2D.getFontMetrics().getDescent() - (this.verticalMargin / 2)
						- this.pageTitleMargin) - 10);
		g2D.setFont(g2D.getFont().deriveFont((float) 8));
		g2D.drawString(pageNumberText,
				(int) (pf.getImageableWidth() - g2D.getFontMetrics().stringWidth(pageNumberText) - this.lateralMargin),
				(int) (pf.getImageableHeight() - g2D.getFontMetrics().getDescent() - (this.verticalMargin / 2)
						- this.pageTitleMargin));

		g2D.drawString(this.date, this.lateralMargin, (int) (pf.getImageableHeight() - g2D.getFontMetrics().getDescent()
				- (this.verticalMargin / 2) - this.pageTitleMargin));
		Thread.yield();

		// int heightTranslation = -(int) (pageIndex * pf.getImageableHeight());
		final int firstRowTopPixel = this.table.getJTable().getCellRect(firstPageRowIndex, 0, true).y;
		final int lastRowBottomPixel = this.table.getJTable().getCellRect(lastPageRowIndex, 0, true).y
				+ this.table.getJTable().getCellRect(lastPageRowIndex, 0, true).height;

		// Adjust the height translation
		Thread.yield();
		final int heightTranslation = -firstRowTopPixel;
		g2D.setClip(0, 0, this.table.getJTable().getTableHeader().getWidth(),
				this.table.getJTable().getTableHeader().getHeight());
		Thread.yield();
		this.table.getJTable().getTableHeader().paint(g2D);
		g2D.setClip(-1, headerHeight, (int) pf.getImageableWidth(), lastRowBottomPixel - firstRowTopPixel);
		g2D.translate(0, heightTranslation);
		g2D.translate(0, headerHeight);
		Thread.yield();
		this.table.getJTable().paint(g2D);
		Thread.yield();
		return Printable.PAGE_EXISTS;
	}

	@Override
	public int print(final Graphics g, final PageFormat pf, final int pageIndex) {
		return this.printInPage(g, pf, pageIndex, true, 1.0);
	}

}
