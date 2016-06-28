package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.UserService;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.utilities.Sha256Helper;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "UserEditController")
@ViewScoped
public class UserEditController {

    @Inject
    private UserService userService;

    private User selectedUser;
    private List<User> users;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[A-Za-z0-9_-]+", message = "This may only contain letters, numbers, underscores, and hyphens.")
    private String editableUsername;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableFirstName;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableLastName;

    @Size(max = 64, message = "This may only be 64 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableEmail;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editablePhoneNumber;

    @Size(max = 256, message = "This may only be 256 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableLocation;

    @Size(max = 32, message = "This may only be 32 characters long.")
    private String newPassword;

    @Size(max = 32, message = "This may only be 32 characters long.")
    private String confirmPassword;

    private TreeNode[] editableUserGroupNodes;

    @PostConstruct
    public void init() {

        refreshUserList();
        resetSettings();
    }

    public void refreshUserList() {
        users = userService.getAll();
    }

    public void resetSettings() {
        if (this.selectedUser != null) {
            setEditableUsername(selectedUser.getUsername());
            setEditableFirstName(selectedUser.getFirstName());
            setEditableLastName(selectedUser.getLastName());
            setEditableEmail(selectedUser.getEmail());
            setEditablePhoneNumber(selectedUser.getPhoneNumber());
            setEditableLocation(selectedUser.getLocation());

            setNewPassword(null);
            setConfirmPassword(null);
        }
    }

    public void updateSettings() {
        if ((getNewPassword() == null && getConfirmPassword() != null) || (getNewPassword() != null && !getNewPassword().equals(getConfirmPassword()))) {
            FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Passwords do not match."));
            setNewPassword(null);
            setConfirmPassword(null);
        }

        if (FacesContext.getCurrentInstance().getMessageList("userEditForm").isEmpty()) {
            selectedUser.setUsername(editableUsername);
            selectedUser.setFirstName(editableFirstName);
            selectedUser.setLastName(editableLastName);
            selectedUser.setEmail(editableEmail);
            selectedUser.setPhoneNumber(getEditablePhoneNumber());
            selectedUser.setLocation(editableLocation);

            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Personal Information Updated."));

            if (getNewPassword() != null && FacesContext.getCurrentInstance().getMessageList("userEditForm:passwordEdit").isEmpty()) {
                selectedUser.setPassword(Sha256Helper.rawToSha(getNewPassword()));
                FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Password Changed Successfully."));
            }

            if (editableUserGroupNodes != null) {
                boolean changesDiscovered = false;

                List<UserGroup> selectedUserGroups = new ArrayList<>();
                List<TreeNode> editableUserGroupNodesList = Arrays.asList(editableUserGroupNodes);
                for (TreeNode node : editableUserGroupNodesList) {

                    //Check to see if any parents are selected, and skip if they are.
                    //No need to select both parent and child.
                    TreeNode parent = node;
                    boolean skip = false;
                    while ((parent = parent.getParent()) != null) {
                        if (editableUserGroupNodesList.contains(parent)) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip)
                        continue;

                    if (node.getData() instanceof UserGroup) {
                        UserGroup userGroup = (UserGroup) node.getData();
                        selectedUserGroups.add(userGroup);
                        if (!selectedUser.getUserGroups().contains(userGroup))
                            changesDiscovered = true;
                    }
                }

                for (UserGroup userGroup : selectedUser.getUserGroups())
                    if (!selectedUserGroups.contains(userGroup))
                        changesDiscovered = true;

                if (changesDiscovered) {
                    selectedUser.setUserGroups(selectedUserGroups);
                    FacesContext.getCurrentInstance().addMessage("userEditForm:memberGroups",
                            new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Group Memberships Updated Successfully."));
                }
            }

            try {
                selectedUser = userService.merge(selectedUser);
                refreshUserList();
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("userEditForm",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating User Settings: " + e.getMessage()));
            }

        }
    }

    public void addNewUser() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            User newUser = new User();
            newUser.setUsername("new-user");
            userService.insert(newUser);

            if (userService.get(newUser.getId()) != null) {
                context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "New User Added!"));
                setSelectedUser(newUser);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Adding User!"));
        }

        refreshUserList();
    }

    public void deleteSelectedUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (userService.get(getSelectedUser().getId()) != null) {
                context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Deleted!"));
                userService.delete(getSelectedUser().getId());
                setSelectedUser(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Deleting User!"));
        }

        refreshUserList();
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
        resetSettings();
    }

    public List<User> getUsers() {
        return users;
    }

    public String getEditableUsername() {
        return editableUsername;
    }

    public void setEditableUsername(String editableUsername) {
        this.editableUsername = editableUsername;
    }

    public String getEditableFirstName() {
        return editableFirstName;
    }

    public void setEditableFirstName(String editableFirstName) {
        this.editableFirstName = editableFirstName;
    }

    public String getEditableLastName() {
        return editableLastName;
    }

    public void setEditableLastName(String editableLastName) {
        this.editableLastName = editableLastName;
    }

    public String getEditableEmail() {
        return editableEmail;
    }

    public void setEditableEmail(String editableEmail) {
        this.editableEmail = editableEmail;
    }

    public String getEditablePhoneNumber() {
        return editablePhoneNumber;
    }

    public void setEditablePhoneNumber(String editablePhoneNumber) {
        this.editablePhoneNumber = editablePhoneNumber;
    }

    public String getEditableLocation() {
        return editableLocation;
    }

    public void setEditableLocation(String editableLocation) {
        this.editableLocation = editableLocation;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setEditableUserGroupNodes(TreeNode[] editableUserGroupNodes) {
        this.editableUserGroupNodes = editableUserGroupNodes;
    }

    public TreeNode[] getEditableUserGroupNodes() {
        return editableUserGroupNodes;
    }
}
