package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.ReservationService;
import com.aptitekk.agenda.core.utilities.time.SegmentedTime;
import com.aptitekk.agenda.core.AssetTypeService;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.utilities.time.CalendarRange;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.*;

@ManagedBean(name = "TimeSelectionController")
@ViewScoped
public class TimeSelectionController {

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

        //--End Duplicated code--//
        assetTypes = assetTypeService.getAll();
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

    public Date getMinDate() {
        Calendar minDate = Calendar.getInstance();
        if (prunedTimes.isEmpty()) //We've passed all the allowed times today.
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
