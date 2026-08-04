package com.ontimize.gui.field.document;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.IntegerDataField;

/**
 * This document implements the model for managing integers in a JTextField
 *
 * @version 1.0 01/04/2001
 */

public class IntegerDocument extends PlainDocument {

    private static final Logger logger = LoggerFactory.getLogger(IntegerDocument.class);

    // It occurs a problem with Bigdecimal and BigInteger types that returns
    // ORACLE. If it is compared the value in field and database Value for it,
    // result for equals() is false, this issue provokes errors in tree, form,
    // table,... For solving this problem,
    // it is stored the type used
    // during setValue() and checked when getValue() is called for returning the
    // convenient type.

    public static final int INTEGER = 0;

    public static final int SHORT = 1;

    public static final int LONG = 2;

    public static final int BIGINTEGER = 3;

    public static final int BIGDECIMAL = 4;

    protected int lastNumberTypeUsed = IntegerDocument.INTEGER;

    // El number format and symbols use the machine locale

    protected NumberFormat numberFormat = NumberFormat.getInstance();

    protected DecimalFormatSymbols symbols = new DecimalFormatSymbols();

    protected Number integerValue;

    public boolean isRight() {
        try {
            if (this.getLength() == 0) {
                return true;
            }
            String currentText = this.getText(0, this.getLength());
            // Try to parse the string to check if it is a valid number
            this.numberFormat.parse(currentText);
            return true;
        } catch (Exception e) {
            // The exception is only used to know if the value is valid
            if (ApplicationManager.DEBUG) {
                IntegerDocument.logger.debug(null, e);
            }
            return false;
        }
    }

    public void format() {
        try {
            if (this.getLength() == 0) {
                return;
            }
            String currentText = this.getText(0, this.getLength());
            if ("-".equalsIgnoreCase(currentText)) {
                currentText = "0";
            }
            Number number = this.numberFormat.parse(currentText);
            this.remove(0, this.getLength());
            this.insertString(0, this.numberFormat.format(number).toString(), null);
        } catch (Exception e) {
            if (ApplicationManager.DEBUG) {
                IntegerDocument.logger.debug(null, e);
            }
        }
    }

    public Number getIntegerValue(String s) {
        try {
            Number number = this.getNumericValue(s);
            return number;
        } catch (Exception e) {
            IntegerDocument.logger.trace(null, e);
            return null;
        }
    }

    public int getNumberTypeUsed() {
        return this.lastNumberTypeUsed;
    }

    public void setNumberTypeUsed(int lastNumberTypeUsed) {
        this.lastNumberTypeUsed = lastNumberTypeUsed;
    }

    /**
     * This method returns a <code>Number</code> object in function of <code>type</code> specified in
     * {@link #getNumberTypeUsed()} (This type is also modifiable in parameter <code>"numbertype"</code>
     * of {@link IntegerDataField#init(java.util.Hashtable)}. Available types are:
     *
     * <ul>
     * <li>BIGINTEGER
     * <li>BIGDECIMAL
     * <li>SHORT
     * <li>LONG
     * <li>INTEGER (default)
     * </ul>
     * @param s the string with value
     * @return the correspondent object
     */
    protected Number getNumericValue(String s) {
        try {
            Number number = this.numberFormat.parse(s);
            switch (this.lastNumberTypeUsed) {
                case BIGINTEGER:
                    return BigInteger.valueOf(number.longValue());

                case BIGDECIMAL:
                    return BigDecimal.valueOf(number.longValue());

                case SHORT:
                    return new Short(number.shortValue());

                case LONG:
                    return new Long(number.longValue());
                default:
                    return Integer.valueOf(number.intValue());
            }
        } catch (Exception e) {
            IntegerDocument.logger.debug(null, e);
            return null;
        }
    }

    public void setValue(Number value) {
        if (value instanceof BigDecimal) {
            this.lastNumberTypeUsed = IntegerDocument.BIGDECIMAL;
        } else if (value instanceof BigInteger) {
            this.lastNumberTypeUsed = IntegerDocument.BIGINTEGER;
        } else if (value instanceof Short) {
            this.lastNumberTypeUsed = IntegerDocument.SHORT;
        } else if (value instanceof Long) {
            this.lastNumberTypeUsed = IntegerDocument.LONG;
        } else {
            this.lastNumberTypeUsed = IntegerDocument.INTEGER;
        }
        // Now, represent the value as an integer:
        try {
            this.remove(0, this.getLength());
            // Format the integer value
            String stringValue = this.numberFormat.format(value);
            this.insertString(0, stringValue, null);
        } catch (Exception e) {
            if (ApplicationManager.DEBUG) {
                IntegerDocument.logger.debug(null, e);
            }
        }
    }

