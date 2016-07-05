package com.aptitekk.agenda.web.converters;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.services.TagService;
import com.aptitekk.agenda.core.entity.Tag;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;

@ManagedBean(name = "TagConverter")
@RequestScoped
public class TagConverter implements Converter {

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private TagService tagService;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || string.isEmpty() || tagService == null)
            return null;

        String[] split = string.split("\\|");
        if (split.length != 2)
            return null;

        AssetType assetType = assetTypeService.findByName(split[0]);
        if (assetType == null)
            return null;

        return tagService.findByName(assetType, split[1]);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof Tag) {
            return ((Tag) o).getAssetType().getName() + "|" + ((Tag) o).getName();
        }



        return "";
    }

}
