/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenants.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.TenantService;
import com.aptitekk.agenda.core.tenants.TenantManagementService;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class TenantManagementServiceImpl implements TenantManagementService {

    @Inject
    private TenantService tenantService;

    private String allowedTenantUrlPattern;

    private void buildTenantUrlPattern() {
        StringBuilder urlPatternBuilder = new StringBuilder();
        for (Tenant tenant : tenantService.getAll()) {
            urlPatternBuilder.append(tenant.getSubscriptionId()).append("|");
        }

        if (urlPatternBuilder.length() > 0)
            urlPatternBuilder.deleteCharAt(urlPatternBuilder.length() - 1);

        allowedTenantUrlPattern = urlPatternBuilder.toString();
    }

    @Override
    public String getAllowedTenantUrlPattern() {
        return allowedTenantUrlPattern;
    }

    @Override
    public void refreshTenants() {
        buildTenantUrlPattern();
    }
}
