package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.GoogleService;
import static com.AptiTekk.Agenda.core.GoogleService.CALENDAR_ID_PROPERTY;
import com.AptiTekk.Agenda.core.NotificationService;
import com.AptiTekk.Agenda.core.Properties;
import com.AptiTekk.Agenda.core.ReservableService;
import javax.ejb.Stateless;

import com.AptiTekk.Agenda.core.ReservationService;
import com.AptiTekk.Agenda.core.entity.Notification;
import com.AptiTekk.Agenda.core.entity.QReservation;
import com.AptiTekk.Agenda.core.entity.Reservable;
import com.AptiTekk.Agenda.core.entity.ReservableType;
import com.AptiTekk.Agenda.core.entity.Reservation;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import static com.AptiTekk.Agenda.core.utilities.NotificationFactory.createDefaultNotificationBuilder;
import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import javax.mail.MessagingException;

import java.util.List;
import javax.inject.Inject;

@Stateless
public class ReservationServiceImpl extends EntityServiceAbstract<Reservation> implements ReservationService {

    QReservation reservationTable = QReservation.reservation;

    @Inject
    Properties properties;

    @Inject
    ReservableService reservableService;

    @Inject
    GoogleService googleService;

    @Inject
    NotificationService notificationService;

    public ReservationServiceImpl() {
        super(Reservation.class);
    }

    @Override
    public void insert(Reservation reservation) {
        try {
            if (!properties.get(GoogleService.ACCESS_TOKEN_PROPERTY.getKey()).isEmpty()) {
                Event event = new Event();
                event.setSummary(reservation.getTitle());
                event.setDescription(reservation.getReservable().getName() + "\n" + reservation.getDescription());
                googleService.getCalendarService().events().insert(properties.get(CALENDAR_ID_PROPERTY.getKey()), event);
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
