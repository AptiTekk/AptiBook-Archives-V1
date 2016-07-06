/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.UserGroup;

import javax.ejb.Local;
import java.util.List;

@Local
public interface UserGroupService extends EntityService<UserGroup> {

    String ROOT_GROUP_NAME = "root";

    /**
     * Forces a reload of the tree, starting at the root element and propagating downward.
     */
    void loadTree();

    /**
     * Finds Group Entity by its name
     *
     * @param groupName The name of the group to search for.
     * @return Group where table.name = groupName
     */
    UserGroup findByName(String groupName);

    /**
     * @return The top level, AKA root group.
     */
    UserGroup getRootGroup();

    UserGroup[] getSenior(List<UserGroup> groups);

    List<UserGroup> getHierarchyUp(UserGroup origin);
}
