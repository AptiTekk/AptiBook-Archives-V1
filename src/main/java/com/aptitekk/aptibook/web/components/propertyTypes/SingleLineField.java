/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.propertyTypes;

import com.aptitekk.aptibook.web.components.propertyTypes.abstractTypes.RegexPropertyType;

public class SingleLineField extends RegexPropertyType {

    public SingleLineField(String label, int maxLength, String pattern, String validationMessage) {
        super(label, maxLength, pattern, validationMessage);
    }

    public SingleLineField(String label, int maxLength) {
        this(label, maxLength, null, null);
    }

}
