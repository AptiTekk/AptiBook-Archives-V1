/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Stateful
public class UserGroupService extends MultiTenantEntityServiceAbstract<UserGroup> implements Serializable {

    public static final String ROOT_GROUP_NAME = "root";

    /**
     * Finds Group Entity by its name, within the current Tenant.
     *
     * @param userGroupName The name of the group to search for.
     * @return A User Group with the specified name, or null if one does not exist.
     */
    public UserGroup findByName(String userGroupName) {
        return findByName(userGroupName, getTenant());
    }

    /**
     * Finds Group Entity by its name, within the specified Tenant.
     *
     * @param userGroupName The name of the group to search for.
     * @param tenant    The Tenant of the User Group to search for.
     * @return A User Group with the specified name, or null if one does not exist.
     */
    public UserGroup findByName(String userGroupName, Tenant tenant) {
        if (userGroupName == null || tenant == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT g FROM UserGroup g WHERE g.name = :name AND g.tenant = :tenant", UserGroup.class)
                    .setParameter("name", userGroupName)
                    .setParameter("tenant", tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    /**
     * @return The Root UserGroup of the current Tenant.
     */
    public UserGroup getRootGroup() {
        return getRootGroup(getTenant());
    }

    /**
     * @return The Root UserGroup of the specified Tenant.
     */
    public UserGroup getRootGroup(Tenant tenant) {
        return findByName(ROOT_GROUP_NAME, tenant);
    }

    /**
     *
     * @param userGroup User Group to get all children for.
     * @return A list of all children of passed in User Group.
     */
    public List<UserGroup> getUserGroupChildren(UserGroup userGroup){
        Queue<UserGroup> queue = new LinkedList<>();
        queue.add(userGroup);
        UserGroup currEntry;
        List<UserGroup> groups = new ArrayList<>();
        while ((currEntry = queue.poll()) != null){
            groups.add(currEntry);
            for(UserGroup child : currEntry.getChildren()){
                queue.add(child);
            }
        }
        return groups;
    }

    public List<UserGroup> getHierarchyUp(UserGroup origin) {
        List<UserGroup> hierarchy = new ArrayList<>();
        hierarchy.add(origin);

        UserGroup currentGroup = origin;
        UserGroup parentGroup;
        while ((parentGroup = currentGroup.getParent()) != null) {
            hierarchy.add(parentGroup);
            currentGroup = parentGroup;
        }

        return hierarchy;
    }


}
