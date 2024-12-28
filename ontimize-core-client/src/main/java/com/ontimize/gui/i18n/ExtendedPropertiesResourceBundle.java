package com.ontimize.gui.i18n;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ExtendedPropertiesResourceBundle extends ResourceBundle implements Serializable {

	protected Map values;

	protected Locale locale;

	public ExtendedPropertiesResourceBundle(final Map data, final Locale l) {
		this.values = data;
		if (this.values == null) {
			this.values = new Hashtable();
		}
		this.locale = l;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public Enumeration getKeys() {
		return Collections.enumeration(this.values.keySet());
	}

	@Override
	protected Object handleGetObject(final String key) {
		return this.values.get(key);
	}

	public Map getValues() {
		return this.values;
	}

	public void updateValues(final Map values) {
		this.values = values;
	}

}
