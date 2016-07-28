/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.notification;

import com.aptitekk.agenda.core.entities.Notification;
import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.entities.services.NotificationService;
import com.aptitekk.agenda.core.entities.services.TenantService;
import com.aptitekk.agenda.core.util.LogManager;
import org.joda.time.Interval;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Singleton
public class NotificationCleaner {

    @Inject
    private TenantService tenantService;

    @Inject
    private NotificationService notificationService;

    /**
     * Cleans up Notifications every 12 hours.
     * If the Notification is >= 3 days old and has been read, it will be removed.
     */
    @Schedule(hour = "*/12")
    private void cleanReadNotifications() {
        Date now = new Date();

        List<Tenant> tenants = tenantService.getAll();
        for (Tenant tenant : tenants) {
            List<Notification> notifications = notificationService.getAll(tenant);
            for (Notification notification : notifications) {
                if (notification.getRead() && new Interval(now.getTime(), notification.getCreation().getTime()).toDuration().getStandardDays() >= 3) {
                    try {
                        notificationService.delete(notification.getId());
                    } catch (Exception e) {
                        LogManager.logError("Could not delete Notification on cleanup: " + e.getMessage());
                    }
                }
            }
        }
    }

}
