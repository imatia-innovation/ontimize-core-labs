package com.ontimize.util.templates;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.ontimize.util.FileUtils;

public class ODFConcatParser {

	private static final Logger logger = LoggerFactory.getLogger(ODFConcatParser.class);

	public static boolean DEBUG = false;

	protected String destinationTag;

	protected String sourceStartTag;

	protected String sourceEndTag;

	protected File destinationDirectory = null;

	protected File destinationFile;

	protected List fontFaceDecls;

	protected List styles;

	protected List tableStyles;

	public static final String CONTENT_FILE = "content.xml";

	public static final String STYLE_FILE = "styles.xml";

	public static final String XML_TAG_TEXT_USER_FIELD_GET = "text:user-field-get";

	public static final String XML_ATTR_TEXT_NAME = "text:name";

	public static final String XML_TAG_OFFICE_TEXT = "office:text";

	public static final String XML_TAG_TEXT_SEQUENCE_DECLS = "text:sequence-decls";

	public static final String XML_TAG_TEXT_USER_FIELD_DECLS = "text:user-field-decls";

	public static final String XML_TAG_TEXT_P = "text:p";

	public static final String XML_TAG_FONT_FACE_DECLS = "office:font-face-decls";

	public static final String XML_TAG_STYLE_FONT_FACE = "style:font-face";

	public static final String XML_ATTR_STYLE_NAME = "style:name";

	public static final String XML_TAG_OFFICE_AUTOMATIC_STYLES = "office:automatic-styles";

	public static final String XML_TAG_OFFICE_STYLES = "office:styles";

	public static final String XML_TAG_STYLE_STYLE = "style:style";

	public static final String XML_ATTR_STYLE_FAMILY = "style:family";

	public static final String XML_VALUE_STYLE_FAMILY_TEXT = "text";

	public static final String XML_VALUE_STYLE_FAMILY_PARAGRAPH = "paragraph";

	public static final String XML_VALUE_STYLE_FAMILY_TABLE = "table";

	public static final String XML_ATTR_TEXT_STYLE_NAME = "text:style-name";

	public static final String XML_ATTR_TABLE_STYLE_NAME = "table:style-name";

	public ODFConcatParser(final String destinationTag, final String sourceStartTag, final String sourceEndTag) {
		this.destinationTag = destinationTag;
		this.sourceStartTag = sourceStartTag;
		this.sourceEndTag = sourceEndTag;
	}

	public File concatODTs(final File destination, final List sources, final List sourceHeader) throws Exception {
		this.destinationFile = destination;
		this.destinationDirectory = FileUtils.createTempDirectory();
		this.unzip(new FileInputStream(this.destinationFile), this.destinationDirectory);

		final File destinationContent = new File(this.destinationDirectory.getAbsolutePath(), ODFConcatParser.CONTENT_FILE);
		final Document destinationContentDocument = ODFConcatParser.getDocument(destinationContent);
		final Element e = destinationContentDocument.getDocumentElement();

		final File destinationStyle = new File(this.destinationDirectory.getAbsolutePath(), ODFConcatParser.STYLE_FILE);
		final Document destinationStyleDocument = ODFConcatParser.getDocument(destinationStyle);

		this.checkFontFaceDeclsDestination(destinationContentDocument);

		final NodeList nlot = e.getElementsByTagName(ODFConcatParser.XML_TAG_TEXT_USER_FIELD_GET);
		for (int i = 0, size = nlot.getLength(); i < size; i++) {
			final Node currentNode = nlot.item(i);
			if (currentNode instanceof Element) {
				final Element userFieldElement = (Element) currentNode;
				Node insertNode = null;
				if (this.destinationTag.equals(userFieldElement.getAttribute(ODFConcatParser.XML_ATTR_TEXT_NAME))) {
					insertNode = userFieldElement.getParentNode();
					while (!ODFConcatParser.XML_TAG_TEXT_P.equals(insertNode.getNodeName())) {
						insertNode = insertNode.getParentNode();
					}
				}
				if (insertNode != null) {
					String styleParent = null;
					if (insertNode instanceof Element) {
						styleParent = ((Element) insertNode).getAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME);
					}
					if (styleParent == null) {
						styleParent = "Standard";
					}
					// userFieldElement.getParentNode().replaceChild(arg0, arg1)
					// logger.debug("Encontre....");
					for (int j = 0; j < sources.size(); j++) {
						try {
							try {
								if (sourceHeader.size() > j) {
									final String header = (String) sourceHeader.get(j);
									if ((header != null) && (header.length() > 0)) {
										this.insertHeader(header, insertNode, styleParent);
									}
								}
							} catch (final Exception e2) {
								ODFConcatParser.logger.error(null, e2);
							}
							this.processSourceDocument((File) sources.get(j), insertNode, destinationStyleDocument);

						} catch (final Exception ex) {
							ODFConcatParser.logger.error("Error processing " + sources.get(j) + " file.", ex);
						}
					}
				}
				// else
				// throw new Exception(this.destinationTag +
				// " destination tag hasn't been found");
			}
		}

