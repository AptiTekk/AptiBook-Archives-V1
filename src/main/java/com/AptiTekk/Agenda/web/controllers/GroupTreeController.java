package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.UserGroupService;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.*;

@ManagedBean
@RequestScoped
public class GroupTreeController {

    private TreeNode rootNode;

    private UserGroup initiallySelectedGroup;
    private UserGroup initiallyExcludedGroup;
    private boolean initialSelectionAllowance;

    @Inject
    private UserGroupService groupService;

    public void buildTree(UserGroup currentlySelectedGroup, UserGroup userGroupToExclude, boolean allowRootSelection) {
        initiallySelectedGroup = currentlySelectedGroup;
        initiallyExcludedGroup = userGroupToExclude;
        initialSelectionAllowance = allowRootSelection;

        UserGroup rootGroup = groupService.getRootGroup();

        Queue<TreeNode> queue = new LinkedList<>();
        rootNode = new DefaultTreeNode(rootGroup);
        rootNode.setExpanded(true);
        if(currentlySelectedGroup != null && rootGroup.equals(currentlySelectedGroup))
            rootNode.setSelected(true);
        queue.add(rootNode);

        TreeNode currEntry;

        while ((currEntry = queue.poll()) != null) {
            UserGroup group = currEntry.getData() == null ? null : (UserGroup) currEntry.getData();

            if (group != null) {
                for (UserGroup child : group.getChildren()) {
                    if (userGroupToExclude != null && child.equals(userGroupToExclude))
                        continue;

                    TreeNode node = new DefaultTreeNode(child, currEntry);
                    node.setExpanded(true);
                    if(currentlySelectedGroup != null && child.equals(currentlySelectedGroup))
                        node.setSelected(true);
                    queue.add(node);
                }
            }
        }

        rootNode.setSelectable(allowRootSelection);
    }

    public TreeNode getTree(UserGroup currentlySelectedGroup, UserGroup userGroupToExclude, boolean allowRootSelection) {
        System.out.println("Building tree with data: ");
        if(currentlySelectedGroup != null)
            System.out.println(currentlySelectedGroup.getName());
        if(userGroupToExclude != null)
            System.out.println(userGroupToExclude.getName());
        System.out.println(allowRootSelection);

        boolean createNewTree = false;

        if (rootNode != null)
        {
            if(initiallySelectedGroup != null)
            {
                if(currentlySelectedGroup == null)
                    createNewTree = true;
                else if(!initiallySelectedGroup.equals(currentlySelectedGroup))
                    createNewTree = true;
            }

            if(initiallyExcludedGroup != null)
            {
                if(userGroupToExclude == null)
                    createNewTree = true;
                else if(!initiallyExcludedGroup.equals(userGroupToExclude))
                    createNewTree = true;
            }

            if(initialSelectionAllowance != allowRootSelection)
                createNewTree = true;
        }
        else
            createNewTree = true;

        if(createNewTree)
        {
            //We need to make a new tree, then return it.
            System.out.println("Building new tree");
            buildTree(currentlySelectedGroup, userGroupToExclude, allowRootSelection);
            return rootNode;
        }
        else
        {
            //We already generated the tree. Return it.
            System.out.println("Already Built");
            return rootNode;
        }
    }

}
