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
package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Notification;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.services.MailingService;
import com.aptitekk.agenda.core.services.NotificationService;
import com.aptitekk.agenda.core.utilities.NotificationFactory;
import com.aptitekk.agenda.core.utilities.notification.NotificationListener;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateful
public class NotificationServiceImpl extends MultiTenantEntityServiceAbstract<Notification> implements NotificationService, Serializable {

    @Inject
    MailingService mailingService;

    List<NotificationListener> notificationListeners = new ArrayList<>();

    @Override
    public void insert(Notification n) throws Exception {
        super.insert(n);
        notificationListeners.forEach(notificationListener -> notificationListener.pushNotification(n));
    }

    @Override
    public void sendEmailNotification(Notification notification)
            throws MessagingException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        if (notification == null)
            return;

        mailingService.send(NotificationFactory.convert(notification));
    }

    @Override
    public void markAsRead(Notification notification) throws Exception {
        if (notification == null)
            return;

        notification.setRead(Boolean.TRUE);
        merge(notification);
    }

    @Override
    public void buildNotification(String subject, String body, List<UserGroup> userGroupList) {
        if (subject == null || body == null || userGroupList == null)
            return;

        for (UserGroup userGroup : userGroupList) {
            for (User user : userGroup.getUsers()) {
                Notification notification = new Notification(user, subject, body);
                try {
                    insert(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Notification> getUnread(User user) {
        if (user == null)
            return null;

        try {
            List<Notification> result = entityManager
                    .createQuery("SELECT n FROM Notification n WHERE n.user = :user AND n.notif_read = false", Notification.class)
                    .setParameter("user", user)
                    .getResultList();

            Comparator<Notification> comparator = Comparator.comparing(notif -> notif.getCreation());
            comparator = comparator.reversed();

            // Sort the stream:
            Stream<Notification> notificationStream = result.stream().sorted(comparator);

            return notificationStream.collect(Collectors.toList());
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public List<Notification> getAllByUser(User user) {
        if (user == null)
            return null;

        try {
            List<Notification> result = entityManager
                    .createQuery("SELECT n FROM Notification n WHERE n.user = :user", Notification.class)
                    .setParameter("user", user)
                    .getResultList();

            result.stream().filter(notification -> notification.getRead() == null).forEach(notification -> {
                notification.setRead(false);
            });

            Comparator<Notification> comparator = Comparator.comparing(Notification::getRead);
            comparator = comparator.reversed();
            comparator = comparator.thenComparing(Notification::getCreation);
            comparator = comparator.reversed();
            Stream<Notification> notificationStream = result.stream().sorted(comparator);

            return notificationStream.collect(Collectors.toList());
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public void registerListener(NotificationListener newListener) {
        notificationListeners.add(newListener);
    }

    @Override
    public void unregisterListener(NotificationListener notificationListener) {
        notificationListeners.remove(notificationListener);
    }
}
