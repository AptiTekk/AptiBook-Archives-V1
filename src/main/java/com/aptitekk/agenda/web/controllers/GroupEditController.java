/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.UserGroupService;
import com.aptitekk.agenda.core.entity.UserGroup;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@ManagedBean(name = "GroupEditController")
@ViewScoped
public class GroupEditController {

    @Inject
    private UserGroupService userGroupService;

    private UserGroup selectedGroup;

    private String editableGroupName = "";

    @PostConstruct
    public void init() {
        resetSettings();
    }

    public void updateSettings() {
        UserGroup group = userGroupService.findByName(editableGroupName);
        if (group != null && !group.equals(selectedGroup))
            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Group with that name already exists!"));
        else if (editableGroupName.isEmpty())
            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Group's name cannot be empty!"));
        else if (!editableGroupName.matches("[A-Za-z0-9 #]+"))
            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Group's name may only contain A-Z, a-z, 0-9, #, and space!"));

        if (FacesContext.getCurrentInstance().getMessageList("groupEditForm").isEmpty()) {
            try {
                selectedGroup.setName(editableGroupName);

                selectedGroup = userGroupService.merge(selectedGroup);
                resetSettings();

                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group Updated"));
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }
        }
    }

    public void resetSettings() {
        if (selectedGroup != null) {
            editableGroupName = selectedGroup.getName();
        } else {
            editableGroupName = "";
        }
    }

    public void deleteSelectedGroup() {
        if (this.selectedGroup != null) {
            UserGroup parentGroup = selectedGroup.getParent();

            for (UserGroup child : selectedGroup.getChildren()) { //Reassign parents of the children of the node to be deleted.
                child.setParent(parentGroup);
                try {
                    userGroupService.merge(child);
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
                }
            }
            try {
                userGroupService.delete(selectedGroup.getId()); //Remove selected group from database
                selectedGroup = null;

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
            UserGroup parentGroup = selectedGroup == null ? userGroupService.getRootGroup() : selectedGroup;
            newGroup.setParent(parentGroup);
            userGroupService.insert(newGroup);

            setSelectedGroup(newGroup);

            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group Added"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        if (event.getTreeNode().getData() instanceof UserGroup)
            setSelectedGroup((UserGroup) event.getTreeNode().getData());
        else
            setSelectedGroup(null);
    }

    public UserGroup getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(UserGroup selectedGroup) {
        this.selectedGroup = selectedGroup;

        resetSettings();
    }

    public String getEditableGroupName() {
        return editableGroupName;
    }

    public void setEditableGroupName(String editableGroupName) {
        this.editableGroupName = editableGroupName;
    }
}
