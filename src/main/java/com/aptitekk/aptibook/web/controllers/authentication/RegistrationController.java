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
import com.sparkpost.exception.SparkPostException;
import org.apache.commons.lang3.RandomStringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class RegistrationController implements Serializable {

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private TenantSessionService tenantSessionService;

    @Inject
    private EmailService emailService;

    @Inject
    private UserService userService;

    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;


    public void register() {
        if (username == null || password == null || confirmPassword == null || firstName == null || lastName == null) {
            FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Complete all required forms"));
            return;
        }
        if (password.equals(confirmPassword)) {
            //send link
            if(passwordRegex(password)) {
                byte[] passwordByte = password.getBytes();
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordByte);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setWantsEmailNotifications(true);
                user.setVerified(false);
                user.setVerificationcode(RandomStringUtils.randomAlphanumeric(5));
                try {
                    //search if exists first
                    if (userService.findByName(username) == null) {
                        userService.insert(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Notification notification = new Notification();
                notification.setSubject("Registration Confirmation");
                notification.setBody("<a href='http://localhost:8080/aptibook/tenant0/index.xhtml?" + "id=" + user.getVerificationcode() + "'" + "> Yo </a>");
                notification.setUser(user);
                try {
                    emailService.sendEmailNotification(notification);
                    FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "An email confirmation has been sent"));
                } catch (SparkPostException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public boolean passwordRegex(String password){
        if(password.length() > 6 && password.length() < 20){
            return true;
        }else{
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
