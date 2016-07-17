/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.TenantService;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateless
public class TenantServiceImpl extends EntityServiceAbstract<Tenant> implements TenantService, Serializable {

    @Override
    public Tenant getTenantBySubscriptionId(int subscriptionId) {
        try {
            return entityManager.createNamedQuery("Tenant.getBySubscriptionId", entityType).setParameter("subscriptionId", subscriptionId).getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
