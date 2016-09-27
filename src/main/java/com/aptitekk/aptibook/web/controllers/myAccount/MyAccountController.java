/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.myAccount;

import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.util.Sha256Helper;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.controllers.settings.users.UserFieldSupplier;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

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
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setLocation(location);
            user.setWantsEmailNotifications(wantsEmailNotifications);

            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Personal Information Updated."));

            if (password != null && FacesContext.getCurrentInstance().getMessageList("userEditForm:passwordEdit").isEmpty()) {
                user.setPassword(Sha256Helper.rawToSha(password));
                FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Password Changed Successfully."));
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
