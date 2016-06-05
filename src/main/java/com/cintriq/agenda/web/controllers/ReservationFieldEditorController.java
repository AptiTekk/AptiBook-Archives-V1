package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.AssetService;
import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.ReservationFieldService;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.ReservationField;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;

@ManagedBean(name = "ReservationFieldEditorController")
@ViewScoped
public class ReservationFieldEditorController {

    @Inject
    AssetTypeService assetTypeService;

    @Inject
    AssetService assetService;
    List<AssetType> reservableTypes;

    AssetType type;

    @Inject
    ReservationFieldService reservationFieldService;

    List<ReservationField> fields;

    ReservationField field;

    @PostConstruct
    public void init() {
        resetSettings();
    }

    public void updateSettings() {

        fields.forEach(field -> {
            try {
                reservationFieldService.merge(field);
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("pageMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }
        });
        FacesContext.getCurrentInstance().addMessage("pageMessages",
                new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Saved Changes"));


    }

    public void addField() {
        ReservationField field = new ReservationField();
        field.setName("New Field");
        field.setDescription("Field needs a description so they know what to add.");
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

    public void resetSettings() {
        fields = reservationFieldService.getByType(type);
        reservableTypes = assetTypeService.getAll();
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

    public List<AssetType> getReservableTypes() {
        return reservableTypes;
    }

    public void setReservableTypes(List<AssetType> reservableTypes) {
        this.reservableTypes = reservableTypes;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }
}
