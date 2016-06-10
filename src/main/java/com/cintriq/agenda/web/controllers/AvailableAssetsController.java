package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Reservation;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.time.CalendarRange;
import com.cintriq.agenda.core.utilities.time.SegmentedTime;
import com.cintriq.agenda.core.utilities.time.SegmentedTimeRange;

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

    public void searchForAssets(AssetType assetType, SegmentedTimeRange segmentedTimeRange) {
        this.availableAssets = reservationService.findAvailableAssets(assetType, segmentedTimeRange, 0f);
    }

    public void onMakeReservationFired(User user, Asset asset, SegmentedTimeRange segmentedTimeRange) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setAsset(asset);
        reservation.setDate(segmentedTimeRange.getDate());
        reservation.setTimeStart(segmentedTimeRange.getStartTime());
        reservation.setTimeEnd(segmentedTimeRange.getEndTime());

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
