/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.assets;

import com.aptitekk.aptibook.core.entities.*;
import com.aptitekk.aptibook.core.entities.services.AssetCategoryService;
import com.aptitekk.aptibook.core.entities.services.AssetService;
import com.aptitekk.aptibook.core.entities.services.FileService;
import com.aptitekk.aptibook.core.entities.services.UserGroupService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.controllers.settings.assetCategories.TagController;
import com.aptitekk.aptibook.web.controllers.settings.groups.GroupTreeController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class AssetSettingsController extends AssetFieldSupplier implements Serializable {

    @Inject
    private AssetService assetService;

    @Inject
    private FileService fileService;

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private GroupTreeController groupTreeController;

    @Inject
    private HelpController helpController;

    @Inject
    private UserGroupService userGroupService;

    private List<AssetCategory> assetCategoryList;
    private HashMap<AssetCategory, List<Asset>> assetMap;
    private Asset selectedAsset;

    private boolean userGroupsEmpty;

    @Inject
    private TagController tagController;

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.ASSETS);
    }

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }

        refreshAssets();

        this.userGroupsEmpty = userGroupService.getAll().size() <= 1;

        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_ASSETS);
    }

    void refreshAssets() {
        assetCategoryList = new ArrayList<>();
        assetMap = new HashMap<>();

        if (authenticationController.userHasPermission(Permission.Descriptor.ASSET_CATEGORIES_MODIFY_ALL) || authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            assetCategoryList = assetCategoryService.getAll();
        }

        if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            for (AssetCategory assetCategory : assetCategoryList) {
                assetMap.putIfAbsent(assetCategory, new ArrayList<>(assetCategory.getAssets()));
            }
        } else {
            if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_HIERARCHY)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    Queue<UserGroup> queue = new LinkedList<>();
                    queue.add(userGroup);

                    //Traverse the tree downward using a queue.
                    UserGroup queueGroup;
                    while ((queueGroup = queue.poll()) != null) {
                        queue.addAll(queueGroup.getChildren());

                        //Add all AssetCategories for the Assets that the User is allowed to edit.
                        //noinspection Duplicates
                        for (Asset asset : queueGroup.getAssets()) {
                            if (!assetCategoryList.contains(asset.getAssetCategory()))
                                assetCategoryList.add(asset.getAssetCategory());
                            assetMap.putIfAbsent(asset.getAssetCategory(), new ArrayList<>());
                            assetMap.get(asset.getAssetCategory()).add(asset);
                        }
                    }
                }
            } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_OWN)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    //Add all AssetCategories for the Assets that the User is allowed to edit.
                    //noinspection Duplicates
                    for (Asset asset : userGroup.getAssets()) {
                        if (!assetCategoryList.contains(asset.getAssetCategory()))
                            assetCategoryList.add(asset.getAssetCategory());
                        assetMap.putIfAbsent(asset.getAssetCategory(), new ArrayList<>());
                        assetMap.get(asset.getAssetCategory()).add(asset);
                    }
                }
            }
        }
    }

    public void updateSettings() {
        if (selectedAsset != null) {
            boolean update = true;

            if (assetOwnerGroup == null) {
                FacesContext.getCurrentInstance().addMessage("editAssetModalForm:ownerGroup", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This is required. Please select an owner group for this asset."));
                update = false;
            }

            if (image != null) {
                try {
                    File file = fileService.createFileFromImagePart(image);
                    selectedAsset.setImage(file);
                } catch (IOException e) {
                    LogManager.logError("Attempt to upload image for " + selectedAsset.getName() + " failed due to IOException.");
                    FacesContext.getCurrentInstance().addMessage("editAssetModalForm:imageUpload", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The image upload failed. Please try again or try another file."));
                    update = false;
                    e.printStackTrace();
                }
            }

            if (update) {
                selectedAsset.setName(assetName);
                tagController.updateAssetTags(selectedAsset);
                selectedAsset.setNeedsApproval(assetApprovalRequired);

                try {
                    if (assetOwnerGroup != null)
                        selectedAsset.setOwner(assetOwnerGroup);

                    setSelectedAsset(assetService.merge(selectedAsset));
                    LogManager.logInfo("Asset updated, Asset Id and Name: " + selectedAsset.getId() + ", " + selectedAsset.getName());
                    FacesContext.getCurrentInstance().addMessage("assetsForm_" + selectedAsset.getAssetCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset '" + selectedAsset.getName() + "' Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.logError("Error updating asset settings " + e.getMessage());
                    FacesContext.getCurrentInstance().addMessage("assetsForm_" + selectedAsset.getAssetCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedAsset != null) {
            assetName = selectedAsset.getName();
            tagController.setSelectedAsset(selectedAsset);
            tagController.setSelectedAssetTags(selectedAsset.getTags());
            assetApprovalRequired = selectedAsset.getNeedsApproval();
            this.assetOwnerGroup = selectedAsset.getOwner();

            groupTreeController.invalidateTrees();
            if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
                tree = groupTreeController.getTree(selectedAsset.getOwner(), null, false, false);
            } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_HIERARCHY)) {
                tree = groupTreeController.getFilteredTree(selectedAsset.getOwner(), authenticationController.getAuthenticatedUser().getUserGroups(), true);
            } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_OWN)) {
                tree = groupTreeController.getFilteredTree(selectedAsset.getOwner(), authenticationController.getAuthenticatedUser().getUserGroups(), false);
            } else
                tree = null;
        }
        this.fileName = null;
    }
    public void deleteSelectedAsset() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetService.get(selectedAsset.getId()) != null) {
                context.addMessage("assetsForm_" + selectedAsset.getAssetCategory().getId(), new FacesMessage("Successful", "Asset Deleted!"));
                LogManager.logInfo("Asset deleted, Asset Id and Name: " + selectedAsset.getId() + ", " + selectedAsset.getName());
                assetService.delete(selectedAsset.getId());
                refreshAssets();
            } else {
                throw new Exception("Asset not found!");
            }
        } catch (Exception e) {
            context.addMessage("assetsForm_" + selectedAsset.getAssetCategory().getId(), new FacesMessage("Failure", "Error While Deleting Asset!"));
            LogManager.logError("Error while Deleting Asset: " + e.getMessage());
            e.printStackTrace();
        }

        selectedAsset = null;
    }

    public List<AssetCategory> getAssetCategoryList() {
        return assetCategoryList;
    }

    public HashMap<AssetCategory, List<Asset>> getAssetMap() {
        return assetMap;
    }

    public Asset getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(Asset selectedAsset) {
        this.selectedAsset = selectedAsset;
        resetSettings();
    }

    public boolean isUserGroupsEmpty() {
        return userGroupsEmpty;
    }
}
