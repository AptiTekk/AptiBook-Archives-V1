/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.UserGroupService;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@ManagedBean
@ViewScoped
public class GroupEditController {

    @Inject
    private UserGroupService groupService;

    @ManagedProperty(value = "#{GroupController}")
    private GroupController groupController;

    private TreeNode selectedNode;

    private String editableGroupName = "";
    private TreeNode editableGroupParent;

    public void setGroupController(GroupController groupController) {
        this.groupController = groupController;
    }

    @PostConstruct
    public void init()
    {
        resetSettings();
    }

    public void updateSettings() {
        UserGroup group = groupService.findByName(editableGroupName);
        if(group != null && !group.equals((UserGroup) selectedNode.getData()))
            FacesContext.getCurrentInstance().addMessage(":groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Group with that name already exists!"));
        else if(editableGroupName.isEmpty())
            FacesContext.getCurrentInstance().addMessage(":groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Group's name cannot be empty!"));
        else if(!editableGroupName.matches("[A-Za-z0-9 #]+"))
            FacesContext.getCurrentInstance().addMessage(":groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Group's name may only contain A-Z, a-z, 0-9, #, and space!"));

        if(FacesContext.getCurrentInstance().getMessageList(":groupEditForm").isEmpty())
        {
            UserGroup selectedGroup = (UserGroup) selectedNode.getData();
            selectedGroup.setName(editableGroupName);
            groupService.merge(selectedGroup);
            groupController.buildTree();

            FacesContext.getCurrentInstance().addMessage(":groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group Updated"));
        }
    }

    public void resetSettings()
    {
        if(selectedNode != null)
        {
            editableGroupName = ((UserGroup)selectedNode.getData()).getName();
            editableGroupParent = selectedNode.getParent();
        }
        else
        {
            editableGroupName = "";
            editableGroupParent = null;
        }
    }

    public void deleteSelectedGroup() {
        if (this.selectedNode != null) {
            UserGroup selectedGroup = (UserGroup) selectedNode.getData();
            UserGroup parentGroup = (UserGroup) selectedNode.getParent().getData();

            for (TreeNode child : selectedNode.getChildren()) { //Reassign parents of the children of the node to be deleted.
                UserGroup childGroup = (UserGroup) child.getData();
                childGroup.setParent(parentGroup);
                groupService.merge(childGroup);
            }

            System.out.println("Parent ID: "+parentGroup.getId());
            System.out.println("Selected ID: "+selectedGroup.getId());

            groupService.delete(selectedGroup.getId()); //Remove selected group from database
            selectedNode = null;

            FacesContext.getCurrentInstance().addMessage(":groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group Deleted"));
            groupController.buildTree(); //Rebuild Tree
        }
    }

    public void addGroup() {
        UserGroup newGroup = new UserGroup();
        newGroup.setName("New Group");
        UserGroup parentGroup = selectedNode == null ? (UserGroup) groupController.getRoot().getData() : (UserGroup) selectedNode.getData();
        newGroup.setParent(parentGroup);
        parentGroup.getChildren().add(newGroup);
        groupService.insert(newGroup);
        groupService.merge(parentGroup);

        FacesContext.getCurrentInstance().addMessage(":groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group Added"));
        groupController.buildTree();
    }

    public UserGroup getData(TreeNode node) {
        return node == null ? null : (UserGroup) node.getData();
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public UserGroup getSelectedGroup()
    {
        if(selectedNode != null)
            return (UserGroup) selectedNode.getData();
        return null;
    }

    public String getEditableGroupName() {
        return editableGroupName;
    }

    public void setEditableGroupName(String editableGroupName) {
        this.editableGroupName = editableGroupName;
    }

    public TreeNode getEditableGroupParent() {
        return editableGroupParent;
    }

    public void setEditableGroupParent(TreeNode editableGroupParent) {
        this.editableGroupParent = editableGroupParent;
    }
}
