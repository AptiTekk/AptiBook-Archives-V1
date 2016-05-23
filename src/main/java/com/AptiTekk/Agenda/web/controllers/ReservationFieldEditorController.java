package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.ReservableTypeService;
import com.AptiTekk.Agenda.core.ReservationFieldService;
import com.AptiTekk.Agenda.core.entity.ReservableType;
import com.AptiTekk.Agenda.core.entity.ReservationField;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by kevint on 5/18/2016.
 */
@ManagedBean(name = "ReservationFieldEditorController")
@ViewScoped
public class ReservationFieldEditorController {

    @Inject
    ReservableTypeService typeService;

    List<ReservableType> reservableTypes;

    ReservableType type;

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
            reservationFieldService.merge(field);
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
        fields.remove(field);
        reservationFieldService.delete(field.getId());
        FacesContext.getCurrentInstance().addMessage("pageMessages",
                new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Field Deleted"));
    }

    public void resetSettings() {
        fields = reservationFieldService.getByType(type);
        reservableTypes = typeService.getAll();
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

    public List<ReservableType> getReservableTypes() {
        return reservableTypes;
    }

    public void setReservableTypes(List<ReservableType> reservableTypes) {
        this.reservableTypes = reservableTypes;
    }

    public ReservableType getType() {
        return type;
    }

    public void setType(ReservableType type) {
        this.type = type;
    }
}
