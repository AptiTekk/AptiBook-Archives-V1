/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.Permission;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.User;

import javax.ejb.Local;
import java.util.List;

@Local
public interface PermissionService extends MultiTenantEntityService<Permission> {

    List<Permission> getAllJoinUsersAndGroups();

    /**
     * Gets the Permission Entity that matches the Permission Descriptor, within the current Tenant.
     *
     * @param descriptor The Permission Descriptor.
     * @return the Permission Entity if found, null otherwise.
     */
    Permission getPermissionByDescriptor(Permission.Descriptor descriptor);

    /**
     * Gets the Permission Entity that matches the Permission Descriptor, within the specified Tenant.
     *
     * @param descriptor The Permission Descriptor.
     * @param tenant The Tenant of the Permission being searched for.
     * @return the Permission Entity if found, null otherwise.
     */
    Permission getPermissionByDescriptor(Permission.Descriptor descriptor, Tenant tenant);

    /**
     * Determines if the User has the permission supplied.
     * Will take into account the User Groups that the User is part of.
     *
     * @param user       The User to check.
     * @param descriptor The Permission Descriptor to look for.
     * @return true if the User has the Permission, false otherwise.
     */
    boolean userHasPermission(User user, Permission.Descriptor descriptor);

    /**
     * Determines if the User has any permissions within the specified Permission Group.
     *
     * @param user  The User to check.
     * @param group The Permission Group that one or more of the User's Permissions should belong to.
     * @return true if the User has a Permission in the Group, false otherwise.
     */
    boolean userHasPermissionOfGroup(User user, Permission.Group group);

}
