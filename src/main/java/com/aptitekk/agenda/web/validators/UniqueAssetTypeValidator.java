package com.aptitekk.agenda.web.validators;

import com.aptitekk.agenda.core.AssetTypeService;
import com.aptitekk.agenda.core.UserService;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.entity.UserGroup;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

@ManagedBean(name = "UniqueAssetTypeValidator")
@RequestScoped
public class UniqueAssetTypeValidator implements Validator {

    @Inject
    AssetTypeService assetTypeService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object inputText) throws ValidatorException {
        Object exemptionAttribute = uiComponent.getAttributes().get("exemption");

        if (inputText != null && inputText instanceof String && assetTypeService != null) {
            AssetType otherAssetType = assetTypeService.findByName((String) inputText);
            if (otherAssetType != null) {
                if (exemptionAttribute != null && exemptionAttribute instanceof UserGroup && otherAssetType.equals(exemptionAttribute))
                    return;
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "An Asset Type with this name already exists."));
            }
        }
    }

}
