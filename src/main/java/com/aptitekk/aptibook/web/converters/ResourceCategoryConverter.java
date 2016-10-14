/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.converters;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class ResourceCategoryConverter implements Converter, Serializable {

    @Inject
    private ResourceCategoryService resourceCategoryService;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || string.isEmpty() || resourceCategoryService == null)
            return null;

        return fc.getExternalContext().getSessionMap().get("ResourceCategory_" + string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof ResourceCategory) {
            fc.getExternalContext().getSessionMap().put("ResourceCategory_" + ((ResourceCategory) o).getName(), o);
            return ((ResourceCategory) o).getName();
        }
        return "";
    }

}
