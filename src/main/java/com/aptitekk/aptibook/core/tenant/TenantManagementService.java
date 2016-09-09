/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.tenant;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.TenantService;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Startup
@Singleton
public class TenantManagementService {

    @Inject
    private TenantService tenantService;

    private Map<String, Tenant> allowedTenants;

    private void buildAllowedTenants() {
        allowedTenants = new HashMap<>();

        for (Tenant tenant : tenantService.getAll()) {
            if (tenant.isActive())
                allowedTenants.put(tenant.getSlug(), tenant);
        }
    }

    /**
     * @return A Set of valid tenant slugs.
     */
    public Set<String> getAllowedTenantSlugs() {
        return allowedTenants.keySet();
    }

    /**
     * Reloads the cached Tenant EntityManagerFactories and allowed Tenant pattern using the current database entries.
     */
    public void refreshTenants() {
        buildAllowedTenants();
    }

    /**
     * Returns a Tenant based on the slug provided.
     *
     * @param tenantSlug The slug of the Tenant.
     * @return The Tenant with the corresponding slug, or null.
     */
    public Tenant getTenantBySlug(String tenantSlug) {
        return allowedTenants.get(tenantSlug);
    }
}
