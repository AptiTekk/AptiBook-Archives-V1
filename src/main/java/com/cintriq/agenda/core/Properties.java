package com.cintriq.agenda.core;

import javax.ejb.Local;

import com.cintriq.agenda.core.entity.AppProperty;

import java.util.List;

@Local
public interface Properties {

    List<AppProperty> getAll();
    
    String get(String key);

    AppProperty getProperty(String key);

    void put(String key, String value);
    
    AppProperty merge(AppProperty property);
    
    void insert(AppProperty property);

}
