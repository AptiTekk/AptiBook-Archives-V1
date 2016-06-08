package com.cintriq.agenda.core;

import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Tag;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Local
public interface TagService extends EntityService<Tag> {

    Tag findByName(AssetType assetType, String tag);
}
