/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenants;

import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Local
public interface TenantManagementService {

    /**
     * @return A Regex pattern to be used in matching the tenant portion of the request URL and ensuring only existing tenant URLs are accessible.
     */
    String getAllowedTenantUrlPattern();

    /**
     * Reloads the cached Tenant EntityManagerFactories and allowed Tenant pattern using the current database entries.
     */
    void refreshTenants();

}
