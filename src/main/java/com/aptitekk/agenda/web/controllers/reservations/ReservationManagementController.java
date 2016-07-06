package com.aptitekk.agenda.web.controllers.reservations;

import com.aptitekk.agenda.core.entity.Reservation;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.services.ReservationService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.utilities.FacesSessionHelper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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

    private User user;

    private List<Reservation> reservations;

    @PostConstruct
    public void init() {
        String loggedInUser
                = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (loggedInUser != null) {
            this.setUser(userService.findByName(loggedInUser));
        }

        reservations = new ArrayList<>(reservationService.getAllUnderUser(user));
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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
