/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util.propertyTypes;

import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.RegexPropertyType;
import net.bootsfaces.component.inputText.InputText;

import javax.faces.component.html.HtmlPanelGroup;

public class SingleLineField extends RegexPropertyType {

    public SingleLineField(String label, int maxLength, String pattern, String validationMessage) {
        super(label, maxLength, pattern, validationMessage);
    }

    @Override
    public void injectIntoGroup(HtmlPanelGroup formGroup) {
        InputText inputText = new InputText();
        inputText.setId(getId());
        inputText.setLabel(getLabel());
        inputText.setMaxlength(getMaxLength());
        inputText.setValue(getValue());
        formGroup.getChildren().add(inputText);
    }
}
