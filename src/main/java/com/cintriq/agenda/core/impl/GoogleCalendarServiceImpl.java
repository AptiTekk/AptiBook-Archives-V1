package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.GoogleCalendarService;
import com.cintriq.agenda.core.GoogleService;
import com.cintriq.agenda.core.Properties;
import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.Reservation;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import javax.inject.Inject;
import java.util.UUID;

public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    @Inject
    Properties properties;

    @Inject
    GoogleService googleService;

    @Inject
    ReservationService reservationService;

    @Override
    public Calendar getCalendarService() throws Exception {
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
    public void insert(Calendar calendarService, Reservation reservation) throws Exception {
        UUID eventId = UUID.randomUUID();

        EventDateTime start = new EventDateTime();
        EventDateTime end = new EventDateTime();

        start.setDateTime(new DateTime(reservation.getTimeStart().mergeWithCalendar(reservation.getDate()).getTime()));
        end.setDateTime(new DateTime(reservation.getTimeEnd().mergeWithCalendar(reservation.getDate()).getTime()));

        Event reservationEvent = new Event();
        reservationEvent.setId(eventId.toString());
        reservationEvent.setSummary(reservation.getTitle());
        reservationEvent.setLocation(reservation.getAsset().getName());
        reservationEvent.setDescription(reservation.getDescription());
        reservationEvent.setStart(start);
        reservationEvent.setEnd(end);

        calendarService.events().insert(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservationEvent).execute();

        reservation.setGoogleEventId(eventId.toString());
        reservationService.merge(reservation);
    }

    @Override
    public Event get(Calendar calendarService, Reservation reservation) throws Exception {
        return calendarService.events().get(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId()).execute();
    }

    @Override
    public void update(Calendar calendarService, Reservation reservation) throws Exception {
        EventDateTime start = new EventDateTime();
        EventDateTime end = new EventDateTime();

        start.setDateTime(new DateTime(reservation.getTimeStart().mergeWithCalendar(reservation.getDate()).getTime()));
        end.setDateTime(new DateTime(reservation.getTimeEnd().mergeWithCalendar(reservation.getDate()).getTime()));

        Event reservationEvent = calendarService.events().get(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId()).execute();
        reservationEvent.setSummary(reservation.getTitle());
        reservationEvent.setLocation(reservation.getAsset().getName());
        reservationEvent.setDescription(reservation.getDescription());
        reservationEvent.setStart(start);
        reservationEvent.setEnd(end);

        calendarService.events().update(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId(), reservationEvent).execute();
    }

    @Override
    public void delete(Calendar calendarService, Reservation reservation) throws Exception {
        calendarService.events().delete(properties.get(CALENDAR_ID_PROPERTY.getKey()), reservation.getGoogleEventId()).execute();
    }
}
