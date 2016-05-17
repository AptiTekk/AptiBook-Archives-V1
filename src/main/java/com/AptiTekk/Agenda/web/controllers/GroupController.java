package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.UserGroupService;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import org.primefaces.component.tree.Tree;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.*;

@ManagedBean
@ViewScoped
public class GroupController {

    private TreeNode root;

    private UserGroup lastExcludedUserGroup;
    private TreeNode parentSelectionRoot;

    @Inject
    private UserGroupService groupService;

    @PostConstruct
    public void init() {
        buildTree();
    }

    public void buildTree() {
        UserGroup rootGroup = groupService.getRootGroup();
        this.root = traverseChildren(rootGroup, null);
    }

    public void buildParentSelectionTree() {
        UserGroup rootGroup = groupService.getRootGroup();
        this.parentSelectionRoot = traverseChildren(rootGroup, lastExcludedUserGroup);
        parentSelectionRoot.setSelectable(true);
    }

    private TreeNode traverseChildren(UserGroup rootGroup, UserGroup optionalExclusionGroup) {
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode rootNode = new DefaultTreeNode(rootGroup);
        rootNode.setExpanded(true);
        rootNode.setSelectable(false);
        queue.add(rootNode);

        TreeNode currEntry;

        while ((currEntry = queue.poll()) != null) {
            UserGroup group = currEntry.getData() == null ? null : (UserGroup) currEntry.getData();

            if (group != null) {
                for (UserGroup child : group.getChildren()) {
                    if (optionalExclusionGroup != null && child.equals(optionalExclusionGroup))
                        continue;

                    TreeNode node = new DefaultTreeNode(child, currEntry);
                    node.setExpanded(true);
                    node.setSelectable(true);
                    queue.add(node);
                }
            }
        }

        return rootNode;
    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getParentSelectionRoot(UserGroup userGroupToExclude) {
        if (parentSelectionRoot != null) //Check to see if we have already generated the tree we are requesting.
        {
            if (userGroupToExclude == null && lastExcludedUserGroup == null)
                return parentSelectionRoot;
            else if (userGroupToExclude != null && lastExcludedUserGroup != null && lastExcludedUserGroup.equals(userGroupToExclude))
                return parentSelectionRoot;
        }

        //We need to make a new tree for this group
        lastExcludedUserGroup = userGroupToExclude;
        buildParentSelectionTree();

        return parentSelectionRoot;
    }

}
