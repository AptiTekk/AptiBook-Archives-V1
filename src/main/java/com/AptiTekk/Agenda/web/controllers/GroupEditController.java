/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.UserGroupService;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import com.AptiTekk.Agenda.core.utilities.AgendaLogger;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * @author kevint
 */
@ManagedBean
@ViewScoped
public class GroupEditController {

    private TreeNode root;

    private TreeNode selectedNode;

    @Inject
    private UserGroupService groupService;

    private String newGroupName;

    @PostConstruct
    public void init() {
        buildTree();
    }

    public void buildTree() {
        UserGroup rootGroup = groupService.findByName(UserGroupService.ROOT_GROUP_NAME);
        root = new DefaultTreeNode(rootGroup, null);

        attachChildren(rootGroup, this.root);
    }

    public void attachChildren(UserGroup parentGroup, TreeNode parent) {
        for (UserGroup child : parentGroup.getChildren()) {
            AgendaLogger.logVerbose("Adding children for UserGroup " + child.getName());
            TreeNode nextGen = new DefaultTreeNode(child, parent);
            attachChildren(child, nextGen);
        }
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }

    public void onDragDrop(TreeDragDropEvent event) {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();
        int dropIndex = event.getDropIndex();

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Dragged " + dragNode.getData(), "Dropped on " + dropNode.getData() + " at " + dropIndex);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    //-- Button Actions
    public void updateSettings() {
        saveChildren(root);
    }

    public void saveChildren(TreeNode parent) {
        for (TreeNode child : parent.getChildren()) {
            groupService.merge((UserGroup) child.getData());
            saveChildren(child);
            AgendaLogger.logVerbose("Persisting settings for UserGroup " + ((UserGroup) child.getData()).getName());
        }
    }

    public void resetTree() {
        root = null;
        buildTree();
    }

    public void deleteSelectedGroup() {
        if (this.selectedNode != null) {
            for (TreeNode child : selectedNode.getChildren()) {
                child.setParent(selectedNode.getParent());
                ((UserGroup) child.getData()).setParent(((UserGroup) selectedNode.getData()).getParent());
            }
            TreeNode parent = selectedNode.getParent();
            parent.getChildren().remove(selectedNode);
            selectedNode = null;

        }
    }

    public void addGroup() {
        UserGroup newGroup = new UserGroup();
        newGroup.setName("New Group");
        UserGroup parent = ((selectedNode == null) ? (UserGroup) root.getData() : (UserGroup) selectedNode.getData());
        newGroup.setParent(parent);
        parent.getChildren().add(newGroup);
        AgendaLogger.logVerbose("Setting New Group's parent to " + parent.getName());
        groupService.insert(newGroup);
        groupService.merge(parent);
        resetTree();
    }

}
