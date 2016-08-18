/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.notifications;

import com.aptitekk.aptibook.core.entities.Notification;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class NotificationViewController implements Serializable {

    @Inject
    private NotificationController notificationController;

    @PostConstruct
    private void init() {
        notificationController.markAllAsRead();
    }

    public List<Notification> getUnreadNotifications() {
        return notificationController.getUnreadNotifications();
    }

    public List<Notification> getAllNotifications() {
        return notificationController.getAllNotifications();
    }

}
