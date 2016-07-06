/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.converters;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.services.AssetTypeService;

import javax.faces.bean.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;

@ManagedBean(name = "AssetTypeConverter")
@RequestScoped
public class AssetTypeConverter implements Converter {

    @Inject
    private AssetTypeService assetTypeService;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || string.isEmpty() || assetTypeService == null)
            return null;

        return assetTypeService.findByName(string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof AssetType) {
            return ((AssetType) o).getName();
        }
        return "";
    }

}
