/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.authentication;

import com.aptitekk.aptibook.core.crypto.PasswordStorage;
import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.services.*;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.web.util.FacesURIBuilder;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.settings.users.UserFieldSupplier;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;

@Named
@ViewScoped
public class RegistrationController extends UserFieldSupplier implements Serializable {

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private EmailService emailService;

    @Inject
    private UserService userService;

    @Inject
    private TenantSessionService tenantSessionService;

    @Inject
    private PermissionService permissionService;

    static final String REGISTRATION_VERIFICATION_PARAMETER = "verificationCode";

    private boolean registrationEnabled;

    @PostConstruct
    private void init() {
        registrationEnabled = Boolean.parseBoolean(propertiesService.getPropertyByKey(Property.Key.REGISTRATION_ENABLED).getPropertyValue());
    }

    public String register() {
        try {
            User user = new User();
            user.setEmailAddress(emailAddress);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setHashedPassword(PasswordStorage.createHash(password));
            user.setVerified(false);
            user.setUserState(User.State.PENDING);

            //Generate verification code
            user.setVerificationCode(generateVerificationCode());

            //Create Registration Notification
            HashMap<String, String> queryParams = new HashMap<>();
            queryParams.put(REGISTRATION_VERIFICATION_PARAMETER, user.getVerificationCode());
            boolean emailSent = emailService.sendEmailNotification(user.getEmailAddress(), "Registration Verification",
                    "<p>Hi! Someone (hopefully you) has registered an account with AptiBook using this email address. " +
                            "To cut down on spam, all we ask is that you click the link below to verify your account.</p>" +
                            "<p>If you did not intend to register with AptiBook, simply ignore this email and have a nice day!</p>" +
                            "<a href='" + FacesURIBuilder.buildTenantURI(tenantSessionService.getCurrentTenant(), "index.xhtml", queryParams) + "'" + ">Verify Account</a>");

            if (!emailSent) {
                FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "We could not send an email to the Email Address provided. Please enter a valid Email Address!"));
                return null;
            } else {
                LogManager.logInfo(getClass(), "Email Confirmation has been sent to: " + user.getEmailAddress());

                //Insert User
                try {
                    userService.insert(user);

                    LogManager.logInfo(getClass(), "New user has been created. User: " + user.getEmailAddress());
                    return "index?faces-redirect=true&includeViewParams=true&action=register&complete=true";
                } catch (Exception e) {
                    LogManager.logException(getClass(), e, "Could not persist user");
                    FacesContext.getCurrentInstance().addMessage("registerForm:emailAddress", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
                    return null;
                }
            }
        } catch (PasswordStorage.CannotPerformOperationException e) {
            LogManager.logException(getClass(), e, "Could not add new user on registration due to crypto error");
            FacesContext.getCurrentInstance().addMessage("registerForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
            return null;
        }
    }

    private String generateVerificationCode() {
        String verificationCode;
        do {
            verificationCode = RandomStringUtils.randomAlphanumeric(5);
        } while (userService.findByCode(verificationCode) != null);
        return verificationCode;
    }

    public boolean isRegistrationEnabled() {
        return registrationEnabled;
    }
}
