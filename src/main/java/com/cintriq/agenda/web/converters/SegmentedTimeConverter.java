package com.cintriq.agenda.web.converters;

import com.cintriq.agenda.core.utilities.time.SegmentedTime;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
