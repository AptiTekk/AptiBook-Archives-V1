/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.time;

import com.aptitekk.aptibook.core.util.EqualsHelper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class provides a wrapper for time in which minutes may only be segments of 30, and date is irrelevant.
 */
public class SegmentedTime implements Comparable<SegmentedTime>, Serializable, Cloneable {

    private Calendar calendar;

    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");

    public SegmentedTime() {
        this.calendar = Calendar.getInstance();
        this.calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) < 30 ? 0 : 30);
        cleanCalendar();
    }

    public SegmentedTime(int hourOfDay, boolean isThirtyMinute) {
        this();
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this.calendar.set(Calendar.MINUTE, isThirtyMinute ? 30 : 0);
    }

    SegmentedTime(Date date) {
        this.calendar = Calendar.getInstance();
        calendar.setTime(date);
        cleanCalendar();
    }

    private void cleanCalendar() {
        calendar.set(2000, Calendar.JANUARY, 1); //Give a common year, month, and day to all SegmentedTime Calendar objects.
        calendar.set(Calendar.SECOND, 0); //Remove seconds component
        calendar.set(Calendar.MILLISECOND, 0); //Remove milliseconds component
    }

    public String getTimeString() {
        return TIME_FORMAT.format(this.calendar.getTime());
    }

    public static SegmentedTime fromTimeString(String timeString) {
        if (timeString != null) {
            try {
                Date date = TIME_FORMAT.parse(timeString);
                return new SegmentedTime(date);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    public Calendar mergeWithCalendar(Calendar calendar) {
        Calendar mergedCalendar = Calendar.getInstance();
        mergedCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this.calendar.get(Calendar.HOUR_OF_DAY), this.calendar.get(Calendar.MINUTE), this.calendar.get(Calendar.SECOND));
        return mergedCalendar;
    }

    /**
     * Get the hour of the day, starting at 0 and ending at 23.
     *
     * @return The hour of the day.
     */
    public int getHourOfDay() {
        return this.calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Sets the hour of day for this SegmentedTime
     *
     * @param hourOfDay The hour of day to set to.
     */
    public void setHourOfDay(int hourOfDay) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
    }

    /**
     * Get the minute of the hour. Will be either 0 or 30, nowhere in-between.
     *
     * @return The minute of the hour; either 0 or 30.
     */
    public int getMinute() {
        return this.calendar.get(Calendar.MINUTE);
    }

    /**
     * Whether or not the minute of the hour is 0 or 30.
     *
     * @return True if the minute of the hour is 30, false if 0.
     */
    public boolean isThirtyMinute() {
        return this.calendar.get(Calendar.MINUTE) == 30;
    }

    /**
     * Sets the minute component to either 30 or 0 based on the input.
     *
     * @param isThirtyMinute Whether or not the minute component should be 30.
     */
    public void setThirtyMinute(boolean isThirtyMinute) {
        this.calendar.set(Calendar.MINUTE, isThirtyMinute ? 30 : 0);
    }

    /**
     * Calculates and returns the current segment value.
     * For example, a time with an hour of 1 and an "isThirtyMinute" value of true would return a segment value of 3.
     *
     * @return The current segment value.
     */
    public int getCurrentSegment() {
        return (getHourOfDay() * 2) + (isThirtyMinute() ? 1 : 0);
    }

    /**
     * Increase the time by one 30 minute segment
     */
    public void increaseSegment() {
        increaseSegments(1);
    }

    /**
     * Decrease the time by one 30 minute segment
     */
    public void decreaseSegment() {
        decreaseSegments(1);
    }

    /**
     * Increase the time by the number of specified 30 minute segments.
     *
     * @param segments Number of 30 minute segments to increase by.
     */
    public void increaseSegments(int segments) {
        this.calendar.add(Calendar.MINUTE, 30 * segments);
        cleanCalendar();
    }

    /**
     * Decrease the time by the number of specified 30 minute segments.
     *
     * @param segments Number of 30 minute segments to decrease by.
     */
    public void decreaseSegments(int segments) {
        this.calendar.add(Calendar.MINUTE, -30 * segments);
        cleanCalendar();
    }

    @Override
    public int compareTo(SegmentedTime other) {
        if (getCurrentSegment() > other.getCurrentSegment())
            return 1;
        else if (getCurrentSegment() == other.getCurrentSegment())
            return 0;
        else
            return -1;
    }

    @Override
    public Object clone() {
        try {
            SegmentedTime other = (SegmentedTime) super.clone();
            other.calendar = (Calendar) calendar.clone();

            return other;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof SegmentedTime)) return false;

        SegmentedTime other = (SegmentedTime) o;

        return EqualsHelper.areEquals(getCurrentSegment(), other.getCurrentSegment());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getCurrentSegment());
    }

    Calendar getCalendar() {
        return calendar;
    }
}
