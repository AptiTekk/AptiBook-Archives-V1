/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservationManagement;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetType;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class ReservationManagementController implements Serializable {

    @Inject
    private AuthenticationController authenticationController;

    private HashMap<AssetType, List<Reservation>> reservationMap;

    @PostConstruct
    public void init() {
        buildReservationList();
    }

    private void buildReservationList() {
        reservationMap = new LinkedHashMap<>();

        Queue<UserGroup> queue = new LinkedList<>();
        queue.addAll(authenticationController.getAuthenticatedUser().getUserGroups());

        UserGroup currentGroup;
        while ((currentGroup = queue.poll()) != null) {
            queue.addAll(currentGroup.getChildren());

            for (Asset asset : currentGroup.getAssets()) {
                for (Reservation reservation : asset.getReservations()) {
                    if (reservation.getStatus() == Reservation.Status.PENDING) {
                        reservationMap.putIfAbsent(asset.getAssetType(), new ArrayList<>());
                        reservationMap.get(asset.getAssetType()).add(reservation);
                    }
                }
            }
        }
    }

    public HashMap<AssetType, List<Reservation>> getReservationMap() {
        return reservationMap;
    }
}
