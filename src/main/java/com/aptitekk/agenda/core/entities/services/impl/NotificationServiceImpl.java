/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.agenda.core.entities.services.impl;

import com.aptitekk.agenda.core.entities.Notification;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.entities.services.NotificationService;
import com.aptitekk.agenda.core.entities.services.UserGroupService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateful
public class NotificationServiceImpl extends MultiTenantEntityServiceAbstract<Notification> implements NotificationService, Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Override
    public void buildNotification(String subject, String body, List<UserGroup> userGroupList) {
        if (subject == null || body == null || userGroupList == null)
            return;

        for (UserGroup userGroup : userGroupList) {
            for (User user : userGroup.getUsers()) {
                buildNotification(subject, body, user);
            }
        }
    }

    @Override
    public void buildNotification(String subject, String body, User user) {
        System.out.println("Notification body: " + body);
        Notification notification = new Notification(user, subject, body);
        try {
            insert(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNewReservationNotifications(Reservation reservation) {
        if (reservation == null)
            return;

        if (reservation.getAsset().getNeedsApproval()) {
            buildNotification(
                    "New Reservation Request",
                    "A new Reservation for "
                            + reservation.getAsset().getName()
                            + " has been requested by "
                            + reservation.getUser().getFullname()
                            + ".",
                    userGroupService.getHierarchyUp(reservation.getAsset().getOwner()));
        } else {
            buildNotification(
                    "New Reservation Approved",
                    "A new Reservation for "
                            + reservation.getAsset().getName()
                            + " has been automatically approved for "
                            + reservation.getUser().getFullname()
                            + ".",
                    userGroupService.getHierarchyUp(reservation.getAsset().getOwner()));
        }
    }

    @Override
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
                            + " has been Approved!",
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
                            + " has been Rejected.",
                    reservation.getUser());
        }
    }

    @Override
    public void markAllAsReadForUser(User user) {
        try {
            entityManager
                    .createQuery("UPDATE Notification n SET n.notif_read = true WHERE n.user = ?1")
                    .setParameter(1, user)
                    .executeUpdate();
        } catch (PersistenceException ignored) {
        }
    }

    @Override
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
