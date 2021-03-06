/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.util;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.util.AptiBookInfoProvider;
import org.apache.http.client.utils.URIBuilder;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class FacesURIBuilder {

    /**
     * Builds a standard (non tenant-based) URI.
     *
     * @param page        The page the user will be redirected to. Must include file type (ex. "index.xhtml"). If null, uses root.
     * @param queryParams Query parameters to be passed in URI.
     * @return Returns built URI, or null if an exception occurred.
     */
    public static URI buildURI(String page, HashMap<String, String> queryParams) {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String file;
        if (page != null) {
            file = origRequest.getContextPath() + "/" + page;
        } else {
            file = origRequest.getContextPath() + "/";
        }

        try {
            URIBuilder b = new URIBuilder();
            //Force HTTPS if using Heroku.
            b.setScheme(AptiBookInfoProvider.isUsingHeroku() ? "https" : origRequest.getScheme());
            b.setHost(origRequest.getServerName());
            if (origRequest.getServerPort() != 80)
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

    /**
     * Builds a URI for the passed in Tenant.
     *
     * @param tenant      The tenant to build the URI for.
     * @param page        The page the user will be redirected to. Must include file type (ex. "index.xhtml"). If null, uses tenant root.
     * @param queryParams Query parameters to be passed in URI.
     * @return Returns built URI, or null if an exception occurred or the tenant is null.
     */
    public static URI buildTenantURI(Tenant tenant, String page, HashMap<String, String> queryParams) {
        if (tenant == null)
            return null;
        if (page != null)
            return buildURI(tenant.getSlug() + "/" + page, queryParams);
        else
            return buildURI(tenant.getSlug() + "/", queryParams);

    }


}
