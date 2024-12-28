package com.ontimize.util.templates;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.util.FileUtils;

/**
 * Generates a new ODF template file from a base empty ODF file. This file contains the input
 * fields, tables and images to fill with the values of the form.
 *
 * @author Imatia Innovation
 * @since Ontimize 5.1085
 */
public class ODFParser {

	private static final Logger logger = LoggerFactory.getLogger(ODFParser.class);

	public static boolean DEBUG = true, VERBOSE = true;

	public static final String FILE_TO_PARSE = "content.xml", FILE_DIR_ODF_IMAGES = "Pictures",
			FILE_EMPTY_IMAGE = ImageManager.EMPTY_IMAGE,

			XML_TAG_STYLE_AUT = "office:automatic-styles", XML_TAG_STYLE = "style:style",
			XML_TAG_STYLE_NAME = "style:name", XML_TAG_STYLE_NAME_4_BOLD_VALUE = "Text.Bold",
			XML_TAG_STYLE_NAME_4_TABLE_VALUE = "Table.Cell.Line.Border", XML_TAG_STYLE_FAMILY = "style:family",
			XML_TAG_STYLE_FAMILY_4_BOLD_VALUE = "paragraph",
			XML_TAG_STYLE_FAMILY_4_TABLE_VALUE = "table-cell", XML_TAG_STYLE_TEXT = "style:text-properties",
			XML_TAG_STYLE_WEIGHT = "fo:font-weight",
			XML_TAG_STYLE_WEIGHT_VALUE = "bold", XML_TAG_STYLE_CELL = "style:table-cell-properties",
			XML_TAG_STYLE_BORDER = "fo:border",
			XML_TAG_STYLE_BORDER_VALUE = "0.002cm solid #000000",

			XML_TAG_OFFICE_TEXT = "office:text",

			XML_TAG_TEXT = "text:p", XML_TAG_TEXT_STYLE = "text:style-name",

			XML_TAG_INPUT = "text:text-input", XML_TAG_INPUT_DESCRIP = "text:description",

			XML_TAG_TABLE = "table:table", XML_TAG_TABLE_NAME = "table:name", XML_TAG_TABLE_ROW = "table:table-row",
			XML_TAG_TABLE_COLUMN = "table:table-column",
			XML_TAG_TABLE_COLUMN_NUMBER = "table:number-columns-repeated", XML_TAG_TABLE_CELL = "table:table-cell",
			XML_TAG_TABLE_CELL_STYLE = "table:style-name",
			XML_TAG_TABLE_CELL_TYPE = "office:value-type", XML_TAG_TABLE_CELL_TYPE_VALUE = "string",

			XML_TAG_DRAW_FRAME = "draw:frame", XML_TAG_DRAW_FRAME_NAME = "draw:name",
			XML_TAG_DRAW_FRAME_ANCHOR = "text:anchor-type", XML_TAG_DRAW_FRAME_ANCHOR_VALUE = "as-char",
			XML_TAG_DRAW_FRAME_WIDTH = "svg:width", XML_TAG_DRAW_FRAME_WIDTH_VALUE = "5cm",
			XML_TAG_DRAW_FRAME_HEIGHT = "svg:height", XML_TAG_DRAW_FRAME_HEIGHT_VALUE = "5cm",
			XML_TAG_DRAW_IMAGE = "draw:image", XML_TAG_DRAW_IMAGE_HREF = "xlink:href";

	protected static void log(final String log) {
		if (ODFParser.DEBUG) {
			ODFParser.logger.debug(log);
		}
	}

	protected static void verbose(final String verbose) {
		if (ODFParser.VERBOSE) {
			ODFParser.logger.debug(verbose);
		}
	}

