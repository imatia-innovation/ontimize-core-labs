package com.ontimize.gui.field;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueEvent;

/**
 * The main class to implement a combo with the months. This combo is locale sensitive, if it is not
 * specified, default locale is used.
 * <p>
 *
 * @author Imatia Innovation
 */
public class MonthComboDataField extends TextComboDataField {

	private static final Logger logger = LoggerFactory.getLogger(MonthComboDataField.class);

	protected GregorianCalendar calendar = null;

	protected boolean currentLocaleExists = true;

	/**
	 * The class constructor. Calls to <code>super()</code> with parameters and creates an instance of a
	 * <code>Gregorian Calendar</code>.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public MonthComboDataField(final Map parameters) {
		super(parameters);
		// Creates the calendar
		this.calendar = new GregorianCalendar();
		this.addMonth(null);
	}

	/**
	 * Sets locale to combo data field component.
	 */
	@Override
	public void setComponentLocale(final Locale l) {
		super.setLocale(l);
		this.calendar = null;
		this.addMonth(l);
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		super.setResourceBundle(resource);
		if (!this.currentLocaleExists) {
			final Locale locEN = new Locale("en", "US");
			final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locEN);
			final String[] monthNames = dateFormatSymbols.getMonths();
			for (int i = 0; i < monthNames.length; i++) {
				monthNames[i] = ApplicationManager.getTranslation(monthNames[i], this.resources);
			}
			this.updateMonthNames(monthNames);
		}
	}

	/**
	 * Replaces old string months by others for the given locale.
	 * <p>
	 * @param locale the reference to locale
	 */
	protected void addMonth(final Locale locale) {
		// Adds the months to combo in form of strings.

		DateFormatSymbols dateFormatSymbols = null;
		if (locale != null) {
			final Locale sameCountryLocale = DateDataField.getSameCountryLocale(locale);
			if (sameCountryLocale.equals(locale)) {
				this.currentLocaleExists = true;
			} else {
				this.currentLocaleExists = false;
			}
			dateFormatSymbols = new DateFormatSymbols(locale);
		} else {
			dateFormatSymbols = new DateFormatSymbols();
		}
		final String[] monthNames = dateFormatSymbols.getMonths();

		if (this.currentLocaleExists) {
			this.updateMonthNames(monthNames);
		}
	}

	protected void updateMonthNames(final String[] monthNames) {
		// Save the index of selected month to maintain it after locale change.
		final int selectedMonthIndex = ((JComboBox) this.dataField).getSelectedIndex();
		((JComboBox) this.dataField).removeAllItems();
		for (int i = 0; i < 12; i++) {
			((JComboBox) this.dataField).addItem(monthNames[i]);
		}
		try {
			((JComboBox) this.dataField).setSelectedIndex(selectedMonthIndex);
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				MonthComboDataField.logger.debug(this.getClass().toString() + e.getMessage(), e);
			}
		}
	}

	/**
	 * Sets the month that is selected in combo. This month is specified by a integer param: <br>
	 * &nbsp;&nbsp;0=January, 1= February,...
	 * <p>
	 * @param value an integer instance to indicate the month
	 */
	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		if (value instanceof Integer) {
			((JComboBox) this.dataField).setSelectedIndex(((Integer) value).intValue());
		}
		this.valueSave = this.getValue();
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Returns the selected month in combobox.
	 * <p>
	 * @return a integer instance to indicate the month
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		return new Integer(((JComboBox) this.dataField).getSelectedIndex());
	}

}
