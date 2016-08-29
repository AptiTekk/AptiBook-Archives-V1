/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.assetCategories;

import com.aptitekk.aptibook.core.entities.ReservationField;
import com.aptitekk.aptibook.core.entities.services.ReservationFieldService;
import com.aptitekk.aptibook.core.util.LogManager;

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
    private EditAssetCategoryController editAssetCategoryController;

    @Inject
    private ReservationFieldService reservationFieldService;

    public void createReservationField() {
        if (editAssetCategoryController.getSelectedAssetCategory() != null) {
            ReservationField reservationField = new ReservationField();
            reservationField.setAssetCategory(editAssetCategoryController.getSelectedAssetCategory());
            reservationField.setTitle(title);
            reservationField.setDescription(description);
            reservationField.setRequired(required);
            reservationField.setMultiLine(size.equals(sizes[1]));

            try {
                reservationFieldService.insert(reservationField);
                LogManager.logInfo("New Reservation Field persisted. Id and Title: " + reservationField.getId() + ", " + reservationField.getTitle());

                editAssetCategoryController.refreshAssetCategories();
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field '" + reservationField.getTitle() + "' Added!"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Internal Server Error while adding new Field."));
                LogManager.logError("Error while adding new Reservation Field to " + editAssetCategoryController.getSelectedAssetCategory().getName() + " Asset Category: " + e.getMessage());
            }
        }
    }

}
