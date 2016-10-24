/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.User;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@Stateful
public class ReservationService extends MultiTenantEntityServiceAbstract<Reservation> implements Serializable {

    @Inject
    ResourceService resourceService;

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
        return getAllBetweenDates(start, end, null, (ResourceCategory[]) null);
    }

    /**
     * Retrieves reservations from the database that occur within the specified dates.
     * Only reservations within the specified categories will be returned.
     *
     * @param start           The start date.
     * @param end             The end date. Should be after the start date.
     * @param resourceCategories The categories to filter by. Null if no category filtering should be performed.
     * @return A list of reservations between the specified dates, or null if any date is null or the end date is not after the start date.
     */
    public List<Reservation> getAllBetweenDates(ZonedDateTime start, ZonedDateTime end, ResourceCategory... resourceCategories) {
        return getAllBetweenDates(start, end, null, resourceCategories);
    }

    /**
     * Retrieves reservations from the database that occur within the specified dates.
     * Only reservations within the specified categories will be returned.
     *
     * @param start           The start date.
     * @param end             The end date. Should be after the start date.
     * @param user            The user to filter by. Null if no user filtering should be performed.
     * @param resourceCategories The categories to filter by. Null if no category filtering should be performed.
     * @return A list of reservations between the specified dates, or null if any date is null or the end date is not after the start date.
     */
    public List<Reservation> getAllBetweenDates(ZonedDateTime start, ZonedDateTime end, User user, ResourceCategory... resourceCategories) {
        if (start == null || end == null || start.isAfter(end))
            return null;

        if (resourceCategories != null)
            if (resourceCategories.length == 0)
                return null;

        StringBuilder queryBuilder = new StringBuilder("SELECT r FROM Reservation r JOIN r.resource a WHERE ((r.startTime BETWEEN ?1 AND ?2) OR (r.startTime < ?1 AND r.endTime > ?1)) AND r.tenant = ?3 ");
        HashMap<Integer, Object> parameterMap = new HashMap<>();

        if (resourceCategories != null) {
            queryBuilder.append("AND a.resourceCategory IN ?4 ");
            parameterMap.put(4, Arrays.asList(resourceCategories));
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
     * Finds and returns a list of resources that are available for reservation at the given times from the given ResourceCategory.
     *
     * @param resourceCategory The ResourceCategory that a reservation is desired to be made from
     * @param startTime     The reservation start time
     * @param endTime       The reservation end time
     * @return A list of available resources during the selected times.
     */
    public List<Resource> findAvailableResources(ResourceCategory resourceCategory, ZonedDateTime startTime, ZonedDateTime endTime) {
        //This list contains all the resources for the given ResourceCategory.
        List<Resource> resourcesOfType = resourceCategory.getResources();
        //This list is what will be returned, it will contain all of the resources that are available for reservation.
        List<Resource> availableResources = new ArrayList<>();

        for (Resource resource : resourcesOfType) {
            //Check for intersections of previous reservations.
            if (isResourceAvailableForReservation(resource, startTime, endTime)) {
                availableResources.add(resource);
            }
        }
        return availableResources;
    }

    /**
     * Checks if the specified resource is available for reservation during the specified times.
     *
     * @param resource     The resource to check
     * @param startTime The reservation start time
     * @param endTime   The reservation end time
     * @return true if available, false if not.
     */
    public boolean isResourceAvailableForReservation(Resource resource, ZonedDateTime startTime, ZonedDateTime endTime) {
        //Iterate over all reservations of the resource and check for intersections
        for (Reservation reservation : resource.getReservations()) {
            //Ignore rejected reservations.
            if (reservation.getStatus() == Reservation.Status.REJECTED)
                continue;
            //If user canceled reservation, allow resource to be reserved.
            if(reservation.getStatus() == Reservation.Status.CANCELLED)
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
