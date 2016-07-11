/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.permissions;

import com.aptitekk.agenda.core.entities.Permission;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.services.PermissionService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.web.controllers.AuthenticationController;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class PermissionsController implements Serializable {

    @Inject
    private PermissionService permissionService;

    @Inject
    private UserService userService;

    @Inject
    private AuthenticationController authenticationController;

    private List<PermissionDetails> permissionDetailsList;

    private Permission assignmentPermission;

    private List<User> allUsers;
    private List<User> assignedUsers;
    private List<User> availableUsers;
    private TreeNode[] assignedGroups;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }

        allUsers = userService.getAll();

        //Admin has all permissions by default. Remove it from the list.
        Iterator<User> iterator = allUsers.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isAdmin())
                iterator.remove();
        }

        buildPermissionDetailsList();
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.PERMISSIONS);
    }

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.PERMISSIONS_MODIFY_ALL);
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
        if (!hasModifyPermission())
            return;

        this.assignmentPermission = assignmentPermission;
        if (assignmentPermission != null) {
            if (assignmentPermission.getUsers() == null || assignmentPermission.getUsers().isEmpty())
                assignedUsers = null;
            else
                assignedUsers = new ArrayList<>(assignmentPermission.getUsers());

            availableUsers = new ArrayList<>(allUsers);
            if (assignedUsers != null) {
                //Remove any users from the availableUsers list that are already assigned.
                assignedUsers.stream().filter(user -> availableUsers.contains(user)).forEach(user -> availableUsers.remove(user));
            }
        }
    }

    public void assignUser(User user) {
        if (!hasModifyPermission())
            return;

        if (availableUsers != null) {
            if (availableUsers.contains(user) && (assignedUsers == null || !assignedUsers.contains(user))) {
                if (assignedUsers == null)
                    assignedUsers = new ArrayList<>();
                assignedUsers.add(user);
                availableUsers.remove(user);
            }
        }
    }

    public void unAssignUser(User user) {
        if (!hasModifyPermission())
            return;

        if (availableUsers != null && assignedUsers != null) {
            if (!availableUsers.contains(user) && assignedUsers.contains(user)) {
                assignedUsers.remove(user);
                if (assignedUsers.isEmpty())
                    assignedUsers = null;
                availableUsers.add(user);
            }
        }
    }

    public void saveChanges() {
        if (!hasModifyPermission())
            return;

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
        return assignedUsers;
    }

    public void setAssignedUsers(List<User> users) {
        this.assignedUsers = users;
    }

    public List<User> getAvailableUsers() {
        return availableUsers;
    }

    public void setAssignedGroups(TreeNode[] assignedGroups) {
        this.assignedGroups = assignedGroups;
    }

    public TreeNode[] getAssignedGroups() {
        return assignedGroups;
    }
}
