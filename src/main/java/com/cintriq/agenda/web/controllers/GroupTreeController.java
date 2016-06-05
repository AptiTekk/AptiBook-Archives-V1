package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.UserGroupService;
import com.cintriq.agenda.core.entity.UserGroup;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.*;

@ManagedBean(name = "GroupTreeController")
@RequestScoped
public class GroupTreeController {

    private HashMap<Integer, TreeNode> cache = new HashMap<>();

    @Inject
    private UserGroupService groupService;

    private TreeNode buildTree(UserGroup currentlySelectedGroup, UserGroup userGroupToExclude, boolean allowRootSelection) {
        UserGroup rootGroup = groupService.getRootGroup();

        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode rootNode = new DefaultTreeNode(rootGroup);
        rootNode.setExpanded(true);
        if (currentlySelectedGroup != null && rootGroup.equals(currentlySelectedGroup))
            rootNode.setSelected(true);
        queue.add(rootNode);

        TreeNode currEntry;

        while ((currEntry = queue.poll()) != null) {
            UserGroup group = currEntry.getData() == null ? null : (UserGroup) currEntry.getData();

            if (group != null) {
                for (UserGroup child : group.getChildren()) {
                    if (userGroupToExclude != null && child.equals(userGroupToExclude)) {
                        continue;
                    }

                    TreeNode node = new DefaultTreeNode(child, currEntry);
                    node.setExpanded(true);
                    if (currentlySelectedGroup != null && child.equals(currentlySelectedGroup)) {
                        node.setSelected(true);
                    }
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

        if (cache.containsKey(hashCode)) {
            return cache.get(hashCode);
        } else {
            TreeNode newTree = buildTree(currentlySelectedGroup, userGroupToExclude, allowRootSelection);
            cache.put(hashCode, newTree);

            return newTree;
        }
    }

}