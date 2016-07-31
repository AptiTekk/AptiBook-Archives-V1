/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assetCategories;

import com.aptitekk.agenda.core.entities.ReservationField;
import com.aptitekk.agenda.core.entities.services.ReservationFieldService;
import com.aptitekk.agenda.core.util.LogManager;

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
    private AssetCategoryEditController assetCategoryEditController;

    @Inject
    private ReservationFieldService reservationFieldService;

    private ReservationField reservationField;

    public void saveChanges() {
        if (reservationField != null) {
            reservationField.setTitle(title);
            reservationField.setDescription(description);
            reservationField.setMultiLine(size.equals(sizes[1]));
            try {
                reservationFieldService.merge(reservationField);
                assetCategoryEditController.refreshAssetCategories();
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field '" + reservationField.getTitle() + "' Updated!"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Internal Server Error while updating Field."));
                LogManager.logError("Error while updating Reservation Field " + reservationField.getTitle() + " of " + assetCategoryEditController.getSelectedAssetCategory().getName() + " Asset Category: " + e.getMessage());
            }

            reservationField = null;
        }
    }

    public void delete() {
        if (reservationField != null) {
            try {
                String reservationTitle = reservationField.getTitle();
                reservationFieldService.delete(reservationField.getId());
                assetCategoryEditController.refreshAssetCategories();
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field '" + reservationTitle + "' Deleted!"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Internal Server Error while deleting Field."));
                LogManager.logError("Error while deleting Reservation Field " + reservationField.getTitle() + " of " + assetCategoryEditController.getSelectedAssetCategory().getName() + " Asset Category: " + e.getMessage());
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
        size = reservationField.isMultiLine() ? sizes[1] : sizes[0];
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getSizes() {
        return sizes;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
