package com.ontimize.util.rtf;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.util.rtf.element.RTFElement;

public class RTFReader {

	private static final Logger logger = LoggerFactory.getLogger(RTFReader.class);

	public static boolean DEBUG = false;

	protected com.ontimize.util.rtf.style.RTFDocument mainDocument;

	private final Map fontMap = new Hashtable();

	private final List colorTable = new Vector(1000);

	private int currentOffset = 0;

	protected boolean isTable = true;

	protected boolean cellread = true;

	public RTFReader(final Document doc) {
		this.mainDocument = (com.ontimize.util.rtf.style.RTFDocument) doc;
		this.colorTable.add(Color.black);
	}

	public void read(final String fileName, final int offset) throws IOException, BadLocationException {
		final FileReader in = new FileReader(fileName);
		this.read(in, offset);
		in.close();
	}

	public void read(final Reader in, final int offset) throws IOException, BadLocationException {
		try {
			this.currentOffset = offset;
			final RTFParserExtended rtf = new RTFParserExtended(in);
			RTFElement root = null;
			try {
				root = rtf.parse();
			} catch (final Exception ex) {
				RTFReader.logger.error(null, ex);
				throw new IOException(ex.getMessage());
			}

			if (root == null) {
				return;
			}

			final int cnt = root.getChildCount();
			for (int i = 0; i < cnt; i++) {
				final RTFElement node = root.getChild(i);

				final String nodeName = node.getName();
				final Object nodeContent = node.getContent();
				final String nodeType = node.getType();
				final int nodeLength = node.getLength();
				if (RTFReader.DEBUG) {
					RTFReader.logger.debug(nodeName);
					RTFReader.logger.debug("{}", nodeContent != null ? nodeContent : "");
				}
				if (nodeName.equals("\\pard") || nodeName.equals("\\li") || nodeName.equals("\\ri")
						|| nodeName.equals("\\fi") || nodeName.equals("\\f")
						|| nodeName.equals("\\fs")) {
					this.currentOffset = offset;
					this.processDocumentBody(root, i, this.mainDocument);
					if (this.mainDocument.getText(0, this.mainDocument.getLength())
							.charAt(this.currentOffset - 1) != '\n') {
						break;
					}
					this.mainDocument.remove(this.currentOffset - 1, 1);
					break;
				}

				if (nodeName.equals("*<group>*")) {
					final RTFElement groupNode = node;
					final String groupName = groupNode.getChild(0).getName();

					this.processGroup(groupNode, groupName);
				}
			}
			if (ApplicationManager.DEBUG) {
				RTFReader.logger.debug(this.mainDocument.getText(0, this.mainDocument.getLength()));
			}
		} catch (final Exception ex) {
			RTFReader.logger.error("Invalid RTF content! " + ex.getMessage(), ex);
		}
	}

	private void processDocumentBody(final RTFElement root, final int indexStartBody,
			final com.ontimize.util.rtf.style.RTFDocument document) throws BadLocationException, IOException {
		MutableAttributeSet attr = new SimpleAttributeSet();
		final int cnt = root.getChildCount();
		String previousName = "";
		for (int i = indexStartBody; i < cnt; i++) {
			final RTFElement node = root.getChild(i);
			final String nodeName = node.getName();
			Object nodeContent = node.getContent();
			if (RTFReader.DEBUG) {
				RTFReader.logger.debug(nodeName);
				RTFReader.logger.debug("{}", nodeContent != null ? nodeContent : "");
			}
			if (nodeName.equals("\\pard")) {
				if (attr == null) {
					attr = new SimpleAttributeSet();
				} else {
					attr = new SimpleAttributeSet();
				}
			} else if (nodeName.equals("*<group>*")) {
				if ((i + 1) < root.getChildCount()) {
					this.processBodyGroup(node, attr, document, root.getChild(i + 1).getName());
				} else {
					this.processBodyGroup(node, attr, document, null);
				}

			} else if (nodeName.equals("\\tab")) {
				document.insertString(this.currentOffset, "\t", attr);
				this.currentOffset += 1;
				nodeContent = null;
			} else if (nodeName.equals("\\page")) {
				processPage(document, attr);
				nodeContent = null;
			} else if (nodeName.equals("\\par")) {
				nodeContent = this.processParBodyGroup(document, attr);
			} else if (nodeName.equals("\\intbl") && this.isTable) {
				this.isTable = false;
				final int endOfTableInd = this.processTableDefinition(node, attr, i, 1, document);

				this.processTableContent(node, attr, i, endOfTableInd, 1, document);
				i = endOfTableInd;
			} else if (nodeName.substring(0, Math.min(2, nodeName.length())).equals("\\'")) {
				this.processApostropheBodyGroup(document, attr, node, nodeName);

			} else {
				this.processAttribute(node, attr);
			}

			if ((nodeContent != null) && !nodeContent.equals(" ")) {
				final String content = (String) nodeContent;
				if ((i != 0) && !previousName.equals("*<group>*") && (content.charAt(0) == ' ')) {
					document.insertString(this.currentOffset, content.substring(1), attr);
					this.currentOffset += content.length() - 1;
				} else {
					document.insertString(this.currentOffset, content, attr);
					this.currentOffset += content.length();
				}
			}
			previousName = nodeName;
		}
	}

