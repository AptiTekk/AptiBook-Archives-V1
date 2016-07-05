package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.*;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;

import javax.ejb.Local;
import java.util.List;
import java.util.Set;

@Local
public interface ReservationService extends EntityService<Reservation> {

    List<Asset> findAvailableAssets(AssetType type, SegmentedTimeRange segmentedTimeRange, float cushionInHours);

    boolean isAssetAvailableForReservation(Asset asset, SegmentedTimeRange segmentedTimeRange);

    Set<Reservation> getAllUnderUser(User user);

}
