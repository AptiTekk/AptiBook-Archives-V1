/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.UserGroup;

import javax.ejb.Local;
import java.util.List;

@Local
public interface UserGroupService extends MultiTenantEntityService<UserGroup> {

    String ROOT_GROUP_NAME = "root";

    /**
     * Forces a reload of the tree, starting at the root element and propagating downward.
     */
    void loadTree();

    /**
     * Finds Group Entity by its name, within the current Tenant.
     *
     * @param userGroupName The name of the group to search for.
     * @return A User Group with the specified name, or null if one does not exist.
     */
    UserGroup findByName(String userGroupName);

    /**
     * Finds Group Entity by its name, within the specified Tenant.
     *
     * @param userGroupName The name of the group to search for.
     * @param tenant    The Tenant of the User Group to search for.
     * @return A User Group with the specified name, or null if one does not exist.
     */
    UserGroup findByName(String userGroupName, Tenant tenant);

    /**
     * @return The Root UserGroup of the current Tenant.
     */
    UserGroup getRootGroup();

    /**
     * @return The Root UserGroup of the specified Tenant.
     */
    UserGroup getRootGroup(Tenant tenant);

    List<UserGroup> getHierarchyUp(UserGroup origin);
}
