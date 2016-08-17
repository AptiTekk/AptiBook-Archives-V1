/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.aptibook.web.controllers.settings.groups;

import com.aptitekk.aptibook.core.entities.Permission;
import com.aptitekk.aptibook.core.entities.User;
import com.aptitekk.aptibook.core.entities.UserGroup;
import com.aptitekk.aptibook.core.entities.services.UserGroupService;
import com.aptitekk.aptibook.core.entities.services.UserService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.AuthenticationController;
import org.primefaces.event.NodeSelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Named
@ViewScoped
public class GroupEditController implements Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private UserService userService;

    @Inject
    private AuthenticationController authenticationController;

    private UserGroup selectedUserGroup;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableGroupName;
    private NewGroupController newGroupController;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }

        resetSettings();
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.GROUPS);
    }

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.GROUPS_MODIFY_ALL);
    }

    public void updateSettings() {
        if (!hasModifyPermission())
            return;

        if (editableGroupName != null) {
            //Check if another AssetCategory has the same name.
            UserGroup userGroup = userGroupService.findByName(editableGroupName);
            if (userGroup != null && !userGroup.equals(selectedUserGroup))
                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A User Group with that name already exists!"));

            if (FacesContext.getCurrentInstance().getMessageList("groupEditForm").isEmpty()) {
                try {
                    selectedUserGroup.setName(editableGroupName);
                    LogManager.logInfo("User Group updated, User Group Id and Name: " + selectedUserGroup.getId() + ", " + selectedUserGroup.getName());
                    selectedUserGroup = userGroupService.merge(selectedUserGroup);
                    resetSettings();
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Group Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.logError("Error while updating User Group" + selectedUserGroup.getName() + ": " + e.getMessage());
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating User Group: " + e.getMessage()));
                }
            }
        }
    }

    public void resetSettings() {
        if (!hasModifyPermission())
            return;

        if (selectedUserGroup != null) {
            editableGroupName = selectedUserGroup.getName();
        } else {
            editableGroupName = null;
        }
    }

    public void deleteSelectedGroup() {
        if (!hasModifyPermission())
            return;

        if (this.selectedUserGroup != null) {
            UserGroup parentGroup = selectedUserGroup.getParent();

            for (UserGroup child : selectedUserGroup.getChildren()) { //Reassign parents of the children of the node to be deleted.
                child.setParent(parentGroup);
                try {
                    userGroupService.merge(child);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.logError(e.getMessage());
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
                }
            }
            try {
                userGroupService.delete(selectedUserGroup.getId()); //Remove selected group from database
                LogManager.logInfo("User Group deleted, User Group Id and Name: " + selectedUserGroup.getId() + ", " + selectedUserGroup.getName());
                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group '" + selectedUserGroup.getName() + "' Deleted"));
                selectedUserGroup = null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.logError("Error while deleting User Group " + selectedUserGroup.getName() + ": " + e.getMessage());
                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }
        }
    }

    public void removeUserFromSelectedGroup(User user) throws Exception {
        if (!hasModifyPermission())
            return;

        if (user != null && user.getUserGroups().contains(selectedUserGroup)) {
            user.getUserGroups().remove(selectedUserGroup);
            userService.merge(user);
            LogManager.logInfo("Update user, removed from User group. User id and name: " + ", " + user.getId() + user.getFullname());
            selectedUserGroup = userGroupService.get(selectedUserGroup.getId());
            resetSettings();

            FacesContext.getCurrentInstance().addMessage("groupEditForm:usersTable", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + user.getUsername() + "' has been removed from '" + selectedUserGroup.getName() + "'"));
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        if (!hasModifyPermission())
            return;

        if (event.getTreeNode().getData() instanceof UserGroup)
            setSelectedUserGroup((UserGroup) event.getTreeNode().getData());
        else
            setSelectedUserGroup(null);
    }

    public UserGroup getSelectedUserGroup() {
        return selectedUserGroup;
    }

    public void setSelectedUserGroup(UserGroup selectedUserGroup) {
        if (!hasModifyPermission())
            return;

        this.selectedUserGroup = selectedUserGroup;
        if (newGroupController != null)
            newGroupController.setParentGroup(selectedUserGroup);

        resetSettings();
    }

    public String getEditableGroupName() {
        return editableGroupName;
    }

    public void setEditableGroupName(String editableGroupName) {
        this.editableGroupName = editableGroupName;
    }

    void setNewGroupController(NewGroupController newGroupController) {
        this.newGroupController = newGroupController;
    }
}
