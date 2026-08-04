package com.ontimize.gui.field.document;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ColorConstants;

public class ColorDocument extends PlainDocument {

    private static final Logger logger = LoggerFactory.getLogger(ColorDocument.class);

    protected Color colorValue = null;

    protected int returnMode = 0;

    public static final int RETURN_COLOR = 1;

    public static final int RETURN_RGB_STRING = 2;

    public static final int RETURN_HEX_STRING = 3;

    public static final int RETURN_NUMBER = 4;

    public static final int RETURN_INTEGER = 5;

    public ColorDocument(int returnType) {
        super();
        if ((returnType < 1) || (returnType > 5)) {
            throw new IllegalArgumentException(
                    "ColorDocument: invalid value for 'returnType', must be between 1 and 5");
        }
        this.returnMode = returnType;
    }

    @Override
    public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
        if (string.equals(".")) {
            string = ";";
        }
        if (string.length() == 0) {
            return;
        }
        String resultText = this.getInsertTextResult(offset, string);
        Color color = this.parseText(resultText);
        if (color != null) {
            this.colorValue = color;
            super.insertString(offset, string.toUpperCase(), attributes);
        }
    }

    protected String getInsertTextResult(int offset, String string) {
        try {
            String text = this.getText(0, this.getLength());
            if (ColorDocument.checkTextIsNotNullOrLenghtZero(text)) {
                return string;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(0, offset));
            sb.append(string);
            sb.append(text.subSequence(offset, text.length()));
            return sb.toString();
        } catch (Exception ex) {
            ColorDocument.logger.error(null, ex);
            return "";
        }
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        String resultText = this.getRemoveTextResult(offset, length);
        if (resultText != null) {
            if (resultText.length() == 0) {
                this.colorValue = null;
                super.remove(offset, length);
            } else {
                Color color = this.parseText(resultText);
                if (color != null) {
                    this.colorValue = color;
                    super.remove(offset, length);
                }
            }
        }
    }

    protected String getRemoveTextResult(int offset, int length) {
        try {
            String text = this.getText(0, this.getLength());
            if (ColorDocument.checkTextIsNotNullOrLenghtZero(text)) {
                return "";
            }
            if ((offset + length) > text.length()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(0, offset));
            sb.append(text.substring(offset + length, text.length()));
            return sb.toString();
        } catch (Exception ex) {
            ColorDocument.logger.error(null, ex);
            return "";
        }
    }

    protected Color parseText(String text) {
        if (ColorDocument.checkTextIsNotNullOrLenghtZero(text)) {
            return null;
        }

        if (text.startsWith("#")) {
            return this.parseHashText(text);
        } else {
            if (Character.isDigit(text.charAt(0))) {
                return parseNumberText(text);
            }
        }
        return null;
    }

    protected Color parseNumberText(String text) {
        int semicolonNumber = ColorDocument.getSemicolonNumber(text);
        if (semicolonNumber > 2) {
            return null;
        }
        String[] parts = text.split(";");
        if ((semicolonNumber == 2) && (parts.length == 1)) {
            return null;
        }

        if ((parts == null) || (parts.length == 0)) {
            return Color.black;
        } else if (parts.length > 3) {
            return null;
        } else {
            try {
                int r = 0;
                if (parts[0] != null) {
                    if (parts[0].length() > 3) {
                        return null;
                    }
                    r = Integer.parseInt(parts[0]);
                }
                int g = 0;
                if ((parts.length > 1) && (parts[1] != null)) {
                    if (parts[1].length() > 3) {
                        return null;
                    }
                    g = Integer.parseInt(parts[1]);
                }
                int b = 0;
                if ((parts.length == 3) && (parts[2] != null)) {
                    if (parts[2].length() > 3) {
                        return null;
                    }
                    b = Integer.parseInt(parts[2]);
                }
                if ((r == 0) && (g == 0) && (b == 0)) {
                    return Color.black;
                }
                return new Color(r, g, b);
            } catch (Exception ex) {
                ColorDocument.logger.trace(null, ex);
                return null;
            }
        }
    }

    protected Color parseHashText(String text) {
        if (text.length() <= 7) {
            String rH = null;
            String gH = null;
            String bH = null;
            if (text.length() > 5) {
                bH = text.substring(5);
            }

            gH = this.checkTexctLenghtMoreThanFourThree(text, gH);

            rH = this.checkTextLenghtMoreThanTwoOne(text, rH);

            try {
                int r = 0;
                r = this.parseTextReduceComplexity(rH, r);
                int g = 0;
                g = this.parseTextReduceComplexity(gH, g);
                int b = 0;
                b = this.parseTextReduceComplexity(bH, b);
                if ((r == 0) && (g == 0) && (b == 0)) {
                    return Color.black;
                }
                return new Color(r, g, b);
            } catch (Exception ex) {
                ColorDocument.logger.trace(null, ex);
                return null;
            }
        }

        return null;
    }


    /**
     * Method used to reduce the complexity of {@link #parseText(String)}
     * @param text
     * @param gH
     * @return
     */
    protected String checkTexctLenghtMoreThanFourThree(String text, String gH) {
        if (text.length() > 4) {
            gH = text.substring(3, 5);
        } else if (text.length() > 3) {
            gH = text.substring(3, 4);
        }
        return gH;
    }

    /**
     * Method used to reduce the complexity of {@link #parseText(String)}
     * @param text
     * @param rH
     * @return
     */
    protected String checkTextLenghtMoreThanTwoOne(String text, String rH) {
        if (text.length() > 2) {
            rH = text.substring(1, 3);
        } else if (text.length() > 1) {
            rH = text.substring(1, 2);
        }
        return rH;
    }

    /**
     * Method used to reduce the complexity of {@link #parseText(String)}
     * @param text
     * @return
     */
    protected static boolean checkTextIsNotNullOrLenghtZero(String text) {
        return (text == null) || (text.length() == 0);
    }

    /**
     * Method used to reduce the complexity of {@link #parseText(String)}
     * @param bH
     * @param b
     * @return
     * @throws NumberFormatException
     */
    protected int parseTextReduceComplexity(String bH, int b) throws NumberFormatException {
        if (bH != null) {
            b = Integer.parseInt(bH, 16);
        }
        return b;
    }

    protected static int getSemicolonNumber(String text) {
        if (ColorDocument.checkTextIsNotNullOrLenghtZero(text)) {
            return 0;
        }
        int i = 0;
        int index = text.indexOf(";");
        if (index != -1) {
            i++;
            while ((index = text.indexOf(";", index + 1)) != -1) {
                i++;
            }
        }
        return i;
    }

    public Object getValue() {
        switch (this.returnMode) {
            case RETURN_COLOR:
                return this.getColorValue();
            case RETURN_RGB_STRING:
                return this.getRGBStringValue();
            case RETURN_HEX_STRING:
                return this.getHEXStringValue();
            case RETURN_NUMBER:
                return this.getNumericalValue();
            case RETURN_INTEGER:
                return this.getIntegerValue();
        }
        return null;
    }

    public Color getColorValue() {
        return this.colorValue;
    }

    public String getRGBStringValue() {
        if (this.colorValue == null) {
            return null;
        } else {
            return ColorConstants.colorToRGB(this.colorValue);
        }
    }

    public String getHEXStringValue() {
        if (this.colorValue == null) {
            return null;
        } else {
            return ColorConstants.colorToHEX(this.colorValue);
        }
    }

    public Number getNumericalValue() {
        if (this.colorValue == null) {
            return null;
        } else {
            return Integer.valueOf(ColorDocument.colorToInt(this.colorValue));
        }
    }

    public Integer getIntegerValue() {
        if (this.colorValue == null) {
            return null;
        } else {
            return Integer.valueOf(this.colorValue.getRGB());
        }
    }

    public void setValue(Object value) {
        if (value instanceof Color) {
            this.setColorValue((Color) value);
        } else if (value instanceof String) {
            this.setStringValue((String) value);
        } else if (value instanceof Integer) {
            this.setIntegerValue((Integer) value);
        } else if (value instanceof Number) {
            this.setNumberValue((Number) value);
        } else {
            this.setColorValue(null);
        }
    }

    public void setColorValue(Color color) {
        try {
            String text = null;
            try {
                text = this.getText(0, this.getLength());
            } catch (Exception ex) {
                ColorDocument.logger.error(null, ex);
            }
            this.remove(0, this.getLength());
            this.colorValue = null;
            if (color != null) {
                if ((text == null) || !text.startsWith("#")) {
                    String string = ColorConstants.colorToRGB(color);
                    this.insertString(0, string, null);
                } else {
                    String string = ColorConstants.colorToHEX(color);
                    this.insertString(0, string, null);
                }
            }
            this.colorValue = color;
        } catch (Exception ex) {
            ColorDocument.logger.error(null, ex);
        }
    }

    public void setStringValue(String colorString) {
        Color color = null;
        try {
            color = ColorConstants.parseColor(colorString);
        } catch (Exception ex) {
            ColorDocument.logger.trace(null, ex);
        }
        this.setColorValue(color);
    }

    public void setNumberValue(Number colorNumber) {
        Color color = null;
        if (colorNumber != null) {
            color = ColorDocument.intToColor(colorNumber.intValue());
        }
        this.setColorValue(color);
    }

    public void setIntegerValue(Integer rgb) {
        Color color = null;
        if (rgb != null) {
            color = new Color(rgb.intValue());
        }
        this.setColorValue(color);
    }

    public static int colorToInt(Color color) {
        return (color.getRed() * ColorDocument.DCS2) + (color.getGreen() * 256) + color.getBlue();
    }

    public static final int DCS2 = (int) Math.pow(256, 2);

    public static final int DCS3 = (int) Math.pow(256, 3);

    public static Color intToColor(int num) {
        if (num < 0) {
            return null;
        } else if (num >= ColorDocument.DCS3) {
            num = ColorDocument.DCS3 - 1;
        }

        int red = num / ColorDocument.DCS2;
        int green = (num - (red * ColorDocument.DCS2)) / 256;
        int blue = num - (red * ColorDocument.DCS2) - (green * 256);

        return new Color(red, green, blue);
    }

}
