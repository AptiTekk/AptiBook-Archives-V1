/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenants.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.TenantService;
import com.aptitekk.agenda.core.tenants.TenantSessionService;

import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class TenantSessionServiceImpl implements TenantSessionService {

    @Inject
    private TenantService tenantService;

    private Tenant currentTenant;

    @Override
    public Tenant getCurrentTenant() {
        return currentTenant;
    }

    @Override
    public void setCurrentTenant(String tenant) {
        try {
            int subscriptionId = Integer.parseInt(tenant);
            if (currentTenant == null || currentTenant.getSubscriptionId() != subscriptionId)
                currentTenant = tenantService.getTenantBySubscriptionId(subscriptionId);

        } catch (NumberFormatException ignored) {
        }
    }
}
