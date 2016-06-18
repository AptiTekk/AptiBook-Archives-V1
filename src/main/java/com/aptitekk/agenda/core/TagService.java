package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.Tag;

import javax.ejb.Local;

@Local
public interface TagService extends EntityService<Tag> {

    Tag findByName(AssetType assetType, String tag);
}
