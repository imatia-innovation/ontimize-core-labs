package com.ontimize.printing;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to parse HTML code. This class can use the html string or the path of a html file.<br>
 * It allows to replace some keys in the text with other string values. It does not allow page break
 */
public class HTMLProcessor {

	private static final Logger logger = LoggerFactory.getLogger(HTMLProcessor.class);

	public static final boolean DEBUG = true;

	public static final String TABLE_TAG = "<TABLE width='100%' BORDER='1' CELLPADDING='0' CELLSPACING='0' style='border-top-width: thin; border-left-width: thin; border-bottom-width: thin; border-right-width:thin;border-style:solid;border-color:white'>";

	public static final String ROW_TAG = "<TR>";

	public static final String CELL_HEAD_TAG = "<TH   style='border-style: solid;border-color:black'>";

	public static final String CELL_TAG = "<TD   style='border-style: solid;border-color:black'>";

	public static final String END_ROW_TAG = "</TR>";

	public static final String END_CELL_HEAD_TAG = "</TH>";

	public static final String END_CELL_TAG = "</TD>";

	public static final String END_TABLE_TAG = "</TABLE>";

	protected String code = null;

	protected HTMLDocument document = null;

	protected HTMLEditorKit editor = null;

	protected JTextPane textPane = null;

	protected String headId = null;

	protected String footId = null;

	/**
	 * Creates a processor class using the html code
	 * @param htmlCode
	 * @param base
	 * @throws Exception
	 */
	public HTMLProcessor(final String htmlCode, final URL base) throws Exception {
		if (htmlCode == null) {
			throw new IllegalArgumentException("Html code can not be null");
		}
		// If code is not null
		this.code = htmlCode;
		this.editor = new HTMLEditorKit();
		this.document = (HTMLDocument) this.editor.createDefaultDocument();
		this.document.setBase(base);
		try {
			final StringReader sr = new StringReader(this.code);
			this.editor.read(sr, this.document, 0);
			sr.close();
			this.getJTextPane();
		} catch (final Exception e) {
			HTMLProcessor.logger.trace(null, e);
			this.editor = null;
			this.document = null;
			throw e;
		}
	}

	/**
	 * Builds a new processor using the file with the specified URL
	 * @param fileURL
	 * @param base
	 * @throws Exception
	 */
	public HTMLProcessor(final URL fileURL, final URL base) throws Exception {
		if (fileURL == null) {
			throw new IllegalArgumentException("The URL of the file can not be null");
		}
		this.editor = new HTMLEditorKit();
		this.document = (HTMLDocument) this.editor.createDefaultDocument();
		this.document.setBase(base);
		if (HTMLProcessor.DEBUG) {
			HTMLProcessor.logger.debug(this.document.getBase().toString());
		}
		try {
			this.editor.read(fileURL.openStream(), this.document, 0);
			final StringWriter writer = new StringWriter();
			this.editor.write(writer, this.document, 0, this.document.getLength());
			writer.flush();
			writer.close();
			this.code = writer.toString();
			this.getJTextPane();
		} catch (final Exception e) {
			HTMLProcessor.logger.trace(null, e);
			this.editor = null;
			this.document = null;
			throw e;
		}
	}

	/**
	 * Builds a new processor using the html file with the specified URL
	 * @param fileURL
	 * @throws Exception
	 */
	public HTMLProcessor(final URL fileURL) throws Exception {
		if (fileURL == null) {
			throw new IllegalArgumentException("URL can not be null");
		}
		this.editor = new HTMLEditorKit();
		this.document = (HTMLDocument) this.editor.createDefaultDocument();
		this.document.setBase(fileURL);
		try {
			this.editor.read(fileURL.openStream(), this.document, 0);
			final StringWriter writer = new StringWriter();
			this.editor.write(writer, this.document, 0, this.document.getLength());
			writer.flush();
			writer.close();
			this.code = writer.toString();
			this.getJTextPane();
		} catch (final Exception e) {
			HTMLProcessor.logger.trace(null, e);
			this.editor = null;
			this.document = null;
			throw e;
		}
	}

