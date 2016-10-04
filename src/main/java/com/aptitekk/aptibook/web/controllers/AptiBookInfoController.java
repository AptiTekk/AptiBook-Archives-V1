/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.util.LogManager;
import org.apache.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

@Named
@ApplicationScoped
public class AptiBookInfoController implements Serializable {

    private static final Properties aptiBookProperties = new Properties();
    private static final String attributions;

    static {
        try {
            InputStream propertiesStream = AptiBookInfoController.class.getClassLoader().getResourceAsStream("aptibook.properties");
            if (propertiesStream == null)
                throw new IOException("File not found.");

            aptiBookProperties.load(propertiesStream);
            propertiesStream.close();
        } catch (IOException e) {
            LogManager.logError("Could not load aptibook.properties: " + e.getMessage());
        }

        String tempAttributions;
        try {
            InputStream attributionsInputStream = AptiBookInfoController.class.getClassLoader().getResourceAsStream("attributions");
            if (attributionsInputStream == null)
                throw new IOException("File not found.");

            tempAttributions = IOUtils.toString(attributionsInputStream);
            attributionsInputStream.close();
        } catch (IOException e) {
            LogManager.logError("Could not load attributions: " + e.getMessage());
            tempAttributions = null;
        }

        attributions = tempAttributions;
    }

    @PostConstruct
    private void init() {

    }

    public String getVersion() {
        return aptiBookProperties.getProperty("version");
    }

    public String getAttributions() {
        return attributions;
    }

}
