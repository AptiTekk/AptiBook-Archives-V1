/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.myAccount;

import com.aptitekk.aptibook.core.crypto.PasswordStorage;
import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.controllers.settings.users.UserFieldSupplier;
import sun.security.util.Password;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.aptitekk.aptibook.web.controllers.help.HelpController.Topic.USER_SETTINGS;

@Named
@ViewScoped
public class MyAccountController extends UserFieldSupplier implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    private User user;

    @PostConstruct
    public void init() {
        this.user = authenticationController.getAuthenticatedUser();
        resetFields();

        helpController.setCurrentTopic(USER_SETTINGS);
    }

    public void resetFields() {
        resetFields(user);
    }

    public void updateSettings() {
        if ((password == null && confirmPassword != null) || (password != null && !password.equals(confirmPassword))) {
            FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Passwords do not match."));
            password = null;
            setConfirmPassword(null);
        }

        if (FacesContext.getCurrentInstance().getMessageList("userEditForm").isEmpty()) {
            user.setEmailAddress(emailAddress);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setLocation(location);

            Map<Notification.Type, Boolean> notificationTypeSettings = new HashMap<>();
            for(Notification.Type type : getNotificationTypes())
            {
                notificationTypeSettings.put(type, getNotificationTypeSettings()[type.ordinal()]);
            }

            user.setNotificationTypeSettings(notificationTypeSettings);

            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Account Settings Updated."));

            if (password != null && FacesContext.getCurrentInstance().getMessageList("userEditForm:passwordEdit").isEmpty()) {
                try {
                    user.setHashedPassword(PasswordStorage.createHash(password));
                    FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                            new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Password Changed Successfully."));
                } catch (PasswordStorage.CannotPerformOperationException e) {
                    LogManager.logError("Could not change user password: "+e.getMessage());
                    FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                            new FacesMessage(FacesMessage.SEVERITY_INFO, null, "An error occurred while changing your password. Your password has not been changed."));
                }
            }

            try {
                user = userService.merge(user);
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("userEditForm",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating User Settings: " + e.getMessage()));
            }

        }
    }

    public User getUser() {
        return user;
    }
}
