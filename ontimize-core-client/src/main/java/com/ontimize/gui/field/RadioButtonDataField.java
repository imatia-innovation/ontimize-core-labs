package com.ontimize.gui.field;

import java.util.Map;

import javax.swing.JRadioButton;

/**
 * This class implements a radio button field with {@link CheckDataField} behaviour.
 * <p>
 *
 * @author Imatia Innovation
 */
public class RadioButtonDataField extends CheckDataField {

	/**
	 * The key for 1 value.
	 */
	public static final Short ONE = new Short((short) 1);

	/**
	 * The key for 0 value.
	 */
	public static final Short ZERO = new Short((short) 0);


	/**
	 * The class constructor. Calls to <code>super()</code> with parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public RadioButtonDataField(final Map parameters) {
		super(parameters);
	}

	@Override
	public Object getValue() {
		// Needed for compatibility
		final Object oValue = super.getValue();
		if (oValue instanceof Number) {
			if (((Number) oValue).intValue() != 0) {
				return RadioButtonDataField.ONE;
			} else {
				return RadioButtonDataField.ZERO;
			}
		}
		return oValue;
	}

	@Override
	protected void createDataField() {
		this.dataField = new JRadioButton() {

			@Override
			public void setOpaque(final boolean opaque) {
				super.setOpaque(false);
			}
		};
	}

}
