/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;

@Local
public interface AssetService extends MultiTenantEntityService<Asset> {

    /**
     * Finds Asset by its name, within the current Tenant.
     *
     * @param assetName The name of the Asset.
     * @return An Asset with the specified name, or null if one does not exist.
     */
    Asset findByName(String assetName);

    /**
     * Finds Asset by its name, within the supplied Tenant.
     *
     * @param assetName The name of the Asset.
     * @param tenant    The Tenant of the Asset being searched for.
     * @return An Asset with the specified name, or null if one does not exist.
     */
    Asset findByName(String assetName, Tenant tenant);

}
