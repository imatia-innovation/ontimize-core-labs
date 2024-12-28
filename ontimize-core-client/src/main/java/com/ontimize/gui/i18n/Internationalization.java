package com.ontimize.gui.i18n;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface Internationalization {

    public static boolean DEBUG_LANGUAGE = false;

    public void setComponentLocale(Locale l);

    /**
     * Sets the language resource bundle. When the application changes the configured language, for all
     * the objects that implement this interface this method must be called with the new file. <br>
     * The method must translate all the elements that have texts in the GUI
     * @param resourceBundle the new language bundle.
     */
    public void setResourceBundle(ResourceBundle resourceBundle);

    /**
     * Returns all the texts in the element suitable for being translated.
     * @return all the texts in the element suitable for being translated.
     */
    public List getTextsToTranslate();

}
