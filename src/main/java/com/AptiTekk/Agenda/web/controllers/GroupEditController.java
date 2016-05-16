/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.UserGroupService;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import com.AptiTekk.Agenda.core.utilities.AgendaLogger;
import org.primefaces.component.tree.Tree;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
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

    private TreeNode selectedNode;

    @Inject
    private UserGroupService groupService;

    @ManagedProperty(value = "#{GroupController}")
    private GroupController groupController;

    public void setGroupController(GroupController groupController) {
        this.groupController = groupController;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public UserGroup getData(TreeNode node) {
        return node == null ? null : (UserGroup) node.getData();
    }

    //-- Button Actions
    public void updateSettings() {
        //TODO
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

            //TODO: FacesMessages
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

        //TODO: FacesMessages
        groupController.buildTree();
    }

}
