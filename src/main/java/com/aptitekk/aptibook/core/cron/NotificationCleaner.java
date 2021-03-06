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
import com.aptitekk.aptibook.core.util.AptiBookInfoProvider;
import com.aptitekk.aptibook.core.util.LogManager;
import org.threeten.extra.Days;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;

@Singleton
public class NotificationCleaner {

    @Inject
    private TenantService tenantService;

    @Inject
    private NotificationService notificationService;

    /**
     * Cleans up Notifications every hour.
     * If the Notification is >= 3 days old and has been read, it will be removed.
     */
    @Schedule(hour = "*", persistent = false)
    private void cleanReadNotifications() {
        LogManager.logDebug(getClass(), "Cleaning Notifications...");

        if (!AptiBookInfoProvider.isStarted()) {
            LogManager.logInfo(getClass(), "Skipping run since AptiBook is not started.");
            return;
        }

        int numNotificationsRemoved = 0;
        List<Tenant> tenants = tenantService.getAll();
        for (Tenant tenant : tenants) {
            List<Notification> notifications = notificationService.getAll(tenant);
            for (Notification notification : notifications) {
                if (notification.getRead() && Days.between(notification.getCreation(), ZonedDateTime.now()).getAmount() > 3) {
                    try {
                        notificationService.delete(notification);
                        numNotificationsRemoved++;
                    } catch (Exception e) {
                        LogManager.logException(getClass(), e, "Could not delete Notification on cleanup.");
                    }
                }
            }
        }

        LogManager.logDebug(getClass(), "Removed " + numNotificationsRemoved + " old Notifications. Cleaning complete.");
    }

}
