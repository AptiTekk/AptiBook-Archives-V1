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
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.sparkpost.exception.SparkPostException;

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

    private String username;
    private String password;
    private String confirmPassword;

    public void register() {
        if (username == null || password == null || confirmPassword == null) {
            System.out.println(username + password + confirmPassword);
            FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Complete all forms"));
            return;
        }
        if (password.equals(confirmPassword)) {
            //send link
            byte[] passwordByte = password.getBytes();
            User user = new User();
            user.setUsername(this.username);
            user.setPassword(passwordByte);
            Notification notification = new Notification();
            notification.setSubject("Registration Confirmation");
            notification.setBody("<a:href=http://localhost:8080/aptibook/tenant0/index.xhtml");
            notification.setUser(user);
            try {
                emailService.sendEmailNotification(notification);
                System.out.println("Sent email");
            } catch (SparkPostException e) {
                e.printStackTrace();
            }

            System.out.println("Got to password equals");

        }
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

}
