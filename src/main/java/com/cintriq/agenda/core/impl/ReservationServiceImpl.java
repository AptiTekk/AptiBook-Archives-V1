package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.*;
import com.cintriq.agenda.core.entity.*;
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


    public String startTime;
    public String endTime;

    public ReservationServiceImpl() {
        super(Reservation.class);
    }



/*
    @Override
    public void timeAvailable(String startTime, String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

*/


    @Override
    public void insert(Reservation reservation) throws Exception {
        try {
            if (!properties.get(GoogleService.ACCESS_TOKEN_PROPERTY.getKey()).isEmpty()) {
                googleCalendarService.insert(googleCalendarService.getCalendarService(), reservation);
            }

            String notif_subject = properties.get(NEW_RESERVATION_NOTIFICATION_SUBJECT.getKey());
            String notif_body = properties.get(NEW_RESERVATION_NOTIFICATION_BODY.getKey());

            if(reservation.getAsset() == null){
                System.out.println("reservation.getAsset() is null");
            }
            if(reservation.getAsset().getOwner() == null){
                System.out.println("reservation.getAsset().getOwner*() is null");
            }

/*
            List<UserGroup> userGroups = userGroupService.getHierarchyUp(reservation.getAsset().getOwner());

            for (UserGroup group : userGroups) {
                System.out.println("Group: " + group.getName());
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
            }*/

            super.insert(reservation);
        } catch (IOException e) {
            System.out.println("Catch in insert(reservation) is thrown");
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

    @Override
    public List<Asset> findAvailableAssets(AssetType type, Date specifiedStartDateTime, Date specifiedEndDateTime, float cushionInHours) {
        List<Asset> assetsGivenByType = assetService.getAllByType(type);
        List<Asset> result = new ArrayList<>();

        for (Asset assetOfGivenType : assetsGivenByType) {
            //Make sure given times are in between availability for that asset
            if (specifiedEndDateTime.before(assetOfGivenType.getAvailabilityEnd()) && specifiedStartDateTime.after(assetOfGivenType.getAvailabilityStart())) {
                //Now lets make sure we aren't stepping on already-made reservations
                if (freeOfOtherReservations(assetOfGivenType, specifiedStartDateTime)) {
                    result.add(assetOfGivenType);
                } else {
                    Calendar cl = Calendar.getInstance();
                    cl.setTime(specifiedStartDateTime);
                    cl.add(Calendar.HOUR, (int) (cushionInHours * 10));
                    if (freeOfOtherReservations(assetOfGivenType, cl.getTime())) {
                        result.add(assetOfGivenType);
                    }
                }
            }
        }
        return result;
    }

    private boolean freeOfOtherReservations(Asset asset, Date specifiedStartDateTime) {
        for (Reservation alreadyMadeReservation : asset.getReservations()) {
            if (alreadyMadeReservation.getTimeStart().before(specifiedStartDateTime)
                    && alreadyMadeReservation.getTimeEnd().before(specifiedStartDateTime)) {
                return true;
            } else if (alreadyMadeReservation.getTimeStart().after(specifiedStartDateTime)
                    && alreadyMadeReservation.getTimeEnd().after(specifiedStartDateTime)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<Reservation> getAllUnderUser(User user) {
        final Set<Reservation> result = new HashSet<>();
        UserGroup[] seniors = userGroupService.getSenior(user.getUserGroups());
        for (UserGroup group : seniors) {
            group.getAssets().forEach(reservable -> result.addAll(reservable.getReservations()));
            result.addAll(getDecendantsReservation(result, group));
        }
        return result;
    }

    private Set<Reservation> getDecendantsReservation(Set<Reservation> set, UserGroup group) {
        group.getChildren().forEach(userGroup -> {
            userGroup.getAssets().forEach(reservable
                    -> reservable.getReservations().forEach(set::add));
                    getDecendantsReservation(set, userGroup);
                }
        );
        return set;
    }

}
