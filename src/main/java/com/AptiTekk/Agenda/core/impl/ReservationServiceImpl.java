package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.*;
import com.AptiTekk.Agenda.core.entity.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.AptiTekk.Agenda.core.utilities.NotificationFactory.createDefaultNotificationBuilder;

@Stateless
public class ReservationServiceImpl extends EntityServiceAbstract<Reservation> implements ReservationService {

    QReservation reservationTable = QReservation.reservation;

    @Inject
    Properties properties;

    @Inject
    ReservableService reservableService;

    @Inject
    GoogleCalendarService googleCalendarService;

    @Inject
    NotificationService notificationService;

    public ReservationServiceImpl() {
        super(Reservation.class);
    }

    @Override
    public void insert(Reservation reservation) {
        try {
            if (!properties.get(GoogleService.ACCESS_TOKEN_PROPERTY.getKey()).isEmpty()) {
                googleCalendarService.insert(googleCalendarService.getCalendarService(), reservation);
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

            super.insert(reservation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reservable> findAvailableReservables(ReservableType type, Date startDateTime, Date endDateTime
    ) {
        List<Reservable> reservables = reservableService.getAllByType(type);
        List<Reservable> result = new ArrayList<>();

        for (Reservable reservable : reservables) {
            boolean openSlot = true;
            for (Reservation reservation : reservable.getReservations()) {
                if (reservation.getTimeStart().before(startDateTime)
                        && reservation.getTimeEnd().before(startDateTime)) {
                    openSlot = true;
                } else if (reservation.getTimeStart().after(startDateTime)
                        && reservation.getTimeEnd().after(startDateTime)) {
                    openSlot = true;
                } else {
                    openSlot = false;
                }
            }
            if (openSlot) {
                result.add(reservable);
            }
        }
        return result;
    }

}
