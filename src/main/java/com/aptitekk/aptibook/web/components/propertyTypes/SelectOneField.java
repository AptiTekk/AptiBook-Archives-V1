/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.propertyTypes;

import com.aptitekk.aptibook.web.components.propertyTypes.abstractTypes.PropertyType;
import com.aptitekk.aptibook.web.components.propertyTypes.abstractTypes.RegexPropertyType;

import java.util.Set;

public class SelectOneField extends PropertyType {

    private final Set<String> values;

    public SelectOneField(String label, Set<String> values) {
        super(label);
        this.values = values;
    }

    public Set<String> getValues() {
        return values;
    }

}
