/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings;

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
    private SettingsPagesController settingsPagesController;

    @PostConstruct
    private void init() {
        settingsPagesController.checkPagesValidity();

        if (settingsPagesController.getPages() != null) {
            String settingsPage = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("settingsPage");
            if (settingsPage != null && !settingsPage.isEmpty()) {
                for (SettingsPagesController.SettingsPage page : settingsPagesController.getPages()) {
                    if (page.getName().equalsIgnoreCase(settingsPage)) {
                        settingsPagesController.setCurrentPage(page);
                        break;
                    }
                }
            } else {
                settingsPagesController.setCurrentPage(null);
            }
        }
    }

    public List<SettingsPagesController.SettingsPage> getPages() {
        return settingsPagesController.getPages();
    }

    public SettingsPagesController.SettingsPage getCurrentPage() {
        return settingsPagesController.getCurrentPage();
    }

    public void setCurrentPage(SettingsPagesController.SettingsPage page) {
        settingsPagesController.setCurrentPage(page);
    }

    public String redirectIfPageIsNull() {
        return settingsPagesController.redirectIfPageIsNull();
    }

}
