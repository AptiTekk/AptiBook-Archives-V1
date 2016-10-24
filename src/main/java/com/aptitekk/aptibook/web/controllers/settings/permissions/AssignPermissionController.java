/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.permissions;

import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.domain.services.PermissionService;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
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
public class AssignPermissionController implements Serializable {

    @Inject
    private PermissionService permissionService;

    @Inject
    private UserService userService;

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    private List<PermissionDetails> permissionDetailsList;

    private Permission assignmentPermission;

    /**
     * All users, whether assigned or not.
     */
    private List<User> allUsers;

    /**
     * Contains the users that are currently assigned.
     */
    private Set<User> assignedUsers;

    /**
     * Contains the users that have been assigned since the last save.
     * Used to determine which users should be given new permissions.
     */
    private Set<User> newlyAssignedUsers;

    /**
     * Contains the users that have been unassigned since the last save.
     * Used to determine which users should have permissions removed.
     */
    private Set<User> newlyUnassignedUsers;

    /**
     * The users that are available for assignment.
     */
    private List<User> availableUsers;

    /**
     * Contains the user groups that are currently assigned.
     */
    private TreeNode[] assignedUserGroupNodes;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.invokeUserRedirect();
            return;
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
        //Get all users from database
        allUsers = userService.getAll();

        //Admin has all permissions by default. Remove it from the list.
        Iterator<User> iterator = allUsers.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isAdmin())
                iterator.remove();
        }

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
            if (assignmentPermission.getUsers() != null && !assignmentPermission.getUsers().isEmpty())
                assignedUsers = new LinkedHashSet<>(assignmentPermission.getUsers());
            else
                assignedUsers = new LinkedHashSet<>();

            availableUsers = new ArrayList<>(allUsers);
            //Remove any users from the availableUsers list that are already assigned.
            assignedUsers.stream().filter(user -> availableUsers.contains(user)).forEach(user -> availableUsers.remove(user));

            newlyAssignedUsers = new HashSet<>();
            newlyUnassignedUsers = new HashSet<>();
        }
    }

    public void assignUser(User user) {
        if (!hasModifyPermission())
            return;

        if (availableUsers != null) {
            if (availableUsers.contains(user) && !assignedUsers.contains(user)) {

                //Synchronize assigned/available users
                assignedUsers.add(user);
                availableUsers.remove(user);

                //Synchronize newlyAssigned/Unassigned Users
                newlyAssignedUsers.add(user);
                if (newlyUnassignedUsers.contains(user))
                    newlyUnassignedUsers.remove(user);
            }
        }
    }

    public void unAssignUser(User user) {
        if (!hasModifyPermission())
            return;

        if (availableUsers != null && assignedUsers != null) {
            if (!availableUsers.contains(user) && assignedUsers.contains(user)) {
                //Synchronize assigned/available users
                assignedUsers.remove(user);
                availableUsers.add(user);

                //Synchronize newlyAssigned/Unassigned Users
                if (newlyAssignedUsers.contains(user))
                    newlyAssignedUsers.remove(user);

                newlyUnassignedUsers.add(user);
            }
        }
    }

    public void saveChanges() {
        if (!hasModifyPermission())
            return;

        if (assignmentPermission != null) {
            //Assign new permissions to newly assigned users
            for (User user : newlyAssignedUsers) {
                if (!user.getPermissions().contains(assignmentPermission)) {
                    user.getPermissions().add(assignmentPermission);
                    try {
                        userService.merge(user);
                    } catch (Exception e) {
                        LogManager.logException(getClass(), e, "Could not assign Permission to User");
                    }
                }
            }

            //Remove old permissions from newly unassigned users
            for (User user : newlyUnassignedUsers) {
                if (user.getPermissions().contains(assignmentPermission)) {
                    user.getPermissions().remove(assignmentPermission);
                    try {
                        userService.merge(user);
                    } catch (Exception e) {
                        LogManager.logException(getClass(), e, "Could not remove Permission from User");
                    }
                }
            }

            //Build a Set from the Tree Nodes
            Set<UserGroup> assignedUserGroups = new LinkedHashSet<>();
            for (TreeNode treeNode : assignedUserGroupNodes) {
                if (treeNode.getData() != null && treeNode.getData() instanceof UserGroup)
                    assignedUserGroups.add((UserGroup) treeNode.getData());
            }

            //Add and Remove permissions to/from usergroups
            List<UserGroup> allUserGroups = userGroupService.getAll();
            for (UserGroup userGroup : allUserGroups) {
                if (assignedUserGroups.contains(userGroup)) {
                    if (!userGroup.getPermissions().contains(assignmentPermission)) {
                        userGroup.getPermissions().add(assignmentPermission);
                        try {
                            userGroupService.merge(userGroup);
                        } catch (Exception e) {
                            LogManager.logException(getClass(), e, "Could not assign Permission to User Group");
                        }
                    }
                } else {
                    if (userGroup.getPermissions().contains(assignmentPermission)) {
                        userGroup.getPermissions().remove(assignmentPermission);
                        try {
                            userGroupService.merge(userGroup);
                        } catch (Exception e) {
                            LogManager.logException(getClass(), e, "Could not remove Permission from User Group");
                        }
                    }
                }
            }
        }

        buildPermissionDetailsList();
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

    public void setAssignedUserGroupNodes(TreeNode[] assignedUserGroupNodes) {
        this.assignedUserGroupNodes = assignedUserGroupNodes;
    }

    public TreeNode[] getAssignedUserGroupNodes() {
        return assignedUserGroupNodes;
    }
}
