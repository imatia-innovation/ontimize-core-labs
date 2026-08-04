package com.ontimize.gui.field.html.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Action which clears inline text styles
 *
 * @author Imatia S.L.
 *
 */
public class ClearStylesAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ClearStylesAction() {
        super("HTMLShef.clear_styles");
        this.putValue("ID", "HTMLShef.clear_styles");
        this.putValue(Action.NAME, HTMLTextEditAction.i18n.str("HTMLShef.clear_styles"));
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("HTMLShef.clear_styles")));
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift ctrl Y"));

    }

    @Override
    protected void editPerformed(ActionEvent e, JEditorPane editor) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();

        MutableAttributeSet attrs = new SimpleAttributeSet();
        attrs.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);

        int selStart = editor.getSelectionStart();
        int selEnd = editor.getSelectionEnd();

        if (selEnd > selStart) {
            document.setCharacterAttributes(selStart, selEnd - selStart, attrs, true);
        }

        kit.getInputAttributes().removeAttributes(kit.getInputAttributes());
        kit.getInputAttributes().addAttributes(attrs);

    }

}
