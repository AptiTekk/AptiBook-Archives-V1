/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.permissions;

import com.aptitekk.agenda.core.entities.Permission;

import java.util.List;

public class PermissionDetails {

    private Permission.Group group;
    private List<Permission> permissions;

    public PermissionDetails(Permission.Group group, List<Permission> permissions)
    {
        this.group = group;
        this.permissions = permissions;
    }

    public Permission.Group getGroup() {
        return group;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
