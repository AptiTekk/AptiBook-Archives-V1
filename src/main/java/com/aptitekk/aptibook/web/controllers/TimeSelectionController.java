/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.domain.services.ReservationService;
import org.joda.time.DateTime;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Named
@ViewScoped
public class TimeSelectionController implements Serializable {

    @Inject
    private ReservationService reservationService;

    private DateTime minDateTime = new DateTime().withHourOfDay(4).withMinuteOfHour(0).withSecondOfMinute(0);

    public DateTime getMinDateTime() {
        return minDateTime;
    }

    public boolean isToday(Date date) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTime(date);

        Calendar today = Calendar.getInstance();
        return selectedCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && selectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }
}
