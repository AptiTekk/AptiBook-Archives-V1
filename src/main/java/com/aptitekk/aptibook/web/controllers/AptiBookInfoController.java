/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.util.AptiBookInfoProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;

@Named
@ApplicationScoped
public class AptiBookInfoController implements Serializable {

    public String getVersion() {
        return AptiBookInfoProvider.getVersion();
    }

    public Map<String, String> getAttributionsMap() {
        return AptiBookInfoProvider.getAttributionsMap();
    }

}
