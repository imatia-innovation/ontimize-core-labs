package com.caucho.hessian.util;

import java.io.InputStream;
import java.lang.reflect.Method;

public final class MangleTools {

	private MangleTools() {
		super();
	}

	/**
	 * Creates a unique mangled method name based on the method name and the method parameters.
	 *
	 * @param method
	 *            the method to mangle
	 * @param isFull
	 *            if true, mangle the full classname
	 * @return a mangled string.
	 */
	public static String mangleName(final Method method, final boolean isFull) {
		final StringBuilder sb = new StringBuilder();

		sb.append(method.getName());

		final Class<?>[] params = method.getParameterTypes();
		for (int i = 0; i < params.length; i++) {
			sb.append('_');
			sb.append(mangleClass(params[i], isFull));
		}

		return sb.toString();
	}

	/**
	 * Mangles a classname.
	 */
	public static String mangleClass(final Class<?> cl, final boolean isFull) {
		final String name = cl.getName();

		if ("boolean".equals(name) || name.equals("java.lang.Boolean")) {
			return "boolean";
		} else if ("int".equals(name) || "java.lang.Integer".equals(name) || "short".equals(name)
				|| "java.lang.Short".equals(name) || "byte".equals(name) || "java.lang.Byte"
				.equals(name)) {
			return "int";
		} else if ("long".equals(name) || "java.lang.Long".equals(name)) {
			return "long";
		} else if ("float".equals(name) || "java.lang.Float".equals(name) || "double".equals(name)
				|| "java.lang.Double".equals(name)) {
			return "double";
		} else if ("java.lang.String".equals(name) || "com.caucho.util.CharBuffer".equals(name) || "char".equals(name)
				|| "java.lang.Character".equals(name) || "java.io.Reader"
				.equals(name)) {
			return "string";
		} else if ("java.util.Date".equals(name) || "com.caucho.util.QDate".equals(name)) {
			return "date";
		} else if (InputStream.class.isAssignableFrom(cl) || "[B".equals(name)) {
			return "binary";
		} else if (cl.isArray()) {
			return "[" + mangleClass(cl.getComponentType(), isFull);
		} else if ("org.w3c.dom.Node".equals(name) || "org.w3c.dom.Element".equals(name)
				|| "org.w3c.dom.Document".equals(name)) {
			return "xml";
		} else if (isFull) {
			return name;
		} else {
			final int p = name.lastIndexOf('.');
			if (p > 0) {
				return name.substring(p + 1);
			} else {
				return name;
			}
		}
	}
}
