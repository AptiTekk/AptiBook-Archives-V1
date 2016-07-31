/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assetCategories;

import com.aptitekk.agenda.core.entities.ReservationField;
import com.aptitekk.agenda.core.entities.services.ReservationFieldService;
import com.aptitekk.agenda.core.util.LogManager;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Named
@RequestScoped
public class NewReservationFieldController implements Serializable {

    @Inject
    private AssetCategoryEditController assetCategoryEditController;

    @Inject
    private ReservationFieldService reservationFieldService;

    /**
     * Title of the Reservation Field.
     */
    @Size(max = 64, message = "This may only be 64 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String title;

    /**
     * Description of the Reservation Field.
     */
    @Size(max = 256, message = "This may only be 256 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String description;

    /**
     * Values for the size selection combo box.
     */
    private final String[] sizes = {"Single-Line", "Multi-Line"};

    /**
     * Chosen size from the combo box.
     */
    private String size;

    public void createReservationField() {
        if (assetCategoryEditController.getSelectedAssetCategory() != null) {
            ReservationField reservationField = new ReservationField();
            reservationField.setAssetCategory(assetCategoryEditController.getSelectedAssetCategory());
            reservationField.setTitle(title);
            reservationField.setDescription(description);
            reservationField.setMultiLine(size.equals(sizes[1]));

            try {
                reservationFieldService.insert(reservationField);
                LogManager.logInfo("New Reservation Field persisted. Id and Title: " + reservationField.getId() + ", " + reservationField.getTitle());

                assetCategoryEditController.refreshAssetCategories();
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field '" + reservationField.getTitle() + "' Added!"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Internal Server Error while adding new Field."));
                LogManager.logError("Error while adding new Reservation Field to " + assetCategoryEditController.getSelectedAssetCategory().getName() + " Asset Category: " + e.getMessage());
            }
        }
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
