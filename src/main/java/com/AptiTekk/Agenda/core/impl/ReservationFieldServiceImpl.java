package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.ReservationFieldService;
import com.AptiTekk.Agenda.core.entity.QReservationField;
import com.AptiTekk.Agenda.core.entity.ReservableType;
import com.AptiTekk.Agenda.core.entity.ReservationField;
import com.mysema.query.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by kevint on 5/18/2016.
 */
@Stateless
public class ReservationFieldServiceImpl extends EntityServiceAbstract<ReservationField> implements ReservationFieldService {

    QReservationField table = QReservationField.reservationField;

    public ReservationFieldServiceImpl() {
        super(ReservationField.class);
    }

    @Override
    public List<ReservationField> getByType(ReservableType type) {
        return new JPAQuery(entityManager).from(table).where(table.reservableType.eq(type))
                .list(table);
    }
}
