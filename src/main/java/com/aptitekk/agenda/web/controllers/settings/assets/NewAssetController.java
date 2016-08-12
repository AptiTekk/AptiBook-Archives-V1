/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assets;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.File;
import com.aptitekk.agenda.core.entities.Permission;
import com.aptitekk.agenda.core.entities.services.AssetService;
import com.aptitekk.agenda.core.entities.services.FileService;
import com.aptitekk.agenda.core.util.LogManager;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;
import com.aptitekk.agenda.web.controllers.AuthenticationController;
import com.aptitekk.agenda.web.controllers.TimeSelectionController;
import com.aptitekk.agenda.web.controllers.settings.assetCategories.TagController;
import com.aptitekk.agenda.web.controllers.settings.groups.GroupTreeController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class NewAssetController extends AssetFieldSupplier implements Serializable {

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private GroupTreeController groupTreeController;

    @Inject
    private AssetService assetService;

    @Inject
    private FileService fileService;

    @Inject
    private TimeSelectionController timeSelectionController;

    @Inject
    private TagController tagController;

    @Inject
    private AssetSettingsController assetSettingsController;

    private AssetCategory assetCategory;

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.ASSETS);
    }

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }
        resetSettings();
    }

    public void addNewAsset() {
        boolean update = true;
        try {
            if (assetCategory != null) {
                Asset asset = new Asset();

                if (assetOwnerGroup == null) {
                    FacesContext.getCurrentInstance().addMessage("newAssetModalForm:ownerGroup", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This is required. Please select an owner group for this asset."));
                    update = false;
                }
                if (image != null) {
                    try {
                        File file = fileService.createFileFromImagePart(image);
                        asset.setImage(file);
                    } catch (IOException e) {
                        LogManager.logError("Attempt to upload image for new asset failed due to IOException.");
                        FacesContext.getCurrentInstance().addMessage("newAssetModalForm:imageUpload", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The image upload failed. Please try again or try another file."));
                        e.printStackTrace();
                    }
                }
                if (update) {
                    asset.setName(assetName);
                    asset.setNeedsApproval(assetApprovalRequired);
                    asset.setOwner(assetOwnerGroup);
                    asset.setAssetCategory(assetCategory);
                    SegmentedTimeRange availabilityRange = timeSelectionController.getSegmentedTimeRange();
                    asset.setAvailabilityStart(availabilityRange.getStartTime());
                    asset.setAvailabilityEnd(availabilityRange.getEndTime());
                    assetService.insert(asset);
                    assetSettingsController.refreshAssets();
                    LogManager.logInfo("Asset created, Asset Id and Name: " + asset.getId() + ", " + asset.getName());
                    FacesContext.getCurrentInstance().addMessage("assetsForm_" + asset.getAssetCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Added!"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.logError("Error persisting asset: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage("assetsForm_" + assetCategory.getId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
        }
    }

    private void resetSettings() {
        assetName = null;
        tagController.setSelectedAsset(null);
        tagController.setSelectedAssetTags(null);
        assetApprovalRequired = false;
        timeSelectionController.setSelectedStartTime(null);
        timeSelectionController.setSelectedEndTime(null);
        this.assetOwnerGroup = null;

        groupTreeController.invalidateTrees();
        if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            tree = groupTreeController.getTree(null, null, false, false);
        } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_HIERARCHY)) {
            tree = groupTreeController.getFilteredTree(null, authenticationController.getAuthenticatedUser().getUserGroups(), true);
        } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_OWN)) {
            tree = groupTreeController.getFilteredTree(null, authenticationController.getAuthenticatedUser().getUserGroups(), false);
        } else {
            tree = null;
        }
        this.fileName = null;
    }

    public AssetCategory getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
        this.assetCategory = assetCategory;
        resetSettings();
    }
}
