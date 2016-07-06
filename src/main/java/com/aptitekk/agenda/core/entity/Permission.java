/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entity;

import com.aptitekk.agenda.core.permissions.PermissionDescriptor;
import com.aptitekk.agenda.core.permissions.PermissionGroup;

import javax.persistence.*;
import java.util.List;

@Entity
public class Permission {

    @Id
    @GeneratedValue
    private int id;

    private PermissionDescriptor descriptor;

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

    public PermissionDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(PermissionDescriptor descriptor) {
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
