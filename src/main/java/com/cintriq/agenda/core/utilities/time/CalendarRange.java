package com.cintriq.agenda.core.utilities.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Used to specify a time range, for example, all of April, first week of March,
 * 12 AM to 12 PM of a certain day... Contains a start time and an end time,
 * using the Calendar object.
 */
public class CalendarRange {

    public final static SimpleDateFormat FORMAT_TIME_ONLY = new SimpleDateFormat("h:mm a");
    public final static SimpleDateFormat FORMAT_DATE_ONLY = new SimpleDateFormat("dd/MM/yyyy");
    public final static SimpleDateFormat FORMAT_DATE_FRIENDLY = new SimpleDateFormat("EEEE, dd MMMM, yyyy");
    public final static SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy h:mm a");

    private Calendar startTime;
    private Calendar endTime;

    /**
     * Creates a CalendarRange using Calendars
     *
     * @param startTime The start of the time range
     * @param endTime   The end of the time range
     */
    public CalendarRange(Calendar startTime, Calendar endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates a CalendarRange using Dates
     *
     * @param startDate The start of the time range
     * @param endDate   The end of the time range
     */
    public CalendarRange(Date startDate, Date endDate) {
        startTime = Calendar.getInstance();
        startTime.setTime(startDate);

        endTime = Calendar.getInstance();
        endTime.setTime(endDate);
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public String getStartTimeFormatted(DateFormat dateFormat) {
        if(startTime == null || dateFormat == null)
            return null;
        return dateFormat.format(startTime.getTime());
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getEndTimeFormatted(DateFormat dateFormat) {
        if(endTime == null || dateFormat == null)
            return null;
        return dateFormat.format(endTime.getTime());
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

}