	/**
	 * Replaces the <code>text</code> in the string HTML with the <code>newText</code><br>
	 * This replaces the code in the same html code and this method must be used carefully.
	 * @param text Text to replace
	 * @param newText New text to set
	 */
	public void replaceTextInHTML(final String text, final String newText) {
		// To replace the text in the HTML we use an EditorKit.
		// Get the editor content and replace the text in it
		try {
			final StringWriter writer = new StringWriter();
			this.editor.write(writer, this.document, 0, this.document.getLength());
			writer.flush();
			writer.close();
			final String sHTMLContent = writer.toString();
			// Search and replace
			final int iTextLenght = text.length();
			final StringBuilder sbResult = new StringBuilder(sHTMLContent.length());
			for (int i = 0; i < (sHTMLContent.length() - iTextLenght); i++) {
				// Additional checking. Previous characters can not be letters
				// to consider the text a complete word
				if (sHTMLContent.regionMatches(i, text, 0,
						iTextLenght) && !Character.isLetterOrDigit(sHTMLContent.charAt(i - 1))
						&& !Character.isLetterOrDigit(sHTMLContent.charAt(i + iTextLenght + 1))) {
					sbResult.append(newText);
					if (HTMLProcessor.DEBUG) {
						HTMLProcessor.logger.debug("Replaced " + text + " in possition " + Integer.toString(i));
					}
					// Update the index i
					i = (i + iTextLenght) - 1;
					// Continue with the loop to allow more than one replace
				} else { // If it is different then adds the character
					sbResult.append(sHTMLContent.charAt(i));
				}
			}
			// Now add characters from sHTMLContent.length()- sTextLenght, to
			// the end
			for (int i = sHTMLContent.length() - iTextLenght; i < sHTMLContent.length(); i++) {
				sbResult.append(sHTMLContent.charAt(i));
			}
			// Set the content of the EditorKit
			this.document.remove(0, this.document.getLength());
			final StringReader sr = new StringReader(sbResult.toString());
			this.editor.read(sr, this.document, 0);
			sr.close();
		} catch (final Exception e) {
			if (HTMLProcessor.DEBUG) {
				HTMLProcessor.logger.debug("Error inserting in HTML", e);
			} else {
				HTMLProcessor.logger.trace("Error inserting in HTML", e);
			}
		}
	}

	/**
	 * Replace the <code>text</code> in the html code with the image represented by the bytes array.<br>
	 * If <code>width</code> or <code>height</code> are 0 then the image size will be the real<br>
	 * With values different from 0 this method scales the image. The method replaces te values in the
	 * html code and must be used carefully
	 * @param text
	 * @param imageBytes
	 * @param width
	 * @param height
	 */
	public void replaceTextInHTML(final String text, final byte[] imageBytes, final int width, final int height) {
		// Use the EditorKit to replace values in the HTML. Get the Editor
		// content and replace the key word.
		try {
			final StringWriter writer = new StringWriter();
			this.editor.write(writer, this.document, 0, this.document.getLength());
			writer.flush();
			writer.close();
			final String sHTMLContent = writer.toString();
			// Search and replace
			final int iTextLength = text.length();
			final StringBuilder sbResult = new StringBuilder(sHTMLContent.length());
			for (int i = 0; i < (sHTMLContent.length() - iTextLength); i++) {
				if (sHTMLContent.regionMatches(i, text, 0,
						iTextLength) && !Character.isLetterOrDigit(sHTMLContent.charAt(i - 1))
						&& !Character.isLetterOrDigit(sHTMLContent.charAt(i + iTextLength + 1))) {
					// Select the text and replace with the image
					// Whe need to create an URL
					FileOutputStream outputStream = null;
					File temporal = null;
					try {
						temporal = new File("imag_0001.gif");
						outputStream = new FileOutputStream(temporal);
						outputStream.write(imageBytes);
						outputStream.flush();
					} catch (final Exception e) {
						HTMLProcessor.logger.trace(null, e);
					} finally {
						outputStream.close();
					}
					final String sNewText = "<IMG src='" + temporal.toURL() + "' height='" + Integer.toString(height)
					+ "' width='" + Integer.toString(width) + "'></IMG>";
					// Delete the temporal image file from the disk
					temporal.deleteOnExit();
					sbResult.append(sNewText);
					if (HTMLProcessor.DEBUG) {
						HTMLProcessor.logger.debug("Replaced " + text + " in possition " + Integer.toString(i));
					}
					// Update the index i
					i = (i + iTextLength) - 1;
					// Continue with the loop to allow replace more than one
					// item
				} else { // Adds the character
					sbResult.append(sHTMLContent.charAt(i));
				}
			}
			// Now add from sHTMLContent.length()- iTextLenght to the end
			for (int i = sHTMLContent.length() - iTextLength; i < sHTMLContent.length(); i++) {
				sbResult.append(sHTMLContent.charAt(i));
			}
			// Set the EditorKit content
			this.document.remove(0, this.document.getLength());
			final StringReader sr = new StringReader(sbResult.toString());
			this.editor.read(sr, this.document, 0);
			sr.close();
		} catch (final Exception e) {
			if (HTMLProcessor.DEBUG) {
				HTMLProcessor.logger.error("Error inserting image in HTML", e);
			} else {
				HTMLProcessor.logger.trace("Error inserting image in HTML", e);
			}
		}
	}

