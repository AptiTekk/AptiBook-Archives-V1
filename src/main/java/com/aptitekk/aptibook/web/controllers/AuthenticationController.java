/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.entities.Permission;
import com.aptitekk.aptibook.core.entities.User;
import com.aptitekk.aptibook.core.entities.services.PermissionService;
import com.aptitekk.aptibook.core.entities.services.UserService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.FacesSessionHelper;
import com.aptitekk.aptibook.core.util.GoogleJSONResponse;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.filters.TenantFilter;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.primefaces.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Named
@ViewScoped
public class AuthenticationController implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private TenantSessionService tenantSessionService;

    private String username;
    private String password;

    private User authenticatedUser;
    private OAuth20Service oAuthService;

    @PostConstruct
    public void init() {
        if (tenantSessionService != null && tenantSessionService.getCurrentTenant() != null) {
            Object attribute = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(tenantSessionService.getCurrentTenant().getSlug() + "_authenticatedUser");
            if (attribute != null && attribute instanceof User) {
                authenticatedUser = userService.get(((User) attribute).getId());
            }
        }

        oAuthService = getService();
        Object code = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("code");
        if (code != null && code instanceof String) {
            try {
                OAuth2AccessToken accessToken = oAuthService.getAccessToken(code.toString());
                OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo", oAuthService);
                oAuthService.signRequest(accessToken,request);
                Response response = request.send();
                System.out.println(response.getBody());
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                String userData = response.getBody();
                login(userData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private OAuth20Service getService() {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        serviceBuilder.apiKey("908953557522-o6m9dri19o1bmh0hrtkjgh6n0522n5lj.apps.googleusercontent.com");
        serviceBuilder.apiSecret("-mXdL_YoL6Q6HrLIF7lUZpAo");

        HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String requestUrl = httpServletRequest.getRequestURL().toString();
        serviceBuilder.callback(requestUrl.substring(0, requestUrl.indexOf("/", requestUrl.indexOf(httpServletRequest.getServerName()))) + httpServletRequest.getContextPath() + "/oauth");

        serviceBuilder.scope("email");
        serviceBuilder.state("tenant=" + tenantSessionService.getCurrentTenant().getSlug());
        return serviceBuilder.build(GoogleApi20.instance());
    }

    public void signInWithGoogle() {
        if (oAuthService != null)
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(oAuthService.getAuthorizationUrl());
            } catch (IOException e) {
                FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Unable to Sign In with Google at this time."));
                e.printStackTrace();
            }
        else
            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Unable to Sign In with Google at this time."));
    }


    /**
     * Check to see if google user exists, if not creates a new one. Sets authenticated user.
     *
     * @return The outcome page.
     */
    public String login(String json){
        Gson gson = new GsonBuilder().create();
        GoogleJSONResponse googleJSONResponse = gson.fromJson(json, GoogleJSONResponse.class);

        if(userService.findByName(googleJSONResponse.getEmail()) == null){
            User user = new User();
            user.setFirstName(googleJSONResponse.getGiven_name());
            user.setLastName(googleJSONResponse.getFamily_name());
            user.setUsername(googleJSONResponse.getEmail());
            setAuthenticatedUser(user);
            LogManager.logDebug("Logged in user with Google");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(tenantSessionService.getCurrentTenant().getSlug() + "_authenticatedUser", authenticatedUser);
            return redirectHome();
        }else{
            User authenticatedUser = userService.findByName(googleJSONResponse.getEmail());
            setAuthenticatedUser(authenticatedUser);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(tenantSessionService.getCurrentTenant().getSlug() + "_authenticatedUser", authenticatedUser);
            return redirectHome();
        }

    }

    private String redirectHome(){
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



    /**
     * Attempts to log the authenticatedUser in with the credentials they have input.
     *
     * @return The outcome page.
     */
    public String login() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        FacesContext context = FacesContext.getCurrentInstance();
        LogManager.logDebug("Logging In - User: " + username);

        User authenticatedUser = userService.getUserWithCredentials(username, password);
        password = null;

        if (authenticatedUser == null) // Invalid Credentials
        {
            LogManager.logInfo("Login attempt for '" + username + "' has failed.");
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Incorrect Credentials."));
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

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index";
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
