/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.assets;

import com.aptitekk.aptibook.core.domain.entities.Asset;
import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.File;
import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.services.AssetService;
import com.aptitekk.aptibook.core.domain.services.FileService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.settings.assetCategories.TagController;
import com.aptitekk.aptibook.web.controllers.settings.groups.GroupTreeController;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;

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
    private TagController tagController;

    @Inject
    private EditAssetController editAssetController;

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
                    FacesContext.getCurrentInstance().addMessage("newAssetModalForm:ownerGroup", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Please select an Owner Group for this Asset."));
                    update = false;
                }
                if (image != null) {
                    try {
                        File file = fileService.createFileFromImagePart(image);
                        asset.setImage(file);
                    } catch (IOException e) {
                        LogManager.logException(getClass(), "Image Upload Failed", e);
                        FacesContext.getCurrentInstance().addMessage("newAssetModalForm:imageUpload", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The image upload failed. Please try again or try another file."));
                    }
                }
                if (update) {
                    asset.setName(assetName);
                    asset.setNeedsApproval(assetApprovalRequired);
                    asset.setOwner(assetOwnerGroup);
                    asset.setAssetCategory(assetCategory);
                    assetService.insert(asset);
                    tagController.updateAssetTags(asset);
                    editAssetController.refreshAssets();
                    LogManager.logInfo(getClass(), "Asset created, Asset Id and Name: " + asset.getId() + ", " + asset.getName());
                    FacesContext.getCurrentInstance().addMessage("assetsForm_" + asset.getAssetCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Added!"));
                }
            }
        } catch (Exception e) {
            LogManager.logException(getClass(), "Could not persist new asset", e);
            FacesContext.getCurrentInstance().addMessage("assetsForm_" + assetCategory.getId(), CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
        }
    }

    private void resetSettings() {
        assetName = null;
        tagController.setAvailableTags(assetCategory != null ? assetCategory.getTags() : null);
        tagController.setSelectedAssetTags(null);
        assetApprovalRequired = false;
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