	/**
	 * Get the EditorKit used for this processor to can use it in a text panel
	 * @return
	 */
	public HTMLEditorKit getEditorKit() {
		return this.editor;
	}

	/**
	 * Get the document used for this processor to can use it in a text panel
	 * @return
	 */
	public HTMLDocument getDocument() {
		return this.document;
	}

	public String getHTML() {
		try {
			final StringWriter writer = new StringWriter();
			this.editor.write(writer, this.document, 0, this.document.getLength());
			writer.flush();
			writer.close();
			return writer.toString();
		} catch (final Exception e) {
			if (HTMLProcessor.DEBUG) {
				HTMLProcessor.logger.debug("Error in getHTML(): ", e);
			} else {
				HTMLProcessor.logger.trace("Error in getHTML(): ", e);
			}
			return null;
		}
	}

	/**
	 * Get a JTextPanel with the current document text The processor save a reference to the returned
	 * panel
	 * @return
	 */
	public JTextPane getJTextPane() {
		if (this.textPane == null) {
			this.textPane = new JTextPane();
			this.textPane.setEditorKit(this.editor);
			this.textPane.setDocument(this.document);
			return this.textPane;
		} else {
			this.textPane.setEditorKit(this.editor);
			this.textPane.setDocument(this.document);
			return this.textPane;
		}
	}

	/**
	 * Update the values of the JTextPane with the last changes in the HTML
	 */
	public void updateTextPane() {
		if (this.textPane != null) {
			this.textPane.setEditorKit(this.editor);
			this.textPane.setDocument(this.document);
		}
	}

	/**
	 * Undo all the changes in the html code and set the original value again
	 * @throws Exception
	 */
	public void reset() throws Exception {
		try {
			this.document.remove(0, this.document.getLength());
			final StringReader sr = new StringReader(this.code);
			this.editor.read(sr, this.document, 0);
			sr.close();
		} catch (final Exception e) {
			HTMLProcessor.logger.trace(null, e);
			this.editor = null;
			this.document = null;
			throw e;
		}
		this.updateTextPane();
	}

