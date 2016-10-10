/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.assetCategories;

import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.util.LogManager;

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
    private EditAssetCategoryController editAssetCategoryController;

    public void addAssetCategory() throws Exception {
        AssetCategory assetCategory = new AssetCategory();
        assetCategory.setName(name);
        assetCategoryService.insert(assetCategory);

        assetCategory = assetCategoryService.get(assetCategory.getId());

        if (editAssetCategoryController != null) {
            editAssetCategoryController.refreshAssetCategories();
            editAssetCategoryController.setSelectedAssetCategory(assetCategory);
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
