/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.authentication;


import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.services.NotificationService;
import com.aptitekk.aptibook.core.domain.services.PermissionService;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.rest.oAuthModels.GoogleUserInfoModel;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.filters.TenantFilter;
import com.aptitekk.aptibook.web.util.SessionVariableManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Inject
    private NotificationService notificationService;

    public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";

    private String emailAddress;
    private String password;

    private User authenticatedUser;

    @PostConstruct
    public void init() {
        if (tenantSessionService != null && tenantSessionService.getCurrentTenant() != null) {
            Object attribute = SessionVariableManager.fromFacesUsingTenant(FacesContext.getCurrentInstance(), tenantSessionService.getCurrentTenant().getSlug()).getVariableData(AUTHENTICATED_USER_ATTRIBUTE);
            if (attribute != null && attribute instanceof User) {
                authenticatedUser = userService.get(((User) attribute).getId());
            }

            //TODO: Move to RegistrationController.
            String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(RegistrationController.REGISTRATION_VERIFICATION_PARAMETER);
            if (id != null) {
                User user = userService.findByCode(id);
                if (user != null) {
                    if (!user.isVerified()) {
                        user.setVerificationCode(null);
                        user.setVerified(true);
                        authenticatedUser = null; //Sign the user out if they are signed in...
                        try {
                            userService.merge(user);
                            notificationService.sendNewUserRegistrationNotifications(user);
                            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Your account has been verified! You may sign in once your account has been approved by an administrator."));
                        } catch (Exception e) {
                            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "We had a problem while verifying your account. Please try again later!"));
                            LogManager.logException(getClass(), "Could not verify user", e);
                        }
                    }

                }
            }
        }
    }

    /**
     * Login with Google
     *
     * @param googleUserInfoModel The User's details from Google.
     */
    public void loginWithGoogle(GoogleUserInfoModel googleUserInfoModel) {
        if (googleUserInfoModel == null)
            return;

        String whitelistedDomains = propertiesService.getPropertyByKey(Property.Key.GOOGLE_SIGN_IN_WHITELIST).getPropertyValue();
        String[] whitelist = whitelistedDomains.replaceAll("\\s+", "").toLowerCase().split(",");

        boolean domainIsWhitelisted = false;
        for (String domain : whitelist) {
            if (domain.equals(googleUserInfoModel.getEmail().toLowerCase().split("@")[1])) {
                domainIsWhitelisted = true;
            }
        }

        if (domainIsWhitelisted) {
            User existingUser = userService.findByEmailAddress(googleUserInfoModel.getEmail());
            if (existingUser == null) {
                User user = new User();
                user.setFirstName(googleUserInfoModel.getGivenName());
                user.setLastName(googleUserInfoModel.getFamilyName());
                user.setEmailAddress(googleUserInfoModel.getEmail());
                user.setVerified(true);
                user.setUserState(User.State.APPROVED);
                try {
                    userService.insert(user);
                    setAuthenticatedUser(user);
                    LogManager.logInfo(getClass(), "'" + authenticatedUser.getEmailAddress() + "' has logged in with Google.");
                    SessionVariableManager.fromFacesUsingTenant(FacesContext.getCurrentInstance(), tenantSessionService.getCurrentTenant().getSlug()).setVariableData(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser);

                    redirectAfterLogin();
                } catch (Exception e) {
                    LogManager.logException(getClass(), "Could not create User for Google Sign In", e);
                }
            } else {
                setAuthenticatedUser(existingUser);
                LogManager.logInfo(getClass(), "'" + authenticatedUser.getEmailAddress() + "' has logged in with Google.");
                SessionVariableManager.fromFacesUsingTenant(FacesContext.getCurrentInstance(), tenantSessionService.getCurrentTenant().getSlug()).setVariableData(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser);

                redirectAfterLogin();
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Signing in with Google using emails @" + googleUserInfoModel.getEmail().toLowerCase().split("@")[1] + " is not allowed."));
        }
    }

    /**
     * Attempts to log the authenticatedUser in with the credentials they have input.
     */
    public void login() {
        FacesContext context = FacesContext.getCurrentInstance();
        User authenticatedUser = userService.getUserWithCredentials(emailAddress, password);
        password = null;

        if (authenticatedUser == null) // Invalid Credentials
        {
            LogManager.logInfo(getClass(), "Login attempt for '" + emailAddress + "' has failed.");
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Incorrect Credentials."));
        } else if (!authenticatedUser.isVerified() && !authenticatedUser.isAdmin()) {
            LogManager.logInfo(getClass(), "Login attempt for '" + emailAddress + "' has failed due to being unverified.");
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Your account has not been verified by email."));
            //TODO: Allow them to re-send the email.
        } else if (authenticatedUser.getUserState() != User.State.APPROVED && !authenticatedUser.isAdmin()) {
            LogManager.logInfo(getClass(), "Login attempt for '" + emailAddress + "' has failed due to being unapproved.");
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Your account has not yet been approved by an administrator."));
        } else {
            LogManager.logInfo(getClass(), "'" + authenticatedUser.getEmailAddress() + "' has logged in.");
            setAuthenticatedUser(authenticatedUser);
            SessionVariableManager.fromFacesUsingTenant(FacesContext.getCurrentInstance(), tenantSessionService.getCurrentTenant().getSlug()).setVariableData(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser);
            redirectAfterLogin();
        }
    }

    /**
     * Signs a user out of the application and clears their session.
     *
     * @return The outcome page.
     */
    public String logout() {
        LogManager.logInfo(getClass(), "'" + authenticatedUser.getEmailAddress() + "' has logged out.");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index";
    }

    /**
     * Redirects the user to the correct place after login.
     * Will take into account the page the user last tried to access, and will attempt to redirect them back to it.
     * Otherwise, an outcome page will be used.
     */
    private void redirectAfterLogin() {
        String originalUrl = SessionVariableManager.fromFacesUsingTenant(FacesContext.getCurrentInstance(), tenantSessionService.getCurrentTenant().getSlug()).getStringVariableData(TenantFilter.ORIGINAL_URL_ATTRIBUTE);
        if (originalUrl != null) {
            SessionVariableManager.fromFacesUsingTenant(FacesContext.getCurrentInstance(), tenantSessionService.getCurrentTenant().getSlug()).clearVariableData(TenantFilter.ORIGINAL_URL_ATTRIBUTE);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(originalUrl);
                return;
            } catch (IOException e) {
                LogManager.logException(getClass(), "Could not redirect user to original url (" + originalUrl + ")", e);
            }
        }

        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.getApplication().getNavigationHandler().handleNavigation(context, null, "secure");
        }
    }

    /**
     * Used when a user attempts to access the sign in page, to redirect the users that are already signed in.
     *
     * @return The page to redirect to.
     */
    public String redirectIfLoggedIn() throws IOException {
        if (authenticatedUser != null)
            return "secure";
        return null;
    }

    /**
     * Determines if a user has a given permission.
     *
     * @param descriptor The permission descriptor to check for.
     * @return True if they have permission, false otherwise.
     */
    public boolean userHasPermission(Permission.Descriptor descriptor) {
        return authenticatedUser != null && (authenticatedUser.isAdmin() || permissionService.userHasPermission(authenticatedUser, descriptor) || permissionService.userHasPermission(authenticatedUser, Permission.Descriptor.GENERAL_FULL_PERMISSIONS));
    }

    /**
     * Determines if a user has one or more permissions within a given permission group.
     *
     * @param group The permission group to look for permissions within.
     * @return True if they have one or more permissions, false otherwise.
     */
    public boolean userHasPermissionOfGroup(Permission.Group group) {
        return authenticatedUser != null && (authenticatedUser.isAdmin() || permissionService.userHasPermissionOfGroup(authenticatedUser, group) || permissionService.userHasPermission(authenticatedUser, Permission.Descriptor.GENERAL_FULL_PERMISSIONS));
    }

    /**
     * Forces a redirect to the login page. (Used when the user doesn't have permission to access a page, for example.)
     */
    public void forceUserRedirect() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            try {
                TenantFilter.redirectUnauthorized((HttpServletRequest) context.getExternalContext().getRequest(), (HttpServletResponse) context.getExternalContext().getResponse());
            } catch (IOException e) {
                LogManager.logException(getClass(), "Could not force redirect", e);
            }
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
