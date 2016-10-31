/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.domain.services.PropertiesService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class PersonalizationController implements Serializable {

    @Inject
    private PropertiesService propertiesService;
    private String organizationName;

    @PostConstruct
    private void init() {
        List<Property> personalizationProperties = propertiesService.getAllPropertiesByGroup(Property.Group.PERSONALIZATION);
        if (personalizationProperties != null) {
            for (Property property : personalizationProperties) {
                switch (property.getPropertyKey()) {
                    case PERSONALIZATION_ORGANIZATION_NAME:
                        this.organizationName = property.getPropertyValue();
                        break;
                }
            }
        }
    }

    public String getOrganizationName() {
        return organizationName;
    }
}
