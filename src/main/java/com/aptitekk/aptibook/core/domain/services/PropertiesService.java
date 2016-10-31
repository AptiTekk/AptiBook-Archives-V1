/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.domain.entities.Tenant;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;

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


    /**
     * Gets the Property Entities whose Keys are assigned to the specified Property Group, within the current Tenant.
     *
     * @param propertyGroup The Property Group to filter by.
     * @return A list containing all Property Entities that are within the Group.
     */
    public List<Property> getAllPropertiesByGroup(Property.Group propertyGroup) {
        return getAllPropertiesByGroup(propertyGroup, getTenant());
    }

    /**
     * Gets the Property Entities whose Keys are assigned to the specified Property Group, within the specified Tenant.
     *
     * @param propertyGroup The Property Group to filter by.
     * @param tenant        The Tenant of the Properties to search for.
     * @return A list containing all Property Entities that are within the Group.
     */
    public List<Property> getAllPropertiesByGroup(Property.Group propertyGroup, Tenant tenant) {
        if (propertyGroup == null || tenant == null)
            return null;

        try {
            return entityManager.createQuery("SELECT p from Property p WHERE p.propertyKey IN :propertyKeys AND p.tenant = :tenant", Property.class)
                    .setParameter("propertyKeys", propertyGroup.getKeys())
                    .setParameter("tenant", tenant)
                    .getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
