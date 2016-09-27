/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement.calendar;


import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.web.components.primeFaces.schedule.ReservationScheduleModel;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import org.joda.time.DateTime;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Named
@ViewScoped
public class CalendarReservationManagementController implements Serializable {

    @Inject
    UserService userService;

    @Inject
    UserGroupService userGroupService;

    @Inject
    AuthenticationController authenticationController;

    @Inject
    AssetCategoryService assetCategoryService;

    @Inject
    HelpController helpController;

    private ReservationScheduleModel scheduleModel;

    private List<Reservation> reservations;

    @PostConstruct
    private void init() {
        reservations = new ArrayList<>();
        for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
            reservations.addAll(userGroupService.getHierarchyDownReservations(userGroup));
        }

        scheduleModel = new ReservationScheduleModel() {
            @Override
            public List<Reservation> getReservationsBetweenDates(DateTime start, DateTime end) {
                ArrayList<Reservation> prunedReservations = new ArrayList<>(reservations);
                Iterator<Reservation> iterator = prunedReservations.iterator();
                while (iterator.hasNext()) {
                    Reservation next = iterator.next();
                    if (next.getStatus() == Reservation.Status.REJECTED)
                        iterator.remove();
                    else if (next.getEndTime().isBefore(start) || next.getStartTime().isAfter(end))
                        iterator.remove();
                }

                return prunedReservations;
            }
        };

        helpController.setCurrentTopic(HelpController.Topic.RESERVATION_MANAGEMENT_CALENDAR);
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

}
