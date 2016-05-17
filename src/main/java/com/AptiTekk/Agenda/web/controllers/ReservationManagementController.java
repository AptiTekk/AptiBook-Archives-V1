package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.ReservationService;
import com.AptiTekk.Agenda.core.UserService;
import com.AptiTekk.Agenda.core.entity.Reservation;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.FacesSessionHelper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * Created by kevint on 5/16/2016.
 */
@ManagedBean
@ViewScoped
public class ReservationManagementController {

    @Inject
    private UserService userService;

    @Inject
    private ReservationService reservationService;

    private User user;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        String loggedInUser
                = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (loggedInUser != null) {
            this.setUser(userService.findByName(loggedInUser));
        }

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
}
