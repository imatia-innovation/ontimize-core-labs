package com.ontimize.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;

import javax.swing.ImageIcon;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.util.ParseTools;
import com.ontimize.util.templates.ITemplateField;

public class ParseUtils {

	private static final Logger logger = LoggerFactory.getLogger(ParseUtils.class);

	public static boolean getBoolean(final String s, final boolean defaultValue) {
		return ParseTools.getBoolean(s, defaultValue);
	}

	public static int getInteger(final String s, final int defaultValue) {
		return ParseTools.getInteger(s, defaultValue);
	}

	public static long getLong(final String s, final long defaultValue) {
		return ParseTools.getLong(s, defaultValue);
	}

	public static String getString(final String s, final String defaultValue) {
		return ParseTools.getString(s, defaultValue);
	}

	public static double getDouble(final String s, final double defaultValue) {
		return ParseTools.getDouble(s, defaultValue);
	}

	public static float getFloat(final String s, final float defaultValue) {
		return ParseTools.getFloat(s, defaultValue);
	}

	public static Image getImage(final String path, final Image defaultValue) {
		try {
			final ImageIcon icon = ImageManager.getIcon(path);
			return icon == null ? defaultValue : icon.getImage();
		} catch (final Exception e) {
			ParseUtils.logger.trace(null, e);
			return defaultValue;
		}
	}

	public static ImageIcon getImageIcon(final String path, final ImageIcon defaultValue) {
		try {
			final ImageIcon icon = ImageManager.getIcon(path);
			return icon == null ? defaultValue : icon;
		} catch (final Exception e) {
			ParseUtils.logger.trace(null, e);
			return defaultValue;
		}
	}

	public static Border getBorder(final String borderName, final Border defaultBorder) {
		if (borderName == null) {
			return defaultBorder;
		}
		if ("no".equals(borderName)) {
			return BorderManager.getBorder(BorderManager.EMPTY_BORDER_KEY);
		} else if ("yes".equals(borderName)) {
			return defaultBorder;
		} else if ("".equals(borderName)) {
			return defaultBorder;
		} else {
			final Border border = BorderManager.getBorder(borderName);
			if (border != null) {
				return border;
			}
		}
		return defaultBorder;
	}

	public static Font getFont(final String string, final Font defaultFont) {
		if ((string == null) || "".equals(string)) {
			return defaultFont;
		}
		return Font.decode(string);
	}

	public static Color getColor(final String string, final Color defaultColor) {
		if ((string == null) || "".equals(string)) {
			return defaultColor;
		}
		try {
			return ColorConstants.parseColor(string);
		} catch (final Exception e) {
			ParseUtils.logger.error(null, e);
			return defaultColor;
		}
	}

