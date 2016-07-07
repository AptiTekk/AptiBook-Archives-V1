/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.QReservationField;
import com.aptitekk.agenda.core.entity.ReservationField;
import com.aptitekk.agenda.core.services.ReservationFieldService;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;

@Stateless
public class ReservationFieldServiceImpl extends EntityServiceAbstract<ReservationField> implements ReservationFieldService, Serializable {

    QReservationField table = QReservationField.reservationField;

    public ReservationFieldServiceImpl() {
        super(ReservationField.class);
    }

    @Override
    public List<ReservationField> getByType(AssetType type) {
        return new JPAQuery<ReservationField>(entityManager).from(table).where(((type == null) ? table.assetType.isNull() : table.assetType.eq(type)))
                .fetch();
    }
}
