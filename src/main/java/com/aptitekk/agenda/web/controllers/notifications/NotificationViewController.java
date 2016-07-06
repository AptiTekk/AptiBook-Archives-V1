/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.notifications;

import com.aptitekk.agenda.core.entity.Notification;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class NotificationViewController implements Serializable {

    @Inject
    private NotificationController notificationController;

    @PostConstruct
    public void init() {
        notificationController.pullNotifications();
    }

    @PreDestroy
    public void markUnreadRead() {
        notificationController.markUnreadRead();
    }

    public List<Notification> getUnread() {
        return notificationController.getUnread();
    }

    public List<Notification> getNotifications() {
        return notificationController.getNotifications();
    }

}
