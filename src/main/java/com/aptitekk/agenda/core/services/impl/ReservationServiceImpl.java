/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entity.*;
import com.aptitekk.agenda.core.entity.enums.ReservationStatus;
import com.aptitekk.agenda.core.services.*;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.NotificationFactory;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Stateless
public class ReservationServiceImpl extends EntityServiceAbstract<Reservation> implements ReservationService, Serializable {

    QReservation reservationTable = QReservation.reservation;

    @Inject
    AssetService assetService;

    @Inject
    GoogleCalendarService googleCalendarService;

    @Inject
    NotificationService notificationService;

    @Inject
    UserGroupService userGroupService;

    @Inject
    UserService userService;

    public ReservationServiceImpl() {
        super(Reservation.class);
    }

    @Override
    public void insert(Reservation reservation) throws Exception {
        try {
            if (!GoogleService.ACCESS_TOKEN_PROPERTY.isEmpty()) {
                googleCalendarService.insert(googleCalendarService.getCalendarService(), reservation);
            }

            String notif_subject = "Subject";
            String notif_body = "Body";

            List<UserGroup> userGroups = userGroupService.getHierarchyUp(reservation.getAsset().getOwner());

            for (UserGroup group : userGroups) {
                for (User user : group.getUsers()) {
                    try {
                        Notification n = (Notification) NotificationFactory.createDefaultNotificationBuilder()
                                .setTo(user)
                                .setSubject(notif_subject)
                                .setBody(notif_body)
                                .build(reservation, user);
                        notificationService.insert(n);
                    } catch (MessagingException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            super.insert(reservation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reservation reservation, int id) throws Exception {
        try {
            if (!GoogleService.ACCESS_TOKEN_PROPERTY.isEmpty()) {
                googleCalendarService.update(googleCalendarService.getCalendarService(), reservation);
            }

            //String notif_subject = properties.get(NEW_RESERVATION_NOTIFICATION_SUBJECT.getKey());
            //String notif_body = properties.get(NEW_RESERVATION_NOTIFICATION_BODY.getKey());
            //TODO: Traverse Reservable Owner to find all Owners
            /*for (UserGroup group : reservation.getReservable().getOwners()) {
                for (User user : group.getUsers()) {
                    try {
                        Notification n = (Notification) createDefaultNotificationBuilder()
                                .setTo(user)
                                .setSubject(notif_subject)
                                .setBody(notif_body)
                                .build(reservation, user);
                        notificationService.insert(n);
                    } catch (MessagingException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }*/

            super.update(reservation, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        Reservation reservation = get(id);
        try {
            /*if (!properties.get(GoogleService.ACCESS_TOKEN_PROPERTY.getKey()).isEmpty()) {
                googleCalendarService.delete(googleCalendarService.getCalendarService(), reservation);
            }

            String notif_subject = properties.get(NEW_RESERVATION_NOTIFICATION_SUBJECT.getKey());
            String notif_body = properties.get(NEW_RESERVATION_NOTIFICATION_BODY.getKey());
            //TODO: Traverse Reservable Owner to find all Owners*/

            super.delete(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStatus(Reservation reservation, ReservationStatus reservationStatus) throws Exception {
        reservation.setStatus(reservationStatus);
        update(reservation, reservation.getId());
    }

    @Override
    public void approveReservation(Reservation reservation, User owner, boolean approved) throws Exception {
        ReservationApproval approval = new ReservationApproval();
        approval.setReservation(reservation);
        approval.setUser(owner);
        approval.setApproved((approved) ? ReservationStatus.APPROVED : ReservationStatus.REJECTED);

        entityManager.persist(approval);

        resolveStatus(reservation);
    }

    @Override
    public void resolveStatus(Reservation reservation) throws Exception {
        reservation = entityManager.merge(reservation);
        List<ReservationApproval> approvals = reservation.getApprovals();

        UserGroup highestGroup = getHighestApproval(approvals);

        Boolean answer = getGroupAnswer(highestGroup, approvals, highestGroup.getUsers().size());
        if (answer == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        } else if (answer) {
            reservation.setStatus(ReservationStatus.APPROVED);
        } else {
            reservation.setStatus(ReservationStatus.REJECTED);
        }
    }

    private UserGroup getHighestApproval(List<ReservationApproval> approvals) {
        User user;
        UserGroup result = null;

        int level = 0;
        for (ReservationApproval approval : approvals) {
            user = approval.getUser();
            UserGroup[] highest = userGroupService.getSenior(user.getUserGroups());

            for (UserGroup group : highest) {
                List<UserGroup> hierarchy = userGroupService.getHierarchyUp(group);

                if (level < hierarchy.size()) {
                    level = hierarchy.size();
                    result = group;
                }
            }
        }
        return result;
    }

    private Boolean getGroupAnswer(UserGroup group, List<ReservationApproval> approvals, int minimumForApproval) {
        List<ReservationApproval> groupOnlyApprovals = filterByGroup(group, approvals);
        int accepted = 0;
        for (ReservationApproval approval : groupOnlyApprovals) {
            if (approval.getApproved().equals(ReservationStatus.APPROVED)) {
                accepted++;
            }
        }

        return accepted >= minimumForApproval;
    }

    private List<ReservationApproval> filterByGroup(UserGroup group, List<ReservationApproval> approvals) {
        List<ReservationApproval> groupOnlyApprovals = new ArrayList<>();
        approvals.forEach(approval -> {
            if (approval.getUser().getUserGroups().contains(group)) {
                groupOnlyApprovals.add(approval);
            }
        });
        return groupOnlyApprovals;
    }

    /**
     * Finds and returns a list of assets that are available for reservation at the given times from the given asset type.
     *
     * @param assetType            The asset type that a reservation is desired to be made from
     * @param segmentedTimeRange   The time range of the reservation
     * @param hoursOffsetAllowance An amount of time in hours that the start and end times may be offset in case of not finding any available assets.
     * @return A list of available assets during the selected times.
     */
    @Override
    public List<Asset> findAvailableAssets(AssetType assetType, SegmentedTimeRange segmentedTimeRange, float hoursOffsetAllowance) {
        //This list contains all the assets for the given asset type.
        List<Asset> assetsOfType = assetType.getAssets();
        //This list is what will be returned, it will contain all of the assets that are available for reservation.
        List<Asset> availableAssets = new ArrayList<>();

        for (Asset asset : assetsOfType) {
            //Check for intersections of previous reservations.
            if (isAssetAvailableForReservation(asset, segmentedTimeRange)) {
                availableAssets.add(asset);
                LogManager.logDebug("Available.");
            } else {
                LogManager.logDebug("Unavailable.");
                //TODO: Offset time in 30 min intervals
            }
        }
        return availableAssets;
    }

    /**
     * Checks if the specified asset is available for reservation during the specified times.
     *
     * @param asset              The asset to check
     * @param segmentedTimeRange The time range of the reservation
     * @return true if available, false if not.
     */
    public boolean isAssetAvailableForReservation(Asset asset, SegmentedTimeRange segmentedTimeRange) {
        LogManager.logDebug("Checking " + asset.getName());

        //If the asset does not specify a start/end time, return false.
        if (asset.getAvailabilityStart() == null || asset.getAvailabilityEnd() == null)
            return false;

        //Return false if the reservation start or end time is not within the availability time of the asset
        if (asset.getAvailabilityStart().compareTo(segmentedTimeRange.getStartTime()) > 0 || asset.getAvailabilityEnd().compareTo(segmentedTimeRange.getEndTime()) < 0)
            return false;

        //Iterate over all reservations of the asset and check for intersections
        for (Reservation reservation : asset.getReservations()) {
            //Check date of reservation -- Skip if it's not same day as requested.
            if (reservation.getDate().get(Calendar.DAY_OF_YEAR) != segmentedTimeRange.getDate().get(Calendar.DAY_OF_YEAR) || reservation.getDate().get(Calendar.YEAR) != segmentedTimeRange.getDate().get(Calendar.YEAR))
                continue;

            //Check for intersection: a ---XX___ b
            if (reservation.getTimeEnd().compareTo(segmentedTimeRange.getStartTime()) > 0 && reservation.getTimeStart().compareTo(segmentedTimeRange.getEndTime()) < 0)
                return false;

            //Check for intersection: b ___XX--- a
            if (segmentedTimeRange.getStartTime().compareTo(reservation.getTimeEnd()) > 0 && segmentedTimeRange.getEndTime().compareTo(reservation.getTimeStart()) < 0)
                return false;
        }
        return true;
    }

    @Override
    public Set<Reservation> getAllUnderUser(User user) {
        final Set<Reservation> result = new HashSet<>();
        UserGroup[] seniors = userGroupService.getSenior(user.getUserGroups());
        for (UserGroup group : seniors) {
            group.getAssets().forEach(asset -> result.addAll(asset.getReservations()));
            result.addAll(getDecendantsReservation(result, group));
        }
        return result;
    }

    private Set<Reservation> getDecendantsReservation(Set<Reservation> set, UserGroup group) {
        group.getChildren().forEach(userGroup -> {
                    userGroup.getAssets().forEach(asset
                            -> asset.getReservations().forEach(set::add));
                    getDecendantsReservation(set, userGroup);
                }
        );
        return set;
    }

}
