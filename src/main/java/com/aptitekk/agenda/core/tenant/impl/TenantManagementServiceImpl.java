/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenant.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.services.TenantService;
import com.aptitekk.agenda.core.tenant.TenantManagementService;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Startup
@Singleton
public class TenantManagementServiceImpl implements TenantManagementService {

    @Inject
    private TenantService tenantService;

    private Map<String, Tenant> allowedTenants;

    private void buildAllowedTenants() {
        allowedTenants = new HashMap<>();

        for (Tenant tenant : tenantService.getAll()) {
            allowedTenants.put(tenant.getSlug(), tenant);
        }
    }

    @Override
    public Set<String> getAllowedTenantSlugs() {
        return allowedTenants.keySet();
    }

    @Override
    public void refreshTenants() {
        buildAllowedTenants();
    }

    @Override
    public Tenant getTenantBySlug(String tenantSlug) {
        return allowedTenants.get(tenantSlug);
    }
}
