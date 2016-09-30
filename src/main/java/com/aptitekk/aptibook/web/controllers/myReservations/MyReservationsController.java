/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.myReservations;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.web.components.primeFaces.schedule.ReservationScheduleModel;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@Named
@ViewScoped
public class MyReservationsController implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    private Map<AssetCategory, List<Reservation>> presentReservations;

    private ReservationScheduleModel eventModel;

    @PostConstruct
    private void init() {
        buildPresentReservationList();

        List<AssetCategory> assetCategories = assetCategoryService.getAll();
        AssetCategory[] assetCategoriesDisplayed = new AssetCategory[assetCategories.size()];
        assetCategories.toArray(assetCategoriesDisplayed);
        eventModel = new ReservationScheduleModel() {
            public List<Reservation> getReservationsBetweenDates(ZonedDateTime start, ZonedDateTime end) {
                List<Reservation> allBetweenDates = reservationService.getAllBetweenDates(start, end, authenticationController.getAuthenticatedUser(), assetCategoriesDisplayed);

                Iterator<Reservation> iterator = allBetweenDates.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getStatus() == Reservation.Status.REJECTED)
                        iterator.remove();
                }

                return allBetweenDates;
            }
        };

        helpController.setCurrentTopic(HelpController.Topic.USER_MY_RESERVATIONS);
    }


    private void buildPresentReservationList() {
        presentReservations = new LinkedHashMap<>();

        if (authenticationController != null && authenticationController.getAuthenticatedUser() != null) {

            ZonedDateTime now = ZonedDateTime.now();
            for (Reservation reservation : authenticationController.getAuthenticatedUser().getReservations()) {

                //Ignore reservations that have ended.
                if (reservation.getEndTime().isBefore(now))
                    continue;

                presentReservations.putIfAbsent(reservation.getAsset().getAssetCategory(), new ArrayList<>());
                presentReservations.get(reservation.getAsset().getAssetCategory()).add(reservation);
            }
        }
    }

    public Set<AssetCategory> getAssetCategories() {
        return presentReservations.keySet();
    }

    public List<Reservation> getPresentReservationsForCategory(AssetCategory assetCategory) {
        return presentReservations.get(assetCategory);
    }

    public ReservationScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ReservationScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

}
