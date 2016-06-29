package com.aptitekk.agenda.web.controllers.users;

import com.aptitekk.agenda.core.UserGroupService;
import com.aptitekk.agenda.core.entity.UserGroup;
import org.primefaces.event.NodeSelectEvent;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ManagedBean(name = "NewGroupController")
@ViewScoped
public class NewGroupController {

    @Inject
    private UserGroupService userGroupService;

    @ManagedProperty(value = "#{GroupEditController}")
    private GroupEditController groupEditController;

    public void setGroupEditController(GroupEditController groupEditController) {
        this.groupEditController = groupEditController;
        if(groupEditController != null)
            groupEditController.setNewGroupController(this);
    }

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String name;
    private UserGroup parentGroup;

    public void addGroup() {
        try {
            UserGroup newGroup = new UserGroup();
            newGroup.setName(name);

            if (parentGroup != null)
                newGroup.setParent(parentGroup);
            else
                newGroup.setParent(userGroupService.getRootGroup());

            userGroupService.insert(newGroup);

            if (groupEditController != null)
                groupEditController.setSelectedUserGroup(newGroup);

            name = null;

            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Group '" + newGroup.getName() + "' Added"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("groupEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
        }
    }

    public void onParentGroupSelected(NodeSelectEvent event) {
        if (event.getTreeNode() != null && event.getTreeNode().getData() instanceof UserGroup)
            this.parentGroup = (UserGroup) event.getTreeNode().getData();
        else
            this.parentGroup = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(UserGroup parentGroup) {
        this.parentGroup = parentGroup;
    }
}
