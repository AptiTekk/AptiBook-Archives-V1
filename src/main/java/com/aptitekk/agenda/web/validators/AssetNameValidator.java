package com.aptitekk.agenda.web.validators;

import com.aptitekk.agenda.core.AssetService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

@ManagedBean(name = "AssetNameValidator")
@RequestScoped
public class AssetNameValidator implements Validator {

    @Inject
    AssetService assetService;


    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

    }
}
