/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings;

import com.aptitekk.aptibook.core.entities.Permission;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Named
@SessionScoped
public class SettingsSessionController implements Serializable {

    @SuppressWarnings("WeakerAccess")
    public enum SettingsPage {
        ASSET_CATEGORIES("Asset Categories", "assetCategories", "folder-open", Permission.Group.ASSET_CATEGORIES, null),
        ASSETS("Assets", "assets", "tags", Permission.Group.ASSETS, ASSET_CATEGORIES),
        USER_GROUPS("User Groups", "groups", "sitemap", Permission.Group.GROUPS, null),
        USERS("Users", "users", "user", Permission.Group.USERS, USER_GROUPS),
        PERMISSIONS("Permissions", "permissions", "unlock", Permission.Group.PERMISSIONS, null),
        PROPERTIES("Properties", "properties", "cog", Permission.Group.PROPERTIES, null);

        private String name;
        private String fileName;
        private String iconAwesomeName;
        private Permission.Group permissionGroup;
        private SettingsPage parent;

        SettingsPage(String name, String fileName, String iconAwesomeName, Permission.Group permissionGroup, SettingsPage parent) {
            this.name = name;
            this.fileName = fileName;
            this.iconAwesomeName = iconAwesomeName;
            this.permissionGroup = permissionGroup;
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

        public Permission.Group getPermissionGroup() {
            return permissionGroup;
        }

        public SettingsPage getParent() {
            return parent;
        }
    }

    @Inject
    private AuthenticationController authenticationController;

    private List<SettingsPage> pages;
    private SettingsPage currentPage = null;

    void checkPagesValidity() {
        if ((pages == null || pages.isEmpty()) && authenticationController.getAuthenticatedUser() != null) {
            //Prune pages based on the permissions granted to the user.
            pages = new ArrayList<>(Arrays.asList(SettingsPage.values()));
            if (!authenticationController.getAuthenticatedUser().isAdmin()) {
                Iterator<SettingsPage> iterator = pages.iterator();
                while (iterator.hasNext()) {
                    SettingsPage next = iterator.next();
                    Permission.Group permissionGroup = next.getPermissionGroup();
                    if (permissionGroup == null)
                        iterator.remove();
                    else if (!authenticationController.userHasPermissionOfGroup(permissionGroup))
                        iterator.remove();
                }
            }
        }
    }

    String redirectIfPageIsNull() {
        if (getCurrentPage() == null) {
            if (pages == null || pages.isEmpty()) {
                return "index";
            }
            return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true&settingsPage=" + pages.get(0).getName().replaceAll(" ", "+");
        }
        return null;
    }

    List<SettingsPage> getPages() {
        return pages;
    }

    SettingsPage getCurrentPage() {
        return currentPage;
    }

    void setCurrentPage(SettingsPage page) {
        this.currentPage = page;
    }

}
