/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.GlobalEntity;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public abstract class EntityServiceAbstract<T> implements EntityService<T>, Serializable {

    Class<T> entityType;

    @PostConstruct
    private void init() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        //noinspection unchecked
        this.entityType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void insert(T entity) throws Exception {
        this.entityManager.persist(entity);
    }

    @Override
    public T get(int id) {
        return this.entityManager.find(this.entityType, id);
    }

    @Override
    public void delete(T entity) throws Exception {
        if (entity != null)  entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        else throw new Exception("Entity was null.");
    }

    @Override
    public T merge(T entity) throws Exception {
        return entityManager.merge(entity);
    }

}
