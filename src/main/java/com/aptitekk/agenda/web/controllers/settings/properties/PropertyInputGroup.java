/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.properties;

import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.properties.PropertyGroup;
import com.aptitekk.agenda.core.properties.PropertyKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PropertyInputGroup {

    private PropertyGroup propertyGroup;
    private Map<PropertyKey, String> propertiesInputMap = new LinkedHashMap<>();

    private List<Property> propertyEntityList = new ArrayList<>();

    PropertyInputGroup(PropertyGroup propertyGroup, List<Property> propertyEntityList) {
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

    public PropertyGroup getPropertyGroup() {
        return propertyGroup;
    }

    public Map<PropertyKey, String> getPropertiesInputMap() {
        return propertiesInputMap;
    }

    List<Property> getPropertyEntityList() {
        return propertyEntityList;
    }

}

