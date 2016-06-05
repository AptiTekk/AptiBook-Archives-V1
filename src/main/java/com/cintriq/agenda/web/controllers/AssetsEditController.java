package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.AssetService;
import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.UserGroup;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;

@ManagedBean(name = "AssetsEditController")
@ViewScoped
public class AssetsEditController {

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private AssetService assetService;

    private List<AssetType> assetTypes;
    private AssetType selectedAssetType;
    private Asset selectedTabAsset;

    private String editableAssetTypeName;

    private String editableTabAssetName;
    private boolean editableTabAssetApproval;
    private String editableTabAssetAvailabilityStart;
    private String editableTabAssetAvailabilityEnd;
    private TreeNode editableTabAssetOwnerGroup;
    private UserGroup tabAssetCurrentOwnerGroup;

    @PostConstruct
    public void init() {
        refreshAssetTypeList();
        resetSettings();
    }

    private void refreshAssetTypeList() {
        assetTypes = assetTypeService.getAll();
    }

    public void updateSettings() {
        if (getSelectedAssetType() != null) {
            AssetType assetType = assetTypeService.findByName(getEditableAssetTypeName());
            if (assetType != null && !assetType.equals(getSelectedAssetType()))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Asset Type with that name already exists!"));
            else if (getEditableAssetTypeName().isEmpty())
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset Type's name cannot be empty!"));
            else if (!getEditableAssetTypeName().matches("[A-Za-z0-9 #]+"))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset Type's name may only contain A-Z, a-z, 0-9, #, and spaces!"));

            if (FacesContext.getCurrentInstance().getMessageList(":assetTypeEditForm").isEmpty()) {
                try {
                    getSelectedAssetType().setName(getEditableAssetTypeName());
                    setSelectedAssetType(assetTypeService.merge(getSelectedAssetType()));
                    refreshAssetTypeList();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Type Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
                }
            }
        }
    }

    public void resetSettings() {
        if (getSelectedAssetType() != null)
            setEditableAssetTypeName(getSelectedAssetType().getName());
        else
            setEditableAssetTypeName("");
    }

    public void updateTabAssetSettings() {
        if (getSelectedTabAsset() != null) {
            Asset asset = assetService.findByName(getEditableTabAssetName());
            if (asset != null && !asset.equals(getSelectedTabAsset()))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "A Asset with that name already exists!"));
            else if (getEditableTabAssetName().isEmpty())
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset's name cannot be empty!"));
            else if (!getEditableTabAssetName().matches("[A-Za-z0-9 #]+"))
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The Asset's name may only contain A-Z, a-z, 0-9, #, and space!"));

            if (editableTabAssetOwnerGroup == null)
                FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Please select an Owner Group for this Asset!"));

