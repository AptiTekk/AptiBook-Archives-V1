/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.util.AptiBookInfoProvider;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Named
@ViewScoped
public class AptiBookInfoController implements Serializable {

    private String selectedAttribution = null;

    public String getVersion() {
        return AptiBookInfoProvider.getVersion();
    }

    public Map<String, String> getAttributionsMap() {
        return AptiBookInfoProvider.getAttributionsMap();
    }

    public String getSelectedAttribution() {
        return selectedAttribution;
    }

    public void setSelectedAttribution(String attributionName) {
        selectedAttribution = attributionName;
    }

}
