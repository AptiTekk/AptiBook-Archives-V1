package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.ReservableTypeService;
import com.AptiTekk.Agenda.core.entity.ReservableType;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;

@ManagedBean
@ViewScoped
public class ReservablesEditController {

    @Inject
    private ReservableTypeService reservableTypeService;

    private List<ReservableType> reservableTypes;
    private ReservableType selectedReservableType;

    private String editableReservableTypeName;

    @PostConstruct
    public void init() {
        refreshReservableTypeList();
        resetSettings();
    }

    private void refreshReservableTypeList() {
        reservableTypes = reservableTypeService.getAll();
    }

    public void updateSettings() {
        if (getSelectedReservableType() != null) {
            if (getEditableReservableTypeName().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Reservable Type's name cannot be empty!"));
            } else if (!getEditableReservableTypeName().matches("[A-Za-z0-9 ]+")) {
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Reservable Type's name may only contain A-Z, a-z, 0-9, and spaces!"));
            } else if (reservableTypeService.findByName(getEditableReservableTypeName()) != null) {
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Reservable Type with that name already exists!"));
            }

            if(FacesContext.getCurrentInstance().getMessageList("reservableTypeEditForm").isEmpty())
            {
                getSelectedReservableType().setName(getEditableReservableTypeName());
                setSelectedReservableType(reservableTypeService.merge(getSelectedReservableType()));
                refreshReservableTypeList();
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Reservable Type Updated"));
            }
        }
    }

    public void resetSettings() {
        if (getSelectedReservableType() != null) {
            setEditableReservableTypeName(getSelectedReservableType().getName());
        }
    }

    public void addNewReservableType() {
        ReservableType reservableType = new ReservableType("New Reservable Type");
        reservableTypeService.insert(reservableType);
        refreshReservableTypeList();
    }

    public void deleteSelectedReservableType() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (reservableTypeService.get(getSelectedReservableType().getId()) != null) {
                context.addMessage("reservableTypeEditForm", new FacesMessage("Successful", "Reservable Type Deleted!"));
                reservableTypeService.delete(getSelectedReservableType().getId());
                setSelectedReservableType(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("reservableTypeEditForm", new FacesMessage("Failure", "Error While Deleting Reservable Type!"));
        }

        refreshReservableTypeList();
    }

    public List<ReservableType> getReservableTypes() {
        return reservableTypes;
    }

    public ReservableType getSelectedReservableType() {
        return selectedReservableType;
    }

    public void setSelectedReservableType(ReservableType selectedReservableType) {
        this.selectedReservableType = selectedReservableType;
        resetSettings();
    }

    public String getEditableReservableTypeName() {
        return editableReservableTypeName;
    }

    public void setEditableReservableTypeName(String editableReservableTypeName) {
        this.editableReservableTypeName = editableReservableTypeName;
    }
}
