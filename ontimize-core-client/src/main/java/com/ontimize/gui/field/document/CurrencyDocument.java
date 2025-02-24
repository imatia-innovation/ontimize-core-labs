package com.ontimize.gui.field.document;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.gui.SearchValue;

/**
 * This document implements the model for managing currency values. Only Ptas and euros are
 * supported. Internally, value is stored in a variable called floatValue. When actual mode is Pta,
 * values are processed like Pta. Otherwise, when mode is Euro, values are managed like euros.
 */
public class CurrencyDocument extends AdvancedRealDocument {

	private static final Logger logger = LoggerFactory.getLogger(CurrencyDocument.class);

	/*
	 * Equivalent value for an Euro in pesetas.
	 */
	public static final double EURO = 166.386;

	public static String defaultCurrencySymbol = String.valueOf('€');

	// public String euro = new String("€");

	public String currencySymbol;

	// public static String pst = "Pta";

	// protected boolean showEuros = true;

	protected boolean showCurrencySymbol = true;

	protected NumberFormat pstFormatter = NumberFormat.getInstance();

	public CurrencyDocument() {
		this(CurrencyDocument.defaultCurrencySymbol);
	}

	public CurrencyDocument(String currencySymbol) {
		super();
		try {
			this.currencySymbol = currencySymbol;
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				CurrencyDocument.logger.error(null, e);
			}
			currencySymbol = "";
		}
		this.setMinimumFractionDigits(2);
		this.setMaximumFractionDigits(2);
		this.pstFormatter.setMaximumFractionDigits(0);
	}

	// public void setShowEuros(boolean showEuros) throws Exception {
	// this.showEuros = showEuros;
	// currencyChange();
	// }
	//
	// public boolean getShownEuros() {
	// return this.showEuros;
	// }

	/**
	 * This function updates the content in field when currency changes (PTA<=>EURO). If actual currency
	 * is EURO, document only shows the same value. However, when current currency is PTA, showed value
	 * is multiplied by 166.386.
	 * @throws Exception
	 */
	public void currencyChange() throws Exception {
		if (this.showCurrencySymbol) {
			if (!this.getText(0, this.getLength()).equals("")) {
				// Delete and insert
				this.removeWithoutCheck(0, this.getLength());
				this.insertStringWithoutCheck(0, this.formatter.format(this.floatValue.doubleValue()), null);
			} else {
				this.floatValue = new Double(0.0);
				// Delete and insert
				this.removeWithoutCheck(0, this.getLength());
				this.insertStringWithoutCheck(0, this.formatter.format(this.floatValue.doubleValue()), null);
			}
		} else {
			if (!this.getText(0, this.getLength()).equals("")) {
				// Delete and insert
				this.removeWithoutCheck(0, this.getLength());
				this.insertStringWithoutCheck(0,
						this.formatter.format(this.floatValue.doubleValue() * CurrencyDocument.EURO), null);
			} else {
				this.floatValue = new Double(0.0);
				// Delete and insert
				this.removeWithoutCheck(0, this.getLength());
				this.insertStringWithoutCheck(0, this.formatter.format(this.floatValue.doubleValue()), null);
			}
		}
		this.format();
	}

	@Override
	public void format() {
		// If it is advancedquerymode then use the AdvancedRealDocument to
		// format

		super.format();
		try {
			final int length = this.getLength();
			if (length > 0) {
				if (this.showCurrencySymbol) {
					final StringBuilder sb = new StringBuilder(" ");
					sb.append(this.currencySymbol);
					this.insertStringWithoutCheck(length, sb.toString(), null);
				}
			}
		} catch (final Exception e) {
			CurrencyDocument.logger.error(null, e);
		}
	}

	@Override
	protected void updateValue() {
		final Double previousValue = this.floatValue;
		try {
			if (this.advancedQueryMode) {
				final SearchValue v = this.getQueryValue();
				if (v == null) {
					this.floatValue = new Double(0.0);
				} else {
					if (v.getValue() instanceof List) {
						final List vs = (List) v.getValue();
						if ((vs.get(0) != null) && (vs.get(0) instanceof Number)) {
							this.floatValue = new Double(((Number) vs.get(0)).doubleValue());
						} else {
							this.floatValue = new Double(0.0);
						}
					} else {
						if (v.getValue() instanceof Number) {
							this.floatValue = new Double(((Number) v.getValue()).doubleValue());
						} else {
							this.floatValue = new Double(0.0);
						}
					}
				}
				return;
			}
			final String currentText = this.getText(0, this.getLength());
			if (currentText.length() == 0) {
				this.floatValue = new Double(0);
			} else {
				final Number number = this.formatter.parse(currentText);
				this.floatValue = new Double(number.doubleValue());
				if (!this.showCurrencySymbol) {
					this.floatValue = new Double(this.floatValue.doubleValue() / CurrencyDocument.EURO);
				}
			}
		} catch (final Exception e) {
			CurrencyDocument.logger.trace(null, e);
			// logger.error(null,e);
			this.floatValue = previousValue;
		}
	}

	@Override
	public Number getValue() {
		this.updateValue();
		return this.floatValue;
	}

	@Override
	public Double getDoubleValue(final String s) {
		try {

			final Number number = this.formatter.parse(s);
			Double d = new Double(number.doubleValue());
			if (!this.showCurrencySymbol) {
				d = new Double(this.floatValue.doubleValue() / CurrencyDocument.EURO);
			}
			return d;
		} catch (final Exception e) {
			CurrencyDocument.logger.trace(null, e);
			return null;
		}
	}

	@Override
	public void setValue(Number value) {
		try {
			if (this.showCurrencySymbol) {
				super.setValue(value);
				final int length = this.getLength();
				if (length > 0) {
					final StringBuilder sb = new StringBuilder(" ");
					sb.append(this.currencySymbol);
					this.insertStringWithoutCheck(length, sb.toString(), null);
				}
			} else {
				if (value != null) {
					value = new Double(value.doubleValue() * CurrencyDocument.EURO);
				}
				super.setValue(value);
				final int length = this.getLength();
				if (length > 0) {
					// Read the value in euros
					final StringBuilder sb = new StringBuilder(" ");
					// sb.append(pst);
					this.insertStringWithoutCheck(length, sb.toString(), null);
				}
			}
		} catch (final Exception e) {
			CurrencyDocument.logger.trace(null, e);
		}
	}

	@Override
	public SearchValue getQueryValue() {
		if (!this.advancedQueryMode) {
			return null;
		}
		// This document adds the currency symbol at the end
		// The symbol is added after a blank space and it is not used in the
		// comprobations
		try {
			if (this.getLength() == 0) {
				return null;
			}
			if (!this.isSymbolFirst()) {
				String text = this.getText(0, this.getLength());
				if (text.lastIndexOf(" ") > 0) {
					text = text.substring(0, text.lastIndexOf(" "));
				}
				final int orIndex = text.indexOf(AdvancedRealDocument.OR);
				if (orIndex > 0) {
					final List vDoubles = new Vector();
					final StringTokenizer st = new StringTokenizer(text, AdvancedRealDocument.OR);
					while (st.hasMoreTokens()) {
						final String token = st.nextToken();
						final Double t = this.getDoubleValue(token);
						if (t == null) {
							return null;
						}
						vDoubles.add(t);
					}
					return new SearchValue(SearchValue.OR, vDoubles);
				} else {
					final int betweenIndex = text.indexOf(AdvancedRealDocument.BETWEEN);
					if (betweenIndex > 0) {
						final List vDoubles = new Vector();
						final StringTokenizer st = new StringTokenizer(text, AdvancedRealDocument.BETWEEN);
						while (st.hasMoreTokens()) {
							final String token = st.nextToken();
							final Double t = this.getDoubleValue(token);
							if (t == null) {
								return null;
							}
							vDoubles.add(t);
						}
						return new SearchValue(SearchValue.BETWEEN, vDoubles);
					} else {
						final Double t = this.getDoubleValue(text);
						if (t == null) {
							return null;
						} else {
							return new SearchValue(SearchValue.EQUAL, t);
						}
					}
				}
			} else {
				// The first character is a symbol
				final String symbol = this.getDocumentFirstSymbol();
				String text = this.getText(0, this.getLength());
				if (text.lastIndexOf(" ") > 0) {
					text = text.substring(0, text.lastIndexOf(" "));
				}
				if (text.length() > symbol.length()) {
					final String textNumber = text.substring(symbol.length());
					final Double t = this.getDoubleValue(textNumber);
					if (symbol.equals(AdvancedRealDocument.NOT)) {
						if (t == null) {
							return new SearchValue(SearchValue.NULL, null);
						} else {
							return new SearchValue(SearchValue.NOT_EQUAL, t);
						}
					} else {
						if (t == null) {
							return null;
						} else {
							if (symbol.equals(AdvancedRealDocument.LESS)) {
								return new SearchValue(SearchValue.LESS, t);
							} else if (symbol.equals(AdvancedRealDocument.MORE)) {
								return new SearchValue(SearchValue.MORE, t);
							} else if (symbol.equals(AdvancedRealDocument.MORE_EQUAL)) {
								return new SearchValue(SearchValue.MORE_EQUAL, t);
							} else if (symbol.equals(AdvancedRealDocument.LESS_EQUAL)) {
								return new SearchValue(SearchValue.LESS_EQUAL, t);
							} else if (symbol.equals(AdvancedRealDocument.EQUAL)) {
								return new SearchValue(SearchValue.EQUAL, t);
							} else {
								return null;
							}
						}
					}
				} else {
					if (symbol.equals(AdvancedRealDocument.NOT)) {
						return new SearchValue(SearchValue.NULL, null);
					}
					return null;
				}
			}
		} catch (final Exception e) {
			CurrencyDocument.logger.trace(null, e);
			return null;
		}
	}

	// public String formatPst(double d) {
	// return this.pstFormatter.format(d);
	// }

	public boolean isShowCurrencySymbol() {
		return this.showCurrencySymbol;
	}

	public void setShowCurrencySymbol(final boolean showCurrencySymbol) {
		this.showCurrencySymbol = showCurrencySymbol;
	}

	public String getCurrencySymbol() {
		return this.currencySymbol;
	}

	public void setCurrencySymbol(final String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		super.setComponentLocale(l);
		this.pstFormatter = NumberFormat.getInstance(l);
		this.pstFormatter.setMaximumFractionDigits(0);
	}

}
