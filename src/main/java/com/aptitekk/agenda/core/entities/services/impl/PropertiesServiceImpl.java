/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services.impl;

import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.services.PropertiesService;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class PropertiesServiceImpl extends MultiTenantEntityServiceAbstract<Property> implements PropertiesService, Serializable {

    @Override
    public Property getPropertyByKey(Property.Key propertyKey) {
        return getPropertyByKey(propertyKey, getTenant());
    }

    @Override
    public Property getPropertyByKey(Property.Key propertyKey, Tenant tenant) {
        if (propertyKey == null || tenant == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT p FROM Property p WHERE p.propertyKey = :propertyKey AND p.tenant = :tenant", Property.class)
                    .setParameter("propertyKey", propertyKey)
                    .setParameter("tenant", tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
