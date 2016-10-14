/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.filters;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.services.TenantService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.tenant.TenantManagementService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.util.SessionVariableManager;

import javax.faces.application.ViewExpiredException;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebFilter(filterName = "TenantFilter")
public class TenantFilter implements Filter {

    @Inject
    private TenantManagementService tenantManagementService;

    @Inject
    private TenantService tenantService;

    @Inject
    private UserService userService;

    public static final String ORIGINAL_URL_ATTRIBUTE = "originalUrl";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            String path = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
            String[] pathSplit = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length()).split("/");

            if (pathSplit.length >= 2) {

                //Tenants
                if (tenantManagementService.getAllowedTenantSlugs().contains(pathSplit[1].toLowerCase())) { //Valid Tenant ID
                    String tenantSlug = pathSplit[1].toLowerCase();
                    Tenant tenant = tenantManagementService.getTenantBySlug(tenantSlug);
                    if (tenant != null) {
                        httpServletRequest.setAttribute("tenant", tenant);

                        String url = pathSplit.length >= 3 ? path.substring(path.indexOf("/", 2)) : "/";
                        if (url.contains(";"))
                            url = url.substring(0, url.indexOf(";"));

                        if (url.contains("/secure")) {
                            Object attribute = SessionVariableManager.fromServletUsingTenant((HttpServletRequest) request, tenant.getSlug()).getVariableData(AuthenticationController.AUTHENTICATED_USER_ATTRIBUTE);
                            if (attribute != null && attribute instanceof User) {
                                httpServletRequest.getRequestDispatcher(url).forward(request, response);
                                return;
                            }
                            redirectUnauthorized(httpServletRequest, httpServletResponse);
                            return;
                        } else {
                            httpServletRequest.getRequestDispatcher(url).forward(request, response);
                            return;
                        }
                    }
                }

                //Resources
                if (pathSplit[1].matches("javax\\.faces\\.resource|resources|fonts")) {
                    chain.doFilter(request, response);
                    return;
                }

                //Servlets
                if (pathSplit[1].matches("ping|images|oauth")) {
                    chain.doFilter(request, response);
                    return;
                }

            }

            redirectInactiveTenant(request, response);
        } catch (ViewExpiredException e) {
            try {
                redirectViewExpired(request, response);
            } catch (Exception e1) {
                handleUncaughtException(e1, request, response);
            }
        } catch (Exception e) {
            handleUncaughtException(e, request, response);
        }
    }

    private void handleUncaughtException(Exception e, ServletRequest request, ServletResponse response) {
        LogManager.logException(getClass(), "Uncaught Exception", e);
        try {
            redirectError(request, response);
        } catch (ServletException | IOException e1) {
            LogManager.logException(getClass(), "Uncaught Exception", e);
        }
    }

    private void redirectViewExpired(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("tenant") != null)
            ((HttpServletResponse) response).sendRedirect(((Tenant) request.getAttribute("tenant")).getSlug() + "/index.xhtml?session-expired=true");
        else
            request.getRequestDispatcher("/WEB-INF/error/view_expired.xhtml").forward(request, response);
    }

    private void redirectInactiveTenant(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/error/inactive_tenant.xhtml").forward(request, response);
    }

    private void redirectError(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/error/500.xhtml").forward(request, response);
    }

    public static void redirectUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String parameters = "?";
        //noinspection RedundantCast
        for (Map.Entry<String, String[]> entry : ((Map<String, String[]>) request.getParameterMap()).entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            for (String param : value) {
                parameters += key + "=" + param + "&";
            }
        }

        parameters = parameters.substring(0, parameters.length() - 1);

        String attemptedAccessPath = request.getRequestURI();

        if (request.getAttribute("tenant") != null)
            SessionVariableManager.fromServletUsingTenant(request, ((Tenant) request.getAttribute("tenant")).getSlug()).setVariableData(ORIGINAL_URL_ATTRIBUTE, attemptedAccessPath + parameters);
        response.sendRedirect(request.getContextPath() + "/" + (request.getAttribute("tenant") != null ? ((Tenant) request.getAttribute("tenant")).getSlug() : ""));
    }

    @Override
    public void destroy() {
    }
}