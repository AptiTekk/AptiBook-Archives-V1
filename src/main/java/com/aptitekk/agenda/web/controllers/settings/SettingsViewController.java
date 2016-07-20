/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class SettingsViewController implements Serializable {

    @Inject
    SettingsSessionController settingsSessionController;

    @PostConstruct
    private void init() {
        settingsSessionController.checkPagesValidity();

        if (settingsSessionController.getPages() != null) {
            String tab = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tab");
            if (tab != null && !tab.isEmpty()) {
                for (SettingsSessionController.SettingsPage page : settingsSessionController.getPages()) {
                    if (page.getName().equalsIgnoreCase(tab)) {
                        settingsSessionController.setCurrentPage(page);
                        break;
                    }
                }
            } else {
                settingsSessionController.setCurrentPage(null);
            }
        }
    }

    public List<SettingsSessionController.SettingsPage> getPages() {
        return settingsSessionController.getPages();
    }

    public SettingsSessionController.SettingsPage getCurrentPage() {
        return settingsSessionController.getCurrentPage();
    }

    public void setCurrentPage(SettingsSessionController.SettingsPage page) {
        settingsSessionController.setCurrentPage(page);
    }

    public String redirectIfPageIsNull() {
        return settingsSessionController.redirectIfPageIsNull();
    }

}
