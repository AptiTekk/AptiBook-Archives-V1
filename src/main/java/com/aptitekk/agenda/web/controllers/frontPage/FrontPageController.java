/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.frontPage;

import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.services.PropertiesService;
import com.aptitekk.agenda.core.entities.services.ReservationService;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class FrontPageController implements Serializable {

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private ReservationService reservationService;

    private LazyScheduleModel scheduleModel;

    private Property policies;

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
                    DefaultScheduleEvent event = new DefaultScheduleEvent();
                    event.setTitle(reservation.getAsset().getName() + " - " + (reservation.getTitle() != null ? reservation.getTitle() : "No Title."));

                    SegmentedTimeRange timeRange = new SegmentedTimeRange(reservation.getDate(), reservation.getTimeStart(), reservation.getTimeEnd());
                    event.setStartDate(timeRange.getDateWithStartTime().getTime());
                    event.setEndDate(timeRange.getDateWithEndTime().getTime());

                    event.setEditable(false);
                    scheduleModel.addEvent(event);
                }
            }
        };

        this.policies = propertiesService.getPropertyByKey(Property.Key.POLICY_BOX);
    }

    public String getPolicies() {
        return (policies != null ? policies.getPropertyValue() : null);
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }
}
