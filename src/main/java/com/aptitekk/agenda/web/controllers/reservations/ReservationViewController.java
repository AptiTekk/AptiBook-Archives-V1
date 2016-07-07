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
import com.aptitekk.agenda.core.utilities.FacesSessionHelper;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ReservationViewController implements Serializable {

    @Inject
    private ReservationService resService;

    @Inject
    private UserService userService;

    private User user;

    private ScheduleModel lazyEventModel = new LazyScheduleModel();

    private List<Reservation> cardModels = new ArrayList<>();

    @PostConstruct
    public void init() {
        String username = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (username != null) {
            this.setUser(userService.findByName(username));
            updateEvents(user);
            updateCards(user);
        }

    }

    private static DefaultScheduleEvent toEvent(Reservation reservation) {
        DefaultScheduleEvent event = new DefaultScheduleEvent();
        event.setStartDate(reservation.getTimeStart().mergeWithCalendar(reservation.getDate()).getTime());
        event.setEndDate(reservation.getTimeEnd().mergeWithCalendar(reservation.getDate()).getTime());
        event.setTitle(reservation.getAsset().getName() + " - " + reservation.getTitle());
        event.setDescription(reservation.getDescription());
        return event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        updateEvents(user);
        updateCards(user);
    }

    private void updateEvents(User user) {
        lazyEventModel.clear();
        ((user == null) ? resService.getAll()
                : user.getReservations()).forEach((res) -> {
            lazyEventModel.addEvent(toEvent(res));
        });
    }

    private void updateCards(User user) {
        cardModels.clear();
        cardModels.addAll(((user == null) ? resService.getAll() : user.getReservations()));
    }

    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }

    public void setLazyEventModel(ScheduleModel lazyEventModel) {
        this.lazyEventModel = lazyEventModel;
    }

    public List<Reservation> getCardModels() {
        return cardModels;
    }

    public void setCardModels(List<Reservation> cardModels) {
        this.cardModels = cardModels;
    }


}
