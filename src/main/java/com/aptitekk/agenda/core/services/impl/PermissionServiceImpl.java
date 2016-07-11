/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Permission;
import com.aptitekk.agenda.core.entities.QPermission;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.services.PermissionService;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import java.io.Serializable;

@Stateless
public class PermissionServiceImpl extends EntityServiceAbstract<Permission> implements PermissionService, Serializable {

    private QPermission table = QPermission.permission;

    @Override
    public Permission getPermissionByDescriptor(Permission.Descriptor descriptor) {
        if (descriptor == null)
            return null;

        return new JPAQuery<Permission>(entityManager).from(table).where(table.descriptor.eq(descriptor))
                .fetchOne();
    }

    @Override
    public boolean userHasPermission(User user, Permission.Descriptor descriptor) {
        for (Permission permission : user.getPermissions()) {
            if (permission.getDescriptor() == descriptor)
                return true;
        }

        for (UserGroup userGroup : user.getUserGroups()) {
            for (Permission permission : userGroup.getPermissions()) {
                if (permission.getDescriptor() == descriptor)
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean userHasPermissionOfGroup(User user, Permission.Group group) {
        for (Permission permission : user.getPermissions()) {
            if (permission.getDescriptor().getGroup() == group)
                return true;
        }

        for (UserGroup userGroup : user.getUserGroups()) {
            for (Permission permission : userGroup.getPermissions()) {
                if (permission.getDescriptor().getGroup() == group)
                    return true;
            }
        }

        return false;
    }
}
