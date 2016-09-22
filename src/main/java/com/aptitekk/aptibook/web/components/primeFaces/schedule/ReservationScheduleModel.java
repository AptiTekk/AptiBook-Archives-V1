/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.primeFaces.schedule;

import com.aptitekk.aptibook.core.domain.entities.Reservation;
import org.joda.time.DateTime;
import org.primefaces.model.LazyScheduleModel;

import java.util.Date;
import java.util.List;

public abstract class ReservationScheduleModel extends LazyScheduleModel {

    @Override
    public void loadEvents(Date start, Date end) {
        //TODO: Create an expanding cache

        List<Reservation> reservationList = getReservationsBetweenDates(new DateTime(start), new DateTime(end));
        if (reservationList != null) {
            for (Reservation reservation : reservationList) {
                ReservationScheduleEvent event = new ReservationScheduleEvent(reservation);

                event.setEditable(false);
                addEvent(event);
            }
        }
    }

    public abstract List<Reservation> getReservationsBetweenDates(DateTime start, DateTime end);

}
