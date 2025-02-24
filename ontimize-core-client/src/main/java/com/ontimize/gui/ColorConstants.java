package com.ontimize.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

public final class ColorConstants {

	public static Color veryLightGray = new Color(240, 240, 240);

	public static Color veryLightPink = new Color(255, 206, 255);

	public static Color veryLightBlue = new Color(206, 209, 255);

	public static Color veryLightSkyblue = new Color(220, 220, 240);

	public static Color veryLightGreen = new Color(214, 255, 206);

	public static Color veryLightYellow = new Color(253, 252, 210);

	public static Color veryLightYellow2 = new Color(253, 252, 220);

	public static Color yellowCompFocus = new Color(255, 255, 230);

	public static Color veryLightRed = new Color(227, 191, 189);

	public static Color lightGreyishBlue = new Color(200, 205, 240);

	public static Color lightGreyishBlue2 = new Color(200, 205, 220);

	public static final String GRAY = "gray";

	public static final String BLACK = "black";

	public static final String BLUE = "blue";

	public static final String RED = "red";

	public static final String YELLOW = "yellow";

	public static final String CYAN = "cyan";

	public static final String DARKGRAY = "darkgray";

	public static final String GREEN = "green";

	public static final String LIGHTGRAY = "lightgray";

	public static final String MAGENTA = "magenta";

	public static final String ORANGE = "orange";

	public static final String PINK = "pink";

	public static final String WHITE = "white";

	public static final String DARKBLUE = "darkblue";

	protected static Map userColors = null;

	protected static Map userPaints = null;

	protected static Map colorsCache = new Hashtable();

	public static Color colorNameToColor(final String name) throws Exception {
		if ((ColorConstants.userColors != null) && ColorConstants.userColors.containsKey(name)) {
			return (Color) ColorConstants.userColors.get(name);
		}
		if (name.equalsIgnoreCase(ColorConstants.GRAY)) {
			return Color.gray;
		}
		if (name.equalsIgnoreCase(ColorConstants.BLACK)) {
			return Color.black;
		}
		if (name.equalsIgnoreCase(ColorConstants.BLUE)) {
			return Color.blue;
		}
		if (name.equalsIgnoreCase(ColorConstants.RED)) {
			return Color.red;
		}
		if (name.equalsIgnoreCase(ColorConstants.YELLOW)) {
			return Color.yellow;
		}
		if (name.equalsIgnoreCase(ColorConstants.CYAN)) {
			return Color.cyan;
		}
		if (name.equalsIgnoreCase(ColorConstants.DARKGRAY)) {
			return Color.darkGray;
		}
		if (name.equalsIgnoreCase(ColorConstants.GREEN)) {
			return Color.green;
		}
		if (name.equalsIgnoreCase(ColorConstants.LIGHTGRAY)) {
			return Color.lightGray;
		}
		if (name.equalsIgnoreCase(ColorConstants.MAGENTA)) {
			return Color.magenta;
		}
		if (name.equalsIgnoreCase(ColorConstants.ORANGE)) {
			return Color.orange;
		}
		if (name.equalsIgnoreCase(ColorConstants.PINK)) {
			return Color.pink;
		}
		if (name.equalsIgnoreCase(ColorConstants.WHITE)) {
			return Color.white;
		}
		if (name.equalsIgnoreCase(ColorConstants.DARKBLUE)) {
			return Color.blue.darker();
		}
		throw new Exception(ColorConstants.class.getName() + " Unknown color " + name);

	}

	/**
	 * @param rgb String with RGB values separated by ';' (For example 124;100;100)
	 * @return
	 * @throws Exception
	 */
	public static Color colorRGBToColor(final String rgb) throws Exception {
		final StringTokenizer st = new StringTokenizer(rgb, ";");
		if (st.countTokens() != 3) {
			throw new Exception("Invalid values");
		}
		try {
			final int r = Integer.parseInt(st.nextToken());
			final int g = Integer.parseInt(st.nextToken());
			final int b = Integer.parseInt(st.nextToken());
			return new Color(r, g, b);
		} catch (final Exception e) {
			throw e;
		}
	}

