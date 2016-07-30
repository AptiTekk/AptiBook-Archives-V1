/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.frontPage.reserve;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.services.AssetService;
import com.aptitekk.agenda.core.entities.services.NotificationService;
import com.aptitekk.agenda.core.entities.services.ReservationService;
import com.aptitekk.agenda.core.util.LogManager;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class RequestReservationViewController implements Serializable {

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetService assetService;

    @Inject
    private NotificationService notificationService;

    /**
     * The asset that is being requested for reservation.
     */
    private Asset asset;

    /**
     * The Segmented Time Range of the reservation request.
     */
    private SegmentedTimeRange segmentedTimeRange;

    /**
     * The Title for reservation being edited.
     */
    private String reservationTitle;

    public void makeReservation() {
        //If the user refreshes the page and submits the form twice, multiple reservations can be made at the same time.
        //Therefore, we check to make sure the asset is still available for reservation. (This also prevents reserving assets which are not on the page.)
        asset = assetService.get(asset.getId()); //Refresh asset from database to get most recent reservation times.

        if (reservationService.isAssetAvailableForReservation(asset, segmentedTimeRange)) {
            Reservation reservation = new Reservation();
            reservation.setUser(authenticationController.getAuthenticatedUser());
            reservation.setAsset(asset);
            reservation.setDate(segmentedTimeRange.getDate());
            reservation.setTimeStart(segmentedTimeRange.getStartTime());
            reservation.setTimeEnd(segmentedTimeRange.getEndTime());
            reservation.setTitle(reservationTitle);
            if (!asset.getNeedsApproval())
                reservation.setStatus(Reservation.Status.APPROVED);

            try {
                reservationService.insert(reservation);
                LogManager.logInfo("Reservation persisted, Reservation Id and Title: " + reservation.getId() + ", " + reservation.getTitle());
                notificationService.sendNewReservationNotifications(reservation);
            } catch (Exception e) {
                LogManager.logError("Error in Making reservation. Asset name, and user name: " + asset.getName() + authenticationController.getAuthenticatedUser().getFullname() + "Exception message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Used when the user decides to cancel making a reservation request.
     */
    public void cancel() {
        this.asset = null;
        this.segmentedTimeRange = null;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public SegmentedTimeRange getSegmentedTimeRange() {
        return segmentedTimeRange;
    }

    public void setSegmentedTimeRange(SegmentedTimeRange segmentedTimeRange) {
        this.segmentedTimeRange = segmentedTimeRange;
    }

    public String getReservationTitle() {
        return reservationTitle;
    }

    public void setReservationTitle(String reservationTitle) {
        this.reservationTitle = reservationTitle;
    }
}