	/**
	 * Insert a table using its values, replacing in the html code the key word <code>text</code> with
	 * the table HTML equivalence.<br>
	 * @param text
	 * @param tableValues The keys of this Map are the names of the table columns and the values
	 *        are vectors whit the column values. If some of these values are hashtables then they are
	 *        new tables in the appropriate cell.
	 * @param sortColumns This List contains the name of the table columns in the order to put them in
	 *        the table. These columns must be the keys of the <code>tableValues</code> hashtable
	 * @return
	 */
	public String insertTable(final String text, final Map tableValues, final List sortColumns) {
		// Search the maximum length in the vectors with the table values
		int maximumValuesNumber = 0;

		for (int i = 0; i < sortColumns.size(); i++) {
			final Object oColumValues = tableValues.get(sortColumns.get(i));
			if (oColumValues == null) {
				continue;
			}
			if (oColumValues instanceof List) {
				final List vColumValues = (List) oColumValues;
				maximumValuesNumber = Math.max(maximumValuesNumber, vColumValues.size());
			} else {
				HTMLProcessor.logger.debug("Error in the table data. Map must contain vectors");
				return "";
			}
		}
		final StringBuilder sbHTMLTableCode = new StringBuilder();
		sbHTMLTableCode.append(HTMLProcessor.TABLE_TAG);
		// Now we know the number of elements in the table. This is the number
		// of rows
		// For each column get the name
		final StringBuilder sbTableHeader = new StringBuilder(HTMLProcessor.ROW_TAG);
		for (int j = 0; j < sortColumns.size(); j++) {
			if (tableValues.containsKey(sortColumns.get(j))) {
				sbTableHeader.append(HTMLProcessor.CELL_HEAD_TAG + "<FONT size='1'>" + sortColumns.get(j) + "</Font>"
						+ HTMLProcessor.END_CELL_HEAD_TAG);
			}
		}
		sbTableHeader.append(HTMLProcessor.END_ROW_TAG);
		sbHTMLTableCode.append(sbTableHeader.toString());
		for (int i = 0; i < maximumValuesNumber; i++) {
			final StringBuilder sbRowString = new StringBuilder(HTMLProcessor.ROW_TAG);
			for (int j = 0; j < sortColumns.size(); j++) {
				final Object vector = tableValues.get(sortColumns.get(j));
				if (vector == null) {
					continue;
				}
				Object oValue = ((List) vector).get(i);
				if (oValue == null) {
					oValue = "";
				} else {
					if (oValue instanceof Map) {
						// This is when a new table exist, then process the new
						// hashtable
						sbRowString.append(HTMLProcessor.CELL_TAG
								+ this.insertTable(null, (Map) oValue, sortColumns) + HTMLProcessor.END_CELL_TAG);
					} else {
						sbRowString.append(HTMLProcessor.CELL_TAG + "<FONT size='1'>" + oValue.toString() + "</Font>"
								+ HTMLProcessor.END_CELL_TAG);
					}
				}
			}
			sbRowString.append(HTMLProcessor.END_ROW_TAG);
			sbHTMLTableCode.append(sbRowString.toString());
		}
		// Now all the closing tags
		sbHTMLTableCode.append(HTMLProcessor.END_TABLE_TAG);
		if (HTMLProcessor.DEBUG) {
			HTMLProcessor.logger.debug(sbHTMLTableCode.toString());
		}
		if (text != null) {
			this.replaceTextInHTML(text, sbHTMLTableCode.toString());
		} else {
			HTMLProcessor.logger.debug("The key of the text to replace is NULL");
		}
		return sbHTMLTableCode.toString();
	}

