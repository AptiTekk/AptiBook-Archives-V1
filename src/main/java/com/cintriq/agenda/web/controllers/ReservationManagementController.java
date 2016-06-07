package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.UserService;
import com.cintriq.agenda.core.entity.Reservation;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.utilities.FacesSessionHelper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.Set;

@ManagedBean(name = "ReservationManagementController")
@ViewScoped
public class ReservationManagementController {

    @Inject
    private UserService userService;

    @Inject
    private ReservationService reservationService;

    private User user;

    private Set<Reservation> reservations;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        String loggedInUser
                = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (loggedInUser != null) {
            this.setUser(userService.findByName(loggedInUser));
        }

        reservations = reservationService.getAllUnderUser(user);
    }

    public String formatApprovedBy(Reservation reservation) {
        String result = "Approved by ";
        if (reservation.getApprovals().isEmpty()) {
            return "Not yet approved by anyone";
        } else {
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

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }
}
