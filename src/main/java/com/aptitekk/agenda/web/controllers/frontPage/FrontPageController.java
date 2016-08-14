/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.frontPage;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.services.AssetCategoryService;
import com.aptitekk.agenda.core.entities.services.ReservationService;
import com.aptitekk.agenda.core.util.schedule.ReservationScheduleEvent;
import com.aptitekk.agenda.core.util.schedule.ReservationScheduleModel;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

@Named
@ViewScoped
public class FrontPageController implements Serializable {

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetCategoryService assetCategoryService;

    private ReservationScheduleModel scheduleModel;

    private ReservationScheduleEvent selectedEvent;

    private List<AssetCategory> assetCategories;

    private AssetCategory[] assetCategoriesDisplayed;

    @PostConstruct
    private void init() {
        assetCategories = assetCategoryService.getAll();
        assetCategoriesDisplayed = new AssetCategory[assetCategories.size()];
        assetCategories.toArray(assetCategoriesDisplayed);
        scheduleModel = new ReservationScheduleModel() {
            @Override
            public List<Reservation> getReservationsBetweenDates(Calendar start, Calendar end) {
                List<Reservation> allBetweenDates = reservationService.getAllBetweenDates(start, end, assetCategoriesDisplayed);

                Iterator<Reservation> iterator = allBetweenDates.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getStatus() == Reservation.Status.REJECTED)
                        iterator.remove();
                }

                return allBetweenDates;
            }
        };
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ReservationScheduleEvent) selectEvent.getObject();
    }

    public ReservationScheduleEvent getSelectedEvent() {
        return selectedEvent;
    }

    public List<AssetCategory> getAssetCategories() {
        return assetCategories;
    }

    public AssetCategory[] getAssetCategoriesDisplayed() {
        return assetCategoriesDisplayed;
    }

    public void setAssetCategoriesDisplayed(AssetCategory[] assetCategoriesDisplayed) {
        this.assetCategoriesDisplayed = assetCategoriesDisplayed;
    }
}
