/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.primeFaces.schedule;

import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.time.SegmentedTimeRange;
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
