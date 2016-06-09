package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.AssetService;
import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Tag;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
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

    private String editableAssetTypeName;

    @ManagedProperty(value = "#{TagController}")
    private TagController tagController;

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
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
        if (selectedAssetType != null) {
            AssetType assetType = assetTypeService.findByName(editableAssetTypeName);
            if (assetType != null && !assetType.equals(selectedAssetType))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Asset Type with that name already exists!"));
            else if (editableAssetTypeName.isEmpty())
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset Type's name cannot be empty!"));
            else if (!editableAssetTypeName.matches("[A-Za-z0-9 #]+"))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset Type's name may only contain A-Z, a-z, 0-9, #, and spaces!"));

            if (FacesContext.getCurrentInstance().getMessageList(":assetTypeEditForm").isEmpty()) {
                try {
                    selectedAssetType.setName(editableAssetTypeName);

                    //Persist name change
                    assetTypeService.merge(selectedAssetType);

                    //Update tags
                    tagController.updateAssetTags(selectedAssetType);

                    //Refresh AssetType
                    setSelectedAssetType(assetTypeService.get(selectedAssetType.getId()));
                    refreshAssetTypes();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Type Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
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

    public void addNewAssetType() {
        AssetType assetType = new AssetType("New Asset Type");
        try {
            assetTypeService.insert(assetType);
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
        }

        refreshAssetTypes();
    }

    public void deleteSelectedAssetType() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetTypeService.get(getSelectedAssetType().getId()) != null) {
                context.addMessage(":assetTypeEditForm", new FacesMessage("Successful", "Asset Type Deleted!"));
                assetTypeService.delete(getSelectedAssetType().getId());
                setSelectedAssetType(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(":assetTypeEditForm", new FacesMessage("Failure", "Error While Deleting Asset Type!"));
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
