/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util;

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
