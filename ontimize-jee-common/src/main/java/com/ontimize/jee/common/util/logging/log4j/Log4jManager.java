package com.ontimize.jee.common.util.logging.log4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.util.logging.ILogManager;
import com.ontimize.jee.common.util.logging.Level;
import com.ontimize.jee.common.util.remote.BytesBlock;


public class Log4jManager implements ILogManager {

    private static final Logger logger = LoggerFactory.getLogger(Log4jManager.class);

    @Override
    public Logger getLogger(final String name) {
        return LoggerFactory.getILoggerFactory().getLogger(name);
    }

    @Override
    public List<Logger> getLoggerList() {
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (!loggerFactory.getClass().isAssignableFrom(Log4jManager.getLog4jFactory())) {
            return null;
        }

        final List<org.slf4j.Logger> loggers = this.getLoggetList(loggerFactory);
        final List<org.slf4j.Logger> loggersComplete = this.completeLoggersHierarchly(loggers);
        Collections.sort(loggersComplete, new LoggerNameComparetor());
        return loggersComplete;
    }

    private List<Logger> completeLoggersHierarchly(final List<Logger> loggers) {
        // TODO Consider to create "parent" unexisting package loggers
        return loggers;
    }

    protected static Class getLog4jFactory() {
        try {
            return Class.forName("org.apache.logging.slf4j.Log4jLoggerFactory");
        } catch (final ClassNotFoundException e) {
            Log4jManager.logger.trace(null, e);
        }
        return null;
    }

    @Override
    public Level getLevel(final Logger logger) {
        if (logger == null) {
            return null;
        }

        // Look again for logger by name UNDER PROPERLY CONTEXT -> The input logger is not correct, uses
        // "Default" context and has not the properly config
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        final Map loggersToUse = this.getValidLoggersToUse(loggerFactory);
        final org.apache.logging.log4j.core.Logger innerLogger = this.getInnerLogger(loggersToUse.get(logger.getName()));
        final org.apache.logging.log4j.Level cLevel = innerLogger.getLevel();
        if (cLevel == null) {
            return null;
        }
        if (cLevel == org.apache.logging.log4j.Level.TRACE) {
            return Level.TRACE;
        } else if (cLevel == org.apache.logging.log4j.Level.DEBUG) {
            return Level.DEBUG;
        } else if (cLevel == org.apache.logging.log4j.Level.INFO) {
            return Level.INFO;
        } else if (cLevel == org.apache.logging.log4j.Level.WARN) {
            return Level.WARN;
        } else if (cLevel == org.apache.logging.log4j.Level.ERROR) {
            return Level.ERROR;
        } else if (cLevel == org.apache.logging.log4j.Level.OFF) {
            return Level.OFF;
        }

        return null;
    }

    @Override
    public void setLevel(final Logger logger, final Level level) throws Exception {
        if (logger == null) {
            return;
        }

        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        final Map loggersToUse = this.getValidLoggersToUse(loggerFactory);
        final org.apache.logging.log4j.core.Logger innerLogger = this.getInnerLogger(loggersToUse.get(logger.getName()));

        org.apache.logging.log4j.Level cLevel = null;
        switch (level) {
            case TRACE:
                cLevel = org.apache.logging.log4j.Level.TRACE;
                break;
            case DEBUG:
                cLevel = org.apache.logging.log4j.Level.DEBUG;
                break;
            case INFO:
                cLevel = org.apache.logging.log4j.Level.INFO;
                break;
            case WARN:
                cLevel = org.apache.logging.log4j.Level.WARN;
                break;
            case ERROR:
                cLevel = org.apache.logging.log4j.Level.ERROR;
                break;
            case OFF:
                cLevel = org.apache.logging.log4j.Level.OFF;
                break;
            default:
                break;
        }
        innerLogger.setLevel(cLevel);
    }


    @Override
    public BytesBlock getFileLogger() {
        BytesBlock bb = null;
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (!loggerFactory.getClass().isAssignableFrom(Log4jManager.getLog4jFactory())) {
            return bb;
        }
        try {
            final Logger logger = loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            final Object innerLogger = getInnerLogger(logger);
            if (innerLogger instanceof org.apache.logging.log4j.core.Logger) {
                final org.apache.logging.log4j.core.Logger logbackLogger = (org.apache.logging.log4j.core.Logger) innerLogger;
                final Map<String, Appender> appenderMap = logbackLogger.getAppenders();
                final Iterator<Appender> appenders = appenderMap.values().iterator();
                while (appenders.hasNext()) {
                    final Appender appender = appenders.next();
                    if (appender instanceof RollingFileAppender) {
                        final RollingFileAppender rp = (RollingFileAppender) appender;
                        final String data = this.readFile(new File(rp.getFileName()));
                        bb = new BytesBlock(data.getBytes());
                    }
                }
            }
        } catch (final Exception e) {
            Log4jManager.logger.error("Error retrieving data from log file.", e);
        }
        return bb;
    }

