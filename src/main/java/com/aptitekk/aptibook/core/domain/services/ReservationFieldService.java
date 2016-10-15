/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.ReservationField;

import javax.ejb.Stateful;
import java.io.Serializable;
import java.util.List;

@Stateful
public class ReservationFieldService extends MultiTenantEntityServiceAbstract<ReservationField> implements Serializable {

    public List<ReservationField> getAllForResourceCategory(ResourceCategory resourceCategory) {
        return entityManager
                .createQuery("SELECT rf FROM ReservationField rf WHERE rf.resourceCategory = ?1 AND rf.tenant = ?2", ReservationField.class)
                .setParameter(1, resourceCategory)
                .setParameter(2, getTenant())
                .getResultList();
    }

    public List<ReservationField> findByTitle(String title, ResourceCategory resourceCategory) {
        if (title == null || resourceCategory == null)
            return null;

        return entityManager
                .createQuery("SELECT r FROM ReservationField r WHERE r.title = ?1 AND r.resourceCategory = ?2 AND r.tenant = ?3", ReservationField.class)
                .setParameter(1, title)
                .setParameter(2, resourceCategory)
                .setParameter(3, getTenant())
                .getResultList();
    }
}