	private void processAttribute(final RTFElement node, final MutableAttributeSet attr) {
		final String name = node.getName();
		if (name.equals("\\ql")) {
			StyleConstants.setAlignment(attr, 0);
		} else if (name.equals("\\qr")) {
			StyleConstants.setAlignment(attr, 2);
		} else if (name.equals("\\qc")) {
			StyleConstants.setAlignment(attr, 1);
		} else if (name.equals("\\qj")) {
			StyleConstants.setAlignment(attr, 3);
		} else if (name.equals("\\b")) {
			if (node.getLength() == -1) {
				StyleConstants.setBold(attr, true);
			} else {
				StyleConstants.setBold(attr, false);
			}
		} else if (name.equals("\\i")) {
			if (node.getLength() == -1) {
				StyleConstants.setItalic(attr, true);
			} else {
				StyleConstants.setItalic(attr, false);
			}
		} else if (name.equals("\\ul")) {
			if (node.getLength() != 0) {
				StyleConstants.setUnderline(attr, true);
			}
		} else if (name.equals("\\ulnone")) {
			StyleConstants.setUnderline(attr, false);
		} else if (name.equals("\\strike")) {
			StyleConstants.setStrikeThrough(attr, true);
		} else if (name.equals("\\sub")) {
			StyleConstants.setSubscript(attr, true);
		} else if (name.equals("\\super")) {
			StyleConstants.setSuperscript(attr, true);
		} else if (!name.equals("\\strike0")) {
			this.processStrikeAttribute(node, attr, name);
		}
	}

	private void processStrikeAttribute(final RTFElement node, final MutableAttributeSet attr, final String name) {
		if (!name.equals("\\sub0")) {
			if (!name.equals("\\super0")) {
				if (name.equals("\\f")) {
					final String fontNum = Integer.toString(node.getLength());
					final String fontName = (String) this.fontMap.get(fontNum);
					if (fontName != null) {
						StyleConstants.setFontFamily(attr, fontName);
					}
				} else if (name.equals("\\fs")) {
					final int fontSize = node.getLength();
					final int realSize = fontSize / 2;
					StyleConstants.setFontSize(attr, realSize);
				} else if (name.equals("\\cf")) {
					int colorNumber = node.getLength();
					if (colorNumber < 0) {
						colorNumber = 0;
					}
					final Color fg = (Color) this.colorTable.get(colorNumber);
					StyleConstants.setForeground(attr, fg);
				} else if (name.equals("\\highlight")) {
					final int colorNumber = node.getLength();
					final Color bg = (Color) this.colorTable.get(colorNumber - 1);
					StyleConstants.setBackground(attr, bg);
				} else if (name.equals("\\li")) {
					int leftIndent = node.getLength();
					leftIndent = this.convertTwipsToPixels(leftIndent);
					StyleConstants.setLeftIndent(attr, leftIndent);
				} else if (name.equals("\\ri")) {
					int rightIndent = node.getLength();
					rightIndent = this.convertTwipsToPixels(rightIndent);
					StyleConstants.setRightIndent(attr, rightIndent);
				} else if (name.equals("\\fi")) {
					int firstIndent = node.getLength();
					firstIndent = this.convertTwipsToPixels(firstIndent);
					StyleConstants.setFirstLineIndent(attr, firstIndent);
				} else if (name.equals("\\sa")) {
					int spaceAbove = node.getLength();
					spaceAbove = this.convertTwipsToPixels(spaceAbove);
					StyleConstants.setSpaceAbove(attr, spaceAbove);
				} else if (name.equals("\\sb")) {
					int spaceBelow = node.getLength();
					spaceBelow = this.convertTwipsToPixels(spaceBelow);
					StyleConstants.setSpaceBelow(attr, spaceBelow);
				} else if (name.equals("\\sl")) {
					float lineSpacing = node.getLength();
					lineSpacing /= 240.0F;
					StyleConstants.setLineSpacing(attr, lineSpacing);
				} else if (name.equals("\\s")) {
					final int styleNumber = node.getLength();
					attr.addAttribute("styleNumber", new Integer(styleNumber));
				}
			}
		}
	}

