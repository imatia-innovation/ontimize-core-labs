package com.ontimize.gui.field.html.actions;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

import org.bushe.swing.action.ActionManager;

import com.ontimize.gui.field.html.HTMLUtils;
import com.ontimize.gui.field.html.utils.CompoundUndoManager;
import com.ontimize.gui.images.ImageManager;

/**
 * Action which toggles inline HTML elements
 *
 * @author Imatia S.L.
 *
 */
public class HTMLInlineAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final int EM = 0;

    public static final int STRONG = 1;

    public static final int CODE = 2;

    public static final int CITE = 3;

    public static final int SUP = 4;

    public static final int SUB = 5;

    public static final int BOLD = 6;

    public static final int ITALIC = 7;

    public static final int UNDERLINE = 8;

    public static final int STRIKE = 9;

    private static final String[] INLINE_TYPES = { "HTMLShef.emphasis", "HTMLShef.strong", "HTMLShef.code",
            "HTMLShef.cite", "HTMLShef.superscript", "HTMLShef.subscript",
            "HTMLShef.bold", "HTMLShef.italic", "HTMLShef.underline", "HTMLShef.strikethrough" };

    private static final int[] MNEMS = { HTMLTextEditAction.i18n.mnem("HTMLShef.emphasis"), HTMLTextEditAction.i18n
        .mnem("HTMLShef.strong"), HTMLTextEditAction.i18n.mnem("HTMLShef.code"),
            HTMLTextEditAction.i18n.mnem("HTMLShef.cite"), HTMLTextEditAction.i18n
                .mnem("HTMLShef.superscript"),
            HTMLTextEditAction.i18n.mnem("HTMLShef.subscript"), HTMLTextEditAction.i18n.mnem("HTMLShef.bold"),
            HTMLTextEditAction.i18n
                .mnem("HTMLShef.italic"),
            HTMLTextEditAction.i18n.mnem("HTMLShef.underline"),
            HTMLTextEditAction.i18n.mnem("HTMLShef.strikethrough") };

    protected int type;

    /**
     * Creates a new HTMLInlineAction
     * @param itype an inline element type (BOLD, ITALIC, STRIKE, etc)
     * @throws IllegalArgumentException
     */
    public HTMLInlineAction(int itype) throws IllegalArgumentException {
        super("");
        this.type = itype;
        if ((this.type < 0) || (this.type >= HTMLInlineAction.INLINE_TYPES.length)) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        this.putValue("ID", HTMLInlineAction.INLINE_TYPES[this.type]);
        this.putValue(Action.NAME, HTMLTextEditAction.i18n.str(HTMLInlineAction.INLINE_TYPES[this.type]));
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLInlineAction.MNEMS[this.type]));

        Icon ico = null;
        KeyStroke ks = null;
        if (this.type == HTMLInlineAction.BOLD) {
            ico = ImageManager.getIcon(ImageManager.BOLD_FONT);
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        } else if (this.type == HTMLInlineAction.ITALIC) {
            ico = ImageManager.getIcon(ImageManager.ITALIC_FONT);
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        } else if (this.type == HTMLInlineAction.UNDERLINE) {
            ico = ImageManager.getIcon(ImageManager.UNDERLINE_FONT);
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK);
        }
        this.putValue(Action.SMALL_ICON, ico);
        this.putValue(Action.ACCELERATOR_KEY, ks);
        this.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_CHECKBOX);
        this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
    }

    @Override
    protected void updateContextState(JEditorPane ed) {
        this.setSelected(this.isDefined(HTMLUtils.getCharacterAttributes(ed)));
    }

    public HTML.Tag getTag() {
        return this.getTagForType(this.type);
    }

    protected HTML.Tag getTagForType(int type) {
        HTML.Tag tag = null;

        switch (type) {
            case EM:
                tag = HTML.Tag.EM;
                break;
            case STRONG:
                tag = HTML.Tag.STRONG;
                break;
            case CODE:
                tag = HTML.Tag.CODE;
                break;
            case SUP:
                tag = HTML.Tag.SUP;
                break;
            case SUB:
                tag = HTML.Tag.SUB;
                break;
            case CITE:
                tag = HTML.Tag.CITE;
                break;
            case BOLD:
                tag = HTML.Tag.B;
                break;
            case ITALIC:
                tag = HTML.Tag.I;
                break;
            case UNDERLINE:
                tag = HTML.Tag.U;
                break;
            case STRIKE:
                tag = HTML.Tag.STRIKE;
                break;
        }
        return tag;
    }

    @Override
    protected void editPerformed(ActionEvent e, JEditorPane editor) {
        CompoundUndoManager.beginCompoundEdit(editor.getDocument());
        this.toggleStyle(editor);
        CompoundUndoManager.endCompoundEdit(editor.getDocument());
    }

    protected boolean isDefined(AttributeSet attr) {
        boolean hasSC = false;
        if (this.type == HTMLInlineAction.SUP) {
            hasSC = StyleConstants.isSuperscript(attr);
        } else if (this.type == HTMLInlineAction.SUB) {
            hasSC = StyleConstants.isSubscript(attr);
        } else if (this.type == HTMLInlineAction.BOLD) {
            hasSC = StyleConstants.isBold(attr);
        } else if (this.type == HTMLInlineAction.ITALIC) {
            hasSC = StyleConstants.isItalic(attr);
        } else if (this.type == HTMLInlineAction.UNDERLINE) {
            hasSC = StyleConstants.isUnderline(attr);
        } else if (this.type == HTMLInlineAction.STRIKE) {
            hasSC = StyleConstants.isStrikeThrough(attr);
        }

        return hasSC || (attr.getAttribute(this.getTag()) != null);
    }

    protected void toggleStyle(JEditorPane editor) {
        MutableAttributeSet attr = new SimpleAttributeSet();
        attr.addAttributes(HTMLUtils.getCharacterAttributes(editor));
        boolean enable = !this.isDefined(attr);
        HTML.Tag tag = this.getTag();

        if (enable) {
            attr = new SimpleAttributeSet();
            attr.addAttribute(tag, new SimpleAttributeSet());
            // doesn't replace any attribs, just adds the new one
            HTMLUtils.setCharacterAttributes(editor, attr);
        } else {
            // Kind of a ham-fisted way to do this, but sometimes there are
            // CSS attributes, someties there are HTML.Tag attributes, and
            // sometimes
            // there are both. So, we have to remove 'em all to make sure this
            // type
            // gets completely disabled

            // remove the CSS style
            // STRONG, EM, CITE, CODE have no CSS analogs
            if (this.type == HTMLInlineAction.BOLD) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.FONT_WEIGHT, "bold");
            } else if (this.type == HTMLInlineAction.ITALIC) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.FONT_STYLE, "italic");
            } else if (this.type == HTMLInlineAction.UNDERLINE) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.TEXT_DECORATION, "underline");
            } else if (this.type == HTMLInlineAction.STRIKE) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.TEXT_DECORATION, "line-through");
            } else if (this.type == HTMLInlineAction.SUP) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.VERTICAL_ALIGN, "sup");
            } else if (this.type == HTMLInlineAction.SUB) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.VERTICAL_ALIGN, "sub");
            }

            HTMLUtils.removeCharacterAttribute(editor, tag); // make certain the
            // tag is also
            // removed
        }

        this.setSelected(enable);
    }

}
