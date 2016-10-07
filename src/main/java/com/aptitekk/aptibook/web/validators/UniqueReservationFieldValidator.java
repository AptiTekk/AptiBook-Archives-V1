/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.validators;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.ReservationField;
import com.aptitekk.aptibook.core.domain.services.ReservationFieldService;
import com.aptitekk.aptibook.core.util.LogManager;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class UniqueReservationFieldValidator implements Validator, Serializable {

    @Inject
    private ReservationFieldService reservationFieldService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object assetCategory = uiComponent.getAttributes().get("assetCategory");
        Object exceptionAttribute = uiComponent.getAttributes().get("exception");

        if (inputText != null && inputText instanceof String && assetCategory != null && assetCategory instanceof AssetCategory) {
            List<ReservationField> otherReservationFields = reservationFieldService.findByTitle((String) inputText, (AssetCategory) assetCategory);
            if (otherReservationFields != null) {
                if (otherReservationFields.isEmpty())
                    return;
                if (otherReservationFields.size() > 1 || exceptionAttribute == null || !otherReservationFields.get(0).equals(exceptionAttribute))
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Reservation Field with this name already exists."));
            }
        } else {
            LogManager.logError("[UniqueReservationFieldValidator] Unable to Validate");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Internal Server Error during validation."));
        }
    }

}
