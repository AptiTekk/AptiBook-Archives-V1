package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.Reservation;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;

import javax.ejb.Local;
import java.util.List;
import java.util.Set;

@Local
public interface ReservationService extends EntityService<Reservation> {

    List<Asset> findAvailableAssets(AssetType type, SegmentedTimeRange segmentedTimeRange, float cushionInHours);

    boolean isAssetAvailableForReservation(Asset asset, SegmentedTimeRange segmentedTimeRange);

    Set<Reservation> getAllUnderUser(User user);

    void approveReservation(Reservation reservation, User owner, boolean approved) throws Exception;

    void resolveStatus(Reservation reservation) throws Exception;

}
