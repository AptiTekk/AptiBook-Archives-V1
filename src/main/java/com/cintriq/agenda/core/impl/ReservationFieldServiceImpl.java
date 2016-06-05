package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.ReservationFieldService;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.QReservationField;
import com.cintriq.agenda.core.entity.ReservationField;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class ReservationFieldServiceImpl extends EntityServiceAbstract<ReservationField> implements ReservationFieldService {

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
