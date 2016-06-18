package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.entity.Notification;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean(name = "NotificationViewController")
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
