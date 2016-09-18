/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.users;

import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.domain.services.EmailService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.core.util.Sha256Helper;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import org.apache.http.client.utils.URIBuilder;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Named
@ViewScoped
public class EditUserController extends UserFieldSupplier implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    @Inject
    private EmailService emailService;

    @Inject
    private TenantSessionService tenantSessionService;

    private ArrayList<User> pendingUsers = new ArrayList<>();
    private ArrayList<User> approvedUsers = new ArrayList<>();
    private User selectedUser;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }
        refreshUserLists();
        resetFields();
        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_USERS);
    }

    public void decideUserApproval(boolean approved, User user) {
        if (approved) {
            user.setUserState(User.State.APPROVED);
            try {
                userService.merge(user);
                LogManager.logInfo("User approved and merged, User: " + user.getUsername());
                FacesContext.getCurrentInstance().addMessage("userTablesForm",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + user.getUsername() + "' has been Approved."));
                refreshUserLists();
                Notification notification = new Notification(user, "AptiBook Registration Approved", "<p>Congrats! You have been Approved to use AptiBook!</p>"
                        + "<a href='" + tenantSessionService.buildURI("index.xhtml", null) + "'" + ">Login</a>");
                emailService.sendEmailNotification(notification);
            } catch (Exception e) {
                LogManager.logError("Error approving user. User: " + user.getUsername());
                e.printStackTrace();
            }
        } else {
            try {
                userService.delete(user.getId());
                LogManager.logInfo("User deleted, User: " + user.getUsername());
                FacesContext.getCurrentInstance().addMessage("userTablesForm",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + user.getUsername() + "' has been Rejected."));
                refreshUserLists();
                HashMap<String,String> queryParams = new HashMap<>();
                queryParams.put("action", "register");
                tenantSessionService.buildURI("index.xhtml", queryParams);
                Notification notification = new Notification(user, "AptiBook Registration Rejected", "<p>Your registration request for AptiBook has been denied. "
                        + "If you believe this is a mistake, please contact your System Administrator. "
                        + "You may submit another registration request here: </p>"
                        + "<a href='" + tenantSessionService.buildURI("index.xhtml", queryParams) + "'" + ">Register</a>");
                emailService.sendEmailNotification(notification);
            } catch (Exception e) {
                LogManager.logError("Error deleting user, User: " + user.getUsername());
                e.printStackTrace();
            }
        }
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.USERS);
    }

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.USERS_MODIFY_ALL);
    }

    void refreshUserLists() {
        List<User> users = userService.getAll();
        pendingUsers.clear();
        approvedUsers.clear();

        for (User user : users) {
            if (user.getUserState() == User.State.PENDING) {
                pendingUsers.add(user);
            }
            if (user.getUserState() == User.State.APPROVED) {
                approvedUsers.add(user);
            }
        }
    }

    public void resetFields() {
        resetFields(selectedUser);
    }

    public void updateSettings() {
        if (!hasModifyPermission())
            return;

        if ((password == null && confirmPassword != null) || (password != null && !password.equals(confirmPassword))) {
            FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Passwords do not match."));
            password = null;
            setConfirmPassword(null);
        }

        if (FacesContext.getCurrentInstance().getMessageList("userEditForm").isEmpty()) {
            selectedUser.setUsername(username);
            selectedUser.setFirstName(firstName);
            selectedUser.setLastName(lastName);
            selectedUser.setPhoneNumber(phoneNumber);
            selectedUser.setLocation(location);
            selectedUser.setWantsEmailNotifications(wantsEmailNotifications);

            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Personal Information Updated."));

            if (password != null && FacesContext.getCurrentInstance().getMessageList("userEditForm:passwordEdit").isEmpty()) {
                selectedUser.setPassword(Sha256Helper.rawToSha(password));
                FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Password Changed Successfully."));
            }

            if (userGroupNodes != null) {
                boolean changesDiscovered = false;

                List<UserGroup> selectedUserGroups = new ArrayList<>();
                List<TreeNode> editableUserGroupNodesList = Arrays.asList(userGroupNodes);
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
                LogManager.logInfo("User updated, user Id and Name: " + selectedUser.getId() + ", " + selectedUser.getFullname());
                refreshUserLists();
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.logError("Error while updating User Settings for " + selectedUser.getUsername() + ": " + e.getMessage());
                FacesContext.getCurrentInstance().addMessage("userEditForm",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating User Settings: " + e.getMessage()));
            }

        }
    }

    public void deleteSelectedUser() {
        if (!hasModifyPermission())
            return;

        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (userService.get(getSelectedUser().getId()) != null) {
                context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Deleted!"));
                userService.delete(getSelectedUser().getId());
                LogManager.logInfo("User deleted, user Id and Name: " + getSelectedUser().getId() + ", " + getSelectedUser().getFullname());
                setSelectedUser(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.logError("Error While Deleting User " + selectedUser.getUsername() + ": " + e.getMessage());
            context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Deleting User!"));
        }

        refreshUserLists();
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        if (!hasModifyPermission())
            return;

        this.selectedUser = selectedUser;
        resetFields();
    }

    public ArrayList<User> getApprovedUsers() {
        return approvedUsers;
    }

    public void setApprovedUsers(ArrayList<User> approvedUsers) {
        this.approvedUsers = approvedUsers;
    }

    public ArrayList<User> getPendingUsers() {
        return pendingUsers;
    }

    public void setPendingUsers(ArrayList<User> pendingUsers) {
        this.pendingUsers = pendingUsers;
    }
}
