package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.ReservationService;
import com.AptiTekk.Agenda.core.UserService;
import com.AptiTekk.Agenda.core.entity.Reservation;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.AgendaLogger;
import com.AptiTekk.Agenda.core.utilities.FacesSessionHelper;
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

    public static DefaultScheduleEvent toEvent(Reservation res) {
        DefaultScheduleEvent event = new DefaultScheduleEvent();
        event.setStartDate(res.getTimeStart());
        event.setEndDate(res.getTimeEnd());
        event.setTitle(res.getAsset().getName() + " - " + res.getTitle());
        event.setDescription(res.getDescription());
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

    public void updateEvents(User user) {
        AgendaLogger.logVerbose("Changing user, changing event model.");
        lazyEventModel.clear();
        ((user == null) ? resService.getAll()
                : user.getReservations()).stream().forEach((res) -> {
                    lazyEventModel.addEvent(toEvent(res));
        });
    }
    
    public void updateCards(User user) {
        cardModels.clear();
        for (Reservation res : ((user == null) ? resService.getAll()
                : user.getReservations())) {
            cardModels.add(res);
        }
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
