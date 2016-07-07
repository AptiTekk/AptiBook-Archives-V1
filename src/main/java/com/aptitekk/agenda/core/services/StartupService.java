/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import javax.ejb.Local;

@Local
public interface StartupService {

    void checkForRootGroup();

    void checkForAdminUser();

    void checkForAssetTypes();

    void writeDefaultProperties();

    void writeDefaultPermissions();
}
