/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;

public abstract class PropertyType {

    String id;

    Object value;

    private String label;

    public PropertyType(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public abstract void injectIntoGroup(HtmlPanelGroup formGroup);

    public String getLabel() {
        return label;
    }
}
