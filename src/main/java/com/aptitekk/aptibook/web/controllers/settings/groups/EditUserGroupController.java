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

import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.domain.services.PermissionService;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;
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
public class EditUserGroupController implements Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private HelpController helpController;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private NewUserGroupController newUserGroupController;

    private UserGroup selectedUserGroup;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableGroupName;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }

        resetSettings();

        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_USER_GROUPS);
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
                    selectedUserGroup = userGroupService.merge(selectedUserGroup);
                    resetSettings();
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Group Updated"));
                } catch (Exception e) {
                    LogManager.logException(getClass(), "Could not update User Group", e);
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
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

            //Reassign parents of the children of the node to be deleted.
            for (UserGroup child : selectedUserGroup.getChildren()) {
                child.setParent(parentGroup);
                try {
                    userGroupService.merge(child);
                } catch (Exception e) {
                    LogManager.logException(getClass(), "Could not change UserGroup Parent", e);
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
                }
            }

            try {
                userGroupService.delete(selectedUserGroup.getId()); //Remove selected group from database
                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Group '" + selectedUserGroup.getName() + "' Deleted"));
                selectedUserGroup = null;
            } catch (Exception e) {
                LogManager.logException(getClass(), "Could not delete User Group", e);
                FacesContext.getCurrentInstance().addMessage("groupEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
            }
        }
    }

    public void removeUserFromSelectedGroup(User user) throws Exception {
        if (!hasModifyPermission())
            return;

        if (user != null && user.getUserGroups().contains(selectedUserGroup)) {
            user.getUserGroups().remove(selectedUserGroup);
            userService.merge(user);
            selectedUserGroup = userGroupService.get(selectedUserGroup.getId());
            resetSettings();

            FacesContext.getCurrentInstance().addMessage("groupEditForm:usersTable", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + user.getEmailAddress() + "' has been removed from '" + selectedUserGroup.getName() + "'"));
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
        if (newUserGroupController != null)
            newUserGroupController.setParentGroup(selectedUserGroup);

        resetSettings();
    }

    public String getEditableGroupName() {
        return editableGroupName;
    }

    public void setEditableGroupName(String editableGroupName) {
        this.editableGroupName = editableGroupName;
    }
}
