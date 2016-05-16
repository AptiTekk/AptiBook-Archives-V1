package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.UserGroupService;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import com.AptiTekk.Agenda.core.utilities.AgendaLogger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.List;

@ManagedBean
@ViewScoped
public class GroupController {

    private TreeNode root;

    @Inject
    private UserGroupService groupService;

    @PostConstruct
    public void init()
    {
        buildTree();
    }

    public void buildTree() {
        UserGroup rootGroup = groupService.getRootGroup();
        root = new DefaultTreeNode(rootGroup, null);
        root.setExpanded(true);
        root.setSelectable(false);

        attachChildren(rootGroup, root);
    }

    public void attachChildren(UserGroup parentGroup, TreeNode parent) {
        for (UserGroup child : parentGroup.getChildren()) {
            AgendaLogger.logVerbose("Adding children for UserGroup " + child.getName() + " (ID:" + child.getId() + ")");
            TreeNode nextGen = new DefaultTreeNode(child, parent);
            nextGen.setExpanded(true);
            attachChildren(child, nextGen);
        }
    }

    public void saveChildren(TreeNode parent) {
        for (TreeNode child : parent.getChildren()) {
            groupService.merge((UserGroup) child.getData());
            saveChildren(child);
            AgendaLogger.logVerbose("Persisting settings for UserGroup " + ((UserGroup) child.getData()).getName());
        }
    }

    public TreeNode getRoot() {
        return root;
    }

}
