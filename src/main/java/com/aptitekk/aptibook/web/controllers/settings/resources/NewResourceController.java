/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resources;

import com.aptitekk.aptibook.core.domain.entities.File;
import com.aptitekk.aptibook.core.domain.entities.Permission;
import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.services.FileService;
import com.aptitekk.aptibook.core.domain.services.ResourceService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.settings.groups.GroupTreeController;
import com.aptitekk.aptibook.web.controllers.settings.resourceCategories.TagController;
import com.aptitekk.aptibook.web.util.CommonFacesMessages;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class NewResourceController extends ResourceFieldSupplier implements Serializable {

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private GroupTreeController groupTreeController;

    @Inject
    private ResourceService resourceService;

    @Inject
    private FileService fileService;

    @Inject
    private TagController tagController;

    @Inject
    private EditResourceController editResourceController;

    private ResourceCategory resourceCategory;

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.RESOURCES);
    }

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.invokeUserRedirect();
            return;
        }
        resetSettings();
    }

    public void addNewResource() {
        boolean update = true;
        try {
            if (resourceCategory != null) {
                Resource resource = new Resource();

                if (resourceOwnerGroup == null) {
                    FacesContext.getCurrentInstance().addMessage("newResourceModalForm:ownerGroup", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Please select an Owner Group for this Resource."));
                    update = false;
                }
                if (image != null) {
                    try {
                        File file = fileService.createFileFromImagePart(image);
                        resource.setImage(file);
                        image = null;
                    } catch (IOException e) {
                        LogManager.logException(getClass(), e, "Image Upload Failed");
                        FacesContext.getCurrentInstance().addMessage("newResourceModalForm:imageUpload", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The image upload failed. Please try again or try another file."));
                    }
                }
                if (update) {
                    resource.setName(resourceName);
                    resource.setNeedsApproval(reservationApprovalRequired);
                    resource.setOwner(resourceOwnerGroup);
                    resource.setResourceCategory(resourceCategory);
                    resourceService.insert(resource);
                    tagController.updateResourceTags(resource);
                    editResourceController.refreshResources();
                    FacesContext.getCurrentInstance().addMessage("resourcesForm_" + resource.getResourceCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Resource Added!"));
                }
            }
        } catch (Exception e) {
            LogManager.logException(getClass(), e, "Could not persist new resource");
            FacesContext.getCurrentInstance().addMessage("resourcesForm_" + resourceCategory.getId(), CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
        }
    }

    private void resetSettings() {
        resourceName = null;
        tagController.setAvailableTags(resourceCategory != null ? resourceCategory.getTags() : null);
        tagController.setSelectedResourceTags(null);
        reservationApprovalRequired = false;
        this.resourceOwnerGroup = null;

        groupTreeController.invalidateTrees();
        if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_ALL)) {
            tree = groupTreeController.getTree(null, null, false, false);
        } else if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_HIERARCHY)) {
            tree = groupTreeController.getFilteredTree(null, authenticationController.getAuthenticatedUser().getUserGroups(), true);
        } else if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_OWN)) {
            tree = groupTreeController.getFilteredTree(null, authenticationController.getAuthenticatedUser().getUserGroups(), false);
        } else {
            tree = null;
        }
        this.fileName = null;
    }

    public ResourceCategory getResourceCategory() {
        return resourceCategory;
    }

    public void setResourceCategory(ResourceCategory resourceCategory) {
        this.resourceCategory = resourceCategory;
        resetSettings();
    }
}
