package com.aptitekk.agenda.web.controllers.notifications;

import com.aptitekk.agenda.core.entity.Notification;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.services.NotificationService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.utilities.FacesSessionHelper;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.notification.NotificationListener;

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
        DateFormat dateFormat = new SimpleDateFormat(NotificationService.NOTIFICATION_DATEFORMAT);
        return dateFormat.format(date);
    }

    @Override
    public void pushNotification(Notification n) {
        LogManager.logDebug("Notification pushed to frontend!");
        FacesMessage message = new FacesMessage(n.getSubject(), (n.getBody().length() > 30 ? n.getBody().substring(0, 30) + "..." : n.getBody()));
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, message);
    }
}
