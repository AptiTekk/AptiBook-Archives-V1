/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.search;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class SearchByTimeController implements Serializable {

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private TenantSessionService tenantSessionService;

    private int currentStep = 0;

    private List<ResourceCategory> resourceCategories;
    private ResourceCategory resourceCategory;

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    @PostConstruct
    private void init() {
        //Set Start and End times to this moment in time, formatted for the tenant's timezone.
        startTime = ZonedDateTime.now(tenantSessionService.getCurrentTenantZoneId());
        endTime = startTime;
        resourceCategories = resourceCategoryService.getAll();
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

    public List<ResourceCategory> getResourceCategories() {
        return resourceCategories;
    }

    public ResourceCategory getResourceCategory() {
        return resourceCategory;
    }

    public void setResourceCategory(ResourceCategory resourceCategory) {
        this.resourceCategory = resourceCategory;
    }

    public Date getPickerStartTime() {
        //Convert ZonedDateTime of tenant's timezone to Date using same local time.
        return Date.from(startTime.withZoneSameLocal(ZoneId.of("UTC")).toInstant());
    }

    public void setPickerStartTime(Date pickerStartTime) {
        if (pickerStartTime != null) {
            //Convert Date from picker to ZonedDateTime of tenant's timezone using same local time.
            startTime = ZonedDateTime.ofInstant(pickerStartTime.toInstant(), ZoneId.of("UTC")).withZoneSameLocal(tenantSessionService.getCurrentTenantZoneId());
            //Make sure end time isn't before start time.
            if (endTime.isBefore(startTime))
                endTime = startTime;
        }
    }

    public Date getPickerEndTime() {
        //Convert ZonedDateTime of tenant's timezone to Date using same local time.
        return Date.from(endTime.withZoneSameLocal(ZoneId.of("UTC")).toInstant());
    }

    public void setPickerEndTime(Date pickerEndTime) {
        if (pickerEndTime != null) {
            //Convert Date from picker to ZonedDateTime of tenant's timezone using same local time.
            ZonedDateTime newEndTime = ZonedDateTime.ofInstant(pickerEndTime.toInstant(), ZoneId.of("UTC")).withZoneSameLocal(tenantSessionService.getCurrentTenantZoneId());

            //Make sure we don't somehow reserve back in time.
            if (!newEndTime.isBefore(startTime))
                endTime = newEndTime;
        }
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }
}
