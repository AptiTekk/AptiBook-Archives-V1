/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util.propertyTypes;

import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.RegexPropertyType;
import net.bootsfaces.component.inputTextarea.InputTextarea;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlPanelGroup;

public class MultiLineField extends RegexPropertyType {

    private final int rows;

    public MultiLineField(String label, int rows, int maxLength, String pattern, String validationMessage) {
        super(label, maxLength, pattern, validationMessage);
        this.rows = rows;
    }

    public MultiLineField(String label, int rows, int maxLength) {
        this(label, rows, maxLength, null, null);
    }

    public int getRows() {
        return rows;
    }
}
