/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement;

import com.aptitekk.aptibook.core.entities.*;
import com.aptitekk.aptibook.core.entities.services.UserGroupService;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@RequestScoped
public class ReservationDetailsController implements Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private AuthenticationController authenticationController;

    public Map<AssetCategory, List<ReservationDetails>> buildReservationList(Reservation.Status status) {
        Map<AssetCategory, List<ReservationDetails>> reservationDetailsMap = new LinkedHashMap<>();

        Queue<UserGroup> queue = new LinkedList<>();
        queue.addAll(authenticationController.getAuthenticatedUser().getUserGroups());

        //Traverse down the hierarchy and determine which reservations are approved.
        //Then, build details about each reservation and store it in the reservationDetailsMap.
        UserGroup currentGroup;
        while ((currentGroup = queue.poll()) != null) {
            queue.addAll(currentGroup.getChildren());

            for (Asset asset : currentGroup.getAssets()) {
                for (Reservation reservation : asset.getReservations()) {

                    //Found a reservation with a pending status.
                    if (reservation.getStatus() == status) {
                        //If there is not an AssetCategory already in the map, add one with an empty list.
                        reservationDetailsMap.putIfAbsent(asset.getAssetCategory(), new ArrayList<>());

                        reservationDetailsMap.get(asset.getAssetCategory()).add(generateReservationDetails(reservation));
                    }
                }
            }
        }

        return reservationDetailsMap;
    }

    private ReservationDetails generateReservationDetails(Reservation reservation) {
        //Traverse up the hierarchy and determine the decisions that have already been made.
        LinkedHashMap<UserGroup, ReservationDecision> hierarchyDecisions = new LinkedHashMap<>();
        List<UserGroup> hierarchyUp = userGroupService.getHierarchyUp(reservation.getAsset().getOwner());
        UserGroup behalfUserGroup = null;
        //This for loop descends to properly order the groups for display on the page.
        for (int i = hierarchyUp.size() - 1; i >= 0; i--) {
            UserGroup userGroup = hierarchyUp.get(i);
            //This group is the group that the authenticated user is acting on behalf of when making a decision.
            if (authenticationController.getAuthenticatedUser().getUserGroups().contains(userGroup))
                behalfUserGroup = userGroup;
            for (ReservationDecision decision : reservation.getDecisions()) {
                if (decision.getUserGroup().equals(userGroup)) {
                    hierarchyDecisions.put(userGroup, decision);
                    break;
                }
            }
            hierarchyDecisions.putIfAbsent(userGroup, null);
        }

        ReservationDecision currentDecision = null;
        for (ReservationDecision decision : reservation.getDecisions()) {
            if (decision.getUserGroup().equals(behalfUserGroup)) {
                currentDecision = decision;
                break;
            }
        }

        return new ReservationDetails(reservation, behalfUserGroup, currentDecision, hierarchyDecisions);
    }

}
