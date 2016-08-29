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

import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import com.aptitekk.aptibook.core.util.LogManager;

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

    @Inject EmailService emailService;

    public void buildNotification(String subject, String body, List<UserGroup> userGroupList) {
        if (subject == null || body == null || userGroupList == null)
            return;

        for (UserGroup userGroup : userGroupList) {
            for (User user : userGroup.getUsers()) {
                buildNotification(subject, body, user);
            }
        }
    }

    public void buildNotification(String subject, String body, User user) {
        Notification notification = new Notification(user, subject, body);
        try {
            insert(notification);

            if(user.getWantsEmailNotifications()) {
                    emailService.sendEmailNotification(notification);
            }
        } catch (Exception e) {
            LogManager.logError("Error in building Notification, or sending Email notification. Notfication id: " + notification.getId() );
            e.printStackTrace();
        }
    }

    public void sendNewReservationNotifications(Reservation reservation) {
        if (reservation == null)
            return;

        if (reservation.getAsset().getNeedsApproval()) {
            buildNotification(
                    "New Reservation Request",
                    "A new Reservation for "
                            + reservation.getAsset().getName()
                            + " has been <i>requested</i> by "
                            + "<b>"
                            + reservation.getUser().getFullname()
                            + "</b>"
                            + ".",
                    userGroupService.getHierarchyUp(reservation.getAsset().getOwner()));
        } else {
            buildNotification(
                    "New Reservation Approved",
                    "A new Reservation for "
                            + reservation.getAsset().getName()
                            + " has been automatically <i>approved</i> for "
                            + "<b>"
                            +reservation.getUser().getFullname()
                            + "</b>"
                            + ".",
                    userGroupService.getHierarchyUp(reservation.getAsset().getOwner()));
        }
    }

    public void sendReservationDecisionNotification(Reservation reservation) {
        if (reservation == null)
            return;

        if (reservation.getStatus() == Reservation.Status.APPROVED) {
            buildNotification(
                    "Reservation Approved",
                    "Your Reservation for " + reservation.getAsset().getName()
                            + " on "
                            + reservation.getFormattedDate()
                            + " from "
                            + reservation.getTimeStart().getTimeString()
                            + " to "
                            + reservation.getTimeEnd().getTimeString()
                            + " has been <i>Approved!</i>",
                    reservation.getUser());
        } else if (reservation.getStatus() == Reservation.Status.REJECTED) {
            buildNotification(
                    "Reservation Rejected",
                    "Your Reservation for " + reservation.getAsset().getName()
                            + " on "
                            + reservation.getFormattedDate()
                            + " from "
                            + reservation.getTimeStart().getTimeString()
                            + " to "
                            + reservation.getTimeEnd().getTimeString()
                            + " has been <i>Rejected.<i>",
                    reservation.getUser());
        }
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