	private void processBodyGroup(final RTFElement bodyGroup, final MutableAttributeSet parentAttr,
			final com.ontimize.util.rtf.style.RTFDocument document, final String nextNodeName)
					throws BadLocationException, IOException {
		final String groupName = this.getGroupName(bodyGroup);
		if (groupName.equals("\\datafield") || groupName.equals("\\fldinst") || groupName.equals("\\object")
				|| groupName.equals("\\themedata") || groupName.equals("\\datastore")
				|| groupName.equals("\\latentstyles") || groupName.equals("\\colorschememapping")
				|| groupName.equals("\\objdata")) {
			return;
		}
		MutableAttributeSet attr = new SimpleAttributeSet(parentAttr);
		final int cnt = bodyGroup.getChildCount();
		String previousName = "";
		for (int i = 0; i < cnt; i++) {
			final RTFElement node = bodyGroup.getChild(i);
			final String nodeName = node.getName();
			Object nodeContent = node.getContent();
			RTFReader.logger.debug(nodeName);
			RTFReader.logger.debug("{}", nodeContent != null ? nodeContent : "");
			if (nodeName.equals("\\pard")) {
				attr = new SimpleAttributeSet(parentAttr);
			} else if (nodeName.equals("\\page")) {
				processPage(document, attr);
				nodeContent = null;
			} else if (nodeName.equals("\\cell")) {
				this.currentOffset += 1;
			} else if (nodeName.equals("\\nestcell")) {
				this.currentOffset += 1;
			} else if (nodeName.equals("*<group>*")) {
				if (!this.getGroupName(node).equals("\\nonesttables")) {
					this.processBodyGroup(node, attr, document, nodeName);
				}
			} else {
				if (nodeName.equals("\\pict")) {
					this.processImage(bodyGroup, document);
					return;
				}
				if (nodeName.equals("\\par")) {
					nodeContent = this.processParBodyGroup(document, attr);
				} else if (nodeName.substring(0, Math.min(2, nodeName.length())).equals("\\'")) {
					this.processApostropheBodyGroup(document, attr, node, nodeName);
				} else {
					if (nodeName.equals("\\bkmkstart")) {
						return;
					}
					if (nodeName.equals("\\bkmkend")) {
						return;
					}
					if (nodeName.equals("\\footnote")) {
						return;
					}

					this.processAttribute(node, attr);
				}
			}

			if (nodeContent != null) {
				this.processBodyGroupNodeContent(document, nextNodeName, attr, previousName, i, nodeContent);
			}
			previousName = nodeName;
		}
	}

	protected void processPage(final com.ontimize.util.rtf.style.RTFDocument document, final MutableAttributeSet attr)
			throws BadLocationException {
		if (!document.getText(this.currentOffset - 1, 1).equals("\n")) {
			document.insertString(this.currentOffset, "\n\f\n", attr);
			this.currentOffset += 3;
		} else {
			document.insertString(this.currentOffset, "\f", attr);
			this.currentOffset += 1;
		}
	}

	protected void processApostropheBodyGroup(final com.ontimize.util.rtf.style.RTFDocument document,
			final MutableAttributeSet attr, final RTFElement node, final String nodeName)
					throws BadLocationException {
		if (node.getLength() != -1) {
			final String ll = nodeName.substring(2, 4);
			final byte b1 = Byte.parseByte(ll.substring(0, 1), 16);
			final byte b2 = Byte.parseByte(ll.substring(1, 2), 16);
			final byte value = (byte) ((b1 * 16) + b2);
			final char ch = new Character((char) value).charValue();

			final byte[] bb = new byte[1];
			bb[0] = value;
			String ss = new String(bb);
			document.insertString(this.currentOffset, ss, attr);
			this.currentOffset += 1;
			if (ll.length() > 2) {
				ss = ll.substring(2);
				document.insertString(this.currentOffset, ss, attr);
				this.currentOffset += ss.length();
			}
		}
	}

	protected Object processParBodyGroup(final com.ontimize.util.rtf.style.RTFDocument document, final MutableAttributeSet attr)
			throws BadLocationException {
		Object nodeContent;
		document.insertString(this.currentOffset, "\n", attr);
		this.removeCharacterAttributes(attr);
		document.setParagraphAttributes(this.currentOffset, 0, attr, false);
		this.currentOffset += 1;
		// this.currentOffset += (1 + count);
		nodeContent = null;
		return nodeContent;
	}

	private void processBodyGroupNodeContent(final com.ontimize.util.rtf.style.RTFDocument document, final String nextNodeName,
			final MutableAttributeSet attr, final String previousName, final int i,
			final Object nodeContent) throws BadLocationException {
		final String content = (String) nodeContent;
		// since 5.3.8 added condition -> && (content.length() == 1 due
		// to e.g Albertó Doval was pasted in rtf as AlbertóDoval
		// so only skipped individual spaces when content length equals
		// to 1
		if ((i != 0) && !previousName.equals("*<group>*") && (content.charAt(0) == ' ') && (content.length() == 1)) {
			if (content.substring(1).length() > 0) {
				if ((nextNodeName != null) && nextNodeName.equals("\\cell") && !this.cellread) {
					this.currentOffset += 1;
				}
				this.cellread = false;
				document.insertString(this.currentOffset, content.substring(1), attr);
				this.currentOffset += content.length() - 1;
			}
		} else {
			document.insertString(this.currentOffset, content, attr);
			this.currentOffset += content.length();
		}
	}

