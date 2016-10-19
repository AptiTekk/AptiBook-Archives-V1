/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement;

import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;

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
    ReservationManagementPagesController reservationManagementPagesController;

    @Inject
    private AuthenticationController authenticationController;

    @PostConstruct
    private void init() {
        reservationManagementPagesController.checkPagesValidity();

        if (reservationManagementPagesController.getPages() != null) {
            String managementPage = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("managementPage");
            if (managementPage != null && !managementPage.isEmpty()) {
                for (ReservationManagementPagesController.ManagementPage page : reservationManagementPagesController.getPages()) {
                    if (page.getName().equalsIgnoreCase(managementPage)) {
                        if (authenticationController.getAuthenticatedUser().getUserGroups().isEmpty()) {
                            authenticationController.invokeUserRedirect();
                            return;
                        }
                        reservationManagementPagesController.setCurrentPage(page);
                        break;
                    }
                }
            } else {
                reservationManagementPagesController.setCurrentPage(null);
            }
        }
    }

    public List<ReservationManagementPagesController.ManagementPage> getPages() {
        return reservationManagementPagesController.getPages();
    }

    public ReservationManagementPagesController.ManagementPage getCurrentPage() {
        return reservationManagementPagesController.getCurrentPage();
    }

    public void setCurrentPage(ReservationManagementPagesController.ManagementPage page) {
        reservationManagementPagesController.setCurrentPage(page);
    }

    public String redirectIfPageIsNull() {
        return reservationManagementPagesController.redirectIfPageIsNull();
    }

}
