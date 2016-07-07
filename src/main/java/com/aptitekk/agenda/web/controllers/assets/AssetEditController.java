/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.assets;

import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.time.SegmentedTimeRange;
import com.aptitekk.agenda.web.controllers.TimeSelectionController;
import org.apache.commons.io.IOUtils;
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
import java.io.InputStream;
import java.io.Serializable;

@Named
@ViewScoped
public class AssetEditController implements Serializable {

    @Inject
    private AssetService assetService;

    private Asset selectedAsset;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableAssetName;

    private boolean editableAssetApproval;
    private TreeNode editableAssetOwnerGroup;
    private UserGroup currentAssetOwnerGroup;
    private Part file;
    private String fileName;

    @Inject
    private AssetTypeEditController assetTypeEditController;

    @Inject
    private TimeSelectionController timeSelectionController;

    @Inject
    private TagController tagController;

    /**
     * Uploads the selected image file.
     *
     * @return True if upload was successful, false otherwise.
     */
    private boolean uploadPhoto() {
        if (file != null && selectedAsset != null) {
            if (validateFile()) {
                try (InputStream inputStream = file.getInputStream()) {
                    byte[] photoBytes = IOUtils.toByteArray(inputStream);
                    selectedAsset.setPhoto(photoBytes);
                    LogManager.logInfo("An image was added to the asset " + selectedAsset.getName());
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Validates the selected image file.
     *
     * @return True if valid, false otherwise.
     */
    private boolean validateFile() {
        if (file.getSize() < 5000000) { //5MB
            if (file.getContentType().equalsIgnoreCase("image/jpeg") || file.getContentType().equalsIgnoreCase("image/png")) {
                return true;
            } else {
                FacesContext.getCurrentInstance().addMessage("assetEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Only JPEG and PNG files are accepted."));
                return false;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("assetEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This file is too large. Please select a file under 5MB in size."));
            return false;
        }
    }

    /**
     * Called upon an image file being chosen by the user.
     */
    public void onFileChosen() {
        if (file != null)
            this.fileName = file.getSubmittedFileName();
        else
            LogManager.logError("The chosen image file for the asset being edited is null!");
    }

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

            //Upload file if it exists.
            if (file != null && !uploadPhoto())
                update = false;

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
        }
        this.fileName = null;
    }

    public void deleteSelectedAsset() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetService.get(selectedAsset.getId()) != null) {
                context.addMessage("assetSelectForm", new FacesMessage("Successful", "Asset Deleted!"));
                assetService.delete(selectedAsset.getId());
            } else {
                throw new Exception("Asset not found!");
            }
        } catch (Exception e) {
            context.addMessage("assetSelectForm", new FacesMessage("Failure", "Error While Deleting Asset!"));
            e.printStackTrace();
        }

        selectedAsset = null;
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

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
