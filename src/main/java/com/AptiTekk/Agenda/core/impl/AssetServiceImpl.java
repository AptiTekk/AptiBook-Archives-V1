package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.AssetService;
import com.AptiTekk.Agenda.core.AssetTypeService;
import com.AptiTekk.Agenda.core.entity.Asset;
import com.AptiTekk.Agenda.core.entity.AssetType;
import com.AptiTekk.Agenda.core.entity.QAsset;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class AssetServiceImpl extends EntityServiceAbstract<Asset> implements AssetService {

    private QAsset reservableTable = QAsset.asset;

    @Inject
    private AssetTypeService assetTypeService;

    public AssetServiceImpl() {
        super(Asset.class);
    }

    @Override
    public Asset findByName(String assetName) {
        return new JPAQuery<Asset>(entityManager).from(reservableTable).where(reservableTable.name.eq(assetName)).fetchOne();
    }

    @Override
    public List<Asset> getAllByType(AssetType assetType) {
        return new JPAQuery<Asset>(entityManager).from(reservableTable).where(reservableTable.type.eq(assetType)).fetch();
    }


}
