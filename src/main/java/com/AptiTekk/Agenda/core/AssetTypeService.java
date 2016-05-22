package com.AptiTekk.Agenda.core;

import javax.ejb.Local;

import com.AptiTekk.Agenda.core.entity.AssetType;

@Local
public interface AssetTypeService extends EntityService<AssetType> {
  
  /**
   * Finds AssetType by its name
   * 
   * @param assetTypeName The name of the AssetType
   * 
   * @return An AssetType with the specified name, or null if one does not exist.
   */
  AssetType findByName(String assetTypeName);
  
}
