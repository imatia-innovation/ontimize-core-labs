package com.ontimize.util.rtf;

import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.tree.TreeNode;

import com.ontimize.util.rtf.style.RTFDocument;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codecimpl.PNGCodec;

public class RTFGenerator {

	protected Document document;

	protected List fontList;

	protected List colorList;

	public RTFGenerator(final Document doc) {
		this.document = doc;
	}

	public void write(final String fileName) throws IOException {
		final FileWriter out = new FileWriter(fileName);
		this.write(out, 0, this.document.getLength());
		out.close();
	}

	public void write(final Writer out, final int pos, final int len) throws IOException {
		Element root = this.getDocumentTree();
		final JTree tree = new JTree((TreeNode) root);

		this.fontList = new Vector();
		this.fontList.add("Arial");
		this.fontList.add("Wingdings");
		this.fontList = this.getFontList(root, this.fontList);
		this.colorList = this.getDefaultColorList();
		this.colorList = this.getColorList(root, this.colorList);

		out.write("{\\rtf1\\ansi\\ansicpg1252");
		out.write(this.createFontTable(this.fontList));
		out.write(this.createColorTable(this.colorList));

		Element el = root.getElement(root.getElementIndex(pos));
		while (el.getName().equals("table")) {
			final Element table = el;
			final Element row = table.getElement(table.getElementIndex(pos));
			final Element cell = row.getElement(row.getElementIndex(pos));
			if (cell.getEndOffset() < (pos + len)) {
				break;
			}
			root = cell;
			el = root.getElement(root.getElementIndex(pos));
		}

		this.writeContent(root, out, 0, pos, len);
		out.write("}");
		out.flush();
	}

	protected void writeContent(final Element root, final Writer out, final int level, final int pos, final int len) throws IOException {
		final int elCount = root.getElementCount();
		final int startIndex = root.getElementIndex(pos);
		final int endIndex = root.getElementIndex(pos + len);
		for (int i = startIndex; i <= endIndex; i++) {
			final Element child = root.getElement(i);

			if (child.getStartOffset() > (pos + len)) {
				return;
			}
			if (child.getName().equals("paragraph")) {
				if ((level > 0) && (i == (elCount - 1))) {
					this.writeParagraph(child, out, level, true, pos, len);
				} else {
					this.writeParagraph(child, out, level, false, pos, len);
				}
			} else if (child.getName().equals("table")) {
				this.writeTable(child, out, level);
				if ((i == endIndex) && (level > 0)) {
					out.write("\\pard\\intbl");
					if (level > 1) {
						out.write("\\itap" + Integer.toString(level));
					}
				}
			}
		}
	}

	protected void writeParagraph(final Element paragraph, final Writer out, final int level, final boolean lastInTable, final int pos, final int len)
			throws IOException {
		out.write("\\pard ");
		final int ind = 0;
		final int elCount = paragraph.getElementCount();
		out.write(this.getParagraphDescription(paragraph.getAttributes()));
		if (level > 0) {
			out.write("\\intbl");
		}
		out.write("\\itap" + Integer.toString(level));
		final int startIndex = paragraph.getElementIndex(pos);
		final int endIndex = paragraph.getElementIndex(pos + len);
		for (int i = startIndex; i <= endIndex; i++) {
			final Element leaf = paragraph.getElement(i);
			if (leaf.getName().equals("content")) {
				this.writeLeaf(leaf, out, pos, len);
			} else if (leaf.getName().equals("icon")) {
				this.writeIcon(leaf, out);
			}
		}
		if (!lastInTable) {
			out.write("\\par");
		}
	}

