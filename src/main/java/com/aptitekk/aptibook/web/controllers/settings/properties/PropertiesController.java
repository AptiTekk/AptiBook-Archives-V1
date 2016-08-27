/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.properties;

import com.aptitekk.aptibook.core.entities.Permission;
import com.aptitekk.aptibook.core.entities.Property;
import com.aptitekk.aptibook.core.entities.services.PropertiesService;
import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.RegexPropertyType;
import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.TextPropertyType;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class PropertiesController implements Serializable {

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private AuthenticationController authenticationController;

    @Inject
    private HelpController helpController;

    private List<PropertyInputGroup> propertyInputGroups;

    private boolean constructed;

    @PostConstruct
    private void init() {
        if (!hasPagePermission()) {
            authenticationController.forceUserRedirect();
            return;
        }
        buildPropertyInputGroups();

        helpController.setCurrentTopic(HelpController.Topic.SETTINGS_PROPERTIES);
    }

    private boolean hasPagePermission() {
        return authenticationController != null && authenticationController.userHasPermissionOfGroup(Permission.Group.PROPERTIES);
    }

    private boolean hasModifyPermission() {
        return authenticationController != null && authenticationController.userHasPermission(Permission.Descriptor.PROPERTIES_MODIFY_ALL);
    }

    public void constructProperties() {
        if (!constructed) {
            for (PropertyInputGroup inputGroup : propertyInputGroups) {
                inputGroup.constructProperties();
            }
            constructed = true;
        }
    }

    private void buildPropertyInputGroups() {
        Map<Property.Group, List<Property>> propertyGroupKeyEntityMap = new LinkedHashMap<>();
        propertyInputGroups = new ArrayList<>();

        for (Property.Key propertyKey : Property.Key.values()) {
            if (!propertyGroupKeyEntityMap.containsKey(propertyKey.getGroup()))
                propertyGroupKeyEntityMap.put(propertyKey.getGroup(), new ArrayList<>());

            propertyGroupKeyEntityMap.get(propertyKey.getGroup()).add(propertiesService.getPropertyByKey(propertyKey));
        }

        for (Map.Entry<Property.Group, List<Property>> entry : propertyGroupKeyEntityMap.entrySet()) {
            propertyInputGroups.add(new PropertyInputGroup(entry.getKey(), entry.getValue()));
        }
    }

    public void saveProperties() {
        if (!hasModifyPermission())
            return;

        //Iterate over the different property input groups (Sections)
        for (PropertyInputGroup propertyInputGroup : propertyInputGroups) {
            String groupClientId = "propertiesEditForm:propertyGroup" + propertyInputGroup.getPropertyGroup().ordinal();

            Map<Property.Key, String> propertiesInputMap = propertyInputGroup.getPropertiesInputMap();
            List<Property> propertyEntityList = propertyInputGroup.getPropertyEntityList();

            //Validate each property.
            boolean validationFailed = false;
            for (Map.Entry<Property.Key, String> entry : propertiesInputMap.entrySet()) {
                String propertyClientId = "propertiesEditForm:propertyField" + entry.getKey().ordinal();

                if (entry.getKey().getPropertyType() instanceof TextPropertyType) {
                    TextPropertyType textPropertyType = (TextPropertyType) entry.getKey().getPropertyType();
                    if (entry.getValue() != null && entry.getValue().length() > textPropertyType.getMaxLength()) {
                        FacesContext.getCurrentInstance().addMessage(propertyClientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "This may not be longer than " + textPropertyType.getMaxLength() + " characters."));
                        validationFailed = true;
                    }
                }

                if (entry.getKey().getPropertyType() instanceof RegexPropertyType) {
                    RegexPropertyType regexPropertyType = (RegexPropertyType) entry.getKey().getPropertyType();
                    if (regexPropertyType.getPattern() != null) {
                        if (entry.getValue() == null || !entry.getValue().matches(regexPropertyType.getPattern())) {
                            FacesContext.getCurrentInstance().addMessage(propertyClientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, regexPropertyType.getValidationMessage()));
                            validationFailed = true;
                        }
                    }
                }
            }

            //If there were no validation errors, save the properties for this input group.
            if (!validationFailed) {
                boolean changesMade = false;
                for (Property property : propertyEntityList) {

                    //No change to property; We can skip it. No need to make a query to change nothing.
                    if (propertiesInputMap.get(property.getPropertyKey()).equals(property.getPropertyValue()))
                        continue;

                    changesMade = true;
                    property.setPropertyValue(propertiesInputMap.get(property.getPropertyKey()));
                    try {
                        propertiesService.merge(property);
                    } catch (Exception e) {
                        FacesContext.getCurrentInstance().addMessage("propertiesEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Could not update properties. Internal Server Error."));
                        e.printStackTrace();
                        return;
                    }
                }

                if (changesMade) {
                    FacesContext.getCurrentInstance().addMessage(groupClientId, new FacesMessage(FacesMessage.SEVERITY_INFO, null, "The changes to '" + propertyInputGroup.getPropertyGroup().getFriendlyName() + "' have been saved."));
                    propertyInputGroup.getPropertyGroup().firePropertiesChangedEvent();
                }
            }
        }

        buildPropertyInputGroups();
    }

    public void resetFields() {
        if (!hasModifyPermission())
            return;

        for (PropertyInputGroup propertyInputGroup : propertyInputGroups) {
            propertyInputGroup.resetInputMap();
        }
    }

    public List<PropertyInputGroup> getPropertyInputGroups() {
        return propertyInputGroups;
    }
}
