/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.UserGroupService;
import com.aptitekk.agenda.core.entity.UserGroup;
import org.primefaces.event.NodeSelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ManagedBean(name = "GroupEditController")
@ViewScoped
public class GroupEditController {

    @Inject
    private UserGroupService userGroupService;

    private UserGroup selectedUserGroup;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableGroupName;

    @PostConstruct
    public void init() {
        resetSettings();
    }

    public void updateSettings() {
        if (editableGroupName != null) {

            //Check if another Asset Type has the same name.
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
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating User Group: " + e.getMessage()));
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedUserGroup != null) {
            editableGroupName = selectedUserGroup.getName();
        } else {
            editableGroupName = null;
        }
    }

    public void deleteSelectedGroup() {
        if (this.selectedUserGroup != null) {
            UserGroup parentGroup = selectedUserGroup.getParent();

            for (UserGroup child : selectedUserGroup.getChildren()) { //Reassign parents of the children of the node to be deleted.
                child.setParent(parentGroup);
                try {
                    userGroupService.merge(child);
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
                }
            }
            try {
                userGroupService.delete(selectedUserGroup.getId()); //Remove selected group from database
                selectedUserGroup = null;

                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group Deleted"));
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }
        }
    }

    public void addGroup() {
        try {
            UserGroup newGroup = new UserGroup();
            newGroup.setName("New Group");
            UserGroup parentGroup = selectedUserGroup == null ? userGroupService.getRootGroup() : selectedUserGroup;
            newGroup.setParent(parentGroup);
            userGroupService.insert(newGroup);

            setSelectedUserGroup(newGroup);

            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group Added"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        if (event.getTreeNode().getData() instanceof UserGroup)
            setSelectedUserGroup((UserGroup) event.getTreeNode().getData());
        else
            setSelectedUserGroup(null);
    }

    public UserGroup getSelectedUserGroup() {
        return selectedUserGroup;
    }

    public void setSelectedUserGroup(UserGroup selectedUserGroup) {
        this.selectedUserGroup = selectedUserGroup;

        resetSettings();
    }

    public String getEditableGroupName() {
        return editableGroupName;
    }

    public void setEditableGroupName(String editableGroupName) {
        this.editableGroupName = editableGroupName;
    }
}
