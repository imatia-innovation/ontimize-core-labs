package com.ontimize.gui.field.html.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;

/**
 * @author Imatia S.L.
 *
 */
public class SelectAllAction extends BasicEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SelectAllAction() {
        super(HTMLTextEditAction.i18n.str("HTMLShef.select_all"));
        this.putValue("ID", "HTMLShef.select_all");
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("HTMLShef.select_all")));

        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

        this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
    }

    @Override
    protected void doEdit(ActionEvent e, JEditorPane editor) {
        editor.selectAll();
    }

}
