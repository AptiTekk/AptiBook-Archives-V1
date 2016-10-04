/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.authentication;

import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.rest.oAuthModels.GoogleUserInfoModel;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.FacesURIBuilder;
import com.aptitekk.aptibook.core.util.LogManager;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

@Named("oAuthController")
@ViewScoped
public class OAuthController implements Serializable {

    @Inject
    private TenantSessionService tenantSessionService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private PropertiesService propertiesService;

    private boolean googleSignInEnabled;
    public static final String GOOGLE_CODE_ATTRIBUTE = "googleCode";
    private static final String GOOGLE_ACCESS_TOKEN_ATTRIBUTE = "googleAccessToken";
    private static final String GOOGLE_API_KEY = "908953557522-o6m9dri19o1bmh0hrtkjgh6n0522n5lj.apps.googleusercontent.com";
    private static final String GOOGLE_API_SECRET = "-mXdL_YoL6Q6HrLIF7lUZpAo";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String GOOGLE_REVOKE_URL = "https://accounts.google.com/o/oauth2/revoke";

    private OAuth20Service googleOAuthService;

    @PostConstruct
    private void init() {
        googleSignInEnabled = Boolean.valueOf(propertiesService.getPropertyByKey(Property.Key.GOOGLE_SIGN_IN_ENABLED).getPropertyValue());
        if (googleSignInEnabled)
            googleOAuthService = buildGoogleOAuthService();
    }

    private OAuth20Service buildGoogleOAuthService() {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        serviceBuilder.apiKey(GOOGLE_API_KEY);
        serviceBuilder.apiSecret(GOOGLE_API_SECRET);

        URI requestURI = FacesURIBuilder.buildURI("oauth", null);
        if (requestURI == null) {
            googleSignInEnabled = false;
            LogManager.logError("Could not create callback URL for Google OAuth.");
            return null;
        }
        serviceBuilder.callback(requestURI.toString());

        serviceBuilder.scope("email");
        serviceBuilder.state("tenant=" + tenantSessionService.getCurrentTenant().getSlug());
        return serviceBuilder.build(GoogleApi20.instance());
    }

    public void checkGoogleCode() {
        if (!googleSignInEnabled)
            return;

        Object googleCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(GOOGLE_CODE_ATTRIBUTE);
        if (googleCode != null && googleCode instanceof String) {
            try {
                OAuth2AccessToken accessToken = googleOAuthService.getAccessToken(googleCode.toString());
                OAuthRequest request = new OAuthRequest(Verb.GET, GOOGLE_USER_INFO_URL, googleOAuthService);
                googleOAuthService.signRequest(accessToken, request);
                Response response = request.send();
                Gson gson = new GsonBuilder().create();
                GoogleUserInfoModel googleUserInfoModel = gson.fromJson(response.getBody(), GoogleUserInfoModel.class);

                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(GOOGLE_CODE_ATTRIBUTE);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(GOOGLE_ACCESS_TOKEN_ATTRIBUTE, accessToken);
                authenticationController.loginWithGoogle(googleUserInfoModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void signInWithGoogle() {
        if (!googleSignInEnabled)
            return;

        if (googleOAuthService != null)
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(googleOAuthService.getAuthorizationUrl());
            } catch (IOException e) {
                FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Unable to Sign In with Google at this time."));
                e.printStackTrace();
            }
        else
            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Unable to Sign In with Google at this time."));
    }

    void clearTokens() {
        Object attribute = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(GOOGLE_ACCESS_TOKEN_ATTRIBUTE);
        if (attribute != null && attribute instanceof OAuth2AccessToken) {
            OAuth2AccessToken accessToken = (OAuth2AccessToken) attribute;
            OAuthRequest request = new OAuthRequest(Verb.GET, GOOGLE_REVOKE_URL, googleOAuthService);
            request.addQuerystringParameter("token", accessToken.getAccessToken());
            Response response = request.send();
            if (!response.isSuccessful())
                LogManager.logError("Could not revoke token on sign out: " + response.getMessage());
        }
    }

    public boolean isGoogleSignInEnabled() {
        return googleSignInEnabled;
    }
}
