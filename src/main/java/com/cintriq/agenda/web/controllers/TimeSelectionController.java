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
import java.util.*;

@ManagedBean(name = "TimeSelectionController")
@ViewScoped
public class TimeSelectionController {

    private Date selectedDate = Calendar.getInstance().getTime();

    private List<SegmentedTime> startTimes;
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
    }

    public List<SegmentedTime> getStartTimes() {
        return startTimes;
    }

    public SegmentedTime getSelectedStartTime() {
        return selectedStartTime;
    }

    public void setSelectedStartTime(SegmentedTime selectedStartTime) {
        this.selectedStartTime = selectedStartTime;
    }

    public List<SegmentedTime> getEndTimes() {
        if (selectedStartTime == null)
            return null;

        if (selectedStartTime.equals(lastStartTimeUsedForCalculation) && endTimes != null)
            return endTimes;

        endTimes = new ArrayList<>();

        int selectedTimeIndex = allowedTimeSegments.indexOf(selectedStartTime);
        endTimes = allowedTimeSegments.subList(selectedTimeIndex + 1, allowedTimeSegments.size());

        lastStartTimeUsedForCalculation = selectedStartTime;
        selectedEndTime = endTimes.get(0);

        return endTimes;
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
}
