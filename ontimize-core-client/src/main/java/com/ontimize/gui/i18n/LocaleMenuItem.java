package com.ontimize.gui.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.RadioMenuItem;

public class LocaleMenuItem extends RadioMenuItem {

	private static final Logger logger = LoggerFactory.getLogger(LocaleMenuItem.class);

	protected Locale locale = Locale.getDefault();

	public LocaleMenuItem(final Map parameters) {
		super(parameters);
		final Object locale = parameters.get("locale");
		if (locale == null) {
			LocaleMenuItem.logger.debug("'locale' parameter not found");
		} else {
			final String loc = locale.toString();
			final StringTokenizer st = new StringTokenizer(loc.toString(), "_");
			String country = null;
			String language = null;
			String variant = "";// Swamper inicializada
			if (st.hasMoreTokens()) {
				language = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				country = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				variant = st.nextToken();
			}

			if ((language != null) && (country != null)) {
				this.locale = new Locale(language, country, variant);
			} else {
				LocaleMenuItem.logger.debug(
						this.getClass().toString() + ". Parameter 'locale' incorrect: Example: locale='es_ES_VARIANT'");
			}
		}
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

}
