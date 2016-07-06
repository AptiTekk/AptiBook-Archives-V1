/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.AssetType;

import javax.ejb.Local;

@Local
public interface AssetTypeService extends EntityService<AssetType> {

    /**
     * Finds AssetType by its name
     *
     * @param assetTypeName The name of the AssetType
     * @return An AssetType with the specified name, or null if one does not exist.
     */
    AssetType findByName(String assetTypeName);

}
