/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.web.components.primeFaces.schedule.ReservationScheduleModel;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Named
@ViewScoped
public class FrontPageController implements Serializable {

    @Inject
    private ReservationService reservationService;

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private HelpController helpController;

    private ReservationScheduleModel scheduleModel;

    private List<ResourceCategory> resourceCategories;

    private boolean[] resourceCategoryFilterValues;

    private ResourceCategory[] displayedCategories;

    @PostConstruct
    private void init() {
        resourceCategories = resourceCategoryService.getAll();
        resourceCategoryFilterValues = new boolean[resourceCategories.size()];
        for (int i = 0; i < resourceCategoryFilterValues.length; i++)
            resourceCategoryFilterValues[i] = true;

        displayedCategories = buildDisplayedResourceCategories();

        scheduleModel = new ReservationScheduleModel() {
            @Override
            public List<Reservation> getReservationsBetweenDates(ZonedDateTime start, ZonedDateTime end) {
                List<Reservation> allBetweenDates = reservationService.getAllBetweenDates(start, end, displayedCategories);
                if(allBetweenDates == null)
                    return new ArrayList<>();

                Iterator<Reservation> iterator = allBetweenDates.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getStatus() == Reservation.Status.REJECTED)
                        iterator.remove();
                }

                return allBetweenDates;
            }
        };

        helpController.setCurrentTopic(HelpController.Topic.FRONT_PAGE);
    }

    private ResourceCategory[] buildDisplayedResourceCategories() {
        List<ResourceCategory> displayedCategories = new ArrayList<>();
        for (int i = 0; i < resourceCategoryFilterValues.length; i++) {
            if (resourceCategoryFilterValues[i]) {
                displayedCategories.add(resourceCategories.get(i));
            }
        }
        ResourceCategory[] displayedCategoriesArray = new ResourceCategory[displayedCategories.size()];
        return displayedCategories.toArray(displayedCategoriesArray);
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public List<ResourceCategory> getResourceCategories() {
        return resourceCategories;
    }

    public boolean[] getResourceCategoryFilterValues() {
        return resourceCategoryFilterValues;
    }

    public void updateFilters() {
        displayedCategories = buildDisplayedResourceCategories();
    }

}
