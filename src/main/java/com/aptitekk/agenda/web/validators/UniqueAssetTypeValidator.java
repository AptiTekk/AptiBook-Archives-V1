/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.validators;

import com.aptitekk.agenda.core.entities.AssetType;
import com.aptitekk.agenda.core.services.AssetTypeService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class UniqueAssetTypeValidator implements Validator, Serializable {

    @Inject
    AssetTypeService assetTypeService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object exemptionAttribute = uiComponent.getAttributes().get("exemption");

        if (inputText != null && inputText instanceof String && assetTypeService != null) {
            AssetType otherAssetType = assetTypeService.findByName((String) inputText);
            if (otherAssetType != null) {
                if (exemptionAttribute != null && exemptionAttribute instanceof AssetType && otherAssetType.equals(exemptionAttribute))
                    return;
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Asset Type with this name already exists."));
            }
        }
    }

}
