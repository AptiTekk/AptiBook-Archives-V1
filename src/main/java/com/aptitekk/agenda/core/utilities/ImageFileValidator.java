/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.utilities;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.List;

@FacesValidator
public class ImageFileValidator implements Validator {

    public static final String[] VALID_IMAGE_TYPES = {"image/png", "image/jpeg", "image/gif"};

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        LogManager.logDebug("Validating Image Upload...");
        List<FacesMessage> msgs = new ArrayList<FacesMessage>();
        Part file = (Part) value;
        if (file == null) {
            LogManager.logDebug("No image was uploaded.");
            return;
        }
        if (file.getSize() > 1024 * 1024 * 2) {
            LogManager.logDebug("Image is too large. Maximum size is 2MB.");
            msgs.add(new FacesMessage("Image is too large. Maximum size is 2MB."));
        }
        boolean valid = false;
        for (String validType : VALID_IMAGE_TYPES) {
            if (validType.equals(file.getContentType())) {
                valid = true;
            }
        }
        if (!valid) {
            LogManager.logDebug("Invalid type. Use PNG, JPG, or GIF.");
            msgs.add(new FacesMessage("Invalid type. Use PNG, JPG, or GIF."));
        }
        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        } else {
            LogManager.logDebug("Image is valid.");
        }
    }

}
