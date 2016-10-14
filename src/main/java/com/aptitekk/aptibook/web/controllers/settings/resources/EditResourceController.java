/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resources;

import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.domain.services.FileService;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;
import com.aptitekk.aptibook.core.domain.services.ResourceService;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
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
import java.util.*;

@Named
@ViewScoped
public class EditResourceController extends ResourceFieldSupplier implements Serializable {

    @Inject
    private ResourceService resourceService;

    @Inject
    private FileService fileService;

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private GroupTreeController groupTreeController;

    @Inject
    private HelpController helpController;

    @Inject
    private UserGroupService userGroupService;

    private List<ResourceCategory> resourceCategoryList;
    private HashMap<ResourceCategory, List<Resource>> resourceMap;
    private Resource selectedResource;

    private boolean userGroupsEmpty;

    @Inject
    private TagController tagController;

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.RESOURCES);
    }

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }

        refreshResources();

        this.userGroupsEmpty = userGroupService.getAll().size() <= 1;

        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_RESOURCES);
    }

    void refreshResources() {
        resourceCategoryList = new ArrayList<>();
        resourceMap = new HashMap<>();

        if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCE_CATEGORIES_MODIFY_ALL) || authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_ALL)) {
            resourceCategoryList = resourceCategoryService.getAll();
        }

        if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_ALL)) {
            for (ResourceCategory resourceCategory : resourceCategoryList) {
                resourceMap.putIfAbsent(resourceCategory, new ArrayList<>(resourceCategory.getResources()));
            }
        } else {
            if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_HIERARCHY)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    Queue<UserGroup> queue = new LinkedList<>();
                    queue.add(userGroup);

                    //Traverse the tree downward using a queue.
                    UserGroup queueGroup;
                    while ((queueGroup = queue.poll()) != null) {
                        queue.addAll(queueGroup.getChildren());

                        //Add all ResourceCategories for the Resources that the User is allowed to edit.
                        //noinspection Duplicates
                        for (Resource resource : queueGroup.getResources()) {
                            if (!resourceCategoryList.contains(resource.getResourceCategory()))
                                resourceCategoryList.add(resource.getResourceCategory());
                            resourceMap.putIfAbsent(resource.getResourceCategory(), new ArrayList<>());
                            resourceMap.get(resource.getResourceCategory()).add(resource);
                        }
                    }
                }
            } else if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_OWN)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    //Add all ResourceCategories for the Resources that the User is allowed to edit.
                    //noinspection Duplicates
                    for (Resource resource : userGroup.getResources()) {
                        if (!resourceCategoryList.contains(resource.getResourceCategory()))
                            resourceCategoryList.add(resource.getResourceCategory());
                        resourceMap.putIfAbsent(resource.getResourceCategory(), new ArrayList<>());
                        resourceMap.get(resource.getResourceCategory()).add(resource);
                    }
                }
            }
        }
    }

    public void updateSettings() {
        if (selectedResource != null) {
            boolean update = true;

            if (resourceOwnerGroup == null) {
                FacesContext.getCurrentInstance().addMessage("editResourceModalForm:ownerGroup", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Please select an Owner Group for this Resource."));
                update = false;
            }

            if (image != null) {
                try {
                    File file = fileService.createFileFromImagePart(image);
                    selectedResource.setImage(file);
                } catch (IOException e) {
                    LogManager.logException(getClass(), "Image Upload Failed", e);
                    FacesContext.getCurrentInstance().addMessage("editResourceModalForm:imageUpload", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The image upload failed. Please try again or try another file."));
                    update = false;
                }
            }

            if (update) {
                selectedResource.setName(resourceName);
                tagController.updateResourceTags(selectedResource);
                selectedResource.setNeedsApproval(reservationApprovalRequired);

                try {
                    if (resourceOwnerGroup != null)
                        selectedResource.setOwner(resourceOwnerGroup);

                    setSelectedResource(resourceService.merge(selectedResource));
                    FacesContext.getCurrentInstance().addMessage("resourcesForm_" + selectedResource.getResourceCategory().getId(), new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Resource '" + selectedResource.getName() + "' Updated"));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.logException(getClass(), "Updating Resource Settings Failed", e);
                    FacesContext.getCurrentInstance().addMessage("resourcesForm_" + selectedResource.getResourceCategory().getId(), CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedResource != null) {
            resourceName = selectedResource.getName();
            tagController.setAvailableTags(selectedResource.getResourceCategory().getTags());
            tagController.setSelectedResourceTags(selectedResource.getTags());
            reservationApprovalRequired = selectedResource.getNeedsApproval();
            this.resourceOwnerGroup = selectedResource.getOwner();

            groupTreeController.invalidateTrees();
            if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_ALL)) {
                tree = groupTreeController.getTree(selectedResource.getOwner(), null, false, false);
            } else if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_HIERARCHY)) {
                tree = groupTreeController.getFilteredTree(selectedResource.getOwner(), authenticationController.getAuthenticatedUser().getUserGroups(), true);
            } else if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_OWN)) {
                tree = groupTreeController.getFilteredTree(selectedResource.getOwner(), authenticationController.getAuthenticatedUser().getUserGroups(), false);
            } else
                tree = null;
        }
        this.fileName = null;
    }

    public void deleteSelectedResource() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (resourceService.get(selectedResource.getId()) != null) {
                context.addMessage("resourcesForm_" + selectedResource.getResourceCategory().getId(), new FacesMessage("Successful", "Resource Deleted!"));
                resourceService.delete(selectedResource.getId());
                refreshResources();
            } else {
                throw new Exception("Resource not found!");
            }
        } catch (Exception e) {
            context.addMessage("resourcesForm_" + selectedResource.getResourceCategory().getId(), CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
            LogManager.logException(getClass(), "Error while Deleting Resource", e);
        }

        selectedResource = null;
    }

    public List<ResourceCategory> getResourceCategoryList() {
        return resourceCategoryList;
    }

    public HashMap<ResourceCategory, List<Resource>> getResourceMap() {
        return resourceMap;
    }

    public Resource getSelectedResource() {
        return selectedResource;
    }

    public void setSelectedResource(Resource selectedResource) {
        this.selectedResource = selectedResource;
        resetSettings();
    }

    public boolean isUserGroupsEmpty() {
        return userGroupsEmpty;
    }
}
