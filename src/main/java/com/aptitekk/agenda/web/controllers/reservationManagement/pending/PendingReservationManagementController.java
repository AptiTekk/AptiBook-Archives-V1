/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservationManagement.pending;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.services.NotificationService;
import com.aptitekk.agenda.core.services.ReservationDecisionService;
import com.aptitekk.agenda.core.services.ReservationService;
import com.aptitekk.agenda.core.services.UserGroupService;
import com.aptitekk.agenda.core.util.LogManager;
import com.aptitekk.agenda.web.controllers.AuthenticationController;
import com.aptitekk.agenda.web.controllers.reservationManagement.ReservationDetails;
import com.aptitekk.agenda.web.controllers.reservationManagement.ReservationDetailsController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class PendingReservationManagementController implements Serializable {

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private ReservationDetailsController reservationDetailsController;

    @Inject
    private ReservationService reservationService;

    @Inject
    private ReservationDecisionService reservationDecisionService;

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private NotificationService notificationService;

    private Map<AssetCategory, List<ReservationDetails>> reservationDetailsMap;

    private ReservationDetails reservationDetails;

    @PostConstruct
    public void init() {
        buildReservationList();
    }

    private void buildReservationList() {
        reservationDetailsMap = reservationDetailsController.buildReservationList(Reservation.Status.PENDING);
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
                if (reservationDetails.getBehalfUserGroup().isRoot() || reservationDetails.getBehalfUserGroup().getParent().isRoot()) {
                    reservationDetails.getReservation().setStatus(Reservation.Status.APPROVED);
                    notificationService.buildNotification(
                            "Reservation Approved",
                            "Your Reservation for "+reservationDetails.getReservation().getAsset().getName()
                                    +" on "
                                    +reservationDetails.getReservation().getFormattedDate()
                                    +" from "
                                    +reservationDetails.getReservation().getTimeStart().getTimeString()
                                    +" to "
                                    +reservationDetails.getReservation().getTimeEnd().getTimeString()
                                    +" has been Approved!",
                            authenticationController.getAuthenticatedUser());
                }
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
                if (reservationDetails.getBehalfUserGroup().isRoot() || reservationDetails.getBehalfUserGroup().getParent().isRoot()) {
                    reservationDetails.getReservation().setStatus(Reservation.Status.REJECTED);
                    notificationService.buildNotification(
                            "Reservation Rejected",
                            "Your Reservation for "+reservationDetails.getReservation().getAsset().getName()
                                    +" on "
                                    +reservationDetails.getReservation().getFormattedDate()
                                    +" from "
                                    +reservationDetails.getReservation().getTimeStart().getTimeString()
                                    +" to "
                                    +reservationDetails.getReservation().getTimeEnd().getTimeString()
                                    +" has been Rejected.",
                            authenticationController.getAuthenticatedUser());
                }
                reservationService.merge(reservationDetails.getReservation());

                buildReservationList();

                FacesContext.getCurrentInstance().addMessage("pendingReservations", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "You have rejected the Reservation of '" + reservationDetails.getReservation().getAsset().getName() + "' for '" + reservationDetails.getReservation().getUser().getFullname() + "'."));
            } catch (Exception e) {
                LogManager.logError("Error rejecting reservation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public List<AssetCategory> getAssetCategories() {
        return new ArrayList<>(reservationDetailsMap.keySet());
    }

    public List<ReservationDetails> getReservationDetailsForAssetCategory(AssetCategory assetCategory) {
        return reservationDetailsMap.get(assetCategory);
    }

    public ReservationDetails getReservationDetails() {
        return reservationDetails;
    }

    public void setReservationDetails(ReservationDetails reservationDetails) {
        this.reservationDetails = reservationDetails;
    }
}
