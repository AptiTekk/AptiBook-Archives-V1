/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.entities.services;

import com.aptitekk.aptibook.core.entities.AssetCategory;
import com.aptitekk.aptibook.core.entities.Tenant;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Stateful
public class AssetCategoryService extends MultiTenantEntityServiceAbstract<AssetCategory> implements Serializable {

    /**
     * Finds AssetCategory by its name, within the current Tenant.
     *
     * @param assetCategoryName The name of the AssetCategory
     * @return An AssetCategory with the specified name, or null if one does not exist.
     */
    public AssetCategory findByName(String assetCategoryName) {
        return findByName(assetCategoryName, getTenant());
    }

    /**
     * Finds AssetCategory by its name, within the supplied Tenant.
     *
     * @param assetCategoryName The name of the AssetCategory
     * @param tenant            The Tenant of the AssetCategory being searched for.
     * @return An AssetCategory with the specified name, or null if one does not exist.
     */
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

    @Override
    public List<AssetCategory> getAll() {
        List<AssetCategory> assetCategories = super.getAll();
        assetCategories.sort(new AssetCategoryComparator());
        return assetCategories;
    }

    @Override
    public List<AssetCategory> getAll(Tenant tenant) {
        List<AssetCategory> assetCategories = super.getAll(tenant);
        assetCategories.sort(new AssetCategoryComparator());
        return assetCategories;
    }

    /**
     * Sorts Asset Categories by name.
     */
    private class AssetCategoryComparator implements Comparator<AssetCategory> {

        @Override
        public int compare(AssetCategory o1, AssetCategory o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
