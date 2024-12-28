package com.ontimize.gui.attachment;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.util.swing.ButtonSelection;

public class AttachmentMenuListener implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentMenuListener.class);

    protected Form form;

    public AttachmentMenuListener(Form form) {
        this.form = form;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ButtonSelection button = (ButtonSelection) SwingUtilities.getAncestorOfClass(ButtonSelection.class,
                (Component) e.getSource());
        if (button != null) {

            JList jList = button.getMenuList();
            AttachmentListPopup attachmentList = null;
            if (jList instanceof AttachmentListPopup) {
                attachmentList = (AttachmentListPopup) jList;
            }

            if (attachmentList == null) {
                attachmentList = new AttachmentListPopup(this.form);
                button.setMenuList(attachmentList);
            }

            // EntityReferenceLocator locator =
            // this.form.getFormManager().getReferenceLocator();
            // try {
            // Entity attachmentEntity = ((UtilReferenceLocator)
            // locator).getAttachmentEntity(locator.getSessionId());
            // List v = this.form.getKeys();
            // Map kv = new Hashtable();
            // Collections.sort(v);
            // for (int i = 0; i < v.size(); i++) {
            // Object o = this.form.getDataFieldValue((String) v.get(i));
            // if (o != null) {
            // kv.put(v.get(i), o);
            // }
            // }
            // kv.put(Form.ATTACHMENT_ENTITY_NAME, this.form.getEntityName());
            // EntityResult res = attachmentEntity.query(kv, new Vector(),
            // locator.getSessionId());
            // if (ApplicationManager.DEBUG) {
            // AttachmentMenuListener.logger.debug("attachments: " + res);
            // }
            // attachmentList.setAttachments(res);

            // } catch (Exception ex) {
            // AttachmentMenuListener.logger.error(null, ex);
            // }

            attachmentList.show(button, 0, button.getHeight());
        }
    }

}
