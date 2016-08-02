/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.frontPage;

import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.entities.services.PropertiesService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class FrontPageController implements Serializable {

    @Inject
    private PropertiesService propertiesService;
    private Property policies;

    @PostConstruct
    private void init() {
        this.policies = propertiesService.getPropertyByKey(Property.Key.POLICY_BOX);
    }

    public String getPolicies() {
        return (policies != null ? policies.getPropertyValue() : null);
    }

}
