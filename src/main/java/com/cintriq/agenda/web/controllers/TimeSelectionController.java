package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.utilities.time.CalendarRange;
import com.cintriq.agenda.core.utilities.time.SegmentedTime;
import com.cintriq.agenda.core.utilities.time.SegmentedTimeRange;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.text.ParseException;
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
     * Used in getSegmentedTimeRange to ensure that only one SegmentedTimeRange is generated for the selected date, start-time, and end-time.
     */
    private int lastTimeRangeHashcode;

    /**
     * The SegmentedTimeRange for the selected date, start-time, and end-time.
     * Use the {@link #getSegmentedTimeRange() getSegmentedTimeRange()} method to get this object, as it performs generation as well.
     */
    private SegmentedTimeRange segmentedTimeRange;

    //TODO: Get from application properties
    private SegmentedTimeRange allowedTimes;

    private List<AssetType> assetTypes;
    private AssetType selectedAssetType;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetTypeService assetTypeService;


    @PostConstruct
    public void init() {
        // ---- Temporary code to generate an allowed time CalendarRange. Should
        // ideally come from a settings page somewhere. ----//

        allowedTimes = new SegmentedTimeRange(null, new SegmentedTime(6, true), new SegmentedTime(20, true));

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

        SegmentedTime counterTime = (SegmentedTime) allowedTimes.getStartTime().clone();

        while (counterTime.getCurrentSegment() < allowedTimes.getEndTime().getCurrentSegment()) {
            startTimes.add(counterTime.getTimeString());
            counterTime.increaseSegment();
        }

    }

    /**
     * Generates a list of times that can be selected for the "End Time" based
     * on the currently selected time.
     */
    private void calculateEndTimes(String startTime) {
        endTimes = new ArrayList<>();

        SegmentedTime startSegmentedTime = SegmentedTime.fromTimeString(startTime);

        SegmentedTime counterTime = (SegmentedTime) startSegmentedTime.clone();
        counterTime.increaseSegment();

        while (counterTime.getCurrentSegment() <= allowedTimes.getEndTime().getCurrentSegment()) {
            endTimes.add(counterTime.getTimeString());
            counterTime.increaseSegment();
        }

        lastStartTimeCalculated = startTime;
    }

    private Date combineDateAndTimeString(Date date, String timeString) {
        try {
            return CalendarRange.FORMAT_DATE_TIME.parse(CalendarRange.FORMAT_DATE_ONLY.format(date) + " " + timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSelectedDateFriendly() {
        return CalendarRange.FORMAT_DATE_FRIENDLY.format(selectedDate);
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

    public SegmentedTimeRange getSegmentedTimeRange() {
        int hashcode = selectedDate.hashCode() + selectedStartTimeString.hashCode() + selectedEndTimeString.hashCode();
        if (lastTimeRangeHashcode == hashcode && segmentedTimeRange != null)
            return segmentedTimeRange;
        else {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(selectedDate);

            return segmentedTimeRange = new SegmentedTimeRange(calendarDate, SegmentedTime.fromTimeString(selectedStartTimeString), SegmentedTime.fromTimeString(selectedEndTimeString));
        }
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

    public String getFriendlyDatePattern() {
        return CalendarRange.FORMAT_DATE_FRIENDLY.toPattern();
    }
}
