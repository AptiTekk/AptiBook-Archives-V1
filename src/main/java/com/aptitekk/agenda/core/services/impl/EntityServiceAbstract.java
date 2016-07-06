/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.services.EntityService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public abstract class EntityServiceAbstract<T> implements EntityService<T> {

    @PersistenceContext(unitName = "Agenda")
    EntityManager entityManager;

    Class<T> entityType;

    EntityServiceAbstract(Class<T> entityType) {
        this.entityType = entityType;
    }

    protected EntityServiceAbstract() {
    }

    @Override
    public void insert(T o) throws Exception {
        this.entityManager.persist(o);
    }

    @Override
    public T get(int id) {
        return this.entityManager.find(this.entityType, id);
    }

    @Override
    public List<T> getAll() {
        Query query = this.entityManager.createQuery("SELECT e FROM " + this.entityType.getSimpleName() + " e");
        return query.getResultList();
    }

    @Override
    public void update(T newEntity, int id) throws Exception {
        T old = entityManager.find(entityType, id);
        if (old != null) {
            old = newEntity;
        }
        merge(old);
    }

    @Override
    public void delete(int id) throws Exception {
        T entity = entityManager.getReference(entityType, id);
        if (entity != null) {
            entityManager.remove(entity);
        } else {
            throw new Exception("Entity was not found");
        }
    }

    @Override
    public T merge(T entity) throws Exception {
        return entityManager.merge(entity);
    }

}