	private void processGroup(final RTFElement groupRoot, final String groupName) throws BadLocationException, IOException {
		if (groupName.equals("\\fonttbl")) {
			this.processFontTable(groupRoot);
		} else if (groupName.equals("\\colortbl")) {
			this.processColorTable(groupRoot);
		} else if (groupName.equals("\\headerl")) {
			this.processEvenPageHeader(groupRoot);
		} else if (groupName.equals("\\headerf")) {
			this.processFirstPageHeader(groupRoot);
		} else if (groupName.equals("\\headerr")) {
			this.processOddPageHeader(groupRoot);
		} else if (groupName.equals("\\footerl")) {
			this.processEvenPageFooter(groupRoot);
		} else if (groupName.equals("\\footerr")) {
			this.processOddPageFooter(groupRoot);
		} else if (groupName.equals("\\footerf")) {
			this.processFirstPageFooter(groupRoot);
		} else if (groupName.equals("\\header")) {
			this.processCommonHeader(groupRoot);
		} else if (groupName.equals("\\footer")) {
			this.processCommonFooter(groupRoot);
		}
	}

	private void processFirstPageHeader(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument firstPageHeaderDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("first_page_header", firstPageHeaderDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, firstPageHeaderDocument);
	}

	private void processEvenPageHeader(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument evenPageHeaderDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("even_page_header", evenPageHeaderDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, evenPageHeaderDocument);
	}

	private void processOddPageHeader(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument oddPageHeaderDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("odd_page_header", oddPageHeaderDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, oddPageHeaderDocument);
	}

	private void processFirstPageFooter(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument firstPageFooterDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("first_page_footer", firstPageFooterDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, firstPageFooterDocument);
	}

	private void processEvenPageFooter(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument evenPageFooterDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("even_page_footer", evenPageFooterDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, evenPageFooterDocument);
	}

	private void processOddPageFooter(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument oddPageFooterDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("odd_page_footer", oddPageFooterDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, oddPageFooterDocument);
	}

	private void processCommonHeader(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument commonHeaderDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("common_header", commonHeaderDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, commonHeaderDocument);
	}

	private void processCommonFooter(final RTFElement headerRoot) throws BadLocationException, IOException {
		final com.ontimize.util.rtf.style.RTFDocument commonFooterDocument = new com.ontimize.util.rtf.style.RTFDocument();
		this.mainDocument.putProperty("common_footer", commonFooterDocument);
		this.currentOffset = 0;
		this.processDocumentBody(headerRoot, 0, commonFooterDocument);
	}

	private void processFontTable(final RTFElement fontTableRoot) {
		final int cnt = fontTableRoot.getChildCount();
		String fontKey = "0";
		for (int i = 1; i < cnt; i++) {
			final RTFElement fontNode = fontTableRoot.getChild(i);
			final String nodeName = fontNode.getName();
			final String nodeContent = (String) fontNode.getContent();
			if (nodeName.equals("*<group>*")) {
				this.processFont(fontNode);
			} else if (nodeName.equals("\\f")) {
				fontKey = Integer.toString(fontNode.getLength());
			}

			if (nodeContent != null) {
				final String fontName = nodeContent.substring(0, nodeContent.length() - 1).trim();
				this.fontMap.put(fontKey, fontName);
			}
		}
	}

	private void processFont(final RTFElement font) {
		final String fontKey = Integer.toString(font.getChild(0).getLength());
		int lastIndex = font.getChildCount() - 1;
		RTFElement lastChild = font.getChild(lastIndex);
		while ((lastIndex > 0) && (lastChild.getContent() == null)) {
			lastIndex--;
			lastChild = font.getChild(lastIndex);
		}
		if (lastChild.getContent() != null) {
			String fontName = (String) lastChild.getContent();
			fontName = fontName.substring(0, fontName.length() - 1).trim();
			this.fontMap.put(fontKey, fontName);
		}
	}

	private void processColorTableOld(final RTFElement colorTableRoot) {
		final int cnt = colorTableRoot.getChildCount();
		for (int i = 2; i < cnt; i += 4) {
			final RTFElement redNode = colorTableRoot.getChild(i);
			final RTFElement greenNode = colorTableRoot.getChild(i + 1);
			final RTFElement blueNode = colorTableRoot.getChild(i + 2);

			final int red = redNode.getLength();
			final int green = greenNode.getLength();
			final int blue = blueNode.getLength();

			final Color color = new Color(red, green, blue);
			this.colorTable.add(color);
		}
	}

	private void processColorTable(final RTFElement colorTableRoot) {
		final int cnt = colorTableRoot.getChildCount();
		for (int i = 2; (i + 2) < cnt; i += 4) {
			final RTFElement redNode = colorTableRoot.getChild(i);
			final RTFElement greenNode = colorTableRoot.getChild(i + 1);
			final RTFElement blueNode = colorTableRoot.getChild(i + 2);

			int red = redNode.getLength() < 0 ? 0 : redNode.getLength();
			if (red > 255) {
				red = 255;
			}
			int green = greenNode.getLength() < 0 ? 0 : greenNode.getLength();
			if (green > 255) {
				green = 255;
			}
			int blue = blueNode.getLength() < 0 ? 0 : blueNode.getLength();
			if (blue > 255) {
				blue = 255;
			}

			final Color color = new Color(red, green, blue);
			this.colorTable.add(color);
		}
	}