	/**
	 * Change dot character "." on keys with "ç" character
	 * @param values
	 * @return
	 */
	public static Map translateDotFields(final Map values) {

		try {
			final Map translations = new Hashtable();
			if (values == null) {
				return translations;
			}
			final Iterator valuesit = values.keySet().iterator();
			while (valuesit.hasNext()) {
				final Object o = valuesit.next();
				if ((o != null) && (o instanceof String)) {
					final String str = o.toString();
					if (str.indexOf(".") > -1) {
						final String stralt = str.replaceAll("\\.", "ç");
						translations.put(str, stralt);
					}
				}
			}
			final Iterator transit = translations.keySet().iterator();
			while (transit.hasNext()) {
				final String val = transit.next().toString();
				values.put(translations.get(val), values.remove(val));
			}
			// since 5.3.8
			// order columns should be the same as entity result because
			// are used in method getKeysOrder()
			if (values instanceof EntityResult) {
				((EntityResult) values).setColumnOrder(new Vector(translations.values()));
			}
		} catch (final Exception e) {
			ODFParser.logger.error(null, e);
		}
		return values;
	}

	/**
	 * ODF file.
	 */
	protected InputStream input;

	/**
	 * Uncompressed ODF file (Ref. to temporary directory).
	 */
	protected File temp;

	public ODFParser(final File file) throws IOException {
		this(new FileInputStream(file));
	}

	public ODFParser(final InputStream input) throws IOException {
		this.input = input;

		this.init();
	}

	protected void init() throws IOException {
		this.temp = FileUtils.createTempDirectory();
		this.unzip(this.input, this.temp);
	}

	public File getTemporalDiretory() {
		return this.temp;
	}

	public File create(Map fieldValues, Map valuesTable, Map valuesImages, final boolean createLabels)
			throws Exception {

		ODFParser.log("ODFParser -> Creating empty template for given data");

		valuesTable = ODFParser.translateTableDotFields(valuesTable);
		fieldValues = ODFParser.translateDotFields(fieldValues);
		valuesImages = ODFParser.translateDotFields(valuesImages);

		if (this.input == null) {
			return null;
		}

		final File fp = this.get(ODFParser.FILE_TO_PARSE);
		if ((fp == null) || !fp.exists()) {
			ODFParser.log("ODFParser -> File to create not found. " + fp.getCanonicalPath());
			return null;
		}

		final Document d = ODFParser.getDocument(fp);
		final Element e = d.getDocumentElement();

		// Gets the styles of the document.
		final NodeList nloas = e.getElementsByTagName(ODFParser.XML_TAG_STYLE_AUT);
		if (nloas.getLength() != 1) {
			ODFParser.log("ODFParser -> " + ODFParser.XML_TAG_STYLE_AUT + " wrong.");
			return null;
		}
		final Element eoas = (Element) nloas.item(0);

		final Element[] s = this.createStyleElements(d); // Defaults styles.
		for (int i = 0, size = s.length; i < size; i++) {
			eoas.appendChild(s[i]);
		}

		// Gets the body of the document.
		final NodeList nlot = e.getElementsByTagName(ODFParser.XML_TAG_OFFICE_TEXT);
		if (nlot.getLength() != 1) {
			ODFParser.log("ODFParser -> " + ODFParser.XML_TAG_OFFICE_TEXT + " wrong.");
			return null;
		}
		final Element eot = (Element) nlot.item(0);

		final Element[] f = this.createFieldElements(d, fieldValues, createLabels); // Fields.
		for (int i = 0, size = f.length; i < size; i++) {
			eot.appendChild(f[i]);
		}
		eot.appendChild(this.createSeparatorElement(d));

		final Element[] t = this.createTableElements(d, valuesTable); // Tables.
		for (int i = 0, size = t.length; i < size; i++) {
			eot.appendChild(t[i]);
			eot.appendChild(this.createSeparatorElement(d));
		}

		final Element[] i = this.createImageElements(d, valuesImages); // Images.
		for (int j = 0, size = i.length; j < size; j++) {
			eot.appendChild(i[j]);
			eot.appendChild(this.createSeparatorElement(d));
		}

		// Save the current FILE_TO_PARSE XML.
		ODFParser.log("ODFParser -> Save modified XML file to " + fp.getCanonicalPath());

		// Save XML and zip the temporal directory.
		ODFParser.setDocument(d, fp);
		final File file = new File(this.temp.getParent(), this.temp.getName() + ".odt");
		this.zip(this.temp, file);

		this.input.close();
		return file;

	}

