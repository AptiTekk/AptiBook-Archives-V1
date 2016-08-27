/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util.propertyTypes;

import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.RegexPropertyType;
import net.bootsfaces.component.inputSecret.InputSecret;

import javax.faces.component.html.HtmlPanelGroup;

public class PasswordField extends RegexPropertyType {

    public PasswordField(String label, int maxLength, String pattern, String validationMessage) {
        super(label, maxLength, pattern, validationMessage);
    }

    @Override
    public void injectIntoGroup(HtmlPanelGroup formGroup) {
        InputSecret inputSecret = new InputSecret();
        inputSecret.setId(getId());
        inputSecret.setLabel(getLabel());
        inputSecret.setMaxlength(getMaxLength());
        inputSecret.setValue(getValue());
    }
}
