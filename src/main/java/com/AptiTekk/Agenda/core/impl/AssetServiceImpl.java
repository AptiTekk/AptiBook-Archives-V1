package com.AptiTekk.Agenda.core.impl;

import javax.ejb.Stateless;

import com.AptiTekk.Agenda.core.AssetService;
import com.AptiTekk.Agenda.core.AssetTypeService;
import com.AptiTekk.Agenda.core.entity.QReservable;
import com.AptiTekk.Agenda.core.entity.Asset;
import com.AptiTekk.Agenda.core.entity.AssetType;
import com.mysema.query.jpa.impl.JPAQuery;
import java.util.List;
import javax.inject.Inject;

@Stateless
public class AssetServiceImpl extends EntityServiceAbstract<Asset> implements AssetService {

    private QReservable reservableTable = QReservable.reservable;
    
    @Inject
    private AssetTypeService assetTypeService;

    public AssetServiceImpl() {
        super(Asset.class);
    }

    @Override
    public Asset findByName(String roomName) {
        return new JPAQuery(entityManager).from(reservableTable).where(reservableTable.name.eq(roomName))
                .singleResult(reservableTable);
    }

    @Override
    public List<Asset> getAllByType(AssetType type) {
        return new JPAQuery(entityManager).from(reservableTable).where(reservableTable.type.eq(type)).list(reservableTable);
    }
    
    

}
