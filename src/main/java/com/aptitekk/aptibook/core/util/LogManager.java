/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util;

import org.jboss.logging.Logger;

public class LogManager {

    private static final Logger LOGGER = Logger.getLogger(LogManager.class);

    public static void logInfo(Class clazz, String message) {
        LOGGER.info("[" + clazz.getSimpleName() + "] " + message);
    }

    public static void logError(Class clazz, String message) {
        LOGGER.error("[" + clazz.getSimpleName() + "] " + message);
    }

    public static void logException(Class clazz, String message, Throwable t) {
        LOGGER.error("[" + clazz.getSimpleName() + "] " + message, t);
    }

    public static void logDebug(Class clazz, String message) {
        LOGGER.debug("[" + clazz.getSimpleName() + "] " + message);
    }

}
