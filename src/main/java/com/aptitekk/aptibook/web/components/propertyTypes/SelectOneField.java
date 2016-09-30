/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.propertyTypes;

import com.aptitekk.aptibook.web.components.propertyTypes.abstractTypes.PropertyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SelectOneField extends PropertyType {

    private final List<String> values;

    public SelectOneField(String label, List<String> values) {
        super(label);
        this.values = new ArrayList<>(values);
    }

    public SelectOneField(String label, Set<String> values) {
        this(label, values, false);
    }

    public SelectOneField(String label, Set<String> values, boolean sort) {
        super(label);

        List<String> listValues = new ArrayList<>(values);
        if (sort)
            Collections.sort(listValues);
        this.values = listValues;
    }

    public List<String> getValues() {
        return values;
    }

}
