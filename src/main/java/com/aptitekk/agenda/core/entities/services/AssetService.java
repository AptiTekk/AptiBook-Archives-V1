/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class AssetService extends MultiTenantEntityServiceAbstract<Asset> implements Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    /**
     * Finds Asset by its name, within the current Tenant.
     *
     * @param assetName The name of the Asset.
     * @return An Asset with the specified name, or null if one does not exist.
     */
    public Asset findByName(String assetName) {
        return findByName(assetName, getTenant());
    }

    /**
     * Finds Asset by its name, within the supplied Tenant.
     *
     * @param assetName The name of the Asset.
     * @param tenant    The Tenant of the Asset being searched for.
     * @return An Asset with the specified name, or null if one does not exist.
     */
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
