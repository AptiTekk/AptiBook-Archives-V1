/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.properties;

import com.aptitekk.aptibook.core.domain.entities.property.Property;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PropertyInputGroup {

    private Property.Group propertyGroup;
    private Map<Property.Key, Object> propertiesInputMap = new LinkedHashMap<>();

    private List<Property> propertyEntityList = new ArrayList<>();

    PropertyInputGroup(Property.Group propertyGroup, List<Property> propertyEntityList) {
        this.propertyGroup = propertyGroup;

        this.propertyEntityList = propertyEntityList;
        resetInputMap();
    }

    void resetInputMap() {
        if (propertyEntityList != null) {
            for (Property property : propertyEntityList) {
                propertiesInputMap.put(property.getPropertyKey(), property.getPropertyValue());
            }
        }
    }

    public Property.Group getPropertyGroup() {
        return propertyGroup;
    }

    public Map<Property.Key, Object> getPropertiesInputMap() {
        return propertiesInputMap;
    }

    List<Property> getPropertyEntityList() {
        return propertyEntityList;
    }

}

