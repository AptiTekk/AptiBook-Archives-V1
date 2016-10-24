/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.MultiTenantEntity;
import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public abstract class MultiTenantEntityServiceAbstract<T extends MultiTenantEntity> extends EntityServiceAbstract<T> implements EntityService<T>, Serializable {

    @Inject
    private TenantSessionService tenantSessionService;

    private Tenant tenant;

    public Tenant getTenant() {
        return tenant;
    }

    @PostConstruct
    private void init() {
        tenant = tenantSessionService != null ? tenantSessionService.getCurrentTenant() : null;
    }

    @Override
    public void insert(T entity) throws Exception {
        if (entity == null)
            throw new Exception("Entity was null.");
        else if (entity.getTenant() == null)
            entity.setTenant(tenant);

        super.insert(entity);
    }

    @Override
    public List<T> getAll() {
        return getAll(tenant);
    }

    public List<T> getAll(Tenant tenant) {
        TypedQuery<T> query = this.entityManager.createQuery("SELECT e FROM " + this.entityType.getSimpleName() + " e WHERE e.tenant = :tenant", entityType).setParameter("tenant", tenant);
        return query.getResultList();
    }

}
