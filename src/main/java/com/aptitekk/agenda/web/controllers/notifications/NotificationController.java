/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.notifications;

import com.aptitekk.agenda.core.entities.Notification;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.services.NotificationService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.notification.NotificationListener;
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class NotificationController implements NotificationListener, Serializable {

    @Inject
    private UserService userService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AuthenticationController authenticationController;

    private List<Notification> notifications;
    private List<Notification> unreadNotifications;

    private User user;

    @PostConstruct
    public void init() {
        if (authenticationController != null && authenticationController.getAuthenticatedUser() != null) {
            this.user = authenticationController.getAuthenticatedUser();
            loadNotifications();
            notificationService.registerListener(this);
        }
    }

    @PreDestroy
    private void destroy() {
        notificationService.unregisterListener(this);
    }

    private void loadNotifications() {
        notifications = notificationService.getAllForUser(user);
        unreadNotifications = new ArrayList<>();
        unreadNotifications.addAll(notifications.stream().filter(n -> !n.getRead()).collect(Collectors.toList()));
    }

    void markAllAsRead() {
        notificationService.markAllAsReadForUser(user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public List<Notification> getUnreadNotifications() {
        return unreadNotifications;
    }

    @Override
    public void pushNotification(Notification n) {
        LogManager.logInfo("Notification pushed to frontend.");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, n.getSubject(), (n.getBody().length() > 30 ? n.getBody().substring(0, 30) + "..." : n.getBody()));
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, message);
    }
}
