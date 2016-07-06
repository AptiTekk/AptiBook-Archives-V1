/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.entity.QAsset;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;

@Stateless
public class AssetServiceImpl extends EntityServiceAbstract<Asset> implements AssetService, Serializable {

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
