package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.UserGroupService;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
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
        if (allowRootSelection) {
            TreeNode artificialRootNode = new DefaultTreeNode(rootGroup, rootNode);
            artificialRootNode.setExpanded(true);
            if (currentlySelectedGroup != null && rootGroup.equals(currentlySelectedGroup))
                artificialRootNode.setSelected(true);
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

    public void onDragDrop(TreeDragDropEvent event) throws Exception {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();

        if(dragNode.getData() instanceof UserGroup && dropNode.getData() instanceof UserGroup) {
            ((UserGroup) dragNode.getData()).setParent((UserGroup) dropNode.getData());
            groupService.merge((UserGroup) dragNode.getData());
        }

        invalidateTrees();
    }

    public void invalidateTrees()
    {
        cache.clear();
    }

}
