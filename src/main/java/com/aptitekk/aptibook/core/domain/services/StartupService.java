/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Tenant;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;

@Startup
@Singleton
@ApplicationScoped
public class StartupService implements Serializable {

    @Inject
    private TenantService tenantService;

    @PostConstruct
    public void init() {
        for (Tenant tenant : tenantService.getAll()) {
            tenantService.ensureTenantIntegrity(tenant);
        }
    }


}
