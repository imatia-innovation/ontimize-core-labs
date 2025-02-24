package com.ontimize.gui.field.document;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.i18n.Internationalization;

/**
 * This document implements the model for managing dates in a JTextField
 *
 */
public class DateDocument extends PlainDocument implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(DateDocument.class);

	public static boolean DEBUG = false;

	protected static class DateFormatCacheKey {

		protected Locale locale = null;

		protected String pattern = null;

		public DateFormatCacheKey(final Locale l, final String pattern) {
			this.locale = l;
			this.pattern = pattern;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null) {
				return false;
			}
			if (o instanceof DateFormatCacheKey) {
				if (this.locale.equals(((DateFormatCacheKey) o).locale)
						&& this.pattern.equals(((DateFormatCacheKey) o).pattern)) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.locale.hashCode() + this.pattern.hashCode();
		}

	}

	protected static class DateFormatCache {

		protected Map cache = new Hashtable();

		public SimpleDateFormat get(final String pattern, final Locale l) {
			return (SimpleDateFormat) this.cache.get(new DateFormatCacheKey(l, pattern));
		}

		public boolean exists(final String pattern, final Locale l) {
			return this.cache.containsKey(new DateFormatCacheKey(l, pattern));
		}

		public void put(final String pattern, final Locale l, final SimpleDateFormat df) {
			this.cache.put(new DateFormatCacheKey(l, pattern), df);
		}

	}

	protected static SimpleDateFormat dfConstructor = null;

	protected static Locale defaultLocale = Locale.getDefault();

	protected SimpleDateFormat dateFormat = null;

	protected String datePattern;

	protected Date insertedDate;

	protected Date currentDate;

	protected Timestamp currentTimestamp = null;

	protected Locale locale = DateDocument.defaultLocale;

	public DateDocument() {
		super();

		if (DateDocument.dfConstructor == null) {
			DateDocument.dfConstructor = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM,
					DateDocument.defaultLocale);
		}
		this.dateFormat = DateDocument.dfConstructor;

		this.dateFormat.setLenient(false);
		final GregorianCalendar calendar = new GregorianCalendar(DateDocument.defaultLocale);
		calendar.setLenient(false);

		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);

		this.dateFormat.setCalendar(calendar);
		final DateFormatSymbols symbols = new DateFormatSymbols();
		symbols.setLocalPatternChars("GyMdkHmsSEDFwWahKz");
		this.dateFormat.setDateFormatSymbols(symbols);

		this.buildPattern();

		this.dateFormat.applyPattern(this.datePattern);

		// Initializes the date
		this.currentDate = new Date();
		this.insertedDate = this.currentDate;
	}

	public void setValue(final Date value) {
		try {
			final String stingDate = this.dateFormat.format(value);
			this.remove(0, this.getLength());
			this.insertString(0, stingDate, null);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				DateDocument.logger.debug(null, e);
			} else {
				DateDocument.logger.trace(null, e);
			}
		}
	}

	public Timestamp getTimestampValue() {
		this.format();
		return this.currentTimestamp;
	}

	public SimpleDateFormat getFormat() {
		return this.dateFormat;
	}

	public void format() {

		if (this.isValid()) {
			final String pattern = this.getDatePattern();
			try {
				final int dateLength = this.getLength();
				if (dateLength < 10) {
					final StringBuilder newPattern = new StringBuilder("");
					int yIndex = 0;
					int j = 0;
					for (int i = 0; i < pattern.length(); i++) {
						if (pattern.charAt(i) == 'y') {
							yIndex++;
							if (yIndex > 2) {
								newPattern.insert(j, pattern.charAt(i));
								j++;
							}
						} else {
							newPattern.insert(i, pattern.charAt(i));
							j++;
						}
					}
					// Format with the new pattern
					this.dateFormat.applyPattern(newPattern.toString());
					final String sDate = this.getText(0, this.getLength());
					final Date dDate = this.dateFormat.parse(sDate);
					this.dateFormat.applyPattern(pattern);
					final String sNewDate = this.dateFormat.format(dDate);
					this.remove(0, this.getLength());
					this.insertStringWithoutCheck(0, sNewDate, null);
				}
			} catch (final Exception ex) {
				if (ApplicationManager.DEBUG) {
					DateDocument.logger.debug(null, ex);
				} else {
					DateDocument.logger.trace(null, ex);
				}
			} finally {
				this.dateFormat.applyPattern(pattern);
			}
		}
	}

	protected void buildPattern() {
		// Checks the order of day, month and year and use the order specified
		// in the locale
		final String sLocalePattern = this.dateFormat.toPattern();
		int dayIndex = -1;
		int monthIndex = -1;
		int yearIndex = -1;
		this.datePattern = "dd/MM/yyyy";
		for (int i = 0; i < sLocalePattern.length(); i++) {
			if (sLocalePattern.charAt(i) == 'd') {
				dayIndex = i;
			}
			if (sLocalePattern.charAt(i) == 'M') {
				monthIndex = i;
			}
			if (sLocalePattern.charAt(i) == 'y') {
				yearIndex = i;
			}
		}
		// If some index is -1 then use the pattern dd/MM/yyyy
		if ((dayIndex == -1) || (monthIndex == -1) || (yearIndex == -1)) {
			this.datePattern = "dd/MM/yyyy";
		} else {
			// Checks the presentation order
			if ((dayIndex < monthIndex) && (monthIndex < yearIndex)) {
				this.datePattern = "dd/MM/yyyy";
			} else {
				if ((yearIndex < monthIndex) && (monthIndex < dayIndex)) {
					this.datePattern = "yyyy/MM/dd";
				} else {
					if ((dayIndex < yearIndex) && (yearIndex < monthIndex)) {
						this.datePattern = "dd/yyyy/MM";
					} else {
						if ((yearIndex < dayIndex) && (dayIndex < monthIndex)) {
							this.datePattern = "yyyy/dd/MM";
						} else {
							if ((monthIndex < dayIndex) && (dayIndex < yearIndex)) {
								this.datePattern = "MM/dd/yyyy";
							} else {
								if ((monthIndex < yearIndex) && (yearIndex < dayIndex)) {
									this.datePattern = "MM/yyyy/dd";
								} else {
									this.datePattern = "dd/MM/yyyy";
								}
							}
						}
					}
				}
			}
		}
	}

	public void setDatePattern(final String newPattern) {
		this.datePattern = newPattern;
	}

	public String getDatePattern() {
		return this.datePattern;
	}

	public Date getDate() {
		return this.currentDate;
	}

	public synchronized boolean isValid() {
		if (this.getLength() < 8) {
			return false;
		} else {
			try {
				final String sDate = this.getText(0, this.getLength());
				return this.isValid(sDate);
			} catch (final BadLocationException e) {
				DateDocument.logger.trace(null, e);
				return false;
			}
		}
	}

	protected Timestamp getTimestampValue(final String date) {

		try {
			if (date.length() >= 10) {
				final Date d = this.dateFormat.parse(date);
				final Timestamp t = new Timestamp(d.getTime());
				if (DateDocument.DEBUG) {
					DateDocument.logger.debug("Date ( 4-digit year pattern): " + date + " : " + t.toString());
				}
				return t;
			} else {
				final StringBuilder newPattern = new StringBuilder("");
				int yIndex = 0;
				int j = 0;
				for (int i = 0; i < this.datePattern.length(); i++) {
					if (this.datePattern.charAt(i) == 'y') {
						yIndex++;
						if (yIndex > 2) {
							newPattern.insert(j, this.datePattern.charAt(i));
							j++;
						}
					} else {
						newPattern.insert(i, this.datePattern.charAt(i));
						j++;
					}
				}
				// format with the new pattern
				final SimpleDateFormat formatter = new SimpleDateFormat(newPattern.toString());
				formatter.setLenient(false);
				final Date d = formatter.parse(date);
				final Timestamp t = new Timestamp(d.getTime());
				if (DateDocument.DEBUG) {
					DateDocument.logger.debug("Date: " + date + " : " + t.toString());
				}
				return t;
			}
		} catch (final ParseException e) {
			DateDocument.logger.trace(null, e);
			return null;
		}
	}

	protected boolean isValid(final String date) {
		try {
			if (date.length() >= 10) {
				this.insertedDate = this.dateFormat.parse(date);
				this.currentDate = this.insertedDate;
				this.currentTimestamp = new Timestamp(this.currentDate.getTime());
				return true;
			} else {
				final StringBuilder newPattern = new StringBuilder("");
				int yIndex = 0;
				int j = 0;
				for (int i = 0; i < this.datePattern.length(); i++) {
					if (this.datePattern.charAt(i) == 'y') {
						yIndex++;
						if (yIndex > 2) {
							newPattern.insert(j, this.datePattern.charAt(i));
							j++;
						}
					} else {
						newPattern.insert(i, this.datePattern.charAt(i));
						j++;
					}
				}
				// format with the new pattern
				final SimpleDateFormat formatter = new SimpleDateFormat(newPattern.toString());
				formatter.setLenient(false);
				this.currentDate = formatter.parse(date);
				this.currentTimestamp = new Timestamp(this.currentDate.getTime());
				return true;
			}
		} catch (final ParseException e) {
			DateDocument.logger.trace(null, e);
			return false;
		}

	}

	/**
	 * Date pattern is known:<br>
	 * - Non numeric characters are allowed.<br>
	 * - Year field has 4 characters (9999 maximum). <br>
	 * - Month and day fields have 2 characters. The maximum value for day is 31 and for month 12. An
	 * the minimum value for both is 1. <br>
	 * - When a value is introduced in field, this one is checked and text will be showed in red color
	 * until that the value is a correct date. Moreover, field value is also checked when field losts
	 * focus or ENTER key is pressed.<br>
	 * - Separator for dates is /. So a valid format date could be: 12/12/2008
	 *
	 */
	@Override
	public void insertString(int offset, String sValue, final AttributeSet attributes) throws BadLocationException {
		// The string that represents day, month and year (no necessarily in a
		// predefined order).
		// Moreover, copy and paste for complete and valid dates are allowed.
		// It is also possible to insert characters one by one.
		//
		final int sValueLength = sValue.length();
		if (offset >= 10) {
			return;
		}
		if ((sValueLength != 10) && (sValueLength != 1)) {
			return;
		} else {
			// Check that the character is a numeric value
			if (sValueLength == 1) {
				if ((this.getText(0, this.getLength()).length() + sValueLength) > this.datePattern.length()) {
					return;
				}
				final char caracter = sValue.charAt(0);
				if (Character.isDigit(caracter)) {
					// If this is the separator position then add it
					if (this.datePattern.charAt(offset) == '/') {
						final String currentString = this.getText(0, this.getLength());
						if (offset < currentString.length()) {
							if (currentString.charAt(offset) != '/') {
								sValue = "/" + sValue;
							} else {
								sValue = "/" + sValue;
								// Insert the separator in the next character
							}
						} else {
							sValue = "/" + sValue;
						}
					}
				} else {
					return;
				}
			} else {
				// String length is 10. Checks the format
				// Delete the previous content
				offset = 0;
				boolean notAllowed = false;
				for (int i = 0; i < 10; i++) {
					if ((this.datePattern.charAt(i) == '/') && (sValue.charAt(i) != '/')) {
						// String invalid
						notAllowed = true;
					} else {
						if (!Character.isDigit(sValue.charAt(i)) && (this.datePattern.charAt(i) != '/')) {
							notAllowed = true;
						}
					}
				}
				if (notAllowed) {
					return;
				} else {
					if (sValue.length() > 0) {
						this.remove(offset, this.getLength() - offset);
					}
					this.insertStringWithoutCheck(offset, sValue, attributes);
					return;
				}
			}
		}
		this.insertStringWithoutCheck(offset, sValue, attributes);
	}

	protected void insertStringWithoutCheck(final int offset, final String sValue, final AttributeSet attributes)
			throws BadLocationException {
		super.insertString(offset, sValue, attributes);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
		// Set the pattern using the locale
		int length = 0;
		try {
			length = this.getLength();
			final DateFormatSymbols symbols = new DateFormatSymbols();
			symbols.setLocalPatternChars("GyMdkHmsSEDFwWahKz");
			final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, l);
			df.setLenient(false);
			df.getCalendar().set(Calendar.MILLISECOND, 0);
			df.getCalendar().set(Calendar.SECOND, 0);
			df.getCalendar().set(Calendar.MINUTE, 0);
			df.getCalendar().set(Calendar.HOUR, 0);
			if (DateDataField.DEBUG_DATE) {
				final Locale[] locales = DateFormat.getAvailableLocales();
				for (int i = 0; i < locales.length; i++) {
					if (ApplicationManager.DEBUG) {
						DateDocument.logger
						.debug(this.getClass().toString() + " : Supported Locale: " + locales[i].toString());
					}
				}
			}
			this.dateFormat.setDateFormatSymbols(symbols);
			if (df instanceof SimpleDateFormat) {
				if (length != 0) {
					this.currentDate = this.dateFormat.parse(this.getText(0, length));
				}
				this.dateFormat = (SimpleDateFormat) df;
				this.buildPattern();
				((SimpleDateFormat) df).applyPattern(this.datePattern);
				this.dateFormat.applyPattern(this.datePattern);
				if (ApplicationManager.DEBUG) {
					DateDocument.logger.debug(this.getClass().toString() + " : Established Locale: " + l.toString());
				}
				if (ApplicationManager.DEBUG) {
					DateDocument.logger.debug(this.getClass().toString() + " : Date pattern :" + this.datePattern);
				}
			}
			if (length != 0) {
				this.insertString(0, this.dateFormat.format(this.currentDate), null);
			}
		} catch (final Exception e) {
			if (DateDataField.DEBUG_DATE) {
				DateDocument.logger.error(null, e);
			} else {
				DateDocument.logger.trace(null, e);
			}
			try {
				this.remove(0, length);
			} catch (final Exception e2) {
				DateDocument.logger.trace(null, e2);
			}
		}
	}

}
