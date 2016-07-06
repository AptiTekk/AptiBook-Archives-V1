/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.assets;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.services.AssetTypeService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Named
@RequestScoped
public class NewAssetTypeController implements Serializable {

    @Inject
    private AssetTypeService assetTypeService;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String name;

    @Inject
    private TagController tagController;

    @Inject
    private AssetTypeEditController assetTypeEditController;

    public void addAssetType() throws Exception {
        AssetType assetType = new AssetType();
        assetType.setName(name);
        assetTypeService.insert(assetType);

        if (tagController != null)
            tagController.updateAssetTags(assetType);

        assetType = assetTypeService.get(assetType.getId());

        if (assetTypeEditController != null) {
            assetTypeEditController.refreshAssetTypes();
            assetTypeEditController.setSelectedAssetType(assetType);
        }

        FacesContext.getCurrentInstance().addMessage("assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Type '" + assetType.getName() + "' Added!"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
