/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservationManagement;

import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ReservationManagementViewController implements Serializable {

    @Inject
    ReservationManagementSessionController reservationManagementSessionController;

    @Inject
    private AuthenticationController authenticationController;

    @PostConstruct
    private void init() {
        reservationManagementSessionController.checkPagesValidity();

        if (reservationManagementSessionController.getPages() != null) {
            String managementPage = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("managementPage");
            if (managementPage != null && !managementPage.isEmpty()) {
                for (ReservationManagementSessionController.ManagementPage page : reservationManagementSessionController.getPages()) {
                    if (page.getName().equalsIgnoreCase(managementPage)) {
                        if (authenticationController.getAuthenticatedUser().getUserGroups().isEmpty()) {
                            authenticationController.forceUserRedirect();
                            return;
                        }
                        
                        reservationManagementSessionController.setCurrentPage(page);
                        break;
                    }
                }
            } else {
                reservationManagementSessionController.setCurrentPage(null);
            }
        }
    }

    public List<ReservationManagementSessionController.ManagementPage> getPages() {
        return reservationManagementSessionController.getPages();
    }

    public ReservationManagementSessionController.ManagementPage getCurrentPage() {
        return reservationManagementSessionController.getCurrentPage();
    }

    public void setCurrentPage(ReservationManagementSessionController.ManagementPage page) {
        reservationManagementSessionController.setCurrentPage(page);
    }

    public String redirectIfPageIsNull() {
        return reservationManagementSessionController.redirectIfPageIsNull();
    }

}
