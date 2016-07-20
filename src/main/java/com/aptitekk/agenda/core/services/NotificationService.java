/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.Notification;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.utilities.notification.NotificationListener;

import javax.ejb.Local;
import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Local
public interface NotificationService extends MultiTenantEntityService<Notification> {

    void buildNotification(String subject, String body, List<UserGroup> userGroupList);

    void sendEmailNotification(Notification notification)
            throws MessagingException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException;

    void markAllAsReadForUser(User user);

    List<Notification> getAllForUser(User user);

    void registerListener(NotificationListener notificationListener);

    void unregisterListener(NotificationListener notificationListener);

}
