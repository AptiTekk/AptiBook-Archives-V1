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
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class NotificationController implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AuthenticationController authenticationController;

    private List<Notification> allNotifications;
    private List<Notification> unreadNotifications;
    private List<Notification> readNotifications;

    private User user;

    @PostConstruct
    public void init() {
        if (authenticationController != null && authenticationController.getAuthenticatedUser() != null) {
            this.user = authenticationController.getAuthenticatedUser();
            loadNotifications();
        }
    }

    private void loadNotifications() {
        allNotifications = notificationService.getAllForUser(user);

        //Build Unread Notifications List
        unreadNotifications = new ArrayList<>();
        unreadNotifications.addAll(allNotifications.stream().filter(n -> !n.getRead()).collect(Collectors.toList()));
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

    public List<Notification> getAllNotifications() {
        return allNotifications;
    }

    public List<Notification> getUnreadNotifications() {
        return unreadNotifications;
    }
}
