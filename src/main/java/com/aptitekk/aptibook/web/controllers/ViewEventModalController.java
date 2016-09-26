/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.web.components.primeFaces.schedule.ReservationScheduleEvent;
import org.primefaces.event.SelectEvent;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class ViewEventModalController implements Serializable {

    private ReservationScheduleEvent selectedEvent;

    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ReservationScheduleEvent) selectEvent.getObject();
    }

    public ReservationScheduleEvent getSelectedEvent() {
        return selectedEvent;
    }

}
