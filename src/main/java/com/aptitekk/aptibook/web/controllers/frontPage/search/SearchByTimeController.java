/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.search;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.time.SegmentedTime;
import com.aptitekk.aptibook.core.time.SegmentedTimeRange;
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

    /**
     * The SegmentedTimeRange for the selected date, start-time, and end-time.
     */
    private SegmentedTimeRange segmentedTimeRange;

    /**
     * Used in getSegmentedTimeRange to ensure that only one SegmentedTimeRange is generated for the selected date, start-time, and end-time.
     */
    private int lastTimeRangeHashcode;

    private Date startTime;
    private Date endTime;

    @PostConstruct
    private void init() {
        startTime = new Date();
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        System.out.println("Start Time: " + startTime);
        if (endTime == null || endTime.before(startTime))
            endTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        System.out.println("End Time: " + endTime);
    }

    public SegmentedTimeRange getSegmentedTimeRange() {
        int hashcode = startTime.hashCode() + endTime.hashCode();
        if (lastTimeRangeHashcode == hashcode && segmentedTimeRange != null)
            return segmentedTimeRange;
        else {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(startTime);

            lastTimeRangeHashcode = hashcode;
            return segmentedTimeRange = new SegmentedTimeRange(calendarDate, new SegmentedTime(startTime), new SegmentedTime(endTime));
        }
    }
}
