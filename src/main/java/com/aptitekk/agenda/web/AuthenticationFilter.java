/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web;

import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.services.UserService;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

    public static final String SESSION_ORIGINAL_URL = "Original-Url";

    private ServletContext context;

    @Inject
    private UserService userService;

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
        this.context.log("AuthenticationFilter initialized");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest currentReq = (HttpServletRequest) request;
        HttpServletResponse currentRes = (HttpServletResponse) response;
        HttpSession currentSession = currentReq.getSession(true);

        String uri = currentReq.getRequestURI();
        String sessionUsername = (String) currentSession.getAttribute(UserService.SESSION_VAR_USERNAME);

        User user = null;

        if (sessionUsername != null) {
            user = userService.findByName(sessionUsername);
        }

        if (uri.contains("/secure")) {
            if (user != null) {
                chain.doFilter(request, response);
                return;
            }
        } else if (uri.contains(context.getContextPath() + "/index.xhtml") && user != null) {
            currentRes.sendRedirect(context.getContextPath() + "/secure/index.xhtml");
            return;
        } else {
            chain.doFilter(request, response);
            return;
        }

        String parameters = "?";
        for (Map.Entry<String, String[]> entry : ((Map<String, String[]>) currentReq.getParameterMap()).entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            for (String param : value) {
                parameters += key + "=" + param + "&";
            }
        }

        parameters = parameters.substring(0, parameters.length() - 1);

        this.context.log("Unauthorized access request to " + uri + parameters);
        currentSession.setAttribute(SESSION_ORIGINAL_URL, uri + parameters);
        currentRes.sendRedirect(context.getContextPath() + "/index.xhtml");
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }


}
