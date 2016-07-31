/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Stateful
public class AssetService extends MultiTenantEntityServiceAbstract<Asset> implements Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    /**
     * Finds Asset by its name within an Asset Category.
     *
     * @param name          The name of the Asset.
     * @param assetCategory The Asset Category to search within.
     * @return An Asset with the specified name, or null if one does not exist.
     */
    public Asset findByName(String name, AssetCategory assetCategory) {
        if (name == null || assetCategory == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT a FROM Asset a WHERE a.name = ?1 AND a.assetCategory = ?2 AND a.tenant = ?3", Asset.class)
                    .setParameter(1, name)
                    .setParameter(2, assetCategory)
                    .setParameter(3, getTenant())
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public List<Asset> getAll() {
        List<Asset> assets = super.getAll();
        assets.sort(new AssetComparator());
        return assets;
    }

    @Override
    public List<Asset> getAll(Tenant tenant) {
        List<Asset> assets = super.getAll(tenant);
        assets.sort(new AssetComparator());
        return assets;
    }

    /**
     * Sorts Asset Categories by name.
     */
    private class AssetComparator implements Comparator<Asset> {

        @Override
        public int compare(Asset o1, Asset o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