	public static Paint getPaint(final String string, final Paint defaultPaint) {
		if ((string == null) || "".equals(string)) {
			return defaultPaint;
		}
		Paint paint = null;
		try {
			paint = ColorConstants.paintNameToPaint(string);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				ParseUtils.logger.error(null, e);
			} else {
				ParseUtils.logger.trace(null, e);
			}
			try {
				paint = ColorConstants.parseColor(string);
			} catch (final Exception e1) {
				ParseUtils.logger.trace(null, e1);
				paint = defaultPaint;
			}
		}
		return paint;
	}

	public static Insets getMargin(final String string, final Insets defaultMargin) {
		if (string != null) {
			try {
				return ApplicationManager.parseInsets(string);
			} catch (final Exception e) {
				ParseUtils.logger.trace(null, e);
				return defaultMargin;
			}
		} else {
			return defaultMargin;
		}
	}

	public static ImageIcon getPressedImageIcon(final String pressedIconPath, final String iconPath, final ImageIcon defaultValue) {
		if (pressedIconPath != null) {
			ImageIcon icon = null;
			if (!pressedIconPath.equals("yes")) {
				icon = ImageManager.getIcon(pressedIconPath);
			}
			if (icon != null) {
				return icon;
			} else if (iconPath != null) {
				final int index = iconPath.lastIndexOf(".");
				if (index == -1) {
					return ParseUtils.getImageIcon(iconPath + "_pressed", defaultValue);
				} else {
					return ParseUtils.getImageIcon(
							iconPath.substring(0, index) + "_pressed" + iconPath.substring(index, iconPath.length()),
							defaultValue);
				}
			}
		}
		return defaultValue;
	}

	public static ImageIcon getDisabledImageIcon(final String disabledIconPath, final String iconPath, final ImageIcon defaultValue) {
		if (disabledIconPath != null) {
			ImageIcon icon = null;
			if (!disabledIconPath.equals("yes")) {
				icon = ImageManager.getIcon(disabledIconPath);
			}
			if (icon != null) {
				return icon;
			}
			if (iconPath != null) {
				final int index = iconPath.lastIndexOf(".");
				if (index == -1) {
					return ParseUtils.getImageIcon(iconPath + "_disabled", defaultValue);
				} else {
					return ParseUtils.getImageIcon(
							iconPath.substring(0, index) + "_disabled" + iconPath.substring(index, iconPath.length()),
							defaultValue);
				}
			}
		}
		return defaultValue;
	}

	public static ImageIcon getRolloverImageIcon(final String rolloverIconPath, final String iconPath, final ImageIcon defaultValue) {
		if (rolloverIconPath != null) {
			ImageIcon icon = null;
			if (!rolloverIconPath.equals("yes")) {
				icon = ImageManager.getIcon(rolloverIconPath);
			}
			if (icon != null) {
				return icon;
			}
			if (iconPath != null) {
				final int index = iconPath.lastIndexOf(".");
				if (index == -1) {
					return ParseUtils.getImageIcon(iconPath + "_rollover", defaultValue);
				} else {
					return ParseUtils.getImageIcon(
							iconPath.substring(0, index) + "_rollover" + iconPath.substring(index, iconPath.length()),
							defaultValue);
				}
			}
		}
		return defaultValue;
	}

	public static String getCamelCase(final String[] tokens) {
		final StringBuilder buffer = new StringBuilder();

		for (int i = 0, size = tokens != null ? tokens.length : 0; i < size; i++) {
			final String token = tokens[i];
			final String ccToken = ParseUtils.getCamelCase(token);
			if ((ccToken != null) && (ccToken.length() > 0)) {
				buffer.append(ccToken);
			}
		}
		final String s = buffer.toString();
		return s.toString();
	}

	public static String getCamelCase(final String token) {
		return ParseTools.getCamelCase(token);
	}


	public static int getTemplateDataType(final String templateType, final int defaultTemplateType) {
		if (ITemplateField.DATA_TYPE_FIELD_ATTR.equalsIgnoreCase(templateType)) {
			return ITemplateField.DATA_TYPE_FIELD;
		}
		if (ITemplateField.DATA_TYPE_IMAGE_ATTR.equalsIgnoreCase(templateType)) {
			return ITemplateField.DATA_TYPE_IMAGE;
		}
		if (ITemplateField.DATA_TYPE_TABLE_ATTR.equalsIgnoreCase(templateType)) {
			return ITemplateField.DATA_TYPE_TABLE;
		}
		return defaultTemplateType;
	}

	public static String throwableToString(final Throwable e, final int lines) {
		return ApplicationManager.printStackTrace(e, lines).toString();
	}

	/**
	 * @param object
	 * @param classType
	 * @return
	 * @use ParseTools.getValueForClassType(object, classType);
	 */
	@Deprecated
	public static Object getValueForClassType(final Object object, final int classType) {
		return ParseTools.getValueForClassType(object, classType);
	}

	/**
	 * @param typeName
	 * @return
	 * @use ParseTools.getSQLType(typeName);
	 */
	@Deprecated
	public static int getSQLType(final String typeName) {
		return ParseTools.getSQLType(typeName);
	}

	/**
	 * @param parseType
	 * @param defaultType
	 * @return
	 * @use ParseTools.getSQLType(parseType, defaultType);
	 */
	@Deprecated
	public static int getSQLType(final int parseType, final int defaultType) {
		return ParseTools.getSQLType(parseType, defaultType);
	}

	/**
	 * @param typeName
	 * @param defaultValue
	 * @return
	 * @use ParseTools.getTypeForName(typeName, defaultValue);
	 */
	@Deprecated
	public static int getTypeForName(final String typeName, final int defaultValue) {
		return ParseTools.getTypeForName(typeName, defaultValue);
	}

	/**
	 * @param typeName
	 * @return
	 * @use ParseTools.getIntTypeForName(typeName);
	 */
	@Deprecated
	public static int getIntTypeForName(final String typeName) {
		return ParseTools.getIntTypeForName(typeName);
	}

	/**
	 * @param classType
	 * @return
	 * @use ParseTools.getClassType(classType);
	 */
	@Deprecated
	public static Class getClassType(final int classType) {
		return ParseTools.getClassType(classType);
	}


	/**
	 * @param calendarField
	 * @return
	 * @use ParseTools.getCalendarField(calendarField);
	 */
	@Deprecated
	public static int getCalendarField(final String calendarField) {
		return ParseTools.getCalendarField(calendarField);
	}


	public static Object getValueForSQLType(final Object object, final int sqlType) {
		return ParseTools.getValueForSQLType(object, sqlType);
	}

}
