package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.GoogleCalendarService;
import com.AptiTekk.Agenda.core.GoogleService;
import com.AptiTekk.Agenda.core.Properties;
import com.AptiTekk.Agenda.core.ReservationService;
import com.AptiTekk.Agenda.core.entity.Reservation;
import com.AptiTekk.Agenda.core.utilities.AgendaLogger;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by kevint on 5/16/2016.
 */
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    @Inject
    Properties properties;

    @Inject
    GoogleService googleService;

    @Inject
    ReservationService reservationService;

    @Override
    public Calendar getCalendarService() throws IOException {
        Credential cred = googleService.authorize(CALENDAR_USER_ID);
        if (cred == null) { //reauthorize
            AgendaLogger.logMessage("Google Calendar Credential was null");
            return null;
        } else {
            return new com.google.api.services.calendar.Calendar.Builder(
                    new NetHttpTransport(), new JacksonFactory(), cred)
                    .setApplicationName(GoogleService.APP_NAME)
                    .build();
        }
    }

    @Override
    public void insert(Calendar calendarService, Reservation reservation) throws IOException {
        UUID eventId = UUID.randomUUID();

        EventDateTime start = new EventDateTime();
        EventDateTime end = new EventDateTime();

        start.setDateTime(new DateTime(reservation.getTimeStart()));
        end.setDateTime(new DateTime(reservation.getTimeEnd()));

        Event reservationEvent = new Event();
        reservationEvent.setId(eventId.toString());
        reservationEvent.setSummary(reservation.getTitle());
        reservationEvent.setLocation(reservation.getReservable().getName());
        reservationEvent.setDescription(reservation.getDescription());
        reservationEvent.setStart(start);
        reservationEvent.setEnd(end);

        calendarService.events().insert(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservationEvent).execute();

        reservation.setGoogleEventId(eventId.toString());
        reservationService.merge(reservation);
    }

    @Override
    public Event get(Calendar calendarService, Reservation reservation) throws IOException {
        return calendarService.events().get(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId()).execute();
    }

    @Override
    public void update(Calendar calendarService, Reservation reservation) throws IOException {
        EventDateTime start = new EventDateTime();
        EventDateTime end = new EventDateTime();

        start.setDateTime(new DateTime(reservation.getTimeStart()));
        end.setDateTime(new DateTime(reservation.getTimeEnd()));

        Event reservationEvent = calendarService.events().get(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId()).execute();
        reservationEvent.setSummary(reservation.getTitle());
        reservationEvent.setLocation(reservation.getReservable().getName());
        reservationEvent.setDescription(reservation.getDescription());
        reservationEvent.setStart(start);
        reservationEvent.setEnd(end);

        calendarService.events().update(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId(), reservationEvent).execute();
    }

    @Override
    public void delete(Calendar calendarService, Reservation reservation) throws IOException {
        calendarService.events().delete(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId()).execute();
    }
}
