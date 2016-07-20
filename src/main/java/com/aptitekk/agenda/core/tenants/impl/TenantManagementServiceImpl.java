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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Startup
@Singleton
public class TenantManagementServiceImpl implements TenantManagementService {

    @Inject
    private TenantService tenantService;

    private Set<String> allowedTenantSlugs;

    private void buildAllowedTenantSlugSet() {
        allowedTenantSlugs = new HashSet<>();

        allowedTenantSlugs.addAll(tenantService.getAll().stream().map(Tenant::getSlug).collect(Collectors.toList()));
    }

    @Override
    public Set<String> getAllowedTenantSlugs() {
        return allowedTenantSlugs;
    }

    @Override
    public void refreshTenants() {
        buildAllowedTenantSlugSet();
    }
}