	public File create(final Map fieldValues, final Map valuesTable, final Map valuesImages) throws Exception {
		return this.create(fieldValues, valuesTable, valuesImages, true);
	}

	private Element[] createStyleElements(final Document d) {
		final Element[] l = new Element[2];

		// Creates a style for the bold text.
		final Element bold = d.createElement(ODFParser.XML_TAG_STYLE);
		bold.setAttribute(ODFParser.XML_TAG_STYLE_NAME, ODFParser.XML_TAG_STYLE_NAME_4_BOLD_VALUE);
		bold.setAttribute(ODFParser.XML_TAG_STYLE_FAMILY, ODFParser.XML_TAG_STYLE_FAMILY_4_BOLD_VALUE);

		final Element boldProp = d.createElement(ODFParser.XML_TAG_STYLE_TEXT);
		boldProp.setAttribute(ODFParser.XML_TAG_STYLE_WEIGHT, ODFParser.XML_TAG_STYLE_WEIGHT_VALUE);
		bold.appendChild(boldProp);

		l[0] = bold;

		// Creates a style for the cell tables (border line)
		final Element cell = d.createElement(ODFParser.XML_TAG_STYLE);
		cell.setAttribute(ODFParser.XML_TAG_STYLE_NAME, ODFParser.XML_TAG_STYLE_NAME_4_TABLE_VALUE);
		cell.setAttribute(ODFParser.XML_TAG_STYLE_FAMILY, ODFParser.XML_TAG_STYLE_FAMILY_4_TABLE_VALUE);

		final Element cellProp = d.createElement(ODFParser.XML_TAG_STYLE_CELL);
		cellProp.setAttribute(ODFParser.XML_TAG_STYLE_BORDER, ODFParser.XML_TAG_STYLE_BORDER_VALUE);
		cell.appendChild(cellProp);

		l[1] = cell;
		return l;
	}

	private Element createSeparatorElement(final Document d) {
		return d.createElement(ODFParser.XML_TAG_TEXT);
	}

	private Element[] createFieldElements(final Document document, final Map fields, final boolean createLabel) {
		if ((fields == null) || fields.isEmpty()) {
			return new Element[0];
		}

		final List elements = new ArrayList();
		// The keys contains the field names.
		final Object[] keysOrder = AbstractTemplateGenerator.getKeysOrder(fields);
		for (int k = 0; k < keysOrder.length; k++) {
			final Object oKey = keysOrder[k];
			if ((oKey == null) || !(oKey instanceof String)) {
				continue;
			}

			final Object oValue = fields.get(oKey);
			final String key = (String) oKey;

			if (oValue instanceof Map) {
				// Create a group with a title in the template
				final Element eTitle = document.createElement(ODFParser.XML_TAG_TEXT); // Title
				eTitle.appendChild(document.createTextNode(key));
				elements.add(eTitle);
				final Object[] keysOrderGroup = AbstractTemplateGenerator.getKeysOrder((Map) oValue);
				for (int j = 0; j < keysOrderGroup.length; j++) {
					if (keysOrderGroup[j] instanceof String) {
						final String groupElementKey = (String) keysOrderGroup[j];
						final Object groupElementValue = ((Map) oValue).get(groupElementKey);
						final Element eText = document.createElement(ODFParser.XML_TAG_TEXT); // Text.
						if (createLabel) {
							eText.appendChild(document.createTextNode(groupElementValue + ": "));
						}
						final Element eInput = document.createElement(ODFParser.XML_TAG_INPUT); // Input
						// text.
						eInput.setAttribute(ODFParser.XML_TAG_INPUT_DESCRIP, groupElementKey);
						eInput.appendChild(document.createTextNode(groupElementKey));

						eText.appendChild(eInput);
						elements.add(eText);
					}
				}
				if (k < (keysOrder.length - 1)) {
					final Element element = document.createElement(ODFParser.XML_TAG_TEXT); // Blank
					element.appendChild(document.createTextNode(""));
					elements.add(element);
				}
			} else {
				final Element eText = document.createElement(ODFParser.XML_TAG_TEXT); // Text.
				if (createLabel) {
					eText.appendChild(document.createTextNode(oValue + ": "));
				}

				final Element eInput = document.createElement(ODFParser.XML_TAG_INPUT); // Input
				// text.
				eInput.setAttribute(ODFParser.XML_TAG_INPUT_DESCRIP, key);
				eInput.appendChild(document.createTextNode(key));

				eText.appendChild(eInput);
				elements.add(eText);
			}
		}
		final Element[] res = new Element[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			res[i] = (Element) elements.get(i);
		}
		return res;
	}

