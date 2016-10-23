/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.users;

import com.aptitekk.aptibook.core.crypto.PasswordStorage;
import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.domain.services.EmailService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.web.util.FacesURIBuilder;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
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
            authenticationController.invokeUserRedirect();
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
                FacesContext.getCurrentInstance().addMessage("userTablesForm",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + user.getEmailAddress() + "' has been Approved."));
                refreshUserLists();
                emailService.sendEmailNotification(user.getEmailAddress(), "Registration Approved", "<p>Good News! Your account has been Approved, and you may now sign in to AptiBook!</p>"
                        + "<a href='" + FacesURIBuilder.buildTenantURI(tenantSessionService.getCurrentTenant(), "index.xhtml", null) + "'" + ">Click Here to Sign In</a>");
            } catch (Exception e) {
                LogManager.logException(getClass(), "Could not Approve User", e);
            }
        } else {
            try {
                userService.delete(user);
                FacesContext.getCurrentInstance().addMessage("userTablesForm",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + user.getEmailAddress() + "' has been Rejected."));
                refreshUserLists();
                HashMap<String, String> queryParams = new HashMap<>();
                queryParams.put("action", "register");
                FacesURIBuilder.buildTenantURI(tenantSessionService.getCurrentTenant(), "index.xhtml", queryParams);
                emailService.sendEmailNotification(user.getEmailAddress(), "Registration Rejected", "<p>Unfortunately, your account has been rejected. "
                        + "If you believe this is a mistake, please contact your System Administrators. ");
            } catch (Exception e) {
                LogManager.logException(getClass(), "Could not Delete User", e);
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
            selectedUser.setEmailAddress(emailAddress);
            selectedUser.setFirstName(firstName);
            selectedUser.setLastName(lastName);
            selectedUser.setPhoneNumber(phoneNumber);
            selectedUser.setLocation(location);

            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Personal Information Updated."));

            if (password != null && FacesContext.getCurrentInstance().getMessageList("userEditForm:passwordEdit").isEmpty()) {
                try {
                    selectedUser.setHashedPassword(PasswordStorage.createHash(password));
                    FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                            new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Password Changed Successfully."));
                } catch (PasswordStorage.CannotPerformOperationException e) {
                    LogManager.logException(getClass(), "Could not change User Password", e);
                    FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                            new FacesMessage(FacesMessage.SEVERITY_INFO, null, "An error occurred while changing your password. Your password has not been changed."));
                }
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
                refreshUserLists();
            } catch (Exception e) {
                LogManager.logException(getClass(), "Could not Update User Settings", e);
                FacesContext.getCurrentInstance().addMessage("userEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
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
                userService.delete(getSelectedUser());
                setSelectedUser(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            LogManager.logException(getClass(), "Could not Delete User", e);
            context.addMessage("userEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
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
