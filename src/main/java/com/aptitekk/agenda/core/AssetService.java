package com.aptitekk.agenda.core;

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