	private Element[] createTableElements(final Document d, final Map t) {
		if ((t == null) || t.isEmpty()) {
			return new Element[0];
		}

		final Element[] l = new Element[t.size()];

		final Enumeration k = Collections.enumeration(t.keySet());
		final Collection c = t.values();
		final Iterator v = c.iterator();
		int i = 0;

		while (k.hasMoreElements()) {
			Object o = k.nextElement();
			if ((o == null) || !(o instanceof String)) {
				v.next();
				continue;
			}
			final String key = (String) o; // Table name.

			o = v.next();
			if ((o == null) || !(o instanceof Map)) {
				continue;
			}
			final Map value = (Map) o; // Table.
			final int cols = value.size();

			final Element table = d.createElement(ODFParser.XML_TAG_TABLE);
			table.setAttribute(ODFParser.XML_TAG_TABLE_NAME, key);

			final Element columns = d.createElement(ODFParser.XML_TAG_TABLE_COLUMN);
			columns.setAttribute(ODFParser.XML_TAG_TABLE_COLUMN_NUMBER, Integer.toString(cols));
			table.appendChild(columns);

			// Create the title row.
			final Element titles = d.createElement(ODFParser.XML_TAG_TABLE_ROW);
			table.appendChild(titles);

			// Create the data row.
			final Element data = d.createElement(ODFParser.XML_TAG_TABLE_ROW);
			table.appendChild(data);

			final Enumeration e = Collections.enumeration(value.keySet());
			while (e.hasMoreElements()) {
				o = e.nextElement();
				if ((o == null) || !(o instanceof String)) {
					continue;
				}
				final String columnName = (String) o;
				final String translatedColumnName = (String) value.get(o);
				final String cellName = key + "." + columnName;

				// Title cell.
				Element cell = d.createElement(ODFParser.XML_TAG_TABLE_CELL);
				cell.setAttribute(ODFParser.XML_TAG_TABLE_CELL_STYLE, ODFParser.XML_TAG_STYLE_NAME_4_TABLE_VALUE);
				cell.setAttribute(ODFParser.XML_TAG_TABLE_CELL_TYPE, ODFParser.XML_TAG_TABLE_CELL_TYPE_VALUE);

				Element cellText = d.createElement(ODFParser.XML_TAG_TEXT);
				cellText.setAttribute(ODFParser.XML_TAG_TEXT_STYLE, ODFParser.XML_TAG_STYLE_NAME_4_BOLD_VALUE);
				cellText.appendChild(d.createTextNode(translatedColumnName));
				cell.appendChild(cellText);

				titles.appendChild(cell);

				// Data cell.
				cell = d.createElement(ODFParser.XML_TAG_TABLE_CELL);
				cell.setAttribute(ODFParser.XML_TAG_TABLE_CELL_STYLE, ODFParser.XML_TAG_STYLE_NAME_4_TABLE_VALUE);
				cell.setAttribute(ODFParser.XML_TAG_TABLE_CELL_TYPE, ODFParser.XML_TAG_TABLE_CELL_TYPE_VALUE);

				cellText = d.createElement(ODFParser.XML_TAG_TEXT);
				cell.appendChild(cellText);

				final Element cellInput = d.createElement(ODFParser.XML_TAG_INPUT);
				cellText.appendChild(cellInput);
				cellInput.setAttribute(ODFParser.XML_TAG_INPUT_DESCRIP, cellName);
				cellInput.appendChild(d.createTextNode(cellName));

				data.appendChild(cell);
			}
			l[i++] = table;
		}
		return l;
	}

