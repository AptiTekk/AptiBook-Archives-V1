/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.permissions;

public enum PermissionGroup {

    ASSETS("Assets"),
    USERS("Users"),
    GROUPS("Groups"),
    PROPERTIES("Properties");

    private String friendlyName;

    PermissionGroup(String friendlyName) {

        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