	private List retrieveRowAttributes(final int i, final RTFElement node) {
		final List rowsAttributes = new Vector();
		final String nodeName = node.getName();
		int rowCount = 0;
		int cellCount = 0;
		int colCount = 0;
		List cellAttributes = new Vector(65);
		// attr.addAttribute("cellAttributes", cellAttributes);
		// attr.addAttribute("cellCount", new Integer(cellCount));
		for (int j = i - 1; j >= 0; j--) {
			final String groupName = node.getParent().getChild(j).getName();
			if (groupName.indexOf("cellx") != -1) {
				cellCount++;
			}
			// if (groupName.equals("\\trowd")) {
			// rowCount++;
			// break;
			// }
			if (groupName.equals("\\row")) {
				rowCount++;
			}
		}
		colCount = cellCount / rowCount;
		for (int k = 0; k < rowCount; k++) {
			cellAttributes = new Vector();
			for (int l = 0; l < colCount; l++) {
				final MutableAttributeSet cellAttr = new SimpleAttributeSet();
				final BorderAttributes ba = new BorderAttributes();
				final Insets margins = new Insets(2, 2, 2, 2);
				ba.setBorders(63);
				// ba.lineColor = Color.black;
				// ba.borderTop = 1;
				// ba.borderLeft = 1;
				// ba.borderBottom = 1;
				// ba.borderRight = 1;
				cellAttr.addAttribute("BorderAttributes", ba);
				cellAttr.addAttribute("margins", margins);
				cellAttr.addAttribute("cellWidth", new Integer(288));
				cellAttributes.add(cellAttr);
			}
			final MutableAttributeSet rowAttr = new SimpleAttributeSet();
			rowAttr.addAttribute("cellAttributes", cellAttributes);
			rowAttr.addAttribute("cellCount", new Integer(colCount));
			rowsAttributes.add(rowAttr);
		}

		return rowsAttributes;
	}

	private int processTableDefinition(final RTFElement tableNode, final MutableAttributeSet attr, final int index, final int level,
			final com.ontimize.util.rtf.style.RTFDocument document) {
		int rowCount = 0;
		int colCount = 0;
		int cellCount = 0;
		// int deep = 0;

		List rowsAttributes = new Vector(100);

		int currentLevel = -1;
		final int cnt = tableNode.getParent().getChildCount();
		for (int i = index + 1; i < cnt; i++) {
			final RTFElement node = tableNode.getParent().getChild(i);
			final String nodeName = node.getName();
			RTFReader.logger.debug(nodeName);
			if (nodeName.equals("\\pard")) {
				currentLevel = this.getParagraphLevel(node, tableNode.getParent(), i);
				if (currentLevel >= level) {
					continue;
				}
				final MutableAttributeSet tableAttr = new SimpleAttributeSet();

				final BorderAttributes ba = new BorderAttributes();
				ba.setBorders(63);
				tableAttr.addAttribute("BorderAttributes", ba);
				if (rowsAttributes.isEmpty()) {
					rowsAttributes = this.retrieveRowAttributes(cnt, node);
					rowCount = rowsAttributes.size();
					colCount = Integer
							.parseInt(((SimpleAttributeSet) rowsAttributes.get(0)).getAttribute("cellCount").toString());
				}
				if (rowsAttributes.size() > 0) {
					MutableAttributeSet rowAttr = this.getLongestRowAttribute(rowsAttributes);
					StyleConstants.setAlignment(tableAttr, StyleConstants.getAlignment(rowAttr));
					List cellAttributes = (List) rowAttr.getAttribute("cellAttributes");
					final int[] widths = new int[cellAttributes.size()];
					for (int j = 0; j < widths.length; j++) {
						final MutableAttributeSet cellAttr = (MutableAttributeSet) cellAttributes.get(j);
						final Integer cw = (Integer) cellAttr.getAttribute("cellWidth");
						if (cw != null) {
							widths[j] = cw.intValue();
						} else if (this.getWidthFromPosition(cellAttributes, j) > 0) {
							widths[j] = this.getWidthFromPosition(cellAttributes, j);
						} else {
							widths[j] = document.DOCUMENT_WIDTH / widths.length;
						}
					}

					final int[] heights = new int[rowCount];
					for (int j = 0; j < rowCount; j++) {
						heights[j] = 1;
					}

					final Element table = document.insertTable(this.currentOffset, rowCount, colCount, tableAttr, widths,
							heights);
					this.currentOffset = table.getStartOffset();

					for (int j = 0; j < rowCount; j++) {
						rowAttr = (MutableAttributeSet) rowsAttributes.get(j);
						cellAttributes = (List) rowAttr.getAttribute("cellAttributes");
						for (int k = 0; k < colCount; k++) {
							if (cellAttributes.size() >= colCount) {
								final MutableAttributeSet cellAttr = (MutableAttributeSet) cellAttributes.get(k);

								final com.ontimize.util.rtf.style.RTFDocument.CellElement cell = (com.ontimize.util.rtf.style.RTFDocument.CellElement) table
										.getElement(j)
										.getElement(k);
								final BorderAttributes bas = (BorderAttributes) cellAttr.getAttribute("BorderAttributes");
								((BorderAttributes) cell.getAttribute("BorderAttributes")).setBorders(bas.getBorders());
								((BorderAttributes) cell.getAttribute("BorderAttributes")).lineColor = bas.lineColor;
								final Insets margins = (Insets) cellAttr.getAttribute("margins");
								cell.setMargins(margins);
							}
						}
					}
				}
				return i - 1;
			}

			if (nodeName.equals("*<group>*")) {
				for (int j = 0; j < node.getChildCount(); j++) {
					final String groupName = node.getChild(j).getName();
					if (groupName.equals("\\trowd") && (level == currentLevel)) {
						rowCount++;

						final MutableAttributeSet rowAttr = new SimpleAttributeSet();
						this.processRowAttributes(node, rowAttr, 1);
						cellCount = ((Integer) rowAttr.getAttribute("cellCount")).intValue();
						if (colCount < cellCount) {
							colCount = cellCount;
						}
						rowsAttributes.add(rowAttr);
					} else if (groupName.equals("\\nesttableprops") && (level == currentLevel)) {
						final RTFElement nestedNode = node.getChild(2);
						rowCount++;

						final MutableAttributeSet rowAttr = new SimpleAttributeSet();

						this.processRowAttributes(nestedNode, rowAttr, 1);
						cellCount = ((Integer) rowAttr.getAttribute("cellCount")).intValue();
						if (colCount < cellCount) {
							colCount = cellCount;
						}
						rowsAttributes.add(rowAttr);
					} else if (groupName.equals("*<group>*")) {
						for (int i1 = 0; i1 < node.getChildCount(); i1++) {
							final RTFElement nn = node.getChild(i1);
							if (!this.getGroupName(nn).equals("\\nesttableprops") || (level != currentLevel)) {
								continue;
							}
							rowCount++;

							final MutableAttributeSet rowAttr = new SimpleAttributeSet();

							this.processRowAttributes(nn, rowAttr, 1);
							cellCount = ((Integer) rowAttr.getAttribute("cellCount")).intValue();
							if (colCount < cellCount) {
								colCount = cellCount;
							}
							rowsAttributes.add(rowAttr);
							i1 = node.getChildCount();
							j = node.getChildCount();
						}
					}
				}
			}
		}

		return index;
	}

