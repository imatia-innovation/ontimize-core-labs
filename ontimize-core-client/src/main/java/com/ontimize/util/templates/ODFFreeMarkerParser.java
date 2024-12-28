package com.ontimize.util.templates;

import java.awt.Toolkit;
import java.io.File;
import java.util.Map;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.util.remote.BytesBlock;

/**
 * Check if a ODF file contains FreeMarker directives. FreeMarker directives are necessary to
 * visualize data tables. http://jooreports.sourceforge.net http://freemarker.sourceforge.net/
 *
 * @author carlos.pereira
 * @since 28/06/2007
 */
public class ODFFreeMarkerParser extends ODFParser {

	private static final Logger logger = LoggerFactory.getLogger(ODFFreeMarkerParser.class);

	private static final String XML_TAG_SCRIPT = "text:script", XML_TAG_LANG = "script:language",
			XML_TAG_LANG_V = "JOOScript", XML_TAG_SCRIPT1 = "@table:table-row\n[#list ",
			XML_TAG_SCRIPT2 = " as ", XML_TAG_SCRIPT3 = "]\n\n@/table:table-row\n[/#list]";

	public ODFFreeMarkerParser(final File file) throws java.io.IOException {
		super(file);
	}

	public ODFFreeMarkerParser(final java.io.InputStream input) throws java.io.IOException {
		super(input);
	}

	/**
	 * @return The compressed directory or the directory with the decompress content
	 * @throws Exception
	 */
	public File parse() throws Exception {
		return this.parse(null, null, null, null);
	}

	public File parse(final Map fieldValues, final Map valuesTable, final Map valuesImages, final Map valuesPivotTable)
			throws Exception {
		ODFParser.log("ODFFreeMarkerParser -> Parsing ODF document to insert FreeMarker directives.");

		if (this.input == null) {
			return null;
		}

		// In the root directory, check if FILE_TO_PARSE exists.
		final File fp = this.get(ODFParser.FILE_TO_PARSE);
		if ((fp == null) || !fp.exists()) {
			ODFParser.log("ODFFreeMarkerParser -> File to parse not found. " + ODFParser.FILE_TO_PARSE);
			return null;
		}

		final Document document = ODFParser.getDocument(fp);
		final Element e = document.getDocumentElement();

		// Found tables in XML.
		ODFParser.log("ODFFreeMarkerParser -> Parsing ODF document tables.");

		final NodeList nlTables = e.getElementsByTagName(ODFParser.XML_TAG_TABLE);
		for (int i = 0, lTables = nlTables.getLength(); i < lTables; i++) {

			final Node nTable = nlTables.item(i); // Tag XML: table:table
			if ((nTable == null) || !(nTable instanceof Element)) {
				continue;
			}

			// Check if this table is a normal table, a pivot table or nothing
			// special
			boolean pivotTable = false;
			boolean normalTable = false;

			if ((fieldValues == null) && (valuesTable == null) && (valuesPivotTable == null)
					&& (valuesImages == null)) {
				pivotTable = false;
				normalTable = true;
			} else {
				final NamedNodeMap attributes = nTable.getAttributes();
				if ((attributes != null) && (attributes.getNamedItem(ODFParser.XML_TAG_TABLE_NAME) != null)) {
					final String tableName = attributes.getNamedItem(ODFParser.XML_TAG_TABLE_NAME).getNodeValue();
					if ((tableName != null) && (valuesPivotTable != null) && valuesPivotTable.containsKey(tableName)) {
						pivotTable = true;
					} else {
						normalTable = true;
					}
				}
			}
			if (normalTable) {
				this.configureNormalTable(nTable, i, document, valuesTable);
			} else if (pivotTable) {
				throw new Exception("Pivot tables not supported in OpenOffice");
				// configurePivotTable(nTable, i, document, valuesPivotTable);
			}
		}
		final NodeList nlImages = e.getElementsByTagName(ODFParser.XML_TAG_DRAW_FRAME);
		for (int i = 0, lImages = nlImages.getLength(); i < lImages; i++) {

			final Node nImage = nlImages.item(i); // Tag XML: table:table
			if ((nImage == null) || !(nImage instanceof Element)) {
				continue;
			}

			final NamedNodeMap attributes = nImage.getAttributes();
			if ((attributes != null) && (attributes.getNamedItem(ODFParser.XML_TAG_DRAW_FRAME_NAME) != null)) {
				try {
					final String imageName = attributes.getNamedItem(ODFParser.XML_TAG_DRAW_FRAME_NAME).getNodeValue();
					if ((imageName != null) && (valuesImages.get(imageName) != null)) {
						final BytesBlock bbIm = (BytesBlock) valuesImages.get(imageName);
						final ImageIcon im = new ImageIcon(Toolkit.getDefaultToolkit().createImage(bbIm.getBytes()));
						final int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
						final double pixelsPerCM = dpi / 2.54;
						attributes.getNamedItem(ODFParser.XML_TAG_DRAW_FRAME_WIDTH)
						.setNodeValue(String.valueOf(im.getIconWidth() / pixelsPerCM) + "cm");
						attributes.getNamedItem(ODFParser.XML_TAG_DRAW_FRAME_HEIGHT)
						.setNodeValue(String.valueOf(im.getIconHeight() / pixelsPerCM) + "cm");
					}
				} catch (final Exception ex) {
					if (ApplicationManager.DEBUG) {
						ODFFreeMarkerParser.logger.error(null, ex);
					}
				}

			}
		}

		// Save the current FILE_TO_PARSE XML.
		ODFParser.log("ODFFreeMarkerParser -> Save modified XML file to " + fp.getCanonicalPath());

		ODFParser.setDocument(document, fp);
		return this.get(null);
	}

