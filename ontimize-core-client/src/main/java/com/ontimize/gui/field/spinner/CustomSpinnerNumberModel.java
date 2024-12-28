package com.ontimize.gui.field.spinner;

import java.io.Serializable;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.ontimize.jee.common.util.ParseTools;
import com.ontimize.util.ParseUtils;

public class CustomSpinnerNumberModel extends SpinnerNumberModel implements Serializable {

	protected Number stepSize, value;

	protected Comparable minimum, maximum;

	protected int numberClass;

	public CustomSpinnerNumberModel() {
		this(ParseTools.INTEGER_, new Integer(0), null, null, new Integer(1));
	}

	public CustomSpinnerNumberModel(final double value, final double minimum, final double maximum, final double stepSize) {
		this(ParseTools.DOUBLE_, new Double(value), new Double(minimum), new Double(maximum), new Double(stepSize));
	}

	public CustomSpinnerNumberModel(final int value, final int minimum, final int maximum, final int stepSize) {
		this(ParseTools.INTEGER_, new Integer(value), new Integer(minimum), new Integer(maximum),
				new Integer(stepSize));
	}

	/**
	 * Full constructor, with possibility to parse values to concrete value type
	 * @param numberclass
	 * @param initialValue
	 * @param minValue
	 * @param maxValue
	 * @param stepValue
	 */
	public CustomSpinnerNumberModel(final int numberclass, final Number initialValue, final Comparable minValue, final Comparable maxValue,
			final Number stepValue) {
		if ((initialValue == null) || (stepValue == null)) {
			throw new IllegalArgumentException("value and stepSize must be non-null");
		}
		if (!(((minValue == null) || (minValue.compareTo(initialValue) <= 0))
				&& ((maxValue == null) || (maxValue.compareTo(initialValue) >= 0)))) {
			throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
		}
		this.numberClass = numberclass;
		this.value = initialValue;
		this.minimum = minValue;
		this.maximum = maxValue;
		this.stepSize = stepValue;
	}

	/**
	 * Returns the next number in the sequence.
	 * @return <code>value + stepSize</code> or <code>null</code> if the sum exceeds
	 *         <code>maximum</code>.
	 *
	 * @see SpinnerModel#getNextValue
	 * @see #getPreviousValue
	 * @see #setStepSize
	 */
	@Override
	public Object getNextValue() {
		return this.incrValue(+1);
	}

	/**
	 * Returns the previous number in the sequence.
	 * @return <code>value - stepSize</code>, or <code>null</code> if the sum is less than
	 *         <code>minimum</code>.
	 *
	 * @see SpinnerModel#getPreviousValue
	 * @see #getNextValue
	 * @see #setStepSize
	 */
	@Override
	public Object getPreviousValue() {
		return this.incrValue(-1);
	}

	/**
	 * Returns the value of the current element of the sequence.
	 * @return the value property
	 * @see #setValue
	 * @see #getNumber
	 */
	@Override
	public Object getValue() {
		return this.value;
	}

	/**
	 * Sets the current value for this sequence. No bounds checking is done here; the new value may
	 * invalidate the <code>(minimum &lt;= value &lt;= maximum)</code> invariant enforced by the
	 * constructors. It's also possible to set the value to be something that wouldn't naturally occur
	 * in the sequence, i.e. a value that's not modulo the <code>stepSize</code>. This is to simplify
	 * updating the model, and to accommodate spinners that don't want to restrict values that have been
	 * directly entered by the user. Naturally, one should ensure that the
	 * <code>(minimum &lt;= value &lt;= maximum)</code> invariant is true before calling the
	 * <code>next</code>, <code>previous</code>, or <code>setValue</code> methods.
	 * <p>
	 * This method fires a <code>ChangeEvent</code> if the value has changed.
	 * @param value the current (non <code>null</code>) <code>Number</code> for this sequence
	 * @throws IllegalArgumentException if <code>value</code> is <code>null</code> or not a
	 *         <code>Number</code>
	 * @see #getNumber
	 * @see #getValue
	 * @see SpinnerModel#addChangeListener
	 */
	@Override
	public void setValue(Object value) {

		if (value == null) {
			if (this.value != null) {
				this.value = (Number) value;
				this.fireStateChanged();
			}
			return;
		}

		if (!(value instanceof Number)) {
			throw new IllegalArgumentException("illegal value");
		}

		// Addapt to correct data type
		value = ParseUtils.getValueForClassType(value, this.numberClass);

		if (!value.equals(this.value)) {

			if (this.checkMaximumConstraint((Number) value)) {
				throw new IllegalArgumentException("illegal value");
			}
			if (this.checkMinimumConstraint((Number) value)) {
				throw new IllegalArgumentException("illegal value");
			}

			this.value = (Number) value;
			this.fireStateChanged();
		}
	}

	protected Number incrValue(final int dir) {

		if (this.value == null) {
			if (dir > 0) {
				if (this.minimum instanceof Number) {
					this.value = (Number) this.minimum;
				} else {
					this.value = new Integer(0);
				}
			} else {
				if (this.maximum instanceof Number) {
					this.value = (Number) this.maximum;
				} else {
					this.value = new Integer(0);
				}
			}
		}

		Number newValue;
		if ((this.value instanceof Float) || (this.value instanceof Double)) {
			final double v = this.value.doubleValue() + (this.stepSize.doubleValue() * dir);
			if (this.value instanceof Double) {
				newValue = new Double(v);
			} else {
				newValue = new Float(v);
			}
		} else {
			final long v = this.value.longValue() + (this.stepSize.longValue() * dir);

			if (this.value instanceof Long) {
				newValue = new Long(v);
			} else if (this.value instanceof Integer) {
				newValue = new Integer((int) v);
			} else if (this.value instanceof Short) {
				newValue = new Short((short) v);
			} else {
				newValue = new Byte((byte) v);
			}
		}

		newValue = (Number) ParseUtils.getValueForClassType(newValue, this.numberClass);

		if (this.checkMaximumConstraint(newValue)) {
			return (Number) this.maximum;
		}
		if (this.checkMinimumConstraint(newValue)) {
			return (Number) this.minimum;
		} else {
			return newValue;
		}
	}

	private boolean checkMaximumConstraint(final Number newValue) {
		return (this.maximum != null) && (this.maximum.compareTo(newValue) < 0);
	}

	private boolean checkMinimumConstraint(final Number newValue) {
		return (this.minimum != null) && (this.minimum.compareTo(newValue) > 0);
	}

}
