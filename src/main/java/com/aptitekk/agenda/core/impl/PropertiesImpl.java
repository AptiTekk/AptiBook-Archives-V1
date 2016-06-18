package com.aptitekk.agenda.core.impl;

import com.aptitekk.agenda.core.Properties;
import com.aptitekk.agenda.core.entity.AppProperty;
import com.aptitekk.agenda.core.entity.QAppProperty;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PropertiesImpl implements Properties {

    @PersistenceContext(unitName = "Agenda")
    protected EntityManager entityManager;

    protected Class<AppProperty> entityType = AppProperty.class;

    QAppProperty table = QAppProperty.appProperty;

    public PropertiesImpl() {
    }

    @Override
    public String get(String key) {
        return new JPAQuery<AppProperty>(entityManager).from(table).where(table.propertyKey.eq(key))
                .fetchOne().getValue();
    }

    @Override
    public AppProperty getProperty(String key) {
        return new JPAQuery<AppProperty>(entityManager).from(table).where(table.propertyKey.eq(key))
                .fetchOne();
    }

    @Override
    public void put(String key, String value) {
        if (get(key) == null) {
            AppProperty property = new AppProperty(key, value);
            insert(property);
        } else {
            AppProperty property = getProperty(key);
            property.setValue(value);
            merge(property);
        }
    }

    @Override
    public List<AppProperty> getAll() {
        Query query = this.entityManager.createQuery("SELECT e FROM " + this.entityType.getSimpleName() + " e");
        return query.getResultList();
    }

    @Override
    public AppProperty merge(AppProperty property) {
        return entityManager.merge(property);
    }

    @Override
    public void insert(AppProperty property) {
        entityManager.persist(property);
    }

}
