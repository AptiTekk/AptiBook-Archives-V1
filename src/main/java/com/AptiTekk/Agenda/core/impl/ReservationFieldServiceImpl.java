package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.ReservationFieldService;
import com.AptiTekk.Agenda.core.entity.QReservationField;
import com.AptiTekk.Agenda.core.entity.ReservationField;

/**
 * Created by kevint on 5/18/2016.
 */
public class ReservationFieldServiceImpl extends EntityServiceAbstract<ReservationField> implements ReservationFieldService {

    QReservationField table = QReservationField.reservationField;

    public ReservationFieldServiceImpl() {
        super(ReservationField.class);
    }

}
