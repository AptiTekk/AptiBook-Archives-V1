/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.properties;

import com.aptitekk.aptibook.core.entities.Property;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PropertyInputGroup {

    private Property.Group propertyGroup;
    private Map<Property.Key, String> propertiesInputMap = new LinkedHashMap<>();

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

    public Map<Property.Key, String> getPropertiesInputMap() {
        return propertiesInputMap;
    }

    List<Property> getPropertyEntityList() {
        return propertyEntityList;
    }

    void constructProperties() {
        for (Property.Key key : propertiesInputMap.keySet()) {
            UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent(":propertiesEditForm:propertyGroup" + key.ordinal());
            if (component != null && component instanceof HtmlPanelGroup) {
                key.getPropertyType().setId("propertyField" + key.ordinal());
                key.getPropertyType().setValue(propertiesInputMap.get(key));
                key.getPropertyType().injectIntoGroup((HtmlPanelGroup) component);
            }
        }
    }
}

