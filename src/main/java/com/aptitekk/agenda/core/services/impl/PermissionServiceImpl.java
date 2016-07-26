/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Permission;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.services.PermissionService;
import com.aptitekk.agenda.core.util.LogManager;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Stateful
public class PermissionServiceImpl extends MultiTenantEntityServiceAbstract<Permission> implements PermissionService, Serializable {

    @Override
    public List<Permission> getAllJoinUsersAndGroups() {
        try {
            List<Permission> permissionsUsers = entityManager
                    .createQuery("SELECT p FROM Permission p LEFT JOIN FETCH p.users WHERE p.tenant = ?1", Permission.class)
                    .setParameter(1, getTenant())
                    .getResultList();

            List<Permission> permissionsUserGroups = entityManager
                    .createQuery("SELECT p FROM Permission p LEFT JOIN FETCH p.userGroups WHERE p.tenant = ?1", Permission.class)
                    .setParameter(1, getTenant())
                    .getResultList();

            for (Permission permission : permissionsUserGroups) {
                if (permissionsUsers.contains(permission))
                    permissionsUsers.get(permissionsUsers.indexOf(permission)).setUserGroups(permission.getUserGroups());
            }
            return permissionsUsers;
        } catch (PersistenceException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Permission getPermissionByDescriptor(Permission.Descriptor descriptor) {
        return getPermissionByDescriptor(descriptor, getTenant());
    }

    @Override
    public Permission getPermissionByDescriptor(Permission.Descriptor descriptor, Tenant tenant) {
        if (descriptor == null || tenant == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT p FROM Permission p WHERE p.descriptor = :descriptor AND p.tenant = :tenant", Permission.class)
                    .setParameter("descriptor", descriptor)
                    .setParameter("tenant", tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public boolean userHasPermission(User user, Permission.Descriptor descriptor) {
        if (user == null || descriptor == null)
            return false;

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
        if (user == null || group == null)
            return false;

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
