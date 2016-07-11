/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assets;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetType;
import com.aptitekk.agenda.core.entities.Permission;
import com.aptitekk.agenda.core.entities.UserGroup;
import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;
import com.aptitekk.agenda.web.controllers.AuthenticationController;
import com.aptitekk.agenda.web.controllers.TimeSelectionController;
import com.aptitekk.agenda.web.controllers.settings.assetTypes.TagController;
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
import java.util.*;

@Named
@ViewScoped
public class AssetSettingsController implements Serializable {

    @Inject
    private AssetService assetService;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private GroupTreeController groupTreeController;

    private List<AssetType> assetTypeList;
    private HashMap<AssetType, List<Asset>> assetMap;
    private Asset selectedAsset;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String assetName;

    private boolean assetApprovalRequired;

    private UserGroup assetOwnerGroup;
    private TreeNode tree;

    private Part file;
    private String fileName;

    @Inject
    private TimeSelectionController timeSelectionController;

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
    }

    private void refreshAssets() {
        assetTypeList = new ArrayList<>();
        assetMap = new HashMap<>();

        if (authenticationController.userHasPermission(Permission.Descriptor.ASSET_TYPES_MODIFY_ALL) || authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            assetTypeList = assetTypeService.getAll();
        }

        if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            for (AssetType assetType : assetTypeList) {
                assetMap.putIfAbsent(assetType, new ArrayList<>(assetType.getAssets()));
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

                        //Add all Asset Types for the Assets that the User is allowed to edit.
                        //noinspection Duplicates
                        for (Asset asset : queueGroup.getAssets()) {
                            if (!assetTypeList.contains(asset.getAssetType()))
                                assetTypeList.add(asset.getAssetType());
                            assetMap.putIfAbsent(asset.getAssetType(), new ArrayList<>());
                            assetMap.get(asset.getAssetType()).add(asset);
                        }
                    }
                }
            } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_OWN)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    //Add all Asset Types for the Assets that the User is allowed to edit.
                    //noinspection Duplicates
                    for (Asset asset : userGroup.getAssets()) {
                        if (!assetTypeList.contains(asset.getAssetType()))
                            assetTypeList.add(asset.getAssetType());
                        assetMap.putIfAbsent(asset.getAssetType(), new ArrayList<>());
                        assetMap.get(asset.getAssetType()).add(asset);
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

            if (file != null) {
                try {
                    selectedAsset.uploadPhoto(file);
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

                SegmentedTimeRange availabilityRange = timeSelectionController.getSegmentedTimeRange();
                selectedAsset.setAvailabilityStart(availabilityRange.getStartTime());
                selectedAsset.setAvailabilityEnd(availabilityRange.getEndTime());

                try {
                    if (assetOwnerGroup != null)
                        selectedAsset.setOwner(assetOwnerGroup);

                    setSelectedAsset(assetService.merge(selectedAsset));
                    FacesContext.getCurrentInstance().addMessage("assetsForm_" + selectedAsset.getAssetType().getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset '" + selectedAsset.getName() + "' Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage("assetsForm_" + selectedAsset.getAssetType().getId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
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
            SegmentedTimeRange availabilityRange = new SegmentedTimeRange(null, selectedAsset.getAvailabilityStart(), selectedAsset.getAvailabilityEnd());
            timeSelectionController.setSelectedStartTime(availabilityRange.getStartTime());
            timeSelectionController.setSelectedEndTime(availabilityRange.getEndTime());
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

    public void addNewAsset(AssetType assetType) {
        if (assetType != null) {
            try {
                Asset asset = new Asset("New Asset");
                asset.setAssetType(assetType);
                assetService.insert(asset);

                FacesContext.getCurrentInstance().addMessage("assetsForm_" + assetType.getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Added!"));

                refreshAssets();
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("assetsForm_" + assetType.getId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }
        }
    }

    public void deleteSelectedAsset() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetService.get(selectedAsset.getId()) != null) {
                context.addMessage("assetsForm_" + selectedAsset.getAssetType().getId(), new FacesMessage("Successful", "Asset Deleted!"));
                assetService.delete(selectedAsset.getId());

                refreshAssets();
            } else {
                throw new Exception("Asset not found!");
            }
        } catch (Exception e) {
            context.addMessage("assetsForm_" + selectedAsset.getAssetType().getId(), new FacesMessage("Failure", "Error While Deleting Asset!"));
            e.printStackTrace();
        }

        selectedAsset = null;
    }

    public List<AssetType> getAssetTypeList() {
        return assetTypeList;
    }

    public HashMap<AssetType, List<Asset>> getAssetMap() {
        return assetMap;
    }

    public Asset getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(Asset selectedAsset) {
        this.selectedAsset = selectedAsset;
        resetSettings();
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

    public void onOwnerSelected(NodeSelectEvent event) {
        if (event.getTreeNode() != null && event.getTreeNode().getData() != null && event.getTreeNode().getData() instanceof UserGroup)
            this.assetOwnerGroup = (UserGroup) event.getTreeNode().getData();
    }

    public UserGroup getAssetOwnerGroup() {
        return assetOwnerGroup;
    }

    public TreeNode getTree() {
        return tree;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        if (file == null)
            return;
        this.file = file;
    }

    /**
     * Called upon an image file being chosen by the user.
     */
    public void onFileChosen() {
        if (file != null)
            this.fileName = file.getSubmittedFileName();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}