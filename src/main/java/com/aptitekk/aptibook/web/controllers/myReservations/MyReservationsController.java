/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.myReservations;

import com.aptitekk.aptibook.core.entities.AssetCategory;
import com.aptitekk.aptibook.core.entities.Reservation;
import com.aptitekk.aptibook.core.entities.services.AssetCategoryService;
import com.aptitekk.aptibook.core.entities.services.ReservationService;
import com.aptitekk.aptibook.core.entities.services.UserService;
import com.aptitekk.aptibook.core.util.schedule.ReservationScheduleEvent;
import com.aptitekk.aptibook.core.util.schedule.ReservationScheduleModel;
import com.aptitekk.aptibook.core.util.time.SegmentedTime;
import com.aptitekk.aptibook.web.controllers.AuthenticationController;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
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

    private Map<AssetCategory, List<Reservation>> presentReservations;

    private ReservationScheduleModel eventModel;

    private ReservationScheduleEvent selectedEvent;

    @PostConstruct
    private void init() {
        buildPresentReservationList();

        List<AssetCategory> assetCategories = assetCategoryService.getAll();
        AssetCategory[] assetCategoriesDisplayed = new AssetCategory[assetCategories.size()];
        assetCategories.toArray(assetCategoriesDisplayed);
        eventModel = new ReservationScheduleModel() {
            public List<Reservation> getReservationsBetweenDates(Calendar start, Calendar end) {
                List<Reservation> allBetweenDates = reservationService.getAllBetweenDates(start, end, authenticationController.getAuthenticatedUser(), assetCategoriesDisplayed);

                Iterator<Reservation> iterator = allBetweenDates.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getStatus() == Reservation.Status.REJECTED)
                        iterator.remove();
                }

                return allBetweenDates;
            }
        };
    }


    private void buildPresentReservationList() {
        presentReservations = new LinkedHashMap<>();

        if (authenticationController != null && authenticationController.getAuthenticatedUser() != null) {

            for (Reservation reservation : authenticationController.getAuthenticatedUser().getReservations()) {

                //Make sure that the reservation is after right now.
                LocalDate reservationDate = new DateTime(reservation.getDate()).toLocalDate();
                LocalDate nowDate = new DateTime().toLocalDate();

                if (reservationDate.isBefore(nowDate))
                    continue;
                if (reservationDate.isEqual(nowDate) && reservation.getTimeEnd().compareTo(new SegmentedTime()) <= 0)
                    continue;

                presentReservations.putIfAbsent(reservation.getAsset().getAssetCategory(), new ArrayList<>());
                presentReservations.get(reservation.getAsset().getAssetCategory()).add(reservation);
            }
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ReservationScheduleEvent) selectEvent.getObject();
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

    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

}
