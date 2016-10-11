/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util;

import com.mindscapehq.raygun4java.core.RaygunClient;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogManager {

    private static final Logger LOGGER = Logger.getLogger(LogManager.class);
    private static final String RAYGUN_API_KEY = System.getenv("RAYGUN_APIKEY");
    private static final RaygunClient RAYGUN_CLIENT = RAYGUN_API_KEY != null ? new RaygunClient(RAYGUN_API_KEY) : null;

    public static void logInfo(Class clazz, String message) {
        LOGGER.info("[" + clazz.getSimpleName() + "] " + message);
    }

    public static void logError(Class clazz, String message) {
        LOGGER.error("[" + clazz.getSimpleName() + "] " + message);
    }

    public static void logException(Class clazz, String message, Throwable t) {
        LOGGER.error("[" + clazz.getSimpleName() + "] " + message, t);
        if (RAYGUN_CLIENT != null && AptiBookInfoProvider.isUsingHeroku()) {
            List<String> tags = new ArrayList<>();
            tags.add("V. " + AptiBookInfoProvider.getVersion());
            tags.add(clazz.getSimpleName());

            Map<String, String> data = new HashMap<>();
            data.put("message", message);
            RAYGUN_CLIENT.Send(t, tags, data);
        }
    }

    public static void logDebug(Class clazz, String message) {
        LOGGER.debug("[" + clazz.getSimpleName() + "] " + message);
    }

}
