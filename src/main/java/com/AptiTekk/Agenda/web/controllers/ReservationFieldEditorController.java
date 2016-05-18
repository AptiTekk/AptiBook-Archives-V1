package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.ReservableService;
import com.AptiTekk.Agenda.core.ReservableTypeService;
import com.AptiTekk.Agenda.core.ReservationFieldService;
import com.AptiTekk.Agenda.core.entity.ReservationField;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

    @Inject
    ReservableService reservableService;

    @Inject
    ReservationFieldService reservationFieldService;

    List<ReservationField> fields;

    @PostConstruct
    public void init() {
        resetSettings();
    }

    public void updateSettings() {
        fields.forEach(field -> {
            reservationFieldService.merge(field);
        });
    }

    public void resetSettings() {
        fields = reservationFieldService.getAll();
    }

    public List<ReservationField> getFields() {
        return fields;
    }

    public void setFields(List<ReservationField> fields) {
        this.fields = fields;
    }
}
