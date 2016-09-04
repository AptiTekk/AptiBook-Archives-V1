/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.results;

import com.aptitekk.aptibook.core.domain.entities.Asset;
import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.Tag;
import com.aptitekk.aptibook.core.domain.services.AssetService;
import com.aptitekk.aptibook.core.domain.services.NotificationService;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.core.time.SegmentedTimeRange;
import com.aptitekk.aptibook.web.controllers.frontPage.reserve.RequestReservationViewController;

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
    private RequestReservationViewController requestReservationViewController;

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

    /**
     * Loads the RequestReservationViewController with the proper details.
     * The page should be reloaded with ajax after this method is called.
     *
     * @param asset              The Asset that the user wants to reserve.
     * @param segmentedTimeRange The Segmented Time Range of the requested reservation.
     */
    public void onMakeReservationFired(Asset asset, SegmentedTimeRange segmentedTimeRange) {
        requestReservationViewController.setAsset(asset);
        requestReservationViewController.setSegmentedTimeRange(segmentedTimeRange);
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
