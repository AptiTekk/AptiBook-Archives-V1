/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.primeFaces.schedule;

import com.aptitekk.aptibook.core.domain.entities.Reservation;
import org.primefaces.model.LazyScheduleModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class ReservationScheduleModel extends LazyScheduleModel {

    @Override
    public void loadEvents(Date start, Date end) {
        //TODO: Create an expanding cache

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);

        List<Reservation> reservationList = getReservationsBetweenDates(startCalendar, endCalendar);
        if (reservationList != null) {
            for (Reservation reservation : reservationList) {
                ReservationScheduleEvent event = new ReservationScheduleEvent(reservation);

                event.setEditable(false);
                addEvent(event);
            }
        }
    }

    public abstract List<Reservation> getReservationsBetweenDates(Calendar start, Calendar end);

}
