package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.*;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Local
public interface ReservationService extends EntityService<Reservation> {
    
    public static final AppProperty NEW_RESERVATION_NOTIFICATION_SUBJECT = new AppProperty("agenda.notification.reservation.subject", "New {reservation.asset.name} Reservation - {reservation.title}");
    public static final AppProperty NEW_RESERVATION_NOTIFICATION_BODY = new AppProperty("agenda.notification.reservation.body", 
            "Hey {user.fullname},\nA new reservation has been requested for {reservation.asset.name}. Details are as follows:"
                    + "\n\t {reservation.title}"
                    + "\n\t {reservation.timeStart}"
                    + "\n\t {reservation.timeEnd}");

    List<Asset> findAvailableAssets(AssetType type, Date startDateTime, Date endDateTime);

    Set<Reservation> getAllUnderUser(User user);

  /*  void timeAvailable(String startTime, String endTime);*/
    
}
