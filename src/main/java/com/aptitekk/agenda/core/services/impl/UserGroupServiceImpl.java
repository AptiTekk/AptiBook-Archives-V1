/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entity.QUserGroup;
import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.services.UserGroupService;
import com.querydsl.jpa.impl.JPAQuery;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.*;

@Stateless
public class UserGroupServiceImpl extends EntityServiceAbstract<UserGroup> implements UserGroupService, Serializable {

    QUserGroup userGroupTable = QUserGroup.userGroup;

    public UserGroupServiceImpl() {
        super(UserGroup.class);
    }

    @PostConstruct
    public void init() {
        loadTree();
    }

    @Override
    public void loadTree() {
        entityManager.createQuery("SELECT g from UserGroup g left join fetch g.children").getResultList();
    }

    @Override
    public UserGroup findByName(String userGroupName) {
        if (userGroupName == null)
            return null;

        return new JPAQuery<UserGroup>(entityManager).from(userGroupTable)
                .where(userGroupTable.name.equalsIgnoreCase(userGroupName)).fetchOne();
    }

    @Override
    public UserGroup getRootGroup() {
        Object result = entityManager.createQuery("SELECT g FROM UserGroup g WHERE g.name = ?1").setParameter(1, ROOT_GROUP_NAME).getSingleResult();
        return result == null ? null : (UserGroup) result;
    }

    @Override
    public UserGroup[] getSenior(List<UserGroup> groups) {
        Map<UserGroup, Integer> steps = new HashMap<>();
        groups.forEach(userGroup -> {
            UserGroup currentParent = userGroup.getParent();
            if (currentParent != null) {
                steps.put(userGroup, 1);
                while (currentParent.getId() != getRootGroup().getId()) {
                    steps.put(userGroup, steps.get(userGroup) + 1);
                    currentParent = currentParent.getParent();
                }
            }
        });

        Map<UserGroup, Integer> treeMap = new TreeMap<UserGroup, Integer>(
                (o1, o2) -> steps.get(o2) - steps.get(o1));
        treeMap.putAll(steps);
        return treeMap.keySet().toArray(new UserGroup[treeMap.size()]);
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
