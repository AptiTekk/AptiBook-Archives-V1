/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;

@Local
public interface StartupService {

    void checkForRootGroup(Tenant tenant);

    void checkForAdminUser(Tenant tenant);

    void checkForAssetCategories(Tenant tenant);

    void writeDefaultProperties(Tenant tenant);

    void writeDefaultPermissions(Tenant tenant);

    /**
     * Temporary method to demonstrate the capability of tenant
     */
    void loadDefaultTenants();
}
