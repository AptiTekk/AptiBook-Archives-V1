package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.ReservableService;
import com.AptiTekk.Agenda.core.ReservableTypeService;
import com.AptiTekk.Agenda.core.entity.Reservable;
import com.AptiTekk.Agenda.core.entity.ReservableType;
import org.primefaces.event.TabChangeEvent;

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

    @Inject
    private ReservableService reservableService;

    private List<ReservableType> reservableTypes;
    private ReservableType selectedReservableType;
    private Reservable selectedTabReservable;

    private String editableReservableTypeName;

    private String editableTabReservableName;

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
            ReservableType reservableType = reservableTypeService.findByName(getEditableReservableTypeName());
            if (reservableType != null && !reservableType.equals(getSelectedReservableType()))
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Reservable Type with that name already exists!"));
            else if (getEditableReservableTypeName().isEmpty())
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Reservable Type's name cannot be empty!"));
            else if (!getEditableReservableTypeName().matches("[A-Za-z0-9 #]+"))
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Reservable Type's name may only contain A-Z, a-z, 0-9, #, and spaces!"));

            if (FacesContext.getCurrentInstance().getMessageList("reservableTypeEditForm").isEmpty()) {
                getSelectedReservableType().setName(getEditableReservableTypeName());
                setSelectedReservableType(reservableTypeService.merge(getSelectedReservableType()));
                refreshReservableTypeList();
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Reservable Type Updated"));
            }
        }
    }

    public void resetSettings() {
        if (getSelectedReservableType() != null)
            setEditableReservableTypeName(getSelectedReservableType().getName());
        else
            setEditableReservableTypeName("");
    }

    public void updateTabReservableSettings()
    {
        if(getSelectedTabReservable() != null)
        {
            Reservable reservable = reservableService.findByName(getEditableTabReservableName());
            if (reservable != null && !reservable.equals(getSelectedTabReservable()))
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Reservable with that name already exists!"));
            else if (getEditableTabReservableName().isEmpty())
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Reservable's name cannot be empty!"));
            else if (!getEditableTabReservableName().matches("[A-Za-z0-9 #]+"))
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Reservable's name may only contain A-Z, a-z, 0-9, #, and spaces!"));

            if (FacesContext.getCurrentInstance().getMessageList("reservableTypeEditForm").isEmpty()) {
                getSelectedTabReservable().setName(getEditableTabReservableName());
                setSelectedTabReservable(reservableService.merge(getSelectedTabReservable()));
                refreshReservableTypeList();
                FacesContext.getCurrentInstance().addMessage("reservableTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Reservable Updated"));
            }
        }
    }

    public void resetTabReservableSettings() {
        if(getSelectedTabReservable() != null)
            setEditableTabReservableName(getSelectedTabReservable().getName());
        else
            setEditableTabReservableName("");
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

    public void onReservableTabChange(TabChangeEvent event) {
        if (event.getData() instanceof Reservable)
            setSelectedTabReservable((Reservable) event.getData());
    }

    public void deleteSelectedTabReservable() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (reservableService.get(getSelectedTabReservable().getId()) != null) {
                context.addMessage("reservableTypeEditForm", new FacesMessage("Successful", "Reservable Deleted!"));
                reservableService.delete(getSelectedTabReservable().getId());
                setSelectedReservableType(reservableTypeService.get(getSelectedReservableType().getId()));
            } else {
                throw new Exception("Reservable not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("reservableTypeEditForm", new FacesMessage("Failure", "Error While Deleting Reservable!"));
        }

        refreshReservableTypeList();
    }

    public void addNewReservable() {
        if (getSelectedReservableType() != null) {
            Reservable reservable = new Reservable("New Reservable");
            reservable.setType(getSelectedReservableType());
            reservableService.insert(reservable);

            setSelectedReservableType(reservableTypeService.get(getSelectedReservableType().getId()));

            refreshReservableTypeList();
        }
    }

    public List<ReservableType> getReservableTypes() {
        return reservableTypes;
    }

    public ReservableType getSelectedReservableType() {
        return selectedReservableType;
    }

    public Reservable getSelectedTabReservable() {
        return selectedTabReservable;
    }

    public void setSelectedTabReservable(Reservable selectedTabReservable) {
        this.selectedTabReservable = selectedTabReservable;
        resetTabReservableSettings();
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

    public String getEditableTabReservableName() {
        return editableTabReservableName;
    }

    public void setEditableTabReservableName(String editableTabReservableName) {
        this.editableTabReservableName = editableTabReservableName;
    }
}