    public Number getValue() {
        this.updateValue();
        switch (this.lastNumberTypeUsed) {
            case BIGINTEGER:
                return BigInteger.valueOf(this.integerValue.longValue());

            case BIGDECIMAL:
                return BigDecimal.valueOf(this.integerValue.longValue());

            case SHORT:
                return new Short(this.integerValue.shortValue());

            case LONG:
                return new Long(this.integerValue.longValue());

            default:
                return this.integerValue;
        }
    }

    protected void updateValue() {
        Number previousValue = this.integerValue;
        try {
            String currentText = this.getText(0, this.getLength());
            if (currentText.length() == 0) {
                this.integerValue = this.getNumericValue("0");
            } else {
                this.integerValue = this.getNumericValue(currentText);
            }
        } catch (Exception e) {
            IntegerDocument.logger.trace(null, e);
            this.integerValue = previousValue;
        }
    }

    public IntegerDocument() {
        super();
    }

    @Override
    public void insertString(int offset, String stringValue, AttributeSet attributes) throws BadLocationException {
        if (stringValue.length() == 0) {
            return;
        }
        // This is an integer value. The result string must be a valid integer
        // number,
        // in other case insertion is not allowed
        if (stringValue.length() == 1) {
            if (!Character.isDigit(stringValue.charAt(0))) {
                if ((stringValue.charAt(0) == '-') && (offset == 0)) {
                    try {
                        this.insertStringWithoutCheck(offset, stringValue, attributes);
                        String currentText = this.getText(0, this.getLength());
                        this.integerValue = this.getNumericValue(currentText);
                        return;
                    } catch (Exception e) {
                        if (ApplicationManager.DEBUG) {
                            IntegerDocument.logger.error(null, e);
                        }
                    }
                } else {
                    return;
                }
            }
        }

        try {

            if (this.numberFormat.getMaximumIntegerDigits() > 0) {
                if ((this.getText(0, this.getLength()).indexOf(".") == -1)
                        && (this.getLength() >= this.numberFormat.getMaximumIntegerDigits())) {
                    return;
                } else if (this.getLength() > this.numberFormat.getMaximumIntegerDigits()) {
                    return;
                }
            }
            StringBuilder currentText = new StringBuilder(this.getText(0, this.getLength()));
            currentText.insert(offset, stringValue);
            Number previousValue = this.integerValue;
            this.integerValue = this.getNumericValue(currentText.toString());
            try {
                this.insertStringWithoutCheck(offset, stringValue, attributes);
            } catch (BadLocationException ex) {
                this.integerValue = previousValue;
                if (ApplicationManager.DEBUG) {
                    IntegerDocument.logger.debug(null, ex);
                }
                return;
            }
        } catch (Exception e) {
            if (ApplicationManager.DEBUG) {
                IntegerDocument.logger.debug(null, e);
            }
            return;
        }
    }

    protected void insertStringWithoutCheck(int offset, String stringValue, AttributeSet attributes)
            throws BadLocationException {
        super.insertString(offset, stringValue, attributes);
    }

    protected void removeWithoutCheck(int offset, int len) throws BadLocationException {
        super.remove(offset, len);
    }

    @Override
    public void remove(int offset, int len) throws BadLocationException {
        // Delete. First of all in the buffer because the events
        try {
            Number previousValue = this.integerValue;
            StringBuilder currentText = new StringBuilder(this.getText(0, this.getLength()));
            currentText.delete(offset, offset + len);
            if ((currentText.length() == 0) || currentText.toString().equals("-")) {
                this.integerValue = this.getNumericValue("0");
            } else {
                this.integerValue = this.getNumericValue(currentText.toString());
            }
            try {
                super.remove(offset, len);
            } catch (Exception e) {
                if (ApplicationManager.DEBUG) {
                    IntegerDocument.logger.debug(null, e);
                }
                this.integerValue = previousValue;
            }
        } catch (Exception e) {
            if (ApplicationManager.DEBUG) {
                IntegerDocument.logger.debug(null, e);
            }
        }
    }

    public void setMinimumIntegerDigits(int number) {
        this.numberFormat.setMinimumIntegerDigits(number);
    }

    public void setMaximumIntegerDigits(int number) {
        this.numberFormat.setMaximumIntegerDigits(number);
    }

    public void setGroupingUsed(boolean m) {
        this.numberFormat.setGroupingUsed(m);
    }

}
