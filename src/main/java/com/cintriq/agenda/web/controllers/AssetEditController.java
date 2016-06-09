package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.AssetService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.UserGroup;
import com.cintriq.agenda.core.utilities.TimeRange;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@ManagedBean(name = "AssetEditController")
@ViewScoped
public class AssetEditController {

    @Inject
    private AssetService assetService;

    private Asset selectedAsset;

    private String editableAssetName;
    private boolean editableAssetApproval;
    private TreeNode editableAssetOwnerGroup;
    private UserGroup currentAssetOwnerGroup;

    @ManagedProperty(value = "#{AssetTypeEditController}")
    private AssetTypeEditController assetTypeEditController;

    public void setAssetTypeEditController(AssetTypeEditController assetTypeEditController) {
        this.assetTypeEditController = assetTypeEditController;
    }

    @ManagedProperty(value = "#{TimeSelectionController}")
    private TimeSelectionController timeSelectionController;

    public void setTimeSelectionController(TimeSelectionController timeSelectionController) {
        this.timeSelectionController = timeSelectionController;
    }

    @ManagedProperty(value = "#{TagController}")
    private TagController tagController;

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    public void updateSettings() {
        if (selectedAsset != null) {
            Asset asset = assetService.findByName(editableAssetName);
            if (asset != null && !asset.equals(selectedAsset))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Asset with that name already exists!"));
            else if (editableAssetName.isEmpty())
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset's name cannot be empty!"));
            else if (!editableAssetName.matches("[A-Za-z0-9 #]+"))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset's name may only contain A-Z, a-z, 0-9, #, and space!"));

            if (editableAssetOwnerGroup == null && currentAssetOwnerGroup == null)
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Please select an Owner Group for this Asset!"));

            if (FacesContext.getCurrentInstance().getMessageList(":assetTypeEditForm").isEmpty()) {
                selectedAsset.setName(editableAssetName);
                tagController.updateAssetTags(selectedAsset);
                selectedAsset.setNeedsApproval(editableAssetApproval);

                TimeRange availabilityRange = timeSelectionController.getTimeRange();
                selectedAsset.setAvailabilityStart(availabilityRange.getStartTime());
                selectedAsset.setAvailabilityEnd(availabilityRange.getEndTime());

                try {
                    if (editableAssetOwnerGroup != null)
                        selectedAsset.setOwner((UserGroup) editableAssetOwnerGroup.getData());

                    setSelectedAsset(assetService.merge(selectedAsset));
                    this.assetTypeEditController.refreshAssetTypes();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
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
            TimeRange availabilityRange = new TimeRange(selectedAsset.getAvailabilityStart(), selectedAsset.getAvailabilityEnd());
            timeSelectionController.setSelectedStartTimeString(availabilityRange.getStartTimeFormatted(TimeRange.FORMAT_TIME_ONLY));
            timeSelectionController.setSelectedEndTimeString(availabilityRange.getEndTimeFormatted(TimeRange.FORMAT_TIME_ONLY));

            this.currentAssetOwnerGroup = selectedAsset.getOwner();
        } else {
            setEditableAssetName("");
            tagController.setSelectedAsset(null);
            tagController.setSelectedAssetTags(null);
            setEditableAssetApproval(false);
            timeSelectionController.setSelectedStartTimeString(null);
            timeSelectionController.setSelectedEndTimeString(null);
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
                context.addMessage(":assetTypeEditForm", new FacesMessage("Successful", "Asset Deleted!"));
                assetService.delete(selectedAsset.getId());
            } else {
                throw new Exception("Asset not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(":assetTypeEditForm", new FacesMessage("Failure", "Error While Deleting Asset!"));
        }

        assetTypeEditController.refreshAssetTypes();
    }

    public Asset getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(Asset selectedAsset) {
        this.selectedAsset = selectedAsset;
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
}
