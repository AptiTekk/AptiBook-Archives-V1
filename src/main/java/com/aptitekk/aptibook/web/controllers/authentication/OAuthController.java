/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.authentication;

import com.aptitekk.aptibook.core.domain.entities.property.Property;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.GoogleJSONResponse;
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
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;

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
    public static final String GOOGLE_CODE_ATTRIBUTE = "checkGoogleCode";
    private static final String GOOGLE_API_KEY = "908953557522-o6m9dri19o1bmh0hrtkjgh6n0522n5lj.apps.googleusercontent.com";
    private static final String GOOGLE_API_SECRET = "-mXdL_YoL6Q6HrLIF7lUZpAo";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";


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

        HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String requestUrl = httpServletRequest.getRequestURL().toString();
        serviceBuilder.callback(requestUrl.substring(0, requestUrl.indexOf("/", requestUrl.indexOf(httpServletRequest.getServerName()))) + httpServletRequest.getContextPath() + "/oauth");

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
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                Gson gson = new GsonBuilder().create();
                GoogleJSONResponse googleJSONResponse = gson.fromJson(response.getBody(), GoogleJSONResponse.class);

                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(GOOGLE_CODE_ATTRIBUTE);
                authenticationController.loginWithGoogle(googleJSONResponse);
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

    public boolean isGoogleSignInEnabled() {
        return googleSignInEnabled;
    }
}
