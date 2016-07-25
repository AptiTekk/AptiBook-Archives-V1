/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.converters;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.services.AssetCategoryService;

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

        return assetCategoryService.findByName(string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof AssetCategory) {
            return ((AssetCategory) o).getName();
        }
        return "";
    }

}
