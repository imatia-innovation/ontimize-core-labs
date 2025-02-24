package com.ontimize.gui.field.spinner;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;

import com.ontimize.gui.field.document.RealDocument;
import com.ontimize.gui.i18n.Internationalization;

public class CustomNumberEditor extends NumberEditor implements Internationalization {

    public static class NumberFormatterFactory extends AbstractFormatterFactory {

        protected CustomNumberFormatter formatter = new CustomNumberFormatter();

        protected Class valueClass = Integer.class;

        public NumberFormatterFactory() {
            this.formatter.setValueClass(this.valueClass);
        }

        @Override
        public AbstractFormatter getFormatter(JFormattedTextField tf) {
            return this.formatter;
        }

        public void setFormat(NumberFormat format) {
            this.formatter = new CustomNumberFormatter(format);
            this.formatter.setValueClass(this.valueClass);
        }

        public void setValueClass(Class newValueClass) {
            this.valueClass = newValueClass;
            this.formatter.setValueClass(newValueClass);
        }

    }

    public static class CustomNumberFormatter extends NumberFormatter {

        public CustomNumberFormatter() {
            super();
        }

        public CustomNumberFormatter(NumberFormat format) {
            super(format);
        }

        @Override
        public Object stringToValue(String string) throws ParseException {
            if ((string == null) || (string.length() == 0)) {
                return null;
            }
            return super.stringToValue(string);
        }

    }

    protected NumberFormatterFactory numberFormattedFactory = new NumberFormatterFactory();

    public CustomNumberEditor(JSpinner spinner) {
        super(spinner);
        JFormattedTextField fText = this.getTextField();
        fText.setFormatterFactory(this.numberFormattedFactory);
    }

    public void setValueClass(Class classInstance) {

    }

    public void setDocument(Document document) {
        if (document instanceof RealDocument) {
            RealDocument realDocument = (RealDocument) document;
            this.numberFormattedFactory.setFormat(realDocument.getFormat());
            this.getTextField().setDocument(document);
        }
    }

    public CustomNumberEditor(JSpinner spinner, String decimalFormatPattern) {
        super(spinner, decimalFormatPattern);
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public void setComponentLocale(Locale l) {
        Document doc = this.getTextField().getDocument();
        if (doc instanceof RealDocument) {
            ((RealDocument) doc).setComponentLocale(l);
            this.numberFormattedFactory.setFormat(((RealDocument) doc).getFormat());
        }
    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        // TODO Auto-generated method stub

    }

    // public void setUserValue(Object value){
    // if (value instanceof Double || value instanceof Float) {
    //
    // Document doc = this.getTextField().getDocument();
    // RealDocument document = (RealDocument)doc;
    // try {
    // document.remove(0, document.getLength());
    // // Format with the document formatter
    // String stringValue = document.getFormat().format(value);
    // document.insertString(0, stringValue, null);
    // } catch (Exception e) {
    //
    // }
    // return;
    // } else if (value instanceof SearchValue) {
    // this.setUserValue(((SearchValue)value).getValue());
    // } else if (value instanceof Number) {
    // Number n = (Number) value;
    // this.setUserValue(new Double(n.doubleValue()));
    // // Here no event is fired because this method calls itself
    // } else {
    // // If it is not a number then clear the field
    // try {
    // this.getTextField().getDocument().remove(0,
    // getTextField().getDocument().getLength());
    // } catch (BadLocationException e) {
    // // TODO Auto-generated catch block
    // logger.error(null,e);
    // }
    // return;
    // }
    //
    // }
    //
    //

    // public void stateChanged(ChangeEvent e) {
    // JSpinner spinner = (JSpinner)(e.getSource());
    // Object value = spinner.getValue();
    // if (value==null){
    // try {
    // this.getTextField().getDocument().remove(0,
    // getTextField().getDocument().getLength());
    // } catch (BadLocationException e1) {
    // e1.printStackTrace();
    // }
    // }else{
    // if( value instanceof Number){
    // getTextField().setValue(value);
    // return;
    // }
    // }
    // }

}
