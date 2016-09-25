/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement;

import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.settings.SettingsSessionController;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named
@SessionScoped
public class ReservationManagementSessionController implements Serializable {

    @SuppressWarnings("WeakerAccess")
    public enum ManagementPage {
        PENDING("Pending", "pending", "hourglass-half", null),
        APPROVED("Approved", "approved", "calendar-check-o", null),
        REJECTED("Rejected", "rejected", "calendar-times-o", null),
        CALENDAR("My Asset Calendar", "calendar", "calendar", null);

        private String name;
        private String fileName;
        private String iconAwesomeName;
        private SettingsSessionController.SettingsPage parent;

        ManagementPage(String name, String fileName, String iconAwesomeName, SettingsSessionController.SettingsPage parent) {
            this.name = name;
            this.fileName = fileName;
            this.iconAwesomeName = iconAwesomeName;
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public String getFileName() {
            return fileName;
        }

        public String getIconAwesomeName() {
            return iconAwesomeName;
        }

        public SettingsSessionController.SettingsPage getParent() {
            return parent;
        }
    }

    @Inject
    private AuthenticationController authenticationController;

    private List<ManagementPage> pages;
    private ManagementPage currentPage = null;

    void checkPagesValidity() {
        if ((pages == null || pages.isEmpty()) && authenticationController.getAuthenticatedUser() != null) {
            if (!authenticationController.getAuthenticatedUser().getUserGroups().isEmpty())
                pages = new ArrayList<>(Arrays.asList(ManagementPage.values()));
            else
                pages = null;
        }
    }

    String redirectIfPageIsNull() {
        if (getCurrentPage() == null) {
            if (pages == null || pages.isEmpty()) {
                return "index";
            }
            return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true&managementPage=" + pages.get(0).getName().replaceAll(" ", "+");
        }
        return null;
    }

    List<ManagementPage> getPages() {
        return pages;
    }

    ManagementPage getCurrentPage() {
        return currentPage;
    }

    void setCurrentPage(ManagementPage page) {
        this.currentPage = page;
    }

}
