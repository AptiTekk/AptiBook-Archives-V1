package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entity.AppProperty;

import javax.ejb.Local;
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
