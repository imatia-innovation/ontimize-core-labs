package com.ontimize.report.engine.dynamicjasper;

import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Utility to convert the SQL types defined in java.sql.Types.
 *
 * @author Imatia Innovation S.L.
 * @since 07/06/2007
 */
public class TypeMappingsUtils {

    public static final String STRING = "String", BOOLEAN = "Boolean", BINARY = "Binary", DOUBLE = "Double",
            DATE = "Date", INTEGER = "Integer",

            STRING_PATH = "java.lang.String", BOOLEAN_PATH = "java.lang.Boolean", OBJECT_PATH = "java.lang.Object",
            DOUBLE_PATH = "java.lang.Double", DATE_PATH = "java.util.Date",
            INTEGER_PATH = "java.lang.Integer";

    public static Class getClass(int type) {
        switch (type) {
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
                return String.class;

            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.BIGINT:
                return Integer.class;

            case Types.BIT:
            case Types.BOOLEAN:
                return Boolean.class;

            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.REAL:
            case Types.NUMERIC:
            case Types.FLOAT:
                return Double.class;

            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return Date.class;

            case Types.BINARY:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
            case Types.ARRAY:
            case Types.BLOB:
            case Types.OTHER:
                return Object.class;
        }
        return Object.class;
    }

    public static Class getClass(String type) {
        return TypeMappingsUtils.getClass(TypeMappingsUtils.getSQLType(type));
    }

    public static String getClassName(int type) {
        switch (type) {
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
                return TypeMappingsUtils.STRING_PATH;

            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.BIGINT:
                return TypeMappingsUtils.INTEGER_PATH;

            case Types.BIT:
            case Types.BOOLEAN:
                return TypeMappingsUtils.BOOLEAN_PATH;

            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.REAL:
            case Types.NUMERIC:
            case Types.FLOAT:
                return TypeMappingsUtils.DOUBLE_PATH;

            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return TypeMappingsUtils.DATE_PATH;

            case Types.BINARY:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
            case Types.ARRAY:
            case Types.BLOB:
            case Types.OTHER:
                return TypeMappingsUtils.OBJECT_PATH;
        }
        return TypeMappingsUtils.OBJECT_PATH;
    }

    public static String getClassName(String type) {
        return TypeMappingsUtils.getClassName(TypeMappingsUtils.getSQLType(type));
    }

    public static int getSQLType(String type) {
        int returned = Types.OTHER;
        if (type.equalsIgnoreCase(TypeMappingsUtils.STRING)) {
            returned = Types.VARCHAR;
        } else if (type.equalsIgnoreCase(TypeMappingsUtils.BOOLEAN)) {
            returned = Types.BOOLEAN;
        } else if (type.equalsIgnoreCase(TypeMappingsUtils.BINARY)) {
            returned = Types.BINARY;
        } else if (type.equalsIgnoreCase(TypeMappingsUtils.DOUBLE)) {
            returned = Types.DOUBLE;
        } else if (type.equalsIgnoreCase(TypeMappingsUtils.DATE)) {
            returned = Types.DATE;
        } else if (type.equalsIgnoreCase(TypeMappingsUtils.INTEGER)) {
            returned = Types.INTEGER;
        }

        // Returns java.sql.Types.OTHER
        return returned;
    }

    /**
     * Transforms current Map with SQL types as strings in a new Map with SQL types as integers.
     */
    public static Map convertStrSQLMap2IntSQLMap(Map m) {
        HashMap newMap = new HashMap(m.size());

        Set k = m.keySet();
        Collection v = m.values();

        Iterator ik = k.iterator();
        Iterator iv = v.iterator();

        while (ik.hasNext() && iv.hasNext()) {
            Object o = ik.next();
            if (!(o instanceof String)) {
                iv.next();
                continue;
            }
            String key = (String) o;

            o = iv.next();
            if (!(o instanceof String)) {
                continue;
            }
            String value = (String) o;

            newMap.put(key, Integer.valueOf(TypeMappingsUtils.getSQLType(value)));
        }
        return newMap;
    }

}
