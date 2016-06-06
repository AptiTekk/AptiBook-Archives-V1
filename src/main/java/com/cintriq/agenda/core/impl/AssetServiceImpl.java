package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.AssetService;
import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.QAsset;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class AssetServiceImpl extends EntityServiceAbstract<Asset> implements AssetService {

    private QAsset assetTable = QAsset.asset;

    @Inject
    private AssetTypeService assetTypeService;

    public AssetServiceImpl() {
        super(Asset.class);
    }

    @Override
    public Asset findByName(String assetName) {
        return new JPAQuery<Asset>(entityManager).from(assetTable).where(assetTable.name.eq(assetName)).fetchOne();
    }

}
