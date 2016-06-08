package com.cintriq.agenda.web.converters;

import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.entity.AssetType;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@ManagedBean(name = "AssetTypeConverter")
@ApplicationScoped
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
