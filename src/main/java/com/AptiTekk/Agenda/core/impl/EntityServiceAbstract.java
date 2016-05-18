package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.EntityService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public abstract class EntityServiceAbstract<T> implements EntityService<T> {

    @PersistenceContext(unitName = "Agenda")
    protected EntityManager entityManager;

    protected Class<T> entityType;

    public EntityServiceAbstract(Class<T> entityType) {
        this.entityType = entityType;
    }

    protected EntityServiceAbstract() {
    }

    @Override
    public void insert(T o) {
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
    public void update(T newEntity, int id) {
        T old = entityManager.find(entityType, id);
        if (old != null) {
            old = newEntity;
        }
        merge(old);
    }

    @Override
    public void delete(int id) {
        T entity = entityManager.getReference(entityType, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public T merge(T entity) {
        return entityManager.merge(entity);
    }

}
