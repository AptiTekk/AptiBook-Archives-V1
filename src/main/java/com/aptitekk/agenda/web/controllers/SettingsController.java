package com.aptitekk.agenda.web.controllers;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class SettingsController implements Serializable {

    public enum SettingsPage {
        ASSETS("Assets", "assets.xhtml", "tags"),
        RESERVATION_FIELD_EDITOR("Fields Editor", "reservation_field_editor.xhtml", "comments-o"),
        GROUPS("User Groups", "groups.xhtml", "sitemap"),
        USERS("Users", "users.xhtml", "user"),
        PROPERTIES("Properties", "properties.xhtml", "cog"),
        SERVICES("Services", "services.xhtml", "server");

        private String name;
        private String fileName;
        private String iconAwesomeName;

        SettingsPage(String name, String fileName, String iconAwesomeName) {
            this.name = name;
            this.fileName = fileName;
            this.iconAwesomeName = iconAwesomeName;
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
