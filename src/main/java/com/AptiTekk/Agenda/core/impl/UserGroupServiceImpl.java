package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.UserGroupService;
import com.AptiTekk.Agenda.core.entity.QUserGroup;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import com.mysema.query.jpa.impl.JPAQuery;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Stateless
public class UserGroupServiceImpl extends EntityServiceAbstract<UserGroup>
        implements UserGroupService {

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
        return new JPAQuery(entityManager).from(userGroupTable)
                .where(userGroupTable.name.eq(userGroupName)).singleResult(userGroupTable);
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


}
