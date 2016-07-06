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

import com.aptitekk.agenda.core.services.MailingService;
import com.aptitekk.agenda.core.services.NotificationService;
import com.aptitekk.agenda.core.entity.Notification;
import com.aptitekk.agenda.core.entity.QNotification;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.utilities.NotificationFactory;
import com.aptitekk.agenda.core.utilities.notification.NotificationListener;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class NotificationServiceImpl extends EntityServiceAbstract<Notification> implements NotificationService, Serializable {

    QNotification table = QNotification.notification;

    @Inject
    MailingService mailingService;

    List<NotificationListener> notificationListeners = new ArrayList<>();

    public NotificationServiceImpl() {
        super(Notification.class);
    }

    @Override
    public void insert(Notification n) throws Exception {
        super.insert(n);
        notificationListeners.forEach(notificationListener -> notificationListener.pushNotification(n));
    }

    @Override
    public void sendEmailNotification(Notification n)
            throws MessagingException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        mailingService.send(NotificationFactory.convert(n));
    }

    @Override
    public void markAsRead(Notification n) throws Exception {
        n.setRead(Boolean.TRUE);
        merge(n);
    }

    @Override
    public List<Notification> getUnread(User user) {
        List<Notification> result = new JPAQuery<Notification>(entityManager).from(table)
                .where(table.user.eq(user))
                .where(table.notif_read.eq(false)).fetch();

        Comparator<Notification> comparator = Comparator.comparing(notif -> notif.getCreation());
        comparator = comparator.reversed();

        // Sort the stream:
        Stream<Notification> notificationStream = result.stream().sorted(comparator);

        return notificationStream.collect(Collectors.toList());
    }

    @Override
    public List<Notification> getAllByUser(User user) {
        List<Notification> result = new JPAQuery<Notification>(entityManager).from(table).where(table.user.eq(user))
                .fetch();

        result.stream().filter(notification -> notification.getRead() == null).forEach(notification -> {
            notification.setRead(false);
        });

        Comparator<Notification> comparator = Comparator.comparing(Notification::getRead);
        comparator = comparator.reversed();
        comparator = comparator.thenComparing(Notification::getCreation);
        comparator = comparator.reversed();
        Stream<Notification> notificationStream = result.stream().sorted(comparator);

        return notificationStream.collect(Collectors.toList());
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
