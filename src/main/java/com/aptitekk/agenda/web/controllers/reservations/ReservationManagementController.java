/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservations;

import com.aptitekk.agenda.core.entity.Reservation;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.services.ReservationService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ReservationManagementController implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AuthenticationController authenticationController;

    private User user;

    private List<Reservation> reservations;

    @PostConstruct
    public void init() {
        if (authenticationController != null) {
            this.user = authenticationController.getAuthenticatedUser();
            reservations = new ArrayList<>(reservationService.getAllUnderUser(user));
        }
    }

    public String formatApprovedBy(Reservation reservation) {
        if (reservation.getApprovals().isEmpty()) {
            return "Not yet approved by anyone";
        } else {
            String result = "Approved by ";
            for (int i = 0; i < reservation.getApprovals().size(); i++) {
                result = result + reservation.getApprovals().get(i) + ((i == reservation.getApprovals().size() - 1) ? "" : " ");
            }
            return result;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