	protected void writeLeaf(final Element leaf, final Writer out, final int pos, final int len) throws IOException {
		final AttributeSet attr = leaf.getAttributes();
		final Document doc = leaf.getDocument();
		String contentText = "";
		try {
			final int start = Math.max(leaf.getStartOffset(), pos);
			final int end = Math.min(leaf.getEndOffset(), pos + len) - start;
			contentText = this.convertString(doc.getText(start, end));
		} catch (final Exception ex) {
			throw new IOException("Error reading leaf content from source document!", ex);
		}
		if (contentText.length() <= 0) {
			return;
		}
		out.write(this.getBeforeFontDescription(attr, false) + " ");
		out.write(contentText);
		final String after = this.getAfterFontDescription(leaf.getAttributes());
		if (after.length() > 0) {
			out.write(this.getAfterFontDescription(leaf.getAttributes()));
		}
	}

	protected void writeIcon(final Element leaf, final Writer out) throws IOException {
		final AttributeSet attr = leaf.getAttributes();
		final ImageIcon icon = (ImageIcon) StyleConstants.getIcon(attr);
		final int w = StyleConstants.getIcon(attr).getIconWidth();
		final int h = StyleConstants.getIcon(attr).getIconHeight();
		if (icon != null) {
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final PNGCodec p = new PNGCodec();
			final ImageEncoder pe = ImageCodec.createImageEncoder("PNG", os, null);
			final BufferedImage bi = new BufferedImage(w, h, 2);
			bi.getGraphics().drawImage(icon.getImage(), 0, 0, null);
			pe.encode(bi);
			final byte[] ba = os.toByteArray();

			final int len = ba.length;
			final StringBuilder sb = new StringBuilder(len * 2);
			for (int i = 0; i < len; i++) {
				final String sByte = Integer.toHexString(ba[i] & 0xFF);
				if (sByte.length() < 2) {
					sb.append('0' + sByte);
				} else {
					sb.append(sByte);
				}
			}
			final String s = sb.toString();
			final String size = Integer.toString(s.length());
			out.write("{\\pict\\pngblip ");
			out.write(s);
			out.write("}");
		}
	}

	protected void writeTable(final Element table, final Writer out, final int level) throws IOException {
		final int rowCount = table.getElementCount();
		for (int i = 0; i < rowCount; i++) {
			final Element row = table.getElement(i);
			this.writeRow(row, out, level);
		}
	}

	protected void writeRow(final Element row, final Writer out, final int level) throws IOException {
		for (int i = 0; i < row.getElementCount(); i++) {
			this.writeCellContent(row.getElement(i), out, level);
		}

		if (level > 0) {
			out.write("{\\*\\nesttableprops");
		}
		out.write("{\\trowd");
		final Element table = row.getParentElement();
		final AttributeSet tableAttr = table.getAttributes();
		switch (StyleConstants.getAlignment(tableAttr)) {
		case 0:
			out.write("\\trql");
			break;
		case 2:
			out.write("\\trqr");
			break;
		case 1:
			out.write("\\trqc");
		}

		final AttributeSet attr = row.getAttributes();
		final int indent = new Float(StyleConstants.getLeftIndent(attr)).intValue();
		out.write("\\tleft-" + Integer.toString(indent));

		out.write("\\trftsWidth1");
		out.write("\\trpaddl108");
		out.write("\\trpaddr108");
		out.write("\\trpaddfl3");
		out.write("\\trpaddfr3 ");
		int x = 1;
		for (int i = 0; i < row.getElementCount(); i++) {
			final RTFDocument.CellElement cell = (RTFDocument.CellElement) row.getElement(i);
			final Double dX = this.convertPixelsToTwips(new Double(x));
			this.writeCell(row.getElement(i), out, dX.intValue(), level);
			x += cell.getWidth();
		}
		if (level == 0) {
			out.write("\\row}");
		} else {
			out.write("\\nestrow}}");
		}
	}

	protected void writeCellContent(final Element cell, final Writer out, final int level) throws IOException {
		this.writeContent(cell, out, level + 1, cell.getStartOffset(), cell.getEndOffset() - cell.getStartOffset());
		if (level == 0) {
			out.write("\\cell");
		} else {
			out.write("\\nestcell{\\nonesttables\\par }");
		}
	}

