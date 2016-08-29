/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.groups;

import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@RequestScoped
public class GroupTreeController implements Serializable {

    private HashMap<Integer, TreeNode> cache = new HashMap<>();

    @Inject
    private UserGroupService groupService;

    private TreeNode buildTree(Collection<UserGroup> currentlySelectedGroups, UserGroup userGroupToExclude, boolean allowRootSelection, boolean readOnly) {
        UserGroup rootGroup = groupService.getRootGroup();

        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode rootNode = new DefaultTreeNode(rootGroup);
        rootNode.setExpanded(true);
        rootNode.setSelectable(allowRootSelection);
        if (allowRootSelection) {
            TreeNode artificialRootNode = new DefaultTreeNode(rootGroup, rootNode);
            artificialRootNode.setExpanded(true);
            if (currentlySelectedGroups != null && currentlySelectedGroups.contains(rootGroup))
                artificialRootNode.setSelected(true);
            artificialRootNode.setSelectable(true);

            queue.add(artificialRootNode);
        } else
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
                    if (currentlySelectedGroups != null && currentlySelectedGroups.contains(child)) {
                        node.setSelected(true);
                    }
                    if (readOnly)
                        node.setSelectable(false);

                    queue.add(node);
                }
            }
        }
        return rootNode;
    }

    private TreeNode buildFilteredTree(UserGroup currentlySelectedGroup, Collection<UserGroup> allowedGroups, boolean includeChildren) {
        UserGroup rootGroup = groupService.getRootGroup();

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
                    TreeNode node = new DefaultTreeNode(child, currEntry);
                    node.setExpanded(true);
                    node.setSelectable(false);
                    if (currentlySelectedGroup != null && currentlySelectedGroup.equals(child))
                        node.setSelected(true);

                    queue.add(node);
                }
            }
        }

        queue = new LinkedList<>();
        queue.add(rootNode);

        while ((currEntry = queue.poll()) != null) {
            if (!currEntry.equals(rootNode)) {
                //noinspection SuspiciousMethodCalls
                if (!allowedGroups.contains(currEntry.getData()) && !currEntry.isSelectable()) {
                    for (TreeNode child : currEntry.getChildren()) {
                        child.setParent(currEntry.getParent());
                        currEntry.getParent().getChildren().add(child);
                        queue.add(child);
                    }
                    currEntry.getParent().getChildren().remove(currEntry);
                    currEntry.clearParent();
                } else {
                    currEntry.setSelectable(true);
                    if (includeChildren) {
                        for (TreeNode child : currEntry.getChildren()) {
                            child.setSelectable(true);
                            queue.add(child);
                        }
                    } else {
                        currEntry.getChildren().clear();
                    }
                }
            } else {
                for (TreeNode child : currEntry.getChildren()) {
                    queue.add(child);
                }
            }
        }

        return rootNode;
    }

    public TreeNode getTree(UserGroup currentlySelectedGroup, UserGroup userGroupToExclude, Boolean allowRootSelection, Boolean readOnly) {
        List<UserGroup> userGroups = null;

        if (currentlySelectedGroup != null) {
            userGroups = new ArrayList<>();
            userGroups.add(currentlySelectedGroup);
        }

        return getMultipleSelectedTree(userGroups, userGroupToExclude, allowRootSelection, readOnly);
    }

    public TreeNode getFilteredTree(UserGroup currentlySelectedGroup, List<UserGroup> allowedGroups, boolean includeChildren) {
        int hashCode = 0;

        hashCode += currentlySelectedGroup == null ? 0 : currentlySelectedGroup.hashCode();
        hashCode += allowedGroups == null ? 0 : allowedGroups.hashCode();
        hashCode += includeChildren ? 1 : 0;

        if (cache.containsKey(hashCode)) {
            return cache.get(hashCode);
        } else {
            TreeNode newTree = buildFilteredTree(currentlySelectedGroup, allowedGroups, includeChildren);
            cache.put(hashCode, newTree);

            return newTree;
        }
    }

    public TreeNode getMultipleSelectedTree(Collection<UserGroup> currentlySelectedGroups, UserGroup userGroupToExclude, Boolean allowRootSelection, Boolean readOnly) {
        int hashCode = 0;

        hashCode += currentlySelectedGroups == null ? 0 : currentlySelectedGroups.hashCode();
        hashCode += userGroupToExclude == null ? 0 : userGroupToExclude.hashCode();
        hashCode += allowRootSelection.hashCode();
        hashCode += readOnly.hashCode();

        if (cache.containsKey(hashCode)) {
            return cache.get(hashCode);
        } else {
            TreeNode newTree = buildTree(currentlySelectedGroups, userGroupToExclude, allowRootSelection, readOnly);
            cache.put(hashCode, newTree);

            return newTree;
        }
    }

    public void onDragDrop(TreeDragDropEvent event) throws Exception {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();

        if (dragNode.getData() instanceof UserGroup && dropNode.getData() instanceof UserGroup) {
            ((UserGroup) dragNode.getData()).setParent((UserGroup) dropNode.getData());
            groupService.merge((UserGroup) dragNode.getData());
        }

        invalidateTrees();
    }

    public void invalidateTrees() {
        cache.clear();
    }

}
