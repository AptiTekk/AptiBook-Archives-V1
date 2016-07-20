/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.AssetType;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.services.AssetTypeService;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class AssetTypeServiceImpl extends MultiTenantEntityServiceAbstract<AssetType> implements AssetTypeService, Serializable {

    @Override
    public AssetType findByName(String assetTypeName) {
        return findByName(assetTypeName, getTenant());
    }

    @Override
    public AssetType findByName(String assetTypeName, Tenant tenant) {
        if (assetTypeName == null || tenant == null)
            return null;

        try {
        return entityManager
                .createQuery("SELECT a FROM AssetType a WHERE a.name = :assetTypeName AND a.tenant = :tenant", AssetType.class)
                .setParameter("assetTypeName", assetTypeName)
                .setParameter("tenant", tenant)
                .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
