/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.entities.Tenant;
import com.aptitekk.agenda.core.tenants.TenantSessionService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.net.MalformedURLException;

@Named
@RequestScoped
public class FacesNavigationController implements Serializable {

    @Inject
    private TenantSessionService tenantSessionService;

    public String getUrlFromNavigationCase(String outcome) {
        Tenant currentTenant = tenantSessionService.getCurrentTenant();

        if (outcome == null)
            return "#";

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler navigationHandler = (ConfigurableNavigationHandler) facesContext.getApplication().getNavigationHandler();
        try {
            return navigationHandler.getNavigationCase(facesContext, null, outcome).getActionURL(facesContext).toString();
        } catch (MalformedURLException e) {
            return "#";
        }
    }

}
