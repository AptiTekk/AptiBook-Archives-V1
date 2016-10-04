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
import com.aptitekk.aptibook.web.controllers.frontPage.reserve.RequestReservationViewController;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
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
    private boolean[] selectedFilterTags;


    public void searchForAssets(AssetCategory assetCategory, ZonedDateTime startTime, ZonedDateTime endTime) {
        this.availableAssets = reservationService.findAvailableAssets(assetCategory, startTime, endTime);

        filterTags = assetCategory.getTags();
        selectedFilterTags = new boolean[filterTags.size()];
        for (int i = 0; i < selectedFilterTags.length; i++) {
            selectedFilterTags[i] = false;
        }
        filterAssets();
    }

    /**
     * Updates the filteredAssets list with only those assets which contain all the selectedFilterTags
     */
    public void filterAssets() {
        filteredAssets = new ArrayList<>();
        List<Tag> selectedTags = new ArrayList<>();
        for (int i = 0; i < selectedFilterTags.length; i++) {
            if (selectedFilterTags[i]) {
                selectedTags.add(filterTags.get(i));
            }
        }
        for (Asset asset : availableAssets) {
            boolean skipAsset = false;

            //Make sure the Asset has all the selected filter Tags
            for (Tag tag : selectedTags) {
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
     * @param asset     The Asset that the user wants to reserve.
     * @param startTime The start time of the reservation.
     * @param endTime   The end time of the reservation.
     */
    public void onMakeReservationFired(Asset asset, ZonedDateTime startTime, ZonedDateTime endTime) {
        requestReservationViewController.setAsset(asset);
        requestReservationViewController.setStartTime(startTime);
        requestReservationViewController.setEndTime(endTime);
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

    public boolean[] getSelectedFilterTags() {
        return selectedFilterTags;
    }




}
