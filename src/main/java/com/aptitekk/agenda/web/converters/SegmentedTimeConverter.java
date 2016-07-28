/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.converters;

import com.aptitekk.agenda.core.util.time.SegmentedTime;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@ManagedBean(name = "SegmentedTimeConverter")
@RequestScoped
public class SegmentedTimeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String timeString) {
        return SegmentedTime.fromTimeString(timeString);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o != null && o instanceof SegmentedTime) {
            return ((SegmentedTime) o).getTimeString();
        }

        return null;
    }
}
