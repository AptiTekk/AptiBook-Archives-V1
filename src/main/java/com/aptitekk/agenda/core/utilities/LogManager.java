package com.aptitekk.agenda.core.utilities;

import org.jboss.logging.Logger;

public class LogManager {

    private static final Logger LOGGER = Logger.getLogger(LogManager.class);

    public static void logInfo(String message) {
        LOGGER.info(message);
    }

    public static void logError(String message) {
        LOGGER.error(message);
    }

    public static void logDebug(String message) {
        LOGGER.debug(message);
    }

}
