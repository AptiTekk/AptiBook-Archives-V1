/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.Property;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.properties.PropertyKey;
import com.aptitekk.agenda.core.services.*;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.Sha256Helper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
@ApplicationScoped
public class StartupServiceImpl implements StartupService {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private UserService userService;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private PropertiesService propertiesService;

    @PostConstruct
    public void init() {
        checkForRootGroup();
        checkForAdminUser();
        checkForAssetTypes();
        writeDefaultProperties();
    }

    @Override
    public void checkForRootGroup() {
        if (userGroupService.findByName(UserGroupService.ROOT_GROUP_NAME) == null) {
            UserGroup rootGroup = new UserGroup(UserGroupService.ROOT_GROUP_NAME);
            try {
                userGroupService.insert(rootGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void checkForAdminUser() {
        User adminUser = userService.findByName(UserService.ADMIN_USERNAME);
        if (adminUser == null) {

            adminUser = new User();
            adminUser.setUsername(UserService.ADMIN_USERNAME);
            adminUser.setPassword(Sha256Helper.rawToSha(UserService.DEFAULT_ADMIN_PASSWORD));
            adminUser.setEnabled(true);
            try {
                userService.insert(adminUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ensureAdminHasRootGroup();
    }

    private void ensureAdminHasRootGroup() {
        User adminUser = userService.findByName(UserService.ADMIN_USERNAME);
        if (adminUser != null) {
            UserGroup rootGroup = userGroupService.getRootGroup();
            if (rootGroup != null) {
                if (!adminUser.getUserGroups().contains(rootGroup)) {
                    try {
                        adminUser.addGroup(rootGroup);
                        userService.merge(adminUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void checkForAssetTypes() {
        if (assetTypeService.getAll().isEmpty()) {
            try {
                AssetType assetType = new AssetType("Rooms");
                assetTypeService.insert(assetType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeDefaultProperties() {
        for(PropertyKey key : PropertyKey.values())
        {
            if(propertiesService.getPropertyByKey(key) == null)
            {
                Property property = new Property(key, key.getDefaultValue());
                try {
                    propertiesService.insert(property);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
