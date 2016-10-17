/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.util;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class SessionVariableManager {

    private final HttpServletRequest httpServletRequest;
    private final ExternalContext externalContext;

    private final String tenantSlug;

    private SessionVariableManager(FacesContext facesContext, String tenantSlug) {
        this.httpServletRequest = null;
        this.externalContext = facesContext != null ? facesContext.getExternalContext() : null;
        this.tenantSlug = tenantSlug;
    }

    private SessionVariableManager(HttpServletRequest request, String tenantSlug) {
        this.httpServletRequest = request;
        this.externalContext = null;
        this.tenantSlug = tenantSlug;
    }

    /**
     * Instructs the manager that servlet session variable methods should be used.
     *
     * @param request The Servlet Request.
     * @return a new SessionVariableManager.
     */
    public static SessionVariableManager fromServlet(HttpServletRequest request) {
        return new SessionVariableManager(request, null);
    }

    /**
     * Instructs the manager that servlet session variable methods should be used.
     * Ensures that any variables within this instance are tenant-specific.
     *
     * @param request    The Servlet Request.
     * @param tenantSlug The slug of the tenant.
     * @return a new SessionVariableManager.
     */
    public static SessionVariableManager fromServletUsingTenant(HttpServletRequest request, String tenantSlug) {
        return new SessionVariableManager(request, tenantSlug);
    }

    /**
     * Instructs the manager that faces session variable methods should be used.
     *
     * @param facesContext The FacesContext.
     * @return a new SessionVariableManager.
     */
    public static SessionVariableManager fromFaces(FacesContext facesContext) {
        return new SessionVariableManager(facesContext, null);
    }

    /**
     * Instructs the manager that faces session variable methods should be used.
     * Ensures that any variables within this instance are tenant-specific.
     *
     * @param facesContext The FacesContext.
     * @param tenantSlug   The slug of the tenant.
     * @return a new SessionVariableManager.
     */
    public static SessionVariableManager fromFacesUsingTenant(FacesContext facesContext, String tenantSlug) {
        return new SessionVariableManager(facesContext, tenantSlug);
    }

    /**
     * Retrieves data from the variable in Object format.
     *
     * @param variableName The name of the variable to access.
     * @return The data stored in the variable.
     */
    public Object getVariableData(String variableName) {
        if (httpServletRequest != null) {
            return httpServletRequest.getSession().getAttribute((tenantSlug != null ? tenantSlug + "_" : "") + variableName);
        } else if (externalContext != null) {
            return externalContext.getSessionMap().get((tenantSlug != null ? tenantSlug + "_" : "") + variableName);
        }

        return null;
    }

    /**
     * Retrieves data from the variable in String format.
     *
     * @param variableName The name of the variable to access.
     * @return The data stored in the variable.
     */
    public String getStringVariableData(String variableName) {
        Object data = getVariableData(variableName);
        return data instanceof String ? (String) data : null;
    }

    /**
     * Retrieves data from the variable in Boolean format.
     *
     * @param variableName The name of the variable to access.
     * @return The data stored in the variable. (False if null or cannot be parsed).
     */
    public boolean getBooleanVariableData(String variableName) {
        String data = getStringVariableData(variableName);
        return Boolean.parseBoolean(data);
    }

    /**
     * Retrieves data from the variable in Integer format.
     *
     * @param variableName The name of the variable to access.
     * @return The data stored in the variable. (-1 if null or cannot be parsed).
     */
    public int getIntegerVariableData(String variableName) {
        String data = getStringVariableData(variableName);
        if (data == null)
            return -1;

        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Stores data within the variable.
     *
     * @param variableName The name of the variable to store data into.
     * @param data         The data to store in the variable.
     */
    public void setVariableData(String variableName, Object data) {
        if (httpServletRequest != null) {
            httpServletRequest.getSession().setAttribute((tenantSlug != null ? tenantSlug + "_" : "") + variableName, data);
        } else if (externalContext != null) {
            externalContext.getSessionMap().put((tenantSlug != null ? tenantSlug + "_" : "") + variableName, data);
        }
    }

    /**
     * Clears any data within the variable.
     *
     * @param variableName The name of the variable to clear.
     */
    public void clearVariableData(String variableName) {
        setVariableData(variableName, null);
    }
}
