package com.aptitekk.agenda.core.services;

import javax.ejb.Local;

@Local
public interface StartupService {

    void checkForRootGroup();

    void checkForAdminUser();

    void checkForAssetTypes();

    void writeDefaultProperties();
}
