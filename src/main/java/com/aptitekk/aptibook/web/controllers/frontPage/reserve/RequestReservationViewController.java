/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.reserve;

import com.aptitekk.aptibook.core.domain.entities.Asset;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.entities.ReservationField;
import com.aptitekk.aptibook.core.domain.entities.ReservationFieldEntry;
import com.aptitekk.aptibook.core.domain.services.AssetService;
import com.aptitekk.aptibook.core.domain.services.NotificationService;
import com.aptitekk.aptibook.core.domain.services.ReservationFieldEntryService;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map.Entry;

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

    @Inject
    private ReservationFieldEntryService reservationFieldEntryService;

    @Inject
    private TenantSessionService tenantSessionService;

    /**
     * The asset that is being requested for reservation.
     */
    private Asset asset;

    /**
     * The reservation start time
     */
    private ZonedDateTime startTime;

    /**
     * The reservation end time
     */
    private ZonedDateTime endTime;

    /**
     * The Title for reservation being edited.
     */
    private String reservationTitle;

    /**
     * A notificationTypeSettings containing a String for each Reservation Field to store the user's input.
     */
    private HashMap<ReservationField, String> fieldMap = new HashMap<>();

    /**
     * Current Reservation being edited by user, variable to check if we can redirect to success page.
     */
    private Reservation successfulReservation = null;

    public void makeReservation() {
        //If the user refreshes the page and submits the form twice, multiple reservations can be made at the same time.
        //Therefore, we check to make sure the asset is still available for reservation. (This also prevents reserving assets which are not on the page.)
        asset = assetService.get(asset.getId()); //Refresh asset from database to get most recent reservation times.

        if (reservationService.isAssetAvailableForReservation(asset, startTime, endTime)) {
            Reservation reservation = new Reservation();
            reservation.setUser(authenticationController.getAuthenticatedUser());
            reservation.setAsset(asset);

            ZoneId currentTenantTimezone = tenantSessionService.getCurrentTenantZoneId();
            reservation.setStartTime(startTime.withZoneSameInstant(currentTenantTimezone));
            reservation.setEndTime(endTime.withZoneSameInstant(currentTenantTimezone));

            reservation.setTitle(reservationTitle);

            if (!asset.getNeedsApproval())
                reservation.setStatus(Reservation.Status.APPROVED);

            try {
                reservationService.insert(reservation);
                successfulReservation = reservation;
                for (Entry<ReservationField, String> entry : fieldMap.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        ReservationFieldEntry reservationFieldEntry = new ReservationFieldEntry();
                        reservationFieldEntry.setReservation(reservation);
                        reservationFieldEntry.setField(entry.getKey());
                        reservationFieldEntry.setContent(entry.getValue());
                        try {
                            reservationFieldEntryService.insert(reservationFieldEntry);
                        } catch (Exception e) {
                            LogManager.logException(getClass(), "Error persisting ReservationFieldEntry", e);
                        }
                    }
                }

                notificationService.sendNewReservationNotifications(reservation);
            } catch (Exception e) {
                LogManager.logException(getClass(), "Error while creating new Reservation", e);
            }
        }

    }

    /**
     * Used when the user decides to cancel making a reservation request.
     */
    public void cancel() {
        this.asset = null;
        this.startTime = null;
        this.endTime = null;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReservationTitle() {
        return reservationTitle;
    }

    public void setReservationTitle(String reservationTitle) {
        this.reservationTitle = reservationTitle;
    }

    public HashMap<ReservationField, String> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(HashMap<ReservationField, String> fieldMap) {
        this.fieldMap = fieldMap;
    }


    public Reservation getSuccessfulReservation() {
        return successfulReservation;
    }

    public void setSuccessfulReservation(Reservation successfulReservation) {
        this.successfulReservation = successfulReservation;
    }
}
