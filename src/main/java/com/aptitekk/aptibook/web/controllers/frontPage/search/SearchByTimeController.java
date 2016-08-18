/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.search;

import com.aptitekk.aptibook.core.entities.AssetCategory;
import com.aptitekk.aptibook.core.entities.services.AssetCategoryService;
import com.aptitekk.aptibook.core.util.time.SegmentedTime;
import com.aptitekk.aptibook.core.util.time.SegmentedTimeRange;
import com.aptitekk.aptibook.web.controllers.TimeSelectionController;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class SearchByTimeController implements Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private TimeSelectionController timeSelectionController;

    private int currentStep = 0;

    private List<AssetCategory> assetCategories;
    private AssetCategory assetCategory;

    private Date date = Calendar.getInstance().getTime();

    /**
     * The SegmentedTimeRange for the selected date, start-time, and end-time.
     */
    private SegmentedTimeRange segmentedTimeRange;

    /**
     * Used in getSegmentedTimeRange to ensure that only one SegmentedTimeRange is generated for the selected date, start-time, and end-time.
     */
    private int lastTimeRangeHashcode;

    private SegmentedTime startTime;
    private SegmentedTime endTime;

    @PostConstruct
    private void init() {
        date = timeSelectionController.getMinDate();
        assetCategories = assetCategoryService.getAll();
    }

    public void nextStep() {
        currentStep++;
    }

    public void previousStep() {
        currentStep--;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public List<AssetCategory> getAssetCategories() {
        return assetCategories;
    }

    public AssetCategory getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
        this.assetCategory = assetCategory;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.startTime = null;
        this.endTime = null;
    }

    public SegmentedTime getStartTime() {
        return startTime;
    }

    public void setStartTime(SegmentedTime startTime) {
        this.startTime = startTime;
        this.endTime = null;
    }

    public SegmentedTime getEndTime() {
        return endTime;
    }

    public void setEndTime(SegmentedTime endTime) {
        this.endTime = endTime;
    }

    public SegmentedTimeRange getSegmentedTimeRange() {
        int hashcode = date.hashCode() + startTime.hashCode() + endTime.hashCode();
        if (lastTimeRangeHashcode == hashcode && segmentedTimeRange != null)
            return segmentedTimeRange;
        else {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(date);

            lastTimeRangeHashcode = hashcode;
            return segmentedTimeRange = new SegmentedTimeRange(calendarDate, startTime, endTime);
        }
    }
}
