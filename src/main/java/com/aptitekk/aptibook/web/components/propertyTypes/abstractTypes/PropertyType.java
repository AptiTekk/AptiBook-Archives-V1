/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.propertyTypes.abstractTypes;

public abstract class PropertyType {

    private String label;

    public PropertyType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
