/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.tenant;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.TenantService;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.ZoneId;

@Stateful
public class TenantSessionService implements Serializable {

    @Inject
    private TenantManagementService tenantManagementService;

    @Inject
    private TenantService tenantService;

    @Inject
    private HttpServletRequest httpRequest;

    @PostConstruct
    private void init() {
        try {
            if (httpRequest != null)
                httpRequest.getAttribute("tenant");
        } catch (Exception ignored) {
            httpRequest = null;
        }
    }

    public Tenant getCurrentTenant() {
        if (httpRequest != null) {
            Object attribute = httpRequest.getAttribute("tenant");
            if (attribute != null && attribute instanceof Tenant)
                return (Tenant) attribute;
        }

        return null;
    }

    public ZoneId getCurrentTenantZoneId() {
        Tenant tenant = getCurrentTenant();
        if (tenant != null) {
            return tenantManagementService.getZoneId(tenant);
        }
        return ZoneId.systemDefault();
    }


}