    public String readFile(final File f) {
        final ArrayDeque<String> queue = new ArrayDeque<String>(500);
        this.readFromLast(queue, f, 500);
        final StringBuilder builder = new StringBuilder();
        while (!queue.isEmpty()) {
            builder.append(queue.removeFirst());
            builder.append("\n");
        }
        return builder.toString();
    }

    public void readFromLast(final ArrayDeque<String> queue, final File file, final int lines) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                this.addToList(queue, line, lines);
                line = br.readLine();
            }
        } catch (final FileNotFoundException e) {
            Log4jManager.logger.error("File not found. ERROR: {}", e.getMessage(), e);
        } catch (final IOException e) {
            Log4jManager.logger.error("File can't be read. ERROR: {}", e.getMessage(), e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (final IOException e) {
                Log4jManager.logger.error("Buffered reader can't be closed. ERROR:{}", e.getMessage(), e);
            }
        }
    }

    protected void addToList(final ArrayDeque<String> queue, final String element, final int lines) {
        if (queue.size() >= lines) {
            queue.removeFirst();
            queue.offer(element);
        } else {
            queue.offer(element);
        }
    }

    ///////////////////////////// REFLECTION UTILITIES /////////////////////////////////
    // protected org.apache.log4j.Logger getInnerLogger(Log4jLoggerAdapter adapter) {
    // return (org.apache.log4j.Logger) Log4jManager.getReflectionFieldValue(adapter, "logger");
    // }

    private org.apache.logging.log4j.core.Logger getInnerLogger(final Logger logger2) {
        return (org.apache.logging.log4j.core.Logger) Log4jManager.getReflectionFieldValue(logger2, "logger");
    }

    private org.apache.logging.log4j.core.Logger getInnerLogger(final Object logger2) {
        return (org.apache.logging.log4j.core.Logger) Log4jManager.getReflectionFieldValue(logger2, "logger");
    }

    // For some extrange reason, when a lloger is requested to logerFactory it gets from a "Default"
    // context, and not from our own context.
    private Map getValidLoggersToUse(final ILoggerFactory loggerFactory) {
		final Map<Object, Map<String, Object>> registry = (Map<Object, Map<String, Object>>) Log4jManager
            .getReflectionFieldValue(loggerFactory, "registry");
        final Map loggersToUse = registry.get(org.apache.logging.log4j.core.LoggerContext.getContext(false));
        return loggersToUse;
    }

    private List<org.slf4j.Logger> getLoggetList(final ILoggerFactory loggerFactory) {
        final Map loggersToUse = this.getValidLoggersToUse(loggerFactory);
        final List<org.slf4j.Logger> list = new ArrayList<Logger>();
        for (final Object o : loggersToUse.values()) {
            final Logger logger2 = loggerFactory.getLogger((String) Log4jManager.getReflectionFieldValue(o, "name"));
            list.add(logger2);
        }
        return list;
    }

    public static Object getReflectionFieldValue(final Object toInvoke, final String fieldName) {
        try {
            final Field field = Log4jManager
                .getReflectionField(toInvoke instanceof Class ? (Class<?>) toInvoke : toInvoke.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(toInvoke instanceof Class ? null : toInvoke);
        } catch (final Exception error) {
            throw new RuntimeException(error);
        }
    }

    public static Field getReflectionField(final Class<?> cl, final String fieldName) {
        for (Class<?> innerClass = cl; innerClass != null; innerClass = innerClass.getSuperclass()) {
            try {
                return innerClass.getDeclaredField(fieldName);
            } catch (final Exception error) {
                Log4jManager.logger.trace(null, error);
                // do nothing
                for (final Class<?> interfaceClass : innerClass.getInterfaces()) {
                    try {
                        return interfaceClass.getDeclaredField(fieldName);
                    } catch (final Exception err) {
                        Log4jManager.logger.trace(null, err);
                    }
                }
            }
        }

        throw new RuntimeException("Field " + fieldName + " not found in" + cl);
    }

    private final class LoggerNameComparetor implements Comparator<Logger> {

        @Override
        public int compare(final org.slf4j.Logger o1, final org.slf4j.Logger o2) {
            if ((o1 == null) || (o2 == null)) {
                return 0;
            }
            return o1.getName().compareTo(o2.getName());
        }

    }

    @Override
    public Object findAppenderOfType(final Class interfaceOfAppender) {
        return null;
    }

}
