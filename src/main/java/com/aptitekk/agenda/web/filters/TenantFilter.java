/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.filters;

import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.tenants.TenantManagementService;
import com.aptitekk.agenda.core.tenants.TenantSessionService;
import com.aptitekk.agenda.core.utilities.LogManager;

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
    private TenantSessionService tenantSessionService;

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
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String[] pathSplit = request.getRequestURI().substring(request.getContextPath().length()).split("/");

        if (pathSplit.length >= 2) {

            //Tenants
            if (pathSplit[1].matches(tenantManagementService.getAllowedTenantUrlPattern())) { //Valid Tenant ID
                tenantSessionService.setCurrentTenant(pathSplit[1]);
                request.setAttribute("tenant", tenantSessionService.getCurrentTenant());

                String url = pathSplit.length >= 3 ? path.substring(path.indexOf("/", 2)) : "/";
                if (url.contains(";"))
                    url = url.substring(0, url.indexOf(";"));

                if (url.contains("/secure")) {
                    User user = userService.findByName((String) ((HttpServletRequest) req).getSession(true).getAttribute(UserService.SESSION_VAR_USERNAME));
                    if (user != null) {
                        request.getRequestDispatcher(url).forward(req, res);
                        return;
                    }
                    redirectUnauthorized(req, res);
                    return;
                } else {
                    request.getRequestDispatcher(url).forward(req, res);
                    return;
                }
            } else if (pathSplit[1].matches("[0-9]{1,9}")) { //Invalid Tenant ID
                request.getRequestDispatcher("/WEB-INF/error/inactive_tenant.xhtml").forward(req, res);
            }

            //Resources
            if (pathSplit[1].matches("javax\\.faces\\.resource|resources|fonts")) {
                chain.doFilter(req, res);
            }

            //Servlets
            if (pathSplit[1].matches("ping|images")) {
                chain.doFilter(req, res);
            }

        } else {
            ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
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
        ((HttpServletResponse) response).sendRedirect(filterConfig.getServletContext().getContextPath() + "/" + (tenantSessionService.getCurrentTenant() != null ? tenantSessionService.getCurrentTenant().getSubscriptionId() : ""));
    }

    @Override
    public void destroy() {
    }
}