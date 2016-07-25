/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.filters;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.services.TenantService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.tenant.TenantManagementService;
import com.aptitekk.agenda.core.util.LogManager;

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

    public static final String SESSION_ORIGINAL_URL = "Original-Url";
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        LogManager.logInfo("Tenant Filter Initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String path = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
        String[] pathSplit = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length()).split("/");

        if (pathSplit.length >= 2) {

            //Tenants
            if (tenantManagementService.getAllowedTenantSlugs().contains(pathSplit[1].toLowerCase())) { //Valid Tenant ID
                try {
                    String tenantSlug = pathSplit[1].toLowerCase();
                    Tenant tenant = tenantService.getTenantBySlug(tenantSlug);
                    httpServletRequest.setAttribute("tenant", tenant);

                    String url = pathSplit.length >= 3 ? path.substring(path.indexOf("/", 2)) : "/";
                    if (url.contains(";"))
                        url = url.substring(0, url.indexOf(";"));

                    if (url.contains("/secure")) {
                        Object attribute = ((HttpServletRequest) request).getSession(true).getAttribute(tenant.getSlug() + "_authenticatedUser");
                        if (attribute != null && attribute instanceof User) {
                            httpServletRequest.getRequestDispatcher(url).forward(request, response);
                            return;
                        }
                        redirectUnauthorized(request, response);
                        return;
                    } else {
                        httpServletRequest.getRequestDispatcher(url).forward(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }

            //Resources
            if (pathSplit[1].matches("javax\\.faces\\.resource|resources|fonts")) {
                chain.doFilter(request, response);
                return;
            }

            //Servlets
            if (pathSplit[1].matches("ping|images")) {
                chain.doFilter(request, response);
                return;
            }

        }
        redirectInactive(request, response);
    }

    private void redirectInactive(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/error/inactive_tenant.xhtml").forward(request, response);
    }

    private void redirectUnauthorized(ServletRequest request, ServletResponse response) throws IOException {
        String parameters = "?";
        for (Map.Entry<String, String[]> entry : ((Map<String, String[]>) request.getParameterMap()).entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            for (String param : value) {
                parameters += key + "=" + param + "&";
            }
        }

        parameters = parameters.substring(0, parameters.length() - 1);

        String attemptedAccessPath = ((HttpServletRequest) request).getRequestURI();

        LogManager.logInfo("Unauthorized access request to " + attemptedAccessPath + parameters);
        ((HttpServletRequest) request).getSession(true).setAttribute(SESSION_ORIGINAL_URL, attemptedAccessPath + parameters);
        ((HttpServletResponse) response).sendRedirect(filterConfig.getServletContext().getContextPath() + "/" + (request.getAttribute("tenant") != null ? ((Tenant) request.getAttribute("tenant")).getSlug() : ""));
    }

    @Override
    public void destroy() {
    }
}