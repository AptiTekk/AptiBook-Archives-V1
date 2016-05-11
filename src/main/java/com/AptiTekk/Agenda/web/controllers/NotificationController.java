package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.NotificationService;
import com.AptiTekk.Agenda.core.Properties;
import com.AptiTekk.Agenda.core.UserService;
import com.AptiTekk.Agenda.core.entity.Notification;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.FacesSessionHelper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kevint on 5/9/2016.
 * Happy birthday Me
 */
@ManagedBean
@SessionScoped
public class NotificationController {

    @Inject
    private UserService userService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private Properties properties;

    private List<Notification> notifications;

    private List<Notification> unread;

    private User user;

    @PostConstruct
    public void init() {
        String loggedInUser
                = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (loggedInUser != null) {
            this.setUser(userService.findByName(loggedInUser));
            pullNotifications();
        }
    }

    private void pullNotifications() {
        setNotifications(notificationService.getAllByUser(user));
        setUnread(notificationService.getUnread(user));
    }

//    @PreDestroy
//    public void markUnreadRead() {
//        System.out.println("Marking all unread as read");
//        if(unread != null)
//            unread.forEach(notification -> notificationService.markAsRead(notification));
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Notification> getUnread() {
        return unread;
    }

    public void setUnread(List<Notification> unread) {
        this.unread = unread;
    }

    public String formatDateTime(Date date) {
        if(properties.get(NotificationService.NOTIFICATION_DATEFORMAT.getKey()) != null) {
            DateFormat dateFormat = new SimpleDateFormat(properties.get(NotificationService.NOTIFICATION_DATEFORMAT.getKey()));
            return dateFormat.format(date);
        }
        return date.toString();
    }
}
