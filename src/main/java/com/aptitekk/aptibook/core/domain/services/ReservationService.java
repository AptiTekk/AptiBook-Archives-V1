/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Asset;
import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.User;
import com.aptitekk.aptibook.core.util.LogManager;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@Stateful
public class ReservationService extends MultiTenantEntityServiceAbstract<Reservation> implements Serializable {

    @Inject
    AssetService assetService;

    @Inject
    NotificationService notificationService;

    @Inject
    UserGroupService userGroupService;

    @Inject
    UserService userService;

    /**
     * Retrieves reservations from the database that occur within the specified dates.
     *
     * @param start The start date.
     * @param end   The end date. Should be after the start date.
     * @return A list of reservations between the specified dates, or null if any date is null or the end date is not after the start date.
     */
    public List<Reservation> getAllBetweenDates(ZonedDateTime start, ZonedDateTime end) {
        return getAllBetweenDates(start, end, null, (AssetCategory[]) null);
    }

    /**
     * Retrieves reservations from the database that occur within the specified dates.
     * Only reservations within the specified categories will be returned.
     *
     * @param start           The start date.
     * @param end             The end date. Should be after the start date.
     * @param assetCategories The categories to filter by. Null if no category filtering should be performed.
     * @return A list of reservations between the specified dates, or null if any date is null or the end date is not after the start date.
     */
    public List<Reservation> getAllBetweenDates(ZonedDateTime start, ZonedDateTime end, AssetCategory... assetCategories) {
        return getAllBetweenDates(start, end, null, assetCategories);
    }

    /**
     * Retrieves reservations from the database that occur within the specified dates.
     * Only reservations within the specified categories will be returned.
     *
     * @param start           The start date.
     * @param end             The end date. Should be after the start date.
     * @param user            The user to filter by. Null if no user filtering should be performed.
     * @param assetCategories The categories to filter by. Null if no category filtering should be performed.
     * @return A list of reservations between the specified dates, or null if any date is null or the end date is not after the start date.
     */
    public List<Reservation> getAllBetweenDates(ZonedDateTime start, ZonedDateTime end, User user, AssetCategory... assetCategories) {
        if (start == null || end == null || start.isAfter(end))
            return null;

        if (assetCategories != null)
            if (assetCategories.length == 0)
                return null;

        StringBuilder queryBuilder = new StringBuilder("SELECT r FROM Reservation r JOIN r.asset a WHERE r.startTime BETWEEN ?1 AND ?2 AND r.tenant = ?3 ");
        HashMap<Integer, Object> parameterMap = new HashMap<>();

        if (assetCategories != null) {
            queryBuilder.append("AND a.assetCategory IN ?4 ");
            parameterMap.put(4, Arrays.asList(assetCategories));
        }

        if (user != null) {
            queryBuilder.append("AND r.user = ?5 ");
            parameterMap.put(5, user);
        }

        TypedQuery<Reservation> query = entityManager
                .createQuery(queryBuilder.toString(), Reservation.class)
                .setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, getTenant());

        for (Map.Entry<Integer, Object> parameter : parameterMap.entrySet()) {
            query.setParameter(parameter.getKey(), parameter.getValue());
        }

        return query.getResultList();
    }

    /**
     * Finds and returns a list of assets that are available for reservation at the given times from the given AssetCategory.
     *
     * @param assetCategory The AssetCategory that a reservation is desired to be made from
     * @param startTime     The reservation start time
     * @param endTime       The reservation end time
     * @return A list of available assets during the selected times.
     */
    public List<Asset> findAvailableAssets(AssetCategory assetCategory, ZonedDateTime startTime, ZonedDateTime endTime) {
        //This list contains all the assets for the given AssetCategory.
        List<Asset> assetsOfType = assetCategory.getAssets();
        //This list is what will be returned, it will contain all of the assets that are available for reservation.
        List<Asset> availableAssets = new ArrayList<>();

        for (Asset asset : assetsOfType) {
            //Check for intersections of previous reservations.
            if (isAssetAvailableForReservation(asset, startTime, endTime)) {
                availableAssets.add(asset);
            }
        }
        return availableAssets;
    }

    /**
     * Checks if the specified asset is available for reservation during the specified times.
     *
     * @param asset     The asset to check
     * @param startTime The reservation start time
     * @param endTime   The reservation end time
     * @return true if available, false if not.
     */
    public boolean isAssetAvailableForReservation(Asset asset, ZonedDateTime startTime, ZonedDateTime endTime) {
        LogManager.logDebug("Checking " + asset.getName());

        //Iterate over all reservations of the asset and check for intersections
        for (Reservation reservation : asset.getReservations()) {
            //Ignore rejected reservations.
            if (reservation.getStatus() == Reservation.Status.REJECTED)
                continue;

            //If the reservation's end time is before our start time, we're okay.
            if (reservation.getEndTime().isBefore(startTime))
                continue;

            //If the reservation's start time is after our end time, we're okay.
            if (reservation.getStartTime().isAfter(endTime))
                continue;

            //All checks failed, there was a conflict.
            return false;
        }
        return true;
    }

}