	protected void writeCell(final Element cell, final Writer out, final int x, final int level) throws IOException {
		out.write("\\clvertalt");
		final AttributeSet attr = cell.getAttributes();
		final RTFDocument.CellElement currentCell = (RTFDocument.CellElement) cell;

		final BorderAttributes ba = (BorderAttributes) attr.getAttribute("BorderAttributes");
		if (ba != null) {
			final String borderType = "\\brdrs";

			final Color bc = ba.lineColor;
			String lineColor = "";
			if (bc != Color.black) {
				lineColor = "\\brdrcf" + (this.colorList.indexOf(bc) + 1);
			}
			if (ba.borderTop != 0) {
				out.write("\\clbrdrt" + borderType + "\\brdrw10" + lineColor);
			}
			if (ba.borderLeft != 0) {
				out.write("\\clbrdrl" + borderType + "\\brdrw10" + lineColor);
			}
			if (ba.borderBottom != 0) {
				out.write("\\clbrdrb" + borderType + "\\brdrw10" + lineColor);
			}
			if (ba.borderRight != 0) {
				out.write("\\clbrdrr" + borderType + "\\brdrw10" + lineColor);
			}
			final Insets margins = currentCell.getMargins();
			out.write("\\clpadl" + Integer.toString(margins.left * 15));
			out.write("\\clpadr" + Integer.toString(margins.right * 15));
			out.write("\\clpadt" + Integer.toString(margins.top * 15));
			out.write("\\clpadb" + Integer.toString(margins.bottom * 15));
			out.write("\\clpadfl3");
			out.write("\\clpadfr3");
			out.write("\\clpadft3");
			out.write("\\clpadfb3");
			out.write("\\clftsWidth3");
		}
		final Double dWidth = this.convertPixelsToTwips(new Double(currentCell.getWidth()));
		out.write("\\clwWidth" + Integer.toString(dWidth.intValue()));
		out.write("\\cellx" + Integer.toString(x) + " ");
	}

	protected Element getDocumentTree() {
		final StyledDocument doc = (StyledDocument) this.document;
		return doc.getDefaultRootElement();
	}

	protected List getFontList(final Element root, List list) {
		final AttributeSet attr = root.getAttributes();
		final String curFontName = StyleConstants.getFontFamily(attr);
		if (!this.isInList(list, curFontName)) {
			list.add(curFontName);
		}

		final int cnt = root.getElementCount();
		for (int i = 0; i < cnt; i++) {
			final Element el = root.getElement(i);
			list = this.getFontList(el, list);
		}
		return list;
	}

	protected boolean isInList(final List list, final Object fontName) {
		final int len = list.size();
		for (int i = 0; i < len; i++) {
			if (fontName.equals(list.get(i))) {
				return true;
			}
		}
		return false;
	}

	protected String createFontTable(final List fontList) {
		String result = "";
		int fontN = 0;
		result = result + "{\\fonttbl";
		final int len = fontList.size();
		for (int i = 0; i < len; i++) {
			result = result + "{\\f" + new Integer(fontN).toString();
			fontN++;
			result = result + "\\fnil\\fcharset1\\fprq2 ";
			result = result + (String) fontList.get(i) + ";}";
		}
		result = result + "}";
		return result;
	}

	protected List getDefaultColorList() {
		final List result = new Vector();
		final int[] values = { 0, 128, 192, 255 };
		for (int r = 0; r < values.length; r++) {
			for (int g = 0; g < values.length; g++) {
				for (int b = 0; b < values.length; b++) {
					final Color c = new Color(values[r], values[g], values[b]);
					result.add(c);
				}
			}
		}
		return result;
	}

	protected List getColorList(final Element root, List list) {
		final AttributeSet attr = root.getAttributes();
		final Color bgColor = StyleConstants.getBackground(attr);
		if (!this.isInList(list, bgColor)) {
			list.add(bgColor);
		}
		final Color fgColor = StyleConstants.getForeground(attr);
		if (!this.isInList(list, fgColor)) {
			list.add(fgColor);
		}
		final BorderAttributes ba = (BorderAttributes) attr.getAttribute("BorderAttributes");
		if ((ba != null) && !this.isInList(list, ba.lineColor)) {
			list.add(ba.lineColor);
		}

		final int cnt = root.getElementCount();
		for (int i = 0; i < cnt; i++) {
			final Element el = root.getElement(i);
			list = this.getColorList(el, list);
		}
		return list;
	}

