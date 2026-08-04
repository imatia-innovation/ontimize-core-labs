package com.ontimize.gui.field.document;

import javax.swing.text.AttributeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the document model to insert a NIF value into a JTextField
 *
 * @version 1.0 01/04/2001
 */

public class NIFDocument extends MaskDocument {

    private static final Logger logger = LoggerFactory.getLogger(NIFDocument.class);

    private static final String letters = "TRWAGMYFPDXBNJZSQVHLCKET";

    public NIFDocument() {
        super("%0000000A");
    }

    @Override
    public void insertString(int offset, String stringToInsert, AttributeSet attributes) {
        super.insertString(offset, stringToInsert, attributes);
        // If text length is 8
        try {
            if (this.getLength() == 8) {
                char letter = ' ';
                String DNI = this.getText(0, 8);
                letter = NIFDocument.getLetter(DNI);
                Character insertLetter = new Character(letter);
                super.insertString(8, insertLetter.toString(), attributes);
            }
        } catch (Exception e) {
            if (com.ontimize.gui.ApplicationManager.DEBUG) {
                NIFDocument.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
            }
        }
    }

    public static char calculateLetter(String dni) {
        return NIFDocument.getLetter(dni);
    }

    protected static char getLetter(String DNI) {
        // New standard to create the NIFs number to foreigns citizens
        DNI = DNI.toUpperCase();
        if (DNI.startsWith("X")) {
            DNI = "0" + DNI.substring(1);
        } else if (DNI.startsWith("Y")) {
            DNI = "1" + DNI.substring(1);
        } else if (DNI.startsWith("Z")) {
            DNI = "2" + DNI.substring(1);
        } else if (DNI.startsWith("K") || DNI.startsWith("L") || DNI.startsWith("M")) {
            DNI = DNI.substring(1);
        }

        Integer dniNumber = Integer.valueOf(DNI);
        int pos = dniNumber.intValue() % 23;
        return NIFDocument.letters.charAt(pos);
    }

    public static boolean isNIFWellFormed(String nif) {
        if (nif == null) {
            return false;
        }
        if (nif.length() < 9) {
            return false;
        }
        try {
            char letter = NIFDocument.getLetter(nif.substring(0, 8));
            if (nif.charAt(8) != letter) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            if (com.ontimize.gui.ApplicationManager.DEBUG) {
                NIFDocument.logger.debug("NIFDocument: " + e.getMessage(), e);
            }
            return false;
        }
    }

    public static boolean isNIEWellFormed(String nie) {
        if ((nie == null) || (nie.length() < 9)) {
            return false;
        }
        if (nie.toUpperCase().startsWith("X") || nie.toUpperCase().startsWith("Y")
                || nie.toUpperCase().startsWith("Z")) {
            return false;
        }
        String digits = nie.substring(1, 8);
        char letter = nie.charAt(8);
        try {
            char correctLetter = NIFDocument.getLetter(digits);
            return letter == correctLetter;
        } catch (Exception e) {
            NIFDocument.logger.error(null, e);
            return false;
        }
    }

}
