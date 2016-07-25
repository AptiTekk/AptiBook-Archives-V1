/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenant.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.TenantService;
import com.aptitekk.agenda.core.tenant.TenantManagementService;
import com.aptitekk.agenda.core.tenant.TenantSessionService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Stateful
public class TenantSessionServiceImpl implements TenantSessionService {

    @Inject
    private TenantManagementService tenantManagementService;

    @Inject
    private TenantService tenantService;

    private HttpServletRequest httpRequest;

    @Inject
    public TenantSessionServiceImpl(HttpServletRequest httpRequest) {
        try {
            if (httpRequest != null)
                httpRequest.getAttribute("tenant");
            this.httpRequest = httpRequest;
        } catch (Exception ignored) {
        }
    }

    @Override
    public Tenant getCurrentTenant() {
        if (httpRequest != null) {
            Object attribute = httpRequest.getAttribute("tenant");
            if (attribute != null && attribute instanceof Tenant)
                return (Tenant) attribute;
        }

        return null;
    }
}