	private MutableAttributeSet getLongestRowAttribute(final List rowsAttributes) {
		int maxValue = 0;
		int index = 0;
		for (int i = 0; i < rowsAttributes.size(); i++) {
			final MutableAttributeSet rowAttr = (MutableAttributeSet) rowsAttributes.get(i);
			final int currentValue = ((List) rowAttr.getAttribute("cellAttributes")).size();
			if (currentValue > maxValue) {
				maxValue = currentValue;
				index = i;
			}
		}
		return (MutableAttributeSet) rowsAttributes.get(index);
	}

	protected int getWidthFromPosition(final List cellAttributes, final int col) {
		int w = -1;
		if (col < (cellAttributes.size() - 1)) {
			final MutableAttributeSet cellAttr = (MutableAttributeSet) cellAttributes.get(col);
			final MutableAttributeSet nextCellAttr = (MutableAttributeSet) cellAttributes.get(col + 1);
			if ((cellAttr.getAttribute("cellx") != null) && (nextCellAttr.getAttribute("cellx") != null)) {
				final int cellX = ((Integer) cellAttr.getAttribute("cellx")).intValue();
				final int nextCellX = ((Integer) nextCellAttr.getAttribute("cellx")).intValue();
				w = nextCellX - cellX;
			}
		}
		return w;
	}

	private void processRowAttributes(final RTFElement row, final MutableAttributeSet attr, final int index) {
		final int cnt = row.getChildCount();
		final List cellAttributes = new Vector(65);
		int cellCount = 0;
		for (int i = index; i < cnt; i++) {
			final RTFElement node = row.getChild(i);
			final String nodeName = node.getName();
			if (nodeName.equals("\\tleft-")) {
				StyleConstants.setLeftIndent(attr, node.getLength());
			} else if (nodeName.equals("\\trqr")) {
				StyleConstants.setAlignment(attr, 2);
			} else if (nodeName.equals("\\trql")) {
				StyleConstants.setAlignment(attr, 0);
			} else if (nodeName.equals("\\trqc")) {
				StyleConstants.setAlignment(attr, 1);
			} else {
				if (nodeName.equals("\\row")) {
					attr.addAttribute("cellAttributes", cellAttributes);
					attr.addAttribute("cellCount", new Integer(cellCount));
					return;
				}
				if (nodeName.equals("\\nestrow")) {
					attr.addAttribute("cellAttributes", cellAttributes);
					attr.addAttribute("cellCount", new Integer(cellCount));
					return;
				}
				if (nodeName.equals("\\clvertalt")) {
					cellCount++;
					final MutableAttributeSet cellAttr = new SimpleAttributeSet();
					this.processCellAttributes(node, cellAttr, i);
					cellAttributes.add(cellAttr);
				} else if (nodeName.equals("\\clvertalc")) {
					cellCount++;
					final MutableAttributeSet cellAttr = new SimpleAttributeSet();
					this.processCellAttributes(node, cellAttr, i);
					cellAttributes.add(cellAttr);
				}
			}
		}
		attr.addAttribute("cellAttributes", cellAttributes);
		attr.addAttribute("cellCount", new Integer(cellCount));
	}

