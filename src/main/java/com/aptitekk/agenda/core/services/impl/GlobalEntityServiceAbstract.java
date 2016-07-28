/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.util.GlobalEntity;
import com.aptitekk.agenda.core.services.GlobalEntityService;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

abstract class GlobalEntityServiceAbstract<T extends GlobalEntity> implements GlobalEntityService<T>, Serializable {

    @PersistenceContext
    EntityManager entityManager;

    private Class<T> entityType;

    GlobalEntityServiceAbstract() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        //noinspection unchecked
        this.entityType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
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
        TypedQuery<T> query = this.entityManager.createQuery("SELECT e FROM " + this.entityType.getSimpleName() + " e", entityType);
        return query.getResultList();
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