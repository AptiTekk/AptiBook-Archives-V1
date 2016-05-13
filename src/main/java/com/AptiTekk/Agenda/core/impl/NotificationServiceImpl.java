/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.MailingService;
import com.AptiTekk.Agenda.core.NotificationService;
import com.AptiTekk.Agenda.core.entity.Notification;
import com.AptiTekk.Agenda.core.entity.QNotification;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.NotificationFactory;
import com.AptiTekk.Agenda.core.utilities.notification.NotificationListener;
import com.mysema.query.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author kevint
 */
@Stateless
public class NotificationServiceImpl extends EntityServiceAbstract<Notification> implements NotificationService {

    QNotification table = QNotification.notification;

    @Inject
    MailingService mailingService;

    List<NotificationListener> notificationListeners = new ArrayList<>();

    public NotificationServiceImpl() {
        super(Notification.class);
    }

    @Override
    public void insert(Notification n) {
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
    public void markAsRead(Notification n) {
        n.setRead(Boolean.TRUE);
        merge(n);
    }

    @Override
    public List<Notification> getUnread(User user) {
        List<Notification> result = new JPAQuery(entityManager).from(table)
                .where(table.user.eq(user))
                .where(table.notif_read.eq(false))
                .list(table);

        Comparator<Notification> comparator = Comparator.comparing(notif -> notif.getCreation());
        comparator = comparator.reversed();

        // Sort the stream:
        Stream<Notification> notificationStream = result.stream().sorted(comparator);

        return notificationStream.collect(Collectors.toList());
    }

    @Override
    public List<Notification> getAllByUser(User user) {
        List<Notification> result = new JPAQuery(entityManager).from(table).where(table.user.eq(user))
                .list(table);

        result.stream().filter(notification -> notification.getRead() == null).forEach(notification -> {
            notification.setRead(false);
        });

        if (result == null)
            return null;

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
}
