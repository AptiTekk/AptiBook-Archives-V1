/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservations;

import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.ReservationField;
import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.services.ReservationFieldService;
import com.aptitekk.agenda.core.utilities.LogManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ReservationFieldEditorController implements Serializable {

    @Inject
    AssetTypeService assetTypeService;

    @Inject
    AssetService assetService;
    List<AssetType> assetTypes;

    AssetType type;

    @Inject
    ReservationFieldService reservationFieldService;

    List<ReservationField> fields = new ArrayList<>();

    ReservationField field;

    @PostConstruct
    public void init() {
        refreshSettings();
    }

    public void updateSettings() {
        LogManager.logDebug("Saving " + fields.size() + " fields");
        fields.forEach(field -> {
            try {
                reservationFieldService.merge(field);
                LogManager.logDebug("Saved " + field.getName());
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("pageMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }
        });
        FacesContext.getCurrentInstance().addMessage("pageMessages",
                new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Saved Changes"));


    }

    public void addField() {
        LogManager.logDebug("Adding new field");
        ReservationField field = new ReservationField();
        field.setName("New Field");
        field.setDescription("Fields need a description so people can add data to their reservation");
        fields.add(field);
    }

    public void deleteField() {
        try {
            fields.remove(field);
            reservationFieldService.delete(field.getId());
            FacesContext.getCurrentInstance().addMessage("pageMessages",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field Deleted"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("pageMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
        }
    }

    public void refreshSettings() {
        System.out.println("refreshed for type " + ((type == null) ? "Global" : type.getName()));
        fields = reservationFieldService.getByType(type);
        assetTypes = assetTypeService.getAll();
    }

    public List<ReservationField> getFields() {
        return fields;
    }

    public void setFields(List<ReservationField> fields) {
        this.fields = fields;
    }

    public ReservationField getField() {
        return field;
    }

    public void setField(ReservationField field) {
        this.field = field;
    }

    public List<AssetType> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AssetType> assetTypes) {
        this.assetTypes = assetTypes;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
        refreshSettings();
    }
}
