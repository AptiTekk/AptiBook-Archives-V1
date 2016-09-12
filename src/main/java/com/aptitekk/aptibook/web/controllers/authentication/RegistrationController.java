/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.authentication;

import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.services.EmailService;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.core.util.Sha256Helper;
import com.aptitekk.aptibook.web.controllers.settings.users.UserFieldSupplier;
import org.apache.commons.lang3.RandomStringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

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

    static final String REGISTRATION_VERIFICATION_PARAMETER = "verificationCode";

    public String register() {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(Sha256Helper.rawToSha(password));
        user.setWantsEmailNotifications(true);
        user.setVerified(false);

        //Generate verification code
        user.setVerificationCode(generateVerificationCode());

        //Create Registration Notification
        Notification notification = buildRegistrationNotification(user);

        if (!emailService.sendEmailNotification(notification)) {
            FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "We could not send an email to the Email Address provided. Please enter a valid Email Address!"));
            return null;
        } else {
            LogManager.logInfo("Email Confirmation has been sent to: " + user.getUsername());

            //Insert User
            try {
                userService.insert(user);

                LogManager.logInfo("New user has been created. User: " + user.getUsername());
                return "index?faces-redirect=true&includeViewParams=true&action=register&complete=true";
            } catch (Exception e) {
                LogManager.logError("Could not persist user with username: " + user.getUsername());
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("registerForm:username", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Internal Server Error occurred while trying to register your account. Please try again later!"));
                return null;
            }
        }
    }

    private String generateVerificationCode() {
        String verificationCode;
        do {
            verificationCode = RandomStringUtils.randomAlphanumeric(5);
        } while (userService.findByCode(verificationCode) != null);
        return verificationCode;
    }

    private Notification buildRegistrationNotification(User user) {
        Notification notification = new Notification();
        notification.setSubject("Registration Verification");
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String file = origRequest.getContextPath() + "/" + tenantSessionService.getCurrentTenant().getSlug() + origRequest.getServletPath();
        try {
            URL reconstructedURL = new URL(origRequest.getScheme(),
                    origRequest.getServerName(),
                    origRequest.getServerPort(),
                    file);
            System.out.println(reconstructedURL.toString());
            notification.setBody("<p>Hi! Someone (hopefully you) has registered an account with AptiBook using this email address. " +
                    "To cut down on spam, all we ask is that you click the link below to verify your account.</p>" +
                    "<p>If you did not intend to register with AptiBook, simply ignore this email and have a nice day!</p>" +
                    "<a href='" + reconstructedURL.toString() + "?" + REGISTRATION_VERIFICATION_PARAMETER + "=" + user.getVerificationCode() + "'" + ">Verify Account</a>");
            notification.setUser(user);
            return notification;
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
