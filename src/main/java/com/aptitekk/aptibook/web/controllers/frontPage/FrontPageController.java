/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
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
    private AssetCategoryService assetCategoryService;

    @Inject
    private HelpController helpController;

    private ReservationScheduleModel scheduleModel;

    private List<AssetCategory> assetCategories;

    private boolean[] assetCategoryFilterValues;

    private AssetCategory[] displayedCategories;

    @PostConstruct
    private void init() {
        assetCategories = assetCategoryService.getAll();
        assetCategoryFilterValues = new boolean[assetCategories.size()];
        for (int i = 0; i < assetCategoryFilterValues.length; i++)
            assetCategoryFilterValues[i] = true;

        displayedCategories = buildDisplayedAssetCategories();

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

    private AssetCategory[] buildDisplayedAssetCategories() {
        List<AssetCategory> displayedCategories = new ArrayList<>();
        //make the pruned list only what displayed list is true
        for (int i = 0; i < assetCategoryFilterValues.length; i++) {
            if (assetCategoryFilterValues[i]) {
                displayedCategories.add(assetCategories.get(i));
            }
        }
        AssetCategory[] displayedCategoriesArray = new AssetCategory[displayedCategories.size()];
        return displayedCategories.toArray(displayedCategoriesArray);
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public List<AssetCategory> getAssetCategories() {
        return assetCategories;
    }

    public boolean[] getAssetCategoryFilterValues() {
        return assetCategoryFilterValues;
    }

    public void updateFilters() {
        displayedCategories = buildDisplayedAssetCategories();
    }

}
