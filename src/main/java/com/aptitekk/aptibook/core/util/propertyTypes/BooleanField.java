/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util.propertyTypes;

import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.BooleanPropertyType;
import net.bootsfaces.component.switchComponent.Switch;

import javax.faces.component.html.HtmlPanelGroup;

public class BooleanField extends BooleanPropertyType {

    private final String yesLabel;
    private final String noLabel;

    public BooleanField(String label) {
        super(label);
        this.yesLabel = null;
        this.noLabel = null;
    }

    public BooleanField(String label, String yesLabel, String noLabel) {
        super(label);
        this.yesLabel = yesLabel;
        this.noLabel = noLabel;
    }

    @Override
    public void injectIntoGroup(HtmlPanelGroup formGroup) {
        Switch switchComponent = new Switch();
        switchComponent.setId(getId());
        switchComponent.setLabel(getLabel());
        switchComponent.setValue(getValue());
        if (yesLabel != null)
            switchComponent.setOnText(yesLabel);
        else
            switchComponent.setOnText("Yes");

        if (noLabel != null)
            switchComponent.setOffText(noLabel);
        else
            switchComponent.setOffText("No");

        formGroup.getChildren().add(switchComponent);
    }
}