	protected String createColorTable(final List colorList) {
		String result = "";

		result = result + "{\\colortbl;";
		final int len = colorList.size();
		for (int i = 0; i < len; i++) {
			final Color c = (Color) colorList.get(i);
			final int red = c.getRed();
			final int green = c.getGreen();
			final int blue = c.getBlue();
			result = result + "\\red" + new Integer(red).toString();
			result = result + "\\green" + new Integer(green).toString();
			result = result + "\\blue" + new Integer(blue).toString() + ";";
		}
		result = result + "}";
		return result;
	}

	protected String getBeforeFontDescription(final AttributeSet attr, final boolean isStyle) {
		String result = "";

		if (StyleConstants.isItalic(attr)) {
			result = result + "\\i";
		}
		if (StyleConstants.isUnderline(attr)) {
			result = result + "\\ul";
		}
		if (StyleConstants.isStrikeThrough(attr)) {
			result = result + "\\strike";
		}
		if (StyleConstants.isSubscript(attr)) {
			result = result + "\\sub";
		}
		result = result + "\\f" + this.fontList.indexOf(StyleConstants.getFontFamily(attr));
		result = result + "\\fs" + new Integer(StyleConstants.getFontSize(attr) * 2).toString();

		boolean openSubgroup = false;
		if (StyleConstants.isBold(attr)) {
			result = result + "{\\b";
			openSubgroup = true;
		}
		if (StyleConstants.isSuperscript(attr)) {
			if (!openSubgroup) {
				result = result + "{";
			}
			result = result + "\\super";
			openSubgroup = true;
		}
		final Color fg = (Color) attr.getAttribute(StyleConstants.Foreground);
		if (fg != null) {
			if (!isStyle && !openSubgroup) {
				result = result + "{";
			}
			result = result + "\\cf" + (this.colorList.indexOf(fg) + 1);
		}

		final Color bg = (Color) attr.getAttribute(StyleConstants.Background);
		if (bg != null) {
			if (!isStyle) {
				result = result + "{";
			}
			result = result + "\\highlight" + (this.colorList.indexOf(bg) + 1);
		}
		return result;
	}

	protected String getAfterFontDescription(final AttributeSet attr) {
		String result = "";

		final Color bg = (Color) attr.getAttribute(StyleConstants.Background);
		boolean openSubgroup = false;
		if (bg != null) {
			result = result + "}";
			openSubgroup = true;
		}
		final Color fg = (Color) attr.getAttribute(StyleConstants.Foreground);
		if (fg != null) {
			result = result + "}";
			openSubgroup = true;
		}
		if (StyleConstants.isSuperscript(attr) && !openSubgroup) {
			result = result + "} ";
		}

		if (StyleConstants.isBold(attr) && !openSubgroup) {
			result = result + "} ";
		}

		if (StyleConstants.isItalic(attr)) {
			result = result + "\\i0";
		}
		if (StyleConstants.isUnderline(attr)) {
			result = result + "\\ulnone";
		}
		if (StyleConstants.isStrikeThrough(attr)) {
			result = result + "\\strike0";
		}
		if (StyleConstants.isSubscript(attr)) {
			result = result + "\\sub0";
		}
		return result;
	}

