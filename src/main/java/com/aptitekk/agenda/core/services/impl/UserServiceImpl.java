/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.util.Sha256Helper;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class UserServiceImpl extends MultiTenantEntityServiceAbstract<User> implements UserService, Serializable {

    @Override
    public User findByName(String username) {
        return findByName(username, getTenant());
    }

    @Override
    public User findByName(String username, Tenant tenant) {
        if (username == null || tenant == null) {
            return null;
        }

        try {
            return entityManager
                    .createQuery("SELECT u FROM User u WHERE u.username = :username AND u.tenant = :tenant", User.class)
                    .setParameter("username", username)
                    .setParameter("tenant", tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public User getUserWithCredentials(String username, String password) {
        if (username == null || password == null || getTenant() == null) {
            return null;
        }

        byte[] hashedPassword = Sha256Helper.rawToSha(password);
        if (hashedPassword == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password AND u.tenant = :tenant", User.class)
                    .setParameter("username", username)
                    .setParameter("password", hashedPassword)
                    .setParameter("tenant", getTenant())
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

}
