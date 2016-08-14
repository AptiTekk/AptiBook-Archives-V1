/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Tag;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class TagService extends MultiTenantEntityServiceAbstract<Tag> implements Serializable {

    public Tag findByName(AssetCategory assetCategory, String name) {
        if (assetCategory == null || name == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT t FROM Tag t WHERE t.assetCategory = ?1 AND t.name = ?2", Tag.class)
                    .setParameter(1, assetCategory)
                    .setParameter(2, name)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

}