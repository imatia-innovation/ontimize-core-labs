package com.ontimize.gui.field.html;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.ontimize.gui.field.html.utils.ElementWriter;

/**
 * A collection of static convenience methods for working with HTML, HTMLDocuments, AttributeSets
 * and Elements from HTML documents.
 *
 * @author Imatia S.L.
 *
 */
public class HTMLUtils {

    private static final Logger logger = LoggerFactory.getLogger(HTMLUtils.class);

    protected static final Tidy tidy = new Tidy();
    static {
        HTMLUtils.tidy.setQuiet(true);
        HTMLUtils.tidy.setShowWarnings(false);
        HTMLUtils.tidy.setForceOutput(true);
        HTMLUtils.tidy.setFixComments(true);
        HTMLUtils.tidy.setFixUri(true);
        HTMLUtils.tidy.setDropEmptyParas(true);
        HTMLUtils.tidy.setLiteralAttribs(true);
        HTMLUtils.tidy.setTrimEmptyElements(true);
        HTMLUtils.tidy.setXHTML(true);
        // tidy.setInputEncoding("UTF-16");
        // tidy.setOutputEncoding("UTF-16");
    }

    /**
     * Tests if an element is an implied paragraph (p-implied)
     * @param el The element
     * @return true if the elements name equals "p-implied", false otherwise
     */
    public static boolean isImplied(final Element el) {
        return el.getName().equals("p-implied");
    }

    /**
     * Incloses a chunk of HTML text in the specified tag
     * @param enclTag the tag to enclose the HTML in
     * @param innerHTML the HTML to be inclosed
     * @return
     */
    public static String createTag(final HTML.Tag enclTag, final String innerHTML) {
        return HTMLUtils.createTag(enclTag, new SimpleAttributeSet(), innerHTML);
    }

    /**
     * Incloses a chunk of HTML text in the specified tag with the specified attribs
     * @param enclTag
     * @param set
     * @param innerHTML
     * @return
     */
    public static String createTag(final HTML.Tag enclTag, final AttributeSet set, final String innerHTML) {
        final String t = HTMLUtils.tagOpen(enclTag, set) + innerHTML + HTMLUtils.tagClose(enclTag);
        return t;
    }

    protected static String tagOpen(final HTML.Tag enclTag, final AttributeSet set) {
        String t = "<" + enclTag;
        for (final Enumeration e = set.getAttributeNames(); e.hasMoreElements();) {
            final Object name = e.nextElement();
            if (!name.toString().equals("name")) {
                final Object val = set.getAttribute(name);
                t += " " + name + "=\"" + val + "\"";
            }
        }

        return t + ">";
    }

    protected static String tagClose(final HTML.Tag t) {
        return "</" + t + ">";
    }

    public static List getParagraphElements(final JEditorPane editor) {
        final List elems = new LinkedList();
        try {
            final HTMLDocument doc = (HTMLDocument) editor.getDocument();
            Element curE = HTMLUtils.getParaElement(doc, editor.getSelectionStart());
            final Element endE = HTMLUtils.getParaElement(doc, editor.getSelectionEnd());

            while (curE.getEndOffset() <= endE.getEndOffset()) {
                elems.add(curE);
                curE = HTMLUtils.getParaElement(doc, curE.getEndOffset() + 1);
                if (curE.getEndOffset() >= doc.getLength()) {
                    break;
                }
            }
        } catch (final ClassCastException cce) {
            HTMLUtils.logger.trace(null, cce);
        }

        return elems;
    }

    protected static Element getParaElement(final HTMLDocument doc, final int pos) {
        Element curE = doc.getParagraphElement(pos);
        while (HTMLUtils.isImplied(curE)) {
            curE = curE.getParentElement();
        }

        final Element lp = HTMLUtils.getListParent(curE);
        if (lp != null) {
            curE = lp;
        }

        return curE;
    }

    /**
     * Searches upward for the specified parent for the element.
     * @param curElem
     * @param parentTag
     * @return The parent element, or null if the parent wasnt found
     */
    public static Element getParent(final Element curElem, final HTML.Tag parentTag) {
        Element parent = curElem;
        while (parent != null) {
            if (parent.getName().equals(parentTag.toString())) {
                return parent;
            }
            parent = parent.getParentElement();
        }

        return null;
    }

    /**
     * Tests if the element is empty
     * @param el
     * @return
     */
    public static boolean isElementEmpty(final Element el) {
        final String s = HTMLUtils.getElementHTML(el, false).trim();
        return s.length() == 0;
    }

    /**
     * Searches for a list {@link Element} that is the parent of the specified {@link Element}.
     * @param elem
     * @return A list element (UL, OL, DIR, MENU, or DL) if found, null otherwise
     */
    public static Element getListParent(final Element elem) {
        Element parent = elem;
        while (parent != null) {
            if (parent.getName().toUpperCase().equals("UL") || parent.getName().toUpperCase().equals("OL")
                    || parent.getName().equals("dl")
                    || parent.getName().equals("menu") || parent.getName().equals("dir")) {
                return parent;
            }
            parent = parent.getParentElement();
        }
        return null;
    }

    /**
     * Gets the element one position less than the start of the specified element
     * @param doc
     * @param el
     * @return
     */
    public static Element getPreviousElement(final HTMLDocument doc, final Element el) {
        if (el.getStartOffset() > 0) {
            return doc.getParagraphElement(el.getStartOffset() - 1);
        }
        return el;
    }

    /**
     * Gets the element one position greater than the end of the specified element
     * @param doc
     * @param el
     * @return
     */
    public static Element getNextElement(final HTMLDocument doc, final Element el) {
        if (el.getEndOffset() < doc.getLength()) {
            return doc.getParagraphElement(el.getEndOffset() + 1);
        }
        return el;
    }

    /**
     * Removes the enclosing tags from a chunk of HTML text
     * @param elem
     * @param txt
     * @return
     */
    public static String removeEnclosingTags(final Element elem, final String txt) {
        final HTML.Tag t = HTML.getTag(elem.getName());
        return HTMLUtils.removeEnclosingTags(t, txt);
    }

    /**
     * Removes the enclosing tags from a chunk of HTML text
     * @param t
     * @param txt
     * @return
     */
    public static String removeEnclosingTags(final HTML.Tag t, String txt) {
        final String openStart = "<" + t;
        final String closeTag = "</" + t + ">";

        txt = txt.trim();

        if (txt.startsWith(openStart)) {
            final int n = txt.indexOf(">");
            if (n != -1) {
                txt = txt.substring(n + 1, txt.length());
            }
        }

        if (txt.endsWith(closeTag)) {
            txt = txt.substring(0, txt.length() - closeTag.length());
        }

        return txt;
    }

    /**
     * Gets the html of the specified {@link Element}
     * @param el
     * @param includeEnclosingTags true, if the enclosing tags should be included
     * @return
     */
    public static String getElementHTML(final Element el, final boolean includeEnclosingTags) {
        String txt = "";

        try {
            final StringWriter out = new StringWriter();
            final ElementWriter w = new ElementWriter(out, el);
            w.write();
            txt = out.toString();
        } catch (final Exception ex) {
            HTMLUtils.logger.error(null, ex);
        }

        if (includeEnclosingTags) {
            return txt;
        }
        return HTMLUtils.removeEnclosingTags(el, txt);
    }

    /**
     * Removes an element from the document that contains it
     * @param el
     * @throws BadLocationException
     */
    public static void removeElement(final Element el) throws BadLocationException {
        final HTMLDocument document = (HTMLDocument) el.getDocument();
        final int start = el.getStartOffset();
        int len = el.getEndOffset() - start;

        final Element tdEle = HTMLUtils.getParent(el, HTML.Tag.TD);
        if ((tdEle != null) && (el.getEndOffset() == tdEle.getEndOffset())) {
            document.remove(start, len - 1);
        } else {
            if (el.getEndOffset() > document.getLength()) {
                len = document.getLength() - start;
            }

            document.remove(start, len);
        }
    }

    public static HTML.Tag getStartTag(final String text) {
        final String html = text.trim();
        final int s = html.indexOf('<');
        if (s != 0) {
            return null;
        }
        final int e = html.indexOf('>');
        if (e == -1) {
            return null; // not any kind of tag
        }

        String tagName = html.substring(1, e).trim();
        if (tagName.indexOf(' ') != -1) {
            tagName = tagName.split("\\s")[0];
        }

        return HTML.getTag(tagName);
    }

