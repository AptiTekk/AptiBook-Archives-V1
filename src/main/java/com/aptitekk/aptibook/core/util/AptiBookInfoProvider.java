/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util;

import com.aptitekk.aptibook.web.controllers.AptiBookInfoController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class AptiBookInfoProvider {

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    private static final boolean USING_HEROKU = Boolean.parseBoolean(System.getenv("USING_HEROKU"));

    private static final Properties aptiBookProperties = new Properties();

    /**
     * Contains the attributions for AptiBook. Key: Library Name. Value: License Text.
     */
    private static final Map<String, String> attributionsMap = new LinkedHashMap<>();

    static {
        try {
            InputStream propertiesStream = AptiBookInfoController.class.getClassLoader().getResourceAsStream("aptibook.properties");
            if (propertiesStream == null)
                throw new IOException("File not found.");

            aptiBookProperties.load(propertiesStream);
            propertiesStream.close();
        } catch (IOException e) {
            LogManager.logException(AptiBookInfoController.class, "Could not load aptibook.properties", e);
        }

        try {
            InputStream attributionsInputStream = AptiBookInfoController.class.getClassLoader().getResourceAsStream("attributions.xml");
            if (attributionsInputStream == null)
                throw new IOException("File not found.");

            Document attributionsDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(attributionsInputStream);
            Element documentElement = attributionsDocument.getDocumentElement();
            NodeList attributionsNodes = documentElement.getChildNodes();
            for (int i = 0; i < attributionsNodes.getLength(); i++) {
                Node attributionNode = attributionsNodes.item(i);
                NodeList attributionNodeChildren = attributionNode.getChildNodes();
                String libraryName = null;
                String license = null;
                for (int j = 0; j < attributionNodeChildren.getLength(); j++) {
                    Node attributionNodeChild = attributionNodeChildren.item(j);
                    if (attributionNodeChild != null)
                        switch (attributionNodeChild.getNodeName()) {
                            case "library-name":
                                libraryName = attributionNodeChild.getTextContent();
                                break;
                            case "license":
                                license = attributionNodeChild.getTextContent();
                                break;
                        }
                }
                if (libraryName != null && license != null)
                    attributionsMap.put(libraryName, license);
            }

            attributionsInputStream.close();
        } catch (Exception e) {
            LogManager.logException(AptiBookInfoController.class, "Could not load attributions", e);
        }
    }

    public static void setStarted() {
        STARTED.set(true);
    }

    public static boolean isStarted() {
        return STARTED.get();
    }

    public static boolean isUsingHeroku() {
        return USING_HEROKU;
    }

    public static String getVersion() {
        return aptiBookProperties.getProperty("version");
    }

    public static Map<String, String> getAttributionsMap() {
        return attributionsMap;
    }

}
