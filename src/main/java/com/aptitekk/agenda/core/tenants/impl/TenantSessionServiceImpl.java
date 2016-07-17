/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenants.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.TenantService;
import com.aptitekk.agenda.core.tenants.TenantSessionService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Stateful
public class TenantSessionServiceImpl implements TenantSessionService {

    @Inject
    private HttpServletRequest servletRequest;

    @Override
    public Tenant getCurrentTenant() {
        if (servletRequest != null) {
            Object attribute = servletRequest.getAttribute("tenant");
            if (attribute != null && attribute instanceof Tenant)
                return (Tenant) attribute;
        }
        return null;
    }
}
