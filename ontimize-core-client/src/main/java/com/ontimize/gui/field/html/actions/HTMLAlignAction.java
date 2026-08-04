package com.ontimize.gui.field.html.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.bushe.swing.action.ActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.html.HTMLUtils;
import com.ontimize.gui.field.html.utils.CompoundUndoManager;
import com.ontimize.gui.images.ImageManager;

/**
 * Action which aligns HTML elements
 *
 * @author Imatia S.L.
 *
 */
public class HTMLAlignAction extends HTMLTextEditAction {

    private static final Logger logger = LoggerFactory.getLogger(HTMLAlignAction.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final int LEFT = 0;

    public static final int CENTER = 1;

    public static final int RIGHT = 2;

    public static final int JUSTIFY = 3;

    private static final String[] ALIGNMENT_NAMES = { "HTMLShef.left", "HTMLShef.center", "HTMLShef.right",
            "HTMLShef.justify" };

    private static final int[] MNEMS = { HTMLTextEditAction.i18n.mnem("HTMLShef.left"),
            HTMLTextEditAction.i18n.mnem("HTMLShef.center"), HTMLTextEditAction.i18n
                .mnem("HTMLShef.right"),
            HTMLTextEditAction.i18n.mnem("HTMLShef.justify") };

    public static int[] getMnems() {
        return HTMLAlignAction.MNEMS;
    }

    private static final String[] ALIGNMENTS = { "left", "center", "right", "justify" };

    public static String[] getAlignments() {
        return HTMLAlignAction.ALIGNMENTS;
    }

    private static final String[] IMGS = { ImageManager.LEFT_ALIGN, ImageManager.CENTER_ALIGN, ImageManager.RIGHT_ALIGN,
            ImageManager.JUSTIFY_ALIGN };

    protected int align;

    /**
     * Creates a new HTMLAlignAction
     * @param al LEFT, RIGHT, CENTER, or JUSTIFY
     * @throws IllegalArgumentException
     */
    public HTMLAlignAction(int al) throws IllegalArgumentException {
        super("");
        if ((al < 0) || (al >= HTMLAlignAction.ALIGNMENTS.length)) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        this.putValue("ID", HTMLAlignAction.ALIGNMENT_NAMES[al]);
        this.putValue(Action.NAME, HTMLTextEditAction.i18n.str(HTMLAlignAction.ALIGNMENT_NAMES[al]));
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLAlignAction.MNEMS[al]));

        this.putValue(Action.SMALL_ICON, ImageManager.getIcon(HTMLAlignAction.IMGS[al]));
        this.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);

        this.align = al;
    }

    @Override
    protected void updateContextState(JEditorPane ed) {
        this.setSelected(this.shouldBeSelected(ed));
    }

    protected boolean shouldBeSelected(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();
        Element elem = document.getParagraphElement(ed.getCaretPosition());
        if (HTMLUtils.isImplied(elem)) {
            elem = elem.getParentElement();
        }

        AttributeSet at = elem.getAttributes();
        return at.containsAttribute(HTML.Attribute.ALIGN, HTMLAlignAction.ALIGNMENTS[this.align]);
    }

    @Override
    protected void editPerformed(ActionEvent e, JEditorPane editor) {
        HTMLDocument doc = (HTMLDocument) editor.getDocument();
        Element curE = doc.getParagraphElement(editor.getSelectionStart());
        Element endE = doc.getParagraphElement(editor.getSelectionEnd());

        CompoundUndoManager.beginCompoundEdit(doc);
        while (true) {
            this.alignElement(curE);
            if ((curE.getEndOffset() >= endE.getEndOffset()) || (curE.getEndOffset() >= doc.getLength())) {
                break;
            }
            curE = doc.getParagraphElement(curE.getEndOffset() + 1);
        }
        CompoundUndoManager.endCompoundEdit(doc);
    }

    protected void alignElement(Element elem) {
        HTMLDocument doc = (HTMLDocument) elem.getDocument();

        if (HTMLUtils.isImplied(elem)) {
            HTML.Tag tag = HTML.getTag(elem.getParentElement().getName());
            // pre tag doesn't support an align attribute
            // http://www.w3.org/TR/REC-html32#pre
            if ((tag != null) && !tag.equals(HTML.Tag.BODY) && !tag.isPreformatted() && !tag.equals(HTML.Tag.DD)) {
                SimpleAttributeSet as = new SimpleAttributeSet(elem.getAttributes());
                as.removeAttribute("align");
                as.addAttribute("align", HTMLAlignAction.ALIGNMENTS[this.align]);

                Element parent = elem.getParentElement();
                String html = HTMLUtils.getElementHTML(elem, false);
                html = HTMLUtils.createTag(tag, as, html);
                String snipet = "";
                for (int i = 0; i < parent.getElementCount(); i++) {
                    Element el = parent.getElement(i);
                    if (el == elem) {
                        snipet += html;
                    } else {
                        snipet += HTMLUtils.getElementHTML(el, true);
                    }
                }

                try {
                    doc.setOuterHTML(parent, snipet);
                } catch (Exception ex) {
                    HTMLAlignAction.logger.error(null, ex);
                }
            }
        } else {
            // Set the HTML attribute on the paragraph...
            MutableAttributeSet set = new SimpleAttributeSet(elem.getAttributes());
            set.removeAttribute(HTML.Attribute.ALIGN);
            set.addAttribute(HTML.Attribute.ALIGN, HTMLAlignAction.ALIGNMENTS[this.align]);
            // Set the paragraph attributes...
            int start = elem.getStartOffset();
            int length = elem.getEndOffset() - elem.getStartOffset();
            doc.setParagraphAttributes(start, length - 1, set, true);
        }
    }

}
