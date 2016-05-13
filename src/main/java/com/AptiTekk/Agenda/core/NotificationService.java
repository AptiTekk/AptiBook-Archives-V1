/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.AppProperty;
import com.AptiTekk.Agenda.core.entity.Notification;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.notification.NotificationListener;

import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 *
 * @author kevint
 */
public interface NotificationService extends EntityService<Notification> {
    
    public static final AppProperty NOTIFICATION_DATEFORMAT = new AppProperty("agenda.notification.dateFormat", "MM-dd-yyyy hh:mm a");
    
    void sendEmailNotification(Notification n) 
            throws MessagingException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException;
    
    void markAsRead(Notification n);
    
    List<Notification> getUnread(User user);

    List<Notification> getAllByUser(User user);

    void registerListener(NotificationListener notificationListener);

    void unregisterListener(NotificationListener notificationListener);
    
}
