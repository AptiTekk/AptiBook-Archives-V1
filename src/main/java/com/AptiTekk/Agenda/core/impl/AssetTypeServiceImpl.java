package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.AssetTypeService;
import com.AptiTekk.Agenda.core.entity.AssetType;
import com.AptiTekk.Agenda.core.entity.QAssetType;
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
        return new JPAQuery<AssetType>().from(table).where(table.name.eq(name)).fetchOne();
    }

}
