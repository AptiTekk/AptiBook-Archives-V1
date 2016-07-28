/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ReservationService extends MultiTenantEntityService<Reservation> {

    List<Asset> findAvailableAssets(AssetCategory type, SegmentedTimeRange segmentedTimeRange, float cushionInHours);

    boolean isAssetAvailableForReservation(Asset asset, SegmentedTimeRange segmentedTimeRange);

}
