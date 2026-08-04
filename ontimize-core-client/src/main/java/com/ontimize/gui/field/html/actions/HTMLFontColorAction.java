package com.ontimize.gui.field.html.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledEditorKit;

import com.ontimize.gui.images.ImageManager;

/**
 * Action which edits HTML font color
 *
 * @author Imatia S.L.
 *
 */
public class HTMLFontColorAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HTMLFontColorAction() {
        super(HTMLTextEditAction.i18n.str("HTMLShef.color_"));
        this.putValue("ID", "HTMLShef.color_");
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("HTMLShef.color_")));
        this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.CHOOSE_COLOR));
    }

    @Override
    protected void editPerformed(ActionEvent e, JEditorPane editor) {
        Color color = this.getColorFromUser(editor);
        if (color != null) {
            Action a = new StyledEditorKit.ForegroundAction("Color", color);
            a.actionPerformed(e);
        }
    }

    protected Color getColorFromUser(Component c) {
        Window win = SwingUtilities.getWindowAncestor(c);
        if (win != null) {
            c = win;
        }
        Color color = JColorChooser.showDialog(c, HTMLTextEditAction.i18n.str("ColorDataField.chooseColor"),
                Color.black);
        return color;
    }

}
