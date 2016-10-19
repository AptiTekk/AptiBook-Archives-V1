/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resourceCategories;

import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.domain.services.ReservationFieldService;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;
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
public class EditResourceCategoryController implements Serializable {

    @Inject
    private TagController tagController;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private ReservationFieldService reservationFieldService;

    private List<ResourceCategory> resourceCategories;
    private ResourceCategory selectedResourceCategory;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String editableResourceCategoryName;

    private List<ReservationField> reservationFields;

    @PostConstruct
    public void init() {
        if (!hasPagePermission()) {
            authenticationController.invokeUserRedirect();
            return;
        }

        refreshResourceCategories();
        resetSettings();

        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_RESOURCE_CATEGORIES);
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.RESOURCES);
    }

    public boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.RESOURCE_CATEGORIES_MODIFY_ALL);
    }

    void refreshResourceCategories() {
        resourceCategories = new ArrayList<>();

        if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCE_CATEGORIES_MODIFY_ALL) || authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_ALL)) {
            resourceCategories = resourceCategoryService.getAll();
        }

        if (!authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_ALL)) {
            if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_HIERARCHY)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    Queue<UserGroup> queue = new LinkedList<>();
                    queue.add(userGroup);

                    //Traverse the tree downward using a queue.
                    UserGroup queueGroup;
                    while ((queueGroup = queue.poll()) != null) {
                        queue.addAll(queueGroup.getChildren());

                        //Add all Resource Categories for the Resources that the User is allowed to edit.
                        //noinspection Duplicates
                        for (Resource resource : queueGroup.getResources()) {
                            if (!resourceCategories.contains(resource.getResourceCategory()))
                                resourceCategories.add(resource.getResourceCategory());
                        }
                    }
                }
            } else if (authenticationController.userHasPermission(Permission.Descriptor.RESOURCES_MODIFY_OWN)) {
                for (UserGroup userGroup : authenticationController.getAuthenticatedUser().getUserGroups()) {
                    //Add all Resource Categories for the Resources that the User is allowed to edit.
                    //noinspection Duplicates
                    for (Resource resource : userGroup.getResources()) {
                        if (!resourceCategories.contains(resource.getResourceCategory()))
                            resourceCategories.add(resource.getResourceCategory());
                    }
                }
            }
        }

        //Refresh selected ResourceCategory
        if (selectedResourceCategory != null)
            selectedResourceCategory = resourceCategoryService.get(selectedResourceCategory.getId());
        resetSettings();
    }

    public void updateSettings() {
        if (!hasModifyPermission())
            return;

        if (selectedResourceCategory != null && editableResourceCategoryName != null) {

            //Check if another Resource Category has the same name.
            ResourceCategory resourceCategory = resourceCategoryService.findByName(editableResourceCategoryName);
            if (resourceCategory != null && !resourceCategory.equals(selectedResourceCategory))
                FacesContext.getCurrentInstance().addMessage("resourceCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Resource Category with that name already exists!"));

            if (FacesContext.getCurrentInstance().getMessageList("resourceCategoryEditForm").isEmpty()) {
                try {
                    selectedResourceCategory.setName(editableResourceCategoryName);

                    //Persist name change
                    resourceCategoryService.merge(selectedResourceCategory);
                    //Update tags
                    tagController.updateResourceTags(selectedResourceCategory);

                    //Refresh ResourceCategory
                    setSelectedResourceCategory(resourceCategoryService.get(selectedResourceCategory.getId()));
                    refreshResourceCategories();
                    FacesContext.getCurrentInstance().addMessage("resourceCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Resource Category Updated"));
                } catch (Exception e) {
                    LogManager.logException(getClass(), "Error updating Resource Category settings", e);
                    FacesContext.getCurrentInstance().addMessage("resourceCategoryEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
                }
            }
        }
    }

    public void resetSettings() {
        if (selectedResourceCategory != null) {
            editableResourceCategoryName = selectedResourceCategory.getName();
            reservationFields = selectedResourceCategory.getReservationFields();

            List<Tag> tags = selectedResourceCategory.getTags();
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : tags)
                tagNames.add(tag.getName());
            tagController.setSelectedResourceCategoryTagNames(tagNames);
        } else {
            editableResourceCategoryName = "";
            reservationFields = null;
            tagController.setSelectedResourceCategoryTagNames(null);
        }
    }

    public void deleteSelectedResourceCategory() {
        if (!hasModifyPermission())
            return;

        try {
            if (resourceCategoryService.get(getSelectedResourceCategory().getId()) != null) {
                FacesContext.getCurrentInstance().addMessage("resourceCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Resource Category Deleted!"));
                resourceCategoryService.delete(getSelectedResourceCategory().getId());
                setSelectedResourceCategory(null);
            } else {
                LogManager.logError(getClass(), "Error while deleting Resource Category: Resource Category not found.");
            }
        } catch (Exception e) {
            LogManager.logException(getClass(), "Error while deleting Resource Category", e);
            FacesContext.getCurrentInstance().addMessage("resourceCategoryEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
        }

        refreshResourceCategories();
    }

    public void addNewReservationField() {
        if (selectedResourceCategory != null) {
            ReservationField reservationField = new ReservationField();
            reservationField.setResourceCategory(selectedResourceCategory);
            reservationField.setTitle("New Reservation Field");
            reservationField.setMultiLine(false);

            try {
                reservationFieldService.insert(reservationField);
                refreshResourceCategories();

                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "New Reservation Field Added."));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("reservationFieldEditForm", CommonFacesMessages.EXCEPTION_FACES_MESSAGE);
                LogManager.logException(getClass(), "Could not add new Reservation Field", e);
            }
        }
    }

    public List<ResourceCategory> getResourceCategories() {
        return resourceCategories;
    }

    public void setResourceCategories(List<ResourceCategory> resourceCategories) {
        this.resourceCategories = resourceCategories;
    }

    public ResourceCategory getSelectedResourceCategory() {
        return selectedResourceCategory;
    }

    public void setSelectedResourceCategory(ResourceCategory selectedResourceCategory) {
        this.selectedResourceCategory = selectedResourceCategory;
        resetSettings();
    }

    public String getEditableResourceCategoryName() {
        return editableResourceCategoryName;
    }

    public void setEditableResourceCategoryName(String editableResourceCategoryName) {
        this.editableResourceCategoryName = editableResourceCategoryName;
    }

    public List<ReservationField> getReservationFields() {
        return reservationFields;
    }
}
