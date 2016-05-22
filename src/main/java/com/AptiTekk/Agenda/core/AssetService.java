package com.AptiTekk.Agenda.core;

import javax.ejb.Local;

import com.AptiTekk.Agenda.core.entity.Asset;
import com.AptiTekk.Agenda.core.entity.AssetType;

import java.util.List;

@Local
public interface AssetService extends EntityService<Asset> {
  
  /**
   * Finds Asset by its name
   * 
   * @param assetName The name of the Asset.
   * 
   * @return An Asset with the specified name, or null if one does not exist.
   */
  Asset findByName(String assetName);
  
  List<Asset> getAllByType(AssetType type);

}
