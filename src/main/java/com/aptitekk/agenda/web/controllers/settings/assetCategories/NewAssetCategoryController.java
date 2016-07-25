/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assetCategories;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.services.AssetCategoryService;
import com.aptitekk.agenda.core.utilities.LogManager;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Named
@RequestScoped
public class NewAssetCategoryController implements Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String name;

    @Inject
    private TagController tagController;

    @Inject
    private AssetCategoryEditController assetCategoryEditController;

    public void addAssetCategory() throws Exception {
        AssetCategory assetCategory = new AssetCategory();
        assetCategory.setName(name);
        assetCategoryService.insert(assetCategory);
        LogManager.logInfo("New Asset Category persisted , Asset Category Id and Name: " + assetCategory.getId() + ", " + assetCategory.getName());

        if (tagController != null)
            tagController.updateAssetTags(assetCategory);

        assetCategory = assetCategoryService.get(assetCategory.getId());

        if (assetCategoryEditController != null) {
            assetCategoryEditController.refreshAssetCategories();
            assetCategoryEditController.setSelectedAssetCategory(assetCategory);
        }

        FacesContext.getCurrentInstance().addMessage("assetCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Category '" + assetCategory.getName() + "' Added!"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
