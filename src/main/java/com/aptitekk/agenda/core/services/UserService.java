/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.User;

import javax.ejb.Local;

@Local
public interface UserService extends EntityService<User> {

    String ADMIN_USERNAME = "admin";
    String DEFAULT_ADMIN_PASSWORD = "admin";

    String SESSION_VAR_USERNAME = "username";

    /**
     * Finds User Entity by its username
     *
     * @param username
     * @return User where table.username = username
     */
    User findByName(String username);

    /**
     * Determines if the credentials are correct or not.
     *
     * @param username The username of the user to check.
     * @param password The password of the user to check (raw).
     * @return The User if the credentials are correct, or null if they are not.
     */
    User correctCredentials(String username, String password);

}
