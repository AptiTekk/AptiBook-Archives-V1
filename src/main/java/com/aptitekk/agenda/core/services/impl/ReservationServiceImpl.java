/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetType;
import com.aptitekk.agenda.core.entities.QReservation;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.services.*;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Stateless
public class ReservationServiceImpl extends EntityServiceAbstract<Reservation> implements ReservationService, Serializable {

    @Inject
    AssetService assetService;

    @Inject
    NotificationService notificationService;

    @Inject
    UserGroupService userGroupService;

    @Inject
    UserService userService;

    public ReservationServiceImpl() {
        super(Reservation.class);
    }

    /**
     * Finds and returns a list of assets that are available for reservation at the given times from the given asset type.
     *
     * @param assetType            The asset type that a reservation is desired to be made from
     * @param segmentedTimeRange   The time range of the reservation
     * @param hoursOffsetAllowance An amount of time in hours that the start and end times may be offset in case of not finding any available assets.
     * @return A list of available assets during the selected times.
     */
    @Override
    public List<Asset> findAvailableAssets(AssetType assetType, SegmentedTimeRange segmentedTimeRange, float hoursOffsetAllowance) {
        //This list contains all the assets for the given asset type.
        List<Asset> assetsOfType = assetType.getAssets();
        //This list is what will be returned, it will contain all of the assets that are available for reservation.
        List<Asset> availableAssets = new ArrayList<>();

        for (Asset asset : assetsOfType) {
            //Check for intersections of previous reservations.
            if (isAssetAvailableForReservation(asset, segmentedTimeRange)) {
                availableAssets.add(asset);
                LogManager.logDebug("Available.");
            } else {
                LogManager.logDebug("Unavailable.");
                //TODO: Offset time in 30 min intervals
            }
        }
        return availableAssets;
    }

    /**
     * Checks if the specified asset is available for reservation during the specified times.
     *
     * @param asset              The asset to check
     * @param segmentedTimeRange The time range of the reservation
     * @return true if available, false if not.
     */
    public boolean isAssetAvailableForReservation(Asset asset, SegmentedTimeRange segmentedTimeRange) {
        LogManager.logDebug("Checking " + asset.getName());

        //If the asset does not specify a start/end time, return false.
        if (asset.getAvailabilityStart() == null || asset.getAvailabilityEnd() == null)
            return false;

        //Return false if the reservation start or end time is not within the availability time of the asset
        if (asset.getAvailabilityStart().compareTo(segmentedTimeRange.getStartTime()) > 0 || asset.getAvailabilityEnd().compareTo(segmentedTimeRange.getEndTime()) < 0)
            return false;

        //Iterate over all reservations of the asset and check for intersections
        for (Reservation reservation : asset.getReservations()) {
            //Check date of reservation -- Skip if it's not same day as requested.
            if (reservation.getDate().get(Calendar.DAY_OF_YEAR) != segmentedTimeRange.getDate().get(Calendar.DAY_OF_YEAR) || reservation.getDate().get(Calendar.YEAR) != segmentedTimeRange.getDate().get(Calendar.YEAR))
                continue;

            //Check for intersection: a ---XX___ b
            if (reservation.getTimeEnd().compareTo(segmentedTimeRange.getStartTime()) > 0 && reservation.getTimeStart().compareTo(segmentedTimeRange.getEndTime()) < 0)
                return false;

            //Check for intersection: b ___XX--- a
            if (segmentedTimeRange.getStartTime().compareTo(reservation.getTimeEnd()) > 0 && segmentedTimeRange.getEndTime().compareTo(reservation.getTimeStart()) < 0)
                return false;
        }
        return true;
    }

}