	private void processCellAttributes(final RTFElement startCellNode, final MutableAttributeSet attr, final int index) {
		final int cnt = startCellNode.getParent().getChildCount();
		final BorderAttributes ba = new BorderAttributes();
		final Insets margins = new Insets(2, 2, 2, 2);
		ba.setBorders(0);
		for (int i = index; i < cnt; i++) {
			final RTFElement node = startCellNode.getParent().getChild(i);
			final String nodeName = node.getName();
			if (nodeName.equals("\\brdrcf")) {
				final int colorInd = node.getLength();
				ba.lineColor = (Color) this.colorTable.get(colorInd);
			} else if (nodeName.equals("\\clbrdrt")) {
				ba.borderTop = 1;
			} else if (nodeName.equals("\\clbrdrl")) {
				ba.borderLeft = 1;
			} else if (nodeName.equals("\\clbrdrb")) {
				ba.borderBottom = 1;
			} else if (nodeName.equals("\\clbrdrr")) {
				ba.borderRight = 1;
			} else if (nodeName.equals("\\clpadl")) {
				margins.left = node.getLength() / 15;
			} else if (nodeName.equals("\\clpadr")) {
				margins.right = node.getLength() / 15;
			} else if (nodeName.equals("\\clpadt")) {
				margins.top = node.getLength() / 15;
			} else if (nodeName.equals("\\clpadb")) {
				margins.bottom = node.getLength() / 15;
			} else if (nodeName.equals("\\clwWidth")) {
				int width = node.getLength();
				width = this.convertTwipsToPixels(width);
				attr.addAttribute("cellWidth", new Integer(width));
			} else if (nodeName.equals("\\cellx")) {
				attr.addAttribute("BorderAttributes", ba);
				attr.addAttribute("margins", margins);
				if (!attr.isDefined("cellWidth")) {
					int x = node.getLength();
					x = this.convertTwipsToPixels(x);
					attr.addAttribute("cellx", new Integer(x));
				}

				return;
			}
		}
	}

	private void processTableContent(final RTFElement tableNode, final MutableAttributeSet attributes, final int startIndex, final int endIndex,
			final int level,
			final com.ontimize.util.rtf.style.RTFDocument document) throws BadLocationException, IOException {
		MutableAttributeSet attr = new SimpleAttributeSet(attributes);
		String previousName = "";
		for (int i = startIndex; i < endIndex; i++) {
			final RTFElement node = tableNode.getParent().getChild(i);
			final String nodeName = node.getName();
			Object nodeContent = node.getContent();
			RTFReader.logger.debug(nodeName);
			RTFReader.logger.debug("{}", nodeContent != null ? nodeContent : "");
			if (nodeName.equals("\\pard")) {
				attr = new SimpleAttributeSet();
			} else if (nodeName.equals("\\tab")) {
				document.insertString(this.currentOffset, "\t", attr);
				this.currentOffset += 1;
				nodeContent = null;
			} else if (nodeName.equals("\\page")) {
				processPage(document, attr);
				nodeContent = null;
			} else if (nodeName.equals("\\cell")) {
				this.currentOffset += 1;
				this.cellread = true;
			} else if (nodeName.equals("\\nestcell")) {
				this.currentOffset += 1;
			} else if (nodeName.equals("*<group>*")) {
				if (!this.getGroupName(node).equals("\\nonesttables")) {

					this.processBodyGroup(node, attr, document, null);
				}
			} else if (nodeName.equals("\\par")) {
				nodeContent = this.processParBodyGroup(document, attr);
			} else if (nodeName.equals("\\intbl")) {
				final int innerLevel = this.getParagraphLevel(node, node.getParent(), i);
				if (innerLevel > level) {
					final int endOfTableInd = this.processTableDefinition(node, attr, i, level + 1, document);
					this.processTableContent(node, attr, i, endOfTableInd, level + 1, document);
					i = endOfTableInd + 1;
				}
			} else {
				this.processAttribute(node, attr);
			}

			if (nodeContent != null) {
				final String content = (String) nodeContent;
				if ((i != 0) && !previousName.equals("*<group>*") && (content.charAt(0) == ' ')) {
					if (content.substring(1).length() > 0) {
						document.insertString(this.currentOffset, content.substring(1), attr);
					}
					this.currentOffset += content.length() - 1;
				} else {
					document.insertString(this.currentOffset, content, attr);
					this.currentOffset += content.length();
				}
			}
			previousName = nodeName;
		}
	}

