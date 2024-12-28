package com.ontimize.gui.attachment;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ontimize.gui.i18n.Internationalization;

public class EJFile extends JFileChooser implements Internationalization {

    protected JDescriptionPanel panel = null;

    public void setPanel(JDescriptionPanel panel) {
        this.panel = panel;
    }

    public JDescriptionPanel getPanel() {
        return this.panel;
    }

    @Override
    public List getTextsToTranslate() {
        if (this.panel != null) {
            return this.panel.getTextsToTranslate();
        }
        return null;
    }

    @Override
    public void setComponentLocale(Locale l) {
        if (this.panel != null) {
            this.panel.setComponentLocale(l);
        }
    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        if (this.panel != null) {
            this.panel.setResourceBundle(resourceBundle);
        }

    }

}
