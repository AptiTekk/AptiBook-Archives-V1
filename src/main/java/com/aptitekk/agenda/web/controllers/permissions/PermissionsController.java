/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.permissions;

import com.aptitekk.agenda.core.entity.Permission;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.services.PermissionService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.utilities.LogManager;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class PermissionsController implements Serializable {

    @Inject
    private PermissionService permissionService;

    @Inject
    private UserService userService;

    private List<PermissionDetails> permissionDetailsList;

    private Permission assignmentPermission;

    private List<User> allUsers;
    private List<User> assignedUsers;
    private List<User> unAssignedUsers;
    private TreeNode[] assignedGroups;

    @PostConstruct
    public void init() {
        allUsers = userService.getAll();
        buildPermissionDetailsList();
    }

    private void buildPermissionDetailsList() {
        permissionDetailsList = new ArrayList<>();
        Map<Permission.Group, List<Permission>> permissionGroupMap = new LinkedHashMap<>();

        for (Permission.Group group : Permission.Group.values())
            permissionGroupMap.put(group, new ArrayList<>());

        for (Permission.Descriptor descriptor : Permission.Descriptor.values())
            permissionGroupMap.get(descriptor.getGroup()).add(permissionService.getPermissionByDescriptor(descriptor));

        for (Map.Entry<Permission.Group, List<Permission>> entry : permissionGroupMap.entrySet())
            permissionDetailsList.add(new PermissionDetails(entry.getKey(), entry.getValue()));
    }

    public Permission getAssignmentPermission() {
        return assignmentPermission;
    }

    public void setAssignmentPermission(Permission assignmentPermission) {
        this.assignmentPermission = assignmentPermission;
        if (assignmentPermission != null) {
            assignedUsers = assignmentPermission.getUsers();

            unAssignedUsers = allUsers.subList(0, allUsers.size() - 1);
            //Remove any users from the unAssignedUsers list that are already assigned.
            assignedUsers.stream().filter(user -> unAssignedUsers.contains(user)).forEach(user -> unAssignedUsers.remove(user));
        }
    }

    public void assignUser(User user) {
        if (assignedUsers != null && unAssignedUsers != null) {
            if (unAssignedUsers.contains(user) && !assignedUsers.contains(user)) {
                assignedUsers.add(user);
                unAssignedUsers.remove(user);
            }
        }
    }

    public void unAssignUser(User user) {
        if (assignedUsers != null && unAssignedUsers != null) {
            if (!unAssignedUsers.contains(user) && assignedUsers.contains(user)) {
                assignedUsers.remove(user);
                unAssignedUsers.add(user);
            }
        }
    }

    public void saveChanges() {
        if (assignmentPermission != null) {
            assignmentPermission.setUsers(assignedUsers);
            List<UserGroup> userGroups = new ArrayList<>();
            for (TreeNode treeNode : assignedGroups) {
                if (treeNode.getData() != null && treeNode.getData() instanceof UserGroup)
                    userGroups.add((UserGroup) treeNode.getData());
            }
            assignmentPermission.setUserGroups(userGroups);

            try {
                assignmentPermission = permissionService.merge(assignmentPermission);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<PermissionDetails> getPermissionDetailsList() {
        return permissionDetailsList;
    }

    public List<User> getAssignedUsers() {
        if (assignmentPermission == null)
            return null;

        if (assignmentPermission.getUsers() == null || assignmentPermission.getUsers().isEmpty())
            return null;
        else
            return assignmentPermission.getUsers();
    }

    public void setAssignedUsers(List<User> users) {
        this.assignedUsers = users;
    }

    public List<User> getUnAssignedUsers() {
        return unAssignedUsers;
    }

    public void setAssignedGroups(TreeNode[] assignedGroups) {
        this.assignedGroups = assignedGroups;
    }

    public TreeNode[] getAssignedGroups() {
        return assignedGroups;
    }
}
