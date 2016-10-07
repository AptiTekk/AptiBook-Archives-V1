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
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.util.LogManager;
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
    private EditUserController editUserController;

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.USERS_MODIFY_ALL);
    }

    public void addUser() {
        if (!hasModifyPermission())
            return;

        try {
            User newUser = new User();
            newUser.setEmailAddress(emailAddress);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setLocation(location);
            newUser.setVerified(true);
            newUser.setUserState(User.State.APPROVED);
            newUser.setHashedPassword(PasswordStorage.createHash(password));

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
                FacesContext.getCurrentInstance().addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + newUser.getEmailAddress() + "' Added!"));
                if (editUserController != null) {
                    editUserController.refreshUserLists();
                    editUserController.setSelectedUser(newUser);
                }
            } else {
                throw new Exception("User not found!");
            }

            resetFields();
        } catch (Exception e) {
            LogManager.logException("Could not Create User", e);
            FacesContext.getCurrentInstance().addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Adding User!"));
        }
    }

    private void resetFields() {
        if (!hasModifyPermission())
            return;

        resetFields(null);
    }

}
