/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.tenant;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.TenantService;
import org.apache.http.client.utils.URIBuilder;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Stateful
public class TenantSessionService implements Serializable {

    @Inject
    private TenantManagementService tenantManagementService;

    @Inject
    private TenantService tenantService;

    @Inject
    private HttpServletRequest httpRequest;

    @PostConstruct
    private void init() {
        try {
            if (httpRequest != null)
                httpRequest.getAttribute("tenant");
        } catch (Exception ignored) {
            httpRequest = null;
        }
    }

    public Tenant getCurrentTenant() {
        if (httpRequest != null) {
            Object attribute = httpRequest.getAttribute("tenant");
            if (attribute != null && attribute instanceof Tenant)
                return (Tenant) attribute;
        }

        return null;
    }

    public ZoneId getCurrentTenantZoneId() {
        Tenant tenant = getCurrentTenant();
        if (tenant != null) {
            return tenantManagementService.getZoneId(tenant);
        }
        return ZoneId.systemDefault();
    }

    /**
     * @param page        The page the user will be redirected to. Must include file type (ex. "index.xhtml"). If null, uses current servlet path.
     * @param queryParams Query parameters to be passed in URI.
     * @return Returns built URI, or null if exception is thrown.
     */
    public URI buildURI(String page, HashMap<String, String> queryParams) {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String file;
        if (page != null && page.length() > 0) {
            file = origRequest.getContextPath() + "/" + getCurrentTenant().getSlug() + "/" + page;
        } else {
            file = origRequest.getContextPath() + "/" + getCurrentTenant().getSlug() + origRequest.getServletPath();
        }
        try {
            URIBuilder b = new URIBuilder();
            b.setScheme(origRequest.getScheme());
            b.setHost(origRequest.getServerName());
            b.setPort(origRequest.getServerPort());
            b.setPath(file);
            if (queryParams != null && queryParams.size() > 0) {
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    b.setParameter(entry.getKey(), entry.getValue());
                }
            }
            return b.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
