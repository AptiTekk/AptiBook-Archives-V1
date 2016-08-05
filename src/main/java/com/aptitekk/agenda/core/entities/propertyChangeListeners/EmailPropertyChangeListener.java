/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.propertyChangeListeners;

import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.entities.services.EmailService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class EmailPropertyChangeListener implements Property.Group.ChangeListener {
    @Inject
    private EmailService emailService;

    @Override
    public void onPropertiesChanged(Property.Group propertyGroup) {
        emailService.resetProperties();
    }
}
