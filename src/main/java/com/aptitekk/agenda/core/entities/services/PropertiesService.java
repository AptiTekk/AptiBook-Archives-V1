/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;

@Local
public interface PropertiesService extends MultiTenantEntityService<Property> {

    /**
     * Gets the Property Entity that matches the Property Key, within the current Tenant.
     *
     * @param propertyKey The Property Key.
     * @return the Property Entity if found, null otherwise.
     */
    Property getPropertyByKey(Property.Key propertyKey);

    /**
     * Gets the Property Entity that matches the Property Key, within the specified Tenant.
     *
     * @param propertyKey The Property Key.
     * @param tenant      The Tenant of the Property to search for.
     * @return the Property Entity if found, null otherwise.
     */
    Property getPropertyByKey(Property.Key propertyKey, Tenant tenant);

}
