package com.AptiTekk.Agenda.web;

import com.AptiTekk.Agenda.core.AssetTypeService;
import com.AptiTekk.Agenda.core.entity.AssetType;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;

@ManagedBean
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
        if (o instanceof AssetType)
        {
            return ((AssetType) o).getName();
        }
        return "";
    }
    
}