	public String insertTable(final String text, final Map tableValues) {
		final List vSortColumns = new Vector();
		final Enumeration enumDataKeys = Collections.enumeration(tableValues.keySet());
		while (enumDataKeys.hasMoreElements()) {
			vSortColumns.add(enumDataKeys.nextElement());
		}
		// Get the maximum number of elements in the vectors
		int maximumNumberOfValues = 0;

		for (int i = 0; i < vSortColumns.size(); i++) {
			final Object oColumnValues = tableValues.get(vSortColumns.get(i));
			if (oColumnValues == null) {
				continue;
			}
			if (oColumnValues instanceof List) {
				final List vColumnValues = (List) oColumnValues;
				maximumNumberOfValues = Math.max(maximumNumberOfValues, vColumnValues.size());
			} else {
				HTMLProcessor.logger.debug("Error in table data. Table values must be a Map with vectors");
				return "";
			}
		}
		final StringBuilder sbHTMLTableCode = new StringBuilder();
		sbHTMLTableCode.append(HTMLProcessor.TABLE_TAG);
		// The number of elements in the vectors is the number of rows
		// Get the name of the columns
		final StringBuilder sbTableHeader = new StringBuilder(HTMLProcessor.ROW_TAG);
		for (int j = 0; j < vSortColumns.size(); j++) {
			if (tableValues.containsKey(vSortColumns.get(j))) {
				sbTableHeader.append(HTMLProcessor.CELL_HEAD_TAG + "<FONT size='1'>" + vSortColumns.get(j) + "</Font>"
						+ HTMLProcessor.END_CELL_HEAD_TAG);
			}
		}
		sbTableHeader.append(HTMLProcessor.END_ROW_TAG);
		sbHTMLTableCode.append(sbTableHeader.toString());
		for (int i = 0; i < maximumNumberOfValues; i++) {
			final StringBuilder sbRowString = new StringBuilder(HTMLProcessor.ROW_TAG);
			for (int j = 0; j < vSortColumns.size(); j++) {
				final Object vector = tableValues.get(vSortColumns.get(j));
				if (vector == null) {
					continue;
				}
				Object oValue = ((List) vector).get(i);
				if (oValue == null) {
					oValue = "";
				} else {
					if (oValue instanceof Map) {
						// If value is a Map then a new table exist in the
						// cell
						sbRowString.append(HTMLProcessor.CELL_TAG + this.insertTable(null, (Map) oValue)
						+ HTMLProcessor.END_CELL_TAG);
					} else {
						sbRowString.append(HTMLProcessor.CELL_TAG + "<FONT size='1'>" + oValue.toString() + "</Font>"
								+ HTMLProcessor.END_CELL_TAG);
					}
				}
			}
			sbRowString.append(HTMLProcessor.END_ROW_TAG);
			sbHTMLTableCode.append(sbRowString.toString());
		}
		// Now the closing tags
		sbHTMLTableCode.append(HTMLProcessor.END_TABLE_TAG);
		if (HTMLProcessor.DEBUG) {
			HTMLProcessor.logger.debug(sbHTMLTableCode.toString());
		}
		if (text != null) {
			this.replaceTextInHTML(text, sbHTMLTableCode.toString());
		} else {
			HTMLProcessor.logger.debug("Key word to replace is NULL");
		}
		return sbHTMLTableCode.toString();
	}

