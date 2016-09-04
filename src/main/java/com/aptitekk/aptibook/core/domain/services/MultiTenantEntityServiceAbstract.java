/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.MultiTenantEntity;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class MultiTenantEntityServiceAbstract<T extends MultiTenantEntity> implements EntityService<T>, Serializable {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    private TenantSessionService tenantSessionService;

    private Class<T> entityType;

    private Tenant tenant;

    public Tenant getTenant() {
        return tenant;
    }

    @PostConstruct
    private void init() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        //noinspection unchecked
        this.entityType = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        tenant = tenantSessionService != null ? tenantSessionService.getCurrentTenant() : null;
    }

    @Override
    public void insert(T o) throws Exception {
        insert(o, tenant);
    }

    public void insert(T o, Tenant tenant) throws Exception {
        if (o == null)
            throw new Exception("Entity was null.");

        o.setTenant(tenant);
        this.entityManager.persist(o);
    }

    @Override
    public T get(int id) {
        return this.entityManager.find(this.entityType, id);
    }

    @Override
    public List<T> getAll() {
        return getAll(tenant);
    }

    public List<T> getAll(Tenant tenant) {
        TypedQuery<T> query = this.entityManager.createQuery("SELECT e FROM " + this.entityType.getSimpleName() + " e WHERE e.tenant = :tenant", entityType).setParameter("tenant", tenant);
        return query.getResultList();
    }

    @Override
    public void delete(int id) throws Exception {
        T entity = entityManager.getReference(entityType, id);
        if (entity != null) {
            entityManager.remove(entity);
        } else {
            throw new Exception("Entity was not found.");
        }
    }

    @Override
    public T merge(T entity) throws Exception {
        return entityManager.merge(entity);
    }

}
