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
     * <p>
     * NOTE: Any modifications to the NAME of the group (not friendlyName) will clear its existence from the database!
     */
    public enum Group {

        GENERAL("General"),
        ASSET_TYPES("Asset Types"),
        ASSETS("Assets"),
        USERS("Users"),
        GROUPS("Groups"),
        PERMISSIONS("Permissions"),
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
     * <p>
     * NOTE: Any modifications to the NAME of the descriptor (not friendlyName) will clear its existence from the database!
     */
    public enum Descriptor {

        GENERAL_FULL_PERMISSIONS(Group.GENERAL, "Full Permissions",
                "Users and User Groups with this permission are granted all permissions. They have un-restricted access to Agenda." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User is granted all permissions.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group are granted all permissions.</li>" +
                        "</ul>"),

        ASSET_TYPES_MODIFY_ALL(Group.ASSET_TYPES, "May Modify Any Asset Types",
                "Users and User Groups with this permission may create, edit, and delete any Asset Types." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may create, edit, and delete any Asset Types.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may create, edit, and delete any Asset Types.</li>" +
                        "</ul>"),

        ASSETS_MODIFY_OWN(Group.ASSETS, "May Modify Own Group's Assets",
                "Users and User Groups with this permission may create, edit, and delete Assets for their User Group." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may create, edit, and delete Assets for the User Groups that the User is assigned to.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may create, edit, and delete Assets for their User Group.</li>" +
                        "</ul>"),
        ASSETS_MODIFY_HIERARCHY(Group.ASSETS, "May Modify Hierarchy's Assets",
                "Users and User Groups with this permission may create, edit, and delete Assets for their User Group and that User Group's children." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may create, edit, and delete Assets for the User Groups that the User is assigned to, and those User Groups' children.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may create, edit, and delete Assets for their User Group and that User Group's children.</li>" +
                        "</ul>"),
        ASSETS_MODIFY_ALL(Group.ASSETS, "May Modify Any Assets",
                "Users and User Groups with this permission may create, edit, and delete any Assets." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may create, edit, and delete any Assets.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may create, edit, and delete any Assets.</li>" +
                        "</ul>"),

        USERS_MODIFY_ALL(Group.USERS, "May Modify Any Users",
                "Users and User Groups with this permission may create, edit, and delete any Users." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may create, edit, and delete any Users.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may create, edit, and delete any Users.</li>" +
                        "</ul>"),

        GROUPS_MODIFY_ALL(Group.GROUPS, "May Modify Any User Groups",
                "Users and User Groups with this permission may create, edit, and delete any User Groups." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may create, edit, and delete any User Groups.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may create, edit, and delete any User Groups.</li>" +
                        "</ul>"),

        PERMISSIONS_MODIFY_ALL(Group.PERMISSIONS, "May Assign Any Permissions",
                "Users and User Groups with this permission may assign any Permissions." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may assign any Permissions.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may assign any Permissions.</li>" +
                        "</ul>"),

        PROPERTIES_MODIFY_ALL(Group.PROPERTIES, "May Modify Any Properties",
                "Users and User Groups with this permission may edit any Properties." +
                        "<ul>" +
                        "<li>If a User is given this permission, the User may edit any Properties.</li>" +
                        "<li>If a User Group is given this permission, any Users within the User Group may edit any Properties.</li>" +
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

    @Enumerated(value = EnumType.STRING)
    private Descriptor descriptor;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserGroup> userGroups;

    @ManyToMany(fetch = FetchType.EAGER)
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
