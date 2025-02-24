package com.ontimize.gui.field.html.dialogs;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.Icon;

import com.ontimize.gui.field.html.dialogs.panels.ImagePanel;
import com.ontimize.gui.field.html.utils.I18n;
import com.ontimize.gui.images.ImageManager;

public class ImageDialog extends HTMLOptionDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.getInstance();

    public static Icon icon = ImageManager.getIcon(ImageManager.IMAGE);

    public static String title = "HTMLShef.image";

    public static String desc = "HTMLShef.image_desc";

    protected ImagePanel imagePanel;

    public ImageDialog(Frame parent) {
        super(parent, ImageDialog.title, ImageDialog.desc, ImageDialog.icon);
        this.init();
    }

    public ImageDialog(Dialog parent) {
        super(parent, ImageDialog.title, ImageDialog.desc, ImageDialog.icon);
        this.init();
    }

    protected void init() {
        this.imagePanel = new ImagePanel();
        this.setContentPane(this.imagePanel);
        this.setSize(300, 345);
        this.setResizable(false);
    }

    public void setImageAttributes(Map attr) {
        this.imagePanel.setAttributes(attr);
    }

    public Map getImageAttributes() {
        return this.imagePanel.getAttributes();
    }

    protected String createImgAttributes(Map ht) {
        String html = "";

        boolean embed = false;
        if (ht.containsKey("embed") && "yes".equalsIgnoreCase((String) ht.get("embed"))) {
            embed = true;
        }

        for (Iterator e = ht.keySet().iterator(); e.hasNext();) {
            Object k = e.next();
            if (k.toString().equals("src") && embed) {
                html += " " + k + "=" + "\"data:image/png;base64," + ht.get(k) + "\"";
                continue;
            }
            html += " " + k + "=" + "\"" + ht.get(k) + "\"";
        }

        return html;
    }

    @Override
    public String getHTML() {
        Map imgAttr = this.imagePanel.getAttributes();

        String html = "";
        html += "<img" + this.createImgAttributes(imgAttr) + ">";

        return html;
    }

    @Override
    public void setComponentLocale(Locale l) {

    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        super.setResourceBundle(resourceBundle);
        if (this.imagePanel != null) {
            this.imagePanel.setResourceBundle(resourceBundle);
        }
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

}
