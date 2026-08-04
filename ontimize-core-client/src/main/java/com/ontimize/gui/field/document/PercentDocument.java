package com.ontimize.gui.field.document;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;

public class PercentDocument extends RealDocument {

    private static final Logger logger = LoggerFactory.getLogger(PercentDocument.class);

    protected boolean limit100 = true;

    public PercentDocument() {
        this.setMinimumFractionDigits(0);
        this.setMaximumFractionDigits(0);
    }

    @Override
    public Number getValue() {
        Number n = super.getValue();
        if (n != null) {
            return Double.valueOf(n.doubleValue() / 100.0);
        }
        return Double.valueOf(0.0);
    }

    @Override
    public void format() {
        super.format();
        try {
            if ((this.getLength() > 0) && (this.getText(0, this.getLength()).indexOf("%") < 0)) {
                super.insertStringWithoutCheck(this.getLength(), "%", null);
            }
        } catch (Exception e) {
            PercentDocument.logger.trace(null, e);
        }
    }

    @Override
    public void setValue(Number value) {
        // theoretically value is a number between 0 and 1
        if (value == null) {
            return;
        }
        double v = value.doubleValue() * 100.0;
        super.setValue(Double.valueOf(v));
        this.format();
    }

    @Override
    public void insertString(int offset, String s, AttributeSet at) throws BadLocationException {
        // Try with the text. Possible values between 100 and -100
        String currentText = this.getText(0, this.getLength());
        int posPercent = currentText.indexOf("%");
        if ((posPercent != -1) && (offset > posPercent)) {
            return;
        }
        StringBuilder sb = new StringBuilder(currentText);
        sb.insert(offset, s);
        try {
            Number n = this.formatter.parse(sb.toString());
            double d = n.doubleValue();
            if (this.isLimit100() && ((d > 100.0d) || (d < -100.0d))) {
                throw new IllegalArgumentException("percent can´t be <-100 or >100");
            } else {
                super.insertString(offset, s, at);
            }
        } catch (Exception ex) {
            if (ApplicationManager.DEBUG) {
                PercentDocument.logger.debug(null, ex);
            } else {
                PercentDocument.logger.trace(null, ex);
            }
        }
    }

    public boolean isLimit100() {
        return this.limit100;
    }

    public void setLimit100(boolean limit100) {
        this.limit100 = limit100;
    }

}
