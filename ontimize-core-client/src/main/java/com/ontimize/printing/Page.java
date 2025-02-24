package com.ontimize.printing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.StringReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Page extends JTextPane implements Printable {

	private static final Logger logger = LoggerFactory.getLogger(Page.class);

	private static boolean DEBUG = false;

	protected Map elementList = new Hashtable();

	protected PageFormat pageFormat = null;

	protected int pageNumber = 0;

	protected boolean fitToPage = true;

	protected HTMLEditorKit htmlEditor = null;

	protected HTMLDocument htmlDocument = null;

	protected PageHeader header = null;

	protected PageFooter footer = null;

	/**
	 * Creates a new page with the specified format and without template
	 * @param pageFormat
	 * @param pageHeader
	 * @param pageFooter
	 */
	public Page(final PageFormat pageFormat, final PageHeader pageHeader, final PageFooter pageFooter) {
		this.setEditable(false);
		this.htmlEditor = new HTMLEditorKit();
		this.header = pageHeader;
		this.footer = pageFooter;
		this.htmlDocument = (HTMLDocument) this.htmlEditor.createDefaultDocument();

		if (Page.DEBUG) {
			// temporal code used to personalize the document
			final StyleSheet styles = this.htmlDocument.getStyleSheet();
			final Enumeration rules = styles.getStyleNames();
			while (rules.hasMoreElements()) {
				final String name = (String) rules.nextElement();
				final Style rule = styles.getStyle(name);
				Page.logger.debug(rule.toString());
			}
		}

		this.fitToPage = true;
		// If the page format is null then use de default system forma
		if (pageFormat == null) {
			this.pageFormat = PrinterJob.getPrinterJob().defaultPage();
		} else {
			this.pageFormat = pageFormat;
		}

		// Creates a basic document
		try {
			final StringReader lector = new StringReader(this.getBasicHTMLPage());
			this.htmlEditor.read(lector, this.htmlDocument, 0);
			this.setEditorKit(this.htmlEditor);
			this.setDocument(this.htmlDocument);
			// insert the footer and the header
			if (this.header != null) {
				this.htmlDocument.insertAfterStart(this.getElementById(ReportElement.HEADERID), this.header.toHTML());
			} else {
				this.htmlDocument.insertAfterStart(this.getElementById(ReportElement.HEADERID), "");
			}
			if (this.footer != null) {
				this.htmlDocument.insertAfterStart(this.getElementById(ReportElement.PIEID), this.footer.toHTML());
			} else {
				this.htmlDocument.insertAfterStart(this.getElementById(ReportElement.PIEID), "");
			}
		} catch (final Exception e) {
			Page.logger.error(null, e);
		}
	}

	/**
	 * Creates a new page with a template
	 * @param pageFormat
	 * @param templateURL
	 * @param pageHeader
	 * @param pageFooter
	 */
	public Page(final PageFormat pageFormat, final URL templateURL, final PageHeader pageHeader, final PageFooter pageFooter) {
		this.setEditable(false);
		this.htmlEditor = new HTMLEditorKit();
		this.header = pageHeader;
		this.footer = pageFooter;
		this.htmlDocument = (HTMLDocument) this.htmlEditor.createDefaultDocument();
		this.fitToPage = true;
		// If the page format is null then use the default format in the system
		if (pageFormat == null) {
			this.pageFormat = PrinterJob.getPrinterJob().defaultPage();
		} else {
			this.pageFormat = pageFormat;
		}
		// Read the template
		try {
			this.htmlDocument.remove(0, this.htmlDocument.getLength());
			this.htmlEditor.read(templateURL.openStream(), this.htmlDocument, 0);
			this.setEditorKit(this.htmlEditor);
			this.setDocument(this.htmlDocument);
			// Inserts the footer and the header
			if (this.header != null) {
				this.htmlDocument.insertAfterStart(this.getHTMLDocumentBody(),
						"<DIV id='" + ReportElement.HEADERID + "'>" + this.header.toHTML() + "</DIV>");
			} else {
				this.htmlDocument.insertAfterStart(this.getHTMLDocumentBody(),
						"<DIV id='" + ReportElement.HEADERID + "'>" + "" + "</DIV>");
			}
			if (this.footer != null) {
				this.htmlDocument.insertBeforeEnd(
						this.getHTMLDocumentBody().getElement(this.getHTMLDocumentBody().getElementCount() - 1),
						"<DIV id='" + ReportElement.PIEID + "'>" + this.footer.toHTML() + "</DIV>");
			} else {
				this.htmlDocument.insertBeforeEnd(
						this.getHTMLDocumentBody().getElement(this.getHTMLDocumentBody().getElementCount() - 1),
						"<DIV id='" + ReportElement.PIEID + "'>" + "" + "</DIV>");
			}
		} catch (final Exception e) {
			Page.logger.error(null, e);
		}
	}

	/**
	 * Gets an String with the basic HTML code for a page without template. Only contains a heaeer and a
	 * foot
	 * @return
	 */
	protected String getBasicHTMLPage() {
		final String basicHTMLString = "<DIV style='width:" + Integer.toString((int) this.pageFormat
				.getImageableWidth() - 10) + ";font-size:12pt'><DIV id='" + ReportElement.HEADERID + "'></DIV>"
				+ "<DIV id='" + ReportElement.PIEID + "'></DIV></DIV>";
		return basicHTMLString;
	}

	public boolean isFull() {
		try {
			// Checks the last element in the body
			final int iLastOffset = this.getDocument().getLength() - 1;
			final Rectangle rViewLimits = this.modelToView(iLastOffset);
			int coordInfY = rViewLimits.y;
			// Try with the footer
			final Element footerElement = this.htmlDocument.getElement(ReportElement.PIEID);
			if (footerElement != null) {
				try {
					coordInfY = Math.max(coordInfY, this.modelToView(footerElement.getEndOffset()).y);
				} catch (final Exception e) {
					Page.logger.trace(null, e);
				}
			}
			if (coordInfY > this.pageFormat.getImageableHeight()) {
				return true;
			} else {
				return false;
			}
		} catch (final Exception e) {
			Page.logger.error(null, e);
			return false;
		}
	}

	public HTMLEditorKit getHTMLEditor() {
		return this.htmlEditor;
	}

	public HTMLDocument getHTMLDocument() {
		return this.htmlDocument;
	}

	public Element getElementById(final String id) {
		return this.htmlDocument.getElement(id);
	}

	public void printAllElements() {
		final Element raiz = this.htmlDocument.getDefaultRootElement();
		this.travelDocument(raiz);
	}

	protected void travelDocument(final Element root) {
		for (int i = 0; i < root.getElementCount(); i++) {
			final Element child = root.getElement(i);
			Page.logger.debug(child.getName());
			this.travelDocument(child);
		}
	}

	public Element getHTMLDocumentBody() {
		return this.htmlDocument.getDefaultRootElement()
				.getElement(this.htmlDocument.getDefaultRootElement().getElementCount() - 1);
	}

	public Element getPageFooter() {
		final Element pageFootElement = this.htmlDocument.getElement(ReportElement.PIEID);
		return pageFootElement;
	}

	@Override
	public int print(final Graphics g, final PageFormat pf, final int pageIndex) {
		final int maximum = this.getMaximumWidth();
		if (maximum > (pf.getImageableWidth() - 3)) {
			final Graphics2D g2 = (Graphics2D) g;
			g2.translate(pf.getImageableX(), pf.getImageableY());
			final double factorEscalaH = pf.getImageableWidth() / (maximum + 5);
			g2.scale(factorEscalaH, 1);
			this.paint(g2);
		} else {
			final Graphics2D g2 = (Graphics2D) g;
			g2.translate(pf.getImageableX(), pf.getImageableY());
			this.paint(g2);
		}
		return Printable.PAGE_EXISTS;
	}

	public int getMaximumWidth() {
		int maximum = 0;
		try {
			final String sDocContent = this.htmlDocument.getText(0, this.htmlDocument.getLength());
			for (int i = 0; i < sDocContent.length(); i++) {
				try {
					final int pixel = this.modelToView(i).x;
					maximum = Math.max(maximum, pixel);
				} catch (final Exception e) {
					Page.logger.trace(null, e);
				}
			}
		} catch (final Exception e) {
			Page.logger.error(null, e);
		}
		return maximum;
	}

}
