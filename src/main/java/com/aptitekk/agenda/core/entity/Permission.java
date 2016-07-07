/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Permission {

    /**
     * Defines the groups of permissions.
     * The order in this enum determines the order shown to the user on the Permissions page.
     */
    public enum Group {

        ASSETS("Assets"),
        USERS("Users"),
        GROUPS("Groups"),
        PROPERTIES("Properties");

        private String friendlyName;

        Group(String friendlyName) {

            this.friendlyName = friendlyName;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

    }

    /**
     * Defines the details about the permissions, including their descriptions.
     * The order in this enum determines the order shown to the user within each Group on the Permissions page.
     */
    public enum Descriptor {

        ASSETS_EDIT_OWN(Group.ASSETS, "May Edit Own Group's Assets",
                "Users and User Groups with this permission may edit the Assets that are assigned to their User Group." +
                        "<ul>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may edit the Assets which belong to the User Group.</li>" +
                        "<li>If a User is given this permission, the User may edit the Assets which belong to the User Groups to which the User is assigned.</li>" +
                        "</ul>"),
        ASSETS_EDIT_HIERARCHY(Group.ASSETS, "May Edit Hierarchy's Assets",
                "Users and User Groups with this permission may edit the Assets that are assigned to their User Group and any User Groups beneath them." +
                        "<ul>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may edit the Assets which belong to the User Group and those beneath it.</li>" +
                        "<li>If a User is given this permission, the User may edit the Assets which belong to the User Groups to which the User is assigned, and all groups beneath them.</li>" +
                        "</ul>"),
        ASSETS_EDIT_ALL(Group.ASSETS, "May Edit All Assets",
                "Users and Groups with this permission may edit all Assets regardless of assignment." +
                        "<ul>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may edit any Assets.</li>" +
                        "<li>If a User is given this permission, the User may edit any Assets.</li>" +
                        "</ul>");

        private final Group group;
        private final String friendlyName;
        private final String description;

        Descriptor(Group group, String friendlyName, String description) {
            this.group = group;
            this.friendlyName = friendlyName;
            this.description = description;
        }

        public Group getGroup() {
            return group;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

        public String getDescription() {
            return description;
        }

    }

    @Id
    @GeneratedValue
    private int id;

    private Descriptor descriptor;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "permissions")
    private List<UserGroup> userGroups;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "permissions")
    private List<User> users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
