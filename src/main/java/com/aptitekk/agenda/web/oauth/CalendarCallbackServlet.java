/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.agenda.web.oauth;

import static com.aptitekk.agenda.core.services.GoogleService.CALLBACK_PATH;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.aptitekk.agenda.core.services.GoogleService;
import static com.aptitekk.agenda.core.services.GoogleService.ACCESS_TOKEN_PROPERTY;

/**
 *
 * @author kevint
 */
@WebServlet(name = "CalendarCallbackServlet", urlPatterns = {CALLBACK_PATH})
public class CalendarCallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

    @Inject
    GoogleService service;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws ServletException, IOException {
        System.out.println("It worked! Welcome token: " + credential.getAccessToken());
        //properties.put(ACCESS_TOKEN_PROPERTY.getKey(), credential.getAccessToken());
    }

    @Override
    protected void onError(
            HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        // handle error
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return service.getCalendarRedirectUri(req);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return service.initializeCalendarFlow();
    }

    @Override
    protected String getUserId(HttpServletRequest hsr) throws ServletException, IOException {
        return service.getCalendarUserId(hsr);
    }
}
