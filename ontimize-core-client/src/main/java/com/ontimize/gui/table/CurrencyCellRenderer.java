package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;

import com.ontimize.gui.SelectCurrencyValues;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.document.CurrencyDocument;
import com.ontimize.jee.common.db.NullValue;

/**
 * Renderer used to show double or float data types in tables.
 *
 * @version 1.0 01/04/2001
 */
public class CurrencyCellRenderer extends RealCellRenderer implements SelectCurrencyValues {

	public static boolean useNegativeColor = true;

	public static Color defaultNegativeColor = new Color(255, 0, 0);

	public static final double EURO = 166.386;

	/**
	 * @deprecated unused since @version 5.3.15
	 */
	@Deprecated
	public byte[] EURO_BYTE = new byte[1];

	/**
	 * @deprecated Symbol is in currency document
	 */
	@Deprecated
	public String euro = "Euro";

	/**
	 * @deprecated removed references to pts currency
	 */
	@Deprecated
	public static String pst = "Pta";

	protected NumberFormat pstFormatter = NumberFormat.getInstance();

	/**
	 * Since 5.3.15, default currency Symbol for renderers is read from static value
	 * <code>CurrencyDocument.defaultCurrencySymbol</code> and can be change in this renderer.
	 */
	protected String currencySymbol = CurrencyDocument.defaultCurrencySymbol;

	/**
	 * @deprecated not used.
	 */
	@Deprecated
	protected boolean showEuros = true;

	public CurrencyCellRenderer(final String currencySymbol) {
		this(currencySymbol, 2, 2);
	}

	public CurrencyCellRenderer(final String currencySymbol, final int minimumFractionDigits, final int maximumFractionDigits) {
		super();
		this.currencySymbol = currencySymbol;
		((NumberFormat) this.format).setMinimumFractionDigits(minimumFractionDigits);
		((NumberFormat) this.format).setMaximumFractionDigits(maximumFractionDigits);
		this.pstFormatter.setMaximumFractionDigits(0);
	}

	public CurrencyCellRenderer() {
		this(CurrencyDocument.defaultCurrencySymbol);
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {
		final Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
		if ((((JLabel) c).getText() != null) && (((JLabel) c).getText().length() > 0)) {
			final StringBuilder sb = new StringBuilder(((JLabel) c).getText());
			sb.append(" ");
			sb.append(this.currencySymbol);
			((JLabel) c).setText(sb.toString());
		}
		return c;
	}

	@Override
	public void setTipWhenNeeded(final JTable table, final Object value, final int column) {
		if ((value == null) || (value instanceof NullValue)) {
			return;
		}
		if (value instanceof Number) {
			// Tip
			final StringBuilder sbTipText = new StringBuilder(this.format.format(value));
			sbTipText.append(" ");
			sbTipText.append(this.currencySymbol);
			this.setToolTipText(sbTipText.toString());
		} else {
			this.setToolTipText(value.toString());
		}
	}

	@Override
	public void setComponentLocale(final Locale loc) {
		// formatter for digits cannot be gl_ES because not exists in JRE. It
		// must be es_ES
		final Locale l = DateDataField.getSameCountryLocale(loc);
		super.setComponentLocale(l);
		this.pstFormatter = NumberFormat.getInstance(l);
		this.pstFormatter.setMaximumFractionDigits(0);
	}

	/**
	 * @deprecated unused but sets the current currencySymbol
	 */
	@Deprecated
	@Override
	public void showCurrencyValue(final String currencySymbol) {
		// unused
		this.currencySymbol = currencySymbol;
	}

	public String getCurrencySymbol() {
		return this.currencySymbol;
	}

	public void setCurrencySymbol(final String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	@Override
	protected Color getForegroundColor(final JTable table, final Object value, final boolean selected, final boolean editable, final boolean hasFocus,
			final int row, final int column) {
		Color defaultColor = super.getForegroundColor(table, value, selected, editable, hasFocus, row, column);
		if (CurrencyCellRenderer.useNegativeColor && (value instanceof Number)) {
			if (((Number) value).doubleValue() < 0.0d) {
				defaultColor = CurrencyCellRenderer.defaultNegativeColor;
			}
		}
		return defaultColor;
	}

}
