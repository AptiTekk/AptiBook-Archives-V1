/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.AssetCategoryService;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class AssetCategoryServiceImpl extends MultiTenantEntityServiceAbstract<AssetCategory> implements AssetCategoryService, Serializable {

    @Override
    public AssetCategory findByName(String assetCategoryName) {
        return findByName(assetCategoryName, getTenant());
    }

    @Override
    public AssetCategory findByName(String assetCategoryName, Tenant tenant) {
        if (assetCategoryName == null || tenant == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT a FROM AssetCategory a WHERE a.name = ?1 AND a.tenant = ?2", AssetCategory.class)
                    .setParameter(1, assetCategoryName)
                    .setParameter(2, tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
