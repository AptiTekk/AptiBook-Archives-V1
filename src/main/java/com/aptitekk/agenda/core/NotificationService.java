/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entity.AppProperty;
import com.aptitekk.agenda.core.entity.Notification;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.utilities.notification.NotificationListener;

import javax.ejb.Local;
import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Local
public interface NotificationService extends EntityService<Notification> {

    public static final AppProperty NOTIFICATION_DATEFORMAT = new AppProperty("agenda.notification.dateFormat", "MM-dd-yyyy hh:mm a");

    void sendEmailNotification(Notification n)
            throws MessagingException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException;

    void markAsRead(Notification n) throws Exception;

    List<Notification> getUnread(User user);

    List<Notification> getAllByUser(User user);

    void registerListener(NotificationListener notificationListener);

    void unregisterListener(NotificationListener notificationListener);

}