    protected static int depthFromRoot(final Element curElem) {
        Element parent = curElem;
        int depth = 0;
        while (parent != null) {
            if (parent.getName().equals("body")
                    || parent.getName().equals("td")) {
                break;
            }
            parent = parent.getParentElement();
            depth++;
        }

        return depth;
    }

	/**
	 * Inserts an arbitrary chunk of HTML into the JEditorPane at the current caret position.
	 *
	 * @param rawHtml
	 * @param editor
	 */
	public static void insertArbitraryHTML(final String rawHtml, final JEditorPane editor) {
		HTMLUtils.tidy.setOutputEncoding("UTF-8");
		HTMLUtils.tidy.setInputEncoding("UTF-8");

		try {
			final ByteArrayInputStream bin = new ByteArrayInputStream(rawHtml.getBytes("UTF-8"));
			final Document doc = HTMLUtils.tidy.parseDOM(bin, null);
			final NodeList nodelist = doc.getElementsByTagName("body");

			if (nodelist != null) {
				final Node body = nodelist.item(0);
				final NodeList bodyChildren = body.getChildNodes();

				// for(int i = bodyChildren.getLength() - 1; i >= 0; i--)
				final int len = bodyChildren.getLength();
				for (int i = 0; i < len; i++) {
					String ml = HTMLUtils.xmlToString(bodyChildren.item(i));
					if (ml != null) {
						HTML.Tag tag = HTMLUtils.getStartTag(ml);
						if (tag == null) {
							tag = HTML.Tag.SPAN;
							ml = "<span>" + ml + "</span>";
						}
						HTMLUtils.insertHTML(ml, tag, editor);
					}
				}
			}
		} catch (final UnsupportedEncodingException e) {
			HTMLUtils.logger.error(null, e);
		}
	}

    protected static String xmlToString(final Node node) {
        try {
            final Source source = new DOMSource(node);
            final StringWriter stringWriter = new StringWriter();
            final Result result = new StreamResult(stringWriter);
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);

            return stringWriter.getBuffer().toString();
        } catch (final TransformerConfigurationException e) {
            HTMLUtils.logger.error(null, e);
        } catch (final TransformerException e) {
            HTMLUtils.logger.error(null, e);
        }

