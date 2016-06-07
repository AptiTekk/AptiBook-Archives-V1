package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Reservation;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.TimeRange;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.List;

@ManagedBean(name = "AvailableAssetsController")
@ViewScoped
public class AvailableAssetsController {

    @Inject
    private ReservationService reservationService;

    private List<Asset> availableAssets;

    public void searchForAssets(AssetType assetType, TimeRange timeRange) {
        this.availableAssets = reservationService.findAvailableAssets(assetType, timeRange, 0f);
    }

    public void onMakeReservationFired(User user, Asset asset, TimeRange timeRange) {
        AgendaLogger.logVerbose("Reserving " + asset.getName() + " for " + user.getUsername() + " on " + timeRange.getStartTimeFormatted(TimeRange.FORMAT_DATE_TIME) + " to " + timeRange.getEndTimeFormatted(TimeRange.FORMAT_DATE_TIME));
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setAsset(asset);
        reservation.setTimeStart(timeRange.getStartTime());
        reservation.setTimeEnd(timeRange.getEndTime());

        try {
            reservationService.insert(reservation);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: Tell user
        }
    }

    public List<Asset> getAvailableAssets() {
        return availableAssets;
    }
}
