package com.ontimize.gui.field.document;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
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
 * This document implements the model for managing real numbers in a JTextField
 *
 * @version 1.0 01/04/2001
 */
public class RealDocument extends PlainDocument implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(RealDocument.class);

	protected DecimalFormatSymbols symbology = new DecimalFormatSymbols();

	protected NumberFormat formatter = NumberFormat.getInstance();

	protected Double floatValue = null;

	public RealDocument() {
		super();
		this.formatter.setMaximumFractionDigits(10);
	}

	public void setValue(final Number value) {
		try {
			this.remove(0, this.getLength());
			// Format
			final String stringValue = this.formatter.format(value);
			this.insertString(0, stringValue, null);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				RealDocument.logger.debug(null, e);
			}
		}
	}

	public void format() {
		try {
			String currentText = this.getText(0, this.getLength());
			if (currentText.length() == 0) {
				return;
			}
			if ("-".equalsIgnoreCase(currentText)) {
				currentText = "0";
			}
			final Number number = this.formatter.parse(currentText);
			this.remove(0, this.getLength());
			this.insertString(0, this.formatter.format(number).toString(), null);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				RealDocument.logger.debug(null, e);
			}
		}
	}

	public Number getValue() {
		this.updateValue();
		return this.floatValue;
	}

	public Double getDoubleValue(final String s) {
		try {
			final Number number = this.formatter.parse(s);
			return new Double(number.doubleValue());
		} catch (final Exception e) {
			RealDocument.logger.trace(null, e);
			return null;
		}
	}

	protected void updateValue() {

		final Double previousValue = this.floatValue;
		try {
			final String currentText = this.getText(0, this.getLength());
			if (currentText.length() == 0) {
				this.floatValue = new Double(0);
			} else {
				final Number number = this.formatter.parse(currentText);
				this.floatValue = new Double(number.doubleValue());
			}
		} catch (final Exception e) {
			RealDocument.logger.trace(null, e);
			this.floatValue = previousValue;
		}
	}

	public boolean isValid() {
		try {
			if (this.getLength() == 0) {
				return true;
			}
			final String currentText = this.getText(0, this.getLength());
			this.formatter.parse(currentText);
			return true;
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				RealDocument.logger.debug(null, e);
			}
			return false;
		}
	}

	public void setFractionDigits(final int number) {
		this.formatter.setMaximumFractionDigits(number);
	}

	@Override
	public void insertString(final int offset, final String sringValue, final AttributeSet attributes) throws BadLocationException {
		if (sringValue.length() == 0) {
			return;
		}
		// Use the system separator (. or ,)
		final char decimalSeparator = this.symbology.getDecimalSeparator();
		// First comprobation:
		if (sringValue.length() == 1) {
			// Checks that it is a numeric character
			if (!Character.isDigit(sringValue.charAt(0)) && (sringValue.charAt(0) != decimalSeparator)) {
				if ((sringValue.charAt(0) == '-') && (offset == 0)) {
					try {
						super.insertString(offset, sringValue, attributes);
					} catch (final Exception e) {
						if (com.ontimize.gui.ApplicationManager.DEBUG) {
							RealDocument.logger.error(null, e);
						}
					}
				} else {
					return;
				}
			} else {
				// Only one decimal separator is allowed
				try {
					final int length = this.getLength();
					boolean separatorExist = false;
					final String text = this.getText(0, length);
					for (int i = 0; i < length; i++) {
						if (text.charAt(i) == decimalSeparator) {
							separatorExist = true;
							break;
						}
					}
					if (((sringValue.charAt(0) == decimalSeparator) && (offset == 0))
							|| ((sringValue.charAt(0) == decimalSeparator) && separatorExist)) {
						return;
					}
					final StringBuilder currentText = new StringBuilder(this.getText(0, this.getLength()));
					currentText.insert(offset, sringValue);
					final Number number = this.formatter.parse(currentText.toString());
					final Double previousValue = this.floatValue;
					this.floatValue = new Double(number.doubleValue());

					try {
						super.insertString(offset, sringValue, attributes);
					} catch (final Exception ex) {
						if (com.ontimize.gui.ApplicationManager.DEBUG) {
							RealDocument.logger.debug(this.getClass().toString() + ": " + ex.getMessage(), ex);
						}
						this.floatValue = previousValue;
						return;
					}
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						RealDocument.logger.debug(null, e);
					}
				}
			}
		} else {
			// Check that the result string is a valid number
			try {
				final StringBuilder currentText = new StringBuilder(this.getText(0, this.getLength()));
				currentText.insert(offset, sringValue);
				try {
					final Number number = this.formatter.parse(currentText.toString());
					final Double previousValue = this.floatValue;
					this.floatValue = new Double(number.doubleValue());
					try {
						super.insertString(offset, sringValue, attributes);
					} catch (final BadLocationException e) {
						if (com.ontimize.gui.ApplicationManager.DEBUG) {
							RealDocument.logger.debug(null, e);
						}
						this.floatValue = previousValue;
					}
				} catch (final ParseException ex) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						RealDocument.logger.debug(this.getClass().toString() + ": " + ex.getMessage(), ex);
					}
					return;
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					RealDocument.logger.debug(null, e);
				}
				return;
			}
		}
	}

	@Override
	public void remove(final int offset, final int len) throws BadLocationException {
		// Delete. First in the buffer because of the events
		try {
			final Double previousValue = this.floatValue;
			final StringBuilder currentText = new StringBuilder(this.getText(0, this.getLength()));
			currentText.delete(offset, offset + len);
			if ((currentText.length() == 0) || currentText.toString().equals("-")) {
				this.floatValue = new Double(0);
			} else {
				final Number number = this.formatter.parse(currentText.toString());
				this.floatValue = new Double(number.doubleValue());
			}
			try {
				super.remove(offset, len);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					RealDocument.logger.debug(null, e);
				}
				this.floatValue = previousValue;
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				RealDocument.logger.debug(null, e);
			}
		}
	}

	public void setMinimumIntegerDigits(final int number) {
		this.formatter.setMinimumIntegerDigits(number);
	}

	public void setMaximumIntegerDigits(final int number) {
		this.formatter.setMaximumIntegerDigits(number);
	}

	public void setGrouping(final boolean group) {
		this.formatter.setGroupingUsed(group);
	}

	public void setMinimumFractionDigits(final int number) {
		this.formatter.setMinimumFractionDigits(number);
	}

	public void setMaximumFractionDigits(final int number) {
		this.formatter.setMaximumFractionDigits(number);
	}

	protected void insertStringWithoutCheck(final int offset, final String stringValue, final AttributeSet attributes)
			throws BadLocationException {
		super.insertString(offset, stringValue, attributes);
	}

	protected void removeWithoutCheck(final int offset, final int length) throws BadLocationException {
		super.remove(offset, length);
	}

	public NumberFormat getFormat() {
		return this.formatter;
	}

	public char getDecimalSeparator() {
		return this.symbology.getDecimalSeparator();
	}

	@Override
	public void setComponentLocale(final Locale loc) {
		final Locale l = DateDataField.getSameCountryLocale(loc);
		this.symbology = new DecimalFormatSymbols(l);
		final int minimunFractionDigits = this.formatter.getMinimumFractionDigits();
		final int maximumFractionDigits = this.formatter.getMaximumFractionDigits();

		final int minimunIntegerDigits = this.formatter.getMinimumIntegerDigits();
		final int maximumIntegerDigits = this.formatter.getMaximumIntegerDigits();

		final boolean g = this.formatter.isGroupingUsed();
		this.formatter = NumberFormat.getInstance(l);
		this.formatter.setMaximumFractionDigits(maximumFractionDigits);
		this.formatter.setMinimumFractionDigits(minimunFractionDigits);
		this.formatter.setMaximumIntegerDigits(maximumIntegerDigits);
		this.formatter.setMinimumIntegerDigits(minimunIntegerDigits);
		this.formatter.setGroupingUsed(g);
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector();
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
	}

}
