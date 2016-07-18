/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.properties.PropertyKey;

import javax.ejb.Local;

@Local
public interface PropertiesService extends MultiTenantEntityService<Property> {

    /**
     * Gets the Property Entity that matches the PropertyKey, within the current Tenant.
     *
     * @param propertyKey The PropertyKey.
     * @return the Property Entity if found, null otherwise.
     */
    Property getPropertyByKey(PropertyKey propertyKey);

    /**
     * Gets the Property Entity that matches the PropertyKey, within the specified Tenant.
     *
     * @param propertyKey The PropertyKey.
     * @param tenant   The Tenant of the Property to search for.
     * @return the Property Entity if found, null otherwise.
     */
    Property getPropertyByKey(PropertyKey propertyKey, Tenant tenant);

}
