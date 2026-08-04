package com.ontimize.gui.field.html.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.bushe.swing.action.ActionManager;
import org.bushe.swing.action.ShouldBeEnabledDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.html.utils.CompoundUndoManager;
import com.ontimize.gui.images.ImageManager;

public class PasteAction extends BasicEditAction {

    private static final Logger logger = LoggerFactory.getLogger(PasteAction.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PasteAction() {
        super(HTMLTextEditAction.i18n.str("HTMLShef.paste"));
        this.putValue("ID", "HTMLShef.paste");
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("HTMLShef.paste")));
        this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.PASTE));
        this.putValue(ActionManager.LARGE_ICON, ImageManager.getIcon(ImageManager.PASTE));
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        this.addShouldBeEnabledDelegate(new ShouldBeEnabledDelegate() {

            @Override
            public boolean shouldBeEnabled(Action a) {
                // return getCurrentEditor() != null &&
                // Toolkit.getDefaultToolkit().getSystemClipboard().getContents(PasteAction.this)
                // != null;
                return true;
            }
        });

        this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
    }

    @Override
    protected void updateContextState(JEditorPane wysEditor) {
        this.updateEnabledState();
    }

    @Override
    protected void doEdit(ActionEvent e, JEditorPane editor) {
        HTMLEditorKit ekit = (HTMLEditorKit) editor.getEditorKit();
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

        try {
            CompoundUndoManager.beginCompoundEdit(document);
            Transferable content = clip.getContents(this);
            String txt = content.getTransferData(new DataFlavor(String.class, "String")).toString();

            document.replace(editor.getSelectionStart(), editor.getSelectionEnd() - editor.getSelectionStart(), txt,
                    ekit.getInputAttributes());

        } catch (Exception ex) {
            PasteAction.logger.trace(null, ex);
        } finally {
            CompoundUndoManager.endCompoundEdit(document);
        }
    }

}
