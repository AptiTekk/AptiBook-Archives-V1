/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.TenantService;

import javax.ejb.Stateless;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateless
public class TenantServiceImpl extends EntityServiceAbstract<Tenant> implements TenantService, Serializable {

    @Override
    public Tenant getTenantBySubscriptionId(int subscriptionId) {
        try {
            return entityManager
                    .createQuery("SELECT t FROM Tenant t WHERE t.subscriptionId = ?1", Tenant.class)
                    .setParameter(1, subscriptionId)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public Tenant getTenantBySlug(String slug) {
        try {
            return entityManager
                    .createQuery("SELECT t FROM Tenant t WHERE t.slug = ?1", Tenant.class)
                    .setParameter(1, slug)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
