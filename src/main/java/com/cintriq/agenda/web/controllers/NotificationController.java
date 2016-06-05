package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.NotificationService;
import com.cintriq.agenda.core.Properties;
import com.cintriq.agenda.core.UserService;
import com.cintriq.agenda.core.entity.Notification;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.FacesSessionHelper;
import com.cintriq.agenda.core.utilities.notification.NotificationListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "NotificationController")
@SessionScoped
public class NotificationController implements NotificationListener {

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
            notificationService.registerListener(this);
        }
    }

    @PreDestroy
    public void deinit() {
        notificationService.unregisterListener(this);
    }

    public void pullNotifications() {
        setNotifications(notificationService.getAllByUser(user));
        setUnread(notificationService.getUnread(user));
    }

    public void markUnreadRead() {
        System.out.println("Marking all unread as read");
        if (unread != null)
            unread.forEach(notification -> {
                try {
                    notificationService.markAsRead(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        pullNotifications();
    }

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
        if (properties.get(NotificationService.NOTIFICATION_DATEFORMAT.getKey()) != null) {
            DateFormat dateFormat = new SimpleDateFormat(properties.get(NotificationService.NOTIFICATION_DATEFORMAT.getKey()));
            return dateFormat.format(date);
        }
        return date.toString();
    }

    @Override
    public void pushNotification(Notification n) {
        AgendaLogger.logVerbose("Notification pushed to frontend!");
        FacesMessage message = new FacesMessage(n.getSubject(), (n.getBody().length() > 30 ? n.getBody().substring(0, 30) + "..." : n.getBody()));
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, message);
    }
}
