/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.services.AssetCategoryService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class AssetServiceImpl extends MultiTenantEntityServiceAbstract<Asset> implements AssetService, Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    @Override
    public Asset findByName(String assetName) {
        return findByName(assetName, getTenant());
    }

    @Override
    public Asset findByName(String assetName, Tenant tenant) {
        if (assetName == null || tenant == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT a FROM Asset a WHERE a.name = :assetName AND a.tenant = :tenant", Asset.class)
                    .setParameter("assetName", assetName)
                    .setParameter("tenant", tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
