package com.aptitekk.agenda.core.utilities;

public class LogManager {

    private static final org.jboss.logging.Logger LOGGER = org.jboss.logging.Logger.getLogger(LogManager.class);

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