	private Element[] createImageElements(final Document d, final Map i) throws Exception {
		if ((i == null) || i.isEmpty()) {
			return new Element[0];
		}

		final Element[] l = new Element[i.size()];

		final File dImages = this.get(ODFParser.FILE_DIR_ODF_IMAGES);
		if (!dImages.exists()) {
			dImages.mkdirs();
		}

		final Enumeration k = Collections.enumeration(i.keySet());
		int c = 0;

		int imageIndex = 0;

		while (k.hasMoreElements()) {

			final InputStream empty = this.getEmptyImageInputStream(imageIndex);
			imageIndex = imageIndex + 1;

			final Object o = k.nextElement();
			if ((o == null) || !(o instanceof String)) {
				continue;
			}
			final String key = (String) o;

			final Element eText = d.createElement(ODFParser.XML_TAG_TEXT);
			final Element eFrame = d.createElement(ODFParser.XML_TAG_DRAW_FRAME);
			eText.appendChild(eFrame);

			eFrame.setAttribute(ODFParser.XML_TAG_DRAW_FRAME_NAME, key);
			eFrame.setAttribute(ODFParser.XML_TAG_DRAW_FRAME_ANCHOR, ODFParser.XML_TAG_DRAW_FRAME_ANCHOR_VALUE);
			eFrame.setAttribute(ODFParser.XML_TAG_DRAW_FRAME_WIDTH, ODFParser.XML_TAG_DRAW_FRAME_WIDTH_VALUE);
			eFrame.setAttribute(ODFParser.XML_TAG_DRAW_FRAME_HEIGHT, ODFParser.XML_TAG_DRAW_FRAME_HEIGHT_VALUE);

			final Element eImage = d.createElement(ODFParser.XML_TAG_DRAW_IMAGE);
			eFrame.appendChild(eImage);
			eImage.setAttribute(ODFParser.XML_TAG_DRAW_IMAGE_HREF, ODFParser.FILE_DIR_ODF_IMAGES + "/" + key + ".png");

			FileUtils.copyFile(empty, new File(dImages, key + ".png"));
			l[c++] = eText;

		}
		return l;
	}

