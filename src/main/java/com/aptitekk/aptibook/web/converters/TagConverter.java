/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.converters;

import com.aptitekk.aptibook.core.entities.AssetCategory;
import com.aptitekk.aptibook.core.entities.Tag;
import com.aptitekk.aptibook.core.entities.services.AssetCategoryService;
import com.aptitekk.aptibook.core.entities.services.TagService;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;

@ManagedBean(name = "TagConverter")
@RequestScoped
public class TagConverter implements Converter {

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private TagService tagService;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || string.isEmpty() || tagService == null)
            return null;

        String[] split = string.split("\\|");
        if (split.length != 2)
            return null;

        AssetCategory assetCategory = assetCategoryService.findByName(split[0]);
        if (assetCategory == null)
            return null;

        return tagService.findByName(assetCategory, split[1]);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof Tag) {
            return ((Tag) o).getAssetCategory().getName() + "|" + ((Tag) o).getName();
        }


        return "";
    }

}
