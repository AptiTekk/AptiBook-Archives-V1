package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.FacesSessionHelper;
import com.aptitekk.agenda.web.AuthenticationFilter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;

@ManagedBean(name = "AuthenticationController")
@ViewScoped
public class AuthenticationController {

    @Inject
    private UserService userService;

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
            LogManager.logInfo("'" + username + "' has logged in.");
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

    public void redirectIfLoggedIn() throws IOException {
        if (authenticatedUser != null) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            externalContext.redirect(externalContext.getRequestContextPath() + "/secure/index.xhtml");
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
