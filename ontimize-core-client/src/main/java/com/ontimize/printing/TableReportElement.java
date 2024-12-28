package com.ontimize.printing;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.text.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;

public class TableReportElement implements ReportElement {

	private static final Logger logger = LoggerFactory.getLogger(TableReportElement.class);

	private static boolean DEBUG = false;

	protected String htmlCode = "";

	protected String identificator = "";

	protected Map data = null;

	protected int border = 1;

	protected int fontSize = 1;

	protected int tableCellspacing = 0;

	protected int tableCellpadding = 0;

	protected Layer layout = null;

	boolean sumColumn = false;

	String[] sumColumns = null;

	double[] columnAccumulatedTotal = null;

	int columnNumber = 0;

	protected TextAttributes tableTextAttr = TextAttributes.getDefaultAttributes();

	protected List sorterColumns = null;

	/**
	 * Builds a table with the appropriate number of rows and columns and with the specified indentifier
	 * @param id
	 * @param width
	 * @param height
	 * @param pixels
	 * @param tableValues
	 */
	public TableReportElement(final String id, final int width, final int height, final boolean pixels, final Map tableValues) {
		this.layout = new Layer(id, width, height, pixels, ReportElement.ALIGN_CENTER);
		this.data = tableValues;
		this.columnNumber = this.data.size();
		this.identificator = id;
		this.tableTextAttr.setFontSize(this.fontSize);
	}

	public TableReportElement(final String id, final int width, final int height, final boolean pixels, final Map tableValues, final int fontSize) {
		this.fontSize = 1;
		this.layout = new Layer(id, width, height, pixels, ReportElement.ALIGN_CENTER);
		this.data = tableValues;
		this.columnNumber = this.data.size();
		this.identificator = id;
		this.tableTextAttr.setFontSize(fontSize);
	}

	public TableReportElement(final String id, final int width, final int height, final boolean pixels, final Map tableValues,
			final List sortColumns, final int fontSize) {
		this.fontSize = 1;
		this.layout = new Layer(id, width, height, pixels, ReportElement.ALIGN_CENTER);
		this.data = tableValues;
		this.sorterColumns = sortColumns;
		this.columnNumber = this.data.size();
		this.identificator = id;
		this.tableTextAttr.setFontSize(fontSize);
	}

	/**
	 * Values in the specified column will be sum at the end of the page (if the table is in more than
	 * one page) and at the end of the table
	 * @param columnTitles
	 */
	public void sumColumns(final String[] columnTitles) {
		this.sumColumn = true;
		this.sumColumns = columnTitles;
		this.columnAccumulatedTotal = new double[columnTitles.length];
	}

	/**
	 * Puts or removes a border the table border
	 * @param bUseBorder
	 */
	public void setBorder(final boolean bUseBorder) {
		if (bUseBorder) {
			this.border = 1;
		} else {
			this.border = 0;
		}
	}

	public void setCellpadding(final int cellpadding) {
		this.tableCellpadding = cellpadding;
	}

	public void setCellspacing(final int cellspacing) {
		this.tableCellspacing = cellspacing;
	}

