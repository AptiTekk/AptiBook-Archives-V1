/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.propertyGroupChangeListeners;

import com.aptitekk.aptibook.core.domain.entities.Property;
import com.aptitekk.aptibook.core.tenant.TenantManagementService;
import com.aptitekk.aptibook.core.tenant.TenantSessionService;

import javax.inject.Inject;

public class DateTimeChangeListener implements Property.Group.ChangeListener {

    @Inject
    private TenantManagementService tenantManagementService;

    @Override
    public void onPropertiesChanged(Property.Group propertyGroup) {
        tenantManagementService.refreshDateTimeZones();
    }
}
