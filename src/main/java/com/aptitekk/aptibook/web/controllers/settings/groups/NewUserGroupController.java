/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.groups;

import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;
import org.primefaces.event.NodeSelectEvent;

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
public class NewUserGroupController implements Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private EditUserGroupController editUserGroupController;

    @Inject
    private AuthenticationController authenticationController;

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.GROUPS_MODIFY_ALL);
    }

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String name;
    private UserGroup parentGroup;

    public void addGroup() {
        if (!hasModifyPermission())
            return;

        try {
            UserGroup newGroup = new UserGroup();
            newGroup.setName(name);

            if (parentGroup != null)
                newGroup.setParent(parentGroup);
            else
                newGroup.setParent(userGroupService.getRootGroup());

            userGroupService.insert(newGroup);
            if (editUserGroupController != null)
                editUserGroupController.setSelectedUserGroup(newGroup);

            name = null;

            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Group '" + newGroup.getName() + "' Added"));
        } catch (Exception e) {
            LogManager.logException(getClass(), e, "Could not add User Group");
            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, CommonFacesMessages.EXCEPTION_MESSAGE));
        }
    }

    public void onParentGroupSelected(NodeSelectEvent event) {
        if (!hasModifyPermission())
            return;

        if (event.getTreeNode() != null && event.getTreeNode().getData() instanceof UserGroup)
            this.parentGroup = (UserGroup) event.getTreeNode().getData();
        else
            this.parentGroup = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(UserGroup parentGroup) {
        this.parentGroup = parentGroup;
    }
}
