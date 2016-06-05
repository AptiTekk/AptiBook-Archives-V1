package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.QAssetType;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;

@Stateless
public class AssetTypeServiceImpl extends EntityServiceAbstract<AssetType> implements AssetTypeService {

    QAssetType table = QAssetType.assetType;

    public AssetTypeServiceImpl() {
        super(AssetType.class);
    }

    @Override
    public AssetType findByName(String name) {
        return new JPAQuery<AssetType>(entityManager).from(table).where(table.name.eq(name)).fetchOne();
    }

}
