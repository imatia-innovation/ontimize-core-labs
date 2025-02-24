package com.ontimize.util.swing.list;

import java.awt.Component;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;

public class I18nListCellRenderer extends DefaultListCellRenderer implements Internationalization {

    private static final Logger logger = LoggerFactory.getLogger(I18nListCellRenderer.class);

    protected ResourceBundle bundle = null;

    public I18nListCellRenderer() {
        try {
            this.bundle = ApplicationManager.getApplication().getResourceBundle();
        } catch (Exception e) {
            I18nListCellRenderer.logger.trace(null, e);
        }
    }

    public I18nListCellRenderer(ResourceBundle resource) {
        this.bundle = resource;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value != null) {
            ((JLabel) c).setText(ApplicationManager.getTranslation(value.toString(), this.bundle));
        }
        return c;
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public void setComponentLocale(Locale l) {

    }

    @Override
    public void setResourceBundle(ResourceBundle recursos) {
        this.bundle = recursos;
    }

}