	protected InputStream getEmptyImageInputStream(final int imageIndex) throws Exception {
		// Get a reference to the empty image.
		final int width = 150;
		final int height = 60;
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		final Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
		graphics.setBackground(Color.white);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.red);
		graphics.drawString("Image", 50, 10);
		graphics.drawString("not available", 30, 30);
		graphics.drawString("" + imageIndex, 60, 50);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", out);
		final ByteArrayInputStream result = new ByteArrayInputStream(out.toByteArray());
		return result;
	}

	/**
	 * Returns the file inside of the ODF document. Note: The ODF document is a ZIP file.
	 */
	public File get(final String path) {
		if (this.temp == null) {
			return null;
		}
		if ((path == null) || (path.length() == 0)) {
			return this.temp;
		}
		return new File(this.temp, path);
	}

	public static Document getDocument(final File f) throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.parse(f);
		return document;
	}

	public static void setDocument(final Document d, final File f) throws Exception {
		final Source source = new DOMSource(d);
		final Transformer transformer = TransformerFactory.newInstance().newTransformer();
		final Result resFile = new StreamResult(f);
		transformer.transform(source, resFile);
	}

	public void zip(final File input, final File output) throws IOException {
		ODFParser.verbose("Zip to " + output.getCanonicalPath() + "\n\n");

		// List all files in directory in recursive mode.
		final ArrayList list = new ArrayList();
		this.listFiles(input, list);

		final String baseStr = input.getCanonicalPath();
		final int baseLength = baseStr.length() + 1;
		final byte[] buffer = new byte[4096];
		final FileOutputStream fos = new FileOutputStream(output);
		final ZipOutputStream out = new ZipOutputStream(fos);

		for (int i = 0, size = list.size(); i < size; i++) {

			// Current file.
			final Object o = list.get(i);
			if ((o == null) || !(o instanceof File)) {
				continue;
			}
			final File current = (File) o;
			if (current.isDirectory()) {
				continue;
			}

			String currentStr = current.getCanonicalPath();
			if (baseLength < currentStr.length()) {
				currentStr = currentStr.substring(baseLength);
			}
			currentStr = currentStr.replace('\\', '/');

			// Insert in ZIP file.
			final ZipEntry ze = new ZipEntry(currentStr);
			out.putNextEntry(ze);
			ODFParser.verbose("\rZip file " + currentStr + ". ");

			if (!ze.isDirectory()) {
				final FileInputStream fis = new FileInputStream(current);
				int bytesTotal = 0, bytesRead = 0;
				while ((bytesRead = fis.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					bytesTotal += bytesRead;
				}
				ODFParser.verbose("Written bytes " + bytesTotal);
			}

			out.closeEntry();
		}

		out.flush();
		fos.flush();
		out.close();
		fos.close();
	}

	protected void listFiles(final File file, final List list) {

		if ((file == null) || !file.exists()) {
			return;
		}

		list.add(file);

		if (!file.isDirectory()) {
			return;
		}

		final File[] f = file.listFiles();
		for (int i = 0, size = f.length; i < size; i++) {
			this.listFiles(f[i], list);
		}
	}

	/**
	 * Unzip the current InputStream to a temporal directory.
	 */
	public void unzip(final InputStream input, final File output) throws IOException {
		ODFParser.verbose("Unzip to " + output + "\n\n");

		final byte[] buffer = new byte[4096];

		final ZipInputStream in = new ZipInputStream(input);
		ZipEntry entry = null;
		FileOutputStream out = null;

		while ((entry = in.getNextEntry()) != null) {
			final File fout = new File(output, entry.getName());
			final String s = fout.getCanonicalPath();

			if (entry.isDirectory()) {
				if (fout.mkdirs()) {
					ODFParser.verbose("\rUnzip directory " + s + ". Created sucessfully.");
				} else {
					ODFParser.verbose("\rUnzip directory " + s + ". Directory not created.");
				}
			} else {
				ODFParser.verbose("\rUnzip file " + s + ". ");

				// Create parent directories if not exists.
				final File foutp = fout.getParentFile();
				if (foutp != null) {
					foutp.mkdirs();
				}

				// Check if file exists.
				if (fout.exists()) {
					fout.delete();
				}

				out = new FileOutputStream(fout);

				int bytesTotal = 0, bytesRead = 0;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);

					bytesTotal += bytesRead;
				}

				ODFParser.verbose("Written bytes " + bytesTotal);
				out.flush();
				out.close();
			}
		}
		in.close();
		out.close();
	}

	public List queryTemplateFields() throws Exception {
		final File fp = this.get(ODFParser.FILE_TO_PARSE);
		if ((fp == null) || !fp.exists()) {
			ODFParser.log("ODFParser -> File to query not found. " + fp.getCanonicalPath());
			return null;
		}

		final Document d = ODFParser.getDocument(fp);
		final Element e = d.getDocumentElement();

		// Gets the input-fields of the document.
		final NodeList nInput = e.getElementsByTagName(ODFParser.XML_TAG_INPUT);
		if ((nInput != null) && (nInput.getLength() > 0)) {
			final List fieldNames = new ArrayList();
			for (int i = 0; i < nInput.getLength(); i++) {
				final Node currentField = nInput.item(i);
				if ((currentField.getAttributes() != null)
						&& (currentField.getAttributes().getNamedItem(ODFParser.XML_TAG_INPUT_DESCRIP) != null)) {
					fieldNames
					.add(currentField.getAttributes().getNamedItem(ODFParser.XML_TAG_INPUT_DESCRIP).getNodeValue());
				}
			}
			return fieldNames;
		}
		return null;
	}

	public static Map translateTableDotFields(Map valuesTable) {
		valuesTable = ODFParser.translateDotFields(valuesTable);
		final Iterator it = valuesTable.keySet().iterator();
		final List vlist = new Vector();
		while (it.hasNext()) {
			vlist.add(it.next());
		}

		for (int i = 0; i < vlist.size(); i++) {
			final Object o = valuesTable.get(vlist.get(i));
			if ((o != null) && (o instanceof Map)) {
				final Map newo = ODFParser.translateTableDotFields((Map) o);
				if ((newo != null) && !newo.equals(o)) {
					valuesTable.put(vlist.get(i), newo);
				}
			}
		}
		return valuesTable;
	}

}
