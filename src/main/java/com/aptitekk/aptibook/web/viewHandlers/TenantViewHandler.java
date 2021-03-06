/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.viewHandlers;

import com.aptitekk.aptibook.core.domain.entities.Tenant;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;

public class TenantViewHandler extends ViewHandlerWrapper {

    private ViewHandler wrapped;

    public TenantViewHandler(ViewHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        String actionUrl = super.getActionURL(context, viewId);
        String contextPath = context.getExternalContext().getApplicationContextPath();
        String url = actionUrl.substring(context.getExternalContext().getApplicationContextPath().length() + 1);

        Object attribute = context.getExternalContext().getRequestMap().get("tenant");
        Tenant tenant = attribute != null && attribute instanceof Tenant ? (Tenant) attribute : null;

        if (tenant != null) {
            actionUrl = contextPath + "/" + tenant.getSlug().toLowerCase() + "/" + url;
        }

        return actionUrl;
    }

    @Override
    public ViewHandler getWrapped() {
        return wrapped;
    }

}