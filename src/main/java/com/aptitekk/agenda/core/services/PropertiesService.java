package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.Property;
import com.aptitekk.agenda.core.properties.PropertyKey;

import javax.ejb.Local;

@Local
public interface PropertiesService extends EntityService<Property> {

    Property getPropertyByKey(PropertyKey property);

}
