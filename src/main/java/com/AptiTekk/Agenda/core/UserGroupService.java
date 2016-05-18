package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.UserGroup;

import javax.ejb.Local;
import java.util.List;

@Local
public interface UserGroupService extends EntityService<UserGroup> {

    public final static String ROOT_GROUP_NAME = "root";

    /**
     * Forces a reload of the tree, starting at the root element and propagating downward.
     */
    public void loadTree();

    /**
     * Finds Group Entity by its name
     *
     * @param groupName The name of the group to search for.
     * @return Group where table.name = groupName
     */
    public UserGroup findByName(String groupName);

    /**
     * @return The top level, AKA root group.
     */
    public UserGroup getRootGroup();

    UserGroup[] getSenior(List<UserGroup> groups);

}