	protected String getParagraphDescription(final AttributeSet attr) {
		String result = "";
		StyleConstants.getIcon(attr);

		switch (StyleConstants.getAlignment(attr)) {
		case 0:
			result = result + "\\ql ";
			break;
		case 2:
			result = result + "\\qr ";
			break;
		case 1:
			result = result + "\\qc ";
			break;
		case 3:
			result = result + "\\qj ";
		}

		final TabSet ts = StyleConstants.getTabSet(attr);
		if (ts != null) {
			for (int i = 0; i < ts.getTabCount(); i++) {
				final TabStop stop = ts.getTab(i);
				Double f = new Double(stop.getPosition());
				f = this.convertPixelsToTwips(f);
				result = result + "\\tx" + new Integer(f.intValue()).toString();
			}
		}
		if (Float.compare(StyleConstants.getLeftIndent(attr), 0) != 0) {
			Double f = new Double(StyleConstants.getLeftIndent(attr));
			f = this.convertPixelsToTwips(f);
			result = result + "\\li" + new Integer(f.intValue()).toString();
		} else {
			result = result + "\\li0";
		}
		if (Float.compare(StyleConstants.getRightIndent(attr), 0) != 0) {
			Double f = new Double(StyleConstants.getRightIndent(attr));
			f = this.convertPixelsToTwips(f);
			result = result + "\\ri" + new Integer(f.intValue()).toString();
		} else {
			result = result + "\\ri0";
		}
		if (Float.compare(StyleConstants.getFirstLineIndent(attr), 0) != 0) {
			Double f = new Double(StyleConstants.getFirstLineIndent(attr));
			f = this.convertPixelsToTwips(f);
			result = result + "\\fi" + new Integer(f.intValue()).toString();
		} else {
			result = result + "\\fi0";
		}

		if (Float.compare(StyleConstants.getSpaceAbove(attr), 0) != 0) {
			Double f = new Double(StyleConstants.getSpaceAbove(attr));
			f = this.convertPixelsToTwips(f);
			result = result + "\\sa" + new Integer(f.intValue()).toString();
		} else {
			result = result + "\\sa0";
		}
		if (Float.compare(StyleConstants.getSpaceBelow(attr), 0) != 0) {
			Double f = new Double(StyleConstants.getSpaceBelow(attr));
			f = this.convertPixelsToTwips(f);
			result = result + "\\sb" + new Integer(f.intValue()).toString();
		} else {
			result = result + "\\sb0";
		}
		if (Float.compare(StyleConstants.getLineSpacing(attr), 0.0F) != 0) {
			double spacing = StyleConstants.getLineSpacing(attr);
			if (spacing < 1.0D) {
				spacing = 1.0D;
			}
			spacing *= 240.0D;
			result = result + "\\sl" + new Integer(new Double(spacing).intValue()).toString() + "\\slmult1";
		} else {
			result = result + "\\sl240";
		}
		return result;
	}

	protected Double convertPixelsToTwips(final Double value) {
		double result = value.doubleValue();

		result *= 15.0D;
		return new Double(result);
	}

	protected String convertString(String source) {
		String dest = "";
		int i = 0;
		int index = source.indexOf('\\', i);
		while (index >= 0) {
			dest = dest + source.substring(i, index + 1) + '\\';
			i = index + 1;
			index = source.indexOf('\\', i);
		}
		dest = dest + source.substring(i);
		source = dest;
		dest = "";

		i = 0;
		index = source.indexOf('\t', i);
		while (index >= 0) {
			dest = dest + source.substring(i, index) + '\\' + "tab ";
			i = index + 1;
			index = source.indexOf('\t', i);
		}
		dest = dest + source.substring(i);

		i = 0;
		String src = dest;
		dest = "";
		index = src.indexOf('{', i);
		while (index >= 0) {
			dest = dest + src.substring(i, index) + '\\' + '{';
			i = index + 1;
			index = src.indexOf('{', i);
		}
		dest = dest + src.substring(i);
		i = 0;

		src = dest;
		dest = "";
		index = src.indexOf('}', i);
		while (index >= 0) {
			dest = dest + src.substring(i, index) + '\\' + '}';
			i = index + 1;
			index = src.indexOf('}', i);
		}
		dest = dest + src.substring(i);

		i = 0;
		src = dest;
		dest = "";
		index = src.indexOf("\f", i);
		while (index >= 0) {
			dest = dest + src.substring(i, index) + '\\' + "page";
			i = index + 1;
			index = src.indexOf('\f', i);
		}
		dest = dest + src.substring(i);

		return dest;
	}

}
