/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.superClasses.MultiTenantEntity;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.MultiTenantEntityService;
import com.aptitekk.agenda.core.tenants.TenantSessionService;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class MultiTenantEntityServiceAbstract<T extends MultiTenantEntity> implements MultiTenantEntityService<T>, Serializable {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    private TenantSessionService tenantSessionService;

    private Class<T> entityType;

    private Tenant tenant;

    public Tenant getTenant() {
        return tenant;
    }

    MultiTenantEntityServiceAbstract() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        //noinspection unchecked
        this.entityType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    @PostConstruct
    private void init() {
        tenant = tenantSessionService != null ? tenantSessionService.getCurrentTenant() : null;
    }

    @Override
    public void insert(T o) throws Exception {
        insert(o, tenant);
    }

    @Override
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

    @Override
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
