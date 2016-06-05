package com.cintriq.agenda.core;

import com.cintriq.agenda.core.entity.AppProperty;
import com.cintriq.agenda.core.entity.Reservation;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import javax.ejb.Local;
import java.io.IOException;
import java.text.SimpleDateFormat;

@Local
public interface GoogleCalendarService {

    String CALENDAR_USER_ID = "CALENDAR";

    AppProperty CALENDAR_ID_PROPERTY = new AppProperty("agenda.google.calendar.calendarID", "primary");

    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

    Calendar getCalendarService() throws IOException;

    void insert(Calendar calendarService, Reservation reservation) throws IOException;

    Event get(Calendar calendarService, Reservation reservation) throws IOException;

    void update(Calendar calendarService, Reservation reservation) throws IOException;

    void delete(Calendar calendarService, Reservation reservation) throws IOException;


}
