/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.util.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Named
@ApplicationScoped
public class AptiBookInfoController implements Serializable {

    private static final Properties aptiBookProperties = new Properties();

    /**
     * Contains the attributions for AptiBook. Key: Library Name. Value: License Text.
     */
    private static final Map<String, String> attributionsMap;

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

        attributionsMap = new LinkedHashMap<>();

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

    @PostConstruct
    private void init() {

    }

    public String getVersion() {
        return aptiBookProperties.getProperty("version");
    }

    public Map<String, String> getAttributionsMap() {
        return attributionsMap;
    }

}
