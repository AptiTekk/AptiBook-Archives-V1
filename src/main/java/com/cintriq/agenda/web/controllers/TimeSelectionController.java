package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.TimeRange;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "TimeSelectionController")
@ViewScoped
public class TimeSelectionController {

    private Date selectedDate = Calendar.getInstance().getTime();

    private List<String> startTimes;
    private String selectedStartTimeString;

    private List<String> endTimes;
    private String selectedEndTimeString;

    /**
     * Used in getEndTimes to ensure that only one set of times is generated for the given start-time.
     */
    private String lastStartTimeCalculated;

    /**
     * Used in getTimeRange to ensure that only one TimeRange is generated for the selected date, start-time, and end-time.
     */
    private int lastTimeRangeHashcode;

    /**
     * The TimeRange for the selected date, start-time, and end-time.
     * Use the {@link #getTimeRange() getTimeRange()} method to get this object, as it performs generation as well.
     */
    private TimeRange timeRange;

    //TODO: Get from application properties
    private TimeRange allowedTimes;

    private List<AssetType> assetTypes;
    private AssetType selectedAssetType;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetTypeService assetTypeService;


    @PostConstruct
    public void init() {
        // ---- Temporary code to generate an allowed time TimeRange. Should
        // ideally come from a settings page somewhere. ----//

        Calendar startTime = Calendar.getInstance();
        startTime.set(0, 0, 0, 6, 30);

        Calendar endTime = Calendar.getInstance();
        endTime.set(0, 0, 0, 20, 30);

        allowedTimes = new TimeRange(startTime, endTime);

        // ---- End Temporary Code ----//
        calculateStartTimes();

        assetTypes = assetTypeService.getAll();
    }

    /**
     * Generates a list of times that can be selected for the "Start Time". This
     * list will range from the minimum time allowed to 30 minutes before the
     * maximum time allowed (To allow for the end time to be at least 30 minutes
     * away from the selected time)
     */
    private void calculateStartTimes() {
        startTimes = new ArrayList<>();

        Calendar counterCalendar = (Calendar) allowedTimes.getStartTime().clone();

        while (counterCalendar.get(Calendar.HOUR_OF_DAY)
                + (counterCalendar.get(Calendar.MINUTE) / 60d) != allowedTimes.getEndTime()
                .get(Calendar.HOUR_OF_DAY) + (allowedTimes.getEndTime().get(Calendar.MINUTE) / 60d)) {
            String value = TimeRange.FORMAT_TIME_ONLY.format(counterCalendar.getTime());
            startTimes.add(value);

            counterCalendar.add(Calendar.MINUTE, 30);
        }

    }

    /**
     * Generates a list of times that can be selected for the "End Time" based
     * on the currently selected time.
     */
    private void calculateEndTimes(String startTime) {
        endTimes = new ArrayList<>();

        try {
            Date parsedDate = TimeRange.FORMAT_TIME_ONLY.parse(startTime);
            Calendar minCalendar = Calendar.getInstance();
            minCalendar.setTime(parsedDate);

            Calendar counterCalendar = (Calendar) minCalendar.clone();
            counterCalendar.add(Calendar.MINUTE, 30);

            while ((counterCalendar.get(Calendar.HOUR_OF_DAY)
                    + (counterCalendar.get(Calendar.MINUTE) / 60d)) != (allowedTimes.getEndTime()
                    .get(Calendar.HOUR_OF_DAY) + (allowedTimes.getEndTime().get(Calendar.MINUTE) / 60d))
                    + 0.5) {
                String value = TimeRange.FORMAT_TIME_ONLY.format(counterCalendar.getTime());
                endTimes.add(value);

                counterCalendar.add(Calendar.MINUTE, 30);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            endTimes.add("--- Internal Server Error ---");
        }

        lastStartTimeCalculated = startTime;
    }

    private Date combineDateAndTimeString(Date date, String timeString) {
        try {
            return TimeRange.FORMAT_DATE_TIME.parse(TimeRange.FORMAT_DATE_ONLY.format(date) + " " + timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSelectedDateFriendly() {
        return TimeRange.FORMAT_DATE_FRIENDLY.format(selectedDate);
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        //Set to today if null
        if (selectedDate == null)
            selectedDate = Calendar.getInstance().getTime();

        this.selectedDate = selectedDate;
    }

    public List<String> getStartTimes() {
        return startTimes;
    }

    public String getSelectedStartTimeString() {
        return selectedStartTimeString;
    }

    public void setSelectedStartTimeString(String selectedStartTimeString) {
        this.selectedStartTimeString = selectedStartTimeString;
    }

    public List<String> getEndTimes(String startTime) {
        if (startTime == null || startTime.isEmpty())
            return null;
        if (startTime.equals(lastStartTimeCalculated) && endTimes != null)
            return endTimes;
        calculateEndTimes(startTime);
        return endTimes;
    }

    public TimeRange getTimeRange() {
        int hashcode = selectedDate.hashCode() + selectedStartTimeString.hashCode() + selectedEndTimeString.hashCode();
        if (lastTimeRangeHashcode == hashcode && timeRange != null)
            return timeRange;
        else
            return (timeRange = new TimeRange(combineDateAndTimeString(selectedDate, selectedStartTimeString), combineDateAndTimeString(selectedDate, selectedEndTimeString)));
    }

    public String getSelectedEndTimeString() {
        return selectedEndTimeString;
    }

    public void setSelectedEndTimeString(String selectedEndTimeString) {
        this.selectedEndTimeString = selectedEndTimeString;
    }

    public List<AssetType> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AssetType> assetTypes) {
        this.assetTypes = assetTypes;
    }

    public AssetType getSelectedAssetType() {
        return selectedAssetType;
    }

    public void setSelectedAssetType(AssetType selectedAssetType) {
        this.selectedAssetType = selectedAssetType;
        this.setSelectedDate(getSelectedDate());
    }
    public Date getToday() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    public String getFriendlyDatePattern()
    {
        return TimeRange.FORMAT_DATE_FRIENDLY.toPattern();
    }
}

