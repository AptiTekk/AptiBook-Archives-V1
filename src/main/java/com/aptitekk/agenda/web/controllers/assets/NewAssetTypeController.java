package com.aptitekk.agenda.web.controllers.assets;

import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.entity.AssetType;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ManagedBean(name = "NewAssetTypeController")
@RequestScoped
public class NewAssetTypeController {

    @Inject
    private AssetTypeService assetTypeService;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String name;

    @ManagedProperty(value = "#{TagController}")
    private TagController tagController;

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    @ManagedProperty(value = "#{AssetTypeEditController}")
    private AssetTypeEditController assetTypeEditController;

    public void setAssetTypeEditController(AssetTypeEditController assetTypeEditController) {
        this.assetTypeEditController = assetTypeEditController;
    }

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
