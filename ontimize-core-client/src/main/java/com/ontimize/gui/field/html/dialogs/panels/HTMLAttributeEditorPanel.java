package com.ontimize.gui.field.html.dialogs.panels;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.JPanel;

import com.ontimize.gui.field.html.utils.I18n;
import com.ontimize.gui.i18n.Internationalization;

/**
 * This abstract class should be subclassed to create any sort of panel that is used to modify html
 * element attributes.. e.g an HTML table dialog
 *
 * @author Bob Tantlinger
 *
 */
public abstract class HTMLAttributeEditorPanel extends JPanel implements Internationalization {

    protected static final I18n i18n = I18n.getInstance();

    protected Map attribs = new HashMap();

    protected ResourceBundle resourceBundle;

    public HTMLAttributeEditorPanel() {
        super();
    }

    public HTMLAttributeEditorPanel(Map attribs) {
        super();
        this.attribs = attribs;
    }

    public void setAttributes(Map attribs) {
        this.attribs = attribs;
        this.updateComponentsFromAttribs();
    }

    public Map getAttributes() {
        this.updateAttribsFromComponents();
        return this.attribs;
    }

    /**
     * Subclasses should implement this method to set component values to the values in the attribs
     * hashtable.
     */
    public abstract void updateComponentsFromAttribs();

    /**
     * Subclasses should implement this method to set values in the attribs Map from the states of
     * any components on the panel.
     */
    public abstract void updateAttribsFromComponents();

    @Override
    public void setComponentLocale(Locale l) {

    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

}
