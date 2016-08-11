/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assets;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.entities.services.AssetCategoryService;
import com.aptitekk.agenda.core.entities.services.AssetService;
import com.aptitekk.agenda.core.entities.services.FileService;
import com.aptitekk.agenda.core.util.LogManager;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;
import com.aptitekk.agenda.web.controllers.AuthenticationController;
import com.aptitekk.agenda.web.controllers.TimeSelectionController;
import com.aptitekk.agenda.web.controllers.settings.assetCategories.TagController;
import com.aptitekk.agenda.web.controllers.settings.groups.GroupTreeController;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class NewAssetController implements Serializable {

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
    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String assetName;

    private boolean assetApprovalRequired;

    private UserGroup assetOwnerGroup;
    private TreeNode tree;

    private Part image;
    private String fileName;

    private AssetCategory assetCategory;
    private Asset asset = new Asset("New Asset");

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
                asset.setAssetCategory(assetCategory);
                if (assetOwnerGroup == null) {
                    FacesContext.getCurrentInstance().addMessage("newAssetModalForm:ownerGroup", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This is required. Please select an owner group for this asset."));
                    update = false;
                }
                if (image != null) {
                    try {
                        File file = fileService.createFileFromImagePart(image);
                        asset.setImage(file);
                    } catch (IOException e) {
                        LogManager.logError("Attempt to upload image for " + asset.getName() + " failed due to IOException.");
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
                    LogManager.logInfo("Asset created, Asset Id and Name: " + asset.getId() + ", " + asset.getName());
                    // System.out.println("#######asset.getAssetCategory id, and assetCategory id: " + asset.getAssetCategory().getId() + assetCategory.getId());
                    FacesContext.getCurrentInstance().addMessage("assetsForm_" + asset.getAssetCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Added!"));
                    //resetSettings();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.logError("Error persisting asset: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage("assetsForm_" + asset.getAssetCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
        }
    }

    public void onOwnerSelected(NodeSelectEvent event) {
        if (event.getTreeNode() != null && event.getTreeNode().getData() != null && event.getTreeNode().getData() instanceof UserGroup)
            this.assetOwnerGroup = (UserGroup) event.getTreeNode().getData();
    }

    /**
     * Called upon an image file being chosen by the user.
     */
    public void onFileChosen() {
        if (image != null)
            this.fileName = image.getSubmittedFileName();
    }

    public void resetSettings() {
        assetName = null;
        tagController.setSelectedAsset(null);
        tagController.setSelectedAssetTags(null);
        assetApprovalRequired = false;
        SegmentedTimeRange availabilityRange = new SegmentedTimeRange(null, null, null);
        timeSelectionController.setSelectedStartTime(availabilityRange.getStartTime());
        timeSelectionController.setSelectedEndTime(availabilityRange.getEndTime());
        this.assetOwnerGroup = asset.getOwner();
        groupTreeController.invalidateTrees();
        System.out.println("made it here1");
        if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            tree = groupTreeController.getTree(asset.getOwner(), null, false, false);
        } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_HIERARCHY)) {
            tree = groupTreeController.getFilteredTree(asset.getOwner(), authenticationController.getAuthenticatedUser().getUserGroups(), true);
        } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_OWN)) {
            tree = groupTreeController.getFilteredTree(asset.getOwner(), authenticationController.getAuthenticatedUser().getUserGroups(), false);
        } else {
            tree = null;
        }
        this.fileName = null;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public boolean isAssetApprovalRequired() {
        return assetApprovalRequired;
    }

    public void setAssetApprovalRequired(boolean assetApprovalRequired) {
        this.assetApprovalRequired = assetApprovalRequired;
    }

    public UserGroup getAssetOwnerGroup() {
        return assetOwnerGroup;
    }

    public void setAssetOwnerGroup(UserGroup assetOwnerGroup) {
        this.assetOwnerGroup = assetOwnerGroup;
    }

    public TreeNode getTree() {
        return tree;
    }

    public void setTree(TreeNode tree) {
        this.tree = tree;
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public AssetCategory getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
        this.assetCategory = assetCategory;
        resetSettings();
    }
}
