/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.AssetType;
import com.aptitekk.agenda.core.entities.QAssetType;
import com.aptitekk.agenda.core.services.AssetTypeService;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import java.io.Serializable;

@Stateless
public class AssetTypeServiceImpl extends EntityServiceAbstract<AssetType> implements AssetTypeService, Serializable {

    QAssetType table = QAssetType.assetType;

    public AssetTypeServiceImpl() {
        super(AssetType.class);
    }

    @Override
    public AssetType findByName(String name) {
        if (name == null)
            return null;

        return new JPAQuery<AssetType>(entityManager).from(table).where(table.name.equalsIgnoreCase(name)).fetchOne();
    }

}
