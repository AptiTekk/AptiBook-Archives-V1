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

    private HashMap<Integer, TreeNode> cache = new HashMap<>();

    @Inject
    private UserGroupService groupService;

    @PostConstruct
    public void init()
    {
        System.out.println("--------------- New Tree Controller ---------------");
    }

    private TreeNode buildTree(UserGroup currentlySelectedGroup, UserGroup userGroupToExclude, boolean allowRootSelection) {
        UserGroup rootGroup = groupService.getRootGroup();

        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode rootNode = new DefaultTreeNode(rootGroup);
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

        return rootNode;
    }

    public TreeNode getTree(UserGroup currentlySelectedGroup, UserGroup userGroupToExclude, Boolean allowRootSelection) {
        int hashCode = 0;

        hashCode += currentlySelectedGroup == null ? 0 : currentlySelectedGroup.hashCode();
        hashCode += userGroupToExclude == null ? 0 : userGroupToExclude.hashCode();
        hashCode += allowRootSelection.hashCode();

        if(cache.containsKey(hashCode))
        {
            System.out.println("Already Built");
            return cache.get(hashCode);
        }
        else
        {
            System.out.println("Creating new Tree");
            TreeNode newTree = buildTree(currentlySelectedGroup, userGroupToExclude, allowRootSelection);
            cache.put(hashCode, newTree);

            return newTree;
        }
    }

}
