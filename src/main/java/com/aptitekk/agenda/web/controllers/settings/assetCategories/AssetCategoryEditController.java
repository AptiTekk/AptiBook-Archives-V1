/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assetCategories;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.entities.services.AssetCategoryService;
import com.aptitekk.agenda.core.entities.services.AssetService;
import com.aptitekk.agenda.core.util.LogManager;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Named
@ViewScoped
public class AssetCategoryEditController implements Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private AssetService assetService;

    private List<AssetCategory> assetCategories;
    private AssetCategory selectedAssetCategory;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableAssetCategoryName;

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

        refreshAssetCategories();
        resetSettings();
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.ASSETS);
    }

    public boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.ASSET_CATEGORIES_MODIFY_ALL);
    }

    void refreshAssetCategories() {
        assetCategories = new ArrayList<>();

        if (authenticationController.userHasPermission(Permission.Descriptor.ASSET_CATEGORIES_MODIFY_ALL) || authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_ALL)) {
            assetCategories = assetCategoryService.getAll();
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

                        //Add all Asset Categories for the Assets that the User is allowed to edit.
                        //noinspection Duplicates
                        for (Asset asset : queueGroup.getAssets()) {
                            if (!assetCategories.contains(asset.getAssetCategory()))
                                assetCategories.add(asset.getAssetCategory());
                        }
                    }
                }
            } else if (authenticationController.userHasPermission(Permission.Descriptor.ASSETS_MODIFY_OWN)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    //Add all Asset Categories for the Assets that the User is allowed to edit.
                    //noinspection Duplicates
                    for (Asset asset : userGroup.getAssets()) {
                        if (!assetCategories.contains(asset.getAssetCategory()))
                            assetCategories.add(asset.getAssetCategory());
                    }
                }
            }
        }

        //Refresh selected AssetCategory
        if (selectedAssetCategory != null)
            selectedAssetCategory = assetCategoryService.get(selectedAssetCategory.getId());
    }

    public void updateSettings() {
        if (!hasModifyPermission())
            return;

        if (selectedAssetCategory != null && editableAssetCategoryName != null) {

            //Check if another Asset Category has the same name.
            AssetCategory assetCategory = assetCategoryService.findByName(editableAssetCategoryName);
            if (assetCategory != null && !assetCategory.equals(selectedAssetCategory))
                FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Asset Category with that name already exists!"));

            if (FacesContext.getCurrentInstance().getMessageList("assetCategoryEditForm").isEmpty()) {
                try {
                    selectedAssetCategory.setName(editableAssetCategoryName);

                    //Persist name change
                    assetCategoryService.merge(selectedAssetCategory);
                    LogManager.logInfo("Asset Category updated, Asset Category Id and Name: " + selectedAssetCategory.getId() + ", " + selectedAssetCategory.getName());
                    //Update tags
                    tagController.updateAssetTags(selectedAssetCategory);

                    //Refresh AssetCategory
                    setSelectedAssetCategory(assetCategoryService.get(selectedAssetCategory.getId()));
                    refreshAssetCategories();
                    FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Category Updated"));
                } catch (Exception e) {
                    LogManager.logError("Error updating Asset Category settings for " + selectedAssetCategory.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating Asset Category: " + e.getMessage()));
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedAssetCategory != null) {
            editableAssetCategoryName = selectedAssetCategory.getName();

            List<Tag> tags = selectedAssetCategory.getTags();
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : tags)
                tagNames.add(tag.getName());
            tagController.setSelectedAssetCategoryTagNames(tagNames);
        } else {
            editableAssetCategoryName = "";
            tagController.setSelectedAssetCategoryTagNames(null);
        }
    }

    public void deleteSelectedAssetCategory() {
        if (!hasModifyPermission())
            return;

        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetCategoryService.get(getSelectedAssetCategory().getId()) != null) {
                context.addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Category Deleted!"));
                assetCategoryService.delete(getSelectedAssetCategory().getId());
                LogManager.logInfo("Asset Category deleted, Asset Category Id and Name: " + selectedAssetCategory.getId() + ", " + selectedAssetCategory.getName());
                setSelectedAssetCategory(null);
            } else {
                LogManager.logError("Error while deleting Asset Category " + selectedAssetCategory.getName() + ": Asset Category not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.logError("Error while deleting Asset Category " + selectedAssetCategory.getName() + ": " + e.getMessage());
            context.addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Deleting Asset Category!"));
        }

        refreshAssetCategories();
    }

    public List<AssetCategory> getAssetCategories() {
        return assetCategories;
    }

    public void setAssetCategories(List<AssetCategory> assetCategories) {
        this.assetCategories = assetCategories;
    }

    public AssetCategory getSelectedAssetCategory() {
        return selectedAssetCategory;
    }

    public void setSelectedAssetCategory(AssetCategory selectedAssetCategory) {
        this.selectedAssetCategory = selectedAssetCategory;
        resetSettings();
    }

    public String getEditableAssetCategoryName() {
        return editableAssetCategoryName;
    }

    public void setEditableAssetCategoryName(String editableAssetCategoryName) {
        this.editableAssetCategoryName = editableAssetCategoryName;
    }
}
