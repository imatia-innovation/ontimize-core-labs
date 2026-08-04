package com.ontimize.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ColorConstants;

/**
 * <p>
 * Utility class to create new class objects.
 * <p>
 * Uses Reflection Java API.
 *
 * @author Imatia Innovation S.L.
 * @since Ontimize 5.2059EN
 */
public class ClassHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClassHandler.class);

    // Class object for each primitive type key.
    protected static final Map PRIMITIVE_CLASS_TYPE = new Hashtable();
    static {
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("byte", Byte.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("int", Integer.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("integer", Integer.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("short", Short.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("long", Long.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("float", Float.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("double", Double.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("bool", Boolean.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("boolean", Boolean.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("char", Character.TYPE);
        ClassHandler.PRIMITIVE_CLASS_TYPE.put("character", Character.TYPE);
    }

    // Class name for each primitive type key.
    protected static final Map PRIMITIVE_CLASS_OBJECT = new Hashtable();
    static {
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("byte", Byte.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("int", Integer.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("integer", Integer.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("short", Short.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("long", Long.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("float", Float.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("double", Double.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("bool", Boolean.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("boolean", Boolean.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("char", Character.class.getName());
        ClassHandler.PRIMITIVE_CLASS_OBJECT.put("character", Character.class.getName());
    }

    private static final String MSG_CLASS_INVALID = "Invalid class ";

    /**
     * <p>
     * Returns the {@link Class} object for the given <code>className</code>.
     * @param className The class name of the {@link Class} object.
     * @return {@link Class} object for the given <code>className</code>.
     */
    public static Class getClassObject(String className) throws IllegalArgumentException {
        return ClassHandler.getClassObject(className, false);
    }

    /**
     * <p>
     * Returns the {@link Class} object for the given <code>className</code>.
     * <p>
     * Allows to return {@link Class} objects for the Java primitive types.
     * @param className The class name of the {@link Class} object.
     * @param primitiveTypes If true, return the {@link Class} for Java primitive types.
     * @return {@link Class} object for the given <code>className</code>.
     */
    public static Class getClassObject(String className, boolean primitiveTypes) throws IllegalArgumentException {
        String clazz = className;
        if (primitiveTypes) { // Return the class for the primitive type
            Object oPrimClassType = ClassHandler.PRIMITIVE_CLASS_TYPE.get(clazz.toLowerCase());
            if ((oPrimClassType != null) && (oPrimClassType instanceof Class)) {
                return (Class) oPrimClassType;
            }
        } else { // Check the class name for the primitive type.
            Object oPrimClassObj = ClassHandler.PRIMITIVE_CLASS_OBJECT.get(clazz.toLowerCase());
            if ((oPrimClassObj != null) && (oPrimClassObj instanceof String)) {
                clazz = oPrimClassObj.toString();
            }
        }

        try {
            Class classObject = Class.forName(clazz);
            return classObject;
        } catch (Exception t) {
            throw new IllegalArgumentException(ClassHandler.MSG_CLASS_INVALID + clazz, t);
        }
    }

    /**
     * <p>
     * Returns the {@link Object} instance for the given <code>className</code>
     * @param className The class name of the {@link Class} object.
     * @return {@link Object} instance for the given <code>className</code>
     * @throws IllegalArgumentException
     */
    public static Object getClassInstance(String className) throws IllegalArgumentException {
        Class clazz = ClassHandler.getClassObject(className);
        try {
            Object o = clazz.newInstance();
            return o;
        } catch (Exception t) {
            throw new IllegalArgumentException(t.getMessage(), t);
        }
    }

    /**
     * <p>
     * Returns the {@link Method} of the given Object <code>source</code> with the given
     * <code>name</code>.
     * @param source Object with the method.
     * @param name of the method to return.
     * @return {@link Method} with the given <code>name</code>
     */
    public static Method getMethodObject(Object source, String name) {
        if ((source == null) || (name == null) || (name.length() == 0)) {
            return null;
        }

        try {
            Class sourceClass = source.getClass();

            Method current = null;
            Method[] methods = sourceClass.getMethods();
            for (int i = 0, size = methods != null ? methods.length : 0; (current == null) && (i < size); i++) {
                Method m = methods[i];
                if (m != null) {
                    String methodName = m.getName();
                    if ((methodName != null) && (name.compareTo(methodName) == 0)) {
                        current = m;
                    }
                }
            }
            return current;
        } catch (Exception e) {
            ClassHandler.logger.error(null, e);
        }
        return null;
    }

    /**
     * <p>
     * Returns the {@link Method} of the given Object <code>source</code> with the <code>name</code> and
     * the parameter <code>class</code>.
     * @param source Object with the method.
     * @param name of the method to return.
     * @param clazz Array with the {@link Class} paramters of the method.
     * @return {@link Method} with the given <code>name</code> and <code>clazz</code> parameters.
     */
    public static Method getMethodObject(Object source, String name, Class[] clazz) {
        if ((source == null) || (name == null) || (name.length() == 0)) {
            return null;
        }

        try {
            Class sourceClass = source.getClass();
            Method method = sourceClass.getMethod(name, clazz);
            return method;
        } catch (NoSuchMethodException e) {
            ClassHandler.logger.error(null, e);
        } catch (Exception e) {
            ClassHandler.logger.error(null, e);
        }
        return null;
    }

    /**
     * <p>
     * Returns the given {@link String} <code>value</code> converted into the <code>className</code>
     * {@link Class}.
     *
     * <p>
     * Some classes have a custom converter, else the default converter uses the contructor class with
     * the String parameter (a {@link IllegalArgumentException} can be thrown).
     * @param className The class name of the returned object value.
     * @param value The {@link String} to convert.
     * @return The string value converted.
     */
    public static Object getValueObject(String className, String value) throws IllegalArgumentException {
        Class clazz = ClassHandler.getClassObject(className);

        Object oConverter = ClassHandler.STRING_CONVERTERS.get(clazz);
        if ((oConverter == null) || !(oConverter instanceof StringConverter)) {
            DefaultStringConverter dConverter = new DefaultStringConverter();
            dConverter.setTargetClass(clazz);
            oConverter = dConverter;
        }

        StringConverter converter = (StringConverter) oConverter;
        Object instance = converter.convert(value);
        return instance;
    }

    // String to Object conversion

    protected static Map STRING_CONVERTERS = new Hashtable();
    static {
        ClassHandler.STRING_CONVERTERS.put(java.lang.Boolean.class, new BooleanStringConverter());
        ClassHandler.STRING_CONVERTERS.put(java.awt.Color.class, new ColorStringConverter());
    }

    public static interface StringConverter {

        public Object convert(String value) throws IllegalArgumentException;

    }

    public static class DefaultStringConverter implements StringConverter {

        private static final String MSG_VALUE_INVALID = "Invalid value ";

        protected Class targetClass = null;

        @Override
        public Object convert(String value) throws IllegalArgumentException {
            if (this.targetClass == null) {
                return null;
            }

            try {
                Constructor c = this.targetClass.getConstructor(new Class[] { String.class });
                Object instance = c.newInstance(new Object[] { value });
                return instance;
            } catch (Exception t) {
                throw new IllegalArgumentException(DefaultStringConverter.MSG_VALUE_INVALID + value, t);
            }
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

    }

    public static class BooleanStringConverter implements StringConverter {

        @Override
        public Object convert(String value) throws IllegalArgumentException {
            boolean b = ParseUtils.getBoolean(value, false);
            return Boolean.valueOf(b);
        }

    }

    public static class ColorStringConverter implements StringConverter {

        @Override
        public Object convert(String value) throws IllegalArgumentException {
            try {
                Object o = ColorConstants.parseColor(value);
                return o;
            } catch (Exception t) {
                throw new IllegalArgumentException(t != null ? t.getMessage() : null, t);
            }
        }

    }

}
