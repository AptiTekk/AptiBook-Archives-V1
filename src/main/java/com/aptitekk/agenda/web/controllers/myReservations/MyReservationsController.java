/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.myReservations;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.services.AssetCategoryService;
import com.aptitekk.agenda.core.entities.services.ReservationService;
import com.aptitekk.agenda.core.entities.services.UserService;
import com.aptitekk.agenda.core.util.schedule.ReservationScheduleEvent;
import com.aptitekk.agenda.core.util.schedule.ReservationScheduleModel;
import com.aptitekk.agenda.core.util.time.SegmentedTime;
import com.aptitekk.agenda.web.controllers.AuthenticationController;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

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

    private List<AssetCategory> assetCategories;

    private AssetCategory[] assetCategoriesDisplayed;

    private ReservationScheduleEvent selectedEvent;

    @PostConstruct
    private void init() {
        buildPresentReservationList();
        eventModel = new ReservationScheduleModel() {
            public List<Reservation> getReservationsBetweenDates(Calendar start, Calendar end, AssetCategory[] assetCategories) {
                return reservationService.getAllBetweenDates(start, end, authenticationController.getAuthenticatedUser(), assetCategories);
            }
        };
        assetCategories = assetCategoryService.getAll();
        assetCategoriesDisplayed = new AssetCategory[assetCategories.size()];
        assetCategories.toArray(assetCategoriesDisplayed);
        eventModel.setSelectedAssetCategories(assetCategoriesDisplayed);
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
