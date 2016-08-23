/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.users;

import com.aptitekk.aptibook.core.entities.Permission;
import com.aptitekk.aptibook.core.entities.User;
import com.aptitekk.aptibook.core.entities.UserGroup;
import com.aptitekk.aptibook.core.entities.services.UserService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.core.util.Sha256Helper;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import org.primefaces.model.TreeNode;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named
@RequestScoped
public class NewUserController extends UserFieldSupplier implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private UserEditController userEditController;

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.USERS_MODIFY_ALL);
    }

    public void addUser() {
        if (!hasModifyPermission())
            return;

        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setLocation(location);
            newUser.setPassword(Sha256Helper.rawToSha(password));

            if (userGroupNodes != null) {
                List<UserGroup> selectedUserGroups = new ArrayList<>();
                List<TreeNode> userGroupNodesList = Arrays.asList(userGroupNodes);
                for (TreeNode node : userGroupNodesList) {

                    //Check to see if any parents are selected, and skip if they are.
                    //No need to select both parent and child.
                    TreeNode parent = node;
                    boolean skip = false;
                    while ((parent = parent.getParent()) != null) {
                        if (userGroupNodesList.contains(parent)) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip)
                        continue;

                    selectedUserGroups.add((UserGroup) node.getData());
                }

                newUser.setUserGroups(selectedUserGroups);
            }

            userService.insert(newUser);
            LogManager.logInfo("New User persisted, User Id and Name: " + newUser.getId() + ", " + newUser.getFullname());
            if (userService.get(newUser.getId()) != null) {
                FacesContext.getCurrentInstance().addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + newUser.getUsername() + "' Added!"));
                if (userEditController != null) {
                    userEditController.refreshUserList();
                    userEditController.setSelectedUser(newUser);
                }
            } else {
                throw new Exception("User not found!");
            }

            resetFields();
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.logError("Error While Adding User: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Adding User!"));
        }
    }

    private void resetFields() {
        if (!hasModifyPermission())
            return;

        resetFields(null);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public TreeNode[] getUserGroupNodes() {
        return userGroupNodes;
    }

    public void setUserGroupNodes(TreeNode[] userGroupNodes) {
        this.userGroupNodes = userGroupNodes;
    }
}
