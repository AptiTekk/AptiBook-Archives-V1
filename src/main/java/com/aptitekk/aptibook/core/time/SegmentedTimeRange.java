/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.time;

import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.annotation.Nullable;
import java.util.Calendar;

/**
 * Used to specify a segmented time range, for example, 8:00 AM to 4:30 PM,
 * as well as a date in the form of a Calendar.
 * Contains a start time and an end time, using the SegmentedTime object.
 */
public class SegmentedTimeRange {

    private Calendar date;
    private SegmentedTime startTime;
    private SegmentedTime endTime;

    /**
     * Creates a SegmentedTimeRange using SegmentedTimes and a Calendar
     *
     * @param date      The date of the time range.
     * @param startTime The start of the time range
     * @param endTime   The end of the time range
     */
    public SegmentedTimeRange(@Nullable Calendar date, SegmentedTime startTime, SegmentedTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public SegmentedTime getStartTime() {
        return startTime;
    }

    public void setStartTime(SegmentedTime startTime) {
        this.startTime = startTime;
    }

    public SegmentedTime getEndTime() {
        return endTime;
    }

    public void setEndTime(SegmentedTime endTime) {
        this.endTime = endTime;
    }

    public Calendar getDateWithStartTime() {
        return embedDateWithTime(startTime);
    }

    public Calendar getDateWithEndTime() {
        return embedDateWithTime(endTime);
    }

    private Calendar embedDateWithTime(SegmentedTime segmentedTime) {
        Calendar dateTime = (Calendar) date.clone();
        dateTime.set(Calendar.SECOND, 0);
        dateTime.set(Calendar.MINUTE, segmentedTime.getMinute());
        dateTime.set(Calendar.HOUR_OF_DAY, segmentedTime.getHourOfDay());

        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof SegmentedTimeRange)) return false;

        SegmentedTimeRange other = (SegmentedTimeRange) o;

        return EqualsHelper.areEquals(getDate(), other.getDate())
                && EqualsHelper.areEquals(getStartTime(), other.getStartTime())
                && EqualsHelper.areEquals(getEndTime(), other.getEndTime());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getDate(), getStartTime(), getEndTime());
    }

}
