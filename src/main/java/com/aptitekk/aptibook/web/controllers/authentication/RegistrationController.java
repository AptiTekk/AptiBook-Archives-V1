/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.authentication;

import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.services.EmailService;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.domain.services.TenantService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.sparkpost.exception.SparkPostException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.URL;

@Named
@ViewScoped
public class RegistrationController implements Serializable {

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private EmailService emailService;

    @Inject
    private UserService userService;

    @Inject
    private TenantSessionService tenantSessionService;

    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;


    public void register() throws Exception {
        if (username == null || password == null || confirmPassword == null || firstName == null || lastName == null) {
            FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Complete all required forms"));
            return;
        }
        if (password.equals(confirmPassword)) {
            if (passwordRegex(password)) {
                byte[] passwordByte = password.getBytes();
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordByte);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setWantsEmailNotifications(true);
                String verificationCode = RandomStringUtils.randomAlphanumeric(5);
                while (userService.findByCode(verificationCode) != null) {
                    verificationCode = RandomStringUtils.randomAlphanumeric(5);
                }
                user.setVerificationcode(RandomStringUtils.randomAlphanumeric(5));
                if (userService.findByName(username) == null) {
                    userService.insert(user);
                    LogManager.logInfo("New user has been created. User: " + user.getUsername());
                } else {
                    FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An account for that email has already been made."));
                    LogManager.logInfo("User already exists. User: " + user.getUsername());
                    return;
                }
                Notification notification = new Notification();
                notification.setSubject("Registration Confirmation");
                HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
                String file = origRequest.getContextPath()+ "/"+ tenantSessionService.getCurrentTenant().getSlug() + origRequest.getServletPath();
                //I dont think we will ever need this:
              /*  if (origRequest.getQueryString() != null) {
                    file += '?' + origRequest.getQueryString();
                }*/
                URL reconstructedURL = new URL(origRequest.getScheme(),
                        origRequest.getServerName(),
                        origRequest.getServerPort(),
                        file);
                System.out.println(reconstructedURL.toString());
                notification.setBody("<a href='" + reconstructedURL.toString() + "?" + "id=" + user.getVerificationcode() + "'" + ">Confirm AptiBook Registration</a>");
                notification.setUser(user);
                try {
                    emailService.sendEmailNotification(notification);
                    LogManager.logInfo("Email Confirmation has been sent to: " + notification.getUser().getUsername());
                } catch (SparkPostException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public boolean passwordRegex(String password) {
        if (password.length() > 6 && password.length() < 20) {
            //Nest if here if more regex conditions are needed
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Password must be between 6 - 20 characters long"));
        }
        return false;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
