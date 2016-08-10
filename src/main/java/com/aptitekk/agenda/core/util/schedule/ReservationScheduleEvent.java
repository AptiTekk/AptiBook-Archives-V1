/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.util.schedule;

import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;
import org.primefaces.model.DefaultScheduleEvent;

public class ReservationScheduleEvent extends DefaultScheduleEvent {

    private final Reservation reservation;

    public ReservationScheduleEvent(Reservation reservation) {
        super();

        this.reservation = reservation;

        //Set Title
        setTitle(reservation.getAsset().getName() + " - " + (reservation.getTitle() != null ? reservation.getTitle() : "No Title."));

        //Set Time
        SegmentedTimeRange timeRange = new SegmentedTimeRange(reservation.getDate(), reservation.getTimeStart(), reservation.getTimeEnd());
        setStartDate(timeRange.getDateWithStartTime().getTime());
        setEndDate(timeRange.getDateWithEndTime().getTime());
    }

    public Reservation getReservation() {
        return reservation;
    }
}
