/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenant;

import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;
import java.util.Set;

@Local
public interface TenantManagementService {

    /**
     * @return A Set of valid tenant slugs.
     */
    Set<String> getAllowedTenantSlugs();

    /**
     * Reloads the cached Tenant EntityManagerFactories and allowed Tenant pattern using the current database entries.
     */
    void refreshTenants();

    /**
     * Returns a Tenant based on the slug provided.
     *
     * @param tenantSlug The slug of the Tenant.
     * @return The Tenant with the corresponding slug, or null.
     */
    Tenant getTenantBySlug(String tenantSlug);
}
