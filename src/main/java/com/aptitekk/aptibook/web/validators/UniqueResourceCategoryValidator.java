/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.validators;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;

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
public class UniqueResourceCategoryValidator implements Validator, Serializable {

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object exceptionAttribute = uiComponent.getAttributes().get("exception");

        if (inputText != null && inputText instanceof String && resourceCategoryService != null) {
            ResourceCategory otherResourceCategory = resourceCategoryService.findByName((String) inputText);
            if (otherResourceCategory != null) {
                if (exceptionAttribute != null && exceptionAttribute instanceof ResourceCategory && otherResourceCategory.equals(exceptionAttribute))
                    return;
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Resource Category with this name already exists."));
            }
        }
    }

}
