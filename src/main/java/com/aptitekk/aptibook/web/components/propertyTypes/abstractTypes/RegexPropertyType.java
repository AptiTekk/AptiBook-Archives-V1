/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.propertyTypes.abstractTypes;

public abstract class RegexPropertyType extends TextPropertyType {

    private final String pattern;
    private final String validationMessage;

    public RegexPropertyType(String label, int maxLength, String pattern, String validationMessage)
    {
        super(label, maxLength);
        this.pattern = pattern;
        this.validationMessage = validationMessage;
    }

    public String getPattern() {
        return pattern;
    }

    public String getValidationMessage() {
        return validationMessage;
    }
}
