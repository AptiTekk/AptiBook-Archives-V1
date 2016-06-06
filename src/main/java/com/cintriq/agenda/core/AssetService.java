package com.cintriq.agenda.core;

import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;

import javax.ejb.Local;
import java.util.List;

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