            if (FacesContext.getCurrentInstance().getMessageList(":assetTypeEditForm").isEmpty()) {
                getSelectedTabAsset().setName(getEditableTabAssetName());
                getSelectedTabAsset().setNeedsApproval(isEditableTabAssetApproval());

                try {
                    getSelectedTabAsset().setAvailabilityStart(getEditableTabAssetAvailabilityStart() == null ? null : TimeSelectionController.TIME_FORMAT.parse(getEditableTabAssetAvailabilityStart()));
                    getSelectedTabAsset().setAvailabilityEnd(getEditableTabAssetAvailabilityEnd() == null ? null : TimeSelectionController.TIME_FORMAT.parse(getEditableTabAssetAvailabilityEnd()));
                } catch (ParseException e) {
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An internal error occurred while updating Asset! (Time selection invalid)"));
                    e.printStackTrace();
                    refreshAssetTypeList();
                    return;
                }
                try {
                    getSelectedTabAsset().setOwner((UserGroup) editableTabAssetOwnerGroup.getData());

                    setSelectedTabAsset(assetService.merge(getSelectedTabAsset()));
                    refreshAssetTypeList();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Asset Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage(":assetTypeEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
                }
            }
        }
    }

    public void resetTabAssetSettings() {
        if (getSelectedTabAsset() != null) {
            setEditableTabAssetName(getSelectedTabAsset().getName());
            setEditableTabAssetApproval(getSelectedTabAsset().getNeedsApproval());
            setEditableTabAssetAvailabilityStart(getSelectedTabAsset().getAvailabilityStart() == null ? null : TimeSelectionController.TIME_FORMAT.format(getSelectedTabAsset().getAvailabilityStart()));
            setEditableTabAssetAvailabilityEnd(getSelectedTabAsset().getAvailabilityEnd() == null ? null : TimeSelectionController.TIME_FORMAT.format(getSelectedTabAsset().getAvailabilityEnd()));
            this.tabAssetCurrentOwnerGroup = getSelectedTabAsset().getOwner();
        } else {
            setEditableTabAssetName("");
            setEditableTabAssetApproval(false);
            setEditableTabAssetAvailabilityStart(null);
            setEditableTabAssetAvailabilityEnd(null);
            this.tabAssetCurrentOwnerGroup = null;
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

        refreshAssetTypeList();
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

        refreshAssetTypeList();
    }

    public void onAssetTabChange(TabChangeEvent event) {
        if (event.getData() instanceof Asset)
            setSelectedTabAsset((Asset) event.getData());
    }

    public void deleteSelectedTabAsset() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (assetService.get(getSelectedTabAsset().getId()) != null) {
                context.addMessage(":assetTypeEditForm", new FacesMessage("Successful", "Asset Deleted!"));
                assetService.delete(getSelectedTabAsset().getId());
                setSelectedAssetType(assetTypeService.get(getSelectedAssetType().getId()));
            } else {
                throw new Exception("Asset not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(":assetTypeEditForm", new FacesMessage("Failure", "Error While Deleting Asset!"));
        }

        refreshAssetTypeList();
    }

    public void addNewAsset() {
        if (getSelectedAssetType() != null) {
            try {
                Asset asset = new Asset("New Asset");
                asset.setType(getSelectedAssetType());
                assetService.insert(asset);

                setSelectedAssetType(assetTypeService.get(getSelectedAssetType().getId()));
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error: " + e.getMessage()));
            }

            refreshAssetTypeList();

        }
    }

    public List<AssetType> getAssetTypes() {
        return assetTypes;
    }

    public AssetType getSelectedAssetType() {
        return selectedAssetType;
    }

    public Asset getSelectedTabAsset() {
        return selectedTabAsset;
    }

    public void setSelectedTabAsset(Asset selectedTabAsset) {
        this.selectedTabAsset = selectedTabAsset;
        resetTabAssetSettings();
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

    public String getEditableTabAssetName() {
        return editableTabAssetName;
    }

    public void setEditableTabAssetName(String editableTabAssetName) {
        this.editableTabAssetName = editableTabAssetName;
    }

    public boolean isEditableTabAssetApproval() {
        return editableTabAssetApproval;
    }

    public void setEditableTabAssetApproval(boolean editableTabAssetApproval) {
        this.editableTabAssetApproval = editableTabAssetApproval;
    }

    public String getEditableTabAssetAvailabilityStart() {
        return editableTabAssetAvailabilityStart;
    }

    public void setEditableTabAssetAvailabilityStart(String editableTabAssetAvailabilityStart) {
        this.editableTabAssetAvailabilityStart = editableTabAssetAvailabilityStart;
    }

    public String getEditableTabAssetAvailabilityEnd() {
        return editableTabAssetAvailabilityEnd;
    }

    public void setEditableTabAssetAvailabilityEnd(String editableTabAssetAvailabilityEnd) {
        this.editableTabAssetAvailabilityEnd = editableTabAssetAvailabilityEnd;
    }

    public void onOwnerSelected(NodeSelectEvent event) {
        this.editableTabAssetOwnerGroup = event.getTreeNode();
    }

    public UserGroup getTabAssetCurrentOwnerGroup() {
        return tabAssetCurrentOwnerGroup;
    }
}
