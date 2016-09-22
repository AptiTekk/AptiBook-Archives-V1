/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.components.primeFaces.schedule;

import com.aptitekk.aptibook.core.domain.entities.Reservation;
import org.primefaces.model.DefaultScheduleEvent;

public class ReservationScheduleEvent extends DefaultScheduleEvent {

    private final Reservation reservation;

    public ReservationScheduleEvent(Reservation reservation) {
        super();

        this.reservation = reservation;

        //Set Title
        setTitle(reservation.getAsset().getName() + " - " + (reservation.getTitle() != null ? reservation.getTitle() : "No Title."));

        //Set Time
        setStartDate(reservation.getStartTime().toDate());
        setEndDate(reservation.getEndTime().toDate());
    }

    public Reservation getReservation() {
        return reservation;
    }
}
