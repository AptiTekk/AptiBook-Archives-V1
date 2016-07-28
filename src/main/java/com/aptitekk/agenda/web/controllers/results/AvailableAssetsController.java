/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.results;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.entities.services.AssetService;
import com.aptitekk.agenda.core.entities.services.NotificationService;
import com.aptitekk.agenda.core.entities.services.ReservationService;
import com.aptitekk.agenda.core.entities.services.UserGroupService;
import com.aptitekk.agenda.core.util.LogManager;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AvailableAssetsController implements Serializable {

    @Inject
    private ReservationService reservationService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private AssetService assetService;

    private List<Asset> availableAssets;
    private List<Asset> filteredAssets;

    private List<Tag> filterTags;
    private List<Tag> selectedFilterTags;

    public void searchForAssets(AssetCategory assetCategory, SegmentedTimeRange segmentedTimeRange) {
        this.availableAssets = reservationService.findAvailableAssets(assetCategory, segmentedTimeRange, 0f);

        filterTags = assetCategory.getTags();
        selectedFilterTags = new ArrayList<>();
        filterAssets();
    }

    /**
     * Updates the filteredAssets list with only those assets which contain all the selectedFilterTags
     */
    private void filterAssets() {
        filteredAssets = new ArrayList<>();

        for (Asset asset : availableAssets) {
            boolean skipAsset = false;

            //Make sure the Asset has all the selected filter Tags
            for (Tag tag : selectedFilterTags) {
                if (!asset.getTags().contains(tag))
                    skipAsset = true;
            }

            if (skipAsset)
                continue;

            filteredAssets.add(asset);
        }
    }

    public void onMakeReservationFired(User user, Asset asset, SegmentedTimeRange segmentedTimeRange) {

        //If the user refreshes the page and submits the form twice, multiple reservations can be made at the same time.
        //Therefore, we check to make sure the asset is still available for reservation. (This also prevents reserving assets which are not on the page.)
        if (availableAssets != null && availableAssets.contains(asset)) {
            asset = assetService.get(asset.getId()); //Refresh asset from database to get most recent reservation times.

            if (reservationService.isAssetAvailableForReservation(asset, segmentedTimeRange)) {
                Reservation reservation = new Reservation();
                reservation.setUser(user);
                reservation.setAsset(asset);
                reservation.setDate(segmentedTimeRange.getDate());
                reservation.setTimeStart(segmentedTimeRange.getStartTime());
                reservation.setTimeEnd(segmentedTimeRange.getEndTime());
                if (!asset.getNeedsApproval())
                    reservation.setStatus(Reservation.Status.APPROVED);

                try {
                    reservationService.insert(reservation);
                    LogManager.logInfo("Reservation persisted, Reservation Id and Title: " + reservation.getId() + ", " + reservation.getTitle());
                    notificationService.sendNewReservationNotifications(reservation);
                } catch (Exception e) {
                    LogManager.logError("Error in Making reservation. Asset name, and user name: " + asset.getName() + user.getFullname() + "Exception message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Asset> getAvailableAssets() {
        return availableAssets;
    }

    public List<Asset> getFilteredAssets() {
        return filteredAssets;
    }

    public List<Tag> getFilterTags() {
        return filterTags;
    }

    public List<Tag> getSelectedFilterTags() {
        return selectedFilterTags;
    }

    public void setSelectedFilterTags(List<Tag> selectedFilterTags) {
        this.selectedFilterTags = selectedFilterTags;

        filterAssets();
    }
}
