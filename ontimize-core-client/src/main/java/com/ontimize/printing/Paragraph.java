package com.ontimize.printing;

import java.util.List;
import java.util.Vector;

import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a text paragraph with configurable parameters (font,...).
 *
 * @author Imatia Innovation
 *
 */
public class Paragraph implements ReportElement {

	private static final Logger logger = LoggerFactory.getLogger(Paragraph.class);

	protected String textContain = "";

	protected List sentences = new Vector();

	protected String identifier = "";

	protected Layer layer = null;

	protected Element paragraphElement = null;

	/**
	 * Internal class with some attributes to apply to all the text
	 */
	class Sentence {

		String text = "";

		TextAttributes attributes = null;

		public Sentence(final String text, final TextAttributes textAttributes) {
			this.text = text;
			this.attributes = textAttributes;
		}

		public void setText(final String text) {
			this.text = text;
		}

	}

	public Paragraph(final String id, final int leftMargin) {
		this.identifier = id;
		this.layer = new Layer(id, 100, 100, false, ReportElement.ALIGN_LEFT, leftMargin);
	}

	public Paragraph(final String id, final int leftMargin, final int width) {
		this.identifier = id;
		this.layer = new Layer(id, width, 100, false, ReportElement.ALIGN_LEFT, leftMargin);
	}

	/**
	 * Adds a text to the paragraph with the default attributes (font, etc)
	 * @param text
	 */
	public void add(final String text) {
		// Adds a new sentence in the appropriate position to keep the order
		this.sentences.add(this.sentences.size(), new Sentence(text, TextAttributes.getDefaultAttributes()));
	}

	/**
	 * Adds a text to the paragraph with the specified attributes
	 * @param text
	 * @param attributes
	 */
	public void add(final String text, final TextAttributes attributes) {
		// Adds a new sentence in the appropriate position to keep the order
		this.sentences.add(this.sentences.size(), new Sentence(text, attributes));
	}

	@Override
	public void insert(final ReportFrame reportFrame, final boolean multipage) throws Exception {
		// Inserts the paragraph content in the report
		// If the last character is not an space the put it to allow the
		// insertion of the last word
		final HTMLDocument htmlDocument = reportFrame.getCurrentPage().getHTMLDocument();
		try {
			final StringBuilder sbHTMLString = new StringBuilder(this.layer.getStartTag());
			// Try to insert all the string
			sbHTMLString.append(this.layer.getEndTag());
			// Checks for page footer
			final Element pageFooterElement = htmlDocument.getElement(ReportElement.PIEID);
			htmlDocument.insertBeforeStart(pageFooterElement, sbHTMLString.toString());
			this.paragraphElement = htmlDocument.getElement(this.identifier);// .getElement(0);
			// For each sentence insert word by word in the page
			for (int i = 0; i < this.sentences.size(); i++) {
				this.insert(reportFrame, multipage, (Sentence) this.sentences.get(i), this.paragraphElement);
			}
		} catch (final Exception e) {
			Paragraph.logger.error("Error formatting paragraph: " + e.getMessage(), e);
		}
	}

	/**
	 * Insert page by page
	 * @param reportFrame
	 * @param multipage
	 * @param sentence
	 * @param paragraphElement
	 */
	protected void insert(final ReportFrame reportFrame, final boolean multipage, final Sentence sentence, Element paragraphElement) {
		try {
			HTMLDocument htmlDocument = reportFrame.getCurrentPage().getHTMLDocument();
			String sText = sentence.text;
			final TextAttributes attributes = sentence.attributes;
			String sWord = "";
			String sWords = "";
			if (sText.charAt(sText.length() - 1) != ' ') {
				sText = sText + " ";
			}
			for (int j = 0; j < sText.length(); j++) {
				// Try with words, using the blanks to separate them in the
				// sentence
				if (sText.charAt(j) == ' ') {
					sWord = sWord + new Character(sText.charAt(j)).toString();
					sWords = sWords + sWord;
				} else {
					sWord = sWord + new Character(sText.charAt(j)).toString();
					continue;
				}
				final String textDoc1 = htmlDocument.getText(0, htmlDocument.getLength());
				// Delete the previous words except the last one
				htmlDocument.remove(paragraphElement.getEndOffset() - (sWords.length() - sWord.length()) - 1,
						sWords.length() - sWord.length());
				final String textDoc = htmlDocument.getText(0, htmlDocument.getLength());
				// Inserts the tags of font
				htmlDocument.insertBeforeEnd(paragraphElement,
						attributes.getStartTag() + sWords + attributes.getEndTag());
				// Checks the page
				final boolean bFullPage = reportFrame.getCurrentPage().isFull();
				if (!bFullPage) {
					// delete the word
					sWord = "";
				} else {
					htmlDocument.remove(paragraphElement.getEndOffset() - sWord.length() - 1, sWord.length());
					sentence.setText(sText.substring(j - (sWord.length() - 1), sText.length()));
					reportFrame.addPage(null);
					htmlDocument = reportFrame.getCurrentPage().getHTMLDocument();
					final StringBuilder sbHTMLString = new StringBuilder(this.layer.getStartTag());
					sbHTMLString.append(this.layer.getEndTag());
					final Element pageFooterElement = htmlDocument.getElement(ReportElement.PIEID);
					htmlDocument.insertBeforeStart(pageFooterElement, sbHTMLString.toString());
					paragraphElement = htmlDocument.getElement(this.identifier);
					this.insert(reportFrame, multipage, sentence, paragraphElement);
					break;
				}
			}
		} catch (final Exception e) {
			Paragraph.logger.error("Parrafo: " + e.getMessage(), e);
		}
	}

	/**
	 * Insert in the template, in the element with the specified identifier
	 */
	@Override
	public void insert(final ReportFrame reportFrame, final String identifier, final boolean multipage) throws Exception {
		// Get the element with the identifier
		final Element element = reportFrame.getCurrentPage().getElementById(identifier);
		String sHTMLString = ""/* "<P>" */;
		// Content:
		for (int i = 0; i < this.sentences.size(); i++) {
			sHTMLString = sHTMLString + ((Sentence) this.sentences.get(i)).attributes
					.getStartTag() + ((Sentence) this.sentences.get(i)).text
					+ ((Sentence) this.sentences.get(i)).attributes.getEndTag();
		}
		reportFrame.getCurrentPage().getHTMLDocument().insertBeforeEnd(element, sHTMLString);
	}

}
