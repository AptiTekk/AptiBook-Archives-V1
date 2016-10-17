/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resourceCategories;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;

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
public class NewResourceCategoryController implements Serializable {

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String name;

    @Inject
    private EditResourceCategoryController editResourceCategoryController;

    public void addResourceCategory() throws Exception {
        ResourceCategory resourceCategory = new ResourceCategory();
        resourceCategory.setName(name);
        resourceCategoryService.insert(resourceCategory);

        resourceCategory = resourceCategoryService.get(resourceCategory.getId());

        if (editResourceCategoryController != null) {
            editResourceCategoryController.refreshResourceCategories();
            editResourceCategoryController.setSelectedResourceCategory(resourceCategory);
        }

        FacesContext.getCurrentInstance().addMessage("resourceCategoryEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Resource Category '" + resourceCategory.getName() + "' Added!"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
