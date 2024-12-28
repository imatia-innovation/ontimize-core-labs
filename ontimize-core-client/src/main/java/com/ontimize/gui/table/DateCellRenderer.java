package com.ontimize.gui.table;

import java.awt.Component;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.cache.DateFormatCache;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.util.ParseUtils;

/**
 * Renderer used to show the date and timestamp data in a table.
 *
 * @version 1.0 01/04/2001
 */
public class DateCellRenderer extends CellRenderer implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(DateCellRenderer.class);

	public static boolean hourDefaultValueFirst = true;

	static {
		try {
			final String prop = System.getProperty("com.ontimize.gui.table.DateCellRenderer.timebefore");
			if ((prop != null) && prop.equalsIgnoreCase("false")) {
				DateCellRenderer.hourDefaultValueFirst = false;
			}
		} catch (final Exception ex) {
			DateCellRenderer.logger.trace(null, ex);
		}
	}

	protected SimpleDateFormat dateFormat = null;

	protected String datePattern;

	protected String hourPattern = "HH:mm";

	protected boolean withHour = false;

	protected boolean hourOnly = false;

	protected boolean hourInFirstPlace = DateCellRenderer.hourDefaultValueFirst;

	public DateCellRenderer() {
		this(false);
	}

	public DateCellRenderer(final Map params) {
		this(ParseUtils.getBoolean((String) params.get("withhour"), false),
				ParseUtils.getBoolean((String) params.get("hourfirst"), DateCellRenderer.hourDefaultValueFirst),
				ParseUtils.getBoolean((String) params.get("onlyhour"), false));
	}

	public DateCellRenderer(final boolean withHour) {
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.withHour = withHour;
		this.createFormatter(Locale.getDefault());
	}

	public DateCellRenderer(final boolean withHour, final boolean hourFirst) {
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.withHour = withHour;
		this.hourInFirstPlace = hourFirst;
		this.createFormatter(Locale.getDefault());
	}

	public DateCellRenderer(final boolean withHour, final boolean hourFirst, final boolean onlyHour) {
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.withHour = withHour;
		this.hourInFirstPlace = hourFirst;
		this.hourOnly = onlyHour;
		this.createFormatter(Locale.getDefault());
	}

	protected void createNewFormatter(final Locale l) {
		this.dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, l);
		this.dateFormat.setLenient(false);
		final GregorianCalendar calendar = new GregorianCalendar(l);
		calendar.setLenient(false);
		this.dateFormat.setCalendar(calendar);
		// Set the patter using the locale
		final DateFormatSymbols symbols = new DateFormatSymbols(l);
		symbols.setLocalPatternChars("GyMdkHmsSEDFwWahKz");
		this.dateFormat.setDateFormatSymbols(symbols);
		// Initialize the date
		this.buildPattern();

		if (this.withHour) {
			if (this.hourOnly) {
				this.dateFormat.applyPattern(this.hourPattern);
			} else {
				if (this.hourInFirstPlace) {
					this.dateFormat.applyPattern(this.hourPattern + " " + this.datePattern);
				} else {
					this.dateFormat.applyPattern(this.datePattern + " " + this.hourPattern);
				}
			}
		} else {
			this.dateFormat.applyPattern(this.datePattern);
			DateFormatCache.addDateFormat(l, this.dateFormat);
		}
	}

	protected void createFormatter(final Locale locale) {
		if (!DateFormatCache.containsDateFormat(locale)) {
			this.createNewFormatter(locale);
		} else {
			this.dateFormat = (SimpleDateFormat) DateFormatCache.getDateFormat(locale);
			this.buildPattern();
			if (!this.withHour && this.dateFormat.toPattern().equals(this.datePattern)) {
			} else {
				// Create the new format with the new locale
				this.createNewFormatter(locale);
			}
		}

		this.setFormater(this.dateFormat);

	}

	protected void buildPattern() {
		// Check the order of day, month and year, and use the order
		// specified by the locale
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

		if ((dayIndex == -1) || (monthIndex == -1) || (yearIndex == -1)) {
			this.datePattern = "dd/MM/yyyy";
		} else {
			// Check the order
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

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {

		final Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);

		try {
			if ((value != null) && (!(value instanceof NullValue))) {
				this.setText(this.format.format(value));
			} else {
				this.setText("");
			}
		} catch (final Exception ex) {
			DateCellRenderer.logger.trace(null, ex);
			if (value != null) {
				this.setText(value.toString());
			}
		}

		this.setTipWhenNeeded(table, value, column);
		return c;
	}

	public String getDatePattern() {
		return this.datePattern;
	}

	public String getHourPattern() {
		return this.hourPattern;
	}

	public boolean isWithHour() {
		return this.withHour;
	}

	public boolean isHourOnly() {
		return this.hourOnly;
	}

	public boolean isHourInFirstPlace() {
		return this.hourInFirstPlace;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {

	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.createFormatter(DateDataField.getSameCountryLocale(l));
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector(0);
	}

}
