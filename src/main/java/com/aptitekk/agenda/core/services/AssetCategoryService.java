/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;

@Local
public interface AssetCategoryService extends MultiTenantEntityService<AssetCategory> {

    /**
     * Finds AssetCategory by its name, within the current Tenant.
     *
     * @param assetCategoryName The name of the AssetCategory
     * @return An AssetCategory with the specified name, or null if one does not exist.
     */
    AssetCategory findByName(String assetCategoryName);

    /**
     * Finds AssetCategory by its name, within the supplied Tenant.
     *
     * @param assetCategoryName The name of the AssetCategory
     * @param tenant        The Tenant of the AssetCategory being searched for.
     * @return An AssetCategory with the specified name, or null if one does not exist.
     */
    AssetCategory findByName(String assetCategoryName, Tenant tenant);

}
