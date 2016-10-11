/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;

@Stateful
public class PermissionService extends MultiTenantEntityServiceAbstract<Permission> implements Serializable {

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

    /**
     * Gets the Permission Entity that matches the Permission Descriptor, within the current Tenant.
     *
     * @param descriptor The Permission Descriptor.
     * @return the Permission Entity if found, null otherwise.
     */
    public Permission getPermissionByDescriptor(Permission.Descriptor descriptor) {
        return getPermissionByDescriptor(descriptor, getTenant());
    }

    /**
     * Gets the Permission Entity that matches the Permission Descriptor, within the specified Tenant.
     *
     * @param descriptor The Permission Descriptor.
     * @param tenant     The Tenant of the Permission being searched for.
     * @return the Permission Entity if found, null otherwise.
     */
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

    /**
     * Determines if the User has the permission supplied.
     * Will take into account the User Groups that the User is part of.
     *
     * @param user       The User to check.
     * @param descriptor The Permission Descriptor to look for.
     * @return true if the User has the Permission, false otherwise.
     */
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

    /**
     * Determines if the User has any permissions within the specified Permission Group.
     *
     * @param user  The User to check.
     * @param group The Permission Group that one or more of the User's Permissions should belong to.
     * @return true if the User has a Permission in the Group, false otherwise.
     */
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
