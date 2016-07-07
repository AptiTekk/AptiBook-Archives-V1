/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.permissions;

import com.aptitekk.agenda.core.entity.Permission;
import com.aptitekk.agenda.core.permissions.PermissionDescriptor;
import com.aptitekk.agenda.core.permissions.PermissionGroup;

import java.util.List;

public class PermissionDetails {

    private PermissionGroup permissionGroup;
    private List<Permission> permissions;

    public PermissionDetails(PermissionGroup permissionGroup, List<Permission> permissions)
    {
        this.permissionGroup = permissionGroup;
        this.permissions = permissions;
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
