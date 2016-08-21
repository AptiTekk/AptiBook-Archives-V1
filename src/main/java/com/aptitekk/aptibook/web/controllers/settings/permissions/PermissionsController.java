/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.permissions;

import com.aptitekk.aptibook.core.entities.Permission;
import com.aptitekk.aptibook.core.entities.User;
import com.aptitekk.aptibook.core.entities.UserGroup;
import com.aptitekk.aptibook.core.entities.services.PermissionService;
import com.aptitekk.aptibook.core.entities.services.UserService;
import com.aptitekk.aptibook.web.controllers.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
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

    @Inject
    private HelpController helpController;

    private List<PermissionDetails> permissionDetailsList;

    private Permission assignmentPermission;

    private List<User> allUsers;
    private Set<User> assignedUsers;
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

        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_PERMISSIONS);
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.PERMISSIONS);
    }

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.PERMISSIONS_MODIFY_ALL);
    }

    private void buildPermissionDetailsList() {
        //Load all permissions from database.
        List<Permission> permissions = permissionService.getAllJoinUsersAndGroups();
        Map<Permission.Descriptor, Permission> descriptorPermissionMap = new HashMap<>();
        for (Permission permission : permissions) {
            descriptorPermissionMap.putIfAbsent(permission.getDescriptor(), permission);
        }

        permissionDetailsList = new ArrayList<>();

        Map<Permission.Group, List<Permission>> permissionGroupMap = new LinkedHashMap<>();

        for (Permission.Group group : Permission.Group.values())
            permissionGroupMap.put(group, new ArrayList<>());

        for (Permission.Descriptor descriptor : Permission.Descriptor.values())
            permissionGroupMap.get(descriptor.getGroup()).add(descriptorPermissionMap.get(descriptor));

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
                assignedUsers = new LinkedHashSet<>(assignmentPermission.getUsers());

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
                    assignedUsers = new LinkedHashSet<>();
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
            Set<UserGroup> userGroups = new LinkedHashSet<>();
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

    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<User> users) {
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
