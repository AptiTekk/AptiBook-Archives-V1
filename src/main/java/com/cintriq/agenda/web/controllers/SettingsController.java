package com.cintriq.agenda.web.controllers;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "SettingsController")
@ViewScoped
public class SettingsController {

    public enum SettingsPage {
        PROPERTIES("Properties", "properties.xhtml"), USERS("Users", "users.xhtml"), GROUPS("Groups",
                "groups.xhtml"), ASSETS("Assets", "assets.xhtml"),
        RESERVATIONFIELDEDITOR("Fields Editor", "reservationfieldeditor.xhtml");

        private String name;
        private String fileName;

        SettingsPage(String name, String fileName) {
            this.name = name;
            this.fileName = fileName;
        }

        public String getName() {
            return name;
        }

        public String getFileName() {
            return fileName;
        }
    }

    private List<SettingsPage> pages = Arrays.asList(SettingsPage.values());
    private SettingsPage currentPage = SettingsPage.values()[0];

    @PostConstruct
    public void init() {
        String tab = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tab");
        if (tab != null && !tab.isEmpty()) {
            for (SettingsPage page : SettingsPage.values())
                if (page.name.equalsIgnoreCase(tab))
                    setCurrentPage(page);
        }

        if (getCurrentPage() == null)
            setCurrentPage(SettingsPage.values()[0]);
    }

    public List<SettingsPage> getPages() {
        return pages;
    }

    public SettingsPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(SettingsPage settingsPage) {
        this.currentPage = settingsPage;
    }

}
