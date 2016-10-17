/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationFields;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.ReservationField;
import com.aptitekk.aptibook.core.domain.entities.ReservationFieldEntry;
import com.aptitekk.aptibook.core.domain.services.ReservationFieldEntryService;
import com.aptitekk.aptibook.core.domain.services.ReservationFieldService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class ReservationFieldController implements Serializable {

    @Inject
    private ReservationFieldService reservationFieldService;

    @Inject
    private ReservationFieldEntryService reservationFieldEntryService;

    private Map<ResourceCategory, List<ReservationField>> reservationFieldCache = new HashMap<>();
    private Map<Reservation, Map<ReservationField, String>> reservationFieldEntryCache = new HashMap<>();

    public List<ReservationField> getReservationFields(ResourceCategory resourceCategory) {
        //noinspection Java8CollectionsApi (Otherwise we are calling getAllForResourceCategory every time this method is called.)
        if (reservationFieldCache.get(resourceCategory) == null)
            reservationFieldCache.put(resourceCategory, reservationFieldService.getAllForResourceCategory(resourceCategory));

        return reservationFieldCache.get(resourceCategory);
    }

    public String getEntryTextForReservationField(Reservation reservation, ReservationField reservationField) {
        if (reservation == null || reservationField == null)
            return null;

        //Build a cache of all the entries for this Reservation so we don't have to fire a query for every single entry.
        if (reservationFieldEntryCache.get(reservation) == null) {
            List<ReservationFieldEntry> reservationFieldEntries = reservationFieldEntryService.getAllForReservation(reservation);
            Map<ReservationField, String> reservationFieldEntryMap = new HashMap<>();

            for (ReservationFieldEntry reservationFieldEntry : reservationFieldEntries) {
                reservationFieldEntryMap.put(reservationFieldEntry.getField(), reservationFieldEntry.getContent());
            }

            reservationFieldEntryCache.put(reservation, reservationFieldEntryMap);
        }

        return reservationFieldEntryCache.get(reservation).get(reservationField);
    }

}
