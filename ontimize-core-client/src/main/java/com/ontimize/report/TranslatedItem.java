package com.ontimize.report;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.i18n.Internationalization;

public class TranslatedItem implements Internationalization {

    private static final Logger logger = LoggerFactory.getLogger(TranslatedItem.class);

    protected String text = "";

    protected String translatedText = null;

    protected ResourceBundle res = null;

    public TranslatedItem(String text, ResourceBundle res) {
        this.text = text;
        this.translatedText = text;
        this.setResourceBundle(res);
    }

    @Override
    public void setResourceBundle(ResourceBundle res) {
        this.res = res;
        if (res != null) {
            try {
                this.translatedText = res.getString(this.text);
            } catch (Exception e) {
                TranslatedItem.logger.trace(null, e);
                this.translatedText = this.text;
            }
        }
    }

    @Override
    public void setComponentLocale(Locale l) {
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public String toString() {
        return this.translatedText;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.text.hashCode();
    }

}
