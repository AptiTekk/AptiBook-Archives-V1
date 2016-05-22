package com.AptiTekk.Agenda.core.impl;

import javax.ejb.Stateless;

import com.AptiTekk.Agenda.core.AssetTypeService;
import com.AptiTekk.Agenda.core.entity.AssetType;
import com.AptiTekk.Agenda.core.entity.QReservableType;
import com.mysema.query.jpa.impl.JPAQuery;

@Stateless
public class AssetTypeServiceImpl extends EntityServiceAbstract<AssetType> implements AssetTypeService {

    QReservableType table = QReservableType.reservableType;

    public AssetTypeServiceImpl() {
        super(AssetType.class);
    }

    @Override
    public AssetType findByName(String name) {
        return new JPAQuery(entityManager).from(table).where(table.name.eq(name))
                .singleResult(table);
    }

}
