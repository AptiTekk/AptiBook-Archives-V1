package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.Properties;
import com.aptitekk.agenda.core.entity.AppProperty;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@ManagedBean(name = "PropertiesController")
@ViewScoped
public class PropertiesController {

    @Inject
    Properties propertiesService;
    
    private Collection<AppProperty> properties;
    
    @PostConstruct
    public void init() {
        resetProperties();
    }

    public Collection<AppProperty> getProperties() {
        return properties;
    }
    
    public void updateProperties() {
        for(AppProperty property : properties) {
            property = propertiesService.merge(property);
        }
        
        FacesContext.getCurrentInstance().addMessage("propertiesForm",
                    new FacesMessage("Application Properties Updated."));
    }
    
    public void resetProperties() {
        properties = propertiesService.getAll();
    }
    
    public String formatPropertyTitle(String original) {
        String[] split = original.split("\\.");
        StringBuilder result = new StringBuilder();
        for(int i = 1; i < split.length; i++) {
            split[i] = split[i].replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
            split[i] = split[i].substring(0, 1).toUpperCase() + split[i].substring(1) + " ";
            result.append(split[i]);
        }
        return result.toString();
    }
    
}
