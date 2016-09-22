/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.search;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.web.controllers.TimeSelectionController;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
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

    private DateTime startTime;
    private DateTime endTime;

    @PostConstruct
    private void init() {
        startTime = new DateTime();
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

    public Date getPickerStartTime() {
        return startTime != null ? startTime.toDate() : null;
    }

    public void setPickerStartTime(Date pickerStartTime) {
        startTime = new DateTime(pickerStartTime);
        if (endTime == null || endTime.isBefore(startTime))
            endTime = startTime;
    }

    public Date getPickerEndTime() {
        return endTime != null ? endTime.toDate() : null;
    }

    public void setPickerEndTime(Date pickerEndTime) {
        if (pickerEndTime != null)
            endTime = new DateTime(pickerEndTime);
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }
}
