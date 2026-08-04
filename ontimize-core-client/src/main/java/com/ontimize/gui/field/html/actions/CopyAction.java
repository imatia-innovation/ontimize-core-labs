package com.ontimize.gui.field.html.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;

import org.bushe.swing.action.ActionManager;
import org.bushe.swing.action.ShouldBeEnabledDelegate;

import com.ontimize.gui.images.ImageManager;

/**
 * @author Imatia S.L.
 *
 */
public class CopyAction extends BasicEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CopyAction() {
        super("");
        this.putValue("ID", "HTMLShef.copy");
        this.putValue(Action.NAME, HTMLTextEditAction.i18n.str("HTMLShef.copy"));
        this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.COPY));
        this.putValue(ActionManager.LARGE_ICON, ImageManager.getIcon(ImageManager.COPY));
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("HTMLShef.copy")));
        this.addShouldBeEnabledDelegate(new ShouldBeEnabledDelegate() {

            @Override
            public boolean shouldBeEnabled(Action a) {
                JEditorPane ed = CopyAction.this.getCurrentEditor();
                return (ed != null) && (ed.getSelectionStart() != ed.getSelectionEnd());
                // return true;
            }
        });
        this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
    }

    @Override
    protected void doEdit(ActionEvent e, JEditorPane editor) {
        editor.copy();
    }

    @Override
    protected void contextChanged() {
        super.contextChanged();
        this.updateEnabledState();
    }

}
