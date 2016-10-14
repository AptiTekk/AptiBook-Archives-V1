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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class EditReservationFieldController extends ReservationFieldFieldSupplier implements Serializable {

    @Inject
    private EditResourceCategoryController editResourceCategoryController;

    @Inject
    private ReservationFieldService reservationFieldService;

    private ReservationField reservationField;

    public void saveChanges() {
        if (reservationField != null) {
            reservationField.setTitle(title);
            reservationField.setDescription(description);
            reservationField.setMultiLine(size.equals(sizes[1]));
            reservationField.setRequired(required);
            try {
                reservationFieldService.merge(reservationField);
                editResourceCategoryController.refreshResourceCategories();
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field '" + reservationField.getTitle() + "' Updated!"));
            } catch (Exception e) {
                LogManager.logException(getClass(), "Error while updating Reservation Field", e);
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
            }

            reservationField = null;
        }
    }

    public void delete() {
        if (reservationField != null) {
            try {
                String reservationTitle = reservationField.getTitle();
                reservationFieldService.delete(reservationField.getId());
                editResourceCategoryController.refreshResourceCategories();
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field '" + reservationTitle + "' Deleted!"));
            } catch (Exception e) {
                LogManager.logException(getClass(), "Error while deleting Reservation Field", e);
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
            }
        }
    }

    public ReservationField getReservationField() {
        return reservationField;
    }

    public void setReservationField(ReservationField reservationField) {
        this.reservationField = reservationField;

        title = reservationField.getTitle();
        description = reservationField.getDescription();
        required = reservationField.isRequired();
        size = reservationField.isMultiLine() ? sizes[1] : sizes[0];
    }
}
