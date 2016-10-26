/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.tenant;

import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.EmailService;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.domain.services.TenantService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Startup
@Singleton
public class TenantManagementService {

    @Inject
    private TenantService tenantService;

    @Inject
    private PropertiesService propertiesService;

    private Map<String, Tenant> allowedTenants;

    private Map<Tenant, ZoneId> zoneIdMap;

    @PostConstruct
    private void init() {
        refresh();
    }

    public void refresh() {
        refreshAllowedTenants();
        refreshDateTimeZones();
    }

    private void refreshAllowedTenants() {
        allowedTenants = new HashMap<>();

        for (Tenant tenant : tenantService.getAll()) {
            if (tenant.isActive())
                allowedTenants.put(tenant.getSlug(), tenant);
        }
    }

    private void refreshDateTimeZones() {
        zoneIdMap = new HashMap<>();

        for (Tenant tenant : tenantService.getAll()) {
            Property dateTimeZoneKey = propertiesService.getPropertyByKey(Property.Key.DATE_TIME_TIMEZONE, tenant);
            try {
                ZoneId dateTimeZone = ZoneId.of(dateTimeZoneKey.getPropertyValue());
                zoneIdMap.put(tenant, dateTimeZone);
            } catch (Exception e) {
                zoneIdMap.put(tenant, ZoneId.systemDefault());
            }
        }
    }

    /**
     * @return A Set of valid tenant slugs.
     */
    public Set<String> getAllowedTenantSlugs() {
        return allowedTenants.keySet();
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

    /**
     * Returns the ZoneId for the specified tenant, as is set on the properties page by the administrator.
     *
     * @param tenant The tenant to get the DateTimeZone of.
     * @return The ZoneId of the tenant.
     */
    public ZoneId getZoneId(Tenant tenant) {
        return zoneIdMap.get(tenant);
    }
}
