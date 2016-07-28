/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.myReservations;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.util.time.DateUtils;
import com.aptitekk.agenda.core.util.time.SegmentedTime;
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class MyReservationsController implements Serializable {

    @Inject
    private AuthenticationController authenticationController;

    private Map<AssetCategory, List<Reservation>> presentReservations;

    @PostConstruct
    private void init() {
        buildPresentReservationList();
    }

    private void buildPresentReservationList() {
        presentReservations = new LinkedHashMap<>();

        if (authenticationController != null && authenticationController.getAuthenticatedUser() != null) {

            for (Reservation reservation : authenticationController.getAuthenticatedUser().getReservations()) {

                //Make sure that the reservation is after right now.
                if (DateUtils.isBeforeDay(reservation.getDate(), Calendar.getInstance()))
                    continue;
                if (reservation.getTimeEnd().compareTo(new SegmentedTime()) < 0)
                    continue;

                presentReservations.putIfAbsent(reservation.getAsset().getAssetCategory(), new ArrayList<>());
                presentReservations.get(reservation.getAsset().getAssetCategory()).add(reservation);
            }
        }
    }

    public Set<AssetCategory> getAssetCategories() {
        return presentReservations.keySet();
    }

    public List<Reservation> getPresentReservationsForCategory(AssetCategory assetCategory) {
        return presentReservations.get(assetCategory);
    }
}
