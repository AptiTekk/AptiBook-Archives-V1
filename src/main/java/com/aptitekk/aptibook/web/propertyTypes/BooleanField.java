/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.propertyTypes;

import com.aptitekk.aptibook.web.propertyTypes.abstractTypes.BooleanPropertyType;

public class BooleanField extends BooleanPropertyType {

    private final String yesLabel;
    private final String noLabel;

    public BooleanField(String label) {
        this(label, null, null);
    }

    public BooleanField(String label, String yesLabel, String noLabel) {
        super(label);
        this.yesLabel = yesLabel;
        this.noLabel = noLabel;
    }

    public String getYesLabel() {
        return yesLabel != null ? yesLabel : "Yes";
    }

    public String getNoLabel() {
        return noLabel != null ? noLabel : "No";
    }
}
