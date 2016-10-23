/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.services.NotificationService;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.components.primeFaces.schedule.ReservationScheduleEvent;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import org.primefaces.event.SelectEvent;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class ViewEventModalController implements Serializable {

    @Inject
    private NotificationService notificationService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AuthenticationController authenticationController;

    private ReservationScheduleEvent selectedEvent;

    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ReservationScheduleEvent) selectEvent.getObject();
    }

    public ReservationScheduleEvent getSelectedEvent() {
        return selectedEvent;
    }

    public boolean isAllowedToCancelSelectedEvent() {
        return selectedEvent != null && (authenticationController.getAuthenticatedUser().equals(selectedEvent.getReservation().getUser()) || authenticationController.userHasPermission(Permission.Descriptor.RESERVATIONS_MODIFY_ALL));
    }

    public void cancelSelectedEvent() {
        if (selectedEvent == null)
            return;

        selectedEvent.getReservation().setStatus(Reservation.Status.CANCELLED);
        try {
            reservationService.merge(selectedEvent.getReservation());
            notificationService.sendReservationCancelledNotifications(selectedEvent.getReservation());
        } catch (Exception e) {
            LogManager.logException(getClass(), e, "Could not Cancel Reservation");
        }
    }

}
