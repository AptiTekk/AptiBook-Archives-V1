/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.validators;

import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.entity.User;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

@ManagedBean(name = "UniqueUserValidator")
@RequestScoped
public class UniqueUserValidator implements Validator {

    @Inject
    UserService userService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object exemptionAttribute = uiComponent.getAttributes().get("exemption");

        if (inputText != null && inputText instanceof String && userService != null) {
            User otherUser = userService.findByName((String) inputText);
            if (otherUser != null) {
                if (exemptionAttribute != null && exemptionAttribute instanceof User && otherUser.equals(exemptionAttribute))
                    return;
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A User with this name already exists."));
            }
        }
    }

}
