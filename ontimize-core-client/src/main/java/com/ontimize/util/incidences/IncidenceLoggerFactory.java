package com.ontimize.util.incidences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncidenceLoggerFactory {

    private static final Logger logger = LoggerFactory.getLogger(IncidenceLoggerFactory.class);

    public static IIncidenceLogger incidenceLoggerInstance(final String loggerFactoryClassName) {

        if (loggerFactoryClassName == null) {
            IncidenceLoggerFactory.logger.error("No logger factory is binded");
            return null;
        }


		final String loggerClassFactory = LoggerFactory.getILoggerFactory().getClass().getName();

        if (loggerFactoryClassName.equals(loggerClassFactory)) {
            return new LogbackIncidenceLogger();
        }

        return null;
    }

}
