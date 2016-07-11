/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.users;

import com.aptitekk.agenda.core.entities.User;
import org.primefaces.model.TreeNode;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public abstract class UserFieldSupplier {

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[A-Za-z0-9_-]+", message = "This may only contain letters, numbers, underscores, and hyphens.")
    String username;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String firstName;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String lastName;

    @Size(max = 64, message = "This may only be 64 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String email;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String phoneNumber;

    @Size(max = 256, message = "This may only be 256 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String location;

    @Size(max = 32, message = "This may only be 32 characters long.")
    String password;

    @Size(max = 32, message = "This may only be 32 characters long.")
    String confirmPassword;

    TreeNode[] userGroupNodes;

    /**
     * Resets the fields with the values of the supplied user, or null if the user is null.
     *
     * @param user The user to fill the fields with, or null if they should be filled with null.
     */
    void resetFields(User user) {
        if (user != null) {
            username = user.getUsername();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            email = user.getEmail();
            phoneNumber = user.getPhoneNumber();
            location = user.getLocation();
        } else {
            username = null;
            firstName = null;
            lastName = null;
            email = null;
            phoneNumber = null;
            location = null;
        }
        password = null;
        confirmPassword = null;
        userGroupNodes = null;
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
