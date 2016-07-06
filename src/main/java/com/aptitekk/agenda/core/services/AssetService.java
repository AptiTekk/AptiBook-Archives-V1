/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.Asset;

import javax.ejb.Local;

@Local
public interface AssetService extends EntityService<Asset> {

    /**
     * Finds Asset by its name
     *
     * @param assetName The name of the Asset.
     * @return An Asset with the specified name, or null if one does not exist.
     */
    Asset findByName(String assetName);

}
