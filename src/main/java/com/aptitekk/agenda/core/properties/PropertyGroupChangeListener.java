/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.properties;

/**
 * An interface used for listening for changes to Property Groups.
 * Please ensure that the listening bean is managed.
 */
public interface PropertyGroupChangeListener {

    void onPropertiesChanged(PropertyGroup propertyGroup);

}
