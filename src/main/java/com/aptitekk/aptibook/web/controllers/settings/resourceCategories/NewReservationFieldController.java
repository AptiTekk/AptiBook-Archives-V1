/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resourceCategories;

import com.aptitekk.aptibook.core.domain.entities.ReservationField;
import com.aptitekk.aptibook.core.domain.services.ReservationFieldService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class NewReservationFieldController extends ReservationFieldFieldSupplier implements Serializable {

    @Inject
    private EditResourceCategoryController editResourceCategoryController;

    @Inject
    private ReservationFieldService reservationFieldService;

    public void createReservationField() {
        if (editResourceCategoryController.getSelectedResourceCategory() != null) {
            ReservationField reservationField = new ReservationField();
            reservationField.setResourceCategory(editResourceCategoryController.getSelectedResourceCategory());
            reservationField.setTitle(title);
            reservationField.setDescription(description);
            reservationField.setRequired(required);
            reservationField.setMultiLine(size.equals(sizes[1]));

            try {
                reservationFieldService.insert(reservationField);
                editResourceCategoryController.refreshResourceCategories();
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field '" + reservationField.getTitle() + "' Added!"));
            } catch (Exception e) {
                LogManager.logException(getClass(), "Error while adding new Reservation Field", e);
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
            }
        }
    }

}
