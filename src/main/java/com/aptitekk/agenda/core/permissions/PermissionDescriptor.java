/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.permissions;

public enum PermissionDescriptor {

    ASSETS_EDIT_OWN(PermissionGroup.ASSETS, "May Edit Own Group's Assets",
            "Users and User Groups with this permission may edit the Assets that are assigned to their User Group." +
                    "<ul>" +
                    "<li>If a User Group is given this permission, any Users within the User Group may edit the Assets which belong to the User Group.</li>" +
                    "<li>If a User is given this permission, the User may edit the Assets which belong to the User Groups to which the User is assigned.</li>" +
                    "</ul>"),
    ASSETS_EDIT_HIERARCHY(PermissionGroup.ASSETS, "May Edit Hierarchy's Assets",
            "Users and User Groups with this permission may edit the Assets that are assigned to their User Group and any User Groups beneath them." +
                    "<ul>" +
                    "<li>If a User Group is given this permission, any Users within the User Group may edit the Assets which belong to the User Group and those beneath it.</li>" +
                    "<li>If a User is given this permission, the User may edit the Assets which belong to the User Groups to which the User is assigned, and all groups beneath them.</li>" +
                    "</ul>"),
    ASSETS_EDIT_ALL(PermissionGroup.ASSETS, "May Edit All Assets",
            "Users and Groups with this permission may edit all Assets regardless of assignment." +
                    "<ul>" +
                    "<li>If a User Group is given this permission, any Users within the User Group may edit any Assets.</li>" +
                    "<li>If a User is given this permission, the User may edit any Assets.</li>" +
                    "</ul>");

    private final PermissionGroup permissionGroup;
    private final String friendlyName;
    private final String description;

    PermissionDescriptor(PermissionGroup permissionGroup, String friendlyName, String description) {
        this.permissionGroup = permissionGroup;
        this.friendlyName = friendlyName;
        this.description = description;
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getDescription() {
        return description;
    }
}
