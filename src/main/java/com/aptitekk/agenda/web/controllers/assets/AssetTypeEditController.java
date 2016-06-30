package com.aptitekk.agenda.web.controllers.assets;

import com.aptitekk.agenda.core.AssetService;
import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.Tag;
import com.aptitekk.agenda.core.AssetTypeService;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "AssetTypeEditController")
@ViewScoped
public class AssetTypeEditController {

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private AssetService assetService;

    private List<AssetType> assetTypes;
    private AssetType selectedAssetType;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableAssetTypeName;

    @ManagedProperty(value = "#{TagController}")
    private TagController tagController;

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    private AssetEditController assetEditController;

    void setAssetEditController(AssetEditController assetEditController) {
        this.assetEditController = assetEditController;
    }

    @PostConstruct
    public void init() {
        refreshAssetTypes();
        resetSettings();
    }

    void refreshAssetTypes() {
        assetTypes = assetTypeService.getAll();

        //Refresh selected AssetType
        if(selectedAssetType != null)
            selectedAssetType = assetTypeService.get(selectedAssetType.getId());
    }

    public void updateSettings() {
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

                if(assetEditController != null)
                    assetEditController.setSelectedAsset(asset);
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
        if(assetEditController != null)
            assetEditController.setSelectedAsset(null);

        resetSettings();
    }

    public String getEditableAssetTypeName() {
        return editableAssetTypeName;
    }

    public void setEditableAssetTypeName(String editableAssetTypeName) {
        this.editableAssetTypeName = editableAssetTypeName;
    }

}
