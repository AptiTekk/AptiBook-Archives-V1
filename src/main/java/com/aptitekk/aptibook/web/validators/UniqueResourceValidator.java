/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.validators;

import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.services.ResourceService;

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
public class UniqueResourceValidator implements Validator, Serializable {

    @Inject
    private ResourceService resourceService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object resourceCategory = uiComponent.getAttributes().get("resourceCategory");
        Object exceptionAttribute = uiComponent.getAttributes().get("exception");

        if (inputText != null && inputText instanceof String && resourceCategory != null && resourceCategory instanceof ResourceCategory) {
            Resource otherResource = resourceService.findByName((String) inputText, (ResourceCategory) resourceCategory);
            if (otherResource != null) {
                if (exceptionAttribute != null && exceptionAttribute instanceof Resource && otherResource.equals(exceptionAttribute))
                    return;
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Resource with this name already exists."));
            }
        }
    }

}