	private int getParagraphLevel(final RTFElement par, final RTFElement parent, final int index) {
		int result = 0;
		final int cnt = parent.getChildCount();
		if (par.getName().equals("\\intbl")) {
			result = 1;
		}
		for (int i = index + 1; i < cnt; i++) {
			final RTFElement node = parent.getChild(i);
			final String nodeName = node.getName();

			if (nodeName.equals("\\pard")) {
				return result;
			}
			if (nodeName.equals("\\intbl")) {
				result = 1;
			} else {
				if (!nodeName.equals("\\itap")) {
					continue;
				}
				final int level = node.getLength();
				if (level >= 0) {
					return level;
				}

				return result;
			}
		}

		return result;
	}

	private String getGroupName(final RTFElement groupNode) {
		if (groupNode.getChildCount() == 0) {
			return "";
		}
		RTFElement node = groupNode.getChild(0);
		if (node.getName().equals("\\*")) {
			node = groupNode.getChild(1);
		}
		return node.getName();
	}

	protected int convertTwipsToPixels(final int value) {
		double result = value;

		result /= 15.0D;

		return (int) result;
	}

	protected void processImage(final RTFElement pictureGroup, final com.ontimize.util.rtf.style.RTFDocument document)
			throws IOException, BadLocationException {
		final int cnt = pictureGroup.getChildCount();
		final StringBuilder imageText = null;
		int imageType = -1;
		StringBuilder imageContent = null;
		String prevNodeName = "";
		for (int i = 0; i < cnt; i++) {
			final RTFElement node = pictureGroup.getChild(i);

			if (node.getName().equals("\\pngblip")) {
				imageType = 0;
			}

			final String content = (String) node.getContent();
			if (content != null) {
				if (imageContent == null) {
					if (prevNodeName.equals("*<group>*")) {
						imageContent = new StringBuilder(content);
					} else {
						imageContent = new StringBuilder(content.substring(0));
					}
				} else {
					imageContent.append(content);
				}
			}
			prevNodeName = node.getName();
		}
		if (imageContent.charAt(0) != ' ') {
			this.insertImage(imageContent.toString(), document);
		} else {
			this.insertImage(imageContent.toString().substring(1), document);
		}
	}

	protected void insertImage(final String content, final com.ontimize.util.rtf.style.RTFDocument document)
			throws IOException, BadLocationException {
		final int len = content.length();

		final byte[] buf = new byte[len / 2];
		for (int j = 0; j < (len / 2); j++) {
			final byte b1 = this.get16(content.charAt(j * 2));
			final byte b2 = this.get16(content.charAt((j * 2) + 1));
			buf[j] = (byte) ((b1 * 16) + b2);
		}

		final Image img = Toolkit.getDefaultToolkit().createImage(buf);

		final ImageIcon icon = new ImageIcon();
		icon.setImage(img);

		if ((icon.getIconHeight() < 0) || (icon.getIconWidth() < 0)) {
			return;
		}
		final MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setIcon(attr, icon);

		document.insertString(this.currentOffset, " ", attr);
		this.currentOffset += 1;
	}

	private byte get16(final char c) {
		if (this.isNon16(c)) {
			return 0;
		}
		byte result;
		switch (c) {
		case '0':
			result = 0;
			break;
		case '1':
			result = 1;
			break;
		case '2':
			result = 2;
			break;
		case '3':
			result = 3;
			break;
		case '4':
			result = 4;
			break;
		case '5':
			result = 5;
			break;
		case '6':
			result = 6;
			break;
		case '7':
			result = 7;
			break;
		case '8':
			result = 8;
			break;
		case '9':
			result = 9;
			break;
		case 'a':
			result = 10;
			break;
		case 'b':
			result = 11;
			break;
		case 'c':
			result = 12;
			break;
		case 'd':
			result = 13;
			break;
		case 'e':
			result = 14;
			break;
		case 'f':
			result = 15;
			break;
		default:
			result = 0;
			break;
		}
		return result;
	}

	private boolean isNon16(final char c) {

		if (this.isLetter(c)) {
			return true;
		}
		boolean result;
		switch (c) {
		case ':':
		case ';':
		case '<':
		case '=':
		case '>':
		case '?':
		case '@':
		case '[':
		case '\\':
		case ']':
		case '^':
		case '_':
		case '`':
			result = true;
			break;
		default:
			result = false;
			break;
		}
		return result;
	}

	private boolean isLetter(final char c) {
		boolean result;
		switch (c) {
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	protected void removeCharacterAttributes(final MutableAttributeSet attrs) {
		attrs.removeAttribute(StyleConstants.Background);
		attrs.removeAttribute(StyleConstants.Foreground);
		attrs.removeAttribute(StyleConstants.Bold);
		attrs.removeAttribute(StyleConstants.Italic);
		attrs.removeAttribute(StyleConstants.Subscript);
		attrs.removeAttribute(StyleConstants.Superscript);
		attrs.removeAttribute(StyleConstants.StrikeThrough);
		attrs.removeAttribute(StyleConstants.FontSize);
		attrs.removeAttribute(StyleConstants.FontFamily);
	}

}
