/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.tenant;

import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;
import com.aptitekk.aptibook.core.domain.services.TenantService;
import org.joda.time.DateTimeZone;

import javax.annotation.PostConstruct;
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

    @Inject
    private PropertiesService propertiesService;

    private Map<String, Tenant> allowedTenants;

    private Map<Tenant, DateTimeZone> dateTimeZones;

    @PostConstruct
    private void init()
    {
        refreshAllowedTenants();
        refreshDateTimeZones();
    }

    public void refreshAllowedTenants() {
        allowedTenants = new HashMap<>();

        for (Tenant tenant : tenantService.getAll()) {
            if (tenant.isActive())
                allowedTenants.put(tenant.getSlug(), tenant);
        }
    }

    public void refreshDateTimeZones() {
        dateTimeZones = new HashMap<>();

        for(Tenant tenant : tenantService.getAll()) {
            Property dateTimeZoneKey = propertiesService.getPropertyByKey(Property.Key.DATE_TIME_TIMEZONE, tenant);
            try {
                DateTimeZone dateTimeZone = DateTimeZone.forID(dateTimeZoneKey.getPropertyValue());
                dateTimeZones.put(tenant, dateTimeZone);
            } catch (Exception e) {
                dateTimeZones.put(tenant, DateTimeZone.UTC);
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
     * Returns the DateTimeZone for the specified tenant, as is set on the properties page by the administrator.
     * @param tenant The tenant to get the DateTimeZone of.
     * @return The DateTimeZone of the tenant.
     */
    public DateTimeZone getDateTimeZone(Tenant tenant) {
        return dateTimeZones.get(tenant);
    }
}
