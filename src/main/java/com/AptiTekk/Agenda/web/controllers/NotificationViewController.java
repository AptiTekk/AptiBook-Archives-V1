package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.entity.Notification;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * Created by kevint on 5/13/2016.
 */
@ManagedBean
@ViewScoped
public class NotificationViewController {

    @ManagedProperty(value = "#{NotificationController}")
    private NotificationController notificationController;

    @PostConstruct
    public void init() {
        notificationController.pullNotifications();
    }

    @PreDestroy
    public void markUnreadRead() {
        notificationController.markUnreadRead();
    }

    public List<Notification> getUnread() {
        return notificationController.getUnread();
    }

    public List<Notification> getNotifications() {
        return notificationController.getNotifications();
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }

    public void setNotificationController(NotificationController notificationController) {
        this.notificationController = notificationController;
    }
}
