/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.converters;

import com.aptitekk.aptibook.core.entities.AssetCategory;
import com.aptitekk.aptibook.core.entities.services.AssetCategoryService;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class AssetCategoryConverter implements Converter, Serializable {

    @Inject
    private AssetCategoryService assetCategoryService;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || string.isEmpty() || assetCategoryService == null)
            return null;

        return fc.getExternalContext().getSessionMap().get("AssetCategory_" + string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof AssetCategory) {
            fc.getExternalContext().getSessionMap().put("AssetCategory_" + ((AssetCategory) o).getName(), o);
            return ((AssetCategory) o).getName();
        }
        return "";
    }

}
