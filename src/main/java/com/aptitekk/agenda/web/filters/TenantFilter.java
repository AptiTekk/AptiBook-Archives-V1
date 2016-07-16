/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.filters;

import com.aptitekk.agenda.core.utilities.LogManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class TenantFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LogManager.logInfo("Tenant Filter Initialized.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String[] pathSplit = request.getRequestURI().substring(request.getContextPath().length()).split("/");

        if (pathSplit.length >= 2 && pathSplit[1].matches("[0-9]{1,9}")) {
            request.setAttribute("tenant", Integer.valueOf(pathSplit[1]));
            String url = pathSplit.length >= 3 ? path.substring(path.indexOf("/", 2)) : "/";
            if (url.contains(";"))
                url = url.substring(0, url.indexOf(";"));
            request.getRequestDispatcher(url).forward(req, res);
        } else if (pathSplit.length < 2) {
            ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
    }
}