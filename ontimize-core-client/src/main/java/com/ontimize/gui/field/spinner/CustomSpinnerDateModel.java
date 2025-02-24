package com.ontimize.gui.field.spinner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.SpinnerDateModel;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;

public class CustomSpinnerDateModel extends SpinnerDateModel {

    private static final Logger logger = LoggerFactory.getLogger(CustomSpinnerDateModel.class);

    protected Calendar value;

    public CustomSpinnerDateModel() {
        super();
    }

    public CustomSpinnerDateModel(Comparable start, Comparable end, int calendarField) {
        this(new Date(), start, end, calendarField);
    }

    public CustomSpinnerDateModel(Date value, Comparable start, Comparable end, int calendarField) {
        super(value, start, end, calendarField);
    }

    @Override
    public Object getValue() {
        if (this.value == null) {
            return null;
        }
        return this.value.getTime();
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            if (value != this.value) {
                this.value = null;
                this.fireStateChanged();
            }
            return;
        }
        if ((value == null) || !(value instanceof Date)) {
            throw new IllegalArgumentException("null value");
        }
        if (this.value == null) {
            this.value = Calendar.getInstance();
        }
        if (!value.equals(this.value.getTime())) {
            this.value.setTime((Date) value);
            this.fireStateChanged();
        }
    }

    @Override
    public Object getNextValue() {
        if (this.value == null) {
            this.value = Calendar.getInstance();
            if (this.getStart() instanceof Date) {
                this.value.setTime((Date) this.getStart());
            }
            return this.value.getTime();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.value.getTime());
            cal.add(this.getCalendarField(), 1);
            Date next = cal.getTime();
            return (this.getEnd() == null) || (this.getEnd().compareTo(next) >= 0) ? next : null;
        }
    }

    @Override
    public Object getPreviousValue() {
        if (this.value == null) {
            this.value = Calendar.getInstance();
            if (this.getStart() instanceof Date) {
                this.value.setTime((Date) this.getEnd());
            }
            return this.value.getTime();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.value.getTime());
            cal.add(this.getCalendarField(), -1);
            Date prev = cal.getTime();
            return (this.getStart() == null) || (this.getStart().compareTo(prev) <= 0) ? prev : null;
        }
    }

    public static class SpinnerDateDocument extends PlainDocument implements Internationalization {

        protected static Locale defaultLocale = Locale.getDefault();

        protected SimpleDateFormat dateFormat = null;

        protected String datePattern;

        protected Date currentDate;

        protected Timestamp currentTimestamp = null;

        protected Locale locale = SpinnerDateDocument.defaultLocale;

        public SpinnerDateDocument() {
            super();
        }

        public SpinnerDateDocument(String datePattern) {
            super();
            this.datePattern = datePattern;

            this.dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM,
                    SpinnerDateDocument.defaultLocale);

            DateFormatSymbols symbols = new DateFormatSymbols();
            symbols.setLocalPatternChars("GyMdkHmsSEDFwWahKz");
            this.dateFormat.setDateFormatSymbols(symbols);

            this.dateFormat.applyPattern(datePattern);
            this.currentDate = this.dateFormat.getCalendar().getTime();
        }

        public void setValue(Date value) {
            try {
                String stringDate = this.dateFormat.format(value);
                this.remove(0, this.getLength());
                this.insertString(0, stringDate, null);
                this.currentDate = this.dateFormat.getCalendar().getTime();
            } catch (Exception e) {
                if (ApplicationManager.DEBUG) {
                    CustomSpinnerDateModel.logger.debug(this.getClass().toString() + ": " + e.getMessage().length(), e);
                }
            }
        }

        @Override
        public void setComponentLocale(Locale l) {
            this.locale = l;
            if (this.dateFormat != null) {
                this.dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, this.locale);
                this.dateFormat.getCalendar().setTime(this.currentDate);
                this.dateFormat.applyPattern(this.datePattern);
            }
        }

        @Override
        public List getTextsToTranslate() {
            return null;
        }

        @Override
        public void setResourceBundle(ResourceBundle resourceBundle) {
        }

    }

}
