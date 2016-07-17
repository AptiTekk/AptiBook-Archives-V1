/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenants.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.TenantService;
import com.aptitekk.agenda.core.tenants.TenantManagementService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class TenantManagementServiceImpl implements TenantManagementService {

    private Map<Tenant, EntityManager> tenantEntityManagerMap = new HashMap<>();
    private String allowedTenantUrlPattern;

    @Inject
    private TenantService tenantService;

    private void buildTenantUrlPattern() {
        List<Tenant> tenants = tenantService.getAll();
        StringBuilder urlPatternBuilder = new StringBuilder();
        for (Tenant tenant : tenants) {
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
