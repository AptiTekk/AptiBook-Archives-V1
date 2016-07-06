/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.validators;

import com.aptitekk.agenda.core.services.UserGroupService;
import com.aptitekk.agenda.core.entity.UserGroup;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

@ManagedBean(name = "UniqueUserGroupValidator")
@RequestScoped
public class UniqueUserGroupValidator implements Validator {

    @Inject
    UserGroupService userGroupService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object exemptionAttribute = uiComponent.getAttributes().get("exemption");

        if (inputText != null && inputText instanceof String && userGroupService != null) {
            UserGroup otherUserGroup = userGroupService.findByName((String) inputText);
            if (otherUserGroup != null) {
                if (exemptionAttribute != null && exemptionAttribute instanceof UserGroup && otherUserGroup.equals(exemptionAttribute))
                    return;
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A User Group with this name already exists."));
            }
        }
    }

}
