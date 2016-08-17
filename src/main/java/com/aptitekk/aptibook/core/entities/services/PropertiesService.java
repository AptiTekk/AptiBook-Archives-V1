/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.entities.services;

import com.aptitekk.aptibook.core.entities.Property;
import com.aptitekk.aptibook.core.entities.Tenant;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class PropertiesService extends MultiTenantEntityServiceAbstract<Property> implements Serializable {

    /**
     * Gets the Property Entity that matches the Property Key, within the current Tenant.
     *
     * @param propertyKey The Property Key.
     * @return the Property Entity if found, null otherwise.
     */
    public Property getPropertyByKey(Property.Key propertyKey) {
        return getPropertyByKey(propertyKey, getTenant());
    }

    /**
     * Gets the Property Entity that matches the Property Key, within the specified Tenant.
     *
     * @param propertyKey The Property Key.
     * @param tenant      The Tenant of the Property to search for.
     * @return the Property Entity if found, null otherwise.
     */
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
