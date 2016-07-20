/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.AssetType;
import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;

@Local
public interface AssetTypeService extends MultiTenantEntityService<AssetType> {

    /**
     * Finds AssetType by its name, within the current Tenant.
     *
     * @param assetTypeName The name of the AssetType
     * @return An AssetType with the specified name, or null if one does not exist.
     */
    AssetType findByName(String assetTypeName);

    /**
     * Finds AssetType by its name, within the supplied Tenant.
     *
     * @param assetTypeName The name of the AssetType
     * @param tenant        The Tenant of the AssetType being searched for.
     * @return An AssetType with the specified name, or null if one does not exist.
     */
    AssetType findByName(String assetTypeName, Tenant tenant);

}
