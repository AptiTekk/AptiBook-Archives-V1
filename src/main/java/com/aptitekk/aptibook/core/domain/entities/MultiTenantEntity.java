/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.domain.entities.Tenant;

import javax.persistence.*;

@MappedSuperclass
public abstract class MultiTenantEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
