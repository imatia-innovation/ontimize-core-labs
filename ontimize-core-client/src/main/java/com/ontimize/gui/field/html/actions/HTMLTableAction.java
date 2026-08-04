/*
 * Created on Feb 26, 2005
 */
package com.ontimize.gui.field.html.actions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.html.HTMLUtils;
import com.ontimize.gui.field.html.dialogs.NewTableDialog;
import com.ontimize.gui.field.html.utils.CompoundUndoManager;
import com.ontimize.gui.images.ImageManager;

/**
 * Action which shows a dialog to insert an HTML table
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLTableAction extends HTMLTextEditAction {

    private static final Logger logger = LoggerFactory.getLogger(HTMLTableAction.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HTMLTableAction() {
        super(HTMLTextEditAction.i18n.str("HTMLShef.table_"));
        this.putValue("ID", "HTMLShef.table_");
        this.putValue(Action.MNEMONIC_KEY, Integer.valueOf(HTMLTextEditAction.i18n.mnem("HTMLShef.table_")));

        this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.HTML_TABLE));
        this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
    }

    @Override
    protected void editPerformed(ActionEvent e, JEditorPane editor) {
        NewTableDialog dlg = this.createNewTableDialog(editor);
        if (dlg == null) {
            return;
        }
        dlg.setLocationRelativeTo(dlg.getParent());
        dlg.setVisible(true);
        if (dlg.hasUserCancelled()) {
            return;
        }

        HTMLDocument document = (HTMLDocument) editor.getDocument();
        String html = dlg.getHTML();

        Element elem = document.getParagraphElement(editor.getCaretPosition());
        CompoundUndoManager.beginCompoundEdit(document);
        try {
            if (HTMLUtils.isElementEmpty(elem)) {
                document.setOuterHTML(elem, html);
            } else if (elem.getName().equals("p-implied")) {
                document.insertAfterEnd(elem, html);
            } else {
                HTMLUtils.insertHTML(html, HTML.Tag.TABLE, editor);
            }
        } catch (Exception ex) {
            HTMLTableAction.logger.error(null, ex);
        }
        CompoundUndoManager.endCompoundEdit(document);
    }

    /**
     * Creates the dialog
     * @param ed
     * @return the dialog
     */
    protected NewTableDialog createNewTableDialog(JTextComponent ed) {
        Window w = SwingUtilities.getWindowAncestor(ed);
        NewTableDialog d = null;
        if ((w != null) && (w instanceof Frame)) {
            d = new NewTableDialog((Frame) w);
        } else if ((w != null) && (w instanceof Dialog)) {
            d = new NewTableDialog((Dialog) w);
        }

        return d;
    }

}
