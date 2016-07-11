/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.entities.Permission;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.services.PermissionService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.utilities.FacesSessionHelper;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.web.AuthenticationFilter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class AuthenticationController implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    private String username;
    private String password;

    private User authenticatedUser;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        this.username = externalContext.getRequestHeaderMap().get("REMOTE_USER");

        String loggedInUser
                = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (loggedInUser != null) {
            this.setAuthenticatedUser(userService.findByName(loggedInUser));
        }
    }

    /**
     * Attempts to log the authenticatedUser in with the credentials they have input.
     *
     * @return The outcome page.
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        LogManager.logDebug("Logging In - User: " + username);

        User authenticatedUser = userService.correctCredentials(username, password);
        password = null;

        if (authenticatedUser == null) // Invalid Credentials
        {
            LogManager.logInfo("Login attempt for '" + username + "' has failed.");
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Incorrect Credentials."));
            return null;
        } else {
            LogManager.logInfo("'" + authenticatedUser.getUsername() + "' has logged in.");
            setAuthenticatedUser(authenticatedUser);
            FacesSessionHelper.setSessionVariable(UserService.SESSION_VAR_USERNAME,
                    authenticatedUser.getUsername());
            String originalUrl
                    = FacesSessionHelper.getSessionVariableAsString(AuthenticationFilter.SESSION_ORIGINAL_URL);
            if (originalUrl != null) {
                FacesSessionHelper.removeSessionVariable(AuthenticationFilter.SESSION_ORIGINAL_URL);
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(originalUrl);
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "index";
    }

    public String logout() {
        LogManager.logInfo("'" + authenticatedUser.getUsername() + "' has logged out.");

        FacesSessionHelper.removeSessionVariable(UserService.SESSION_VAR_USERNAME);
        return "index";
    }

    public String redirectIfLoggedIn() throws IOException {
        if (authenticatedUser != null)
            return "index";
        return null;
    }

    public boolean userHasPermission(Permission.Descriptor descriptor) {
        return authenticatedUser != null && (authenticatedUser.isAdmin() || permissionService.userHasPermission(authenticatedUser, descriptor) || permissionService.userHasPermission(authenticatedUser, Permission.Descriptor.GENERAL_FULL_PERMISSIONS));
    }

    public boolean userHasPermissionOfGroup(Permission.Group group) {
        return authenticatedUser != null && (authenticatedUser.isAdmin() || permissionService.userHasPermissionOfGroup(authenticatedUser, group) || permissionService.userHasPermission(authenticatedUser, Permission.Descriptor.GENERAL_FULL_PERMISSIONS));
    }

    public void forceUserRedirect() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.getApplication().getNavigationHandler().handleNavigation(context, null, "index");
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
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

}
