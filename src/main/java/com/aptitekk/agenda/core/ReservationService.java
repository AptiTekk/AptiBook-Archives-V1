package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entity.*;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;

import javax.ejb.Local;
import java.util.List;
import java.util.Set;

@Local
public interface ReservationService extends EntityService<Reservation> {

    AppProperty NEW_RESERVATION_NOTIFICATION_SUBJECT = new AppProperty("agenda.notification.reservation.subject", "New {reservation.asset.name} Reservation - {reservation.title}");
    AppProperty NEW_RESERVATION_NOTIFICATION_BODY = new AppProperty("agenda.notification.reservation.body",
            "Hey {user.fullname},\nA new reservation has been requested for {reservation.asset.name}. Details are as follows:"
                    + "\n\t {reservation.title}"
                    + "\n\t {reservation.timeStart}"
                    + "\n\t {reservation.timeEnd}");

    List<Asset> findAvailableAssets(AssetType type, SegmentedTimeRange segmentedTimeRange, float cushionInHours);

    boolean isAssetAvailableForReservation(Asset asset, SegmentedTimeRange segmentedTimeRange);

    Set<Reservation> getAllUnderUser(User user);

}
