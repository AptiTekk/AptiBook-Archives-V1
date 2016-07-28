/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.services.AssetCategoryService;
import com.aptitekk.agenda.core.entities.services.ReservationService;
import com.aptitekk.agenda.core.util.time.CalendarRange;
import com.aptitekk.agenda.core.util.time.SegmentedTime;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;

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

    private Date selectedDate = Calendar.getInstance().getTime();

    private List<SegmentedTime> startTimes;
    private List<SegmentedTime> prunedTimes;
    private SegmentedTime selectedStartTime;

    private List<SegmentedTime> endTimes;
    private SegmentedTime selectedEndTime;

    /**
     * Used in getEndTimes to ensure that only one set of times is generated for the given start-time.
     */
    private SegmentedTime lastStartTimeUsedForCalculation;

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
    private SegmentedTimeRange allowedTimeRange;
    private List<SegmentedTime> allowedTimeSegments;

    private List<AssetCategory> assetCategories;
    private AssetCategory selectedAssetCategory;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetCategoryService assetCategoryService;

    @PostConstruct
    public void init() {
        // ---- Temporary code to generate an allowed time CalendarRange. Should
        // ideally come from a settings page somewhere. ----//

        allowedTimeRange = new SegmentedTimeRange(null, new SegmentedTime(6, true), new SegmentedTime(20, true));


        // ---- End Temporary Code ----//

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

        setSelectedDate(getMinDate());

        //--End Duplicated code--//
        assetCategories = assetCategoryService.getAll();
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        //Set to today if null
        if (selectedDate == null)
            selectedDate = Calendar.getInstance().getTime();

        this.selectedDate = selectedDate;
        calculateEndTimes();
    }

    public List<SegmentedTime> getStartTimes(boolean pruneTimes) {
        if (pruneTimes) {
            if (!prunedTimes.contains(selectedStartTime))
                selectedStartTime = null;
            return prunedTimes;
        } else {
            return startTimes;
        }
    }

    public SegmentedTime getSelectedStartTime() {
        return selectedStartTime;
    }

    public void setSelectedStartTime(SegmentedTime selectedStartTime) {
        this.selectedStartTime = selectedStartTime;
        calculateEndTimes();
    }

    public List<SegmentedTime> getEndTimes() {
        if (selectedStartTime == null)
            return null;

        if (selectedStartTime.equals(lastStartTimeUsedForCalculation) && endTimes != null)
            return endTimes;

        return null;
    }

    private void calculateEndTimes() {
        lastStartTimeUsedForCalculation = selectedStartTime;
        endTimes = new ArrayList<>();

        if (selectedStartTime == null) {
            endTimes = null;
            return;
        }

        int selectedTimeIndex = allowedTimeSegments.indexOf(selectedStartTime);
        endTimes = allowedTimeSegments.subList(selectedTimeIndex + 1, allowedTimeSegments.size());

        if(selectedEndTime != null)
            if(endTimes.contains(selectedEndTime))
                return;

        selectedEndTime = endTimes.get(0);
    }

    public SegmentedTimeRange getSegmentedTimeRange() {
        int hashcode = selectedDate.hashCode() + selectedStartTime.hashCode() + selectedEndTime.hashCode();
        if (lastTimeRangeHashcode == hashcode && segmentedTimeRange != null)
            return segmentedTimeRange;
        else {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(selectedDate);

            return segmentedTimeRange = new SegmentedTimeRange(calendarDate, selectedStartTime, selectedEndTime);
        }
    }

    public SegmentedTime getSelectedEndTime() {
        return selectedEndTime;
    }

    public void setSelectedEndTime(SegmentedTime selectedEndTime) {
        this.selectedEndTime = selectedEndTime;
    }

    public List<AssetCategory> getAssetCategories() {
        return assetCategories;
    }

    public void setAssetCategories(List<AssetCategory> assetCategories) {
        this.assetCategories = assetCategories;
    }

    public AssetCategory getSelectedAssetCategory() {
        return selectedAssetCategory;
    }

    public void setSelectedAssetCategory(AssetCategory selectedAssetCategory) {
        this.selectedAssetCategory = selectedAssetCategory;
        this.setSelectedDate(getSelectedDate());
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

    public boolean isTodaySelected() {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTime(selectedDate);

        Calendar today = Calendar.getInstance();
        return selectedCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && selectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }
}