	/**
	 * Gets a List of JTextPane with the document separated by pages with the specified page
	 * format.<br>
	 * @param pageFormat
	 * @return
	 */
	public List toPages(final PageFormat pageFormat) {
		// Break the document, it can fill more than one page.
		// 'textpane' does not allow break it in different pages to print. We
		// have
		// to create different documents. Process the HTML code and put it in a
		// JTextPane with the appropriate size until fill it. Then break the
		// html
		// code with care to maintain the coherence

		// To simplify:
		// - Break the tables after a row (after tag </TR>)
		// - In normal text break
		final List vPages = new Vector();
		final JDialog dWindow = new JDialog();
		final CardLayout lay = new CardLayout();
		dWindow.getContentPane().setLayout(lay);
		dWindow.setVisible(true);
		int index = 0;
		try {
			final StringWriter writer = new StringWriter();
			this.editor.write(writer, this.document, 0, this.document.getLength());
			writer.flush();
			writer.close();
			final String sHTMLContent = writer.toString();
			while (index < sHTMLContent.length()) {
				final JTextPane textPaneAux = new JTextPane();
				textPaneAux.setSize((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
				dWindow.getContentPane().add(textPaneAux, Integer.toString(vPages.size()));
				dWindow.setSize((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
				textPaneAux.paintImmediately(0, 0, (int) pageFormat.getImageableWidth(),
						(int) pageFormat.getImageableHeight());
				final HTMLEditorKit editorAux = new HTMLEditorKit();
				final HTMLDocument docAux = (HTMLDocument) editorAux.createDefaultDocument();
				textPaneAux.setEditorKit(editorAux);
				textPaneAux.setDocument(docAux);
				// Read the first character
				final StringReader srReader = new StringReader(sHTMLContent.substring(index, index + 1));
				editorAux.read(srReader, docAux, index);
				srReader.close();
				index++;
				final int endDocumentOffset = docAux.getLength() - 1;
				while (this.textPane.modelToView(endDocumentOffset).y < (int) pageFormat.getImageableHeight()) {
					// Insert the HTML code to fill the page
					final StringReader auxSReader = new StringReader(sHTMLContent.substring(index, index + 1));
					editorAux.read(auxSReader, docAux, index);
					auxSReader.close();
					index++;
				}
				vPages.add(textPaneAux);
			}
			return vPages;
		} catch (final Exception e) {
			HTMLProcessor.logger.error(null, e);
			return vPages;
		}
	}

	public void print(final PageFormat pf, final int height) {
		final Thread threadImpresion = new Thread() {

			@Override
			public void run() {
				final double dPageCount = height / pf.getImageableHeight();
				// Round the number of pages to the next value
				int iPageNumber = (int) dPageCount;
				if (iPageNumber < dPageCount) {
					iPageNumber++;
				}
				final int iPageCount = iPageNumber;
				// Paint the textPane in the printing Graphics
				final PrinterJob pj = PrinterJob.getPrinterJob();
				pj.setPrintable(new Printable() {

					@Override
					public int print(final Graphics g, final PageFormat pf, final int iPageIndex) {
						if (iPageIndex < iPageCount) {
							// If the width to paint is greater than the page
							// width then scale
							final Graphics2D g2 = (Graphics2D) g;
							g2.translate(pf.getImageableX(), pf.getImageableY());

							g2.translate(0f, -((iPageIndex * pf.getImageableHeight()) - 15));
							HTMLProcessor.this.textPane.paint(g2);
							g2.drawString(Integer.toString(iPageIndex + 1), (int) pf.getImageableWidth() - 15,
									(int) pf.getImageableHeight() - 15);
							// Undo the translation, because it must be from the
							// beginning
							return Printable.PAGE_EXISTS;
						} else {
							return Printable.NO_SUCH_PAGE;
						}
					}
				});
				if (pj.printDialog()) {
					try {
						pj.print();
					} catch (final Exception e) {
						HTMLProcessor.logger.error(null, e);
					}
				}
			}
		};
		threadImpresion.start();
	}

	/**
	 * Fixes the element with attribute <code>id</code> as header in the template for all the pages. If
	 * the element has children the children will be in the header too.
	 * @param id
	 */
	public void setHead(final String id) {
		this.headId = id;
	}

	/**
	 * Fixes the element with attribute <code>id</code> as footer in the template for all the pages.If
	 * the element has children the children will be in the footer too.
	 * @param id
	 */
	public void setFoot(final String id) {
		this.footId = id;
	}

	/**
	 * @param pf Page format to use in the printing method
	 * @param height Height in pixels from the text pane of this processor
	 */
	public void advancedPrint(final PageFormat pf, final int height) {
		final Thread threadImpresion = new Thread() {

			@Override
			public void run() {
				// To support header and footer in the pages
				// 1.- Check if a header exists
				// 2.- Calculates the number of pixels needed for the header
				// 3.- Sets the header element in all the pages
				Element headerElement = null;
				int iInitHeaderOffset = 0;
				int iFinalHeaderOffset = 0;
				final Element footElement = null;
				int iInitFottOffset = 0;
				int iFinalFootOffset = 0;
				int iHeaderInitPosition = 0;
				int iHeaderFinalPosition = 0;
				int iFootInitPosition = 0;
				int iFootFinalPosition = 0;

				if (HTMLProcessor.this.headId != null) {
					headerElement = HTMLProcessor.this.document.getElement(HTMLProcessor.this.headId);
				}
				if (HTMLProcessor.this.footId != null) {
					headerElement = HTMLProcessor.this.document.getElement(HTMLProcessor.this.footId);
				}

				// Pixels to the header
				int iHeaderHeight = 0;
				if (headerElement != null) {
					iInitHeaderOffset = headerElement.getStartOffset();
					iFinalHeaderOffset = headerElement.getEndOffset();
					try {
						iHeaderInitPosition = HTMLProcessor.this.textPane.modelToView(iInitHeaderOffset).y;
						iHeaderFinalPosition = HTMLProcessor.this.textPane.modelToView(iFinalHeaderOffset).y;
						iHeaderHeight = iHeaderFinalPosition - iHeaderInitPosition;
					} catch (final Exception e) {
						if (HTMLProcessor.DEBUG) {
							HTMLProcessor.logger.error(null, e);
						} else {
							HTMLProcessor.logger.trace(null, e);
						}
						iHeaderHeight = 0;
					}
				} else {
					if (HTMLProcessor.this.headId != null) {
						HTMLProcessor.logger.debug("Header element id=" + HTMLProcessor.this.headId + " not found.");
					}
				}

				// Pixels to the foot
				int iFootHeight = 0;
				if (footElement != null) {
					iInitFottOffset = footElement.getStartOffset();
					iFinalFootOffset = footElement.getEndOffset();
					try {
						iFootInitPosition = HTMLProcessor.this.textPane.modelToView(iInitFottOffset).y;
						iFootFinalPosition = HTMLProcessor.this.textPane.modelToView(iFinalFootOffset).y;
						iFootHeight = iFootFinalPosition - iFootInitPosition;
					} catch (final Exception e) {
						if (HTMLProcessor.DEBUG) {
							HTMLProcessor.logger.error(null, e);
						} else {
							HTMLProcessor.logger.trace(null, e);
						}
						iFootHeight = 0;
					}
				} else {
					if (HTMLProcessor.this.footId != null) {
						HTMLProcessor.logger.debug("Footer element id=" + HTMLProcessor.this.footId + " not found.");
					}
				}

				// Calculates the number of pages
				// In each page insert the header and the foot
				// The textPane contains a free area to paint in to share
				// between the pages
				// In each page there is a free space like
				// pf.getImageableHeight() - (header height + footer height)
				final double dPageNumber = height / (pf.getImageableHeight() - (iHeaderHeight + iFootHeight));
				// Rounds the number of pages to the next value
				int iPageNumber = (int) dPageNumber;
				if (iPageNumber < dPageNumber) {
					iPageNumber++;
				}
				final int iNumberOfPages = iPageNumber;
				// Paints the textPane in the printing Graphics
				final PrinterJob pj = PrinterJob.getPrinterJob();
				pj.setPrintable(new Printable() {

					@Override
					public int print(final Graphics g, final PageFormat pf, final int pageIndex) {
						if (pageIndex < iNumberOfPages) {
							// If the width to paint is greater than the page
							// then scale
							final Graphics2D g2 = (Graphics2D) g;
							g2.translate(pf.getImageableX(), pf.getImageableY());
							g2.translate(0f, -(pageIndex * pf.getImageableHeight()));
							HTMLProcessor.this.textPane.paint(g2);
							g2.drawString(Integer.toString(pageIndex + 1), (int) pf.getImageableWidth(),
									(int) pf.getImageableHeight());
							// Undo the translation
							return Printable.PAGE_EXISTS;
						} else {
							return Printable.NO_SUCH_PAGE;
						}
					}
				});
				if (pj.printDialog()) {
					try {
						pj.print();
					} catch (final Exception e) {
						HTMLProcessor.logger.error(null, e);
					}
				}
			}
		};
		threadImpresion.start();
	}

	public Printable getPrintable(final PageFormat pf, final int height) {
		// 1.- Check if a header exists
		// 2.- Calculates the space needed to paint the header
		// 3.- Put the header element in all the pages
		Element elementHeader = null;
		int iInitHeaderOffset = 0;
		int iFinalHeaderOffset = 0;
		final Element footElement = null;
		int iInitFootOffset = 0;
		int iFinalFootOffset = 0;
		int iInitHeaderPosition = 0;
		int iFinalHeaderPosition = 0;
		int iInitFootPosition = 0;
		int iFinalFootPosition = 0;

		if (this.headId != null) {
			elementHeader = this.document.getElement(this.headId);
		}
		if (this.footId != null) {
			elementHeader = this.document.getElement(this.footId);
		}

		// Pixels to paint the header
		int iHeaderHeight = 0;
		if (elementHeader != null) {
			iInitHeaderOffset = elementHeader.getStartOffset();
			iFinalHeaderOffset = elementHeader.getEndOffset();
			try {
				iInitHeaderPosition = this.textPane.modelToView(iInitHeaderOffset).y;
				iFinalHeaderPosition = this.textPane.modelToView(iFinalHeaderOffset).y;
				iHeaderHeight = iFinalHeaderPosition - iInitHeaderPosition;
			} catch (final Exception e) {
				if (HTMLProcessor.DEBUG) {
					HTMLProcessor.logger.error(null, e);
				} else {
					HTMLProcessor.logger.trace(null, e);
				}
				iHeaderHeight = 0;
			}
		} else {
			if (this.headId != null) {
				HTMLProcessor.logger.debug("Header element id=" + this.headId + " not found.");
			}
		}

		// Free space in pixels
		int iFootHeight = 0;
		if (footElement != null) {
			iInitFootOffset = footElement.getStartOffset();
			iFinalFootOffset = footElement.getEndOffset();
			try {
				iInitFootPosition = this.textPane.modelToView(iInitFootOffset).y;
				iFinalFootPosition = this.textPane.modelToView(iFinalFootOffset).y;
				iFootHeight = iFinalFootPosition - iInitFootPosition;
			} catch (final Exception e) {
				if (HTMLProcessor.DEBUG) {
					HTMLProcessor.logger.error(null, e);
				} else {
					HTMLProcessor.logger.trace(null, e);
				}
				iFootHeight = 0;
			}
		} else {
			if (this.footId != null) {
				HTMLProcessor.logger.debug("Footer element id=" + this.footId + " not found.");
			}
		}

		// Calculates the number of pages
		// Insert the footer and the header in each page
		final double dPageNumber = height / (pf.getImageableHeight() - (iHeaderHeight + iFootHeight));
		// Round the number of pages to the next value
		int iPageNumber = (int) dPageNumber;
		if (iPageNumber < dPageNumber) {
			iPageNumber++;
		}
		final int iNumberOfPages = iPageNumber;
		// Paints the textPane in the printing Graphics
		final PrinterJob pj = PrinterJob.getPrinterJob();
		final int fIHeaderHeight = iHeaderHeight;
		final int fIFootHeight = iFootHeight;
		return new Printable() {

			@Override
			public int print(final Graphics g, final PageFormat pf, final int pageIndex) {
				if (pageIndex < iNumberOfPages) {
					final Graphics2D g2 = (Graphics2D) g;
					g2.translate(pf.getImageableX(), pf.getImageableY());
					g2.translate(0f, -(pageIndex * pf.getImageableHeight()));
					g2.setClip(0, (int) (pageIndex * pf.getImageableHeight()), (int) pf.getImageableWidth(),
							(int) (pageIndex * pf.getImageableHeight()) + fIHeaderHeight);
					HTMLProcessor.this.textPane.paint(g2);
					if (HTMLProcessor.DEBUG) {
						HTMLProcessor.logger
						.debug("Header height = " + fIHeaderHeight + " , Footer height = " + fIFootHeight);
					}
					g2.drawString(Integer.toString(pageIndex + 1), (int) pf.getImageableWidth() - 12,
							(int) pf.getImageableHeight() - 12);
					return Printable.PAGE_EXISTS;
				} else {
					return Printable.NO_SUCH_PAGE;
				}
			}
		};
	}

}
