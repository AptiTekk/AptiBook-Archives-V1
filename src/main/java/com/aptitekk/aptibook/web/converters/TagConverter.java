/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.converters;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Tag;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;
import com.aptitekk.aptibook.core.domain.services.TagService;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class TagConverter implements Converter, Serializable {

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private TagService tagService;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || string.isEmpty() || tagService == null)
            return null;

        String[] split = string.split("\\|");
        if (split.length != 2)
            return null;

        ResourceCategory resourceCategory = resourceCategoryService.findByName(split[0]);
        if (resourceCategory == null)
            return null;

        return tagService.findByName(resourceCategory, split[1]);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof Tag) {
            return ((Tag) o).getResourceCategory().getName() + "|" + ((Tag) o).getName();
        }


        return "";
    }

}