		ODFConcatParser.setDocument(destinationContentDocument, destinationContent);
		ODFConcatParser.setDocument(destinationStyleDocument, destinationStyle);
		final File file = new File(this.destinationDirectory.getParent(), this.destinationDirectory.getName() + ".odt");
		this.zip(this.destinationDirectory, file);
		return file;
		// <text:user-field-get text:display="none" style:data-style-name="N0"
		// text:name="INICIO_INSERCION" />
	}

	// <text:p text:style-name="Standard">
	// Header
	// </text:p>

	public void insertHeader(final String header, final Node nodeToReplace, final String style) {
		try {
			final Element node = nodeToReplace.getOwnerDocument().createElement(ODFConcatParser.XML_TAG_TEXT_P);
			node.setAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME, style);

			final Text headerText = nodeToReplace.getOwnerDocument().createTextNode(header);
			node.appendChild(headerText);
			nodeToReplace.getParentNode().insertBefore(node, nodeToReplace);
		} catch (final Exception e) {
			ODFConcatParser.logger.trace(null, e);
		}
	}

	public void processSourceDocument(final File source, final Node nodeToReplace, final Document destinationStyleDocument)
			throws Exception {
		ODFConcatParser.logger.debug("Processing..." + source.getName());
		final File sourceDirectory = FileUtils.createTempDirectory();
		this.unzip(new FileInputStream(source), sourceDirectory);

		final File sourceContent = new File(sourceDirectory.getAbsolutePath(), ODFConcatParser.CONTENT_FILE);
		final Document sourceContentDocument = ODFConcatParser.getDocument(sourceContent);

		final File sourceStyle = new File(sourceDirectory.getAbsolutePath(), ODFConcatParser.STYLE_FILE);
		final Document sourceStyleDocument = ODFConcatParser.getDocument(sourceStyle);

		final Map officeStyleToTransform = this.processStyleFile(destinationStyleDocument, sourceStyleDocument);

		this.checkFontFaceDeclsDestination(nodeToReplace.getOwnerDocument(), sourceContentDocument);

		final Map sourceAutomaticStyles = this.findSourceAutomaticStyles(sourceContentDocument);

		final Element rootElement = sourceContentDocument.getDocumentElement();

		final NodeList nlot = rootElement.getElementsByTagName(ODFConcatParser.XML_TAG_TEXT_USER_FIELD_GET);
		Node startElement = null, endElement = null;
		for (int i = 0, size = nlot.getLength(); i < size; i++) {
			final Node currentNode = nlot.item(i);
			if (currentNode instanceof Element) {
				final Element userFieldElement = (Element) currentNode;
				if (this.sourceStartTag.equals(userFieldElement.getAttribute(ODFConcatParser.XML_ATTR_TEXT_NAME))) {
					startElement = userFieldElement.getParentNode();
					while (!ODFConcatParser.XML_TAG_TEXT_P.equals(startElement.getNodeName())) {
						startElement = startElement.getParentNode();
					}
				}

				if (this.sourceEndTag.equals(userFieldElement.getAttribute(ODFConcatParser.XML_ATTR_TEXT_NAME))) {
					endElement = userFieldElement.getParentNode();
					while (!ODFConcatParser.XML_TAG_TEXT_P.equals(endElement.getNodeName())) {
						endElement = endElement.getParentNode();
					}
				}
			}
		}

		final List nodeListToCopy = new ArrayList();
		boolean startCopy = false;

		// copiar desde el principio...
		if (startElement == null) {
			startCopy = true;
		}

		final NodeList officeTexts = rootElement.getElementsByTagName(ODFConcatParser.XML_TAG_OFFICE_TEXT);
		if (officeTexts.getLength() != 1) {
			throw new Exception("Error: multiples office:text.");
		}
		final Node officeNode = officeTexts.item(0);

		final NodeList children = officeNode.getChildNodes();
		for (int k = 0; k < children.getLength(); k++) {
			final Node processNode = children.item(k);
			if (ODFConcatParser.XML_TAG_TEXT_SEQUENCE_DECLS.equals(processNode.getNodeName())
					|| ODFConcatParser.XML_TAG_TEXT_USER_FIELD_DECLS.equals(processNode.getNodeName())) {
				continue;
			}

			if (!startCopy && processNode.equals(startElement)) {
				startCopy = true;
			} else if (startCopy && processNode.equals(endElement)) {
				startCopy = false;
			} else if (startCopy) {
				nodeListToCopy.add(processNode);
			}
		}

		for (int i = 0; i < nodeListToCopy.size(); i++) {
			final Node current = (Node) nodeListToCopy.get(i);
			final Node importNode = nodeToReplace.getOwnerDocument().importNode(current, true);
			this.processStyles((Element) importNode, sourceAutomaticStyles, officeStyleToTransform);
			try {
				nodeToReplace.getParentNode().insertBefore(importNode, nodeToReplace);
			} catch (final Exception e) {
				ODFConcatParser.logger.trace(null, e);
			}
		}

		this.deleteDirectory(sourceDirectory);
	}

	public void checkFontFaceDeclsDestination(final Document document) {
		final NodeList fontsDecls = document.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_STYLE_FONT_FACE);
		this.fontFaceDecls = new ArrayList();
		for (int i = 0; i < fontsDecls.getLength(); i++) {
			final Node current = fontsDecls.item(i);
			if (current instanceof Element) {
				final Element currentElement = (Element) current;
				final String name = currentElement.getAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME);
				if (!this.fontFaceDecls.contains(name)) {
					this.fontFaceDecls.add(name);
				}
			}
		}

		final NodeList styleNodes = document.getDocumentElement().getElementsByTagName(ODFConcatParser.XML_TAG_STYLE_STYLE);

		this.styles = new ArrayList();
		this.tableStyles = new ArrayList();

		for (int i = 0; i < styleNodes.getLength(); i++) {
			final Node current = styleNodes.item(i);
			if (current instanceof Element) {
				final Element currentElement = (Element) current;
				final String name = currentElement.getAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME);
				if (!this.styles.contains(name) && (ODFConcatParser.XML_VALUE_STYLE_FAMILY_TEXT
						.equals(currentElement.getAttribute(ODFConcatParser.XML_ATTR_STYLE_FAMILY))
						|| ODFConcatParser.XML_VALUE_STYLE_FAMILY_PARAGRAPH
						.equals(currentElement.getAttribute(ODFConcatParser.XML_ATTR_STYLE_FAMILY)))) {
					this.styles.add(name);
				} else if (!this.tableStyles.contains(name) && ODFConcatParser.XML_VALUE_STYLE_FAMILY_TABLE
						.equals(currentElement.getAttribute(ODFConcatParser.XML_ATTR_STYLE_FAMILY))) {
					this.tableStyles.add(name);
				}
			}
		}

	}

	public void checkFontFaceDeclsDestination(final Document destinationDocument, final Document sourceDocument) {
		final NodeList fonts = destinationDocument.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_FONT_FACE_DECLS);
		Node destinationNode = null;
		if (fonts.getLength() == 1) {
			destinationNode = fonts.item(0);
		}

		final NodeList fontsDecls = sourceDocument.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_STYLE_FONT_FACE);

		for (int i = 0; i < fontsDecls.getLength(); i++) {
			final Node current = fontsDecls.item(i);
			if (current instanceof Element) {
				final Element currentElement = (Element) current;
				final String name = currentElement.getAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME);
				if (!this.fontFaceDecls.contains(name)) {
					final Node nodeToInsert = destinationNode.getOwnerDocument().importNode(currentElement, true);
					destinationNode.appendChild(nodeToInsert);
					this.fontFaceDecls.add(name);
				}
			}
		}
	}

	public void processStyles(final Element importNode, final Map sourceAutomaticStyles, final Map officeStyleToTransform) {
		final Map replaces = new Hashtable();
		final List processed = new ArrayList();
		this.processStyles(importNode, sourceAutomaticStyles, officeStyleToTransform, processed, replaces);
	}

	protected void processStyles(final Element importNode, final Map sourceAutomaticStyles, final Map officeStyleToTransform,
			final List processed, final Map replaces) {
		if (importNode.hasAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME)) {
			final String styleName = importNode.getAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME);
			if (sourceAutomaticStyles.containsKey(styleName)) {
				if (!processed.contains(styleName)) {
					final Element styleDefinition = (Element) sourceAutomaticStyles.get(styleName);
					String newStyleName = styleName;
					// insert in destination.
					if (this.styles.contains(styleName)) {
						newStyleName = this.getNewStyleName();
						styleDefinition.setAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME, newStyleName);
						replaces.put(styleName, newStyleName);
						importNode.setAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME, newStyleName);
					}

					final Element rootElement = importNode.getOwnerDocument().getDocumentElement();
					final NodeList list = rootElement.getElementsByTagName(ODFConcatParser.XML_TAG_OFFICE_AUTOMATIC_STYLES);
					final Element styleRoot = (Element) list.item(0);

					final Element importDefinition = (Element) styleRoot.getOwnerDocument().importNode(styleDefinition, true);
					this.debug(importDefinition);
					try {
						styleRoot.appendChild(importDefinition);
					} catch (final Exception ex) {
						ODFConcatParser.logger.trace(null, ex);
					}
					this.styles.add(newStyleName);
					processed.add(styleName);
				} else if (processed.contains(styleName) && replaces.containsKey(styleName)) {
					// Change name to style.....
					final String newStyleName = (String) replaces.get(styleName);
					importNode.setAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME, newStyleName);
				}
			} else if (officeStyleToTransform.containsKey(styleName)) {
				ODFConcatParser.verbose("Transform...." + styleName);
				if (!processed.contains(styleName)) {
					final Element styleDefinition = (Element) officeStyleToTransform.get(styleName);
					this.debug(styleDefinition);
					String newStyleName = styleName;
					// insert in destination.
					newStyleName = this.getNewOfficeStyleName(styleDefinition);
					styleDefinition.setAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME, newStyleName);
					replaces.put(styleName, newStyleName);
					importNode.setAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME, newStyleName);

					final Element rootElement = importNode.getOwnerDocument().getDocumentElement();
					final NodeList list = rootElement.getElementsByTagName(ODFConcatParser.XML_TAG_OFFICE_AUTOMATIC_STYLES);
					final Element styleRoot = (Element) list.item(0);

					final Element importDefinition = (Element) styleRoot.getOwnerDocument().importNode(styleDefinition, true);
					this.debug(importDefinition);
					try {
						styleRoot.appendChild(importDefinition);
					} catch (final Exception ex) {
						ODFConcatParser.logger.trace(null, ex);
					}
					this.styles.add(newStyleName);
					processed.add(styleName);
				} else if (processed.contains(styleName) && replaces.containsKey(styleName)) {
					// Change name to style.....
					final String newStyleName = (String) replaces.get(styleName);
					importNode.setAttribute(ODFConcatParser.XML_ATTR_TEXT_STYLE_NAME, newStyleName);
				}
			}
		} else if (importNode.hasAttribute(ODFConcatParser.XML_ATTR_TABLE_STYLE_NAME)) {
			final String styleName = importNode.getAttribute(ODFConcatParser.XML_ATTR_TABLE_STYLE_NAME);
			final String tableStyleName = styleName.indexOf(".") > 0 ? styleName.substring(0, styleName.indexOf("."))
					: styleName;
			if (sourceAutomaticStyles.containsKey(tableStyleName)) {
				if (!processed.contains(tableStyleName)) {
					final List tableElements = (List) sourceAutomaticStyles.get(tableStyleName);
					// Element styleDefinition = (Element)
					String newTableStyleName = tableStyleName;
					// insert in destination.
					if (this.tableStyles.contains(tableStyleName)) {
						newTableStyleName = this.getNewStyleName();
						this.setAttribute(tableElements, newTableStyleName);
						replaces.put(tableStyleName, newTableStyleName);
						this.setTableStyleName(importNode, tableStyleName, newTableStyleName);
					}

					final Element rootElement = importNode.getOwnerDocument().getDocumentElement();
					final NodeList list = rootElement.getElementsByTagName(ODFConcatParser.XML_TAG_OFFICE_AUTOMATIC_STYLES);
					final Element styleRoot = (Element) list.item(0);

					for (int i = 0; i < tableElements.size(); i++) {
						final Element importDefinition = (Element) styleRoot.getOwnerDocument()
								.importNode((Element) tableElements.get(i), true);
						try {
							styleRoot.appendChild(importDefinition);
						} catch (final Exception ex) {
							ODFConcatParser.logger.trace(null, ex);
						}
					}

					this.styles.add(newTableStyleName);
					processed.add(tableStyleName);
				} else if (processed.contains(tableStyleName) && replaces.containsKey(tableStyleName)) {
					// Change name to style.....
					final String newTableStyleName = (String) replaces.get(tableStyleName);
					this.setTableStyleName(importNode, tableStyleName, newTableStyleName);
				}
			}
		}

		final NodeList children = importNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node current = children.item(i);
			if (current instanceof Element) {
				this.processStyles((Element) current, sourceAutomaticStyles, officeStyleToTransform, processed,
						replaces);
			}
		}
	}

	public void setAttribute(final List tableElements, final String newTableStyleName) {
		for (int i = 0; i < tableElements.size(); i++) {
			final Element current = (Element) tableElements.get(i);
			current.setAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME, newTableStyleName);
		}
	}

	public void setTableStyleName(final Element importNode, final String oldTableStyleName, final String newTableStyleName) {
		final String oldStyleName = importNode.getAttribute(ODFConcatParser.XML_ATTR_TABLE_STYLE_NAME);
		final String value = oldStyleName.replaceFirst(oldTableStyleName, newTableStyleName);
		importNode.setAttribute(ODFConcatParser.XML_ATTR_TABLE_STYLE_NAME, value);
	}

	public String getNewStyleName() {
		int index = 0;
		for (int i = 0; i < this.styles.size(); i++) {
			final String current = (String) this.styles.get(i);
			if (current.startsWith("T")) {
				int currentIndex = -1;
				try {
					currentIndex = Integer.parseInt(current.substring(1).trim());
				} catch (final Exception e) {
					ODFConcatParser.logger.trace(null, e);
				}
				index = Math.max(index, currentIndex);
			}
		}
		return "T" + (index + 1);
	}

	public String getNewParagraphStyleName() {
		int index = 0;
		for (int i = 0; i < this.styles.size(); i++) {
			final String current = (String) this.styles.get(i);
			if (current.startsWith("P")) {
				int currentIndex = -1;
				try {
					currentIndex = Integer.parseInt(current.substring(1).trim());
				} catch (final Exception e) {
					ODFConcatParser.logger.trace(null, e);
				}
				index = Math.max(index, currentIndex);
			}
		}
		return "P" + (index + 1);
	}

	public String getNewTableName() {
		int index = 0;
		for (int i = 0; i < this.tableStyles.size(); i++) {
			final String current = (String) this.tableStyles.get(i);
			if (current.startsWith("Tabla")) {
				int currentIndex = -1;
				try {
					currentIndex = Integer.parseInt(current.substring(1).trim());
				} catch (final Exception e) {
					ODFConcatParser.logger.trace(null, e);
				}
				index = Math.max(index, currentIndex);
			}
		}
		return "Tabla" + (index + 1);
	}

	public String getNewOfficeStyleName(final Element node) {
		final String family = node.getAttribute(ODFConcatParser.XML_ATTR_STYLE_FAMILY);
		if (ODFConcatParser.XML_VALUE_STYLE_FAMILY_TEXT.equals(family)) {
			return this.getNewStyleName();
		}

		if (ODFConcatParser.XML_VALUE_STYLE_FAMILY_PARAGRAPH.equals(family)) {
			return this.getNewParagraphStyleName();
		}

		return this.getNewStyleName();
	}

	public Map findSourceAutomaticStyles(final Document source) {
		final Map automaticStylesCache = new Hashtable();
		final NodeList automaticStyles = source.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_OFFICE_AUTOMATIC_STYLES);
		for (int i = 0; i < automaticStyles.getLength(); i++) {
			final Node autStyle = automaticStyles.item(i);
			final NodeList list = autStyle.getChildNodes();
			for (int j = 0; j < list.getLength(); j++) {
				final Node nodeStyle = list.item(j);
				if (nodeStyle instanceof Element) {
					final String name = ((Element) nodeStyle).getAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME);
					final String family = ((Element) nodeStyle).getAttribute(ODFConcatParser.XML_ATTR_STYLE_FAMILY);

					if (!automaticStylesCache
							.containsKey(name)
							&& (ODFConcatParser.XML_VALUE_STYLE_FAMILY_TEXT.equals(family)
									|| ODFConcatParser.XML_VALUE_STYLE_FAMILY_PARAGRAPH.equals(family))) {
						automaticStylesCache.put(name, nodeStyle);
					}

					if (family.startsWith(ODFConcatParser.XML_VALUE_STYLE_FAMILY_TABLE)) {
						String tableName = null;
						if (name.indexOf(".") > 0) {
							tableName = name.substring(0, name.indexOf("."));
						} else {
							tableName = name;
						}

						if (!automaticStylesCache.containsKey(tableName)) {
							automaticStylesCache.put(tableName, new ArrayList());
						}

						final List tableElements = (List) automaticStylesCache.get(tableName);
						tableElements.add(nodeStyle);
					}
				}
			}
		}

		return automaticStylesCache;
	}

	public static Document getDocument(final File f) throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.parse(f);
		return document;
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

	public void zip(final File input, final File output) throws IOException {
		ODFConcatParser.verbose("Zip to " + output.getCanonicalPath() + "\n\n");

		// List all files in directory in recursive mode.
		final List list = new ArrayList();
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
			ODFConcatParser.verbose("\rZip file " + currentStr + ". ");

			if (!ze.isDirectory()) {
				final FileInputStream fis = new FileInputStream(current);
				int bytesTotal = 0, bytesRead = 0;
				while ((bytesRead = fis.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					bytesTotal += bytesRead;
				}
				ODFConcatParser.verbose("Written bytes " + bytesTotal);
			}

			out.closeEntry();
		}

		out.flush();
		fos.flush();
		out.close();
		fos.close();
	}

	/**
	 * Unzip the current InputStream to a temporal directory.
	 */
	public void unzip(final InputStream input, final File output) throws IOException {
		ODFConcatParser.verbose("Unzip to " + output + "\n\n");

		final byte[] buffer = new byte[4096];

		final ZipInputStream in = new ZipInputStream(input);
		ZipEntry entry = null;
		FileOutputStream out = null;

		while ((entry = in.getNextEntry()) != null) {
			final File fout = new File(output, entry.getName());
			final String s = fout.getCanonicalPath();

			if (entry.isDirectory()) {
				if (fout.mkdirs()) {
					ODFConcatParser.verbose("\rUnzip directory " + s + ". Created sucessfully.");
				} else {
					ODFConcatParser.verbose("\rUnzip directory " + s + ". Directory not created.");
				}
			} else {
				ODFConcatParser.verbose("\rUnzip file " + s + ". ");

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

				ODFConcatParser.verbose("Written bytes " + bytesTotal);
				out.flush();
				out.close();
			}
		}
		in.close();
	}

	protected static void verbose(final String verbose) {
		if (ODFConcatParser.DEBUG) {
			ODFConcatParser.logger.debug(verbose);
		}
	}

	public static void setDocument(final Document d, final File f) throws Exception {
		final Source source = new DOMSource(d);
		final Transformer transformer = TransformerFactory.newInstance().newTransformer();
		final Result resFile = new StreamResult(f);
		transformer.transform(source, resFile);
	}

	public void debug(final Node importNode) {
		if (ODFConcatParser.DEBUG) {
			try {
				final Source source = new DOMSource(importNode);
				final Transformer transformer = TransformerFactory.newInstance().newTransformer();
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final Result resFile = new StreamResult(out);
				transformer.transform(source, resFile);
				ODFConcatParser.logger.debug("-------------------------");
				ODFConcatParser.logger.debug(new String(out.toByteArray()));
				ODFConcatParser.logger.debug("-------------------------");
			} catch (final Exception e) {
				ODFConcatParser.logger.error(null, e);
			}
		}
	}

	public Map processStyleFile(final Document destinationStyleDocument, final Document sourceStyleDocument) {
		final Map officeStyleToTransformHashtable = new Hashtable();
		// style:font-face
		final List destinationData = new ArrayList();
		final NodeList list = destinationStyleDocument.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_STYLE_FONT_FACE);
		this.fillData(destinationData, list);

		final NodeList offices = destinationStyleDocument.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_FONT_FACE_DECLS);
		Node officeToInsert = null;
		for (int i = 0; i < offices.getLength(); i++) {
			officeToInsert = offices.item(i);
		}

		// officeToInsert
		final NodeList sourceList = sourceStyleDocument.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_STYLE_FONT_FACE);
		for (int j = 0; j < sourceList.getLength(); j++) {
			final Node currentElement = sourceList.item(j);
			if (currentElement instanceof Element) {
				final String name = ((Element) currentElement).getAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME);
				if (!destinationData.contains(name)) {
					final Node newElement = officeToInsert.getOwnerDocument().importNode(currentElement, true);
					officeToInsert.appendChild(newElement);
					destinationData.add(name);
				}
			}
		}

		// Retrieve the office:styles from source
		final NodeList sourceOfficeStyles = sourceStyleDocument.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_OFFICE_STYLES);
		final NodeList destinationOfficeStyles = destinationStyleDocument.getDocumentElement()
				.getElementsByTagName(ODFConcatParser.XML_TAG_OFFICE_STYLES);

		if ((sourceOfficeStyles != null) && (sourceOfficeStyles.getLength() == 1) && (destinationOfficeStyles != null)
				&& (destinationOfficeStyles.getLength() == 1)) {
			final Node officeStyle = sourceOfficeStyles.item(0);
			NodeList styles = ((Element) officeStyle).getElementsByTagName(ODFConcatParser.XML_TAG_STYLE_STYLE);
			final Map sourceStyles = new Hashtable();
			this.fillData(sourceStyles, styles);

			final Element destinationParent = (Element) destinationOfficeStyles.item(0);
			styles = destinationParent.getElementsByTagName(ODFConcatParser.XML_TAG_STYLE_STYLE);
			final Map destinationStyles = new Hashtable();
			this.fillData(destinationStyles, styles);

			for (final Enumeration items = Collections.enumeration(sourceStyles.keySet()); items.hasMoreElements();) {
				final String name = (String) items.nextElement();
				final Element sourceToInsert = (Element) sourceStyles.get(name);
				if (destinationStyles.containsKey(name)) {
					final Element destination = (Element) destinationStyles.get(name);
					if (this.isSameStyle(sourceToInsert, destination)) {
						continue;
					}

					ODFConcatParser.verbose("Transform " + name + " to automatic-styles....");
					officeStyleToTransformHashtable.put(name, sourceToInsert);
				}

				final Node newElement = destinationParent.getOwnerDocument().importNode(sourceToInsert, true);
				destinationParent.appendChild(newElement);
			}
		}

		return officeStyleToTransformHashtable;
	}

	protected boolean isSameStyle(final Element source, final Element destination) {
		if (!this.haveSameAttributes(source, destination)) {
			return false;
		}
		final NodeList nodeList = source.getChildNodes();
		final NodeList destinationList = destination.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node currentNode = nodeList.item(i);
			final String name = currentNode.getNodeName();
			final NodeList temp = destination.getElementsByTagName(name);
			if ((temp != null) && (temp.getLength() == 1)) {
				final boolean result = this.isSameStyle((Element) currentNode, (Element) temp.item(0));
				if (!result) {
					return result;
				}
			}
		}
		return true;
	}

	protected boolean haveSameAttributes(final Element source, final Element destination) {
		final NamedNodeMap sourceAttrs = source.getAttributes();
		final NamedNodeMap destinationAttrs = destination.getAttributes();

		if (sourceAttrs.getLength() != destinationAttrs.getLength()) {
			return false;
		}

		for (int i = 0; i < sourceAttrs.getLength(); i++) {
			final Node current = sourceAttrs.item(i);
			final String name = current.getNodeName();
			final String value = current.getNodeValue();
			final Node dest = destinationAttrs.getNamedItem(name);
			if (dest == null) {
				return false;
			}
			if (!(name.equals(dest.getNodeName()) && value.equals(dest.getNodeValue()))) {
				return false;
			}
		}

		return true;
	}

	protected void fillData(final Map data, final NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			final Node current = list.item(i);
			if (current instanceof Element) {
				final String name = ((Element) current).getAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME);
				data.put(name, current);
			}
		}
	}

	protected void fillData(final List data, final NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			final Node current = list.item(i);
			if (current instanceof Element) {
				final String name = ((Element) current).getAttribute(ODFConcatParser.XML_ATTR_STYLE_NAME);
				data.add(name);
			}
		}
	}

	protected void deleteDirectory(final File directory) {
		if (ODFConcatParser.DEBUG) {
			return;
		}

		this.deleteFile(directory);
	}

	protected void deleteFile(final File file) {
		if (file.isFile()) {
			file.delete();
		}
		if (file.isDirectory()) {
			final File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				this.deleteFile(listFiles[i]);
			}
		}
		file.delete();
	}

	public static void main(final String[] args) {
		final ODFConcatParser parser = new ODFConcatParser("ACORDOS_NON_URXENTES", "MARACADORINICIO", "MARCADORFIN");
		final File destinationFile = new File("d:/Temp/odt/cividas/PROBA_CONCAT_DESTINO.odt");

		final List sources = new ArrayList();
		sources.add(new File("d:/Temp/odt/cividas/Origen con tabla sin inicio.odt"));
		sources.add(new File("d:/Temp/odt/cividas/PROBA_CONCATENACION_ORIGEN2.odt"));

		final List header = new ArrayList();
		header.add("Asunto: inserción cabecera.");
		header.add("Asunto: inserción cabecera2...");
		try {
			parser.concatODTs(destinationFile, sources, header);
		} catch (final Exception e) {
			ODFConcatParser.logger.error(null, e);
		}
	}

}
