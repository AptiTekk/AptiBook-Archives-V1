/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.entities.services.ReservationService;
import com.aptitekk.aptibook.core.util.time.CalendarRange;
import com.aptitekk.aptibook.core.util.time.SegmentedTime;
import com.aptitekk.aptibook.core.util.time.SegmentedTimeRange;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class TimeSelectionController implements Serializable {

    private List<SegmentedTime> startTimes;
    private List<SegmentedTime> prunedTimes;

    private List<SegmentedTime> endTimes;

    /**
     * Used in getEndTimes to ensure that only one set of times is generated for the given start-time.
     */
    private SegmentedTime lastStartTimeUsedForCalculation;

    private List<SegmentedTime> allowedTimeSegments;

    @Inject
    private ReservationService reservationService;

    @PostConstruct
    public void init() {
        //From 12:00 AM to 11:30 PM
        SegmentedTimeRange allowedTimeRange = new SegmentedTimeRange(null, new SegmentedTime(0, false), new SegmentedTime(22, true));

        //Build a list of all TimeSegments that can be selected
        allowedTimeSegments = new ArrayList<>();
        SegmentedTime counterTime = (SegmentedTime) allowedTimeRange.getStartTime().clone();

        while (counterTime.getCurrentSegment() <= allowedTimeRange.getEndTime().getCurrentSegment()) {
            allowedTimeSegments.add((SegmentedTime) counterTime.clone());
            counterTime.increaseSegment();
        }
        startTimes = allowedTimeSegments.subList(0, allowedTimeSegments.size() - 1);

        SegmentedTime currentSegmentedTime = new SegmentedTime();
        prunedTimes = new ArrayList<>();
        for (SegmentedTime time : allowedTimeSegments) {
            if (time.compareTo(currentSegmentedTime) >= 0)
                prunedTimes.add(time);
        }

        if (prunedTimes.size() > 0)
            prunedTimes.remove(prunedTimes.size() - 1);
    }


    public List<SegmentedTime> getStartTimes(boolean pruneTimes) {
        if (pruneTimes) {
            return prunedTimes;
        } else {
            return startTimes;
        }
    }

    public List<SegmentedTime> getEndTimes(SegmentedTime startTime) {
        if (startTime == null)
            return null;

        if (endTimes == null || !startTime.equals(lastStartTimeUsedForCalculation))
            calculateEndTimes(startTime);

        return endTimes;
    }

    private void calculateEndTimes(SegmentedTime startTime) {
        lastStartTimeUsedForCalculation = startTime;
        endTimes = new ArrayList<>();

        if (startTime == null) {
            endTimes = null;
            return;
        }

        int selectedTimeIndex = allowedTimeSegments.indexOf(startTime);
        endTimes = allowedTimeSegments.subList(selectedTimeIndex + 1, allowedTimeSegments.size());
    }

    public String getFriendlyDatePattern() {
        return CalendarRange.FORMAT_DATE_FRIENDLY.toPattern();
    }

    public Date getMinDate() {
        Calendar minDate = Calendar.getInstance();
        if (prunedTimes.isEmpty()) //We've passed all the available times today.
            minDate.add(Calendar.DAY_OF_YEAR, 1); //Go to next day.

        return minDate.getTime();
    }

    public boolean isToday(Date date) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTime(date);

        Calendar today = Calendar.getInstance();
        return selectedCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && selectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }
}
