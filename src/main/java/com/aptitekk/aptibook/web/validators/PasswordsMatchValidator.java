/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.validators;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class PasswordsMatchValidator implements Validator, Serializable {

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputSecret) throws ValidatorException {
        Object confirmationFieldAttribute = uiComponent.getAttributes().get("confirmationField");
        if (inputSecret != null && inputSecret instanceof String && confirmationFieldAttribute != null && confirmationFieldAttribute instanceof UIInput) {
            if (!inputSecret.equals(((UIInput) confirmationFieldAttribute).getSubmittedValue()))
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Passwords do not match!"));
        } else
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: Not all attributes supplied. " + (inputSecret == null) + " ... " + (confirmationFieldAttribute == null)));
    }

}
