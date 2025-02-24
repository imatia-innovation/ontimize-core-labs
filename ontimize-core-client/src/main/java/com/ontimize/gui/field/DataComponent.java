package com.ontimize.gui.field;

import java.awt.Color;

import com.ontimize.gui.ColorConstants;

/**
 * This interface defines methods used by data components:
 * <ul>
 * <li>getLabelComponentText()
 * <li>getValue()
 * <li>setValue(Object value)
 * <li>deleteData()
 * <li>isEmpty()
 * <li>isModifiable()
 * <li>setModifiable()
 * <li>isHidden()
 * <li>getSQLDataType()
 * <li>isRequired()
 * <li>isModified()
 * <li>setRequired()
 * </ul>
 *
 * It is also defined constants for colors.
 * <p>
 *
 * @see Imatia Innovation
 */
public interface DataComponent extends FormComponent, IdentifiedElement {

    /**
     * The very light gray color key.
     */
    public static Color VERY_LIGHT_GRAY = ColorConstants.veryLightGray;

    /**
     * The very light pink color key.
     */
    public static Color VERY_LIGHT_PINK = ColorConstants.veryLightPink;

    /**
     * The very light blue color key.
     */
    public static Color VERY_LIGHT_BLUE = ColorConstants.veryLightBlue;

    /**
     * The very light skyblue color key.
     */
    public static Color VERY_LIGHT_SKYBLUE = ColorConstants.veryLightSkyblue;

    /**
     * The very light green color key.
     */
    public static Color GREEN_VERY_LIGHT = ColorConstants.veryLightGreen;

    /**
     * The very light yellow color key. RGB: (253,252,210).
     */
    public static Color VERY_LIGHT_YELLOW = ColorConstants.veryLightYellow;

    /**
     * The very light yellow color key. RGB: (253,252,220).
     */
    public static Color VERY_LIGHT_YELLOW_2 = ColorConstants.veryLightYellow2;

    /**
     * The yellow color for focus key.
     */
    public static Color COMP_FOCUS_YELLOW = ColorConstants.yellowCompFocus;

    /**
     * The very light red color key.
     */
    public static Color VERY_LIGHT_RED = ColorConstants.veryLightRed;

    /**
     * The very light greyish color key.
     */
    public static Color LIGHT_GREYISH_BLUE = ColorConstants.lightGreyishBlue;

    /**
     * The very light gray blue color key.
     */
    public static Color LIGHT_GRAY_BLUE = ColorConstants.lightGreyishBlue2;

    /**
     * Returns the label text component. This label will change according to the <code>Locale</code>.
     * <p>
     * @return the label text
     */
    public String getLabelComponentText();

    /**
     * Gets the value of a component.
     * <p>
     * @return the <code>object</code> with value.
     */
    public Object getValue();

    /**
     * Sets the value for component.
     * <p>
     * @param value the <code>object</code> with value to set
     */
    public void setValue(Object value);

    /**
     * The method to delete data for component.
     */
    public void deleteData();

    /**
     * Checks the data existence in component.
     * <p>
     * @return <code>true</code> when component is empty
     */
    public boolean isEmpty();

    /**
     * Checks the modifiable condition for a component. It is useful when it is not desired form deletes
     * a component value.
     * <p>
     * @return <code>true</code> when component is modifiable
     */
    public boolean isModifiable();

    /**
     * Establishes the modifiable condition for a component. When component is not modifiable, data will
     * not be deleted automatically in <code>DeleteFields</code> call.
     * <p>
     * @param modifiable the boolean condition
     */
    public void setModifiable(boolean modifiable);

    /**
     * Indicates to check the visibility condition for a component.
     * <p>
     * @return the visibility condition
     */
    public boolean isHidden();

    /**
     * Returns the <code>sql</code> data type according to <code>java.sql.Types</code>
     * <p>
     * @return the <code>Integer</code> type
     */
    public int getSQLDataType();

    /**
     * Indicates the required condition for a component.
     * <p>
     * @return the required condition
     */
    public boolean isRequired();

    /**
     * This method should be return <code>true</code> when data have been modified. The method
     * <code>setValue()</code> should be change this condition.
     * <p>
     * @return the modified condition
     */
    public boolean isModified();

    /**
     * Sets required a component according to condition.
     * <p>
     * @param required the required condition
     */
    public void setRequired(boolean required);

}
