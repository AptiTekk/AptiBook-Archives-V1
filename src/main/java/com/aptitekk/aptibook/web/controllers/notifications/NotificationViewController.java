/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.notifications;

import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import org.ocpsoft.prettytime.PrettyTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Named
@RequestScoped
public class NotificationViewController implements Serializable {

    @Inject
    private NotificationController notificationController;

    @Inject
    private TenantSessionService tenantSessionService;

    @Inject
    private HelpController helpController;

    @PostConstruct
    private void init() {
        notificationController.markAllAsRead();

        helpController.setCurrentTopic(HelpController.Topic.USER_NOTIFICATIONS);
    }

    public List<Notification> getUnreadNotifications() {
        return notificationController.getUnreadNotifications();
    }

    public List<Notification> getAllNotifications() {
        return notificationController.getAllNotifications();
    }

    public String formatNotificationTimeAgo(Notification notification) {
        if (notification != null) {
            PrettyTime prettyTime = new PrettyTime(Date.from(ZonedDateTime.now().toInstant()));
            return prettyTime.format(Date.from(notification.getCreation().toInstant()));
        }
        return "Unknown time ago";
    }

}
