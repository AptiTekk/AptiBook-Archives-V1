/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.services.UserGroupService;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class UserGroupServiceImpl extends MultiTenantEntityServiceAbstract<UserGroup> implements UserGroupService, Serializable {

    @Override
    public void loadTree() {
        try {
            entityManager.createQuery("SELECT g from UserGroup g left join fetch g.children").getResultList();
        } catch (PersistenceException ignored) {
        }
    }

    @Override
    public UserGroup findByName(String userGroupName) {
        return findByName(userGroupName, getTenant());
    }

    @Override
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

    @Override
    public UserGroup getRootGroup() {
        return getRootGroup(getTenant());
    }

    @Override
    public UserGroup getRootGroup(Tenant tenant) {
        return findByName(ROOT_GROUP_NAME, tenant);
    }

    @Override
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
