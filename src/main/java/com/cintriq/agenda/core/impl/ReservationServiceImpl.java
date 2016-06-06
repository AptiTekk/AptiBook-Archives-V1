package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.*;
import com.cintriq.agenda.core.entity.*;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.NotificationFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Stateless
public class ReservationServiceImpl extends EntityServiceAbstract<Reservation> implements ReservationService {

    QReservation reservationTable = QReservation.reservation;

    @Inject
    com.cintriq.agenda.core.Properties properties;

    @Inject
    AssetService assetService;

    @Inject
    GoogleCalendarService googleCalendarService;

    @Inject
    NotificationService notificationService;

    @Inject
    UserGroupService userGroupService;

    public ReservationServiceImpl() {
        super(Reservation.class);
    }

    @Override
    public void insert(Reservation reservation) throws Exception {
        try {
            if (!properties.get(GoogleService.ACCESS_TOKEN_PROPERTY.getKey()).isEmpty()) {
                googleCalendarService.insert(googleCalendarService.getCalendarService(), reservation);
            }

            String notif_subject = properties.get(NEW_RESERVATION_NOTIFICATION_SUBJECT.getKey());
            String notif_body = properties.get(NEW_RESERVATION_NOTIFICATION_BODY.getKey());

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
            if (!properties.get(GoogleService.ACCESS_TOKEN_PROPERTY.getKey()).isEmpty()) {
                googleCalendarService.update(googleCalendarService.getCalendarService(), reservation);
            }

            String notif_subject = properties.get(NEW_RESERVATION_NOTIFICATION_SUBJECT.getKey());
            String notif_body = properties.get(NEW_RESERVATION_NOTIFICATION_BODY.getKey());
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
            if (!properties.get(GoogleService.ACCESS_TOKEN_PROPERTY.getKey()).isEmpty()) {
                googleCalendarService.delete(googleCalendarService.getCalendarService(), reservation);
            }

            String notif_subject = properties.get(NEW_RESERVATION_NOTIFICATION_SUBJECT.getKey());
            String notif_body = properties.get(NEW_RESERVATION_NOTIFICATION_BODY.getKey());
            //TODO: Traverse Reservable Owner to find all Owners

            super.delete(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds and returns a list of assets that are available for reservation at the given times from the given asset type.
     *
     * @param assetType            The asset type that a reservation is desired to be made from
     * @param startTime            The start of the desired reservation time
     * @param endTime              The end of the desired reservation time
     * @param hoursOffsetAllowance An amount of time in hours that the start and end times may be offset in case of not finding any available assets.
     * @return A list of available assets during the selected times.
     */
    @Override
    public List<Asset> findAvailableAssets(AssetType assetType, Calendar startTime, Calendar endTime, float hoursOffsetAllowance) {
        //This list contains all the assets for the given asset type.
        List<Asset> assetsOfType = assetType.getAssets();
        //This list is what will be returned, it will contain all of the assets that are available for reservation.
        List<Asset> availableAssets = new ArrayList<>();

        for (Asset asset : assetsOfType) {
            //Check for intersections of previous reservations.
            if (isAssetAvailableForReservationAtTime(asset, startTime, endTime)) {
                availableAssets.add(asset);
                AgendaLogger.logVerbose("Found available Asset: "+asset.getName());
            } else {
                AgendaLogger.logVerbose("unavailable Asset: "+asset.getName());
                //TODO: Offset time in 30 min intervals
            }
        }
        return availableAssets;
    }

    /**
     * Checks if the specified asset is available for reservation during the specified times.
     *
     * @param asset     The asset to check
     * @param startTime The start of the reservation time
     * @param endTime   The end of the reservation time
     * @return true if available, false if not.
     */
    private boolean isAssetAvailableForReservationAtTime(Asset asset, Calendar startTime, Calendar endTime) {
        //Begin by checking the assets availability times
        if (endTime.get(Calendar.HOUR_OF_DAY) * 2 + endTime.get(Calendar.MINUTE) / 30 > asset.getAvailabilityEnd().get(Calendar.HOUR_OF_DAY) * 2 +  asset.getAvailabilityEnd().get(Calendar.MINUTE) / 30
                || startTime.get(Calendar.HOUR_OF_DAY) * 2 + startTime.get(Calendar.MINUTE) / 30 < asset.getAvailabilityStart().get(Calendar.HOUR_OF_DAY) * 2 + asset.getAvailabilityStart().get(Calendar.MINUTE) / 30)
            return false;

        //Iterate over all reservations of the asset and check for intersections
        for (Reservation reservation : asset.getReservations()) {
            //Check for intersection: a ---XX___ b
            if (reservation.getTimeEnd().compareTo(startTime) > 0 && reservation.getTimeStart().compareTo(endTime) < 0)
                return false;

            //Check for intersection: b ___XX--- a
            if (startTime.compareTo(reservation.getTimeEnd()) > 0 && endTime.compareTo(reservation.getTimeStart()) < 0)
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
