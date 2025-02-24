package com.ontimize.util.swing;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComboBox;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.util.swing.list.I18nListCellRenderer;

public class TranslateComboBox extends JComboBox implements Internationalization {

	protected ResourceBundle bundle = null;

	public TranslateComboBox(final ResourceBundle bundle) {
		this.init(bundle);
	}

	public TranslateComboBox(final ResourceBundle bundle, final Vector v) {
		super(v);
		this.init(bundle);
	}

	private void init(final ResourceBundle bundle) {
		this.bundle = bundle;
		if (this.bundle != null) {
			super.setRenderer(new I18nListCellRenderer(this.bundle));
		}
	}

	/**
	 * getTextsToTranslate
	 * @return Vector
	 */
	@Override
	public List getTextsToTranslate() {
		return null;
	}

	/**
	 * setLocaleComponente
	 * @param locale Locale
	 */
	@Override
	public void setComponentLocale(final Locale locale) {
	}

	/**
	 * setResourceBundle
	 * @param resourceBundle ResourceBundle
	 */
	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.bundle = resourceBundle;
		super.setRenderer(new I18nListCellRenderer(this.bundle));
	}

}