	public static Color colorRGBHexToColor(final String rgb) throws Exception {
		if (((rgb.length() != 7) && (rgb.length() != 9)) || !rgb.startsWith("#")) {
			throw new Exception("Invalid values " + rgb + " not hexadecimal color");
		} else {
			if (rgb.length() == 7) {
				final String rH = rgb.substring(1, 3);
				final String gH = rgb.substring(3, 5);
				final String bH = rgb.substring(5, 7);
				try {
					final int r = Integer.parseInt(rH, 16);
					final int g = Integer.parseInt(gH, 16);
					final int b = Integer.parseInt(bH, 16);
					return new Color(r, g, b);
				} catch (final Exception e) {
					throw e;
				}
			} else if (rgb.length() == 9) {
				try {
					final String rH = rgb.substring(1, 3);
					final String gH = rgb.substring(3, 5);
					final String bH = rgb.substring(5, 7);
					final String alphaH = rgb.substring(7, 9);

					final int r = Integer.parseInt(rH, 16);
					final int g = Integer.parseInt(gH, 16);
					final int b = Integer.parseInt(bH, 16);
					final int alpha = Integer.parseInt(alphaH, 16);
					return new Color(r, g, b, alpha);
				} catch (final Exception e) {
					throw e;
				}
			}
		}
		throw new Exception("Invalid values " + rgb + " not hexadecimal color");
	}

	public static Color parseColor(final String color) throws Exception {
		if (color == null) {
			throw new IllegalArgumentException("String parameter can not be null");
		}
		if (ColorConstants.colorsCache.containsKey(color)) {
			return (Color) ColorConstants.colorsCache.get(color);
		}
		if (color.startsWith("#")) {
			final Color c = ColorConstants.colorRGBHexToColor(color);
			ColorConstants.colorsCache.put(color, c);
			return c;
		} else if (color.indexOf(";") >= 0) {
			final Color c = ColorConstants.colorRGBToColor(color);
			ColorConstants.colorsCache.put(color, c);
			return c;
		} else {
			final Color c = ColorConstants.colorNameToColor(color);
			ColorConstants.colorsCache.put(color, c);
			return c;
		}
	}

	public static void addUserColor(final String colorName, final Color c) throws Exception {
		if (ColorConstants.userColors == null) {
			ColorConstants.userColors = new Hashtable();
		}
		ColorConstants.userColors.put(colorName, c);

	}

	public static void addUserColor(final String colorName, final String rgb) throws Exception {
		ColorConstants.addUserColor(colorName, ColorConstants.parseColor(rgb));
	}

	public static void addUserPaint(final String paintName, final Paint c) throws Exception {
		if (ColorConstants.userPaints == null) {
			ColorConstants.userPaints = new Hashtable();
		}
		ColorConstants.userPaints.put(paintName, c);
	}

	public static Paint paintNameToPaint(final String s) throws Exception {
		if (ColorConstants.userPaints.containsKey(s)) {
			return (Paint) ColorConstants.userPaints.get(s);
		} else {
			throw new Exception("Paint " + s + " not defined");
		}
	}

	public static String colorToRGB(final Color c) {
		return c.getRed() + ";" + c.getGreen() + ";" + c.getBlue();
	}

	public static String colorToHEX(final Color c) {
		return Integer.toHexString(c.getRed()) + ";" + Integer.toHexString(c.getGreen()) + ";"
				+ Integer.toHexString(c.getBlue());
	}

	static {
		if (ColorConstants.userPaints == null) {
			ColorConstants.userPaints = new Hashtable();
		}
		ColorConstants.userPaints.put("blue", new GradientPaint(0.0F, 0.0F, Color.white, 1000.0F, 0.0F, Color.blue));

	}

}
