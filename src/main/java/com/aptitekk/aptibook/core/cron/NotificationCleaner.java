/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.cron;

import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.NotificationService;
import com.aptitekk.aptibook.core.domain.services.TenantService;
import com.aptitekk.aptibook.core.util.LogManager;
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
        LogManager.logInfo("Cleaning Notifications...");
        Date now = new Date();

        int numNotificationsRemoved = 0;
        List<Tenant> tenants = tenantService.getAll();
        for (Tenant tenant : tenants) {
            List<Notification> notifications = notificationService.getAll(tenant);
            for (Notification notification : notifications) {
                if (notification.getRead() && new Interval(notification.getCreation().getTime(), now.getTime()).toDuration().getStandardDays() >= 3) {
                    try {
                        notificationService.delete(notification.getId());
                        numNotificationsRemoved++;
                    } catch (Exception e) {
                        LogManager.logError("Could not delete Notification on cleanup: " + e.getMessage());
                    }
                }
            }
        }

        LogManager.logInfo("Removed " + numNotificationsRemoved + " old Notifications. Cleaning complete.");
    }

}
