/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util.propertyTypes;

import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.RegexPropertyType;
import net.bootsfaces.component.inputText.InputText;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

public class SingleLineField extends RegexPropertyType {

    public SingleLineField(String label, int maxLength, String pattern, String validationMessage) {
        super(label, maxLength, pattern, validationMessage);
    }

}
