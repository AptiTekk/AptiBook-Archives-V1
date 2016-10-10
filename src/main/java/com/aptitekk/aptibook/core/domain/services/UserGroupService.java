/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.*;

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

    @Override
    public void delete(int id) throws Exception {
        UserGroup userGroup = get(id);

        if (userGroup != null) {
            //Remove user assignments
            for (User user : userGroup.getUsers()) {
                user.getUserGroups().remove(userGroup);
            }
        }

        super.delete(id);
    }

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
     * @param tenant        The Tenant of the User Group to search for.
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
     * Returns a list containing all usergroups above and including the usergroup passed in.
     * The list is in no specific order.
     *
     * @param origin The origin usergroup, from which the tree shall be traversed up.
     */
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

    /**
     * Returns a list containing all usergroups below and including the usergroup passed in.
     * The list is in no specific order.
     *
     * @param origin The origin usergroup, from which the tree shall be traversed down.
     */
    public List<UserGroup> getHierarchyDown(UserGroup origin) {
        Queue<UserGroup> queue = new LinkedList<>();
        queue.add(origin);
        UserGroup currEntry;
        List<UserGroup> groups = new ArrayList<>();
        while ((currEntry = queue.poll()) != null) {
            groups.add(currEntry);
            for (UserGroup child : currEntry.getChildren()) {
                queue.add(child);
            }
        }
        return groups;
    }

    /**
     * Returns a list containing all assets below and including those belonging to the usergroup passed in.
     * The list is in no specific order.
     *
     * @param origin The origin usergroup, from which the tree shall be traversed down.
     */
    public List<Asset> getHierarchyDownAssets(UserGroup origin) {
        List<UserGroup> userGroups = getHierarchyDown(origin);
        List<Asset> userGroupAssets = new ArrayList<>();
        for (UserGroup userGroup : userGroups) {
            userGroupAssets.addAll(userGroup.getAssets());
        }
        return userGroupAssets;
    }

    /**
     * Returns a list containing all reservations below and including those belonging to the assets of the usergroup passed in.
     * The list is in no specific order.
     *
     * @param origin The origin usergroup, from which the tree shall be traversed down.
     */
    public List<Reservation> getHierarchyDownReservations(UserGroup origin) {
        List<Reservation> assetReservations = new ArrayList<>();
        for (Asset asset : getHierarchyDownAssets(origin)) {
            assetReservations.addAll(asset.getReservations());
        }
        return assetReservations;
    }

}
