/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.util.Sha256Helper;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class UserService extends MultiTenantEntityServiceAbstract<User> implements Serializable {

    public static final String ADMIN_USERNAME = "admin";
    static final String DEFAULT_ADMIN_PASSWORD = "admin";


    /**
     * Finds User Entity by its username, within the current Tenant.
     *
     * @param verificationCode The verification code of the User to search for.
     * @return A User Entity with the specified registration code, or null if one does not exist.
     */
    public User findByCode(String verificationCode) {
        return findByCode(verificationCode, getTenant());
    }

    /**
     * Finds User Entity by its username, within the current Tenant.
     *
     * @param verificationCode The verification code of the User to search for.
     * @param tenant           The Tenant of the User to search for.
     * @return A User Entity with the specified registration code, or null if one does not exist.
     */
    public User findByCode(String verificationCode, Tenant tenant) {
        if (verificationCode == null || tenant == null) {
            return null;
        }
        try {
            return entityManager
                    .createQuery("SELECT u FROM User u WHERE u.verificationCode = :verificationCode AND u.tenant = :tenant", User.class)
                    .setParameter("verificationCode", verificationCode)
                    .setParameter("tenant", tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    /**
     * Finds User Entity by its username, within the current Tenant.
     *
     * @param username The username of the User to search for.
     * @return A User Entity with the specified username, or null if one does not exist.
     */
    public User findByName(String username) {
        return findByName(username, getTenant());
    }

    /**
     * Finds User Entity by its username, within the specified Tenant.
     *
     * @param username The username of the User to search for.
     * @param tenant   The Tenant of the User to search for.
     * @return A User Entity with the specified username, or null if one does not exist.
     */
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

    /**
     * Determines if the credentials are correct or not for the current Tenant.
     *
     * @param username The username of the user to check.
     * @param password The password of the user to check (raw).
     * @return The User if the credentials are correct, or null if they are not.
     */
    public User getUserWithCredentials(String username, String password) {
        if (username == null || password == null || getTenant() == null) {
            return null;
        }

        byte[] hashedPassword = Sha256Helper.rawToSha(password);
        if (hashedPassword == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password AND u.tenant = :tenant AND u.userState = :userState", User.class)
                    .setParameter("username", username)
                    .setParameter("password", hashedPassword)
                    .setParameter("tenant", getTenant())
                    .setParameter("userState", User.Key.APPROVED)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

}
