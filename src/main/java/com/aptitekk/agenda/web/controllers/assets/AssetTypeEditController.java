/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.assets;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.web.controllers.AuthenticationController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class AssetTypeEditController implements Serializable {

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private AssetService assetService;

    private List<AssetType> assetTypes;
    private AssetType selectedAssetType;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableAssetTypeName;

    @Inject
    private TagController tagController;

    @Inject
    private AuthenticationController authenticationController;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }

        refreshAssetTypes();
        resetSettings();
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.ASSETS);
    }

    public boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.ASSET_TYPES_MODIFY_ALL);
    }

    void refreshAssetTypes() {
        assetTypes = new ArrayList<>();

        if (authenticationController.userHasPermission(Permission.Descriptor.ASSET_TYPES_MODIFY_ALL) || authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            assetTypes = assetTypeService.getAll();
        }

        if (!authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
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
                            if (!assetTypes.contains(asset.getAssetType()))
                                assetTypes.add(asset.getAssetType());
                        }
                    }
                }
            } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_OWN)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    //Add all Asset Types for the Assets that the User is allowed to edit.
                    //noinspection Duplicates
                    for (Asset asset : userGroup.getAssets()) {
                        if (!assetTypes.contains(asset.getAssetType()))
                            assetTypes.add(asset.getAssetType());
                    }
                }
            }
        }

        //Refresh selected AssetType
        if (selectedAssetType != null)
            selectedAssetType = assetTypeService.get(selectedAssetType.getId());
    }

    public void updateSettings() {
        if (!hasModifyPermission())
            return;

        if (selectedAssetType != null && editableAssetTypeName != null) {

            //Check if another Asset Type has the same name.
            AssetType assetType = assetTypeService.findByName(editableAssetTypeName);
            if (assetType != null && !assetType.equals(selectedAssetType))
                FacesContext.getCurrentInstance().addMessage("assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Asset Type with that name already exists!"));

            if (FacesContext.getCurrentInstance().getMessageList("assetTypeEditForm").isEmpty()) {
                try {
                    selectedAssetType.setName(editableAssetTypeName);

                    //Persist name change
                    assetTypeService.merge(selectedAssetType);

                    //Update tags
                    tagController.updateAssetTags(selectedAssetType);

                    //Refresh AssetType
                    setSelectedAssetType(assetTypeService.get(selectedAssetType.getId()));
                    refreshAssetTypes();
                    FacesContext.getCurrentInstance().addMessage("assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Type Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage("assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating Asset Type: " + e.getMessage()));
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedAssetType != null) {
            editableAssetTypeName = selectedAssetType.getName();

            List<Tag> tags = selectedAssetType.getTags();
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : tags)
                tagNames.add(tag.getName());
            tagController.setSelectedAssetTypeTagNames(tagNames);
        } else {
            editableAssetTypeName = "";
            tagController.setSelectedAssetTypeTagNames(null);
        }
    }

    public void deleteSelectedAssetType() {
        if (!hasModifyPermission())
            return;

        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetTypeService.get(getSelectedAssetType().getId()) != null) {
                context.addMessage("assetTypeEditForm", new FacesMessage("Successful", "Asset Type Deleted!"));
                assetTypeService.delete(getSelectedAssetType().getId());
                setSelectedAssetType(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("assetTypeEditForm", new FacesMessage("Failure", "Error While Deleting Asset Type!"));
        }

        refreshAssetTypes();
    }

    public void addNewAssetToSelectedType() {
        if (selectedAssetType != null) {
            try {
                Asset asset = new Asset("New Asset");
                asset.setAssetType(selectedAssetType);
                assetService.insert(asset);

                setSelectedAssetType(assetTypeService.get(getSelectedAssetType().getId()));

                FacesContext.getCurrentInstance().addMessage("assetSelectForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Added!"));
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }

            refreshAssetTypes();
        }
    }

    public List<AssetType> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AssetType> assetTypes) {
        this.assetTypes = assetTypes;
    }

    public AssetType getSelectedAssetType() {
        return selectedAssetType;
    }

    public void setSelectedAssetType(AssetType selectedAssetType) {
        this.selectedAssetType = selectedAssetType;
        resetSettings();
    }

    public String getEditableAssetTypeName() {
        return editableAssetTypeName;
    }

    public void setEditableAssetTypeName(String editableAssetTypeName) {
        this.editableAssetTypeName = editableAssetTypeName;
    }
}
