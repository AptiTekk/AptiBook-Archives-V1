package com.aptitekk.agenda.core.impl;

import com.aptitekk.agenda.core.ReservationFieldService;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.QReservationField;
import com.aptitekk.agenda.core.entity.ReservationField;
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
