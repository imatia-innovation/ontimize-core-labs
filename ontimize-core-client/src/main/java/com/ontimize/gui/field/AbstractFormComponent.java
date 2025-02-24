package com.ontimize.gui.field;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JComponent;

import com.ontimize.gui.Freeable;

public abstract class AbstractFormComponent extends JComponent implements FormComponent, Freeable {

    public static int defaultTopMargin = 5;

    public static int defaultBottomMargin = 5;

    public static int defaultRightMargin = 5;

    public static int defaultLeftMargin = 5;

    @Override
    public Object getConstraints(LayoutManager parentLayout) {
        if (parentLayout instanceof GridBagLayout) {
            return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH,
                    new Insets(AbstractFormComponent.defaultTopMargin, AbstractFormComponent.defaultLeftMargin,
                            AbstractFormComponent.defaultBottomMargin,
                            AbstractFormComponent.defaultRightMargin),
                    0, 0);
        } else {
            return null;
        }
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public void setResourceBundle(ResourceBundle res) {
    }

    @Override
    public void setComponentLocale(Locale l) {
        this.setLocale(l);
    }

}
