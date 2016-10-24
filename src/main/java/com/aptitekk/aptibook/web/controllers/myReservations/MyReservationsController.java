/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.myReservations;

import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;
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
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    private Map<ResourceCategory, List<Reservation>> presentReservations;

    private ReservationScheduleModel eventModel;

    @PostConstruct
    private void init() {
        buildPresentReservationList();

        List<ResourceCategory> resourceCategories = resourceCategoryService.getAll();
        ResourceCategory[] resourceCategoriesDisplayed = new ResourceCategory[resourceCategories.size()];
        resourceCategories.toArray(resourceCategoriesDisplayed);
        eventModel = new ReservationScheduleModel() {
            public List<Reservation> getReservationsBetweenDates(ZonedDateTime start, ZonedDateTime end) {
                return reservationService.getAllBetweenDates(start, end, authenticationController.getAuthenticatedUser(), resourceCategoriesDisplayed);
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

                //Ignore cancelled reservations.
                if (reservation.isCancelled())
                    continue;

                presentReservations.putIfAbsent(reservation.getResource().getResourceCategory(), new ArrayList<>());
                presentReservations.get(reservation.getResource().getResourceCategory()).add(reservation);
            }
        }
    }

    public Set<ResourceCategory> getResourceCategories() {
        return presentReservations.keySet();
    }

    public List<Reservation> getPresentReservationsForCategory(ResourceCategory resourceCategory) {
        return presentReservations.get(resourceCategory);
    }

    public ReservationScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ReservationScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

}
