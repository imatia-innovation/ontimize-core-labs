package com.ontimize.gui.field.html.actions;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.bushe.swing.action.ActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.html.HTMLUtils;
import com.ontimize.gui.field.html.utils.CompoundUndoManager;
import com.ontimize.gui.field.html.utils.ElementWriter;
import com.ontimize.gui.images.ImageManager;

/**
 * Action which formats HTML block level elements
 *
 * @author Imatia S.L.
 *
 */
public class HTMLBlockAction extends HTMLTextEditAction {

    private static final Logger logger = LoggerFactory.getLogger(HTMLBlockAction.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final int DIV = 0;

    public static final int P = 1;

    public static final int H1 = 2;

    public static final int H2 = 3;

    public static final int H3 = 4;

    public static final int H4 = 5;

    public static final int H5 = 6;

    public static final int H6 = 7;

    public static final int PRE = 8;

    public static final int BLOCKQUOTE = 9;

    public static final int OL = 10;

    public static final int UL = 11;

    private static final int KEYS[] = { KeyEvent.VK_D, KeyEvent.VK_ENTER, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3,
            KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_R, KeyEvent.VK_Q, KeyEvent.VK_N, KeyEvent.VK_U };

    private static final String[] ELEMENT_TYPES = { "HTMLShef.body_text", "HTMLShef.paragraph", "HTMLShef.heading_1",
            "HTMLShef.heading_2", "HTMLShef.heading_3",
            "HTMLShef.heading_4", "HTMLShef.heading_5", "HTMLShef.heading_6", "HTMLShef.preformatted",
            "HTMLShef.blockquote", "HTMLShef.ordered_list", "HTMLShef.unordered_list" };

    protected int type;

    /**
     * Creates a new HTMLBlockAction
     * @param type A block type - P, PRE, BLOCKQUOTE, H1, H2, etc
     * @throws IllegalArgumentException
     */
    public HTMLBlockAction(int type) throws IllegalArgumentException {
        super("");
        if ((type < 0) || (type >= HTMLBlockAction.ELEMENT_TYPES.length)) {
            throw new IllegalArgumentException("Illegal argument");
        }

        this.type = type;
        this.putValue("ID", HTMLBlockAction.ELEMENT_TYPES[type]);
        this.putValue(Action.NAME, HTMLTextEditAction.i18n.str(HTMLBlockAction.ELEMENT_TYPES[type]));
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(HTMLBlockAction.KEYS[type], Event.ALT_MASK));
        if (type == HTMLBlockAction.P) {
            this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("paragraph")));
        } else if (type == HTMLBlockAction.PRE) {
            this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("preformatted")));
        } else if (type == HTMLBlockAction.BLOCKQUOTE) {
            this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("blockquote")));
        } else if (type == HTMLBlockAction.OL) {
            this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.LIST_ORDERED));
            this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("ordered_list")));
        } else if (type == HTMLBlockAction.UL) {
            this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.LIST_UNORDERED));
            this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("unordered_list")));
        } else {
            String s = type + "";
            this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(s.charAt(0)));
        }
        this.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);
        this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
    }

    @Override
    protected void updateContextState(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();
        Element elem = document.getParagraphElement(ed.getCaretPosition());

        String elemName = elem.getName();
        if (elemName.equals("p-implied")) {
            elemName = elem.getParentElement().getName();
        }

        if ((this.type == HTMLBlockAction.DIV)
                && (elemName.equals("div") || elemName.equals("body") || elemName.equals("td"))) //$NON-NLS-3$
        {
            this.setSelected(true);
        } else if (this.type == HTMLBlockAction.UL) {
            Element listElem = HTMLUtils.getListParent(elem);
            this.setSelected((listElem != null) && listElem.getName().equals("ul"));
        } else if (this.type == HTMLBlockAction.OL) {
            Element listElem = HTMLUtils.getListParent(elem);
            this.setSelected((listElem != null) && listElem.getName().equals("ol"));
        } else if (elemName.equals(this.getTag().toString().toLowerCase())) {
            this.setSelected(true);
        } else {
            this.setSelected(false);
        }
    }

    @Override
    protected void editPerformed(ActionEvent e, JEditorPane editor) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        int caret = editor.getCaretPosition();
        CompoundUndoManager.beginCompoundEdit(document);
        try {
            if ((this.type == HTMLBlockAction.OL) || (this.type == HTMLBlockAction.UL)) {
                this.insertList(editor, e);
            } else {
                this.changeBlockType(editor, e);
            }
            editor.setCaretPosition(caret);
        } catch (Exception awwCrap) {
            HTMLBlockAction.logger.error(null, awwCrap);
        }

        CompoundUndoManager.endCompoundEdit(document);
    }

    protected HTML.Tag getRootTag(Element elem) {
        HTML.Tag root = HTML.Tag.BODY;
        if (HTMLUtils.getParent(elem, HTML.Tag.TD) != null) {
            root = HTML.Tag.TD;
        }
        return root;
    }

    /*
     * protected String cutOutElement(Element el) throws BadLocationException { String txt =
     * HTMLUtils.getElementHTML(el, false); HTMLUtils.removeElement(el); return txt; }
     */

    protected void insertHTML(String html, HTML.Tag tag, HTML.Tag root, ActionEvent e) {
        HTMLEditorKit.InsertHTMLTextAction a = new HTMLEditorKit.InsertHTMLTextAction("insertHTML", html, root, tag);
        a.actionPerformed(e);
    }

    protected void changeListType(Element listParent, HTML.Tag replaceTag, HTMLDocument document) {
        StringWriter out = new StringWriter();
        ElementWriter w = new ElementWriter(out, listParent);
        try {
            w.write();
            String html = out.toString();
            html = html.substring(html.indexOf('>') + 1, html.length());
            html = html.substring(0, html.lastIndexOf('<'));
            html = '<' + replaceTag.toString() + '>' + html + "</" + replaceTag.toString() + '>';
            document.setOuterHTML(listParent, html);
        } catch (Exception idiotic) {
            HTMLBlockAction.logger.trace(null, idiotic);
        }
    }

    protected void insertList(JEditorPane editor, ActionEvent e) throws BadLocationException {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        int caretPos = editor.getCaretPosition();
        Element elem = document.getParagraphElement(caretPos);
        HTML.Tag parentTag = HTML.getTag(elem.getParentElement().getName());

        // check if we need to change the list from one type to another
        Element listParent = elem.getParentElement().getParentElement();
        HTML.Tag listTag = HTML.getTag(listParent.getName());
        if (listTag.equals(HTML.Tag.UL) || listTag.equals(HTML.Tag.OL)) {
            HTML.Tag t = HTML.getTag(listParent.getName());
            if ((this.type == HTMLBlockAction.OL) && t.equals(HTML.Tag.UL)) {
                this.changeListType(listParent, HTML.Tag.OL, document);
                return;
            } else if ((this.type == HTMLBlockAction.UL) && listTag.equals(HTML.Tag.OL)) {
                this.changeListType(listParent, HTML.Tag.UL, document);
                return;
            }
        }

        if (!parentTag.equals(HTML.Tag.LI))// don't allow nested lists
        {
            this.changeBlockType(editor, e);
        } else// is already a list, so turn off list
        {
            HTML.Tag root = this.getRootTag(elem);
            String txt = HTMLUtils.getElementHTML(elem, false);
            editor.setCaretPosition(elem.getEndOffset());
            this.insertHTML("<p>" + txt + "</p>", HTML.Tag.P, root, e);
            HTMLUtils.removeElement(elem);
        }

    }

    protected void changeBlockType(JEditorPane editor, ActionEvent e) throws BadLocationException {
        HTMLDocument doc = (HTMLDocument) editor.getDocument();
        Element curE = doc.getParagraphElement(editor.getSelectionStart());
        Element endE = doc.getParagraphElement(editor.getSelectionEnd());

        Element curTD = HTMLUtils.getParent(curE, HTML.Tag.TD);
        HTML.Tag tag = this.getTag();
        HTML.Tag rootTag = this.getRootTag(curE);
        String html = "";

        if (this.isListType()) {
            html = "<" + this.getTag() + ">";
            tag = HTML.Tag.LI;
        }

        // a list to hold the elements we want to change
        List elToRemove = new ArrayList();
        elToRemove.add(curE);

        while (true) {
            html += HTMLUtils.createTag(tag, curE.getAttributes(), HTMLUtils.getElementHTML(curE, false));
            if ((curE.getEndOffset() >= endE.getEndOffset()) || (curE.getEndOffset() >= doc.getLength())) {
                break;
            }
            curE = doc.getParagraphElement(curE.getEndOffset() + 1);
            elToRemove.add(curE);

            // did we enter a (different) table cell?
            Element ckTD = HTMLUtils.getParent(curE, HTML.Tag.TD);
            if ((ckTD != null) && !ckTD.equals(curTD)) {
                break;// stop here so we don't mess up the table
            }
        }

        if (this.isListType()) {
            html += "</" + this.getTag() + ">";
        }

        // set the caret to the start of the last selected block element
        editor.setCaretPosition(curE.getStartOffset());

        // insert our changed block
        // we insert first and then remove, because of a bug in jdk 6.0
        this.insertHTML(html, this.getTag(), rootTag, e);

        // now, remove the elements that were changed.
        for (Iterator it = elToRemove.iterator(); it.hasNext();) {
            Element c = (Element) it.next();
            HTMLUtils.removeElement(c);
        }
    }

    protected boolean isListType() {
        return (this.type == HTMLBlockAction.OL) || (this.type == HTMLBlockAction.UL);
    }

    /**
     * Gets the tag
     * @return
     */
    public HTML.Tag getTag() {
        HTML.Tag tag = HTML.Tag.DIV;

        switch (this.type) {
            case P:
                tag = HTML.Tag.P;
                break;
            case H1:
                tag = HTML.Tag.H1;
                break;
            case H2:
                tag = HTML.Tag.H2;
                break;
            case H3:
                tag = HTML.Tag.H3;
                break;
            case H4:
                tag = HTML.Tag.H4;
                break;
            case H5:
                tag = HTML.Tag.H5;
                break;
            case H6:
                tag = HTML.Tag.H6;
                break;
            case PRE:
                tag = HTML.Tag.PRE;
                break;
            case UL:
                tag = HTML.Tag.UL;
                break;
            case OL:
                tag = HTML.Tag.OL;
                break;
            case BLOCKQUOTE:
                tag = HTML.Tag.BLOCKQUOTE;
                break;
            case DIV:
                tag = HTML.Tag.DIV;
                break;
        }

        return tag;
    }

}
