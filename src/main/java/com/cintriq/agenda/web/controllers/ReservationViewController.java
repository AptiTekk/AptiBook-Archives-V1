package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.UserService;
import com.cintriq.agenda.core.entity.Reservation;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.FacesSessionHelper;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "ReservationViewController")
@ViewScoped
public class ReservationViewController {

    @Inject
    private ReservationService resService;

    @Inject
    private UserService userService;

    private User user;

    private ScheduleModel lazyEventModel = new LazyScheduleModel();

    private List<Reservation> cardModels = new ArrayList<Reservation>();

    @PostConstruct
    public void init() {
        String username
                = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (username != null) {
            this.setUser(userService.findByName(username));
            updateEvents(user);
            updateCards(user);
        }

    }

    private static DefaultScheduleEvent toEvent(Reservation reservation) {
        DefaultScheduleEvent event = new DefaultScheduleEvent();
        event.setStartDate(reservation.getTimeStart().getTime());
        event.setEndDate(reservation.getTimeEnd().getTime());
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
        AgendaLogger.logVerbose("Changing user, changing event model.");
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
