package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;

public class ImagePanel extends HTMLAttributeEditorPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected ImageAttributesPanel imageAttrPanel;

    public ImagePanel() {
        this(new Hashtable());
    }

    public ImagePanel(Map at) {
        super();
        this.initialize();
        this.setAttributes(at);
        this.updateComponentsFromAttribs();
    }

    protected String createAttribs(Map ht) {
        String html = "";
        for (Iterator e = ht.keySet().iterator(); e.hasNext();) {
            Object k = e.next();
            html += " " + k + "=" + "\"" + ht.get(k) + "\"";
        }

        return html;
    }

    @Override
    public void updateComponentsFromAttribs() {
        this.imageAttrPanel.setAttributes(this.attribs);
    }

    @Override
    public void updateAttribsFromComponents() {
        this.imageAttrPanel.updateAttribsFromComponents();
    }

    protected void initialize() {

        this.imageAttrPanel = new ImageAttributesPanel();
        this.imageAttrPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.setLayout(new BorderLayout());
        this.add(this.imageAttrPanel);

    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        super.setResourceBundle(resourceBundle);
        if (this.imageAttrPanel != null) {
            this.imageAttrPanel.setResourceBundle(resourceBundle);
        }
    }

}
