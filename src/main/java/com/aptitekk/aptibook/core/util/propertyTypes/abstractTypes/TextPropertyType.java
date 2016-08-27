/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes;

public abstract class TextPropertyType extends PropertyType {

    private final int maxLength;

    public TextPropertyType(String label, int maxLength) {
        super(label);
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
