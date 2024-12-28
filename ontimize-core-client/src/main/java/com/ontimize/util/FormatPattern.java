package com.ontimize.util;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;

/**
 * Utility class to create a pattern String with a translation key and the given values.
 *
 * @author Imatia Innovation S.L.
 * @since Ontimize 5.2059EN
 */
public class FormatPattern implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(FormatPattern.class);

	protected static final String SEPARATOR = ";";

	protected String message = new String();

	protected List params = new ArrayList();

	protected DateFormat dateFormat = SimpleDateFormat.getDateInstance();

	protected ResourceBundle rb = null;

	protected boolean useBundle;

	public FormatPattern(final String pattern) {
		this(pattern, true);
	}

	public FormatPattern(final String pattern, final boolean useBundle) {
		this.useBundle = useBundle;
		if ((pattern != null) && (pattern.length() > 0)) {
			final StringTokenizer s = new StringTokenizer(pattern, FormatPattern.SEPARATOR);
			this.message = s.nextToken();

			while (s.hasMoreTokens()) {
				final String token = s.nextToken();
				this.params.add(token);
			}
		}

	}

	public boolean isEmpty() {
		return (this.message == null) || (this.message.length() == 0);
	}

	public String getMessage() {
		return this.message;
	}

	public List getParams() {
		return this.params;
	}

	public DateFormat getDateFormat() {
		return this.dateFormat;
	}

	public void setDateFormat(final String dateFormat) {
		try {
			final DateFormat df = (dateFormat != null) && (dateFormat.length() > 0) ? new SimpleDateFormat(dateFormat)
					: DateFormat.getDateInstance();
			this.setDateFormat(df);
		} catch (final Exception t) {
			if (ApplicationManager.DEBUG) {
				FormatPattern.logger.debug("Invalid dateformat " + dateFormat, t);
			}
		}
	}

	public void setDateFormat(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String parse(final int index, final Map values) {
		final Object[] params = this.createValues(index, values);
		final String s = this.parse(params);
		return s;
	}

	protected Object[] createValues(final int index, final Map values) {
		if ((values == null) || values.isEmpty()) {
			return null;
		}

		final List lValues = new ArrayList();
		for (int i = 0, size = this.params.size(); i < size; i++) {
			final Object key = this.params.get(i);
			Object val = values.get(key);
			if ((index >= 0) && (val instanceof List)) {
				final List v = (List) val;
				if (index < v.size()) {
					val = v.get(index);
				}
			}

			lValues.add(val != null ? val : new String());
		}
		return lValues.toArray();
	}

	public String parse(final Object param) {
		return this.parse(param != null ? new Object[] { param } : null);
	}

	public String parse(final Object[] params) {
		final Object[] converted = this.convertValues(params);
		if (this.useBundle) {
			return ApplicationManager.getTranslation(this.message, this.rb, converted);
		} else {
			return MessageFormat.format(this.message, converted);
		}
	}

	protected Object[] convertValues(final Object[] params) {
		final List l = new ArrayList();

		for (int i = 0, size = params != null ? params.length : 0; i < size; i++) {
			final Object current = params[i];
			final Object convert = this.convertValue(current);
			if (convert != null) {
				l.add(convert);
			}
		}
		return l.toArray();
	}

	protected Object convertValue(final Object value) {
		return (this.dateFormat != null) && (value instanceof Date) ? this.dateFormat.format((Date) value) : value;
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle rb) {
		this.rb = rb;
	}

}