        return null;
    }

    /**
     * Inserts a string of html into the {@link JEditorPane}'s {@link HTMLDocument} at the current caret
     * position.
     * @param html
     * @param tag
     * @param editor
     */
    public static void insertHTML(String html, final HTML.Tag tag, final JEditorPane editor) {
        HTMLEditorKit editorKit;
        HTMLDocument document;
        try {
            editorKit = (HTMLEditorKit) editor.getEditorKit();
            document = (HTMLDocument) editor.getDocument();
        } catch (final ClassCastException ex) {
            HTMLUtils.logger.trace(null, ex);
            return;
        }

        final int caret = editor.getCaretPosition();
        final Element pElem = document.getParagraphElement(caret);

        final boolean breakParagraph = tag.breaksFlow() || tag.isBlock();
        final boolean beginParagraph = caret == pElem.getStartOffset();
        html = HTMLUtils.jEditorPaneizeHTML(html);

        try {
            if (breakParagraph && beginParagraph) {
                document.insertBeforeStart(pElem, "<p></p>");
                final Element nextEl = document.getParagraphElement(caret + 1);
                editorKit.insertHTML(document, caret + 1, html, HTMLUtils.depthFromRoot(nextEl)/* 1 */, 0, tag);
                document.remove(caret, 1);
            } else if (breakParagraph && !beginParagraph) {
                editorKit.insertHTML(document, caret, html, HTMLUtils.depthFromRoot(pElem)/* 1 */, 0, tag);
            } else if (!breakParagraph && beginParagraph) {

                /*
                 * Trick: insert a non-breaking space after start, so that we're inserting into the middle of a
                 * line. Then, remove the space. This works around a bug when using insertHTML near the beginning of
                 * a paragraph.
                 */
                document.insertAfterStart(pElem, "&nbsp;");
                editorKit.insertHTML(document, caret + 1, html, 0, 0, tag);
                document.remove(caret, 1);
            } else if (!breakParagraph && !beginParagraph) {
                editorKit.insertHTML(document, caret, html, 0, 0, tag);
            }
        } catch (final Exception ex) {
            HTMLUtils.logger.error(null, ex);
        }
    }

    /**
     * Gets the character attributes at the {@link JEditorPane}'s caret position
     * <p>
     * If there is no selection, the character attributes at caretPos - 1 are retuned. If there is a
     * slection, the attributes at selectionEnd - 1 are returned
     * </p>
     * @param editor
     * @return An {@link AttributeSet} or null, if the editor doesn't have a {@link StyledDocument}
     */
    public static AttributeSet getCharacterAttributes(final JEditorPane editor) {
        int p;
        if (editor.getSelectedText() != null) {
            p = editor.getSelectionEnd() - 1;
        } else {
            p = editor.getCaretPosition() > 0 ? editor.getCaretPosition() - 1 : 0;
        }

        try {
            final StyledDocument doc = (StyledDocument) editor.getDocument();
            return doc.getCharacterElement(p).getAttributes();
        } catch (final ClassCastException cce) {
            HTMLUtils.logger.trace(null, cce);
        }

        return null;
    }

    /**
     * Gets the font family name at the {@link JEditorPane}'s current caret position
     * @param editor
     * @return The font family name, or null if no font is set
     */
    public static String getFontFamily(final JEditorPane editor) {
        final AttributeSet attr = HTMLUtils.getCharacterAttributes(editor);
        if (attr != null) {
            Object val = attr.getAttribute(StyleConstants.FontFamily);
            if (val != null) {
                return val.toString();
            }
            val = attr.getAttribute(CSS.Attribute.FONT_FAMILY);
            if (val != null) {
                return val.toString();
            }
            val = attr.getAttribute(HTML.Tag.FONT);
            if ((val != null) && (val instanceof AttributeSet)) {
                final AttributeSet set = (AttributeSet) val;
                val = set.getAttribute(HTML.Attribute.FACE);
                if (val != null) {
                    return val.toString();
                }
            }
        }

        return null; // no font family was defined
    }

    /**
     * Set's the font family at the {@link JEditorPane}'s current caret positon, or for the current
     * selection (if there is one).
     * <p>
     * If the fontName parameter is null, any currently set font family is removed.
     * </p>
     * @param editor
     * @param fontName
     */
    public static void setFontFamily(final JEditorPane editor, final String fontName) {
        final AttributeSet attr = HTMLUtils.getCharacterAttributes(editor);
        if (attr == null) {
            return;
        }

        if (fontName == null) // we're removing the font
        {
            // the font might be defined as a font tag
            Object val = attr.getAttribute(HTML.Tag.FONT);
            if ((val != null) && (val instanceof AttributeSet)) {
                final MutableAttributeSet set = new SimpleAttributeSet((AttributeSet) val);
                // does it have a FACE attrib?
                val = set.getAttribute(HTML.Attribute.FACE);
                if (val != null) {
                    set.removeAttribute(HTML.Attribute.FACE);
                    // remove the current font tag
                    HTMLUtils.removeCharacterAttribute(editor, HTML.Tag.FONT);
                    if (set.getAttributeCount() > 0) {
                        // it's not empty so replace the other font attribs
                        final SimpleAttributeSet fontSet = new SimpleAttributeSet();
                        fontSet.addAttribute(HTML.Tag.FONT, set);
                        HTMLUtils.setCharacterAttributes(editor, set);
                    }
                }
            }
            // also remove these for good measure
            HTMLUtils.removeCharacterAttribute(editor, StyleConstants.FontFamily);
            HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.FONT_FAMILY);
        } else // adding the font family
        {
            final MutableAttributeSet tagAttrs = new SimpleAttributeSet();
            tagAttrs.addAttribute(StyleConstants.FontFamily, fontName);
            HTMLUtils.setCharacterAttributes(editor, tagAttrs);
        }
        HTMLUtils.printAttribs(attr);
    }

    /**
     * Removes a CSS character attribute that has the specified value from the {@link JEditorPane}'s
     * current caret position or selection.
     * <p>
     * The val parameter is a {@link String} even though the actual attribute value is not. This is
     * because the actual attribute values are not public. Thus, this method checks the value via the
     * toString() method
     * </p>
     * @param editor
     * @param atr
     * @param val
     */
    public static void removeCharacterAttribute(final JEditorPane editor, final CSS.Attribute atr, final String val) {
        HTMLDocument doc;
        MutableAttributeSet attr;
        try {
            doc = (HTMLDocument) editor.getDocument();
            attr = ((HTMLEditorKit) editor.getEditorKit()).getInputAttributes();
        } catch (final ClassCastException cce) {
            HTMLUtils.logger.trace(null, cce);
            return;
        }

        final List tokens = HTMLUtils.tokenizeCharAttribs(doc, editor.getSelectionStart(), editor.getSelectionEnd());
        for (final Iterator it = tokens.iterator(); it.hasNext();) {
            final CharStyleToken t = (CharStyleToken) it.next();
            if (t.attrs.isDefined(atr) && t.attrs.getAttribute(atr).toString().equals(val)) {
                final SimpleAttributeSet sas = new SimpleAttributeSet();
                sas.addAttributes(t.attrs);
                sas.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                sas.removeAttribute(atr);
                doc.setCharacterAttributes(t.offs, t.len, sas, true);
            }
        }
        final int pos = editor.getCaretPosition();
        attr.addAttributes(doc.getCharacterElement(pos).getAttributes());
        attr.removeAttribute(atr);
    }

    /**
     * Removes a single character attribute from the editor's current position/selection.
     *
     * <p>
     * Removes from the editor kit's input attribtues and/or document at the caret position. If there is
     * a selction the attribute is removed from the selected text
     * </p>
     * @param editor
     * @param atr
     */
    public static void removeCharacterAttribute(final JEditorPane editor, final Object atr) {
        HTMLDocument doc;
        MutableAttributeSet attr;
        try {
            doc = (HTMLDocument) editor.getDocument();
            attr = ((HTMLEditorKit) editor.getEditorKit()).getInputAttributes();
        } catch (final ClassCastException cce) {
            HTMLUtils.logger.trace(null, cce);
            return;
        }

        final List tokens = HTMLUtils.tokenizeCharAttribs(doc, editor.getSelectionStart(), editor.getSelectionEnd());
        for (final Iterator it = tokens.iterator(); it.hasNext();) {
            final CharStyleToken t = (CharStyleToken) it.next();
            if (t.attrs.isDefined(atr)) {
                final SimpleAttributeSet sas = new SimpleAttributeSet();
                sas.addAttributes(t.attrs);
                sas.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                sas.removeAttribute(atr);
                doc.setCharacterAttributes(t.offs, t.len, sas, true);
            }
        }
        final int pos = editor.getCaretPosition();
        attr.addAttributes(doc.getCharacterElement(pos).getAttributes());
        attr.removeAttribute(atr);
    }

    /**
     * Tokenizes character attrbutes.
     * @param doc
     * @param s
     * @param e
     * @return
     */
    protected static List tokenizeCharAttribs(final HTMLDocument doc, int s, final int e) {
        final LinkedList tokens = new LinkedList();
        CharStyleToken tok = new CharStyleToken();
        for (; s <= e; s++) {
            // if(s == doc.getLength())
            // break;
            final AttributeSet as = doc.getCharacterElement(s).getAttributes();
            if ((tok.attrs == null) || (((s + 1) <= e) && !as.isEqual(tok.attrs))) {
                tok = new CharStyleToken();
                tok.offs = s;
                tokens.add(tok);
                tok.attrs = as;
            }

            if ((s + 1) <= e) {
                tok.len++;
            }
        }

        return tokens;
    }

    /**
     * Sets the character attributes for selection of the specified editor
     * @param editor
     * @param attrs
     * @param replace if true, replaces the attrubutes
     */
    public static void setCharacterAttributes(final JEditorPane editor, AttributeSet attr, final boolean replace) {
        HTMLDocument doc;
        StyledEditorKit k;
        try {
            doc = (HTMLDocument) editor.getDocument();
            k = (StyledEditorKit) editor.getEditorKit();
        } catch (final ClassCastException ex) {
            HTMLUtils.logger.trace(null, ex);
            return;
        }

        // TODO figure out what the "CR" attribute is.
        // Somewhere along the line the attribute CR (String key) with a value
        // of Boolean.TRUE
        // gets inserted. If it is in the attributes, something gets screwed up
        // and the text gets all jumbled up and doesn't render correctly.
        // Is it yet another JEditorPane bug?
        final MutableAttributeSet inputAttributes = k.getInputAttributes();
        final SimpleAttributeSet sas = new SimpleAttributeSet(attr);
        sas.removeAttribute("CR");
        attr = sas;

        final int p0 = editor.getSelectionStart();
        final int p1 = editor.getSelectionEnd();
        if (p0 != p1) {
            doc.setCharacterAttributes(p0, p1 - p0, attr, replace);
        } else {
            // No selection, so we have to update the input attributes
            // otherwise they apparently get reread from the document...
            // not sure if this is a bug or what, but the following works
            // so just go with it.
            if (replace) {
                attr = attr.copyAttributes();
                inputAttributes.removeAttributes(inputAttributes);
                inputAttributes.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            }
            inputAttributes.addAttributes(attr);
        }
    }

    /**
     * Sets the character attributes for selection of the specified editor
     * @param editor
     * @param attrs
     */
    public static void setCharacterAttributes(final JEditorPane editor, final AttributeSet attrs) {
        HTMLUtils.setCharacterAttributes(editor, attrs, false);
    }

    /**
     * Converts an html tag attribute list to a {@link Map}. For example, the String
     * 'href="http://blah.com" target="_self"' becomes name-value pairs:<br>
     * href > http://blah.com<br>
     * target > _self
     * @param atts
     * @return
     */
    public static Map tagAttribsToMap(final String atts) {
        final Map attribs = new HashMap();

        final StringTokenizer st = new StringTokenizer(atts.trim(), " ");
        String lastAtt = null;
        while (st.hasMoreTokens()) {
            final String atVal = st.nextToken().trim();
            final int equalPos = atVal.indexOf('=');
            if (equalPos == -1) {
                if (lastAtt == null) {
                    break;// no equals char in this string
                }
                final String lastVal = attribs.get(lastAtt).toString();
                attribs.put(lastAtt, lastVal + " " + atVal);
                continue;
            }

            final String at = atVal.substring(0, equalPos);
            String val = atVal.substring(atVal.indexOf('=') + 1, atVal.length());
            if (val.startsWith("\"")) {
                val = val.substring(1, val.length());
            }
            if (val.endsWith("\"")) {
                val = val.substring(0, val.length() - 1);
            }

            attribs.put(at, val);
            lastAtt = at;
        }

        return attribs;
    }

    /**
     * Converts a Color to a hex string in the format "#RRGGBB"
     */
    public static String colorToHex(final Color color) {
        String colorstr = new String("#");

        // Red
        String str = Integer.toHexString(color.getRed());
        if (str.length() > 2) {
            str = str.substring(0, 2);
        } else if (str.length() < 2) {
            colorstr += "0" + str;
        } else {
            colorstr += str;
        }

        // Green
        str = Integer.toHexString(color.getGreen());
        if (str.length() > 2) {
            str = str.substring(0, 2);
        } else if (str.length() < 2) {
            colorstr += "0" + str;
        } else {
            colorstr += str;
        }

        // Blue
        str = Integer.toHexString(color.getBlue());
        if (str.length() > 2) {
            str = str.substring(0, 2);
        } else if (str.length() < 2) {
            colorstr += "0" + str;
        } else {
            colorstr += str;
        }

        return colorstr;
    }

    /**
     * Convert a "#FFFFFF" hex string to a Color. If the color specification is bad, an attempt will be
     * made to fix it up.
     */
    public static Color hexToColor(final String value) {
        String digits;
        // int n = value.length();
        if (value.startsWith("#")) {
            digits = value.substring(1, Math.min(value.length(), 7));
        } else {
            digits = value;
        }

        final String hstr = "0x" + digits;
        Color c;

        try {
            c = Color.decode(hstr);
        } catch (final NumberFormatException nfe) {
            HTMLUtils.logger.trace(null, nfe);
            c = Color.BLACK; // just return black
        }
        return c;
    }

    /**
     * Convert a color string such as "RED" or "#NNNNNN" or "rgb(r, g, b)" to a Color.
     */
    public static Color stringToColor(final String str) {
        Color color = null;

        if (str.length() == 0) {
            color = Color.black;
        } else if (str.charAt(0) == '#') {
            color = HTMLUtils.hexToColor(str);
        } else if (str.equalsIgnoreCase("Black")) {
            color = HTMLUtils.hexToColor("#000000");
        } else if (str.equalsIgnoreCase("Silver")) {
            color = HTMLUtils.hexToColor("#C0C0C0");
        } else if (str.equalsIgnoreCase("Gray")) {
            color = HTMLUtils.hexToColor("#808080");
        } else if (str.equalsIgnoreCase("White")) {
            color = HTMLUtils.hexToColor("#FFFFFF");
        } else if (str.equalsIgnoreCase("Maroon")) {
            color = HTMLUtils.hexToColor("#800000");
        } else if (str.equalsIgnoreCase("Red")) {
            color = HTMLUtils.hexToColor("#FF0000");
        } else if (str.equalsIgnoreCase("Purple")) {
            color = HTMLUtils.hexToColor("#800080");
        } else if (str.equalsIgnoreCase("Fuchsia")) {
            color = HTMLUtils.hexToColor("#FF00FF");
        } else if (str.equalsIgnoreCase("Green")) {
            color = HTMLUtils.hexToColor("#008000");
        } else if (str.equalsIgnoreCase("Lime")) {
            color = HTMLUtils.hexToColor("#00FF00");
        } else if (str.equalsIgnoreCase("Olive")) {
            color = HTMLUtils.hexToColor("#808000");
        } else if (str.equalsIgnoreCase("Yellow")) {
            color = HTMLUtils.hexToColor("#FFFF00");
        } else if (str.equalsIgnoreCase("Navy")) {
            color = HTMLUtils.hexToColor("#000080");
        } else if (str.equalsIgnoreCase("Blue")) {
            color = HTMLUtils.hexToColor("#0000FF");
        } else if (str.equalsIgnoreCase("Teal")) {
            color = HTMLUtils.hexToColor("#008080");
        } else if (str.equalsIgnoreCase("Aqua")) {
            color = HTMLUtils.hexToColor("#00FFFF");
        } else {
            color = HTMLUtils.hexToColor(str); // sometimes get specified
                                               // without leading
        }
        // #
        return color;
    }

    /**
     * Removes self-closing tags from xhtml for the benifit of {@link JEditorPane}
     *
     * <p>
     * JEditorpane can't handle empty xhtml containers like &lt;br /&gt; or &lt;img /&gt;, so this
     * method replaces them without the "/" as in &lt;br&gt;
     * </p>
     * @param html
     * @return JEditorpane friendly html
     */
    public static String jEditorPaneizeHTML(final String html) {
        return html.replaceAll("(<\\s*\\w+\\b[^>]*)/(\\s*>)", "$1$2");
    }

    /**
     * Helper method that prints out the contents of an {@link AttributeSet} to logger for debugging
     * @param attr
     */
    public static void printAttribs(final AttributeSet attr) {
        HTMLUtils.logger.debug("----------------------------------------------------------------");
        HTMLUtils.logger.debug(attr.toString());
        final Enumeration ee = attr.getAttributeNames();
        while (ee.hasMoreElements()) {
            final Object name = ee.nextElement();
            final Object atr = attr.getAttribute(name);
            HTMLUtils.logger
                .debug(name + " " + name.getClass().getName() + " | " + atr + " " + atr.getClass().getName());
        }
        HTMLUtils.logger.debug("----------------------------------------------------------------");
    }

    protected static class CharStyleToken {

        int offs;

        int len;

        AttributeSet attrs;

    }

    /**
     * @deprecated Use {@link Base64Utils}
     * @author Imatia Innovation
     */
    @Deprecated
    public static class Base64 {

        /* ******** P U B L I C F I E L D S ******** */

        /** No options specified. Value is zero. */
        public static final int NO_OPTIONS = 0;

        /** Specify encoding. */
        public static final int ENCODE = 1;

        /** Specify decoding. */
        public static final int DECODE = 0;

        /** Specify that data should be gzip-compressed. */
        public static final int GZIP = 2;

        /**
         * Don't break lines when encoding (violates strict Base64 specification)
         */
        public static final int DONT_BREAK_LINES = 8;

        /* ******** P R I V A T E F I E L D S ******** */

        /** Maximum line length (76) of Base64 output. */
        private static final int MAX_LINE_LENGTH = 76;

        /** The equals sign (=) as a byte. */
        private static final byte EQUALS_SIGN = (byte) '=';

        /** The new line character (\n) as a byte. */
        private static final byte NEW_LINE = (byte) '\n';

        /** Preferred encoding. */
        private static final String PREFERRED_ENCODING = "UTF-8";

        /** The 64 valid Base64 values. */
        private final static byte[] ALPHABET;

        /** May be something funny like EBCDIC */
        private final static byte[] _NATIVE_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
                (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M',
                (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
                (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
                (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h',
                (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm',
                (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
                (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
                (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

        /** Determine which ALPHABET to use. */
        static {
            byte[] __bytes;
            try {
                __bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
                    .getBytes(Base64.PREFERRED_ENCODING);
            } // end try
            catch (final java.io.UnsupportedEncodingException use) {
                HTMLUtils.logger.trace(null, use);
                __bytes = Base64._NATIVE_ALPHABET; // Fall back to native
                                                   // encoding
            } // end catch
            ALPHABET = __bytes;
        } // end static

        /**
         * Translates a Base64 value to either its 6-bit reconstruction value or a negative number
         * indicating some other meaning.
         **/
        private final static byte[] DECODABET = {
                -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 0 - 8
                -5, -5, // Whitespace: Tab and Linefeed
                -9, -9, // Decimal 11 - 12
                -5, // Whitespace: Carriage Return
                -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 - 26
                -9, -9, -9, -9, -9, // Decimal 27 - 31
                -5, // Whitespace: Space
                -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
                62, // Plus sign at decimal 43
                -9, -9, -9, // Decimal 44 - 46
                63, // Slash at decimal 47
                52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
                -9, -9, -9, // Decimal 58 - 60
                -1, // Equals sign at decimal 61
                -9, -9, -9, // Decimal 62 - 64
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A' through 'N'
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O' through 'Z'
                -9, -9, -9, -9, -9, -9, // Decimal 91 - 96
                26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a' through 'm'
                39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n' through 'z'
                -9, -9, -9, -9 // Decimal 123 - 126

                /*
                 * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 127 - 139
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 140 - 152
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 166 - 178
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 205 - 217
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
                 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 231 - 243 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9
                 * // Decimal 244 - 255
                 */
        };

        // I think I end up not using the BAD_ENCODING indicator.
        private final static byte WHITE_SPACE_ENC = -5; // Indicates white space in encoding

        private final static byte EQUALS_SIGN_ENC = -1; // Indicates equals sign in encoding

        /** Defeats instantiation. */
        private Base64() {
        }

        /* ******** E N C O D I N G M E T H O D S ******** */

        /**
         * Encodes up to the first three bytes of array <var>threeBytes</var> and returns a four-byte array
         * in Base64 notation. The actual number of significant bytes in your array is given by
         * <var>numSigBytes</var>. The array <var>threeBytes</var> needs only be as big as
         * <var>numSigBytes</var>. Code can reuse a byte array by passing a four-byte array as
         * <var>b4</var>.
         * @param b4 A reusable byte array to reduce array instantiation
         * @param threeBytes the array to convert
         * @param numSigBytes the number of significant bytes in your array
         * @return four byte array in Base64 notation.
         * @since 1.5.1
         */
        private static byte[] encode3to4(final byte[] b4, final byte[] threeBytes, final int numSigBytes) {
            Base64.encode3to4(threeBytes, 0, numSigBytes, b4, 0);
            return b4;
        } // end encode3to4

        /**
         * Encodes up to three bytes of the array <var>source</var> and writes the resulting four Base64
         * bytes to <var>destination</var>. The source and destination arrays can be manipulated anywhere
         * along their length by specifying <var>srcOffset</var> and <var>destOffset</var>. This method does
         * not check to make sure your arrays are large enough to accomodate <var>srcOffset</var> + 3 for
         * the <var>source</var> array or <var>destOffset</var> + 4 for the <var>destination</var> array.
         * The actual number of significant bytes in your array is given by <var>numSigBytes</var>.
         * @param source the array to convert
         * @param srcOffset the index where conversion begins
         * @param numSigBytes the number of significant bytes in your array
         * @param destination the array to hold the conversion
         * @param destOffset the index where output will be put
         * @return the <var>destination</var> array
         * @since 1.3
         */
        private static byte[] encode3to4(final byte[] source, final int srcOffset, final int numSigBytes, final byte[] destination,
                final int destOffset) {
            // 1 2 3
            // 01234567890123456789012345678901 Bit position
            // --------000000001111111122222222 Array position from threeBytes
            // --------| || || || | Six bit groups to index ALPHABET
            // >>18 >>12 >> 6 >> 0 Right shift necessary
            // 0x3f 0x3f 0x3f Additional AND

            // Create buffer with zero-padding if there are only one or two
            // significant bytes passed in the array.
            // We have to shift left 24 in order to flush out the 1's that
            // appear
            // when Java treats a value as negative that is cast from a byte to
            // an int.
            final int inBuff = (numSigBytes > 0 ? (source[srcOffset] << 24) >>> 8 : 0)
                    | (numSigBytes > 1 ? (source[srcOffset + 1] << 24) >>> 16 : 0)
                    | (numSigBytes > 2 ? (source[srcOffset + 2] << 24) >>> 24 : 0);

            switch (numSigBytes) {
                case 3:
                    destination[destOffset] = Base64.ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = Base64.ALPHABET[(inBuff >>> 12) & 0x3f];
                    destination[destOffset + 2] = Base64.ALPHABET[(inBuff >>> 6) & 0x3f];
                    destination[destOffset + 3] = Base64.ALPHABET[inBuff & 0x3f];
                    return destination;

                case 2:
                    destination[destOffset] = Base64.ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = Base64.ALPHABET[(inBuff >>> 12) & 0x3f];
                    destination[destOffset + 2] = Base64.ALPHABET[(inBuff >>> 6) & 0x3f];
                    destination[destOffset + 3] = Base64.EQUALS_SIGN;
                    return destination;

                case 1:
                    destination[destOffset] = Base64.ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = Base64.ALPHABET[(inBuff >>> 12) & 0x3f];
                    destination[destOffset + 2] = Base64.EQUALS_SIGN;
                    destination[destOffset + 3] = Base64.EQUALS_SIGN;
                    return destination;

                default:
                    return destination;
            } // end switch
        } // end encode3to4

        /**
         * Serializes an object and returns the Base64-encoded version of that serialized object. If the
         * object cannot be serialized or there is another error, the method will return <tt>null</tt>. The
         * object is not GZip-compressed before being encoded.
         * @param serializableObject The object to encode
         * @return The Base64-encoded object
         * @since 1.4
         */
        public static String encodeObject(final java.io.Serializable serializableObject) {
            return Base64.encodeObject(serializableObject, Base64.NO_OPTIONS);
        } // end encodeObject

        /**
         * Serializes an object and returns the Base64-encoded version of that serialized object. If the
         * object cannot be serialized or there is another error, the method will return <tt>null</tt>.
         * <p>
         * Valid options:
         *
         * <pre>
         *   GZIP: gzip-compresses object before encoding it.
         *   DONT_BREAK_LINES: don't break lines at 76 characters
         *     <i>Note: Technically, this makes your encoding non-compliant.</i>
         * </pre>
         *
         * <p>
         * Example: <code>encodeObject( myObj, Base64.GZIP )</code> or
         * <p>
         * Example: <code>encodeObject( myObj, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
         * @param serializableObject The object to encode
         * @param options Specified options
         * @return The Base64-encoded object
         * @see Base64#GZIP
         * @see Base64#DONT_BREAK_LINES
         * @since 2.0
         */
        public static String encodeObject(final java.io.Serializable serializableObject, final int options) {
            // Streams
            java.io.ByteArrayOutputStream baos = null;
            java.io.OutputStream b64os = null;
            java.io.ObjectOutputStream oos = null;
            java.util.zip.GZIPOutputStream gzos = null;

            // Isolate options
            final int gzip = options & Base64.GZIP;
            final int dontBreakLines = options & Base64.DONT_BREAK_LINES;

            try {
                // ObjectOutputStream -> (GZIP) -> Base64 ->
                // ByteArrayOutputStream
                baos = new java.io.ByteArrayOutputStream();
                b64os = new Base64.OutputStream(baos, Base64.ENCODE | dontBreakLines);

                // GZip?
                if (gzip == Base64.GZIP) {
                    gzos = new java.util.zip.GZIPOutputStream(b64os);
                    oos = new java.io.ObjectOutputStream(gzos);
                } else {
                    oos = new java.io.ObjectOutputStream(b64os);
                }

                oos.writeObject(serializableObject);
            } catch (final java.io.IOException e) {
                HTMLUtils.logger.error(null, e);
                return null;
            } finally {
                try {
                    oos.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
                try {
                    gzos.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
                try {
                    b64os.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
                try {
                    baos.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
            }

            // Return value according to relevant encoding.
            try {
                return new String(baos.toByteArray(), Base64.PREFERRED_ENCODING);
            } catch (final java.io.UnsupportedEncodingException uue) {
                HTMLUtils.logger.trace(null, uue);
                return new String(baos.toByteArray());
            }
        }

        /**
         * Encodes a byte array into Base64 notation. Does not GZip-compress data.
         * @param source The data to convert
         * @since 1.4
         */
        public static String encodeBytes(final byte[] source) {
            return Base64.encodeBytes(source, 0, source.length, Base64.NO_OPTIONS);
        } // end encodeBytes

        /**
         * Encodes a byte array into Base64 notation.
         * <p>
         * Valid options:
         *
         * <pre>
         *   GZIP: gzip-compresses object before encoding it.
         *   DONT_BREAK_LINES: don't break lines at 76 characters
         *     <i>Note: Technically, this makes your encoding non-compliant.</i>
         * </pre>
         *
         * <p>
         * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
         * <p>
         * Example: <code>encodeBytes( myData, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
         * @param source The data to convert
         * @param options Specified options
         * @see Base64#GZIP
         * @see Base64#DONT_BREAK_LINES
         * @since 2.0
         */
        public static String encodeBytes(final byte[] source, final int options) {
            return Base64.encodeBytes(source, 0, source.length, options);
        } // end encodeBytes

        /**
         * Encodes a byte array into Base64 notation. Does not GZip-compress data.
         * @param source The data to convert
         * @param off Offset in array where conversion should begin
         * @param len Length of data to convert
         * @since 1.4
         */
        public static String encodeBytes(final byte[] source, final int off, final int len) {
            return Base64.encodeBytes(source, off, len, Base64.NO_OPTIONS);
        } // end encodeBytes

        /**
         * Encodes a byte array into Base64 notation.
         * <p>
         * Valid options:
         *
         * <pre>
         *   GZIP: gzip-compresses object before encoding it.
         *   DONT_BREAK_LINES: don't break lines at 76 characters
         *     <i>Note: Technically, this makes your encoding non-compliant.</i>
         * </pre>
         *
         * <p>
         * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
         * <p>
         * Example: <code>encodeBytes( myData, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
         * @param source The data to convert
         * @param off Offset in array where conversion should begin
         * @param len Length of data to convert
         * @param options Specified options
         * @see Base64#GZIP
         * @see Base64#DONT_BREAK_LINES
         * @since 2.0
         */
        public static String encodeBytes(final byte[] source, final int off, final int len, final int options) {
            // Isolate options
            final int dontBreakLines = options & Base64.DONT_BREAK_LINES;
            final int gzip = options & Base64.GZIP;

            // Compress?
            if (gzip == Base64.GZIP) {
                java.io.ByteArrayOutputStream baos = null;
                java.util.zip.GZIPOutputStream gzos = null;
                Base64.OutputStream b64os = null;

                try {
                    // GZip -> Base64 -> ByteArray
                    baos = new java.io.ByteArrayOutputStream();
                    b64os = new Base64.OutputStream(baos, Base64.ENCODE | dontBreakLines);
                    gzos = new java.util.zip.GZIPOutputStream(b64os);

                    gzos.write(source, off, len);
                    gzos.close();
                } catch (final java.io.IOException e) {
                    HTMLUtils.logger.error(null, e);
                    return null;
                } finally {
                    try {
                        gzos.close();
                    } catch (final Exception e) {
                        HTMLUtils.logger.trace(null, e);
                    }
                    try {
                        b64os.close();
                    } catch (final Exception e) {
                        HTMLUtils.logger.trace(null, e);
                    }
                    try {
                        baos.close();
                    } catch (final Exception e) {
                        HTMLUtils.logger.trace(null, e);
                    }
                }

                // Return value according to relevant encoding.
                try {
                    return new String(baos.toByteArray(), Base64.PREFERRED_ENCODING);
                } catch (final java.io.UnsupportedEncodingException uue) {
                    HTMLUtils.logger.trace(null, uue);
                    return new String(baos.toByteArray());
                }
            }

            // Else, don't compress. Better not to use streams at all then.
            else {
                // Convert option to boolean in way that code likes it.
                final boolean breakLines = dontBreakLines == 0;

                final int len43 = (len * 4) / 3;
                final byte[] outBuff = new byte[len43 // Main 4:3
                        + ((len % 3) > 0 ? 4 : 0) // Account for padding
                        + (breakLines ? len43 / Base64.MAX_LINE_LENGTH : 0)]; // New lines
                int d = 0;
                int e = 0;
                final int len2 = len - 2;
                int lineLength = 0;
                for (; d < len2; d += 3, e += 4) {
                    Base64.encode3to4(source, d + off, 3, outBuff, e);

                    lineLength += 4;
                    if (breakLines && (lineLength == Base64.MAX_LINE_LENGTH)) {
                        outBuff[e + 4] = Base64.NEW_LINE;
                        e++;
                        lineLength = 0;
                    } // end if: end of line
                } // en dfor: each piece of array

                if (d < len) {
                    Base64.encode3to4(source, d + off, len - d, outBuff, e);
                    e += 4;
                } // end if: some padding needed

                // Return value according to relevant encoding.
                try {
                    return new String(outBuff, 0, e, Base64.PREFERRED_ENCODING);
                } catch (final java.io.UnsupportedEncodingException uue) {
                    HTMLUtils.logger.trace(null, uue);
                    return new String(outBuff, 0, e);
                }
            }
        }

        /* ******** D E C O D I N G M E T H O D S ******** */

        /**
         * Decodes four bytes from array <var>source</var> and writes the resulting bytes (up to three of
         * them) to <var>destination</var>. The source and destination arrays can be manipulated anywhere
         * along their length by specifying <var>srcOffset</var> and <var>destOffset</var>. This method does
         * not check to make sure your arrays are large enough to accomodate <var>srcOffset</var> + 4 for
         * the <var>source</var> array or <var>destOffset</var> + 3 for the <var>destination</var> array.
         * This method returns the actual number of bytes that were converted from the Base64 encoding.
         * @param source the array to convert
         * @param srcOffset the index where conversion begins
         * @param destination the array to hold the conversion
         * @param destOffset the index where output will be put
         * @return the number of decoded bytes converted
         * @since 1.3
         */
        private static int decode4to3(final byte[] source, final int srcOffset, final byte[] destination, final int destOffset) {
            // Example: Dk==
            if (source[srcOffset + 2] == Base64.EQUALS_SIGN) {
                // Two ways to do the same thing. Don't know which way I like
                // best.
                // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 )
                // >>> 6 )
                // | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
                final int outBuff = ((Base64.DECODABET[source[srcOffset]] & 0xFF) << 18)
                        | ((Base64.DECODABET[source[srcOffset + 1]] & 0xFF) << 12);

                destination[destOffset] = (byte) (outBuff >>> 16);
                return 1;
            }

            // Example: DkL=
            else if (source[srcOffset + 3] == Base64.EQUALS_SIGN) {
                // Two ways to do the same thing. Don't know which way I like
                // best.
                // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 )
                // >>> 6 )
                // | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
                // | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
                final int outBuff = ((Base64.DECODABET[source[srcOffset]] & 0xFF) << 18)
                        | ((Base64.DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
                        | ((Base64.DECODABET[source[srcOffset + 2]] & 0xFF) << 6);

                destination[destOffset] = (byte) (outBuff >>> 16);
                destination[destOffset + 1] = (byte) (outBuff >>> 8);
                return 2;
            }

            // Example: DkLE
            else {
                try {
                    // Two ways to do the same thing. Don't know which way I
                    // like best.
                    // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24
                    // ) >>> 6 )
                    // | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12
                    // )
                    // | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18
                    // )
                    // | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24
                    // );
                    final int outBuff = ((Base64.DECODABET[source[srcOffset]] & 0xFF) << 18)
                            | ((Base64.DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
                            | ((Base64.DECODABET[source[srcOffset + 2]] & 0xFF) << 6)
                            | (Base64.DECODABET[source[srcOffset + 3]] & 0xFF);

                    destination[destOffset] = (byte) (outBuff >> 16);
                    destination[destOffset + 1] = (byte) (outBuff >> 8);
                    destination[destOffset + 2] = (byte) outBuff;

                    return 3;
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                    HTMLUtils.logger.debug("" + source[srcOffset] + ": " + Base64.DECODABET[source[srcOffset]]);
                    HTMLUtils.logger.debug("" + source[srcOffset + 1] + ": " + Base64.DECODABET[source[srcOffset + 1]]);
                    HTMLUtils.logger.debug("" + source[srcOffset + 2] + ": " + Base64.DECODABET[source[srcOffset + 2]]);
                    HTMLUtils.logger.debug("" + source[srcOffset + 3] + ": " + Base64.DECODABET[source[srcOffset + 3]]);
                    return -1;
                }
            }
        } // end decodeToBytes

        /**
         * Very low-level access to decoding ASCII characters in the form of a byte array. Does not support
         * automatically gunzipping or any other "fancy" features.
         * @param source The Base64 encoded data
         * @param off The offset of where to begin decoding
         * @param len The length of characters to decode
         * @return decoded data
         * @since 1.3
         */
        public static byte[] decode(final byte[] source, final int off, final int len) {
            final int len34 = (len * 3) / 4;
            final byte[] outBuff = new byte[len34]; // Upper limit on size of output
            int outBuffPosn = 0;

            final byte[] b4 = new byte[4];
            int b4Posn = 0;
            int i = 0;
            byte sbiCrop = 0;
            byte sbiDecode = 0;
            for (i = off; i < (off + len); i++) {
                sbiCrop = (byte) (source[i] & 0x7f); // Only the low seven bits
                sbiDecode = Base64.DECODABET[sbiCrop];

                if (sbiDecode >= Base64.WHITE_SPACE_ENC) // White space, Equals
                                                         // sign or better
                {
                    if (sbiDecode >= Base64.EQUALS_SIGN_ENC) {
                        b4[b4Posn++] = sbiCrop;
                        if (b4Posn > 3) {
                            outBuffPosn += Base64.decode4to3(b4, 0, outBuff, outBuffPosn);
                            b4Posn = 0;

                            // If that was the equals sign, break out of 'for'
                            // loop
                            if (sbiCrop == Base64.EQUALS_SIGN) {
                                break;
                            }
                        } // end if: quartet built

                    } // end if: equals sign or better

                } // end if: white space, equals sign or better
                else {
                    HTMLUtils.logger.debug("Bad Base64 input character at " + i + ": " + source[i] + "(decimal)");
                    return null;
                } // end else:
            } // each input character

            final byte[] out = new byte[outBuffPosn];
            System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
            return out;
        } // end decode

        /**
         * Decodes data from Base64 notation, automatically detecting gzip-compressed data and decompressing
         * it.
         * @param s the string to decode
         * @return the decoded data
         * @since 1.4
         */
        public static byte[] decode(final String s) {
            byte[] bytes;
            try {
                bytes = s.getBytes(Base64.PREFERRED_ENCODING);
            } catch (final java.io.UnsupportedEncodingException uee) {
                HTMLUtils.logger.trace(null, uee);
                bytes = s.getBytes();
            }
            // </change>

            // Decode
            bytes = Base64.decode(bytes, 0, bytes.length);

            // Check to see if it's gzip-compressed
            // GZIP Magic Two-Byte Number: 0x8b1f (35615)
            if ((bytes != null) && (bytes.length >= 4)) {

                final int head = (bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
                if (java.util.zip.GZIPInputStream.GZIP_MAGIC == head) {
                    java.io.ByteArrayInputStream bais = null;
                    java.util.zip.GZIPInputStream gzis = null;
                    java.io.ByteArrayOutputStream baos = null;
                    final byte[] buffer = new byte[2048];
                    int length = 0;

                    try {
                        baos = new java.io.ByteArrayOutputStream();
                        bais = new java.io.ByteArrayInputStream(bytes);
                        gzis = new java.util.zip.GZIPInputStream(bais);

                        while ((length = gzis.read(buffer)) >= 0) {
                            baos.write(buffer, 0, length);
                        } // end while: reading input

                        // No error? Get new bytes.
                        bytes = baos.toByteArray();

                    } catch (final java.io.IOException e) {
                        HTMLUtils.logger.trace(null, e);
                        // Just return originally-decoded bytes
                    } finally {
                        try {
                            baos.close();
                        } catch (final Exception e) {
                            HTMLUtils.logger.trace(null, e);
                        }
                        try {
                            gzis.close();
                        } catch (final Exception e) {
                            HTMLUtils.logger.trace(null, e);
                        }
                        try {
                            bais.close();
                        } catch (final Exception e) {
                            HTMLUtils.logger.trace(null, e);
                        }
                    }
                }
            }

            return bytes;
        }

        /**
         * Attempts to decode Base64 data and deserialize a Java Object within. Returns <tt>null</tt> if
         * there was an error.
         * @param encodedObject The Base64 data to decode
         * @return The decoded and deserialized object
         * @since 1.5
         */
        public static Object decodeToObject(final String encodedObject) {
            // Decode and gunzip if necessary
            final byte[] objBytes = Base64.decode(encodedObject);

            java.io.ByteArrayInputStream bais = null;
            java.io.ObjectInputStream ois = null;
            Object obj = null;

            try {
                bais = new java.io.ByteArrayInputStream(objBytes);
                ois = new java.io.ObjectInputStream(bais);

                obj = ois.readObject();
            } catch (final java.io.IOException e) {
                HTMLUtils.logger.error(null, e);
                obj = null;
            } catch (final java.lang.ClassNotFoundException e) {
                HTMLUtils.logger.error(null, e);
                obj = null;
            } finally {
                try {
                    bais.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
                try {
                    ois.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
            }
            return obj;
        }

        /**
         * Convenience method for encoding data to a file.
         * @param dataToEncode byte array of data to encode in base64 form
         * @param filename Filename for saving encoded data
         * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
         *
         * @since 2.1
         */
        public static boolean encodeToFile(final byte[] dataToEncode, final String filename) {
            boolean success = false;
            Base64.OutputStream bos = null;
            try {
                bos = new Base64.OutputStream(new java.io.FileOutputStream(filename), Base64.ENCODE);
                bos.write(dataToEncode);
                success = true;
            } catch (final java.io.IOException e) {
                HTMLUtils.logger.trace(null, e);
                success = false;
            } finally {
                try {
                    bos.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
            }
            return success;
        }

        /**
         * Convenience method for decoding data to a file.
         * @param dataToDecode Base64-encoded data as a string
         * @param filename Filename for saving decoded data
         * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
         *
         * @since 2.1
         */
        public static boolean decodeToFile(final String dataToDecode, final String filename) {
            boolean success = false;
            Base64.OutputStream bos = null;
            try {
                bos = new Base64.OutputStream(new java.io.FileOutputStream(filename), Base64.DECODE);
                bos.write(dataToDecode.getBytes(Base64.PREFERRED_ENCODING));
                success = true;
            } catch (final java.io.IOException e) {
                HTMLUtils.logger.trace(null, e);
                success = false;
            } finally {
                try {
                    bos.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
            }
            return success;
        }

        /**
         * Convenience method for reading a base64-encoded file and decoding it.
         * @param filename Filename for reading encoded data
         * @return decoded byte array or null if unsuccessful
         *
         * @since 2.1
         */
        public static byte[] decodeFromFile(final String filename) {
            byte[] decodedData = null;
            Base64.InputStream bis = null;
            try {
                // Set up some useful variables
                final java.io.File file = new java.io.File(filename);
                byte[] buffer = null;
                int length = 0;
                int numBytes = 0;

                // Check for size of file
                if (file.length() > Integer.MAX_VALUE) {
                    HTMLUtils.logger
                        .debug("File is too big for this convenience method (" + file.length() + " bytes).");
                    return null;
                } // end if: file too big for int index
                buffer = new byte[(int) file.length()];

                // Open a stream
                bis = new Base64.InputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(file)),
                        Base64.DECODE);

                // Read until done
                while ((numBytes = bis.read(buffer, length, 4096)) >= 0) {
                    length += numBytes;
                }

                // Save in a variable to return
                decodedData = new byte[length];
                System.arraycopy(buffer, 0, decodedData, 0, length);

            } catch (final java.io.IOException e) {
                HTMLUtils.logger.error("Error decoding from file " + filename, e);
            } finally {
                try {
                    bis.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
            }
            return decodedData;
        }

        /**
         * Convenience method for reading a binary file and base64-encoding it.
         * @param filename Filename for reading binary data
         * @return base64-encoded string or null if unsuccessful
         *
         * @since 2.1
         */
        public static String encodeFromFile(final String filename) {
            String encodedData = null;
            Base64.InputStream bis = null;
            try {
                // Set up some useful variables
                final java.io.File file = new java.io.File(filename);
                final byte[] buffer = new byte[(int) (file.length() * 1.4)];
                int length = 0;
                int numBytes = 0;

                // Open a stream
                bis = new Base64.InputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(file)),
                        Base64.ENCODE);

                // Read until done
                while ((numBytes = bis.read(buffer, length, 4096)) >= 0) {
                    length += numBytes;
                }

                // Save in a variable to return
                encodedData = new String(buffer, 0, length, Base64.PREFERRED_ENCODING);

            } catch (final java.io.IOException e) {
                HTMLUtils.logger.error("Error encoding from file " + filename, e);
            } finally {
                try {
                    bis.close();
                } catch (final Exception e) {
                    HTMLUtils.logger.trace(null, e);
                }
            }
            return encodedData;
        }

        /* ******** I N N E R C L A S S I N P U T S T R E A M ******** */

        /**
         * A {@link Base64.InputStream} will read data from another <tt>java.io.InputStream</tt>, given in
         * the constructor, and encode/decode to/from Base64 notation on the fly.
         *
         * @see Base64
         * @since 1.3
         */
        public static class InputStream extends java.io.FilterInputStream {

            private final boolean encode; // Encoding or decoding

            private int position; // Current position in the buffer

            private final byte[] buffer; // Small buffer holding converted data

            private final int bufferLength; // Length of buffer (3 or 4)

            private int numSigBytes; // Number of meaningful bytes in the buffer

            private int lineLength;

            private final boolean breakLines; // Break lines at less than 80
                                              // characters

            /**
             * Constructs a {@link Base64.InputStream} in DECODE mode.
             * @param in the <tt>java.io.InputStream</tt> from which to read data.
             * @since 1.3
             */
            public InputStream(final java.io.InputStream in) {
                this(in, Base64.DECODE);
            } // end constructor

            /**
             * Constructs a {@link Base64.InputStream} in either ENCODE or DECODE mode.
             * <p>
             * Valid options:
             *
             * <pre>
             *   ENCODE or DECODE: Encode or Decode as data is read.
             *   DONT_BREAK_LINES: don't break lines at 76 characters
             *     (only meaningful when encoding)
             *     <i>Note: Technically, this makes your encoding non-compliant.</i>
             * </pre>
             *
             * <p>
             * Example: <code>new Base64.InputStream( in, Base64.DECODE )</code>
             * @param in the <tt>java.io.InputStream</tt> from which to read data.
             * @param options Specified options
             * @see Base64#ENCODE
             * @see Base64#DECODE
             * @see Base64#DONT_BREAK_LINES
             * @since 2.0
             */
            public InputStream(final java.io.InputStream in, final int options) {
                super(in);
                this.breakLines = (options & Base64.DONT_BREAK_LINES) != Base64.DONT_BREAK_LINES;
                this.encode = (options & Base64.ENCODE) == Base64.ENCODE;
                this.bufferLength = this.encode ? 4 : 3;
                this.buffer = new byte[this.bufferLength];
                this.position = -1;
                this.lineLength = 0;
            } // end constructor

            /**
             * Reads enough of the input stream to convert to/from Base64 and returns the next byte.
             * @return next byte
             * @since 1.3
             */
            @Override
            public int read() throws java.io.IOException {
                // Do we need to get data?
                if (this.position < 0) {
                    if (this.encode) {
                        final byte[] b3 = new byte[3];
                        int numBinaryBytes = 0;
                        for (int i = 0; i < 3; i++) {
                            try {
                                final int b = this.in.read();

                                // If end of stream, b is -1.
                                if (b >= 0) {
                                    b3[i] = (byte) b;
                                    numBinaryBytes++;
                                } // end if: not end of stream

                            } catch (final java.io.IOException e) {
                                HTMLUtils.logger.trace(null, e);
                                // Only a problem if we got no data at all.
                                if (i == 0) {
                                    throw e;
                                }
                            }
                        }

                        if (numBinaryBytes > 0) {
                            Base64.encode3to4(b3, 0, numBinaryBytes, this.buffer, 0);
                            this.position = 0;
                            this.numSigBytes = 4;
                        } // end if: got data
                        else {
                            return -1;
                        } // end else
                    } // end if: encoding

                    // Else decoding
                    else {
                        final byte[] b4 = new byte[4];
                        int i = 0;
                        for (i = 0; i < 4; i++) {
                            // Read four "meaningful" bytes:
                            int b = 0;
                            do {
                                b = this.in.read();
                            } while ((b >= 0) && (Base64.DECODABET[b & 0x7f] <= Base64.WHITE_SPACE_ENC));

                            if (b < 0) {
                                break; // Reads a -1 if end of stream
                            }

                            b4[i] = (byte) b;
                        } // end for: each needed input byte

                        if (i == 4) {
                            this.numSigBytes = Base64.decode4to3(b4, 0, this.buffer, 0);
                            this.position = 0;
                        } // end if: got four characters
                        else if (i == 0) {
                            return -1;
                        } // end else if: also padded correctly
                        else {
                            // Must have broken out from above.
                            throw new java.io.IOException("Improperly padded Base64 input.");
                        } // end

                    } // end else: decode
                } // end else: get data

                // Got data?
                if (this.position >= 0) {
                    // End of relevant data?
                    if ( /* !encode && */this.position >= this.numSigBytes) {
                        return -1;
                    }

                    if (this.encode && this.breakLines && (this.lineLength >= Base64.MAX_LINE_LENGTH)) {
                        this.lineLength = 0;
                        return '\n';
                    } // end if
                    else {
                        this.lineLength++; // This isn't important when decoding
                        // but throwing an extra "if" seems
                        // just as wasteful.

                        final int b = this.buffer[this.position++];

                        if (this.position >= this.bufferLength) {
                            this.position = -1;
                        }

                        return b & 0xFF; // This is how you "cast" a byte that's
                                         // intended to be unsigned.
                    } // end else
                } // end if: position >= 0

                // Else error
                else {
                    // When JDK1.4 is more accepted, use an assertion here.
                    throw new java.io.IOException("Error in Base64 code reading stream.");
                } // end else
            } // end read

            /**
             * Calls {@link #read()} repeatedly until the end of stream is reached or <var>len</var> bytes are
             * read. Returns number of bytes read into array or -1 if end of stream is encountered.
             * @param dest array to hold values
             * @param off offset for array
             * @param len max number of bytes to read into array
             * @return bytes read into array or -1 if end of stream is encountered.
             * @since 1.3
             */
            @Override
            public int read(final byte[] dest, final int off, final int len) throws java.io.IOException {
                int i;
                int b;
                for (i = 0; i < len; i++) {
                    b = this.read();

                    // if( b < 0 && i == 0 )
                    // return -1;

                    if (b >= 0) {
                        dest[off + i] = (byte) b;
                    } else if (i == 0) {
                        return -1;
                    } else {
                        break; // Out of 'for' loop
                    }
                } // end for: each byte read
                return i;
            } // end read

        } // end inner class InputStream

        /* ******** I N N E R C L A S S O U T P U T S T R E A M ******** */

        /**
         * A {@link Base64.OutputStream} will write data to another <tt>java.io.OutputStream</tt>, given in
         * the constructor, and encode/decode to/from Base64 notation on the fly.
         *
         * @see Base64
         * @since 1.3
         */
        public static class OutputStream extends java.io.FilterOutputStream {

            private final boolean encode;

            private int position;

            private byte[] buffer;

            private final int bufferLength;

            private int lineLength;

            private final boolean breakLines;

            private final byte[] b4; // Scratch used in a few places

            private boolean suspendEncoding;

            /**
             * Constructs a {@link Base64.OutputStream} in ENCODE mode.
             * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
             * @since 1.3
             */
            public OutputStream(final java.io.OutputStream out) {
                this(out, Base64.ENCODE);
            } // end constructor

            /**
             * Constructs a {@link Base64.OutputStream} in either ENCODE or DECODE mode.
             * <p>
             * Valid options:
             *
             * <pre>
             *   ENCODE or DECODE: Encode or Decode as data is read.
             *   DONT_BREAK_LINES: don't break lines at 76 characters
             *     (only meaningful when encoding)
             *     <i>Note: Technically, this makes your encoding non-compliant.</i>
             * </pre>
             *
             * <p>
             * Example: <code>new Base64.OutputStream( out, Base64.ENCODE )</code>
             * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
             * @param options Specified options.
             * @see Base64#ENCODE
             * @see Base64#DECODE
             * @see Base64#DONT_BREAK_LINES
             * @since 1.3
             */
            public OutputStream(final java.io.OutputStream out, final int options) {
                super(out);
                this.breakLines = (options & Base64.DONT_BREAK_LINES) != Base64.DONT_BREAK_LINES;
                this.encode = (options & Base64.ENCODE) == Base64.ENCODE;
                this.bufferLength = this.encode ? 3 : 4;
                this.buffer = new byte[this.bufferLength];
                this.position = 0;
                this.lineLength = 0;
                this.suspendEncoding = false;
                this.b4 = new byte[4];
            } // end constructor

            /**
             * Writes the byte to the output stream after converting to/from Base64 notation. When encoding,
             * bytes are buffered three at a time before the output stream actually gets a write() call. When
             * decoding, bytes are buffered four at a time.
             * @param theByte the byte to write
             * @since 1.3
             */
            @Override
            public void write(final int theByte) throws java.io.IOException {
                // Encoding suspended?
                if (this.suspendEncoding) {
                    super.out.write(theByte);
                    return;
                } // end if: supsended

                // Encode?
                if (this.encode) {
                    this.buffer[this.position++] = (byte) theByte;
                    if (this.position >= this.bufferLength) // Enough to encode.
                    {
                        this.out.write(Base64.encode3to4(this.b4, this.buffer, this.bufferLength));

                        this.lineLength += 4;
                        if (this.breakLines && (this.lineLength >= Base64.MAX_LINE_LENGTH)) {
                            this.out.write(Base64.NEW_LINE);
                            this.lineLength = 0;
                        } // end if: end of line

                        this.position = 0;
                    } // end if: enough to output
                } // end if: encoding

                // Else, Decoding
                else {
                    // Meaningful Base64 character?
                    if (Base64.DECODABET[theByte & 0x7f] > Base64.WHITE_SPACE_ENC) {
                        this.buffer[this.position++] = (byte) theByte;
                        if (this.position >= this.bufferLength) // Enough to
                                                                // output.
                        {
                            final int len = Base64.decode4to3(this.buffer, 0, this.b4, 0);
                            this.out.write(this.b4, 0, len);
                            // out.write( Base64.decode4to3( buffer ) );
                            this.position = 0;
                        } // end if: enough to output
                    } // end if: meaningful base64 character
                    else if (Base64.DECODABET[theByte & 0x7f] != Base64.WHITE_SPACE_ENC) {
                        throw new java.io.IOException("Invalid character in Base64 data.");
                    } // end else: not white space either
                } // end else: decoding
            } // end write

            /**
             * Calls {@link #write(int)} repeatedly until <var>len</var> bytes are written.
             * @param theBytes array from which to read bytes
             * @param off offset for array
             * @param len max number of bytes to read into array
             * @since 1.3
             */
            @Override
            public void write(final byte[] theBytes, final int off, final int len) throws java.io.IOException {
                // Encoding suspended?
                if (this.suspendEncoding) {
                    super.out.write(theBytes, off, len);
                    return;
                } // end if: supsended

                for (int i = 0; i < len; i++) {
                    this.write(theBytes[off + i]);
                } // end for: each byte written

            } // end write

            /**
             * Method added by PHIL. [Thanks, PHIL. -Rob] This pads the buffer without closing the stream.
             */
            public void flushBase64() throws java.io.IOException {
                if (this.position > 0) {
                    if (this.encode) {
                        this.out.write(Base64.encode3to4(this.b4, this.buffer, this.position));
                        this.position = 0;
                    } // end if: encoding
                    else {
                        throw new java.io.IOException("Base64 input not properly padded.");
                    } // end else: decoding
                } // end if: buffer partially full

            } // end flush

            /**
             * Flushes and closes (I think, in the superclass) the stream.
             *
             * @since 1.3
             */
            @Override
            public void close() throws java.io.IOException {
                // 1. Ensure that pending characters are written
                this.flushBase64();

                // 2. Actually close the stream
                // Base class both flushes and closes.
                super.close();

                this.buffer = null;
                this.out = null;
            } // end close

            /**
             * Suspends encoding of the stream. May be helpful if you need to embed a piece of base640-encoded
             * data in a stream.
             *
             * @since 1.5.1
             */
            public void suspendEncoding() throws java.io.IOException {
                this.flushBase64();
                this.suspendEncoding = true;
            } // end suspendEncoding

            /**
             * Resumes encoding of the stream. May be helpful if you need to embed a piece of base640-encoded
             * data in a stream.
             *
             * @since 1.5.1
             */
            public void resumeEncoding() {
                this.suspendEncoding = false;
            } // end resumeEncoding

        } // end inner class OutputStream

    } // end class Base64

}
