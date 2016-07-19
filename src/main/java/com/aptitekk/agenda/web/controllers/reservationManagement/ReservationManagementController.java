/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservationManagement;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.services.ReservationDecisionService;
import com.aptitekk.agenda.core.services.ReservationService;
import com.aptitekk.agenda.core.services.UserGroupService;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;
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

    @Inject
    private ReservationService reservationService;

    @Inject
    private ReservationDecisionService reservationDecisionService;

    @Inject
    private UserGroupService userGroupService;

    private HashMap<AssetType, List<ReservationDetails>> reservationDetailsMap;

    @PostConstruct
    public void init() {
        buildReservationList();
    }

    private ReservationDetails reservationDetails;

    private void buildReservationList() {
        reservationDetailsMap = new LinkedHashMap<>();

        Queue<UserGroup> queue = new LinkedList<>();
        queue.addAll(authenticationController.getAuthenticatedUser().getUserGroups());

        //Traverse down the hierarchy and determine which reservations are pending approval.
        //Then, build details about each reservation and store it in the reservationDetailsMap.
        UserGroup currentGroup;
        while ((currentGroup = queue.poll()) != null) {
            queue.addAll(currentGroup.getChildren());

            for (Asset asset : currentGroup.getAssets()) {
                for (Reservation reservation : asset.getReservations()) {

                    //Found a reservation with a pending status.
                    if (reservation.getStatus() == Reservation.Status.PENDING) {

                        //If there is not an AssetType already in the map, add one with an empty list.
                        reservationDetailsMap.putIfAbsent(asset.getAssetType(), new ArrayList<>());

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

                        reservationDetailsMap.get(asset.getAssetType()).add(new ReservationDetails(reservation, behalfUserGroup, currentDecision, hierarchyDecisions));
                    }
                }
            }
        }
    }

    public void approveReservation(ReservationDetails reservationDetails) {
        if (reservationDetails != null) {
            try {
                ReservationDecision decision = new ReservationDecision();
                decision.setApproved(true);
                decision.setReservation(reservationDetails.getReservation());
                decision.setUser(authenticationController.getAuthenticatedUser());
                decision.setUserGroup(reservationDetails.getBehalfUserGroup());
                reservationDecisionService.insert(decision);

                reservationDetails.getReservation().getDecisions().add(decision);
                if (reservationDetails.getBehalfUserGroup().isRoot() || reservationDetails.getBehalfUserGroup().getParent().isRoot())
                    reservationDetails.getReservation().setStatus(Reservation.Status.APPROVED);
                reservationService.merge(reservationDetails.getReservation());

                buildReservationList();

                FacesContext.getCurrentInstance().addMessage("pendingReservations", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "You have approved the Reservation of '" + reservationDetails.getReservation().getAsset().getName() + "' for '" + reservationDetails.getReservation().getUser().getFullname() + "'."));
            } catch (Exception e) {
                LogManager.logError("Error approving reservation. Reservation title and id: " + reservationDetails.getReservation().getTitle() + reservationDetails.getReservation().getId()  +  "Reservation for: " +  "Exception message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void rejectReservation(ReservationDetails reservationDetails) {
        if (reservationDetails != null) {
            try {
                ReservationDecision decision = new ReservationDecision();
                decision.setApproved(false);
                decision.setReservation(reservationDetails.getReservation());
                decision.setUser(authenticationController.getAuthenticatedUser());
                decision.setUserGroup(reservationDetails.getBehalfUserGroup());
                reservationDecisionService.insert(decision);

                reservationDetails.getReservation().getDecisions().add(decision);
                if (reservationDetails.getBehalfUserGroup().isRoot() || reservationDetails.getBehalfUserGroup().getParent().isRoot())
                    reservationDetails.getReservation().setStatus(Reservation.Status.REJECTED);
                reservationService.merge(reservationDetails.getReservation());

                buildReservationList();

                FacesContext.getCurrentInstance().addMessage("pendingReservations", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "You have rejected the Reservation of '" + reservationDetails.getReservation().getAsset().getName() + "' for '" + reservationDetails.getReservation().getUser().getFullname() + "'."));
            } catch (Exception e) {
                LogManager.logError("Error rejecting reservation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public List<AssetType> getAssetTypes() {
        return new ArrayList<>(reservationDetailsMap.keySet());
    }

    public List<ReservationDetails> getReservationDetailsForAssetType(AssetType assetType) {
        return reservationDetailsMap.get(assetType);
    }

    public ReservationDetails getReservationDetails() {
        return reservationDetails;
    }

    public void setReservationDetails(ReservationDetails reservationDetails) {
        this.reservationDetails = reservationDetails;
    }
}