	@Override
	public void insert(final ReportFrame reportFrame, final boolean multipage) throws Error {
		try {
			final long t = System.currentTimeMillis();
			this.insertTable(reportFrame, multipage, this.data, 5);
			final long t1 = System.currentTimeMillis();
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				TableReportElement.logger.debug("Time to insert the report table : " + (t1 - t));
			}
			final long t2 = System.currentTimeMillis();
			// At the end of the table insert the last text
			final Element tableElement = reportFrame.getCurrentPage().getElementById(this.identificator).getElement(0);

			// Inserts the sum
			final StringBuilder sbSumText = new StringBuilder();
			if (this.sumColumn) {
				for (int n = 0; n < this.sumColumns.length; n++) {
					double total = this.columnAccumulatedTotal[n];
					final List vColumValues = (List) this.data.get(this.sumColumns[n]);
					if (vColumValues != null) {
						for (int m = 0; m < vColumValues.size(); m++) {
							final Object oColumValue = vColumValues.get(m);
							if (oColumValue != null) {
								try {
									final Number num = NumberFormat.getNumberInstance().parse(oColumValue.toString());
									final double dNumber = num.doubleValue();
									total = total + dNumber;
								} catch (final Exception e) {
									TableReportElement.logger.error(null, e);
								}
							}
						}
						this.columnAccumulatedTotal[n] = total;
					}
					sbSumText.append(this.sumColumns[n] + ": " + Double.toString(total) + ";");
				}
				final TextAttributes textAttributes = new TextAttributes(TextAttributes.ARIAL, this.fontSize,
						TextAttributes.BOLD_ITALICS);
				reportFrame.getCurrentPage()
				.getHTMLDocument()
				.insertBeforeEnd(tableElement,
						"<TR><TD colspan='" + Integer.toString(this.columnNumber)
						+ "'style='border-style: solid;border-color:gray'>" + textAttributes.getStartTag()
						+ sbSumText
						.toString()
						+ textAttributes.getEndTag() + "</TD></TR>");
			}
			final long t3 = System.currentTimeMillis();
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				TableReportElement.logger.debug("Time ot insert the sum columns : " + (t3 - t2));
			}
		} catch (final Exception e) {
			TableReportElement.logger.error(null, e);
		}
	}

	private void insertTable(final ReportFrame reportFrame, final boolean multipage, final Map tableValues, int rowsCount) {
		// Number of rows in one page. Used to optimize the next page
		// configuration
		int iInsertedRowsNumber = 0;
		// Inserts the html code for the layer.
		try {
			// Insert the rows at the end of the body. When the page is full
			// creates a new JTextPane and other HTML document

			final long t = System.currentTimeMillis();
			List vColumNames = null;
			if (this.sorterColumns == null) {
				vColumNames = new Vector();
				final Enumeration enumKeys = Collections.enumeration(tableValues.keySet());
				while (enumKeys.hasMoreElements()) {
					vColumNames.add(enumKeys.nextElement());
				}
			} else {
				vColumNames = this.sorterColumns;
			}
			final StringBuilder sbHeaderString = new StringBuilder(this.layout.getStartTag() + "<TABLE width='100%' BORDER='"
					+ Integer
					.toString(this.border)
					+ "' CELLPADDING='" + Integer.toString(this.tableCellpadding) + "' CELLSPACING='"
					+ Integer.toString(
							this.tableCellspacing)
					+ "' style='border-top-width: thin; border-left-width: thin; border-bottom-width: thin; border-right-width:thin;border-style:solid;border-color:white'> <TR >");

			for (int i = 0; i < vColumNames.size(); i++) {
				sbHeaderString.append("<TH   style='border-style: solid;border-color:black'>"
						+ this.tableTextAttr.getStartTag() + vColumNames.get(i)
						.toString()
						+ this.tableTextAttr.getEndTag() + "</TH>");
			}
			sbHeaderString.append("</TR></TABLE>" + this.layout.getEndTag());
			final Element footerPageElement = reportFrame.getCurrentPage().getHTMLDocument().getElement(ReportElement.PIEID);
			reportFrame.getCurrentPage()
			.getHTMLDocument()
			.insertBeforeStart(footerPageElement, sbHeaderString.toString());
			if (ApplicationManager.DEBUG) {
				TableReportElement.logger.debug("Time to insert the table header: " + (System.currentTimeMillis() - t));
			}
			final Object oFirstColumData = tableValues.get(vColumNames.get(0));
			long tInsertRowsTotal = 0;
			long tCheckPageTotal = 0;
			long tDocumentInsertTotal = 0;
			for (int i = 0; i < ((List) oFirstColumData).size(); i = i + rowsCount) {
				iInsertedRowsNumber = iInsertedRowsNumber + rowsCount;
				if ((((List) oFirstColumData).size() - i) < rowsCount) {
					rowsCount = 1;
				}
				final long t2 = System.currentTimeMillis();
				final StringBuilder sbRowString = new StringBuilder();
				// Builds an HTML for the rows
				for (int k = 0; k < rowsCount; k++) {
					sbRowString.append("<TR>");
					for (int j = 0; j < vColumNames.size(); j++) {
						Object oData = ((List) tableValues.get(vColumNames.get(j))).get(i + k);
						if (oData == null) {
							oData = "";
						}
						sbRowString.append("<TD   style='border-style: solid;border-color:black'>");
						sbRowString.append(this.tableTextAttr.getStartTag());
						sbRowString.append(oData.toString());
						sbRowString.append(this.tableTextAttr.getEndTag());
						sbRowString.append("</TD>");
					}
					sbRowString.append("</TR>");
				}
				// Inserts and checks
				// Save the index
				// Search if a page footer exists
				Element tableElement = null;
				// Then the table has a name
				tableElement = reportFrame.getCurrentPage().getElementById(this.identificator).getElement(0);
				final int iEndTableOffset = tableElement.getEndOffset();
				final long t6 = System.currentTimeMillis();
				reportFrame.getCurrentPage().getHTMLDocument().insertBeforeEnd(tableElement, sbRowString.toString());
				tDocumentInsertTotal += System.currentTimeMillis() - t6;
				tInsertRowsTotal += System.currentTimeMillis() - t2;
				final long t3 = System.currentTimeMillis();
				final boolean bFullPage = reportFrame.getCurrentPage().isFull();
				tCheckPageTotal += System.currentTimeMillis() - t3;
				if (ApplicationManager.DEBUG) {
					TableReportElement.logger.debug("Inserted " + rowsCount + " rows");
				}
				if (!bFullPage) {
					// If the page is not full and nRows is greater than 5,
					// then it is an estimation based on a previous page,
					// probably one more row can be added
					if (rowsCount > 5) {
						// Increment i, because from i to i+nRows are inserted.
						i = (i + iInsertedRowsNumber) - 1;
						rowsCount = 1;
					}
					// If nRows is not greater than 5, then nRows does not
					// change
				} else {
					final long t4 = System.currentTimeMillis();
					if (rowsCount > 1) {
						// Delete nRows inserted rows and decrement i in nRows
						// and then insert rows one by one
						final Element firstOfTheLastRows = tableElement
								.getElement(tableElement.getElementCount() - rowsCount);
						final Element lastRowElement = tableElement.getElement(tableElement.getElementCount() - 1);
						final int offset = firstOfTheLastRows.getStartOffset();
						final int finalOffset = lastRowElement.getEndOffset();
						reportFrame.getCurrentPage().getHTMLDocument().remove(offset, finalOffset - offset);
						// Decrements the number of inserted rows
						iInsertedRowsNumber = iInsertedRowsNumber - rowsCount;
						rowsCount = 1;
						i = i - rowsCount;
						continue;
					}

					final Element lastRowElement = tableElement.getElement(tableElement.getElementCount() - 1);
					int offset = lastRowElement.getStartOffset();
					int finalOffset = lastRowElement.getEndOffset();
					final int newEndTableOffset = tableElement.getEndOffset();
					reportFrame.getCurrentPage()
					.getHTMLDocument()
					.remove(iEndTableOffset - 1, newEndTableOffset - iEndTableOffset);
					// Delete another row
					final Element lastButOneRow = tableElement.getElement(tableElement.getElementCount() - 1);
					offset = lastButOneRow.getStartOffset();
					finalOffset = lastButOneRow.getEndOffset();
					final int newEndTableOffset2 = tableElement.getEndOffset();
					reportFrame.getCurrentPage().getHTMLDocument().remove(offset, newEndTableOffset2 - offset);
					final StringBuilder sbSumText = new StringBuilder();
					if (this.sumColumn) {
						for (int n = 0; n < this.sumColumns.length; n++) {
							double total = this.columnAccumulatedTotal[n];
							final List vColumnData = (List) tableValues.get(this.sumColumns[n]);
							if (vColumnData != null) {
								for (int m = 0; m < (i - 1); m++) {
									final Object oColumnValue = vColumnData.get(m);
									if (oColumnValue != null) {
										try {
											final Number num = NumberFormat.getNumberInstance()
													.parse(oColumnValue.toString());
											final double dNumber = num.doubleValue();
											total = total + dNumber;
										} catch (final Exception e) {
											TableReportElement.logger.error(null, e);
										}
									}
								}
								this.columnAccumulatedTotal[n] = total;
							}
							sbSumText.append(this.sumColumns[n] + ": " + Double.toString(total) + ";");
						}
						final TextAttributes tableAttributes = new TextAttributes(TextAttributes.ARIAL, this.fontSize,
								TextAttributes.BOLD_ITALICS);
						reportFrame.getCurrentPage()
						.getHTMLDocument()
						.insertBeforeEnd(tableElement,
								"<TR><TD  colspan='" + Integer.toString(this.columnNumber)
								+ "'style='border-style: solid;border-color:blue'>" + tableAttributes
								.getStartTag()
								+ sbSumText.toString() + tableAttributes.getEndTag() + "</TD></TR>");
					}

					reportFrame.addPage(null);
					// inserts the basic tags to continue with the table
					final Map hOtherData = new Hashtable();
					for (int k = 0; k < vColumNames.size(); k++) {
						final List vRemainingData = new Vector();
						for (int l = i - 1; l < ((List) oFirstColumData).size(); l++) {
							vRemainingData.add(((List) tableValues.get(vColumNames.get(k))).get(l));
						}
						hOtherData.put(vColumNames.get(k), vRemainingData);
					}
					// New data
					this.data = hOtherData;
					if (ApplicationManager.DEBUG) {
						TableReportElement.logger.debug("Time to insert rows: " + tInsertRowsTotal);
					}
					if (ApplicationManager.DEBUG) {
						TableReportElement.logger.debug("Tiempo to insert in the document: " + tDocumentInsertTotal);
					}
					if (ApplicationManager.DEBUG) {
						TableReportElement.logger.debug("Tiempo checking page: " + tCheckPageTotal);
					}
					if (ApplicationManager.DEBUG) {
						TableReportElement.logger
						.debug("Tiempo Creating a new page: " + (System.currentTimeMillis() - t4));
					}
					this.insertTable(reportFrame, multipage, hOtherData, iInsertedRowsNumber - 1);
					break;
				}
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				TableReportElement.logger.debug(e.getMessage(), e);
			}
		}
	}

	@Override
	public void insert(final ReportFrame informe, final String identifier, final boolean multipage) {
	}

	public String getId() {
		return this.identificator;
	}

}
