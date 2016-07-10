/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.validators;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates an uploaded Part file to make sure that it is a JPEG/PNG, and is less than 5MB in size.
 */
@Named
@RequestScoped
public class ImageUploadValidator implements Validator, Serializable {

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object object) throws ValidatorException {
        if (object != null && object instanceof Part) {
            Part part = (Part) object;

            //A list to store messages for the user (in the case of validation failures)
            List<FacesMessage> messageList = new ArrayList<>();

            //Make sure it is less than 5MB in size.
            if (part.getSize() > 5000000)
                messageList.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This file is too large. Please select a file under 5MB in size."));

            //Make sure it is a JPEG or PNG file.
            if (!part.getContentType().equalsIgnoreCase("image/jpeg") && !part.getContentType().equalsIgnoreCase("image/png"))
                messageList.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Only JPEG and PNG files are accepted."));
            else if (messageList.isEmpty()) { //Only continue if the file is less than the size limit, so we don't try to parse a 100MB file, for example.
                try {
                    //Attempt to read the file as an image. This will fail or return null if it is not a true image.
                    if (ImageIO.read(part.getInputStream()) == null)
                        throw new Exception();
                } catch (Exception e) { //Not a true image. Sneaky! But no cigar.
                    messageList.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This image is corrupted or otherwise cannot be used. Please choose another image."));
                }
            }

            //Throw exception if there were validation failures.
            if (!messageList.isEmpty())
                throw new ValidatorException(messageList);
        }
    }

}
