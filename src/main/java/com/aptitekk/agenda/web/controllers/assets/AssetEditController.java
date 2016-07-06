/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.assets;

import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;
import com.aptitekk.agenda.web.controllers.TimeSelectionController;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Named
@ViewScoped
public class AssetEditController implements Serializable {

    @Inject
    private AssetService assetService;

    private Asset selectedAsset;
    private int selectedAssetIndex = -1;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableAssetName;

    private boolean editableAssetApproval;
    private TreeNode editableAssetOwnerGroup;
    private UserGroup currentAssetOwnerGroup;

    @Inject
    private AssetTypeEditController assetTypeEditController;

    @Inject
    private TimeSelectionController timeSelectionController;

    @Inject
    private TagController tagController;

    @PostConstruct
    public void init() {
        if (assetTypeEditController != null)
            assetTypeEditController.setAssetEditController(this);
    }

    public void updateSettings() {
        if (selectedAsset != null) {
            boolean update = true;

            Asset asset = assetService.findByName(editableAssetName);
            if (asset != null && !asset.equals(selectedAsset)) {
                FacesContext.getCurrentInstance().addMessage("assetEditForm:name", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Asset with that name already exists!"));
                update = false;
            }

            if (editableAssetOwnerGroup == null && currentAssetOwnerGroup == null) {
                FacesContext.getCurrentInstance().addMessage("assetEditForm:ownerGroup", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This is required. Please select an owner group for this asset."));
                update = false;
            }

            if (update) {
                selectedAsset.setName(editableAssetName);
                tagController.updateAssetTags(selectedAsset);
                selectedAsset.setNeedsApproval(editableAssetApproval);

                SegmentedTimeRange availabilityRange = timeSelectionController.getSegmentedTimeRange();
                selectedAsset.setAvailabilityStart(availabilityRange.getStartTime());
                selectedAsset.setAvailabilityEnd(availabilityRange.getEndTime());

                try {
                    if (editableAssetOwnerGroup != null)
                        selectedAsset.setOwner((UserGroup) editableAssetOwnerGroup.getData());

                    setSelectedAsset(assetService.merge(selectedAsset));
                    this.assetTypeEditController.refreshAssetTypes();
                    FacesContext.getCurrentInstance().addMessage("assetEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset '" + selectedAsset.getName() + "' Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage("assetEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedAsset != null) {
            setEditableAssetName(selectedAsset.getName());
            tagController.setSelectedAsset(selectedAsset);
            tagController.setSelectedAssetTags(selectedAsset.getTags());
            setEditableAssetApproval(selectedAsset.getNeedsApproval());
            SegmentedTimeRange availabilityRange = new SegmentedTimeRange(null, selectedAsset.getAvailabilityStart(), selectedAsset.getAvailabilityEnd());
            timeSelectionController.setSelectedStartTime(availabilityRange.getStartTime());
            timeSelectionController.setSelectedEndTime(availabilityRange.getEndTime());

            this.currentAssetOwnerGroup = selectedAsset.getOwner();
        } else {
            setEditableAssetName("");
            tagController.setSelectedAsset(null);
            tagController.setSelectedAssetTags(null);
            setEditableAssetApproval(false);
            timeSelectionController.setSelectedStartTime(null);
            timeSelectionController.setSelectedEndTime(null);
            this.currentAssetOwnerGroup = null;
        }
    }

    public void onAssetTabChange(TabChangeEvent event) {
        if (event.getData() instanceof Asset)
            setSelectedAsset((Asset) event.getData());
    }

    public void deleteSelectedAsset() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetService.get(selectedAsset.getId()) != null) {
                context.addMessage("assetEditForm", new FacesMessage("Successful", "Asset Deleted!"));
                assetService.delete(selectedAsset.getId());
            } else {
                throw new Exception("Asset not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("assetEditForm", new FacesMessage("Failure", "Error While Deleting Asset!"));
        }

        assetTypeEditController.refreshAssetTypes();
    }

    public Asset getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(Asset selectedAsset) {
        this.selectedAsset = selectedAsset;

        if (assetTypeEditController != null && assetTypeEditController.getSelectedAssetType() != null)
            selectedAssetIndex = assetTypeEditController.getSelectedAssetType().getAssets().indexOf(selectedAsset);
        else
            selectedAssetIndex = -1;

        resetSettings();
    }

    public String getEditableAssetName() {
        return editableAssetName;
    }

    public void setEditableAssetName(String editableAssetName) {
        this.editableAssetName = editableAssetName;
    }

    public boolean isEditableAssetApproval() {
        return editableAssetApproval;
    }

    public void setEditableAssetApproval(boolean editableAssetApproval) {
        this.editableAssetApproval = editableAssetApproval;
    }

    public void onOwnerSelected(NodeSelectEvent event) {
        this.editableAssetOwnerGroup = event.getTreeNode();
    }

    public UserGroup getCurrentAssetOwnerGroup() {
        return currentAssetOwnerGroup;
    }

    public int getSelectedAssetIndex() {
        return selectedAssetIndex;
    }

    public void setSelectedAssetIndex(int selectedAssetIndex) {
        this.selectedAssetIndex = selectedAssetIndex;
    }
}
