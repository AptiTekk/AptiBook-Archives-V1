/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.TimeController;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateful
public class NotificationService extends MultiTenantEntityServiceAbstract<Notification> implements Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    EmailService emailService;

    @Inject
    UserService userService;

    public void sendNotification(Notification.Type type, String subject, String body, List<UserGroup> userGroupList) {
        if (subject == null || body == null || userGroupList == null)
            return;

        for (UserGroup userGroup : userGroupList) {
            for (User user : userGroup.getUsers()) {
                sendNotification(type, subject, body, user);
            }
        }
    }

    public void sendNotification(Notification.Type type, String subject, String body, User user) {
        Notification notification = new Notification(user, subject, body);
        try {
            insert(notification);
            if (!user.isAdmin() && (type == null || user.getNotificationTypeSettings().get(type)))
                emailService.sendEmailNotification(notification);
        } catch (Exception e) {
            LogManager.logException(getClass(), e, "Could not insert Notification.");
        }
    }

    public void sendNewUserRegistrationNotifications(User newUser) {
        List<User> recipients = userService.getUsersWithPermission(Permission.Descriptor.USERS_MODIFY_ALL);
        for (User user : recipients) {
            sendNotification(Notification.Type.TYPE_APPROVAL_REQUEST,
                    "New User Registration",
                    "A new user, <b>" + newUser.getFullname() +
                            "</b>, has registered for AptiBook, and is waiting for approval to sign in. " +
                            "Please approve or reject this user.",
                    user);
        }
    }

    public void sendNewReservationNotifications(Reservation reservation) {
        if (reservation == null)
            return;

        if (reservation.getResource().getNeedsApproval()) {
            sendNotification(Notification.Type.TYPE_RESERVATION_REQUESTED,
                    "New Reservation Request",
                    "A new Reservation for <b>"
                            + reservation.getResource().getName()
                            + "</b> has been requested by "
                            + "<b>"
                            + reservation.getUser().getFullname()
                            + "</b>"
                            + ".",
                    userGroupService.getHierarchyUp(reservation.getResource().getOwner()));
        } else {
            sendNotification(Notification.Type.TYPE_RESERVATION_REQUESTED,
                    "New Reservation Approved",
                    "A new Reservation for <b>"
                            + reservation.getResource().getName()
                            + "</b> has been automatically <i>approved</i> for "
                            + "<b>"
                            + reservation.getUser().getFullname()
                            + "</b>"
                            + ".",
                    userGroupService.getHierarchyUp(reservation.getResource().getOwner()));
        }
    }

    public void sendReservationDecisionNotification(Reservation reservation) {
        if (reservation == null)
            return;

        if (reservation.getStatus() == Reservation.Status.APPROVED) {
            sendNotification(Notification.Type.TYPE_RESERVATION_APPROVED,
                    "Reservation Approved",
                    "Your Reservation for <b>" + reservation.getResource().getName()
                            + "</b> from <b>"
                            + reservation.getStartTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                            + "</b> to <b>"
                            + reservation.getEndTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                            + "</b> has been Approved!",
                    reservation.getUser());
        } else if (reservation.getStatus() == Reservation.Status.REJECTED) {
            sendNotification(Notification.Type.TYPE_RESERVATION_REJECTED,
                    "Reservation Rejected",
                    "Your Reservation for <b>" + reservation.getResource().getName()
                            + "</b> from <b>"
                            + reservation.getStartTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                            + "</b> to <b>"
                            + reservation.getEndTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                            + "</b> has been Rejected.",
                    reservation.getUser());
        }
    }

    public void sendReservationCancelledNotifications(Reservation reservation) {
        if (reservation == null || reservation.getStatus() != Reservation.Status.CANCELLED)
            return;

        sendNotification(Notification.Type.TYPE_RESERVATION_CANCELLED_USER_GROUPS, "Reservation Cancelled",
                "The reservation of <b>"
                        + reservation.getResource().getName()
                        + "</b> for <b>"
                        + reservation.getTitle()
                        + "</b>, which was requested by <b>"
                        + reservation.getUser().getFullname()
                        + "</b> from <b>"
                        + reservation.getStartTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                        + "</b> to <b>"
                        + reservation.getEndTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                        + "</b>, has been Cancelled.",
                userGroupService.getHierarchyUp(reservation.getResource().getOwner())
        );

        sendNotification(Notification.Type.TYPE_RESERVATION_CANCELLED_USER, "Reservation Cancelled",
                "Your reservation of <b>"
                        + reservation.getResource().getName()
                        + "</b> for <b>"
                        + reservation.getTitle()
                        + "</b> from <b>"
                        + reservation.getStartTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                        + "</b> to <b>"
                        + reservation.getEndTime().format(TimeController.FRIENDLY_DATE_FORMATTER)
                        + "</b> has been Cancelled.",
                reservation.getUser()
        );
    }

    public void markAllAsReadForUser(User user) {
        try {
            entityManager
                    .createQuery("UPDATE Notification n SET n.notif_read = true WHERE n.user = ?1")
                    .setParameter(1, user)
                    .executeUpdate();
        } catch (PersistenceException ignored) {
        }
    }

    public List<Notification> getAllForUser(User user) {
        if (user == null)
            return null;

        try {
            List<Notification> result = entityManager
                    .createQuery("SELECT n FROM Notification n WHERE n.user = :user", Notification.class)
                    .setParameter("user", user)
                    .getResultList();

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
}
