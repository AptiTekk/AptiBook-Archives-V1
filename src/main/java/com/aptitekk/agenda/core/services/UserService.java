/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.User;

import javax.ejb.Local;

@Local
public interface UserService extends MultiTenantEntityService<User> {

    String ADMIN_USERNAME = "admin";
    String DEFAULT_ADMIN_PASSWORD = "admin";

    /**
     * Finds User Entity by its username, within the current Tenant.
     *
     * @param username The username of the User to search for.
     * @return A User Entity with the specified username, or null if one does not exist.
     */
    User findByName(String username);

    /**
     * Finds User Entity by its username, within the specified Tenant.
     *
     * @param username The username of the User to search for.
     * @param tenant   The Tenant of the User to search for.
     * @return A User Entity with the specified username, or null if one does not exist.
     */
    User findByName(String username, Tenant tenant);

    /**
     * Determines if the credentials are correct or not for the current Tenant.
     *
     * @param username The username of the user to check.
     * @param password The password of the user to check (raw).
     * @return The User if the credentials are correct, or null if they are not.
     */
    User getUserWithCredentials(String username, String password);

}
