/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.users;

import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.domain.entities.User;
import org.primefaces.model.TreeNode;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

public abstract class UserFieldSupplier {

    @Size(max = 64, message = "This may only be 64 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    protected String emailAddress;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    protected String firstName;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    protected String lastName;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    protected String phoneNumber;

    @Size(max = 256, message = "This may only be 256 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    protected String location;

    @Size(max = 32, message = "This may only be 32 characters long.")
    protected String password;

    @Size(max = 32, message = "This may only be 32 characters long.")
    protected String confirmPassword;

    protected boolean wantsEmailNotifications;

    protected TreeNode[] userGroupNodes;

    protected final Notification.Type[] notificationTypes = Notification.Type.values();

    protected Boolean[] notificationTypeSettings;

    /**
     * Resets the fields with the values of the supplied user, or null if the user is null.
     *
     * @param user The user to fill the fields with, or null if they should be filled with null.
     */
    protected void resetFields(User user) {
        if (user != null) {
            emailAddress = user.getEmailAddress();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            phoneNumber = user.getPhoneNumber();
            location = user.getLocation();
            notificationTypeSettings = new Boolean[user.getNotificationTypeSettings().size()];
            for(Notification.Type type : notificationTypes)
            {
                notificationTypeSettings[type.ordinal()] = user.getNotificationTypeSettings().get(type);
            }
        } else {
            emailAddress = null;
            firstName = null;
            lastName = null;
            phoneNumber = null;
            location = null;
            wantsEmailNotifications = true;
            notificationTypeSettings = null;
        }
        password = null;
        confirmPassword = null;
        userGroupNodes = null;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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

    public boolean getWantsEmailNotifications() {
        return wantsEmailNotifications;
    }

    public void setWantsEmailNotifications(boolean wantsEmailNotifications) { this.wantsEmailNotifications = wantsEmailNotifications; }

    public Notification.Type[] getNotificationTypes() {
        return notificationTypes;
    }

    public Boolean[] getNotificationTypeSettings() {
        return notificationTypeSettings;
    }
}
