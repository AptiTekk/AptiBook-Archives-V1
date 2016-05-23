/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AptiTekk.Agenda.web.oauth;

import com.AptiTekk.Agenda.core.GoogleService;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.AptiTekk.Agenda.core.GoogleService.INITAL_PATH;

/**
 *
 * @author kevint
 */
@WebServlet(name = "CalendarServlet", urlPatterns = {INITAL_PATH})
public class CalendarServlet extends AbstractAuthorizationCodeServlet {

    @Inject
    GoogleService service;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(initializeFlow().newAuthorizationUrl().build());
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
