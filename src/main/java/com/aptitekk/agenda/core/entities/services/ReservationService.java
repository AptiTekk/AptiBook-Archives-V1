/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.util.LogManager;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
     * Retrieves reservations from the database that occur within the specified month and year.
     *
     * @param month The month of the reservation, starting with 0 for January. Valid values are 0 to 11.
     * @param year  The year of the reservation.
     * @return A list of reservations during the specified month and year, or null if the month or year is invalid.
     */
    public List<Reservation> getAllInMonth(int month, int year) {
        if (month < 0 || month > 11 || year < 1)
            return null;

        Calendar start = Calendar.getInstance();
        start.set(Calendar.MILLISECOND, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.MONTH, month);
        start.set(Calendar.YEAR, year);

        Calendar end = (Calendar) start.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        return entityManager
                .createQuery("SELECT r FROM Reservation r WHERE r.date BETWEEN ?1 AND ?2 AND r.tenant = ?3", Reservation.class)
                .setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, getTenant())
                .getResultList();
    }

    /**
     * Retrieves reservations from the database that occur within the specified dates.
     *
     * @param start The start date.
     * @param end   The end date. Should be after the start date.
     * @return A list of reservations between the specified dates, or null if any date is null or the end date is not after the start date.
     */
    public List<Reservation> getAllBetweenDates(Calendar start, Calendar end) {
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
    public List<Reservation> getAllBetweenDates(Calendar start, Calendar end, AssetCategory... assetCategories) {
        if (start == null || end == null || start.compareTo(end) > 0)
            return null;

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
    public List<Reservation> getAllBetweenDates(Calendar start, Calendar end, User user, AssetCategory... assetCategories) {
        if (start == null || end == null || start.compareTo(end) > 0)
            return null;

        if (assetCategories != null)
            if (assetCategories.length == 0)
                return null;

        if (user == null) {
            return entityManager
                    .createQuery("SELECT r FROM Reservation r JOIN r.asset a WHERE r.date BETWEEN ?1 AND ?2 AND r.tenant = ?3 " + (assetCategories != null ? "AND a.assetCategory IN ?4 " : "") + (user != null ? "AND r.user = ?5" : ""), Reservation.class)
                    .setParameter(1, start)
                    .setParameter(2, end)
                    .setParameter(3, getTenant())
                    .setParameter(4, assetCategories != null ? Arrays.asList(assetCategories) : null)
                    .getResultList();
        } else {
            return entityManager
                    .createQuery("SELECT r FROM Reservation r JOIN r.asset a WHERE r.date BETWEEN ?1 AND ?2 AND r.tenant = ?3 " + (assetCategories != null ? "AND a.assetCategory IN ?4 " : "") + (user != null ? "AND r.user = ?5" : ""), Reservation.class)
                    .setParameter(1, start)
                    .setParameter(2, end)
                    .setParameter(3, getTenant())
                    .setParameter(4, assetCategories != null ? Arrays.asList(assetCategories) : null)
                    .setParameter(5, user)
                    .getResultList();
        }
    }
    /**
     * Finds and returns a list of assets that are available for reservation at the given times from the given AssetCategory.
     *
     * @param assetCategory        The AssetCategory that a reservation is desired to be made from
     * @param segmentedTimeRange   The time range of the reservation
     * @param hoursOffsetAllowance An amount of time in hours that the start and end times may be offset in case of not finding any available assets.
     * @return A list of available assets during the selected times.
     */
    public List<Asset> findAvailableAssets(AssetCategory assetCategory, SegmentedTimeRange segmentedTimeRange, float hoursOffsetAllowance) {
        //This list contains all the assets for the given AssetCategory.
        List<Asset> assetsOfType = assetCategory.getAssets();
        //This list is what will be returned, it will contain all of the assets that are available for reservation.
        List<Asset> availableAssets = new ArrayList<>();

        for (Asset asset : assetsOfType) {
            //Check for intersections of previous reservations.
            if (isAssetAvailableForReservation(asset, segmentedTimeRange)) {
                availableAssets.add(asset);
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

        //Iterate over all reservations of the asset and check for intersections
        for (Reservation reservation : asset.getReservations()) {
            //Skip rejected reservations. They aren't in the way.
            if (reservation.getStatus() == Reservation.Status.REJECTED)
                continue;

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
