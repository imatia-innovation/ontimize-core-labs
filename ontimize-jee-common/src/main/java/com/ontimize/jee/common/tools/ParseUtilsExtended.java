package com.ontimize.jee.common.tools;

import java.awt.Font;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.util.Base64Utils;
import com.ontimize.jee.common.util.ParseTools;

/**
 * The Class ParseUtilsExtended.
 */
public class ParseUtilsExtended extends ParseTools {

	private static final Logger logger = LoggerFactory.getLogger(ParseUtilsExtended.class);

	public static final int BASE64 = 6464;
	public static final int UUID = 6465;

	protected final static Pattern ISO8601 = Pattern.compile(
			"^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$");

	/**
	 * Gets the boolean.
	 * @param s the s
	 * @return the boolean
	 */
	public static boolean getBoolean(final Object s) {
		return ParseUtilsExtended.getBoolean(s, false);
	}

	public static Boolean getBooleanOrNull(final Object s) {
		if (s != null) {
			final String value = s.toString();
			if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)
					|| "y".equalsIgnoreCase(value) || "s".equalsIgnoreCase(value)) {
				return true;
			} else if ("no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)
					|| "n".equalsIgnoreCase(value)) {
				return false;
			}
		}
		return null;
	}

	/**
	 * Gets the boolean.
	 * @param s the s
	 * @param defaultValue the default value
	 * @return the boolean
	 */
	public static boolean getBoolean(final Object s, final boolean defaultValue) {
		if (s != null) {
			final String value = s.toString();
			if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)
					|| "y".equalsIgnoreCase(value) || "s".equalsIgnoreCase(value)) {
				return true;
			} else if ("no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)
					|| "n".equalsIgnoreCase(value)) {
				return false;
			}
		}
		return defaultValue;
	}

	/**
	 * Convert into a map a string separate with : and ; in this mode: textToParse =
	 * "one:two;three:four" return value
	 *
	 * <pre>
	 * (one,two)
	 * (trhee,four)
	 * </pre>
	 *
	 * @param textToParse the text to parse
	 * @param defaultValue the default value
	 * @return the map
	 */
	public static Map<String, String> getMap(final String textToParse, final Map<String, String> defaultValue) {
		if ((textToParse == null) || "".equals(textToParse)) {
			return defaultValue;
		}
		final Map<String, String> res = new HashMap<>();
		final String[] split = textToParse.split(Pattern.quote(";"));
		for (final String part : split) {
			final String[] mids = part.split(Pattern.quote(":"));
			if (mids.length == 2) {
				res.put(mids[0], mids[1]);
			} else {
				res.put(mids[0], mids[0]);
			}
		}
		return res;
	}

	/**
	 * Obtiene un constante de una clase.
	 * @param clazz the clazz
	 * @param fieldName the field name
	 * @param defaultValue the default value
	 * @return the constant of class
	 */
	public static Object getConstantOfClass(final Class<?> clazz, final String fieldName, final Object defaultValue) {
		try {
			final Field declaredField = clazz.getDeclaredField(fieldName);
			return declaredField.get(null);
		} catch (final Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Analiza el nombre de una clase.
	 * @param clazzName the s
	 * @param defaultValue the default value
	 * @return the clazz
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static Class<?> getClazz(final String clazzName, final Class<?> defaultValue) throws ClassNotFoundException {
		if ((clazzName != null) && !"".equals(clazzName)) {
			return Class.forName(clazzName);
		}
		return defaultValue;
	}

	/**
	 * Gets the class.
	 * @param className the class name
	 * @param parameters the parameters
	 * @param values the values
	 * @param defaultValue the default value
	 * @return the class
	 */
	public static Object getClazz(final String className, final Class[] parameters, final Object[] values, final Object defaultValue) {
		if ((className == null) || "".equals(className)) {
			return defaultValue;
		}
		try {
			final Class<?> cl = Class.forName(className);
			final Constructor<?> constructor = cl.getConstructor(parameters);
			return constructor.newInstance(values);
		} catch (final Exception ex) {
			ParseUtilsExtended.logger.error(null, ex);
			return defaultValue;
		}
	}

	/**
	 * Crea una instancia de una clase. Necesita un constructor sin parametros
	 * @param <T> the generic type
	 * @param clazzName the clazz name
	 * @param defaultValue the default value
	 * @return the clazz
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static <T> T getClazzInstance(final String clazzName, final T defaultValue) throws ClassNotFoundException {
		try {
			final Class<?> clazz = ParseUtilsExtended.getClazz(clazzName, null);
			return (T) clazz.newInstance();
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Gets the clazz instance.
	 * @param <T> the generic type
	 * @param clazzName the clazz name
	 * @param parameters the parameters
	 * @param defaultValue the default value
	 * @return the clazz instance
	 */
	public static <T> T getClazzInstance(final String clazzName, final Object[] parameters, final T defaultValue) {
		try {
			return (T) ReflectionTools.newInstance(clazzName, parameters);
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	public static <T> T getClazzInstance(final String clazzName, final String defaultClassName, final Object... parameters) {
		if (" ".equals(clazzName)) {
			return null;
		}
		try {
			return (T) ReflectionTools.newInstance(clazzName, parameters);
		} catch (final Exception ex) {
			return (T) ReflectionTools.newInstance(defaultClassName, parameters);
		}
	}

	/**
	 * Analiza el nombre de un metodo de una clase.
	 * @param defaultValue the default value
	 * @param clazz the clazz
	 * @param methodName the method name
	 * @param methodParameterTypes the method parameter types
	 * @return the method
	 */
	public static Method getMethod(final Method defaultValue, final Class<?> clazz, final String methodName,
			final Class<?>... methodParameterTypes) {
		try {
			final Method declaredMethod = clazz.getDeclaredMethod(methodName, methodParameterTypes);
			declaredMethod.setAccessible(true);
			return declaredMethod;
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Analiza un campo de una clase.
	 * @param fieldName the field name
	 * @param clazz the clazz
	 * @param defaultValue the default value
	 * @return the field
	 * @throws SecurityException the security exception
	 */
	public static Field getField(final String fieldName, final Class<?> clazz, final Field defaultValue)
			throws SecurityException {
		try {
			final Field declaredField = clazz.getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			return declaredField;
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Trocea la entrada en tantas partes como separators encuentre.
	 * @param s the s
	 * @param separator the separator
	 * @param defaultValue the default value
	 * @return the string list
	 */
	public static List<String> getStringList(String s, final String separator, final List defaultValue) {
		if ((s == null) || "".equals(s)) {
			return defaultValue;
		}
		s = s.trim();
		final List<String> res = new ArrayList<>();
		if (separator == null) {
			res.add(s);
			return res;
		}
		final StringTokenizer st = new StringTokenizer(s, separator);
		while (st.hasMoreTokens()) {
			res.add(st.nextToken());
		}
		return res;
	}

	public static boolean getBoolean(final String s, final boolean defaultValue) {
		if (s != null) {
			if ("yes".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s) || "Y".equalsIgnoreCase(s)
					|| "S".equalsIgnoreCase(s) || "1".equalsIgnoreCase(s)) {
				return true;
			} else if ("no".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s) || "N".equalsIgnoreCase(s)
					|| "0".equalsIgnoreCase(s)) {
				return false;
			}
		}
		return defaultValue;
	}

	public static int getInteger(final String s, final int defaultValue) {
		if (!StringTools.isEmpty(s)) {
			return Integer.parseInt(s);
		}
		return defaultValue;
	}

	public static long getLong(final String s, final long defaultValue) {
		if (!StringTools.isEmpty(s)) {
			return Long.parseLong(s);
		}
		return defaultValue;
	}

	public static String getString(final String s, final String defaultValue) {
		if ((s != null) && !"".equals(s)) {
			return s;
		}
		return defaultValue;
	}

	public static double getDouble(final String s, final double defaultValue) {
		if (!StringTools.isEmpty(s)) {
			return Double.parseDouble(s);
		}
		return defaultValue;
	}

	public static float getFloat(final String s, final float defaultValue) {
		if (!StringTools.isEmpty(s)) {
			return Float.parseFloat(s);
		}
		return defaultValue;
	}

	public static Font getFont(final String string, final Font defaultFont) {
		if ((string == null) || "".equals(string)) {
			return defaultFont;
		}
		return Font.decode(string);
	}

	public static Map<String, String> getMap(final String value, final String separator1, final String separator2,
			final Map<String, String> defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ParseTools.getTokensAt(value, separator1, separator2);
	}

	public static String getMapString(final Map<Object, Object> map, final String separator1, final String separator2,
			final String defaultValue) {
		if (map == null) {
			return defaultValue;
		}
		final StringBuilder sb = new StringBuilder();
		for (final Entry<Object, Object> entry : map.entrySet()) {
			if (entry.getKey() != null) {
				sb.append(entry.getKey());
				sb.append(ObjectTools.coalesce(separator2, ":"));
				sb.append(ObjectTools.coalesce(entry.getValue(), entry.getKey()));
				sb.append(ObjectTools.coalesce(separator1, ";"));
			}
		}

		return sb.toString();
	}

	public static Map getParametersPreffixed(final Map<Object, Object> parameters, final String preffix) {
		return ParseUtilsExtended.getParametersPreffixed(parameters, preffix, true);
	}

	public static Map getParametersPreffixed(final Map<Object, Object> parameters, final String preffix,
			final Map otherParams) {
		final Map parametersPreffixed = ParseUtilsExtended.getParametersPreffixed(parameters, preffix, false);

		if (otherParams != null) {
			for (final Object key : otherParams.keySet()) {
				MapTools.safePut(parametersPreffixed, key, otherParams.get(key), true);
			}
		}

		for (final Entry<Object, Object> entry : parameters.entrySet()) {
			final String param = entry.getKey().toString();
			if (!param.startsWith(preffix) && !param.contains(".")) {
				MapTools.safePut(parametersPreffixed, entry.getKey(), entry.getValue(), true);
			}
		}

		return parametersPreffixed;
	}

	public static Map getParametersPreffixed(final Map<Object, Object> parameters, final String preffix,
			final boolean includeGenerics) {
		final Map params = new HashMap<>();
		for (final Entry<Object, Object> entry : parameters.entrySet()) {
			final String param = entry.getKey().toString();
			if (param.startsWith(preffix)) {
				MapTools.safePut(params, entry.getKey().toString().substring(preffix.length()), entry.getValue(),
						false);
			} else if (!(param.contains(".")) && includeGenerics) {
				MapTools.safePut(params, entry.getKey(), entry.getValue(), true);
			}
		}

		return params;
	}

	/**
	 * Gets the horizontal align.
	 * @param s the s
	 * @param defaultValue the default value
	 * @return the horizontal align
	 */
	public static int getHorizontalAlign(final String s, final int defaultValue) {
		if ("left".equals(s)) {
			return SwingConstants.LEFT;
		} else if ("right".equals(s)) {
			return SwingConstants.RIGHT;
		} else if ("center".equals(s)) {
			return SwingConstants.CENTER;
		} else {
			return defaultValue;
		}
	}

	/**
	 * Gets the vertical align.
	 * @param s the s
	 * @param defaultValue the default value
	 * @return the vertical align
	 */
	public static int getVerticalAlign(final String s, final int defaultValue) {
		if ("top".equals(s)) {
			return SwingConstants.TOP;
		} else if ("bottom".equals(s)) {
			return SwingConstants.BOTTOM;
		} else if ("center".equals(s)) {
			return SwingConstants.CENTER;
		} else {
			return defaultValue;
		}
	}

	/**
	 * Gets the placement.
	 * @param s the s
	 * @param defaultValue the default value
	 * @return the placement
	 */
	public static int getPlacement(final String s, final int defaultValue) {
		if ("top".equals(s)) {
			return SwingConstants.TOP;
		} else if ("left".equals(s)) {
			return SwingConstants.LEFT;
		} else if ("bottom".equals(s)) {
			return SwingConstants.BOTTOM;
		} else if ("right".equals(s)) {
			return SwingConstants.RIGHT;
		} else {
			return defaultValue;
		}
	}

	public static Object getValueForSQLType(final Object object, final int sqlType) {
		switch (sqlType) {
			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
				return ParseUtilsExtended.parseDate(object);
			case java.sql.Types.TIMESTAMP:
				return ParseUtilsExtended.parseTimpestamp(object);
			case ParseUtilsExtended.BASE64:
				return ParseUtilsExtended.parseBase64(object);
			case ParseUtilsExtended.UUID:
				return ParseUtilsExtended.parseUUID(object);
			default:
				return ParseTools.getValueForSQLType(object, sqlType);
		}
	}

	public static Date parseDate(final Object date) {
		if (date instanceof Long) {
			return new Date(((Long) date).longValue());
		} else if (date instanceof String) {
			final String sDate = (String) date;
			if (ParseUtilsExtended.ISO8601.matcher(sDate) != null) {
				final Calendar calendar = DatatypeConverter.parseDate(sDate);
				return calendar.getTime();
			}
		} else if (date instanceof Date) {
			return (Date) date;
		}
		return null;
	}

	public static Timestamp parseTimpestamp(final Object time) {
		if (time instanceof Long) {
			return new Timestamp((Long) time);
		} else if (time instanceof String) {
			final String sTime = (String) time;
			final Calendar calendar = DatatypeConverter.parseTime(sTime);
			return new Timestamp(calendar.getTimeInMillis());
		} else if (time instanceof Timestamp) {
			return (Timestamp) time;
		}
		return null;
	}

	public static byte[] parseBase64(final Object base64) {
		if (base64 instanceof String) {
			try {
				return Base64Utils.decode(((String) base64).toCharArray());
			} catch (final Exception error) {

			}
		}
		return null;
	}

	public static UUID parseUUID(final Object uuid) {
		if (uuid instanceof String) {
			try {
				return java.util.UUID.fromString((String) uuid);
			} catch (final Exception error) {
				return null;
			}
		}
		return null;
	}
}
