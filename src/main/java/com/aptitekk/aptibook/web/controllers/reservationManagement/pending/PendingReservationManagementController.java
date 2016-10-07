/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement.pending;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.ReservationDecision;
import com.aptitekk.aptibook.core.domain.services.NotificationService;
import com.aptitekk.aptibook.core.domain.services.ReservationDecisionService;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.controllers.reservationManagement.ReservationDetails;
import com.aptitekk.aptibook.web.controllers.reservationManagement.ReservationDetailsController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class PendingReservationManagementController implements Serializable {

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private ReservationDetailsController reservationDetailsController;

    @Inject
    private HelpController helpController;

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

        helpController.setCurrentTopic(HelpController.Topic.RESERVATION_MANAGEMENT_PENDING);
    }

    private void buildReservationList() {
        reservationDetailsMap = reservationDetailsController.buildReservationList(Reservation.Status.PENDING);
    }

    public void approveReservation(ReservationDetails reservationDetails) {
        if (reservationDetails != null) {
            try {
                boolean decisionExisted = false;
                for (ReservationDecision reservationDecision : reservationDetails.getReservation().getDecisions()) {
                    if (reservationDecision.getUser().equals(authenticationController.getAuthenticatedUser())) {
                        if (!reservationDecision.isApproved()) {
                            reservationDecision.setApproved(true);
                            reservationDecisionService.merge(reservationDecision);
                        }
                        decisionExisted = true;
                        break;
                    }
                }

                if (!decisionExisted) {
                    ReservationDecision decision = new ReservationDecision();
                    decision.setApproved(true);
                    decision.setReservation(reservationDetails.getReservation());
                    decision.setUser(authenticationController.getAuthenticatedUser());
                    decision.setUserGroup(reservationDetails.getBehalfUserGroup());
                    reservationDecisionService.insert(decision);

                    reservationDetails.getReservation().getDecisions().add(decision);
                    if (reservationDetails.getBehalfUserGroup().isRoot() || reservationDetails.getBehalfUserGroup().getParent().isRoot()) {
                        reservationDetails.getReservation().setStatus(Reservation.Status.APPROVED);
                        notificationService.sendReservationDecisionNotification(reservationDetails.getReservation());
                    }
                    reservationService.merge(reservationDetails.getReservation());
                }

                buildReservationList();

                FacesContext.getCurrentInstance().addMessage("pendingReservations", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "You have approved the Reservation of '" + reservationDetails.getReservation().getAsset().getName() + "' for '" + reservationDetails.getReservation().getUser().getFullname() + "'."));
            } catch (Exception e) {
                LogManager.logException("Error approving reservation.", e);
            }
        }
    }

    public void rejectReservation(ReservationDetails reservationDetails) {
        if (reservationDetails != null) {
            try {
                boolean decisionExisted = false;
                for (ReservationDecision reservationDecision : reservationDetails.getReservation().getDecisions()) {
                    if (reservationDecision.getUser().equals(authenticationController.getAuthenticatedUser())) {
                        if (reservationDecision.isApproved()) {
                            reservationDecision.setApproved(false);
                            reservationDecisionService.merge(reservationDecision);
                        }
                        decisionExisted = true;
                        break;
                    }
                }

                if (!decisionExisted) {
                    ReservationDecision decision = new ReservationDecision();
                    decision.setApproved(false);
                    decision.setReservation(reservationDetails.getReservation());
                    decision.setUser(authenticationController.getAuthenticatedUser());
                    decision.setUserGroup(reservationDetails.getBehalfUserGroup());
                    reservationDecisionService.insert(decision);

                    reservationDetails.getReservation().getDecisions().add(decision);
                    if (reservationDetails.getBehalfUserGroup().isRoot() || reservationDetails.getBehalfUserGroup().getParent().isRoot()) {
                        reservationDetails.getReservation().setStatus(Reservation.Status.REJECTED);
                        notificationService.sendReservationDecisionNotification(reservationDetails.getReservation());
                    }
                    reservationService.merge(reservationDetails.getReservation());
                }

                buildReservationList();

                FacesContext.getCurrentInstance().addMessage("pendingReservations", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "You have rejected the Reservation of '" + reservationDetails.getReservation().getAsset().getName() + "' for '" + reservationDetails.getReservation().getUser().getFullname() + "'."));
            } catch (Exception e) {
                LogManager.logException("Error rejecting reservation", e);
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
