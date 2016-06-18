package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.ReservationService;
import com.aptitekk.agenda.core.entity.*;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "AvailableAssetsController")
@ViewScoped
public class AvailableAssetsController {

    @Inject
    private ReservationService reservationService;

    private List<Asset> availableAssets;
    private List<Asset> filteredAssets;

    private List<Tag> filterTags;
    private List<Tag> selectedFilterTags;

    public void searchForAssets(AssetType assetType, SegmentedTimeRange segmentedTimeRange) {
        this.availableAssets = reservationService.findAvailableAssets(assetType, segmentedTimeRange, 0f);

        filterTags = assetType.getTags();
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