	protected void configureNormalTable(final Node nTable, final int tableIndex, final Document document, final Map valuesTable) {
		// Found the rows. Tag XML: table:table-row
		final NodeList nlRows = ((Element) nTable).getElementsByTagName(ODFParser.XML_TAG_TABLE_ROW);
		final int rowCount = nlRows.getLength();

		if (rowCount <= 0) {
			ODFParser.log("ODFFreeMarkerParser -> Table  " + tableIndex + " without rows.");
			return;
		}

		// If there is only a row, this must contains the keys.
		// With two or plus, the second row always contains the keys.
		final Node row = rowCount == 1 ? nlRows.item(0) : nlRows.item(1);
		if (row == null) {
			ODFParser.log("ODFFreeMarkerParser -> Table number " + tableIndex + " without second row.");
			return;
		}
		final Node rowCell = row.getFirstChild(); // Tag XML: table:table-cell
		if (rowCell == null) {
			ODFParser.log("ODFFreeMarkerParser -> Table number " + tableIndex + " without first cell.");
			return;
		}
		final Node rowText = rowCell.getFirstChild(); // Tag XML: text:p
		if (rowText == null) {
			ODFParser.log("ODFFreeMarkerParser -> Table number " + tableIndex + " without text in first cell.");
			return;
		}
		final Node rowInput = rowText.getFirstChild(); // Tag XML: text:text-input
		if (rowInput == null) {
			ODFParser.log("ODFFreeMarkerParser -> Table number " + tableIndex + " without field in first cell.");
			return;
		}

		final NamedNodeMap nnm = rowInput.getAttributes();
		if ((nnm == null) || (nnm.getLength() == 0) || (nnm.getNamedItem(ODFParser.XML_TAG_INPUT_DESCRIP) == null)) {
			ODFParser.log("ODFFreeMarkerParser -> Table number " + tableIndex + " without description in first cell.");
			return;
		}

		final Node rowInputDescrip = nnm.getNamedItem(ODFParser.XML_TAG_INPUT_DESCRIP);
		final String keyStr = rowInputDescrip.getNodeValue();
		if ((keyStr == null) || (keyStr.length() == 0)) {
			ODFParser
			.log("ODFFreeMarkerParser -> Table number " + tableIndex + " without description key in first cell.");
			return;
		}
		final int index = keyStr.indexOf('.');
		if (index == -1) {
			ODFParser.log("ODFFreeMarkerParser -> Table number " + tableIndex + " with wrong format field.");
			return;
		}
		final String entity = keyStr.substring(0, index);

		// Insert JOOScript.
		final Element joos = document.createElement(ODFFreeMarkerParser.XML_TAG_SCRIPT);
		joos.setAttribute(ODFFreeMarkerParser.XML_TAG_LANG, ODFFreeMarkerParser.XML_TAG_LANG_V);
		final StringBuilder b = new StringBuilder();
		b.append(ODFFreeMarkerParser.XML_TAG_SCRIPT1);
		b.append(entity);
		b.append(ODFFreeMarkerParser.XML_TAG_SCRIPT2);
		b.append(entity);
		b.append(ODFFreeMarkerParser.XML_TAG_SCRIPT3);
		joos.appendChild(document.createTextNode(b.toString()));
		rowText.appendChild(joos);

		ODFParser.log("ODFFreeMarkerParser -> Insert JOOScript to fill tables." + joos);
	}

}
