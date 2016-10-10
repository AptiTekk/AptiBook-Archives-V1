/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.assetCategories;

import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.domain.services.ReservationFieldService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;

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
public class EditAssetCategoryController implements Serializable {

    @Inject
    private TagController tagController;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private ReservationFieldService reservationFieldService;

    private List<AssetCategory> assetCategories;
    private AssetCategory selectedAssetCategory;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableAssetCategoryName;

    private List<ReservationField> reservationFields;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }

        refreshAssetCategories();
        resetSettings();

        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_ASSET_CATEGORIES);
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
        resetSettings();
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
                    //Update tags
                    tagController.updateAssetTags(selectedAssetCategory);

                    //Refresh AssetCategory
                    setSelectedAssetCategory(assetCategoryService.get(selectedAssetCategory.getId()));
                    refreshAssetCategories();
                    FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Category Updated"));
                } catch (Exception e) {
                    LogManager.logException(getClass(), "Error updating Asset Category settings", e);
                    FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedAssetCategory != null) {
            editableAssetCategoryName = selectedAssetCategory.getName();
            reservationFields = selectedAssetCategory.getReservationFields();

            List<Tag> tags = selectedAssetCategory.getTags();
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : tags)
                tagNames.add(tag.getName());
            tagController.setSelectedAssetCategoryTagNames(tagNames);
        } else {
            editableAssetCategoryName = "";
            reservationFields = null;
            tagController.setSelectedAssetCategoryTagNames(null);
        }
    }

    public void deleteSelectedAssetCategory() {
        if (!hasModifyPermission())
            return;

        try {
            if (assetCategoryService.get(getSelectedAssetCategory().getId()) != null) {
                FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Category Deleted!"));
                assetCategoryService.delete(getSelectedAssetCategory().getId());
                setSelectedAssetCategory(null);
            } else {
                LogManager.logError(getClass(), "Error while deleting Asset Category: Asset Category not found.");
            }
        } catch (Exception e) {
            LogManager.logException(getClass(), "Error while deleting Asset Category", e);
            FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
        }

        refreshAssetCategories();
    }

    public void addNewReservationField() {
        if (selectedAssetCategory != null) {
            ReservationField reservationField = new ReservationField();
            reservationField.setAssetCategory(selectedAssetCategory);
            reservationField.setTitle("New Reservation Field");
            reservationField.setMultiLine(false);

            try {
                reservationFieldService.insert(reservationField);
                refreshAssetCategories();

                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "New Reservation Field Added."));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
                LogManager.logException(getClass(), "Could not add new Reservation Field", e);
            }
        }
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

    public List<ReservationField> getReservationFields() {
        return reservationFields;
    }
}
