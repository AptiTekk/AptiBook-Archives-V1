package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.AppProperty;
import com.AptiTekk.Agenda.core.entity.Reservable;
import com.AptiTekk.Agenda.core.entity.ReservableType;
import com.AptiTekk.Agenda.core.entity.Reservation;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;

@Local
public interface ReservationService extends EntityService<Reservation> {
    
    public static final AppProperty NEW_RESERVATION_NOTIFICATION_SUBJECT = new AppProperty("agenda.notification.reservation.subject", "New {reservation.reservable.name} Reservation - {reservation.title}");
    public static final AppProperty NEW_RESERVATION_NOTIFICATION_BODY = new AppProperty("agenda.notification.reservation.body", 
            "Hey {user.fullname},\nA new reservation has been requested for {reservation.reservable.name}. Details are as follows:"
                    + "\n\t {reservation.title}"
                    + "\n\t {reservation.timeStart}"
                    + "\n\t {reservation.timeEnd}");
  
    List<Reservable> findAvailableReservables(ReservableType type, Date startDateTime, Date endDateTime);
    
}
