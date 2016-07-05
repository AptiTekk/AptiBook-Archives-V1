package com.aptitekk.agenda.core.properties;

/**
 * An interface used for listening for changes to Property Groups.
 * Please ensure that the listening bean is managed.
 */
public interface PropertyGroupChangeListener {

    void onPropertiesChanged(PropertyGroup propertyGroup);

}
