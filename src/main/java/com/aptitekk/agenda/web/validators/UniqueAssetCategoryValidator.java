/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.validators;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.services.AssetCategoryService;

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
public class UniqueAssetCategoryValidator implements Validator, Serializable {

    @Inject
    AssetCategoryService assetCategoryService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object exemptionAttribute = uiComponent.getAttributes().get("exemption");

        if (inputText != null && inputText instanceof String && assetCategoryService != null) {
            AssetCategory otherAssetCategory = assetCategoryService.findByName((String) inputText);
            if (otherAssetCategory != null) {
                if (exemptionAttribute != null && exemptionAttribute instanceof AssetCategory && otherAssetCategory.equals(exemptionAttribute))
                    return;
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Asset Category with this name already exists."));
            }
        }
    }

}
