/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.authentication;

import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.entities.property.Property;
import com.aptitekk.aptibook.core.domain.oAuthModels.GoogleUserInfoModel;
import com.aptitekk.aptibook.core.domain.services.PermissionService;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.FacesSessionHelper;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.filters.TenantFilter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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

    @Inject
    private TenantSessionService tenantSessionService;

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private OAuthController oAuthController;

    private String username;
    private String password;

    private User authenticatedUser;

    @PostConstruct
    public void init() {
        if (tenantSessionService != null && tenantSessionService.getCurrentTenant() != null) {
            Object attribute = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(tenantSessionService.getCurrentTenant().getSlug() + "_authenticatedUser");
            if (attribute != null && attribute instanceof User) {
                authenticatedUser = userService.get(((User) attribute).getId());
            }
            String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(RegistrationController.REGISTRATION_VERIFICATION_PARAMETER);
            if (id != null) {
                User user = userService.findByCode(id);
                if (user != null) {
                    if (!user.isVerified()) {
                        user.setVerificationCode(null);
                        user.setVerified(true);
                        try {
                            userService.merge(user);
                            LogManager.logInfo("User " + user.getUsername() + " has been verified.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Your account has been verified! You may sign in once your account has been approved by an administrator."));
                    }

                }
            }
        }
    }

    /**
     * Login with Google
     *
     * @param googleUserInfoModel The User's details from Google.
     * @return The outcome page.
     */
    String loginWithGoogle(GoogleUserInfoModel googleUserInfoModel) {
        if (googleUserInfoModel == null)
            return null;

        //TODO: Get from properties
        String whitelistedDomains = propertiesService.getPropertyByKey(Property.Key.GOOGLE_SIGN_IN_WHITELIST).getPropertyValue();
        String[] whitelist = whitelistedDomains.replaceAll("\\s+", "").toLowerCase().split(",");

        boolean domainIsWhitelisted = false;
        for (String domain : whitelist) {
            if (domain.equals(googleUserInfoModel.getEmail().toLowerCase().split("@")[1])) {
                domainIsWhitelisted = true;
            }
        }

        if (domainIsWhitelisted) {
            User existingUser = userService.findByName(googleUserInfoModel.getEmail());
            if (existingUser == null) {
                User user = new User();
                user.setFirstName(googleUserInfoModel.getGivenName());
                user.setLastName(googleUserInfoModel.getFamilyName());
                user.setUsername(googleUserInfoModel.getEmail());
                user.setVerified(true);

                try {
                    userService.insert(user);
                    setAuthenticatedUser(user);
                    LogManager.logInfo("'" + authenticatedUser.getUsername() + "' has logged in with Google.");
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(tenantSessionService.getCurrentTenant().getSlug() + "_authenticatedUser", authenticatedUser);
                    return redirectHome();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            } else {
                setAuthenticatedUser(existingUser);
                LogManager.logInfo("'" + authenticatedUser.getUsername() + "' has logged in with Google.");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(tenantSessionService.getCurrentTenant().getSlug() + "_authenticatedUser", authenticatedUser);
                return redirectHome();
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Signing in with Google using emails @" + googleUserInfoModel.getEmail().toLowerCase().split("@")[1] + " is not allowed."));
        }
        return null;
    }

    /**
     * Attempts to log the authenticatedUser in with the credentials they have input.
     *
     * @return The outcome page.
     */

    public String login() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext context = FacesContext.getCurrentInstance();
        User authenticatedUser = userService.getUserWithCredentials(username, password);
        password = null;

        if (authenticatedUser == null) // Invalid Credentials
        {
            LogManager.logInfo("Login attempt for '" + username + "' has failed.");
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Incorrect Credentials."));
            return null;
        } else if (!authenticatedUser.isVerified() && !authenticatedUser.isAdmin()) {
            LogManager.logInfo("Login attempt for '" + username + "' has failed due to being unverified.");
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Your account has not been verified."));
            return null;
        } else {
            LogManager.logInfo("'" + authenticatedUser.getUsername() + "' has logged in.");
            setAuthenticatedUser(authenticatedUser);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(tenantSessionService.getCurrentTenant().getSlug() + "_authenticatedUser", authenticatedUser);
            return redirectHome();
        }
    }

    public String logout() {
        LogManager.logInfo("'" + authenticatedUser.getUsername() + "' has logged out.");

        oAuthController.clearTokens();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index";
    }

    private String redirectHome() {
        String originalUrl = FacesSessionHelper.getSessionVariableAsString(TenantFilter.SESSION_ORIGINAL_URL);
        if (originalUrl != null) {
            FacesSessionHelper.removeSessionVariable(TenantFilter.SESSION_ORIGINAL_URL);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(originalUrl);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "secure";
    }

    public String redirectIfLoggedIn() throws IOException {
        if (authenticatedUser != null)
            return "secure";
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
