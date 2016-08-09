/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.frontPage;

import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.services.ReservationService;
import com.aptitekk.agenda.core.util.ReservationScheduleEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Named
@ViewScoped
public class FrontPageController implements Serializable {

    @Inject
    private ReservationService reservationService;

    private LazyScheduleModel scheduleModel;

    private ReservationScheduleEvent selectedEvent;

    @PostConstruct
    private void init() {
        scheduleModel = new LazyScheduleModel() {

            @Override
            public void loadEvents(Date start, Date end) {
                //TODO: Create an expanding cache

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(start);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(end);

                List<Reservation> reservationList = reservationService.getAllBetweenDates(startCalendar, endCalendar);
                for (Reservation reservation : reservationList) {
                    ReservationScheduleEvent event = new ReservationScheduleEvent(reservation);

                    event.setEditable(false);
                    scheduleModel.addEvent(event);
                }
            }
        };
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ReservationScheduleEvent) selectEvent.getObject();
    }

    public ReservationScheduleEvent getSelectedEvent() {
        return selectedEvent;
    }
}
