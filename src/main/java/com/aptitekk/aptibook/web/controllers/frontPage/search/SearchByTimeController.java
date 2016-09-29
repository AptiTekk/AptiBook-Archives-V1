/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.search;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class SearchByTimeController implements Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private TenantSessionService tenantSessionService;

    private int currentStep = 0;

    private List<AssetCategory> assetCategories;
    private AssetCategory assetCategory;

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    @PostConstruct
    private void init() {
        startTime = ZonedDateTime.now().withZoneSameInstant(tenantSessionService.getCurrentTenantZoneId());
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
        return startTime != null ? Date.from(startTime.toInstant()) : null;
    }

    public void setPickerStartTime(Date pickerStartTime) {
        startTime = ZonedDateTime.ofInstant(pickerStartTime.toInstant(), tenantSessionService.getCurrentTenantZoneId());
        if (endTime == null || endTime.isBefore(startTime))
            endTime = startTime;
    }

    public Date getPickerEndTime() {
        return endTime != null ? Date.from(endTime.toInstant()) : null;
    }

    public void setPickerEndTime(Date pickerEndTime) {
        if (pickerEndTime != null)
            endTime = ZonedDateTime.ofInstant(pickerEndTime.toInstant(), tenantSessionService.getCurrentTenantZoneId());
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }
}
