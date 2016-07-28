/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.notifications;

import com.aptitekk.agenda.core.entities.Notification;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class NotificationViewController implements Serializable {

    @Inject
    private NotificationController notificationController;

    @PreDestroy
    private void preDestroy() {
        notificationController.markAllAsRead();
    }

    public List<Notification> getUnreadNotifications() {
        return notificationController.getUnreadNotifications();
    }

    public List<Notification> getAllNotifications() {
        return notificationController.getAllNotifications();
    }

}
